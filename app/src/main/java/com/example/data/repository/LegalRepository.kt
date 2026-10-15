package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.ChatMessage
import com.example.data.remote.GeminiLegalAssistant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LegalRepository(context: Context) {

    private val db = LegalDatabase.getDatabase(context)
    private val dao = db.legalDao()

    // --- Legislation ---
    fun getAllCodes(): List<LegalCode> = LegislationData.UZBEKISTAN_CODES

    fun getCodeById(id: String): LegalCode? = LegislationData.getCodeById(id)

    fun getArticlesForCode(codeId: String): List<LegalArticle> =
        LegislationData.getArticlesForCode(codeId)

    fun getArticleById(articleId: String): LegalArticle? =
        LegislationData.KEY_ARTICLES.find { it.id == articleId }

    fun searchArticles(query: String): List<LegalArticle> =
        LegislationData.searchArticles(query)

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
        return GeminiLegalAssistant.askLegalQuestion(query, history)
    }
}
