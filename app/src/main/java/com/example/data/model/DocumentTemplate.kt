package com.example.data.model

data class DocumentTemplate(
    val id: String,
    val title: String,
    val category: String, // Mehnat, Iste'molchi, Ma'muriy, Fuqarolik, Axborot
    val description: String,
    val statutoryBasis: String,
    val fields: List<TemplateField>,
    val instructions: String
)

data class TemplateField(
    val key: String,
    val label: String,
    val placeholder: String,
    val isMultiline: Boolean = false,
    val isRequired: Boolean = true
)
