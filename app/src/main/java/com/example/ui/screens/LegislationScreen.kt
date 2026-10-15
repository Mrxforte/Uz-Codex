package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
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
import com.example.data.model.LegalCode
import com.example.data.repository.LegalRepository
import com.example.ui.components.CodeCoverCard
import com.example.ui.components.CodeCoverIllustration
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*

@Composable
fun LegislationScreen(
    repository: LegalRepository,
    onSelectCode: (String) -> Unit,
    onBack: () -> Unit
) {
    val codes = remember { repository.getAllCodes() }
    var selectedCategory by remember { mutableStateOf("Barchasi") }
    var searchQuery by remember { mutableStateOf("") }
    var isGridView by remember { mutableStateOf(true) } // Default to illustrated book covers grid

    val categories = listOf(
        "Barchasi",
        "Xususiy huquq",
        "Jinoiy huquq",
        "Protsessual huquq",
        "Ijtimoiy huquq",
        "Iqtisodiy huquq",
        "Resurs huquqi"
    )

    val filteredCodes = remember(searchQuery, selectedCategory) {
        codes.filter { code ->
            val matchesCategory = selectedCategory == "Barchasi" ||
                    code.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    code.officialUzbekName.contains(searchQuery, ignoreCase = true) ||
                    code.shortName.contains(searchQuery, ignoreCase = true) ||
                    code.documentNumber.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "19 ta Qonun Kodeksi",
                subtitle = "Rasmiy korpus va tasvirlangan nashrlar",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    // View Toggle Button (Grid of Book Covers vs List)
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.testTag("toggle_view_mode")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Ko'rinishni almashtirish",
                            tint = Color.White
                        )
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
                .testTag("legislation_screen")
        ) {
            // Search Input
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Kodekslar va qonunlarni qidirish...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UzLegalSlateBody) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Tozalash")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UzLegalTeal,
                        unfocusedBorderColor = UzLegalBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_codes_input")
                )
            }

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { idx ->
                    val cat = categories[idx]
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UzLegalTeal,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) UzLegalTeal else UzLegalBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display: Either 2-Column Illustrated Covers Grid or Editorial List
            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(filteredCodes) { index, code ->
                        CodeCoverCard(
                            code = code,
                            index = index,
                            onClick = { onSelectCode(code.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(filteredCodes) { index, code ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCode(code.id) }
                                .testTag("code_item_${code.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Number badge
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(UzLegalTealSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = UzLegalTeal
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = code.shortName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = "${code.documentNumber} • ${code.totalArticlesEstimate} modda",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = UzLegalSlateBody,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = UzLegalSlateBody,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
