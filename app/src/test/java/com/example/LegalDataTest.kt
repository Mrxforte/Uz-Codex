package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.*
import com.example.data.remote.HybridLegalRetrievalEngine
import com.example.data.remote.QueryUnderstandingEngine
import com.example.data.remote.UzbekTextNormalizer
import com.example.ui.screens.cleanPdfDocumentText
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LegalDataTest {

    private lateinit var context: Context
    private lateinit var retrievalEngine: HybridLegalRetrievalEngine

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        LegislationData.loadAllArticles(context)
        retrievalEngine = HybridLegalRetrievalEngine(context)
    }

    @Test
    fun testLegislationDataSearchAndRetrieval() {
        val codes = LegislationData.UZBEKISTAN_CODES
        assertEquals(19, codes.size)

        val fkCode = LegislationData.getCodeById("fk")
        assertNotNull(fkCode)
        assertEquals("Fuqarolik kodeksi", fkCode?.shortName)

        val jkArticles = LegislationData.getArticlesForCode("jk")
        assertTrue(jkArticles.isNotEmpty())
        assertTrue(jkArticles.any { it.articleNumber == 97 })

        val searchResults = LegislationData.searchArticles("odam o'ldirish")
        assertTrue(searchResults.isNotEmpty())
        assertTrue(searchResults.any { it.articleNumberStr == "97-modda" })

        val emptySearch = LegislationData.searchArticles("")
        assertEquals(LegislationData.KEY_ARTICLES.size, emptySearch.size)
    }

    @Test
    fun testAcademyDataQuizAndCourses() {
        val courses = AcademyData.COURSES
        assertTrue(courses.isNotEmpty())

        val questions = AcademyData.QUIZ_QUESTIONS
        assertTrue(questions.isNotEmpty())

        val topics = AcademyData.AVAILABLE_TOPICS
        assertTrue(topics.isNotEmpty())

        val adaptiveQuiz = AcademyData.generateAdaptiveQuiz(setOf("Inson huquqlari", "Jinoyat huquqi"), questionCount = 5)
        assertTrue(adaptiveQuiz.isNotEmpty())
        assertTrue(adaptiveQuiz.size <= 5)

        val achievements = AcademyData.ACHIEVEMENTS
        assertTrue(achievements.isNotEmpty())
    }

    @Test
    fun testCaseDataScenarios() {
        val scenarios = CaseData.EDUCATIONAL_SCENARIOS
        assertTrue(scenarios.isNotEmpty())

        val scenario = CaseData.getScenarioById("fight_death_scenario")
        assertNotNull(scenario)
        assertEquals("Janjal oqibatida shaxs vafot etishi (Jinoyat huquqi tahlili)", scenario?.title)
    }

    @Test
    fun testDocumentDataTemplatesAndGeneration() {
        val templates = DocumentData.TEMPLATES
        assertTrue(templates.isNotEmpty())

        val template = DocumentData.getTemplateById("doc_salary_claim")
        assertNotNull(template)

        val generatedText = DocumentData.generateFormattedDocument(
            template!!,
            mapOf("employer_name" to "Test MChJ", "employee_name" to "Ali Valiyev")
        )
        assertTrue(generatedText.contains("Test MChJ"))
        assertTrue(generatedText.contains("Ali Valiyev"))
    }

    @Test
    fun testUpdatesData() {
        val updates = UpdatesData.RECENT_UPDATES
        assertTrue(updates.isNotEmpty())
        assertTrue(updates.any { it.codeName == "Mehnat kodeksi" })
    }

    @Test
    fun testCleanPdfDocumentText() {
        val messyPdfText = """
            paytdagidan
            boshqacha
            qoidalarni
            belgilaydigan
            qonun
            qabul
            qilingan
            boʻlsa,
            10/15/26, 2:44 PM
            21.12.1995. Oʻzbekiston Respublikasining Fuqarolik kodeksi
            3/98
            a) birinchi qoida
            b) ikkinchi qoida
        """.trimIndent()

        val cleaned = cleanPdfDocumentText(messyPdfText)

        assertFalse(cleaned.contains("10/15/26"))
        assertFalse(cleaned.contains("3/98"))
        assertFalse(cleaned.contains("21.12.1995. Oʻzbekiston"))
        assertTrue(cleaned.contains("paytdagidan boshqacha qoidalarni belgilaydigan qonun qabul qilingan boʻlsa,"))
        assertTrue(cleaned.contains("a) birinchi qoida"))
        assertTrue(cleaned.contains("b) ikkinchi qoida"))
    }

    // REQUIRED SUITE TESTS

    @Test
    fun test1_TrafficMultiIssue() = runBlocking {
        val query = "remen taqmay qizilda otib ketdim qancha jarima"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("seat_belt_violation"))
        assertTrue(analysis.detectedIssues.contains("red_light_violation"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.codeId == "mjtk" })
    }

    @Test
    fun test2_SeriousCriminalDisclosure() = runBlocking {
        val query = "Men bir qizni zo'rladim, endi nima bo'ladi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.isSeriousDisclosure)
        assertTrue(analysis.detectedDomains.contains("criminal"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
    }

    @Test
    fun test3_EmploymentTerminationClarification() = runBlocking {
        val query = "Ishdan haydashdi."
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("unlawful_dismissal"))
        assertTrue(analysis.requiresClarification)

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.codeId == "mk" })
    }

    @Test
    fun test5_DebtRecoveryDispute() = runBlocking {
        val query = "Qarz berdim, qaytarmayapti."
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("debt_recovery"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.codeId == "fk" })
    }

    @Test
    fun test6_DirectArticleQuery() = runBlocking {
        val query = "JK 97-modda nima deydi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)
        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)

        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.id == "jk_97" })
    }

    @Test
    fun test7_MisspelledAndColloquialUzbek() = runBlocking {
        val query = "prikaz berishmadi zarplata yo'q uvolnenie qilmoqchi"
        val norm = UzbekTextNormalizer.normalizeText(query)
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(norm.contains("prikaz"))
        assertTrue(analysis.detectedDomains.contains("labor"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.codeId == "mk" })
    }

    @Test
    fun test9_MultiIntentQuery() = runBlocking {
        val query = "Kvartiramni ijaraga berganman ijara haqini bermayapti va ishxonamda oyligimni kechiktirishyapti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedDomains.contains("housing") || analysis.detectedIssues.contains("tenancy_dispute"))
        assertTrue(analysis.detectedDomains.contains("labor") || analysis.detectedIssues.contains("salary_delay"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
    }

    @Test
    fun test12_DrivingWithoutLicenseDistinction() = runBlocking {
        val query = "Pravam yo'q edi mashina haydadim."
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("driving_without_license"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.codeId == "mjtk" })
    }

    @Test
    fun test14_VulgarSlangNormalizedUnderstanding() = runBlocking {
        val query = "gaylar ushladi pravam yoq edi mashina haydadim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("driving_without_license"))

        val retrieved = retrievalEngine.retrieveRelevantArticles(query, analysis.searchQueries)
        assertTrue(retrieved.isNotEmpty())
        assertTrue(retrieved.any { it.article.codeId == "mjtk" })
    }
}
