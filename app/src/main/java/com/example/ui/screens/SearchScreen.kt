package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LegalArticle
import com.example.data.model.LegalCode
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*

sealed class SearchResultItem {
    data class CodeItem(val code: LegalCode) : SearchResultItem()
    data class ArticleItem(val article: LegalArticle) : SearchResultItem()
}

@Composable
fun SearchScreen(
    repository: LegalRepository,
    onBack: () -> Unit,
    onSelectCode: (String) -> Unit = {},
    onSelectArticle: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Barchasi") }
    val filterOptions = listOf("Barchasi", "Kodekslar", "Qonunlar", "Farmonlar")

    val allCodes = remember { repository.getAllCodes() }

    val searchResults = remember(query, selectedFilter) {
        val q = query.trim().lowercase()
        val results = mutableListOf<SearchResultItem>()

        if (q.isEmpty()) {
            // Default initial results: show featured codes and articles
            allCodes.take(4).forEach { results.add(SearchResultItem.CodeItem(it)) }
            repository.searchArticles("").take(6).forEach { results.add(SearchResultItem.ArticleItem(it)) }
        } else {
            // 1. Search matching codes
            val matchingCodes = allCodes.filter {
                it.shortName.lowercase().contains(q) ||
                it.officialUzbekName.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.documentNumber.lowercase().contains(q)
            }

            if (selectedFilter == "Barchasi" || selectedFilter == "Kodekslar") {
                matchingCodes.forEach { results.add(SearchResultItem.CodeItem(it)) }
            }

            // 2. Search matching articles
            val matchingArticles = repository.searchArticles(q)
            matchingArticles.forEach { results.add(SearchResultItem.ArticleItem(it)) }
        }

        // Apply filter chips
        when (selectedFilter) {
            "Kodekslar" -> results.filter { it is SearchResultItem.CodeItem || (it is SearchResultItem.ArticleItem && repository.getCodeById(it.article.codeId) != null) }
            "Qonunlar", "Farmonlar" -> results.filter { it is SearchResultItem.ArticleItem }
            else -> results
        }
    }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "Qidiruv",
                subtitle = "Tez va qulay qidiruv",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("search_screen")
        ) {
            // Search Input
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Qonunlar, kodekslar, moddalar bo'yicha qidirish...", fontSize = 13.sp, color = UzLegalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UzLegalTextSecondary) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Tozalash")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UzLegalPrimary,
                        unfocusedBorderColor = UzLegalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input")
                )
            }

            // Filter Chips: Barchasi, Kodekslar, Qonunlar, Farmonlar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions.size) { idx ->
                    val opt = filterOptions[idx]
                    val isSelected = opt == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = opt },
                        label = { Text(opt, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UzLegalPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) UzLegalPrimary else UzLegalBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Results List
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = UzLegalTextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "'$query' bo'yicha hech narsa topilmadi",
                            style = MaterialTheme.typography.bodyMedium.copy(color = UzLegalTextSecondary),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { item ->
                        when (item) {
                            is SearchResultItem.CodeItem -> {
                                val code = item.code
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, UzLegalBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectCode(code.id) }
                                        .testTag("search_code_${code.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(UzLegalPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Gavel,
                                                contentDescription = null,
                                                tint = UzLegalPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = code.shortName,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                                Surface(
                                                    color = UzLegalPrimary.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "Kodeks",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = UzLegalPrimary,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = code.description,
                                                style = MaterialTheme.typography.bodySmall.copy(color = UzLegalTextSecondary),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${code.totalArticlesEstimate} modda | ${code.documentNumber}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = UzLegalTextSecondary, fontSize = 10.sp)
                                            )
                                        }
                                    }
                                }
                            }

                            is SearchResultItem.ArticleItem -> {
                                val article = item.article
                                val code = remember(article) { repository.getCodeById(article.codeId) }
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, UzLegalBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectArticle(article.id) }
                                        .testTag("search_result_${article.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(UzLegalTealSurface, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = UzLegalTealDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = code?.shortName ?: "Modda",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                                Surface(
                                                    color = UzLegalTealSurface,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "Modda",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = UzLegalTealDark,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${article.articleNumberStr}: ${article.title}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = cleanPdfDocumentText(article.content),
                                                style = MaterialTheme.typography.bodySmall.copy(color = UzLegalTextSecondary),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
