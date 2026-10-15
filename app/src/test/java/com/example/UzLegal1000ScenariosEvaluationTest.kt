package com.example

import com.example.data.remote.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UzLegal1000ScenariosEvaluationTest {

    @Test
    fun testAll1000ScenariosPipelineAndQualityMetrics() = runBlocking {
        val dataset = UzLegal1000ScenarioDataset.getScenarios()
        assertEquals(1000, dataset.size)

        var correctScopeCount = 0
        var correctRetrievalDecisionCount = 0
        var correctClarificationDecisionCount = 0
        var totalNegativeRagCases = 0
        var correctNegativeRagCount = 0
        var totalPositiveRagCases = 0
        var correctPositiveRagCount = 0

        for (scenario in dataset) {
            val analysis = QueryUnderstandingEngine.analyzeQuery(scenario.inputQuery)

            if (analysis.scopeCategory == scenario.expectedScopeCategory) {
                correctScopeCount++
            }

            if (analysis.schema.needsLegalRetrieval == scenario.shouldRetrieve) {
                correctRetrievalDecisionCount++
            }

            if (!scenario.shouldRetrieve) {
                totalNegativeRagCases++
                if (!analysis.schema.needsLegalRetrieval) {
                    correctNegativeRagCount++
                }
            } else {
                totalPositiveRagCases++
                if (analysis.schema.needsLegalRetrieval) {
                    correctPositiveRagCount++
                }
            }

            if (analysis.requiresClarification == scenario.shouldClarify) {
                correctClarificationDecisionCount++
            }
        }

        val overallScopeAccuracy = (correctScopeCount.toDouble() / dataset.size) * 100
        val overallRetrievalAccuracy = (correctRetrievalDecisionCount.toDouble() / dataset.size) * 100
        val negativeRagAccuracy = (correctNegativeRagCount.toDouble() / totalNegativeRagCases) * 100
        val positiveRagAccuracy = (correctPositiveRagCount.toDouble() / totalPositiveRagCases) * 100
        val clarificationAccuracy = (correctClarificationDecisionCount.toDouble() / dataset.size) * 100

        println("=== UzLegal 1,000 Scenarios Evaluation Metrics ===")
        println("Total Evaluated Scenarios: ${dataset.size}")
        println("Overall Scope Routing Accuracy: $overallScopeAccuracy%")
        println("Overall RAG Retrieval Decision Accuracy: $overallRetrievalAccuracy%")
        println("Negative RAG Routing Accuracy (0% unnecessary retrieval): $negativeRagAccuracy%")
        println("Positive RAG Routing Accuracy (Concrete legal queries): $positiveRagAccuracy%")
        println("Clarification Decision Accuracy: $clarificationAccuracy%")

        assertTrue("Overall Scope Routing Accuracy must be >= 85%", overallScopeAccuracy >= 85.0)
        assertTrue("Negative RAG Routing Accuracy must be >= 85%", negativeRagAccuracy >= 85.0)
        assertTrue("Positive RAG Routing Accuracy must be >= 85%", positiveRagAccuracy >= 85.0)
        assertTrue("Clarification Decision Accuracy must be >= 85%", clarificationAccuracy >= 85.0)
    }

    @Test
    fun testUserCorrectionActionHandling() = runBlocking {
        val query = "Yo'q, men sudga bermoqchi emasman, faqat nima deyishni bilmoqchiman"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertFalse(analysis.conversationState.isLitigationDesired)
        assertEquals("provide_communication_advice", analysis.schema.nextAction)
    }
}
