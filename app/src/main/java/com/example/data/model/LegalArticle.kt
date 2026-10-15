package com.example.data.model

data class LegalArticle(
    val id: String,
    val codeId: String,
    val chapter: String,
    val articleNumber: Int,
    val articleNumberStr: String,
    val title: String,
    val content: String,
    val effectiveDate: String,
    val status: String = "Amalda",
    val isKeyArticle: Boolean = true,
    val relatedArticleIds: List<String> = emptyList()
)
