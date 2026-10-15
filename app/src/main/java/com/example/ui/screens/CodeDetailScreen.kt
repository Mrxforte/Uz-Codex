package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LegalArticle
import com.example.data.repository.LegalRepository
import com.example.ui.components.CodeCoverIllustration
import com.example.ui.components.StatusBadge
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*

@Composable
fun CodeDetailScreen(
    codeId: String,
    repository: LegalRepository,
    onBack: () -> Unit,
    onSelectArticle: (String) -> Unit
) {
    val code = remember(codeId) { repository.getCodeById(codeId) }
    val articles = remember(codeId) { repository.getArticlesForCode(codeId) }
    var articleQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredArticles = remember(articles, articleQuery) {
        if (articleQuery.isBlank()) articles
        else articles.filter {
            it.title.contains(articleQuery, ignoreCase = true) ||
                    it.articleNumberStr.contains(articleQuery, ignoreCase = true) ||
                    it.content.contains(articleQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = code?.shortName ?: "Kodeks tafsiloti",
                subtitle = code?.documentNumber,
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    if (code != null) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(code.lexUzUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Lex.uz", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("code_detail_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (code != null) {
                // Large Illustrated Masterpiece Cover
                item {
                    CodeCoverIllustration(
                        code = code,
                        height = 220.dp,
                        showDetailedHeader = true
                    )
                }

                // Official Metadata Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(statusText = code.status)
                                Text(
                                    text = "Taxminan ${code.totalArticlesEstimate} modda",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = UzLegalSlateBody,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = code.officialUzbekName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = UzLegalNavyHeadings
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = code.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = UzLegalSlateBody,
                                    lineHeight = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = UzLegalSurfaceVariantLight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = "Qabul qilingan", style = MaterialTheme.typography.labelSmall.copy(color = UzLegalSlateBody, fontSize = 10.sp))
                                        Text(text = code.adoptionDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = UzLegalNavyHeadings))
                                    }
                                }

                                Surface(
                                    color = UzLegalSurfaceVariantLight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = "Kuchga kirgan", style = MaterialTheme.typography.labelSmall.copy(color = UzLegalSlateBody, fontSize = 10.sp))
                                        Text(text = code.effectiveDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = UzLegalNavyHeadings))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Article Filter Search
            item {
                OutlinedTextField(
                    value = articleQuery,
                    onValueChange = { articleQuery = it },
                    placeholder = { Text("Modda raqami yoki matn bo'yicha qidirish...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UzLegalSlateBody) },
                    trailingIcon = {
                        if (articleQuery.isNotEmpty()) {
                            IconButton(onClick = { articleQuery = "" }) {
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
                        .testTag("search_articles_input")
                )
            }

            // Articles Header
            item {
                Text(
                    text = "Asosiy moddalar (${filteredArticles.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = UzLegalNavyHeadings
                    )
                )
            }

            // Articles List
            items(filteredArticles) { article ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectArticle(article.id) }
                        .testTag("article_item_${article.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = UzLegalTealSurface,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = article.articleNumberStr,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = UzLegalTealDark,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = UzLegalSlateBody,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = article.content,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UzLegalSlateBody,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
