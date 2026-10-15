package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LegalArticle
import com.example.data.model.LegalCode
import com.example.data.repository.LegalRepository
import com.example.ui.components.CodeCoverIllustration
import com.example.ui.components.StatusBadge
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CodeDetailScreen(
    codeId: String,
    repository: LegalRepository,
    onBack: () -> Unit,
    onSelectArticle: (String) -> Unit,
    onOpenFullScreenViewer: ((String, String, String?) -> Unit)? = null
) {
    val code = remember(codeId) { repository.getCodeById(codeId) }
    val articles = remember(codeId) { repository.getArticlesForCode(codeId) }
    var articleQuery by remember { mutableStateOf("") }
    var selectedViewMode by remember { mutableStateOf(0) } // 0: ListView (Ro'yxat), 1: PDF Hujjat (PDF Viewer)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

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
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("code_detail_screen"),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = if (selectedViewMode == 0 && filteredArticles.size > 3) 28.dp else 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (code != null) {
                    // Large Illustrated Masterpiece Cover
                    item {
                        CodeCoverIllustration(
                            code = code,
                            height = 200.dp,
                            showDetailedHeader = true
                        )
                    }

                    // Official Metadata Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, UzLegalBorder),
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
                                        text = "${code.totalArticlesEstimate} modda",
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

                // View Mode Toggle Switch (Big Beautiful Icon Card Buttons)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ViewModeCardButton(
                            title = "Moddalar ro'yxati",
                            subtitle = "Interaktiv moddalar",
                            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                            isSelected = selectedViewMode == 0,
                            selectedColor = UzLegalTeal,
                            onClick = { selectedViewMode = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_list_view_button")
                        )

                        ViewModeCardButton(
                            title = "PDF Hujjat",
                            subtitle = "Rasmiy A4 ko'rinish",
                            icon = Icons.Default.PictureAsPdf,
                            isSelected = selectedViewMode == 1,
                            selectedColor = UzLegalError,
                            onClick = {
                                selectedViewMode = 1
                                if (code != null && onOpenFullScreenViewer != null) {
                                    val fullText = buildString {
                                        appendLine(code.officialUzbekName.uppercase())
                                        appendLine("Hujjat №: ${code.documentNumber} | Qabul qilingan sana: ${code.adoptionDate}")
                                        appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
                                        articles.forEach { art ->
                                            appendLine("${art.chapter}\n${art.articleNumberStr}. ${art.title}\n\n${art.content}\n")
                                        }
                                    }
                                    onOpenFullScreenViewer(code.shortName, fullText, code.officialUzbekName)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_pdf_view_button")
                        )
                    }
                }

                if (selectedViewMode == 0) {
                    // --- LIST VIEW MODE ---

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

                    // Quick Jump Range Chips for easy thumb navigation
                    if (filteredArticles.size > 5) {
                        item {
                            val chunkCount = (filteredArticles.size + 9) / 10
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(chunkCount) { chunkIdx ->
                                    val startNum = chunkIdx * 10 + 1
                                    val endNum = minOf((chunkIdx + 1) * 10, filteredArticles.size)
                                    Surface(
                                        onClick = {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(5 + chunkIdx * 10)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = UzLegalPrimary.copy(alpha = 0.08f),
                                        border = BorderStroke(1.dp, UzLegalPrimary.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "$startNum-$endNum moddalar",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = UzLegalPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
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

                    // Articles List Cards
                    items(filteredArticles) { article ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, UzLegalBorder),
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
                                    text = cleanPdfDocumentText(article.content),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = UzLegalSlateBody,
                                        lineHeight = 16.sp,
                                        textAlign = TextAlign.Justify
                                    ),
                                    softWrap = true,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    // --- PDF DOCUMENT VIEW MODE ---
                    item {
                        PdfDocumentViewerCard(
                            code = code,
                            articles = articles,
                            onSharePdf = {
                                if (code != null) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "${code.officialUzbekName}\n\nHujjat raqami: ${code.documentNumber}\nQabul qilingan sana: ${code.adoptionDate}\n\nUshbu kodeks matni UzLegal ilovasi orqali tayyorlandi."
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "PDF Hujjatni ulashish"))
                                }
                            },
                            onOpenFullScreen = {
                                if (code != null && onOpenFullScreenViewer != null) {
                                    val fullText = buildString {
                                        appendLine(code.officialUzbekName.uppercase())
                                        appendLine("Hujjat №: ${code.documentNumber} | Qabul qilingan sana: ${code.adoptionDate}")
                                        appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
                                        articles.forEach { art ->
                                            appendLine("${art.chapter}\n${art.articleNumberStr}. ${art.title}\n\n${art.content}\n")
                                        }
                                    }
                                    onOpenFullScreenViewer(code.shortName, fullText, code.officialUzbekName)
                                }
                            }
                        )
                    }
                }
            }

            // Interactive Fast Scrollbar Overlay with Drag Thumb & Active Position Bubble
            if (selectedViewMode == 0 && filteredArticles.size > 3) {
                val currentArticleLabel = remember(listState.firstVisibleItemIndex, filteredArticles) {
                    val headerOffset = 5
                    val articleIdx = (listState.firstVisibleItemIndex - headerOffset).coerceIn(0, (filteredArticles.size - 1).coerceAtLeast(0))
                    filteredArticles.getOrNull(articleIdx)?.articleNumberStr ?: ""
                }

                FastScrollbar(
                    listState = listState,
                    totalItems = filteredArticles.size + 5,
                    currentArticleText = currentArticleLabel,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp, top = 80.dp, bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
fun FastScrollbar(
    listState: LazyListState,
    totalItems: Int,
    currentArticleText: String,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }

    val firstVisible = listState.firstVisibleItemIndex
    val scrollProgress = remember(firstVisible, totalItems) {
        if (totalItems <= 1) 0f
        else (firstVisible.toFloat() / totalItems.toFloat()).coerceIn(0f, 1f)
    }

    BoxWithConstraints(
        modifier = modifier
            .width(36.dp)
            .fillMaxHeight()
    ) {
        val maxHeightDp = this.maxHeight

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(totalItems) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, _ ->
                            change.consume()
                            val y = change.position.y
                            val fraction = (y / 1000f).coerceIn(0f, 1f)
                            val targetIndex = (fraction * totalItems).toInt().coerceIn(0, (totalItems - 1).coerceAtLeast(0))
                            coroutineScope.launch {
                                listState.scrollToItem(targetIndex)
                            }
                        }
                    )
                }
        ) {
            // Track Line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .background(UzLegalBorder, RoundedCornerShape(2.dp))
            )

            // Thumb handle
            val thumbY = (scrollProgress * (maxHeightDp.value - 40f).coerceAtLeast(0f)).dp
            Box(
                modifier = Modifier
                    .offset(y = thumbY)
                    .align(Alignment.TopCenter)
                    .size(width = 24.dp, height = 36.dp)
                    .background(
                        if (isDragging) UzLegalTeal else UzLegalPrimary,
                        RoundedCornerShape(12.dp)
                    )
                    .border(1.5.dp, Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Floating Active Article Indicator Pill (Visible during drag)
            if (isDragging && currentArticleText.isNotEmpty()) {
                Surface(
                    color = UzLegalNavyHeadings,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, UzLegalAccent),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-42).dp, y = thumbY)
                ) {
                    Text(
                        text = currentArticleText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PdfDocumentViewerCard(
    code: LegalCode?,
    articles: List<LegalArticle>,
    onSharePdf: () -> Unit,
    onOpenFullScreen: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF323639)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pdf_document_sheet")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // PDF TOP TOOLBAR (#323639)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF323639))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${code?.shortName ?: "Kodeks"}_Rasmiy_A4.pdf",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "1 / 15 Sahifa • Rasmiy Hujjat",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onOpenFullScreen != null) {
                        Button(
                            onClick = onOpenFullScreen,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.OpenInFull, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("To'liq ekran", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    IconButton(onClick = onSharePdf, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.White)
                    }
                }
            }

            // PDF CANVAS WORKSPACE (#525659) WITH A4 WHITE PAPER SHEET
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF525659))
                    .padding(12.dp)
            ) {
                // REAL A4 PAPER SHEET
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(2.dp),
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Official State Emblem Header & Document Title
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(34.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "O'ZBEKISTON RESPUBLIKASI QONUNCHILIGI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = 1.2.sp,
                                    fontSize = 10.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = code?.officialUzbekName?.uppercase() ?: "KODEKS MATNI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 15.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Hujjat №: ${code?.documentNumber ?: "-"} | Qabul qilingan: ${code?.adoptionDate ?: "-"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF475569),
                                    fontSize = 10.5.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF0F172A), thickness = 1.5.dp)
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // PDF Legal Body Text
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (articles.isEmpty()) {
                                Text(
                                    text = "Ushbu kodeks bo'yicha rasmiy moddalar yuklanmoqda...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF475569),
                                        fontFamily = FontFamily.Serif
                                    )
                                )
                            } else {
                                articles.forEach { article ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = article.chapter,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF0369A1),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "${article.articleNumberStr}. ${article.title}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A8A),
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 13.sp
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = cleanPdfDocumentText(article.content),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color(0xFF1E293B),
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                textAlign = TextAlign.Justify
                                            ),
                                            softWrap = true
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = Color(0xFFF3E8FF),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, Color(0xFFC084FC)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(8.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(3.dp)
                                                        .height(28.dp)
                                                        .background(Color(0xFF7C3AED), RoundedCornerShape(2.dp))
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Sharh: Qarang: O'zbekiston Respublikasi Konstitutsiyasining va ushbu ${code?.shortName ?: "Kodeks"} moddalarining tegishli bandlariga.",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF6D28D9),
                                                        fontStyle = FontStyle.Italic,
                                                        fontSize = 10.5.sp,
                                                        lineHeight = 14.sp,
                                                        textAlign = TextAlign.Justify
                                                    ),
                                                    softWrap = true
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // PDF Footer Page Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "UzLegal Qonunchilik Bazasi",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "1 / 15 Sahifa",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewModeCardButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) selectedColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) selectedColor else UzLegalBorder
    val iconBgColor = if (isSelected) selectedColor else UzLegalSurfaceVariantLight
    val iconColor = if (isSelected) Color.White else UzLegalSlateBody

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large Icon Container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBgColor, CircleShape)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = UzLegalNavyHeadings,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) selectedColor else UzLegalSlateBody,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = selectedColor,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}
