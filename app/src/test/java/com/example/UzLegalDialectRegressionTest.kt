package com.example

import com.example.data.remote.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UzLegalDialectRegressionTest {

    @Test
    fun test1_CasualGreetingWithSlang() = runBlocking {
        val query = "salom brat qalaysan"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)
        assertFalse(analysis.schema.needsLegalRetrieval)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertFalse(response.isGrounded)
        assertTrue(response.citedArticles.isEmpty())
        assertTrue(response.text.contains("Salom") || response.text.contains("yordam"))
    }

    @Test
    fun test2_EmploymentProblemWithTypos() = runBlocking {
        val query = "nma qlsam boladi ishga kiritmayapti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.scopeCategory == ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION || analysis.scopeCategory == ScopeCategory.IN_SCOPE_LEGAL_QUESTION)
        assertTrue(analysis.detectedDomains.contains("labor") || analysis.detectedIssues.contains("unlawful_dismissal"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("buyruq") || response.text.contains("Mehnat kodeks") || response.text.contains("ogohlantirish"))
    }

    @Test
    fun test3_UnpaidSalaryShortenedSpelling() = runBlocking {
        val query = "oyligmni tashamadi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.detectedIssues.contains("salary_delay") || analysis.detectedDomains.contains("labor"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("253-modda") || response.text.contains("ish haqi") || response.text.contains("Mehnat kodeks"))
    }

    @Test
    fun test4_NegationPreservation() = runBlocking {
        val query = "u meni urmedi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)
        assertFalse(analysis.isSeriousDisclosure)
        assertFalse(analysis.schema.needsLegalRetrieval)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertFalse(response.text.contains("102") || response.text.contains("IIV") || response.text.contains("militsiya"))
    }

    @Test
    fun test5_AssaultStatementDiffersFromNegation() = runBlocking {
        val query = "u meni urdi"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.URGENT_SAFETY_RELATED, analysis.scopeCategory)
        assertTrue(analysis.schema.needsLegalRetrieval)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("102") || response.text.contains("IIV") || response.text.contains("xavfsizlik"))
    }

    @Test
    fun test6_WithheldDocumentSlangClarification() = runBlocking {
        val query = "dokumntimni olib qoyishvotti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.requiresClarification)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("Nimani olib qo'yishdi") || response.text.contains("hujjat") || response.text.contains("telefon"))
    }

    @Test
    fun test7_UnfamiliarRegionalExpressionClarification() = runBlocking {
        val query = "meni siqishyapti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.requiresClarification)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("Qaysi ma'noda aytyapsiz") || response.text.contains("bosim"))
    }

    @Test
    fun test8_ClearLegalQuestionWithSlangAndMisspellings() = runBlocking {
        val query = "gaylar ushladi pravam yoq edi mashina haydadim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.IN_SCOPE_LEGAL_QUESTION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("driving_without_license"))

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertTrue(response.text.contains("135-modda") || response.text.contains("haydovchilik guvohnomasi"))
    }

    @Test
    fun test9_EmotionalStatementNoLegalRetrieval() = runBlocking {
        val query = "bugun kayfiyatim yoq"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, analysis.scopeCategory)
        assertFalse(analysis.schema.needsLegalRetrieval)

        val response = GeminiLegalAssistant.askLegalQuestion(query, emptyList())
        assertNotNull(response)
        assertFalse(response.isGrounded)
        assertTrue(response.citedArticles.isEmpty())
        assertTrue(response.text.contains("Afsus") || response.text.contains("nima bo‘ldi"))
    }

    @Test
    fun test10_All1500DialectScenariosEvaluation() = runBlocking {
        val dataset = UzLegal1500DialectDataset.getScenarios()
        assertEquals(1500, dataset.size)

        var correctScopeCount = 0
        var correctRetrievalDecisionCount = 0

        for (scenario in dataset) {
            val analysis = QueryUnderstandingEngine.analyzeQuery(scenario.inputQuery, scenario.conversationHistory)

            if (analysis.scopeCategory == scenario.expectedScopeCategory) {
                correctScopeCount++
            }

            if (analysis.schema.needsLegalRetrieval == scenario.shouldRetrieve) {
                correctRetrievalDecisionCount++
            }
        }

        val scopeAccuracy = (correctScopeCount.toDouble() / dataset.size) * 100
        val retrievalAccuracy = (correctRetrievalDecisionCount.toDouble() / dataset.size) * 100

        println("=== UzLegal 1,500 Dialect & Slang Evaluation Metrics ===")
        println("Total Evaluated Scenarios: ${dataset.size}")
        println("Dialect & Slang Scope Routing Accuracy: $scopeAccuracy%")
        println("Dialect & Slang RAG Retrieval Accuracy: $retrievalAccuracy%")

        assertTrue("Scope routing accuracy must be >= 90%", scopeAccuracy >= 90.0)
        assertTrue("RAG retrieval decision accuracy must be >= 90%", retrievalAccuracy >= 90.0)
    }
}
