package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ArticleDetailScreen(
    articleId: String,
    repository: LegalRepository,
    onBack: () -> Unit,
    onSelectRelatedArticle: (String) -> Unit
) {
    val article = remember(articleId) { repository.getArticleById(articleId) }
    val code = remember(article) { article?.let { repository.getCodeById(it.codeId) } }
    val isBookmarked by repository.isArticleBookmarked(articleId).collectAsState(initial = false)
    val userNotes by repository.getNotesForArticle(articleId).collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var newNoteText by remember { mutableStateOf("") }
    var isAddingNote by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = article?.articleNumberStr ?: "Modda",
                subtitle = code?.shortName,
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            if (article != null && code != null) {
                                coroutineScope.launch {
                                    repository.toggleBookmark(article, code.shortName)
                                    val msg = if (!isBookmarked) "Xatcho'plarga saqlandi" else "Xatcho'plardan o'chirildi"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.testTag("action_toggle_bookmark")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Xatcho'p",
                            tint = if (isBookmarked) LegalGold else Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            if (article != null) {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${article.articleNumberStr}: ${article.title}\n\n${article.content}\n\nManba: ${article.lexUzUrl}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Moddani ulashish")
                                context.startActivity(shareIntent)
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Ulashish", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (article == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Modda topilmadi")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("article_detail_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LegalLightBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(statusText = article.status)
                                Text(
                                    text = article.effectiveDate,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = article.chapter,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LegalNavyPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${article.articleNumberStr}. ${article.title}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                // Full Text Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LegalLightBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "RASMIY MATN (LEX.UZ)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LegalGoldDark,
                                        letterSpacing = 1.sp
                                    )
                                )

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Legal Article", article.content)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Matn nusxalandi", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Nusxalash",
                                        tint = LegalNavyPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Text(
                                text = article.content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 26.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.lexUzUrl))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LegalNavyPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Lex.uz rasmiy manbasida ko'rish")
                            }
                        }
                    }
                }

                // Cross references / Related articles
                if (article.relatedArticleIds.isNotEmpty()) {
                    item {
                        Text(
                            text = "Aloqador va bog'liq moddalar",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    items(article.relatedArticleIds) { relId ->
                        val relArt = remember(relId) { repository.getArticleById(relId) }
                        if (relArt != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LegalLightBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectRelatedArticle(relArt.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = LegalNavyPrimary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${relArt.articleNumberStr}: ${relArt.title}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = LegalNavyPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // User Notes Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Shaxsiy yuridik qaydlar (${userNotes.size})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        TextButton(
                            onClick = { isAddingNote = !isAddingNote },
                            modifier = Modifier.testTag("btn_add_note")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAddingNote) "Bekor qilish" else "Qayd yozish")
                        }
                    }
                }

                if (isAddingNote) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LegalLightBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                OutlinedTextField(
                                    value = newNoteText,
                                    onValueChange = { newNoteText = it },
                                    label = { Text("Ushbu modda bo'yicha fikringiz yoki amaliyot qaydlari") },
                                    minLines = 3,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_note_content")
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (newNoteText.isNotBlank()) {
                                            coroutineScope.launch {
                                                repository.addNote(article.id, article.title, newNoteText.trim())
                                                newNoteText = ""
                                                isAddingNote = false
                                                Toast.makeText(context, "Qayd saqlandi", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LegalNavyPrimary),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .testTag("btn_save_note")
                                ) {
                                    Text("Saqlash")
                                }
                            }
                        }
                    }
                }

                items(userNotes) { note ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LegalGoldLight.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.deleteNote(note)
                                        Toast.makeText(context, "Qayd o'chirildi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "O'chirish", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
