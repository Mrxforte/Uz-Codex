package com.example.data.model

data class LegalCaseScenario(
    val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val narrative: String,
    val clarifyingQuestions: List<ClarifyingQuestion>,
    val factsExtracted: CaseFacts,
    val legalQualifications: List<LegalQualification>,
    val proceduralSafeguards: List<String>,
    val visualNodes: List<CaseMapNode>,
    val visualEdges: List<CaseMapEdge>,
    val urgentLegalDisclaimer: String
)

data class ClarifyingQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val explanationWhyNeeded: String
)

data class CaseFacts(
    val partiesInvolved: List<String>,
    val timeline: String,
    val actionsAlleged: List<String>,
    val mentalStateIntent: String,
    val consequences: String,
    val availableEvidence: List<String>,
    val missingCrucialFacts: List<String>
)

data class LegalQualification(
    val articleCitation: String,
    val codeName: String,
    val relevance: String,
    val statutoryLanguage: String,
    val likelihoodRating: String, // e.g. "Asosiy ehtimol", "Muqobil malakalash", "Istisno holat"
    val reasonWhyApplicable: String
)

data class CaseMapNode(
    val id: String,
    val label: String,
    val nodeType: String, // FACT, ISSUE, LAW_ARTICLE, PROCEDURE, SOURCE
    val description: String,
    val sourceRef: String? = null
)

data class CaseMapEdge(
    val fromNodeId: String,
    val toNodeId: String,
    val label: String
)
