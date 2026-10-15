package com.example

import com.example.data.local.*
import com.example.data.remote.ChatMessage
import com.example.data.remote.GeminiLegalAssistant
import com.example.data.remote.QueryUnderstandingEngine
import com.example.data.remote.UzbekTextNormalizer
import com.example.ui.screens.cleanPdfDocumentText
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class LegalDataTest {

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

    // REQUIRED SUITE TESTS (TEST 1 to TEST 14)

    @Test
    fun test1_TrafficMultiIssue() = runBlocking {
        val query = "remen taqmay qizilda otib ketdim qancha jarima"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("seat_belt_violation"))
        assertTrue(analysis.detectedIssues.contains("red_light_violation"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("125-modda") || response.citedArticles.any { it.articleNumberStr == "125-modda" })
        assertTrue(response.text.contains("128-4-modda") || response.text.contains("128") || response.citedArticles.any { it.articleNumberStr.contains("128") })
    }

    @Test
    fun test2_SeriousCriminalDisclosure() = runBlocking {
        val query = "Men bir qizni zo'rladim, endi nima bo'ladi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.isSeriousDisclosure)
        assertTrue(analysis.detectedDomains.contains("criminal"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("118-modda") || response.text.contains("Nomusga tegish"))
        assertTrue(response.text.contains("advokat") || response.text.contains("himoyach"))
    }

    @Test
    fun test3_EmploymentTerminationClarification() = runBlocking {
        val query = "Ishdan haydashdi."
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("unlawful_dismissal"))
        assertTrue(analysis.requiresClarification)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("ogohlantirish") || response.text.contains("buyruq") || response.text.contains("rasmiy"))
    }

    @Test
    fun test4_FactPreservationAcrossContext() = runBlocking {
        val history = listOf(
            ChatMessage("1", "user", "Ishdan haydashdi."),
            ChatMessage("2", "assistant", "Sizga rasmiy buyruq berildimi?"),
            ChatMessage("3", "user", "Ish beruvchi menga buyruq berdi, uch yildan beri ishlayman.")
        )
        val query = "Menga qancha kompensatsiya to'lanishi kerak?"
        val response = GeminiLegalAssistant.askLegalQuestion(query, history)

        assertNotNull(response)
        assertTrue(response.text.contains("Mehnat kodeksi"))
        assertFalse(response.text.contains("Sizga buyruq berildimi"))
    }

    @Test
    fun test5_DebtRecoveryDispute() = runBlocking {
        val query = "Qarz berdim, qaytarmayapti."
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("debt_recovery"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("Fuqarolik kodeksi") || response.text.contains("qarz", ignoreCase = true))
    }

    @Test
    fun test6_DirectArticleQuery() = runBlocking {
        val query = "JK 97-modda nima deydi?"
        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())

        assertNotNull(response)
        assertTrue(response.text.contains("97-modda") || response.citedArticles.any { it.articleNumberStr == "97-modda" })
        assertTrue(response.text.contains("odam o'ldirish") || response.text.contains("Jinoyat kodeksi"))
    }

    @Test
    fun test7_MisspelledAndColloquialUzbek() = runBlocking {
        val query = "prikaz berishmadi zarplata yo'q uvolnenie qilmoqchi"
        val norm = UzbekTextNormalizer.normalizeText(query)
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(norm.contains("prikaz"))
        assertTrue(analysis.detectedDomains.contains("labor"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("Mehnat kodeksi"))
    }

    @Test
    fun test8_MissingLawCorpusFallback() = runBlocking {
        val query = "O'zbekistonda xalqaro kosmik stansiya solig'i qancha?"
        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())

        assertNotNull(response)
        assertTrue(response.text.contains("topilmadi") || response.text.contains("bazasida"))
    }

    @Test
    fun test9_MultiIntentQuery() = runBlocking {
        val query = "Kvartiramni ijaraga berganman ijara haqini bermayapti va ishxonamda oyligimni kechiktirishyapti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedDomains.contains("housing") || analysis.detectedIssues.contains("tenancy_dispute"))
        assertTrue(analysis.detectedDomains.contains("labor") || analysis.detectedIssues.contains("salary_delay"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.citedArticles.size >= 2 || response.text.contains("ijara") && response.text.contains("ish haqi"))
    }

    @Test
    fun test10_FollowUpContextDependency() = runBlocking {
        val history = listOf(
            ChatMessage("1", "user", "Ishdan haydashdi."),
            ChatMessage("2", "assistant", "Sizga rasmiy buyruq berildimi?"),
            ChatMessage("3", "user", "Menga baribir yozma ogohlantirish berishgani yo'q")
        )
        val query = "Shu holatda sudga bersam bo'ladimi?"
        val response = GeminiLegalAssistant.askLegalQuestion(query, history)

        assertNotNull(response)
        assertTrue(response.text.contains("Mehnat kodeksi") || response.text.contains("sud"))
    }

    @Test
    fun test11_DrivingVsPedestrianRedLightClarification() = runBlocking {
        val query = "Qizilga kirib ketdim."
        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())

        assertNotNull(response)
        assertTrue(response.text.contains("mashina") || response.text.contains("piyoda") || response.text.contains("svetofor") || response.text.contains("128-modda"))
    }

    @Test
    fun test12_DrivingWithoutLicenseDistinction() = runBlocking {
        val query = "Pravam yo'q edi mashina haydadim."
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("driving_without_license"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("135-modda") || response.text.contains("haydovchilik guvohnomasi") || response.citedArticles.any { it.articleNumberStr.contains("135") })
    }

    @Test
    fun test13_FineAmountCalculation() = runBlocking {
        val query = "Qizil chiroqda o'tsam jarimasi BHMning necha baravari?"
        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())

        assertNotNull(response)
        assertTrue(response.text.contains("128") || response.text.contains("BHM") || response.text.contains("jarima"))
    }

    @Test
    fun test14_VulgarSlangNormalizedUnderstanding() = runBlocking {
        val query = "gaylar ushladi pravam yoq edi mashina haydadim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("driving_without_license"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("135-modda") || response.text.contains("haydovchilik guvohnomasi"))
    }
}
