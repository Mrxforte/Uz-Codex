package com.example

import com.example.data.remote.QueryUnderstandingEngine
import com.example.data.remote.ScopeCategory
import com.example.data.remote.UzbekTextNormalizer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UzLegalDialectRegressionTest {

    @Test
    fun test1_VulgarSlangNormalizedUnderstanding() = runBlocking {
        val query = "gaylar ushladi pravam yoq edi mashina haydadim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.IN_SCOPE_LEGAL_QUESTION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("driving_without_license"))
    }

    @Test
    fun test2_SeriousCriminalDisclosureHandling() = runBlocking {
        val query = "Men bir qizni zo'rladim, endi nima bo'ladi?"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(analysis.isSeriousDisclosure)
        assertTrue(analysis.detectedDomains.contains("criminal"))
        assertEquals("safety_guidance", analysis.schema.nextAction)
    }

    @Test
    fun test3_UnpaidSalaryShortenedSpelling() = runBlocking {
        val query = "oyligmni 2 oydan beri tashamadi nma qlay"
        val norm = UzbekTextNormalizer.normalizeText(query)
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(norm.contains("oyligimni") || norm.contains("tashlamadi") || norm.contains("nima"))
        assertTrue(analysis.detectedIssues.contains("salary_delay"))
    }

    @Test
    fun test4_InformalEmploymentTerminationSlang() = runBlocking {
        val query = "prikaz berishmadi zarplata yo'q uvolnenie qilmoqchi"
        val norm = UzbekTextNormalizer.normalizeText(query)
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(norm.contains("prikaz"))
        assertTrue(analysis.detectedDomains.contains("labor"))
    }

    @Test
    fun test5_DocumentWithholdingDialectSpelling() = runBlocking {
        val query = "dokumntimni olib qo'yishdi"
        val norm = UzbekTextNormalizer.normalizeText(query)
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertTrue(norm.contains("hujjatimni"))
        assertTrue(analysis.missingFacts.contains("withheld_item_type"))
    }

    @Test
    fun test6_VagueComplaintDialectSpelling() = runBlocking {
        val query = "meni siqishyapti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.requiresClarification)
    }

    @Test
    fun test7_UnfamiliarRegionalExpressionClarification() = runBlocking {
        val query = "meni siqishyapti"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, analysis.scopeCategory)
        assertTrue(analysis.requiresClarification)
    }

    @Test
    fun test8_ClearLegalQuestionWithSlangAndMisspellings() = runBlocking {
        val query = "gaylar ushladi pravam yoq edi mashina haydadim"
        val analysis = QueryUnderstandingEngine.analyzeQuery(query)

        assertEquals(ScopeCategory.IN_SCOPE_LEGAL_QUESTION, analysis.scopeCategory)
        assertTrue(analysis.detectedIssues.contains("driving_without_license"))
    }
}
