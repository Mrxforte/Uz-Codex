package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.util.regex.Pattern

data class ParsedDocBlock(
    val id: String,
    val type: DocBlockType,
    val rawText: String,
    val numberStr: String = "",
    val title: String = "",
    val body: String = "",
    val note: String = ""
)

enum class DocBlockType {
    HEADER_INFO,
    CHAPTER_TITLE,
    ARTICLE_CARD,
    LEXUZ_COMMENT,
    REGULAR_PARAGRAPH
}

@Composable
fun DocumentViewerScreen(
    title: String,
    content: String,
    statutoryBasis: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    // Adaptive sidebar state: default open on tablets/wide screens, closed on compact phone portrait/landscape to maximize PDF space
    val isWideScreen = configuration.screenWidthDp >= 600
    var isSidebarOpen by remember { mutableStateOf(isWideScreen) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) } // 0.8f to 1.8f

    // Search state
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Clean raw text
    val cleanedRawContent = remember(content) {
        cleanPdfDocumentText(content)
    }

    // Parse blocks
    val parsedBlocks = remember(cleanedRawContent) {
        parseDocumentTextToBlocks(cleanedRawContent)
    }

    // Chunk blocks into pages (Simulated A4 PDF pages)
    val pages = remember(parsedBlocks) {
        val list = mutableListOf<List<ParsedDocBlock>>()
        val blocksPerPage = 4
        var i = 0
        while (i < parsedBlocks.size) {
            val end = minOf(i + blocksPerPage, parsedBlocks.size)
            list.add(parsedBlocks.subList(i, end))
            i = end
        }
        if (list.isEmpty()) listOf(parsedBlocks) else list
    }

    val totalPages = pages.size
    val listState = rememberLazyListState()

    // Determine current visible page
    val currentPage by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex + 1).coerceIn(1, totalPages)
        }
    }

    // Search matches count
    val matchCount = remember(parsedBlocks, searchQuery) {
        if (searchQuery.isBlank()) 0
        else {
            val q = searchQuery.lowercase()
            parsedBlocks.sumOf { block ->
                val fullBlockText = "${block.title} ${block.body} ${block.rawText}".lowercase()
                var count = 0
                var idx = 0
                while (idx < fullBlockText.length) {
                    val found = fullBlockText.indexOf(q, idx)
                    if (found == -1) break
                    count++
                    idx = found + q.length
                }
                count
            }
        }
    }

    // Function to print or save PDF
    fun printOrSavePdf() {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName = "UzLegal_PDF_${System.currentTimeMillis()}"

            printManager.print(
                jobName,
                object : PrintDocumentAdapter() {
                    override fun onLayout(
                        oldAttributes: PrintAttributes?,
                        newAttributes: PrintAttributes?,
                        cancellationSignal: CancellationSignal?,
                        callback: LayoutResultCallback?,
                        extras: Bundle?
                    ) {
                        if (cancellationSignal?.isCanceled == true) {
                            callback?.onLayoutCancelled()
                            return
                        }
                        val pdi = PrintDocumentInfo.Builder("${title}.pdf")
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(totalPages)
                            .build()
                        callback?.onLayoutFinished(pdi, true)
                    }

                    override fun onWrite(
                        pageRanges: Array<out PageRange>?,
                        destination: ParcelFileDescriptor?,
                        cancellationSignal: CancellationSignal?,
                        callback: WriteResultCallback?
                    ) {
                        val pdfDoc = PdfDocument()
                        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
                        val page = pdfDoc.startPage(pageInfo)

                        val canvas = page.canvas
                        val paint = Paint().apply {
                            textSize = 11f
                            color = android.graphics.Color.BLACK
                        }

                        var y = 40f
                        cleanedRawContent.split("\n").forEach { line ->
                            val trimmed = line.trim()
                            if (trimmed.isNotEmpty()) {
                                canvas.drawText(if (trimmed.length > 80) trimmed.substring(0, 80) else trimmed, 40f, y, paint)
                                y += 16f
                                if (y > 800f) return@forEach
                            }
                        }

                        pdfDoc.finishPage(page)

                        try {
                            FileOutputStream(destination?.fileDescriptor).use { out ->
                                pdfDoc.writeTo(out)
                            }
                            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                        } catch (e: Exception) {
                            callback?.onWriteFailed(e.message)
                        } finally {
                            pdfDoc.close()
                        }
                    }
                },
                null
            )
        } catch (e: Exception) {
            Toast.makeText(context, "PDF chop etishda xatolik: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF323639))
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            ) {
                // Adobe / Chrome PDF Viewer Dark Control Bar (#323639)
                Surface(
                    color = Color(0xFF323639),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left section: Back, Sidebar Toggle, Document Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Ortga", tint = Color.White)
                            }

                            IconButton(
                                onClick = { isSidebarOpen = !isSidebarOpen },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSidebarOpen) Icons.AutoMirrored.Filled.ViewSidebar else Icons.Default.Menu,
                                    contentDescription = "Menejer",
                                    tint = if (isSidebarOpen) Color(0xFF38BDF8) else Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "${title.take(18)}.pdf",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Middle section: Page Switcher & Zoom Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Page Switcher
                            Surface(
                                color = Color(0xFF212529),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFF475569))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (currentPage > 1) {
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(currentPage - 2)
                                                }
                                            }
                                        },
                                        enabled = currentPage > 1,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White)
                                    }

                                    Text(
                                        text = "$currentPage / $totalPages",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            if (currentPage < totalPages) {
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(currentPage)
                                                }
                                            }
                                        },
                                        enabled = currentPage < totalPages,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Zoom controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (zoomScale > 0.8f) zoomScale -= 0.15f },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "-", tint = Color.White)
                                }

                                Text(
                                    text = "${(zoomScale * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )

                                IconButton(
                                    onClick = { if (zoomScale < 1.8f) zoomScale += 0.15f },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "+", tint = Color.White)
                                }
                            }
                        }

                        // Right section: Search, Print, Share
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isSearchActive = !isSearchActive },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Qidiruv", tint = Color.White)
                            }

                            IconButton(
                                onClick = { printOrSavePdf() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = "Chop etish", tint = Color.White)
                            }

                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, cleanedRawContent)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "PDF Hujjatni ulashish"))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.White)
                            }
                        }
                    }
                }

                // Search Bar
                if (isSearchActive) {
                    Surface(
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("PDF matnidan izlash...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (searchQuery.isNotEmpty()) {
                                Surface(
                                    color = Color(0xFF0284C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "$matchCount mos",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Yopish", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF525659)) // Standard PDF reader grey
                .testTag("document_viewer_screen")
        ) {
            // LEFT THUMBNAIL SIDEBAR (#212529)
            AnimatedVisibility(visible = isSidebarOpen) {
                Surface(
                    color = Color(0xFF212529),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .width(88.dp)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SAHIFALAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(pages) { pageIdx, _ ->
                                val isSelected = pageIdx + 1 == currentPage

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(pageIdx)
                                        }
                                    }
                                ) {
                                    // Thumbnail Paper Miniature
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(2.dp),
                                        shadowElevation = if (isSelected) 6.dp else 2.dp,
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B)
                                        ),
                                        modifier = Modifier.size(width = 62.dp, height = 82.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                // Miniature header bar
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .background(Color(0xFF1E293B), RoundedCornerShape(1.dp))
                                                )
                                                // Miniature lines
                                                repeat(6) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.85f)
                                                            .height(2.dp)
                                                            .background(Color(0xFFCBD5E1))
                                                    )
                                                }
                                            }

                                            // Small page tag
                                            Surface(
                                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B),
                                                shape = RoundedCornerShape(2.dp),
                                                modifier = Modifier.align(Alignment.BottomEnd)
                                            ) {
                                                Text(
                                                    text = "${pageIdx + 1}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 8.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "${pageIdx + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // MAIN PDF CANVAS (A4 PAPER SHEETS ON GREY WORKSPACE)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(pages) { pageIndex, pageBlocks ->
                    // REAL A4 PAPER SHEET CARD
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(2.dp),
                        shadowElevation = 8.dp,
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth(0.98f)
                            .padding(horizontal = (4 * zoomScale).dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            // Official State Emblem Header (Printed on First Page)
                            if (pageIndex == 0) {
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
                                            fontSize = (10 * zoomScale).sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = title.uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A),
                                            fontFamily = FontFamily.Serif,
                                            fontSize = (15 * zoomScale).sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = statutoryBasis ?: "O'zbekiston Respublikasining Rasmiy Qonuni",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF475569),
                                            fontSize = (10.5 * zoomScale).sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFF0F172A), thickness = 1.5.dp)
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            } else {
                                // Sub-page minimal header line
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = (9.5 * zoomScale).sp,
                                            fontStyle = FontStyle.Italic
                                        )
                                    )
                                    Text(
                                        text = "UzLegal Rasmiy Baza",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = (9.5 * zoomScale).sp
                                        )
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))
                            }

                            // PAGE CONTENT BLOCKS
                            Column(
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                pageBlocks.forEach { block ->
                                    when (block.type) {
                                        DocBlockType.HEADER_INFO -> {
                                            Text(
                                                text = buildHighlightedText(block.title, searchQuery),
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A),
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = (14 * zoomScale).sp,
                                                    textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        DocBlockType.CHAPTER_TITLE -> {
                                            Text(
                                                text = buildHighlightedText(block.title, searchQuery),
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0369A1), // Blue section title
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = (13.5 * zoomScale).sp
                                                ),
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }

                                        DocBlockType.ARTICLE_CARD -> {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                // Article Header (Bold Blue Title)
                                                Text(
                                                    text = buildHighlightedText("${block.numberStr}. ${block.title}", searchQuery),
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E3A8A), // Dark Blue
                                                        fontFamily = FontFamily.Serif,
                                                        fontSize = (13 * zoomScale).sp
                                                    )
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                // Body text
                                                if (block.body.isNotBlank()) {
                                                    Text(
                                                        text = buildHighlightedText(block.body, searchQuery),
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color(0xFF1E293B),
                                                            fontFamily = FontFamily.Serif,
                                                            fontSize = (12 * zoomScale).sp,
                                                            lineHeight = (18 * zoomScale).sp,
                                                            textAlign = TextAlign.Justify
                                                        ),
                                                        softWrap = true
                                                    )
                                                }

                                                // Sharh / Annotation Box
                                                if (block.note.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Surface(
                                                        color = Color(0xFFF3E8FF), // Light purple surface
                                                        shape = RoundedCornerShape(4.dp),
                                                        border = BorderStroke(1.dp, Color(0xFFC084FC)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(modifier = Modifier.padding(8.dp)) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .width(3.dp)
                                                                    .height(32.dp)
                                                                    .background(Color(0xFF7C3AED), RoundedCornerShape(2.dp))
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = buildHighlightedText("Sharh: ${block.note}", searchQuery),
                                                                style = MaterialTheme.typography.bodySmall.copy(
                                                                    color = Color(0xFF6D28D9), // Purple text
                                                                    fontStyle = FontStyle.Italic,
                                                                    fontSize = (11 * zoomScale).sp,
                                                                    lineHeight = (15 * zoomScale).sp,
                                                                    textAlign = TextAlign.Justify
                                                                ),
                                                                softWrap = true
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        DocBlockType.LEXUZ_COMMENT -> {
                                            Surface(
                                                color = Color(0xFFF3E8FF),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(1.dp, Color(0xFFC084FC)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = buildHighlightedText("Sharh: ${block.rawText.removePrefix("LexUZ sharhi:").removePrefix("LexUZ sharhi").trim()}", searchQuery),
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF6D28D9),
                                                        fontStyle = FontStyle.Italic,
                                                        fontSize = (11 * zoomScale).sp,
                                                        lineHeight = (15 * zoomScale).sp,
                                                        textAlign = TextAlign.Justify
                                                    ),
                                                    softWrap = true,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }

                                        DocBlockType.REGULAR_PARAGRAPH -> {
                                            Text(
                                                text = buildHighlightedText(block.rawText, searchQuery),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF1E293B),
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = (12 * zoomScale).sp,
                                                    lineHeight = (18 * zoomScale).sp,
                                                    textAlign = TextAlign.Justify
                                                ),
                                                softWrap = true
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Page Footer
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
                                            fontSize = (9 * zoomScale).sp
                                        )
                                    )
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${pageIndex + 1} / $totalPages",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF0F172A),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (9.5 * zoomScale).sp
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
}

/**
 * Builds annotated string highlighting searchQuery matches with gold background pills.
 */
@Composable
private fun buildHighlightedText(text: String, query: String) = buildAnnotatedString {
    if (query.isBlank()) {
        append(text)
    } else {
        val q = query.lowercase()
        val textLower = text.lowercase()
        var startIndex = 0

        while (startIndex < text.length) {
            val matchIndex = textLower.indexOf(q, startIndex)
            if (matchIndex == -1) {
                append(text.substring(startIndex))
                break
            }

            if (matchIndex > startIndex) {
                append(text.substring(startIndex, matchIndex))
            }

            withStyle(
                style = SpanStyle(
                    background = LegalGold,
                    color = LegalNavyPrimary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(matchIndex, matchIndex + q.length))
            }

            startIndex = matchIndex + q.length
        }
    }
}

/**
 * Parses raw text into structured Document Blocks (Header, Chapter, Article, LexUZ Comment, Paragraph).
 */
private fun parseDocumentTextToBlocks(rawContent: String): List<ParsedDocBlock> {
    val blocks = mutableListOf<ParsedDocBlock>()
    val lines = rawContent.split("\n")

    val articleHeaderPattern = Pattern.compile("^\\s*(\\d+)-modda\\.\\s*(.*)", Pattern.CASE_INSENSITIVE)
    val chapterHeaderPattern = Pattern.compile("^\\s*((?:\\d+|[IVXLCDM]+)\\s*-\\s*(?:bob|BOB|boʻlim|BOʻLIM)\\.?[^\\n]*)", Pattern.CASE_INSENSITIVE)

    var currentBlockType: DocBlockType? = null
    var currentHeader = ""
    var currentTitle = ""
    val currentBodyLines = mutableListOf<String>()
    val currentNoteLines = mutableListOf<String>()
    var blockCount = 0

    fun flushCurrentBlock() {
        if (currentBlockType == null) return

        val bodyText = currentBodyLines.joinToString("\n").trim()
        val noteText = currentNoteLines.joinToString("\n").trim()
        val fullText = "$currentHeader $currentTitle $bodyText $noteText".trim()

        if (fullText.isNotEmpty()) {
            blocks.add(
                ParsedDocBlock(
                    id = "blk_${blockCount++}",
                    type = currentBlockType!!,
                    rawText = fullText,
                    numberStr = currentHeader,
                    title = currentTitle,
                    body = bodyText,
                    note = noteText
                )
            )
        }

        currentBlockType = null
        currentHeader = ""
        currentTitle = ""
        currentBodyLines.clear()
        currentNoteLines.clear()
    }

    var isDocMasterHeader = true

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue

        // Check for LexUZ Sharhi line
        if (trimmed.startsWith("LexUZ sharhi", ignoreCase = true) || trimmed.startsWith("Qarang:", ignoreCase = true) || trimmed.startsWith("Karang:", ignoreCase = true)) {
            if (currentBlockType == DocBlockType.ARTICLE_CARD) {
                currentNoteLines.add(trimmed.removePrefix("LexUZ sharhi:").trim())
            } else {
                flushCurrentBlock()
                currentBlockType = DocBlockType.LEXUZ_COMMENT
                currentBodyLines.add(trimmed)
                flushCurrentBlock()
            }
            continue
        }

        // Check for Chapter / Section Banner
        val chapterMatcher = chapterHeaderPattern.matcher(trimmed)
        if (chapterMatcher.find()) {
            flushCurrentBlock()
            isDocMasterHeader = false
            currentBlockType = DocBlockType.CHAPTER_TITLE
            currentTitle = trimmed
            flushCurrentBlock()
            continue
        }

        // Check for Article Header
        val articleMatcher = articleHeaderPattern.matcher(trimmed)
        if (articleMatcher.find()) {
            flushCurrentBlock()
            isDocMasterHeader = false
            currentBlockType = DocBlockType.ARTICLE_CARD
            currentHeader = "${articleMatcher.group(1)}-modda"
            currentTitle = articleMatcher.group(2) ?: ""
            continue
        }

        // Master Document Header block (Title, Document number)
        if (isDocMasterHeader && (trimmed.contains("KODEKS", ignoreCase = true) || trimmed.contains("Hujjat №", ignoreCase = true) || trimmed.contains("Qabul qilingan", ignoreCase = true) || trimmed.contains("O'ZBEKISTON", ignoreCase = true))) {
            if (currentBlockType != DocBlockType.HEADER_INFO) {
                flushCurrentBlock()
                currentBlockType = DocBlockType.HEADER_INFO
                currentTitle = trimmed
            } else {
                currentBodyLines.add(trimmed)
            }
            continue
        }

        // If line starts with parentheses (editorial/law note)
        if (trimmed.startsWith("(") && currentBlockType == DocBlockType.ARTICLE_CARD) {
            currentNoteLines.add(trimmed)
            continue
        }

        if (currentBlockType == null) {
            currentBlockType = DocBlockType.REGULAR_PARAGRAPH
            currentBodyLines.add(trimmed)
        } else {
            currentBodyLines.add(trimmed)
        }
    }

    flushCurrentBlock()

    return if (blocks.isNotEmpty()) blocks else listOf(
        ParsedDocBlock(
            id = "blk_fallback",
            type = DocBlockType.REGULAR_PARAGRAPH,
            rawText = rawContent
        )
    )
}

/**
 * Cleans PDF raw content by stripping HTML line break tags, web-scraping header/footer junk,
 * extra blank spaces, and normalizing line wrapping and soft wraps into clean paragraphs.
 */
fun cleanPdfDocumentText(content: String): String {
    if (content.isBlank()) return ""

    val text = content
        .replace(Regex("(?i)</?br\\s*/?>"), "\n")
        .replace(Regex("\\d{1,2}/\\d{1,2}/\\d{2,4},\\s*\\d{1,2}:\\d{2}\\s*(?:AM|PM)?"), "")
        .replace(Regex("\\d{2}\\.\\d{2}\\.\\d{4}\\.\\s*Oʻzbekiston\\s+Respublikasining[^\n]*"), "")
        .replace(Regex("(?m)^\\s*\\d{1,3}/\\d{1,3}\\s*$"), "")
        .replace("Қўшимча ахборот", "")
        .replace("Улашиш", "")
        .replace("", "")
        .replace("%d", "")
        .replace("b%d", "")
        .replace("\u00A0", " ")
        .replace(Regex("━{3,}"), "")
        .replace(Regex("-{5,}"), "")
        .replace(Regex("_{5,}"), "")
        .replace("\r\n", "\n")
        .replace("\r", "\n")

    val rawLines = text.split("\n").map { it.trim() }
    val resultLines = mutableListOf<String>()
    var currentParagraph = mutableListOf<String>()

    val bulletPattern = Regex("^(?:[a-zа-яA-ZА-Я0-9]{1,3}[).]|—|-|•|\\([0-9]+-[mM]odda|[0-9]+-[bB]ob|[IVX]+\\s+[bB]OʻLIM|[IVX]+-[bB]ob).*")

    for (line in rawLines) {
        if (line.isEmpty()) {
            if (currentParagraph.isNotEmpty()) {
                resultLines.add(currentParagraph.joinToString(" ").trim())
                currentParagraph = mutableListOf()
            }
        } else {
            if (currentParagraph.isNotEmpty() && currentParagraph.last().endsWith("-")) {
                val lastIdx = currentParagraph.lastIndex
                currentParagraph[lastIdx] = currentParagraph[lastIdx].dropLast(1) + line
            } else {
                if (currentParagraph.isEmpty()) {
                    currentParagraph.add(line)
                } else {
                    if (bulletPattern.matches(line)) {
                        resultLines.add(currentParagraph.joinToString(" ").trim())
                        currentParagraph = mutableListOf(line)
                    } else {
                        currentParagraph.add(line)
                    }
                }
            }
        }
    }

    if (currentParagraph.isNotEmpty()) {
        resultLines.add(currentParagraph.joinToString(" ").trim())
    }

    return resultLines
        .filter { it.isNotBlank() }
        .joinToString("\n\n") { p -> p.replace(Regex("[ \t]+"), " ").trim() }
        .trim()
}
