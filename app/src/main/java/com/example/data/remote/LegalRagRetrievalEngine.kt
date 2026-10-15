package com.example.data.remote

import android.content.Context
import com.example.data.local.LegislationData
import com.example.data.model.LegalArticle
import com.example.data.model.LegalSourceSelection

/**
 * Source-grounded RAG retrieval engine enforcing strict source selection boundaries.
 */
class LegalRagRetrievalEngine(private val context: Context) {

    /**
     * Resolves and returns ONLY the articles authorized by the user's LegalSourceSelection.
     * Unselected codes or unselected articles are strictly excluded from the pool.
     */
    fun resolveSelectedArticles(selection: LegalSourceSelection): List<LegalArticle> {
        val resolved = selection.resolveArticles(context)
        return resolved
    }

    /**
     * Returns neighboring articles within the same code if needed to provide full context
     * for multi-paragraph or multi-provision questions (e.g. general rule vs exception).
     */
    fun getNeighboringArticles(
        targetArticle: LegalArticle,
        selection: LegalSourceSelection,
        radius: Int = 1
    ): List<LegalArticle> {
        val selectedArticles = resolveSelectedArticles(selection)
        val codeArticles = selectedArticles.filter { it.codeId == targetArticle.codeId }
            .sortedBy { it.articleNumber }

        val targetIndex = codeArticles.indexOfFirst { it.id == targetArticle.id }
        if (targetIndex == -1) return listOf(targetArticle)

        val startIndex = (targetIndex - radius).coerceAtLeast(0)
        val endIndex = (targetIndex + radius).coerceAtMost(codeArticles.size - 1)

        return codeArticles.subList(startIndex, endIndex + 1)
    }

    /**
     * Search within ONLY the selected articles using keywords or exact legal terms.
     */
    fun searchInSelection(
        query: String,
        selection: LegalSourceSelection,
        limit: Int = 10
    ): List<LegalArticle> {
        val selectedArticles = resolveSelectedArticles(selection)
        if (selectedArticles.isEmpty() || query.isBlank()) return selectedArticles.take(limit)

        val normQuery = query.lowercase().trim()
        val tokens = normQuery.split(Regex("\\s+")).filter { it.length >= 3 }

        return selectedArticles.mapNotNull { article ->
            val normTitle = article.title.lowercase()
            val normContent = article.content.lowercase()
            val normNum = article.articleNumberStr.lowercase()

            var score = 0
            if (normNum.contains(normQuery) || normTitle.contains(normQuery)) score += 50
            if (normContent.contains(normQuery)) score += 30

            for (tok in tokens) {
                if (normTitle.contains(tok)) score += 10
                if (normContent.contains(tok)) score += 5
            }

            if (score > 0) Pair(score, article) else null
        }
            .sortedByDescending { it.first }
            .map { it.second }
            .take(limit)
    }
}
