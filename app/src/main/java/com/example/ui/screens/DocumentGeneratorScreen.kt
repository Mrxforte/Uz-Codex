package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentTemplate
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*

@Composable
fun DocumentGeneratorScreen(
    repository: LegalRepository,
    onBack: () -> Unit
) {
    val templates = remember { repository.getAllDocumentTemplates() }

    var selectedCategory by remember { mutableStateOf("Barchasi") }
    val categories = listOf("Barchasi", "Mehnat", "Oila", "Biznes")

    var selectedTemplate by remember { mutableStateOf<DocumentTemplate?>(null) }
    var fieldValues by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var showPreview by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = if (selectedTemplate == null) "Hujjat shablonlari" else selectedTemplate!!.title,
                subtitle = if (selectedTemplate == null) "Kerakli hujjatni oson yarating" else "Shablon to'ldirish",
                showBackButton = true,
                onBackClick = {
                    if (showPreview) {
                        showPreview = false
                    } else if (selectedTemplate != null) {
                        selectedTemplate = null
                        fieldValues = mutableMapOf()
                    } else {
                        onBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("document_generator_screen")
        ) {
            if (selectedTemplate == null) {
                // Category Filter Chips matching mockup: Barchasi, Mehnat, Oila, Biznes
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UzLegalPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Templates List matching mockup with document icon and chevron
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(templates) { template ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTemplate = template
                                    fieldValues = mutableMapOf()
                                    showPreview = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(UzLegalPrimary.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = UzLegalPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = template.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = template.statutoryBasis,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = UzLegalTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = UzLegalTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            } else if (!showPreview) {
                // Form Fields Editor
                val template = selectedTemplate!!
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(template.fields) { field ->
                        Column {
                            Text(
                                text = field.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = fieldValues[field.key] ?: "",
                                onValueChange = { newValue ->
                                    fieldValues = fieldValues.toMutableMap().apply { put(field.key, newValue) }
                                },
                                placeholder = { Text(field.placeholder, fontSize = 13.sp, color = UzLegalTextSecondary) },
                                minLines = if (field.isMultiline) 3 else 1,
                                maxLines = if (field.isMultiline) 6 else 1,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = UzLegalPrimary,
                                    unfocusedBorderColor = UzLegalBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("field_${field.key}")
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = { showPreview = true },
                            colors = ButtonDefaults.buttonColors(containerColor = UzLegalPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_preview_document")
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hujjatni shakllantirish (Preview)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Professional Full-Screen Document Viewer Mode with PDF Print, Zoom & Find Tools
                val template = selectedTemplate!!
                val formattedText = remember(template, fieldValues) {
                    repository.generateDocumentText(template, fieldValues)
                }

                DocumentViewerScreen(
                    title = template.title,
                    content = formattedText,
                    statutoryBasis = template.statutoryBasis,
                    onBack = { showPreview = false }
                )
            }
        }
    }
}
