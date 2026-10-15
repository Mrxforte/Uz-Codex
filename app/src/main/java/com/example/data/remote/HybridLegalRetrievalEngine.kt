package com.example.data.remote

import android.content.Context
import com.example.data.local.LegalDocumentIngestionPipeline
import com.example.data.local.LegislationData
import com.example.data.model.LegalArticle

class HybridLegalRetrievalEngine(private val context: Context) {

    private val ingestedArticles: List<LegalArticle> by lazy {
        val (articles, _) = LegalDocumentIngestionPipeline.ingestLocalLegalDatabase(context)
        val keyArts = LegislationData.KEY_ARTICLES
        val loadedArts = LegislationData.getAllArticles(context)
        (articles + keyArts + loadedArts).distinctBy { it.id }
    }

    /**
     * Detects if query explicitly names a specific code or topic.
     */
    fun detectExplicitCodeFilter(query: String): String? {
        val norm = UzbekLegalNormalizer.normalizeText(query)
        return when {
            norm.contains("mehnat kodeks") || norm.contains("mk ") || norm.startsWith("mk") -> "mk"
            norm.contains("fuqarolik kodeks") || norm.contains("fk ") || norm.startsWith("fk") -> "fk"
            norm.contains("jinoyat kodeks") || norm.contains("jk ") || norm.startsWith("jk") -> "jk"
            norm.contains("jinoyat protsessual") || norm.contains("jpk") -> "jpk"
            norm.contains("fuqarolik protsessual") || norm.contains("fpk") -> "fpk"
            norm.contains("oila kodeks") || norm.contains("ok ") || norm.startsWith("ok") -> "ok"
            norm.contains("soliq kodeks") || norm.contains("sk ") || norm.startsWith("sk") -> "sk"
            norm.contains("uy joy kodeks") || norm.contains("ujk") -> "ujk"
            norm.contains("ma'muriy javobgarlik") || norm.contains("mjtk") || norm.contains("mjoek") -> "mjtk"
            norm.contains("bojxona kodeks") || norm.contains("bk ") || norm.startsWith("bk") -> "bk"
            norm.contains("havo kodeks") || norm.contains("hk ") || norm.startsWith("hk") -> "hk"
            norm.contains("jinoyat ijroiya") || norm.contains("jik") -> "jik"
            norm.contains("iqtisodiy protsessual") || norm.contains("ipk") -> "ipk"
            norm.contains("ma'muriy sud") || norm.contains("msiyk") -> "msiyk"
            else -> null
        }
    }

    /**
     * Performs hybrid multi-stage retrieval: candidate generation -> deduplication -> reranking.
     */
    fun retrieveRelevantArticles(
        query: String,
        searchQueries: List<String> = emptyList(),
        minScoreThreshold: Double = 0.02,
        limit: Int = 5
    ): List<ScoredLegalArticle> {
        if (query.isBlank()) return emptyList()

        val explicitCode = detectExplicitCodeFilter(query)
        val candidatePool = mutableListOf<LegalArticle>()
        val seenIds = mutableSetOf<String>()

        fun addCandidate(article: LegalArticle) {
            if (!seenIds.contains(article.id)) {
                seenIds.add(article.id)
                candidatePool.add(article)
            }
        }

        // 1. Direct Article ID, Number & Synonym Hints Match
        val hints = LegalSynonymDictionary.getRetrievalHints(query)
        val combinedQueries = (listOf(query) + searchQueries + hints).distinct()

        for (q in combinedQueries) {
            val normQ = UzbekLegalNormalizer.normalizeText(q)

            // Direct ID match e.g. "fk_14", "jk_97", "mk_161", "mjtk_128", "hk_1", "jik_8"
            val directById = ingestedArticles.find {
                it.id.equals(q, ignoreCase = true) ||
                        it.id.equals(normQ, ignoreCase = true) ||
                        it.id.replace("_", "").equals(normQ.replace("-", "").replace("_", "").replace(" ", ""), ignoreCase = true)
            }
            if (directById != null) addCandidate(directById)

            // Direct article number match
            val artNumMatches = Regex("([0-9]{1,3})").findAll(normQ).map { it.groupValues[1] }.toList()
            for (artNum in artNumMatches) {
                val matches = ingestedArticles.filter {
                    (explicitCode == null || it.codeId == explicitCode) &&
                            (it.articleNumber.toString() == artNum || it.articleNumberStr.contains(artNum))
                }
                matches.forEach { addCandidate(it) }
            }
        }

        // 2. Keyword & Token Matching
        val stems = UzbekLegalNormalizer.tokenizeAndStem(query)
        val allSearchTokens = (stems + hints.flatMap { UzbekLegalNormalizer.tokenizeAndStem(it) }).distinct()

        val keywordMatches = ingestedArticles.filter { article ->
            val normTitle = UzbekLegalNormalizer.normalizeText(article.title)
            val normContent = UzbekLegalNormalizer.normalizeText(article.content)

            allSearchTokens.any { token ->
                normTitle.contains(token) || normContent.contains(token)
            }
        }
        keywordMatches.forEach { addCandidate(it) }

        // 3. Multi-feature Reranking
        return LegalReranker.rerank(
            query = query,
            candidates = candidatePool,
            explicitCodeFilter = explicitCode,
            minScoreThreshold = minScoreThreshold,
            limit = limit
        )
    }
}