package com.example.data.remote

import com.example.data.local.LegislationData
import com.example.data.model.LegalArticle

data class ScoredLegalArticle(
    val article: LegalArticle,
    val score: Double, // Normalized relevance score between 0.0 and 1.0
    val matchReasons: List<String>
)

object LegalReranker {

    /**
     * Reranks candidate legal articles against the query using multi-feature evaluation.
     */
    fun rerank(
        query: String,
        candidates: List<LegalArticle>,
        explicitCodeFilter: String? = null,
        minScoreThreshold: Double = 0.02,
        limit: Int = 5
    ): List<ScoredLegalArticle> {
        if (candidates.isEmpty()) return emptyList()

        val normQuery = UzbekLegalNormalizer.normalizeText(query)
        val queryStems = UzbekLegalNormalizer.tokenizeAndStem(query)
        val synonymHints = LegalSynonymDictionary.getRetrievalHints(query)

        // Article number extraction e.g. "14-modda", "14 modda", "128-4"
        val artNumMatch = Regex("([0-9]{1,3}(?:[-–—_][0-9]{1,2})?)").find(normQuery)?.groupValues?.get(1)

        val scoredList = candidates.map { article ->
            var rawScore = 0.0
            val reasons = mutableListOf<String>()

            val normTitle = UzbekLegalNormalizer.normalizeText(article.title)
            val normContent = UzbekLegalNormalizer.normalizeText(article.content)
            val code = LegislationData.getCodeById(article.codeId)
            val normCodeName = code?.let { UzbekLegalNormalizer.normalizeText(it.shortName + " " + it.officialUzbekName) } ?: ""

            // Feature 1: Direct ID Match or ID in Query
            if (normQuery.contains(article.id) || article.id.replace("_", "").contains(normQuery.replace("-", "").replace("_", "").replace(" ", ""))) {
                rawScore += 50.0
                reasons.add("Direct article ID match: ${article.id}")
            }

            // Feature 2: Explicit Code Filter match
            if (explicitCodeFilter != null && article.codeId == explicitCodeFilter) {
                rawScore += 25.0
                reasons.add("Explicit code filter match: $explicitCodeFilter")
            } else if (normCodeName.isNotEmpty() && queryStems.any { normCodeName.contains(it) }) {
                rawScore += 15.0
                reasons.add("Code name match in query")
            }

            // Feature 3: Exact Article Number Match
            if (artNumMatch != null && (article.articleNumber.toString() == artNumMatch || article.articleNumberStr.contains(artNumMatch))) {
                rawScore += 45.0
                reasons.add("Exact article number match: $artNumMatch")
            }

            // Feature 4: Title Match
            if (normTitle.contains(normQuery) && normQuery.length >= 4) {
                rawScore += 35.0
                reasons.add("Exact phrase match in article title")
            } else {
                var titleMatches = 0
                for (stem in queryStems) {
                    if (normTitle.contains(stem)) titleMatches++
                }
                if (titleMatches > 0) {
                    rawScore += (titleMatches * 10.0)
                    reasons.add("Title token matches ($titleMatches)")
                }
            }

            // Feature 5: Content Term Matches
            var contentMatches = 0
            for (stem in queryStems) {
                if (normContent.contains(stem)) contentMatches++
            }
            if (contentMatches > 0) {
                rawScore += (contentMatches * 4.0).coerceAtMost(25.0)
                reasons.add("Content term matches ($contentMatches)")
            }

            // Feature 6: Synonym Hints Match
            for (hint in synonymHints) {
                val normHint = UzbekLegalNormalizer.normalizeText(hint)
                if (normTitle.contains(normHint) || normContent.contains(normHint) || article.articleNumberStr.contains(normHint)) {
                    rawScore += 12.0
                    reasons.add("Synonym hint match: '$hint'")
                }
            }

            // Calculate normalized score (0.0 to 1.0)
            val maxPossibleScore = 100.0
            val normalizedScore = (rawScore / maxPossibleScore).coerceIn(0.0, 1.0)

            ScoredLegalArticle(
                article = article,
                score = normalizedScore,
                matchReasons = reasons
            )
        }

        return scoredList
            .filter { it.score >= minScoreThreshold }
            .sortedByDescending { it.score }
            .take(limit)
    }
}
