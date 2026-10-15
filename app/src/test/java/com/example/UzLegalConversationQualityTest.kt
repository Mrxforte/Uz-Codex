package com.example

import com.example.data.remote.ChatMessage
import com.example.data.remote.GeminiLegalAssistant
import com.example.data.remote.QueryUnderstandingEngine
import com.example.data.remote.ScopeCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UzLegalConversationQualityTest {

    @Test
    fun testCase1_NaturalGreetingWithoutLegalRetrieval() = runBlocking {
        val query = "Salom"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)
        assertTrue(analysis.isGreeting)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertFalse(response.isGrounded)
        assertTrue(response.citedArticles.isEmpty())
        assertTrue(response.text.contains("Salom") || response.text.contains("yordam"))
    }

    @Test
    fun testCase2_BriefAcknowledgmentWithoutDisclaimer() = runBlocking {
        val query = "Rahmat"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertFalse(response.isGrounded)
        assertTrue(response.citedArticles.isEmpty())
        assertTrue(response.text.contains("Arzimaydi") || response.text.contains("yozing"))
        assertFalse(response.text.contains("Ogohlantirish") || response.text.contains("javobgarlikdan ozod"))
    }

    @Test
    fun testCase3_EmploymentTerminationClarification() = runBlocking {
        val query = "Meni ishdan haydashdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("unlawful_dismissal"))
        assertTrue(analysis.requiresClarification)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("buyruq") || response.text.contains("og'zaki") || response.text.contains("ogohlantirish"))
    }

    @Test
    fun testCase4_InformalDebtDisputeClarification() = runBlocking {
        val query = "Pulimni yeb ketdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("debt_recovery"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("tilxat") || response.text.contains("shartnoma") || response.text.contains("hujjat"))
        assertFalse(response.text.contains("jinoyatchi") || response.text.contains("qamoqqa olinadi"))
    }

    @Test
    fun testCase5_TrafficViolationClarification() = runBlocking {
        val query = "Qizilda o‘tib ketdim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("red_light_violation"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("mashina") || response.text.contains("svetofor") || response.text.contains("protokol") || response.text.contains("radar"))
    }

    @Test
    fun testCase6_ConsequencesInEstablishedContext() = runBlocking {
        val history = listOf(
            ChatMessage("1", "user", "Qizil chiroqda o'tib ketdim"),
            ChatMessage("2", "assistant", "Sizga YHXX protokoli tuzildimi?"),
            ChatMessage("3", "user", "Ha, inspektor protokol tuzdi")
        )
        val query = "Endi boshimga nima tushadi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query, history)

        assertEquals(ScopeCategory.IN_SCOPE_LEGAL_QUESTION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("red_light_violation"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, history)
        assertNotNull(response)
        assertTrue(response.text.contains("128") || response.text.contains("jarima") || response.text.contains("BHM"))
    }

    @Test
    fun testCase7_UnrelatedProgrammingQuestionRedirect() = runBlocking {
        val query = "Python-da for loop qanday yoziladi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.UNRELATED_REQUEST, analysis.scopeCategory)
        assertTrue(analysis.isUnmatchedDomain)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertEquals(
            "Men UzLegal — O‘zbekiston qonunchiligi va huquqiy masalalar bo‘yicha yordamchiman. Huquqiy savolingiz yoki hal qilmoqchi bo‘lgan vaziyatingiz bo‘lsa, yordam beraman.",
            response.text.trim()
        )
    }

    @Test
    fun testCase8_MixedUnrelatedAndLegalMessage() = runBlocking {
        val query = "Barselona jamoasi zo'r, lekin mehnat shartnomasini kimlar bekor qilishi mumkin?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.IN_SCOPE_LEGAL_QUESTION, analysis.scopeCategory)
        assertTrue(analysis.detectedDomains.contains("labor") || analysis.detectedIssues.contains("unlawful_dismissal"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("Mehnat kodeksi"))
        assertFalse(response.text.contains("Messia") || response.text.contains("chempion"))
    }

    @Test
    fun testCase9_FollowUpNewFactUpdatesAnalysis() = runBlocking {
        val history = listOf(
            ChatMessage("1", "user", "Meni ishdan haydashdi"),
            ChatMessage("2", "assistant", "Sizni rasmiy buyruq bilan ishdan bo'shatishdimi?")
        )
        val query = "Uch kuncha oldin yozma buyruq berishdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query, history)

        assertEquals(ScopeCategory.IN_SCOPE_LEGAL_QUESTION, analysis.scopeCategory)
        assertFalse(analysis.requiresClarification)

        val response = GeminiLegalAssistant.askLegalQuestion(query, history)
        assertNotNull(response)
        assertTrue(response.text.contains("Mehnat kodeksi"))
        assertFalse(response.text.contains("Sizni rasmiy buyruq bilan"))
    }

    @Test
    fun testCase10_DistressedUserThreatPrioritizesSafety() = runBlocking {
        val query = "Qo'shnim meni o'ldirish bilan tahdid qilyapti, juda xavotirdaman"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.URGENT_SAFETY_RELATED, analysis.scopeCategory)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("102") || response.text.contains("IIV") || response.text.contains("xavfsizlik"))
        assertTrue(response.text.contains("Prokuratura") || response.text.contains("advokat") || response.text.contains("order"))
    }

    @Test
    fun testCase11_EmotionalPersonalSituationSharing() = runBlocking {
        val query = "Bugun boshimga juda yomon ish tushdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.isEmotionalSharing)
        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertFalse(response.isGrounded)
        assertTrue(response.citedArticles.isEmpty())
        assertTrue(response.text.contains("Afsus") || response.text.contains("nima bo‘ldi"))
        assertFalse(response.text.contains("Jinoyat kodeksi") || response.text.contains("modda"))
    }

    @Test
    fun testQualityMetrics() = runBlocking {
        val testQueries = listOf(
            Pair("Salom", ScopeCategory.GREETING_OR_ACKNOWLEDGMENT),
            Pair("Rahmat", ScopeCategory.GREETING_OR_ACKNOWLEDGMENT),
            Pair("Meni ishdan haydashdi", ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION),
            Pair("Pulimni yeb ketdi", ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION),
            Pair("Qizilda o'tib ketdim", ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION),
            Pair("Python-da code yozib ber", ScopeCategory.UNRELATED_REQUEST),
            Pair("JK 97-modda nima deydi?", ScopeCategory.IN_SCOPE_LEGAL_QUESTION),
            Pair("Pravam yo'q edi mashina haydadim", ScopeCategory.IN_SCOPE_LEGAL_QUESTION),
            Pair("Qo'shnim meni o'ldirish bilan tahdid qilyapti", ScopeCategory.URGENT_SAFETY_RELATED),
            Pair("Jarimani tushunmadim, protokol tuzishdi", ScopeCategory.LEGAL_DOCUMENT_EXPLANATION)
        )

        var correctIntentCount = 0
        var unnecessaryClarificationCount = 0
        var scopeCompliantCount = 0

        for ((q, expectedCat) in testQueries) {
            val analysis = QueryUnderstandingEngine.analyzeQuery(q)
            if (analysis.scopeCategory == expectedCat) {
                correctIntentCount++
            }

            if ((expectedCat == ScopeCategory.GREETING_OR_ACKNOWLEDGMENT || expectedCat == ScopeCategory.IN_SCOPE_LEGAL_QUESTION) && analysis.requiresClarification) {
                unnecessaryClarificationCount++
            }

            if (expectedCat == ScopeCategory.UNRELATED_REQUEST && analysis.scopeCategory == ScopeCategory.UNRELATED_REQUEST) {
                scopeCompliantCount++
            } else if (expectedCat != ScopeCategory.UNRELATED_REQUEST && analysis.scopeCategory != ScopeCategory.UNRELATED_REQUEST) {
                scopeCompliantCount++
            }
        }

        val intentAccuracy = (correctIntentCount.toDouble() / testQueries.size) * 100
        val unnecessaryClarificationRate = (unnecessaryClarificationCount.toDouble() / testQueries.size) * 100
        val scopeComplianceRate = (scopeCompliantCount.toDouble() / testQueries.size) * 100

        println("=== UzLegal Conversation Quality Metrics ===")
        println("Intent Classification Accuracy: $intentAccuracy%")
        println("Unnecessary Clarification Rate: $unnecessaryClarificationRate%")
        println("Scope Compliance Rate: $scopeComplianceRate%")

        assertTrue("Intent classification accuracy must be >= 90%", intentAccuracy >= 90.0)
        assertEquals("Unnecessary clarification rate must be 0%", 0.0, unnecessaryClarificationRate, 0.01)
        assertEquals("Scope compliance rate must be 100%", 100.0, scopeComplianceRate, 0.01)
    }
}
