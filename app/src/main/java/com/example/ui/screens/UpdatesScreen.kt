package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LegislationUpdate
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*

@Composable
fun UpdatesScreen(
    repository: LegalRepository,
    onBack: () -> Unit
) {
    val updates = remember { repository.getRecentUpdates() }
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("Barchasi") }
    val filterOptions = listOf("Barchasi", "Qonunlar", "Kodekslar", "Farmonlar")
    var selectedUpdateForDiff by remember { mutableStateOf<LegislationUpdate?>(null) }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "Yangiliklar",
                subtitle = "Qonunchilikdagi o'zgarishlardan xabardor bo'ling",
                showBackButton = true,
                onBackClick = {
                    if (selectedUpdateForDiff != null) selectedUpdateForDiff = null
                    else onBack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("updates_screen")
        ) {
            if (selectedUpdateForDiff == null) {
                // Filter Chips matching mockup: Barchasi, Qonunlar, Kodekslar, Farmonlar
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { opt ->
                        val isSelected = opt == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = opt },
                            label = { Text(opt, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UzLegalPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // News Feed List matching mockup
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(updates) { upd ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedUpdateForDiff = upd }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = upd.codeName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = upd.summaryOfChange,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = UzLegalTextSecondary,
                                            fontSize = 12.sp
                                        ),
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = upd.effectiveDate,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = UzLegalPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
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
            } else {
                // Diff View
                val upd = selectedUpdateForDiff!!
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = upd.codeName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = UzLegalPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = upd.amendingLaw,
                                    style = MaterialTheme.typography.bodySmall.copy(color = UzLegalTextSecondary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Kuchga kirish sanasi: ${upd.effectiveDate}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = UzLegalSuccess,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Matn tahriri taqqoslashi (Diff):",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    item {
                        Surface(
                            color = UzLegalErrorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "OLDINGI TAHRIR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = UzLegalError
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = upd.oldWording, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    item {
                        Surface(
                            color = UzLegalSuccessContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "YANGI TAHRIR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = UzLegalSuccess
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = upd.newWording, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upd.sourceUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UzLegalPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lex.uz rasmiy manbasida o'qish")
                        }
                    }
                }
            }
        }
    }
}
