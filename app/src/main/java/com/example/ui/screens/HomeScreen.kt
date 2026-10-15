package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UpdatesData
import com.example.data.model.UserLevel
import com.example.data.repository.LegalRepository
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.ui.components.CodeCoverCard
import com.example.ui.components.UzLegalTopBar
import com.example.ui.components.XPLevelBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: LegalRepository,
    onNavigate: (String) -> Unit,
    onOpenScenario: (String) -> Unit
) {
    val context = LocalContext.current
    val userProfile by repository.getUserProfile().collectAsState(initial = null)
    val xp = userProfile?.xp ?: 120
    val streak = userProfile?.streakDays ?: 3
    val currentLevel = remember(xp) { UserLevel.getLevelForXp(xp) }

    var isFavoriteTodayNews by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                showLogo = true,
                actions = {
                    IconButton(
                        onClick = { onNavigate("updates") },
                        modifier = Modifier.testTag("action_notifications")
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Bildirishnomalar",
                                tint = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(UzLegalError)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onNavigate("profile") },
                        modifier = Modifier.testTag("action_profile")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(UzLegalPrimary.copy(alpha = 0.3f))
                                .border(1.dp, UzLegalAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
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
                .testTag("home_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar matching mockup: "Qonunlar, kodekslar, maqolalar qidirish..."
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("search") }
                        .testTag("home_search_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = UzLegalTextSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Qonunlar, kodekslar, maqolalar qidirish...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UzLegalTextSecondary,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            // Hero Card: "Bugungi yangilik" - Suv kodeksi
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("updates") }
                        .testTag("hero_today_news")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF0F2B5C), Color(0xFF1E3A8A), Color(0xFF0284C7))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Bugungi yangilik",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { isFavoriteTodayNews = !isFavoriteTodayNews },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavoriteTodayNews) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Sevimlilar",
                                        tint = if (isFavoriteTodayNews) Color(0xFFEF4444) else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Suv kodeksi",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "2025-yil 1-oktyabrdan kuchga kirdi",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = UzLegalAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // 4 Main Service Tiles: Kodekslar, Qonunlar, Farmonlar, Hujjatlar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ServiceTile(
                        label = "Kodekslar",
                        icon = Icons.Default.MenuBook,
                        iconBg = Color(0xFFDBEAFE),
                        iconTint = UzLegalPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("legislation") }
                    )
                    ServiceTile(
                        label = "Qonunlar",
                        icon = Icons.Default.Article,
                        iconBg = Color(0xFFE0F2FE),
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("search") }
                    )
                    ServiceTile(
                        label = "Farmonlar",
                        icon = Icons.Default.Gavel,
                        iconBg = Color(0xFFF3E8FF),
                        iconTint = Color(0xFF7E22CE),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("updates") }
                    )
                    ServiceTile(
                        label = "Hujjatlar",
                        icon = Icons.Default.Description,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("document_generator") }
                    )
                }
            }

            // 19 ta Qonun Kodeksi Carousel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "19 ta Qonun Kodeksi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings
                        )
                    )
                    Text(
                        text = "Barchasi (19) >",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UzLegalTeal,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { onNavigate("legislation") }
                            .padding(4.dp)
                    )
                }
            }

            item {
                val featuredCodes = remember { repository.getAllCodes().take(6) }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(featuredCodes) { index, code ->
                        Box(modifier = Modifier.width(160.dp)) {
                            CodeCoverCard(
                                code = code,
                                index = index,
                                onClick = { onNavigate("legislation") }
                            )
                        }
                    }
                }
            }

            // O'yin va o'qish Section (Tests, Wins/Streak, Ratings, Achievements)
            item {
                Text(
                    text = "O'yin va o'qish",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlayLearnTile(
                        label = "Testlar",
                        icon = Icons.Default.Quiz,
                        iconBg = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("quiz") }
                    )
                    PlayLearnTile(
                        label = "G'alaba",
                        icon = Icons.Default.LocalFireDepartment,
                        iconBg = Color(0xFFEA580C),
                        badgeText = "$streak kun",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("academy") }
                    )
                    PlayLearnTile(
                        label = "Reyting",
                        icon = Icons.Default.Leaderboard,
                        iconBg = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("academy") }
                    )
                    PlayLearnTile(
                        label = "Yutuqlar",
                        icon = Icons.Default.EmojiEvents,
                        iconBg = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("academy") }
                    )
                }
            }

            // Core feature: Vaziyat Tahlili Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, UzLegalPrimary.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("case_analyzer") }
                        .testTag("home_case_analyzer_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(UzLegalPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Vaziyat tahlili (Keys)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Haqiqiy yoki o'quv vaziyatlarini qonunlar bo'yicha tahlil qiling",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = UzLegalTextSecondary
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = UzLegalPrimary
                        )
                    }
                }
            }

            // So'nggi yangiliklar (Latest News list)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "So'nggi yangiliklar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Barchasi >",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UzLegalPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigate("updates") }
                    )
                }
            }

            item {
                NewsCardItem(
                    date = "2025-10-28",
                    title = "O'zbekistonda yangi qonun qabul qilindi",
                    subtitle = "Axborot xavfsizligi va raqamli iqtisodiyot kafolatlari to'g'risida",
                    onClick = { onNavigate("updates") }
                )
            }

            item {
                NewsCardItem(
                    date = "2025-10-26",
                    title = "Mehnat kodeksiga o'zgartirishlar",
                    subtitle = "Ish haqi kechiktirilganda majburiy kompensatsiya foizlari kiritildi",
                    onClick = { onNavigate("updates") }
                )
            }
        }
    }
}

@Composable
fun ServiceTile(
    label: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PlayLearnTile(
    label: String,
    icon: ImageVector,
    iconBg: Color,
    badgeText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = iconBg),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = badgeText ?: label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NewsCardItem(
    date: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UzLegalTextSecondary,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = UzLegalPrimary,
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
