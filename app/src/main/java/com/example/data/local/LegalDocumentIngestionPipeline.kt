package com.example.data.local

import android.content.Context
import com.example.data.model.LegalArticle
import com.example.data.model.LegalCode
import org.json.JSONArray
import java.security.MessageDigest

data class IngestionError(
    val fileSource: String,
    val recordIdentifier: String?,
    val errorType: String,
    val description: String
)

data class IngestionReport(
    val totalProcessed: Int,
    val validArticlesCount: Int,
    val errorCount: Int,
    val codeCoverageMap: Map<String, Int>,
    val errors: List<IngestionError>
)

object LegalDocumentIngestionPipeline {

    private fun computeSha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Structurally ingests and validates all legal articles from local assets/legal_articles.json.
     * Guarantees original text preservation without AI modification.
     */
    fun ingestLocalLegalDatabase(context: Context): Pair<List<LegalArticle>, IngestionReport> {
        val validArticles = mutableListOf<LegalArticle>()
        val errors = mutableListOf<IngestionError>()
        val seenIds = mutableSetOf<String>()
        val codeCounts = mutableMapOf<String, Int>()

        var totalProcessed = 0

        try {
            val jsonString = context.assets.open("legal_articles.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            totalProcessed = jsonArray.length()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i)
                if (obj == null) {
                    errors.add(IngestionError("legal_articles.json", "Index $i", "MALFORMED_JSON", "Object at index $i is null"))
                    continue
                }

                val id = obj.optString("id", "").trim()
                val codeId = obj.optString("codeId", "").trim()
                val chapter = obj.optString("chapter", "Umumiy qoidalar").trim()
                val articleNumber = obj.optInt("articleNumber", -1)
                val articleNumberStr = obj.optString("articleNumberStr", "").trim()
                val title = obj.optString("title", "").trim()
                val content = obj.optString("content", "").trim()
                val effectiveDate = obj.optString("effectiveDate", "").trim()
                val status = obj.optString("status", "Amalda").trim()

                // Validation checks
                if (id.isBlank()) {
                    errors.add(IngestionError("legal_articles.json", "Index $i", "MISSING_ID", "Record missing article ID"))
                    continue
                }

                if (seenIds.contains(id)) {
                    errors.add(IngestionError("legal_articles.json", id, "DUPLICATE_ID", "Duplicate article ID found: $id"))
                    continue
                }

                if (codeId.isBlank() || LegislationData.UZBEKISTAN_CODES.none { it.id == codeId }) {
                    errors.add(IngestionError("legal_articles.json", id, "INVALID_CODE_ID", "Unknown or missing code ID: $codeId"))
                    continue
                }

                if (articleNumber <= 0) {
                    errors.add(IngestionError("legal_articles.json", id, "INVALID_ARTICLE_NUMBER", "Article number must be positive: $articleNumber"))
                    continue
                }

                if (title.isBlank()) {
                    errors.add(IngestionError("legal_articles.json", id, "EMPTY_TITLE", "Article title is empty"))
                    continue
                }

                if (content.isBlank() || content.length < 10) {
                    errors.add(IngestionError("legal_articles.json", id, "EMPTY_CONTENT", "Article content is empty or too short"))
                    continue
                }

                // Register valid article
                seenIds.add(id)
                val article = LegalArticle(
                    id = id,
                    codeId = codeId,
                    chapter = chapter,
                    articleNumber = articleNumber,
                    articleNumberStr = if (articleNumberStr.isNotBlank()) articleNumberStr else "$articleNumber-modda",
                    title = title,
                    content = content,
                    effectiveDate = if (effectiveDate.isNotBlank()) effectiveDate else "01.01.2020",
                    status = status
                )
                validArticles.add(article)
                codeCounts[codeId] = (codeCounts[codeId] ?: 0) + 1
            }
        } catch (e: Exception) {
            errors.add(IngestionError("legal_articles.json", null, "IO_FILE_ERROR", "Failed to open legal_articles.json: ${e.message}"))
        }

        val report = IngestionReport(
            totalProcessed = totalProcessed,
            validArticlesCount = validArticles.size,
            errorCount = errors.size,
            codeCoverageMap = codeCounts,
            errors = errors
        )

        return Pair(validArticles, report)
    }
}
