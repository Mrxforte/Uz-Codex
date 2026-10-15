package com.example.data.model

data class LegalCode(
    val id: String,
    val officialUzbekName: String,
    val englishName: String,
    val shortName: String,
    val documentNumber: String,
    val adoptionDate: String,
    val effectiveDate: String,
    val status: String = "Amalda",
    val lexUzUrl: String,
    val description: String,
    val totalArticlesEstimate: Int,
    val category: String,
    val iconName: String = "gavel"
)
