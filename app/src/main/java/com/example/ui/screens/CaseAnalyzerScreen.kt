package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LegalCaseScenario
import com.example.data.repository.LegalRepository
import com.example.ui.components.InteractiveVisualCaseMap
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CaseAnalyzerScreen(
    repository: LegalRepository,
    initialScenarioId: String? = null,
    onBack: () -> Unit,
    onSelectArticle: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scenarios = remember { repository.getEducationalScenarios() }

    var activeScenario by remember {
        mutableStateOf(
            if (initialScenarioId != null) repository.getScenarioById(initialScenarioId)
            else scenarios.find { it.id == "salary_delay_scenario" } ?: scenarios.firstOrNull()
        )
    }

    var showAnalysisResult by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "Vaziyat tahlili",
                subtitle = "Haqiqiy holatni tahlil qiling",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("case_analyzer_screen")
        ) {
            // Case Selector Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scenarios.forEach { sc ->
                        val isSelected = activeScenario?.id == sc.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeScenario = sc },
                            label = { Text(if (sc.id.contains("salary")) "Mehnat nizosi" else "Jinoiy vaziyat", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UzLegalPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            val currentCase = activeScenario
            if (currentCase != null) {
                // Card matching mockup: "Vaziyat: Xodim ishdan bo'shatildi"
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Vaziyat: ${currentCase.title}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentCase.narrative,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = UzLegalTextSecondary,
                                    lineHeight = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Blue Button: Tahlil qilish
                            Button(
                                onClick = {
                                    showAnalysisResult = true
                                    Toast.makeText(context, "Qonuniy tahlil yangilandi", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = UzLegalPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_run_case_analysis")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tahlil qilish", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (showAnalysisResult) {
                    // Section matching mockup: "Natija: Ushbu vaziyat quyidagi kodekslar asosida tahlil qilinadi:"
                    item {
                        Column {
                            Text(
                                text = "Natija",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ushbu vaziyat quyidagi kodekslar asosida tahlil qilinadi:",
                                style = MaterialTheme.typography.bodySmall.copy(color = UzLegalTextSecondary)
                            )
                        }
                    }

                    // Relevant Codes Cards matching mockup
                    items(currentCase.legalQualifications) { qual ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(UzLegalPrimary.copy(alpha = 0.1f)),
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
                                        text = qual.codeName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = qual.articleCitation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = UzLegalPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = qual.statutoryLanguage,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = UzLegalTextSecondary,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }

                    // Section: Tahlil natijasi
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Tahlil natijasi",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                currentCase.proceduralSafeguards.forEach { sg ->
                                    Text(
                                        text = "• $sg",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = UzLegalTextSecondary,
                                            lineHeight = 18.sp
                                        ),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = currentCase.urgentLegalDisclaimer,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = UzLegalPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Visual Map
                    item {
                        InteractiveVisualCaseMap(
                            nodes = currentCase.visualNodes,
                            edges = currentCase.visualEdges,
                            onNodeClick = { node ->
                                Toast.makeText(context, "${node.label}: ${node.description}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Save Button
                    item {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.saveCaseAnalysis(
                                        id = currentCase.id + "_" + System.currentTimeMillis(),
                                        title = currentCase.title,
                                        category = currentCase.category,
                                        summary = currentCase.shortDescription,
                                        narrative = currentCase.narrative,
                                        qualification = currentCase.legalQualifications.firstOrNull()?.articleCitation ?: "Tahlil"
                                    )
                                    repository.addXp(30)
                                    Toast.makeText(context, "Tahlil saqlandi (+30 XP)", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UzLegalPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tahlilni Sevimlilarga saqlash (+30 XP)")
                        }
                    }
                }
            }
        }
    }
}
