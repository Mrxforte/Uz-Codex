package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.ChatMessage
import com.example.data.remote.GeminiLegalAssistant
import com.example.data.remote.SmartRagQuestionGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class LegalRepository(context: Context) {

    private val appContext = context.applicationContext
    private val db = LegalDatabase.getDatabase(context)
    private val dao = db.legalDao()
    private val ragQuestionGenerator = SmartRagQuestionGenerator(appContext)

    init {
        LegislationData.loadAllArticles(appContext)
    }

    // --- Legislation ---
    fun getAllCodes(): List<LegalCode> = LegislationData.UZBEKISTAN_CODES

    fun getCodeById(id: String): LegalCode? = LegislationData.getCodeById(id)

    fun getArticlesForCode(codeId: String): List<LegalArticle> =
        LegislationData.getArticlesForCode(codeId, appContext)

    fun getArticleById(articleId: String): LegalArticle? =
        LegislationData.getAllArticles(appContext).find { it.id == articleId }
            ?: LegislationData.KEY_ARTICLES.find { it.id == articleId }

    fun searchArticles(query: String): List<LegalArticle> =
        LegislationData.searchArticles(query, appContext)

    // Bookmarks
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = dao.getAllBookmarks()

    fun isArticleBookmarked(articleId: String): Flow<Boolean> = dao.isArticleBookmarked(articleId)

    suspend fun toggleBookmark(article: LegalArticle, codeTitle: String) {
        val isBookmarked = dao.isArticleBookmarked(article.id).firstOrNull() ?: false
        if (isBookmarked) {
            dao.deleteBookmark(article.id)
        } else {
            dao.insertBookmark(
                BookmarkEntity(
                    articleId = article.id,
                    codeId = article.codeId,
                    codeTitle = codeTitle,
                    articleNumber = article.articleNumberStr,
                    articleTitle = article.title
                )
            )
        }
    }

    // Notes
    fun getNotesForArticle(articleId: String): Flow<List<UserNoteEntity>> =
        dao.getNotesForArticle(articleId)

    suspend fun addNote(articleId: String, articleTitle: String, content: String) {
        dao.insertNote(
            UserNoteEntity(
                articleId = articleId,
                articleTitle = articleTitle,
                content = content
            )
        )
    }

    suspend fun deleteNote(note: UserNoteEntity) = dao.deleteNote(note)

    // --- Case Analysis ---
    fun getEducationalScenarios(): List<LegalCaseScenario> =
        CaseData.EDUCATIONAL_SCENARIOS

    fun getScenarioById(id: String): LegalCaseScenario? =
        CaseData.getScenarioById(id)

    fun getAllSavedCases(): Flow<List<SavedCaseAnalysisEntity>> = dao.getAllSavedCases()

    suspend fun saveCaseAnalysis(
        id: String,
        title: String,
        category: String,
        summary: String,
        narrative: String,
        qualification: String
    ) {
        dao.insertSavedCase(
            SavedCaseAnalysisEntity(
                id = id,
                title = title,
                scenarioCategory = category,
                summary = summary,
                userNarrative = narrative,
                qualificationSummary = qualification
            )
        )
    }

    suspend fun deleteSavedCase(id: String) = dao.deleteSavedCase(id)

    // --- Academy & Gamification ---
    fun getAllCourses(): List<Course> = AcademyData.COURSES

    fun getQuizQuestions(): List<QuizQuestion> = AcademyData.QUIZ_QUESTIONS

    fun getAdaptiveQuizQuestions(selectedTopics: Set<String>, count: Int = 30): List<QuizQuestion> =
        AcademyData.generateAdaptiveQuiz(selectedTopics, count)

    fun getAvailableQuizTopics(): List<String> = AcademyData.AVAILABLE_TOPICS

    fun getAchievements(): List<UserAchievement> = AcademyData.ACHIEVEMENTS

    fun getUserProfile(): Flow<UserProfileEntity?> = dao.getUserProfile()

    fun getLessonProgress(): Flow<List<LessonProgressEntity>> = dao.getAllLessonProgress()

    suspend fun completeLesson(lessonId: String, courseId: String, xp: Int) {
        dao.markLessonCompleted(
            LessonProgressEntity(
                lessonId = lessonId,
                courseId = courseId
            )
        )
        addXp(xp)
    }

    suspend fun addXp(amount: Int) {
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile == null) {
            dao.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    xp = 100 + amount,
                    streakDays = 3
                )
            )
        } else {
            dao.addXp(amount)
        }
    }

    // --- Documents ---
    fun getAllDocumentTemplates(): List<DocumentTemplate> = DocumentData.TEMPLATES

    fun getTemplateById(id: String): DocumentTemplate? = DocumentData.getTemplateById(id)

    fun generateDocumentText(template: DocumentTemplate, values: Map<String, String>): String =
        DocumentData.generateFormattedDocument(template, values)

    // --- Updates ---
    fun getRecentUpdates(): List<LegislationUpdate> = UpdatesData.RECENT_UPDATES

    // --- Gemini AI Assistant ---
    suspend fun sendAIAssistantMessage(query: String, history: List<ChatMessage>): ChatMessage {
        return GeminiLegalAssistant.askLegalQuestion(appContext, query, history)
    }

    // --- SMART RAG Test Generator Operations ---

    suspend fun generateRagTestQuestions(
        selection: LegalSourceSelection,
        config: TestGenerationConfig,
        onProgress: (currentCount: Int, targetEstimate: Int, status: String) -> Unit = { _, _, _ -> }
    ): List<GeneratedTestQuestion> {
        val prevFingerprints = if (config.excludePreviouslyGenerated) {
            dao.getAllGeneratedFingerprints().toSet()
        } else emptySet()

        val generated = ragQuestionGenerator.generateQuestions(
            selection = selection,
            config = config,
            previouslyGeneratedFingerprints = prevFingerprints,
            onProgress = onProgress
        )

        // Save fingerprints to database to exclude in future sessions
        if (generated.isNotEmpty()) {
            val fpEntities = generated.map { GeneratedFingerprintEntity(it.fingerprint) }
            dao.insertFingerprints(fpEntities)
        }

        return generated
    }

    suspend fun saveGeneratedTestSet(
        title: String,
        selectionSummary: String,
        config: TestGenerationConfig,
        questions: List<GeneratedTestQuestion>
    ): String {
        val testSetId = "test_set_${System.currentTimeMillis()}"

        val setEntity = GeneratedTestSetEntity(
            id = testSetId,
            title = title,
            questionCount = questions.size,
            selectionSummary = selectionSummary,
            configJson = JSONObject().apply {
                put("isMaxAvailableMode", config.isMaxAvailableMode)
                put("difficulty", config.difficulty.name)
            }.toString()
        )

        val questionEntities = questions.map { q ->
            GeneratedQuestionEntity(
                id = q.id,
                testSetId = testSetId,
                questionText = q.questionText,
                questionType = q.questionType.name,
                optionsJson = JSONArray(q.options).toString(),
                correctIndicesJson = JSONArray(q.correctIndices).toString(),
                matchPairsJson = q.matchPairs?.let { pairs ->
                    JSONArray(pairs.map { JSONObject().put("left", it.first).put("right", it.second) }).toString()
                },
                fillInBlankAnswer = q.fillInBlankAnswer,
                explanation = q.explanation,
                codeId = q.codeId,
                codeName = q.codeName,
                chapter = q.chapter,
                articleNumber = q.articleNumber,
                articleNumberStr = q.articleNumberStr,
                articleTitle = q.articleTitle,
                sourceExcerpt = q.supportingSourceExcerpt,
                difficulty = q.difficulty.name,
                fingerprint = q.fingerprint
            )
        }

        dao.insertGeneratedTestSet(setEntity)
        dao.insertGeneratedQuestions(questionEntities)
        return testSetId
    }

    fun getAllSavedTestSets(): Flow<List<GeneratedTestSetEntity>> = dao.getAllGeneratedTestSets()

    suspend fun getQuestionsForTestSet(testSetId: String): List<GeneratedTestQuestion> {
        val entities = dao.getQuestionsForTestSet(testSetId)
        return entities.map { e ->
            val optionsList = mutableListOf<String>()
            val optArr = JSONArray(e.optionsJson)
            for (i in 0 until optArr.length()) {
                optionsList.add(optArr.getString(i))
            }

            val correctIndicesList = mutableListOf<Int>()
            val corrArr = JSONArray(e.correctIndicesJson)
            for (i in 0 until corrArr.length()) {
                correctIndicesList.add(corrArr.getInt(i))
            }

            val matchPairs = e.matchPairsJson?.let { json ->
                val arr = JSONArray(json)
                val pairs = mutableListOf<Pair<String, String>>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    pairs.add(obj.getString("left") to obj.getString("right"))
                }
                pairs
            }

            GeneratedTestQuestion(
                id = e.id,
                questionText = e.questionText,
                questionType = try { QuestionType.valueOf(e.questionType) } catch (_: Exception) { QuestionType.SINGLE_CHOICE },
                options = optionsList,
                correctIndices = correctIndicesList,
                matchPairs = matchPairs,
                fillInBlankAnswer = e.fillInBlankAnswer,
                explanation = e.explanation,
                codeId = e.codeId,
                codeName = e.codeName,
                chapter = e.chapter,
                articleNumber = e.articleNumber,
                articleNumberStr = e.articleNumberStr,
                articleTitle = e.articleTitle,
                supportingSourceExcerpt = e.sourceExcerpt,
                difficulty = try { TestDifficulty.valueOf(e.difficulty) } catch (_: Exception) { TestDifficulty.MIXED },
                fingerprint = e.fingerprint
            )
        }
    }

    suspend fun deleteGeneratedTestSet(id: String) {
        dao.deleteQuestionsForTestSet(id)
        dao.deleteGeneratedTestSet(id)
    }

    suspend fun saveTestAttempt(
        testSetId: String,
        testTitle: String,
        scorePercentage: Int,
        correctCount: Int,
        totalCount: Int,
        userAnswersMap: Map<Int, List<Int>>,
        durationSeconds: Int,
        earnedXp: Int
    ) {
        val attemptEntity = TestAttemptHistoryEntity(
            id = "attempt_${System.currentTimeMillis()}",
            testSetId = testSetId,
            testTitle = testTitle,
            scorePercentage = scorePercentage,
            correctCount = correctCount,
            totalCount = totalCount,
            userAnswersJson = JSONObject(userAnswersMap.mapKeys { it.key.toString() }).toString(),
            durationSeconds = durationSeconds,
            earnedXp = earnedXp
        )
        dao.insertTestAttempt(attemptEntity)
        addXp(earnedXp)
    }

    fun getAllTestAttempts(): Flow<List<TestAttemptHistoryEntity>> = dao.getAllTestAttempts()
}
