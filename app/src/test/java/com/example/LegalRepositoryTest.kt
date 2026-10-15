package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.LegalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LegalRepositoryTest {

    private lateinit var repository: LegalRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = LegalRepository(context)
    }

    @Test
    fun testLegislationMethods() {
        val codes = repository.getAllCodes()
        assertEquals(19, codes.size)

        val code = repository.getCodeById("jk")
        assertNotNull(code)

        val articles = repository.getArticlesForCode("jk")
        assertTrue(articles.isNotEmpty())

        val article = repository.getArticleById("jk_97")
        assertNotNull(article)

        val searchResults = repository.searchArticles("odam o'ldirish")
        assertTrue(searchResults.isNotEmpty())
    }

    @Test
    fun testBookmarksFlow() = runBlocking {
        val article = repository.getArticleById("jk_97")!!
        assertFalse(repository.isArticleBookmarked(article.id).first())

        repository.toggleBookmark(article, "Jinoyat kodeksi")
        assertTrue(repository.isArticleBookmarked(article.id).first())

        val bookmarks = repository.getAllBookmarks().first()
        assertTrue(bookmarks.any { it.articleId == article.id })

        repository.toggleBookmark(article, "Jinoyat kodeksi")
        assertFalse(repository.isArticleBookmarked(article.id).first())
    }

    @Test
    fun testNotesFlow() = runBlocking {
        val articleId = "jk_97"
        repository.addNote(articleId, "97-modda", "Eslatma matni")

        val notes = repository.getNotesForArticle(articleId).first()
        assertTrue(notes.isNotEmpty())
        assertEquals("Eslatma matni", notes[0].content)

        repository.deleteNote(notes[0])
        val emptyNotes = repository.getNotesForArticle(articleId).first()
        assertTrue(emptyNotes.isEmpty())
    }

    @Test
    fun testCaseAnalysisFlow() = runBlocking {
        val scenarios = repository.getEducationalScenarios()
        assertTrue(scenarios.isNotEmpty())

        val scenario = repository.getScenarioById(scenarios[0].id)
        assertNotNull(scenario)

        repository.saveCaseAnalysis("case_1", "Sarlavha", "Kategoriya", "Xulosa", "Hikoya", "Kvalifikatsiya")
        val savedCases = repository.getAllSavedCases().first()
        assertTrue(savedCases.any { it.id == "case_1" })

        repository.deleteSavedCase("case_1")
        val afterDelete = repository.getAllSavedCases().first()
        assertFalse(afterDelete.any { it.id == "case_1" })
    }

    @Test
    fun testAcademyAndGamification() = runBlocking {
        val courses = repository.getAllCourses()
        assertTrue(courses.isNotEmpty())

        val quiz = repository.getQuizQuestions()
        assertTrue(quiz.isNotEmpty())

        val adaptiveQuiz = repository.getAdaptiveQuizQuestions(setOf("Jinoyat huquqi"), count = 5)
        assertTrue(adaptiveQuiz.isNotEmpty())

        val topics = repository.getAvailableQuizTopics()
        assertTrue(topics.isNotEmpty())

        val achievements = repository.getAchievements()
        assertTrue(achievements.isNotEmpty())

        repository.completeLesson("l_basics_1", "course_basics", 20)
        val profile = repository.getUserProfile().firstOrNull()
        assertNotNull(profile)
        assertTrue(profile!!.xp >= 120)

        repository.addXp(50)
        val updatedProfile = repository.getUserProfile().firstOrNull()
        assertTrue(updatedProfile!!.xp >= 170)

        val progress = repository.getLessonProgress().first()
        assertTrue(progress.any { it.lessonId == "l_basics_1" })
    }

    @Test
    fun testDocumentTemplatesAndUpdates() {
        val templates = repository.getAllDocumentTemplates()
        assertTrue(templates.isNotEmpty())

        val template = repository.getTemplateById(templates[0].id)
        assertNotNull(template)

        val docText = repository.generateDocumentText(template!!, mapOf())
        assertNotNull(docText)

        val updates = repository.getRecentUpdates()
        assertTrue(updates.isNotEmpty())
    }
}
