package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.LegislationData
import com.example.data.model.LegalArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StructuredGeminiRagService(context: Context) {

    private val retrievalEngine = HybridLegalRetrievalEngine(context)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private fun getFullCodeTitle(codeId: String): String {
        return when (codeId.lowercase()) {
            "fk" -> "Fuqarolik kodeksi"
            "jk" -> "Jinoyat kodeksi"
            "mk" -> "Mehnat kodeksi"
            "jpk" -> "Jinoyat-protsessual kodeksi"
            "fpk" -> "Fuqarolik protsessual kodeksi"
            "ok" -> "Oila kodeksi"
            "sk" -> "Soliq kodeksi"
            "yk", "ujk" -> "Uy-joy kodeksi"
            "mjoek", "mjtk" -> "Ma'muriy javobgarlik to'g'risidagi kodeks"
            "jik" -> "Jinoyat-ijroiya kodeksi"
            "bk" -> "Bojxona kodeksi"
            else -> "O'zbekiston Respublikasi Kodeksi"
        }
    }

    suspend fun processRagQuery(
        userQuery: String,
        conversationHistory: List<ChatMessage>
    ): ChatMessage = withContext(Dispatchers.IO) {
        val analysis = QueryUnderstandingEngine.analyzeQuery(userQuery, conversationHistory)

        // Step 1: Hybrid Retrieval
        val scoredArticles = if (analysis.schema.needsLegalRetrieval && !analysis.isUnmatchedDomain) {
            retrievalEngine.retrieveRelevantArticles(
                query = userQuery,
                searchQueries = analysis.searchQueries
            )
        } else emptyList()

        // Step 2: Strict Local Evidence Validation
        val validatedEvidence = LegalEvidenceValidator.validateEvidence(
            query = userQuery,
            scoredArticles = scoredArticles,
            analysis = analysis
        )

        // Non-legal or conversational modes return immediate grounded response
        if (!analysis.schema.needsLegalRetrieval || analysis.isGreeting || analysis.isEmotionalSharing || analysis.requiresClarification) {
            val fallbackText = GeminiLegalAssistant.generateLocalGroundedAnswer(
                userQuery,
                scoredArticles.map { it.article },
                null,
                analysis
            )
            return@withContext ChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = "assistant",
                text = fallbackText,
                citedArticles = emptyList(),
                isGrounded = false
            )
        }

        // Step 3: Call Gemini with Structured RAG Prompt
        val apiKey = GeminiConfig.getApiKey()
        if (apiKey.isNotBlank() && validatedEvidence.isGroundedInEvidence) {
            for (model in GeminiConfig.FALLBACK_GENERATION_MODELS) {
                try {
                    val structuredMsg = callStructuredGeminiApi(
                        userQuery = userQuery,
                        history = conversationHistory,
                        retrievedArticles = validatedEvidence.verifiedCitations.map { it.article },
                        apiKey = apiKey,
                        modelName = model,
                        analysis = analysis
                    )
                    if (structuredMsg != null && structuredMsg.text.isNotBlank()) {
                        return@withContext structuredMsg
                    }
                } catch (e: Exception) {
                    Log.e("StructuredGeminiRag", "API error with model $model: ${e.message}")
                }
            }
        }

        // Step 4: Fallback to Verified Local Grounded Synthesis
        val formattedResponse = buildString {
            appendLine("### Vaziyatingiz")
            appendLine(validatedEvidence.summaryText)
            appendLine()

            appendLine("### Huquqiy tahlil")
            appendLine(validatedEvidence.legalAnalysisText)
            appendLine()

            appendLine("### Tegishli qonunlar")
            if (validatedEvidence.verifiedCitations.isNotEmpty()) {
                validatedEvidence.verifiedCitations.forEachIndexed { idx, cit ->
                    val art = cit.article
                    val fullCodeTitle = getFullCodeTitle(art.codeId)
                    appendLine("${idx + 1}. **$fullCodeTitle, ${art.articleNumberStr} (${art.title})**")
                    appendLine("   \"${art.content.take(220)}...\"")
                }
            } else {
                appendLine("Aynan ushbu masala bo'yicha tasdiqlangan va ishonchli modda topilmadi.")
            }
            appendLine()

            appendLine("### Nima qilish kerak?")
            validatedEvidence.nextSteps.forEachIndexed { idx, step ->
                appendLine("${idx + 1}. $step")
            }
            appendLine()

            appendLine("### Muhim eslatma")
            appendLine(validatedEvidence.limitations)
        }

        ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "assistant",
            text = formattedResponse,
            citedArticles = validatedEvidence.verifiedCitations.map { it.article },
            isGrounded = validatedEvidence.isGroundedInEvidence
        )
    }

    private fun callStructuredGeminiApi(
        userQuery: String,
        history: List<ChatMessage>,
        retrievedArticles: List<LegalArticle>,
        apiKey: String,
        modelName: String,
        analysis: UserQueryAnalysis
    ): ChatMessage? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val systemInstruction = """
            Siz O'zbekiston Respublikasi qonunchiligi bo'yicha professional, manbaga tayanadigan UzLegal AI yuridik yordamchisiz.
            
            QOIDALAR:
            1. Faqat taqdim etilayotgan rasmiy modda matniga tayaning. To'qima modda yoki dalil keltirish qat'iyan man etiladi.
            2. Javobni JSON formatida qaytaring:
            {
              "summary": "Short direct explanation of user situation",
              "legalAnalysis": "Detailed grounded legal analysis",
              "citedArticleIds": ["id1", "id2"],
              "nextSteps": ["Step 1", "Step 2"],
              "limitations": "Disclaimer"
            }
        """.trimIndent()

        val articlesText = buildString {
            retrievedArticles.forEach { art ->
                val fullTitle = getFullCodeTitle(art.codeId)
                appendLine("ARTICLE_ID: ${art.id}")
                appendLine("DOCUMENT: $fullTitle")
                appendLine("ARTICLE: ${art.articleNumberStr} — ${art.title}")
                appendLine("CONTENT: ${art.content}")
                appendLine("---")
            }
        }

        val requestJson = JSONObject()
        val contentsArray = JSONArray()

        history.takeLast(4).forEach { msg ->
            val role = if (msg.sender == "user") "user" else "model"
            val contentObj = JSONObject()
            contentObj.put("role", role)
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", msg.text))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
        }

        val currentContent = JSONObject()
        currentContent.put("role", "user")
        val currentParts = JSONArray()
        currentParts.put(JSONObject().put("text", "Rasmiy Hujjatlar Manbasi:\n$articlesText\n\nFoydalanuvchi So'rovi: $userQuery"))
        currentContent.put("parts", currentParts)
        contentsArray.put(currentContent)

        requestJson.put("contents", contentsArray)

        val sysObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", systemInstruction))
        sysObj.put("parts", sysParts)
        requestJson.put("systemInstruction", sysObj)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.2)
        genConfig.put("responseMimeType", "application/json")
        requestJson.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful || responseBody.isBlank()) return null

        val rootObj = JSONObject(responseBody)
        val candidates = rootObj.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val candText = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")?.getJSONObject(0)?.optString("text") ?: ""

        if (candText.isBlank()) return null

        val parsedJson = JSONObject(candText)
        val summary = parsedJson.optString("summary", "Vaziyatingiz bo'yicha huquqiy tahlil tayyorlandi.")
        val legalAnalysis = parsedJson.optString("legalAnalysis", "")

        val citedIdsArr = parsedJson.optJSONArray("citedArticleIds")
        val verifiedCitedArticles = mutableListOf<LegalArticle>()

        if (citedIdsArr != null) {
            for (i in 0 until citedIdsArr.length()) {
                val cId = citedIdsArr.getString(i)
                val matched = retrievedArticles.find { it.id == cId } ?: LegislationData.getAllArticles().find { it.id == cId }
                if (matched != null && !verifiedCitedArticles.contains(matched)) {
                    verifiedCitedArticles.add(matched)
                }
            }
        }

        if (verifiedCitedArticles.isEmpty()) {
            verifiedCitedArticles.addAll(retrievedArticles)
        }

        val nextStepsArr = parsedJson.optJSONArray("nextSteps")
        val nextStepsList = mutableListOf<String>()
        if (nextStepsArr != null) {
            for (i in 0 until nextStepsArr.length()) {
                nextStepsList.add(nextStepsArr.getString(i))
            }
        }

        val formattedText = buildString {
            appendLine("### Vaziyatingiz")
            appendLine(summary)
            appendLine()

            appendLine("### Huquqiy tahlil")
            appendLine(legalAnalysis)
            appendLine()

            appendLine("### Tegishli qonunlar")
            verifiedCitedArticles.forEachIndexed { idx, art ->
                val fullCodeTitle = getFullCodeTitle(art.codeId)
                appendLine("${idx + 1}. **$fullCodeTitle, ${art.articleNumberStr} (${art.title})**")
                appendLine("   \"${art.content.take(220)}...\"")
            }
            appendLine()

            if (nextStepsList.isNotEmpty()) {
                appendLine("### Nima qilish kerak?")
                nextStepsList.forEachIndexed { idx, step ->
                    appendLine("${idx + 1}. $step")
                }
                appendLine()
            }

            appendLine("### Muhim eslatma")
            appendLine(parsedJson.optString("limitations", "Sodir bo'lgan holat yuzasidan rasmiy hujjatlar va advokat bilan maslahatlashish tavsiya etiladi."))
        }

        return ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "assistant",
            text = formattedText,
            citedArticles = verifiedCitedArticles,
            isGrounded = true
        )
    }
}
