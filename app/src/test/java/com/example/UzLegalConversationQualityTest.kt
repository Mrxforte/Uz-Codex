package com.example

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
        assertFalse(analysis.schema.needsLegalRetrieval)
    }

    @Test
    fun testCase2_BriefAcknowledgmentWithoutDisclaimer() = runBlocking {
        val query = "Rahmat"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)
        assertFalse(analysis.schema.needsLegalRetrieval)
    }

    @Test
    fun testCase3_EmploymentTerminationClarification() = runBlocking {
        val query = "Meni ishdan haydashdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("unlawful_dismissal"))
        assertTrue(analysis.requiresClarification)
    }

    @Test
    fun testCase4_InformalDebtDisputeClarification() = runBlocking {
        val query = "Pulimni yeb ketdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("debt_recovery"))
    }

    @Test
    fun testCase5_TrafficViolationClarification() = runBlocking {
        val query = "Qizilda o‘tib ketdim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("red_light_violation"))
    }

    @Test
    fun testCase7_UnrelatedProgrammingQuestionRedirect() = runBlocking {
        val query = "Python-da for loop qanday yoziladi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.UNRELATED_REQUEST, analysis.scopeCategory)
        assertTrue(analysis.isUnmatchedDomain)
    }

    @Test
    fun testCase8_MixedUnrelatedAndLegalMessage() = runBlocking {
        val query = "Barselona jamoasi zo'r, lekin mehnat shartnomasini kimlar bekor qilishi mumkin?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedDomains.contains("labor") || analysis.detectedIssues.contains("unlawful_dismissal"))
    }
}
