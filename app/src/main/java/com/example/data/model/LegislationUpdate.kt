package com.example.data.model

data class LegislationUpdate(
    val id: String,
    val codeName: String,
    val articleNumber: String,
    val amendingLaw: String,
    val adoptedDate: String,
    val effectiveDate: String,
    val summaryOfChange: String,
    val oldWording: String,
    val newWording: String,
    val importance: String = "Yuqori" // Yuqori, O'rta, Texnik
)
