package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.LegislationData
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class SmartRagQuestionGenerator(context: Context) {

    private val retrievalEngine = LegalRagRetrievalEngine(context)

    suspend fun generateQuestions(
        selection: LegalSourceSelection,
        config: TestGenerationConfig,
        previouslyGeneratedFingerprints: Set<String> = emptySet(),
        onProgress: (currentCount: Int, targetEstimate: Int, status: String) -> Unit = { _, _, _ -> }
    ): List<GeneratedTestQuestion> = withContext(Dispatchers.IO) {

        val targetArticles = retrievalEngine.resolveSelectedArticles(selection)
        if (targetArticles.isEmpty()) {
            onProgress(0, 0, "Tanlangan manbada moddalar topilmadi")
            return@withContext emptyList()
        }

        val allowedTypes = if (config.selectedTypes.isEmpty() || config.selectedTypes.contains(QuestionType.MIXED)) {
            QuestionType.entries.filter { it != QuestionType.MIXED }.toSet()
        } else {
            config.selectedTypes
        }

        val targetCount = if (config.isMaxAvailableMode) 500 else config.questionCount
        onProgress(0, targetCount, "Manbalar tahlil qilinmoqda (${targetArticles.size} ta modda)...")

        val validQuestions = mutableListOf<GeneratedTestQuestion>()
        val seenFingerprints = previouslyGeneratedFingerprints.toMutableSet()

        // 1. First Pass: Deterministic Local Source Concept Extraction
        var currentArticleIndex = 0
        for (article in targetArticles) {
            if (validQuestions.size >= targetCount) break

            currentArticleIndex++
            if (currentArticleIndex % 3 == 0 || currentArticleIndex == targetArticles.size) {
                onProgress(
                    validQuestions.size,
                    targetCount,
                    "Lokal manbalardan savollar tuzilmoqda (${currentArticleIndex}/${targetArticles.size} modda)..."
                )
            }

            val candidateQuestions = generateCandidatesForArticle(article, allowedTypes, config.difficulty)

            for (candidate in candidateQuestions) {
                if (validQuestions.size >= targetCount) break

                val validation = QuestionValidationPipeline.validateQuestion(candidate, seenFingerprints)
                if (validation.isValid) {
                    validQuestions.add(candidate)
                    seenFingerprints.add(candidate.fingerprint)
                }
            }
        }

        // 2. Second Pass: AI-Enhanced Generation if Gemini Key is Available and More Questions Needed
        val apiKey = GeminiConfig.getApiKey()
        if (apiKey.isNotBlank() && validQuestions.size < targetCount) {
            val remainingNeeded = targetCount - validQuestions.size
            onProgress(validQuestions.size, targetCount, "AI orqali chuqurroq kazus va savollar generatsiya qilinmoqda...")

            val articleBatches = targetArticles.chunked(3)
            for (batch in articleBatches) {
                if (validQuestions.size >= targetCount) break

                try {
                    val aiQuestions = generateAiQuestionsForBatch(batch, config.difficulty, remainingNeeded, apiKey)
                    for (q in aiQuestions) {
                        if (validQuestions.size >= targetCount) break

                        val valResult = QuestionValidationPipeline.validateQuestion(q, seenFingerprints)
                        if (valResult.isValid) {
                            validQuestions.add(q)
                            seenFingerprints.add(q.fingerprint)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SmartRagGenerator", "Gemini API generation error: ${e.message}")
                }
            }
        }

        // Shuffle if requested
        val finalResult = if (config.shuffleQuestionsAndOptions) {
            validQuestions.shuffled().map { q ->
                if (q.options.size >= 2) {
                    val indexedOptions = q.options.mapIndexed { idx, text -> idx to text }.shuffled()
                    val newOptions = indexedOptions.map { it.second }
                    val newCorrectIndices = q.correctIndices.mapNotNull { oldIdx ->
                        val newIdx = indexedOptions.indexOfFirst { it.first == oldIdx }
                        if (newIdx != -1) newIdx else null
                    }
                    q.copy(options = newOptions, correctIndices = newCorrectIndices)
                } else q
            }
        } else {
            validQuestions
        }

        onProgress(finalResult.size, targetCount, "Generatsiya yakunlandi (${finalResult.size} ta savol tayyor)")
        return@withContext finalResult
    }

    /**
     * Extracts source-grounded candidates from a legal article locally.
     */
    private fun generateCandidatesForArticle(
        article: LegalArticle,
        allowedTypes: Set<QuestionType>,
        difficulty: TestDifficulty
    ): List<GeneratedTestQuestion> {
        val candidates = mutableListOf<GeneratedTestQuestion>()
        val code = LegislationData.getCodeById(article.codeId)
        val codeName = code?.officialUzbekName ?: "O'zbekiston Respublikasi Qonuni"

        val content = article.content.trim()
        val sentences = content.split(Regex("(?<=[.!?])\\s+")).filter { it.length >= 15 }

        // Candidate 1: Single Choice / Direct Understanding
        if (allowedTypes.contains(QuestionType.SINGLE_CHOICE) || allowedTypes.contains(QuestionType.MIXED)) {
            val qText = "O'zbekiston Respublikasining ${code?.shortName ?: ""} (${article.articleNumberStr}) ga binoan, '${article.title}' normasining mazmuni qanday?"
            val optionA = content.take(180) + (if (content.length > 180) "..." else "")
            val optionB = "Ushbu holatda taraflar kelishuvi asosida majburiyat rasmiylashtirilmaydi hamda sudga murojaat qilish taqiqlanadi."
            val optionC = "Qonunda ushbu munosabatlar uchun javobgarlik belgilanmagan va u ixtiyoriy tartibda amalga oshiriladi."
            val optionD = "Ushbu norma faqat tuman hokimligining maxsus qarori qabul qilinganidan keyin kuchga kiradi."

            val fp = QuestionValidationPipeline.computeFingerprint(article.id, QuestionType.SINGLE_CHOICE, qText)
            candidates.add(
                GeneratedTestQuestion(
                    id = "rag_sc_${article.id}_1",
                    questionText = qText,
                    questionType = QuestionType.SINGLE_CHOICE,
                    options = listOf(optionA, optionB, optionC, optionD),
                    correctIndices = listOf(0),
                    explanation = "${article.articleNumberStr} (${article.title}): ${content.take(250)}",
                    codeId = article.codeId,
                    codeName = codeName,
                    chapter = article.chapter,
                    articleNumber = article.articleNumber,
                    articleNumberStr = article.articleNumberStr,
                    articleTitle = article.title,
                    supportingSourceExcerpt = article.content.take(300),
                    difficulty = difficulty,
                    fingerprint = fp
                )
            )
        }

        // Candidate 2: True/False
        if (allowedTypes.contains(QuestionType.TRUE_FALSE)) {
            val qText = "${article.articleNumberStr} ga ko'ra: '${article.title}' bo'yicha quyidagi tasdiq to'g'rimi?\n\n\"${sentences.firstOrNull() ?: content.take(120)}\""
            val fp = QuestionValidationPipeline.computeFingerprint(article.id, QuestionType.TRUE_FALSE, qText)
            candidates.add(
                GeneratedTestQuestion(
                    id = "rag_tf_${article.id}_1",
                    questionText = qText,
                    questionType = QuestionType.TRUE_FALSE,
                    options = listOf("To'g'ri (Normaga mos)", "Noto'g'ri (Normaga zid)"),
                    correctIndices = listOf(0),
                    explanation = "${article.articleNumberStr}da belgilanganidek: ${content.take(200)}",
                    codeId = article.codeId,
                    codeName = codeName,
                    chapter = article.chapter,
                    articleNumber = article.articleNumber,
                    articleNumberStr = article.articleNumberStr,
                    articleTitle = article.title,
                    supportingSourceExcerpt = article.content.take(300),
                    difficulty = difficulty,
                    fingerprint = fp
                )
            )
        }

        // Candidate 3: Scenario-based Question
        if (allowedTypes.contains(QuestionType.SCENARIO_BASED)) {
            val qText = "KAZUS VA AMALIY VAZIYAT:\nFuqaro ushbu sohada nizoli holatga duch keldi. ${code?.shortName ?: ""}ning ${article.articleNumberStr} (${article.title}) doirasida ushbu vaziyat qanday hal qilinishi lozim?"
            val opt1 = "Qonunchilik normasiga binoan: ${content.take(160)}..."
            val opt2 = "Nizo faqat og'zaki tushuntirish berish bilan yopiladi va hech qanday huquqiy oqibat keltirib chiqarmaydi."
            val opt3 = "Nizo ma'muriy tartibda 1 yildan keyin va faqat prokuratura ko'rsatmasi bilan ko'riladi."
            val opt4 = "Taraflar bir-biriga nisbatan davo taqdim eta olmaydi va barcha xarajatlar davlat byudjetiga yuklatiladi."

            val fp = QuestionValidationPipeline.computeFingerprint(article.id, QuestionType.SCENARIO_BASED, qText)
            candidates.add(
                GeneratedTestQuestion(
                    id = "rag_scen_${article.id}_1",
                    questionText = qText,
                    questionType = QuestionType.SCENARIO_BASED,
                    options = listOf(opt1, opt2, opt3, opt4),
                    correctIndices = listOf(0),
                    explanation = "Ushbu amaliy vaziyat O'zbekiston Respublikasi ${article.articleNumberStr} bilan tartibga solinadi. Qonun matnida: ${content.take(250)}",
                    codeId = article.codeId,
                    codeName = codeName,
                    chapter = article.chapter,
                    articleNumber = article.articleNumber,
                    articleNumberStr = article.articleNumberStr,
                    articleTitle = article.title,
                    supportingSourceExcerpt = article.content.take(300),
                    difficulty = difficulty,
                    fingerprint = fp
                )
            )
        }

        // Candidate 4: Fill In Blank
        if (allowedTypes.contains(QuestionType.FILL_IN_BLANK) && sentences.isNotEmpty()) {
            val firstSentence = sentences.first()
            val words = firstSentence.split(" ").filter { it.length >= 5 }
            val keyWord = words.getOrNull(words.size / 2) ?: "shart"
            val blankText = firstSentence.replaceFirst(keyWord, "______")

            val qText = "Quyidagi ${article.articleNumberStr} normasida bo'sh qolgan o'rinni (______) to'ldiring:\n\n\"$blankText\""
            val fp = QuestionValidationPipeline.computeFingerprint(article.id, QuestionType.FILL_IN_BLANK, qText)
            candidates.add(
                GeneratedTestQuestion(
                    id = "rag_fill_${article.id}_1",
                    questionText = qText,
                    questionType = QuestionType.FILL_IN_BLANK,
                    options = emptyList(),
                    correctIndices = emptyList(),
                    fillInBlankAnswer = keyWord,
                    explanation = "To'liq norma: \"$firstSentence\" (${article.articleNumberStr})",
                    codeId = article.codeId,
                    codeName = codeName,
                    chapter = article.chapter,
                    articleNumber = article.articleNumber,
                    articleNumberStr = article.articleNumberStr,
                    articleTitle = article.title,
                    supportingSourceExcerpt = article.content.take(300),
                    difficulty = difficulty,
                    fingerprint = fp
                )
            )
        }

        // Candidate 5: Concept Matching
        if (allowedTypes.contains(QuestionType.MATCH_CONCEPTS)) {
            val qText = "${article.articleNumberStr} (${article.title}) bo'yicha huquqiy tushunchalarni ularning qonuniy ta'rifiga moslashtiring:"
            val matchPairs = listOf(
                article.title to content.take(100),
                "Qonuniy asos" to "${article.articleNumberStr} (${article.effectiveDate} yildan amalda)",
                "Qo'llanish sohasi" to article.chapter
            )

            val fp = QuestionValidationPipeline.computeFingerprint(article.id, QuestionType.MATCH_CONCEPTS, qText)
            candidates.add(
                GeneratedTestQuestion(
                    id = "rag_match_${article.id}_1",
                    questionText = qText,
                    questionType = QuestionType.MATCH_CONCEPTS,
                    options = emptyList(),
                    correctIndices = emptyList(),
                    matchPairs = matchPairs,
                    explanation = "${article.articleNumberStr} bo'yicha rasmiy manba: ${content.take(200)}",
                    codeId = article.codeId,
                    codeName = codeName,
                    chapter = article.chapter,
                    articleNumber = article.articleNumber,
                    articleNumberStr = article.articleNumberStr,
                    articleTitle = article.title,
                    supportingSourceExcerpt = article.content.take(300),
                    difficulty = difficulty,
                    fingerprint = fp
                )
            )
        }

        return candidates
    }

    /**
     * AI-based multi-pass generation via Gemini REST API.
     */
    private suspend fun generateAiQuestionsForBatch(
        batch: List<LegalArticle>,
        difficulty: TestDifficulty,
        limit: Int,
        apiKey: String
    ): List<GeneratedTestQuestion> = withContext(Dispatchers.IO) {
        val result = mutableListOf<GeneratedTestQuestion>()
        val systemPrompt = """
            Siz O'zbekiston Respublikasi qonunchiligi bo'yicha RAG test savollari generatorisiz.
            Sizga berilayotgan rasmiy kodeks moddalari asosida 100% Manbaga asoslangan (grounded) professional test savollarini JSON formatida tayyorlang.

            QOIDALAR:
            1. Faqat taqdim etilgan modda matnidan foydalaning. Moddada yo'q fakt, muddat yoki jarimani o'zingizdan to'qimang.
            2. Har bir savol uchun JSON ob'ekt qaytaring.
            
            JSON FORMATI:
            [
              {
                "articleId": "modda_id",
                "questionText": "Savol matni",
                "type": "SINGLE_CHOICE | MULTIPLE_CHOICE | SCENARIO_BASED",
                "options": ["A variant", "B variant", "C variant", "D variant"],
                "correctIndices": [0],
                "explanation": "To'liq huquqiy tushuntirish va modda izohi"
              }
            ]
        """.trimIndent()

        val articlesText = buildString {
            batch.forEach { art ->
                val code = LegislationData.getCodeById(art.codeId)
                appendLine("MODDA ID: ${art.id}")
                appendLine("HUJJAT: ${code?.officialUzbekName ?: ""}")
                appendLine("MODDA: ${art.articleNumberStr} — ${art.title}")
                appendLine("MATN: ${art.content}")
                appendLine("---")
            }
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val requestJson = JSONObject()
        val contentsArray = JSONArray()
        val userContent = JSONObject()
        userContent.put("role", "user")
        val partsArray = JSONArray()
        partsArray.put(JSONObject().put("text", "Manba moddalar:\n$articlesText\n\nYuqoridagi moddalar bo'yicha $limit ta test savolini yaratib berishingizni so'rayman."))
        userContent.put("parts", partsArray)
        contentsArray.put(userContent)
        requestJson.put("contents", contentsArray)

        val sysObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", systemPrompt))
        sysObj.put("parts", sysParts)
        requestJson.put("systemInstruction", sysObj)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.2)
        genConfig.put("responseMimeType", "application/json")
        requestJson.put("generationConfig", genConfig)

        val okHttpClient = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val bodyStr = response.body?.string() ?: ""

        if (response.isSuccessful && bodyStr.isNotBlank()) {
            val rootObj = JSONObject(bodyStr)
            val candidates = rootObj.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candText = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")?.getJSONObject(0)?.optString("text") ?: ""
                if (candText.isNotBlank()) {
                    val jsonArray = JSONArray(candText)
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val artId = item.optString("articleId")
                        val article = batch.find { it.id == artId } ?: batch.firstOrNull() ?: continue
                        val code = LegislationData.getCodeById(article.codeId)

                        val qText = item.getString("questionText")
                        val typeStr = item.optString("type", "SINGLE_CHOICE")
                        val qType = try { QuestionType.valueOf(typeStr) } catch (_: Exception) { QuestionType.SINGLE_CHOICE }

                        val optsArray = item.getJSONArray("options")
                        val options = mutableListOf<String>()
                        for (j in 0 until optsArray.length()) {
                            options.add(optsArray.getString(j))
                        }

                        val correctArr = item.getJSONArray("correctIndices")
                        val correctIndices = mutableListOf<Int>()
                        for (j in 0 until correctArr.length()) {
                            correctIndices.add(correctArr.getInt(j))
                        }

                        val exp = item.optString("explanation", "${article.articleNumberStr} modda izohi.")
                        val fp = QuestionValidationPipeline.computeFingerprint(article.id, qType, qText)

                        result.add(
                            GeneratedTestQuestion(
                                id = "rag_ai_${article.id}_$i",
                                questionText = qText,
                                questionType = qType,
                                options = options,
                                correctIndices = correctIndices,
                                explanation = exp,
                                codeId = article.codeId,
                                codeName = code?.officialUzbekName ?: "O'zbekiston Qonuni",
                                chapter = article.chapter,
                                articleNumber = article.articleNumber,
                                articleNumberStr = article.articleNumberStr,
                                articleTitle = article.title,
                                supportingSourceExcerpt = article.content.take(300),
                                difficulty = difficulty,
                                fingerprint = fp
                            )
                        )
                    }
                }
            }
        }
        return@withContext result
    }
}
