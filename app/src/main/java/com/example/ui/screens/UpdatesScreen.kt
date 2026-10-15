package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UpdatesData
import com.example.data.model.LegislationUpdate
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun UpdatesScreen(
    repository: LegalRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var updateList by remember { mutableStateOf(repository.getRecentUpdates()) }
    var isLoadingOnline by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Barchasi") }
    val filterOptions = listOf("Barchasi", "Farmonlar", "Qarorlar", "Qonunlar", "Kodekslar")
    var selectedUpdateForDiff by remember { mutableStateOf<LegislationUpdate?>(null) }

    val filteredUpdates = remember(updateList, selectedFilter, searchQuery) {
        updateList.filter { upd ->
            val matchesCategory = when (selectedFilter) {
                "Farmonlar" -> upd.codeName.contains("Farmon", ignoreCase = true) || upd.articleNumber.contains("PF", ignoreCase = true)
                "Qarorlar" -> upd.codeName.contains("Qaror", ignoreCase = true) || upd.articleNumber.contains("PQ", ignoreCase = true)
                "Qonunlar" -> upd.amendingLaw.contains("Qonun", ignoreCase = true) || upd.codeName.contains("Qonun", ignoreCase = true)
                "Kodekslar" -> upd.codeName.contains("kodeks", ignoreCase = true)
                else -> true
            }

            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                    upd.codeName.lowercase().contains(q) ||
                    upd.amendingLaw.lowercase().contains(q) ||
                    upd.summaryOfChange.lowercase().contains(q) ||
                    upd.articleNumber.lowercase().contains(q)

            matchesCategory && matchesSearch
        }
    }

    fun refreshOnlineUpdates() {
        coroutineScope.launch {
            isLoadingOnline = true
            Toast.makeText(context, "Online API orqali so'nggi Farmonlar va Qonunlar yuklanmoqda...", Toast.LENGTH_SHORT).show()
            val result = UpdatesData.fetchLiveDecreesAndUpdates()
            updateList = result
            isLoadingOnline = false
            Toast.makeText(context, "Yangilandi! ${result.size} ta hujjat va farmonlar mavjud", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "Farmonlar va Yangiliklar",
                subtitle = "O'zbekiston Respublikasi Prezidenti Farmonlari va Qonunchilik o'zgarishlari",
                showBackButton = true,
                onBackClick = {
                    if (selectedUpdateForDiff != null) selectedUpdateForDiff = null
                    else onBack()
                },
                actions = {
                    IconButton(
                        onClick = { refreshOnlineUpdates() },
                        enabled = !isLoadingOnline
                    ) {
                        if (isLoadingOnline) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Online Yangilash", tint = Color.White)
                        }
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
                .testTag("updates_screen")
        ) {
            if (selectedUpdateForDiff == null) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Farmon, qaror yoki qonun nomini qidiring...", fontSize = 13.sp, color = UzLegalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UzLegalPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UzLegalPrimary,
                        unfocusedBorderColor = UzLegalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
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

                // Updates / Decrees List
                if (filteredUpdates.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kiritilgan so'rov bo'yicha farmon yoki qonun topilmadi.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = UzLegalTextSecondary)
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredUpdates) { upd ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, UzLegalBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedUpdateForDiff = upd }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = upd.codeName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = UzLegalPrimary
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = UzLegalPrimary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = upd.articleNumber,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = UzLegalPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = upd.amendingLaw,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = upd.summaryOfChange,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = UzLegalTextSecondary,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Event,
                                                contentDescription = null,
                                                tint = UzLegalSuccess,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Kuchga kirish: ${upd.effectiveDate}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = UzLegalSuccess,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
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
                }
            } else {
                // Diff / Full Detail View
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
                            border = BorderStroke(1.dp, UzLegalBorder),
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
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Qabul qilindi: ${upd.adoptedDate}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = UzLegalTextSecondary)
                                    )
                                    Text(
                                        text = "Kuchga kirish: ${upd.effectiveDate}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = UzLegalSuccess,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Xulosa va asosiy tartib:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = upd.summaryOfChange,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
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
                                    text = "OLDINGI TAHRIR / TARTIB",
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
                                    text = "YANGI TAHRIR / TARTIB",
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
                }
            }
        }
    }
}
