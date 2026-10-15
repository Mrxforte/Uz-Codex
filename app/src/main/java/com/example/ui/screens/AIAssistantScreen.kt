package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.ChatMessage
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

private const val WELCOME_TEXT = "Assalomu alaykum! Men UzLegal yuridik yordamchisiman. Sizga vaziyatingizni tushunib olish va tegishli qonunlarni topishda yordam beraman. Marhamat, nima masala bo'yicha yordam kerak?"

@Composable
fun AIAssistantScreen(
    repository: LegalRepository,
    initialPrompt: String? = null,
    onBack: () -> Unit,
    onSelectArticle: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "welcome",
                    sender = "assistant",
                    text = WELCOME_TEXT,
                    isGrounded = false
                )
            )
        )
    }

    var inputText by remember { mutableStateOf(initialPrompt ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val quickReplyChips = listOf(
        "💼 Ish va mehnat",
        "💍 Oila va nikoh",
        "🏠 Uy-joy va ijara",
        "📜 Qarz va shartnoma",
        "🏛 Meros",
        "🚗 Yo'l harakati va jarimalar",
        "⚖️ Jinoyat yoki huquqbuzarlik",
        "🏢 Davlat organlari bilan muammo",
        "❓ Boshqa masala"
    )

    fun sendMessage(text: String) {
        val userQuery = text.trim()
        if (userQuery.isBlank() || isLoading) return

        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = userQuery
        )
        messages = messages + userMsg
        inputText = ""
        isLoading = true

        coroutineScope.launch {
            try {
                val assistantMsg = repository.sendAIAssistantMessage(userQuery, messages)
                messages = messages + assistantMsg
                listState.animateScrollToItem(messages.size - 1)
            } catch (e: Exception) {
                messages = messages + ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = "assistant",
                    text = "Xatolik yuz berdi: ${e.message}"
                )
            } finally {
                isLoading = false
            }
        }
    }

    fun onToggleLike(messageId: String, isLike: Boolean) {
        messages = messages.map { msg ->
            if (msg.id == messageId) {
                val newState = if (msg.likedState == isLike) null else isLike
                val toastText = when (newState) {
                    true -> "Javob sizga ma'qul kelganidan xursandmiz! 👍"
                    false -> "Fikringiz qabul qilindi. AI maslahat sifatini oshirishda foydalanamiz! 👎"
                    null -> "Baho bekor qilindi"
                }
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                msg.copy(likedState = newState)
            } else {
                msg
            }
        }
    }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "UzLegal AI",
                subtitle = "🟢 Onlayn • Professional Yuridik Yordamchi",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            messages = listOf(
                                ChatMessage(
                                    id = "welcome",
                                    sender = "assistant",
                                    text = WELCOME_TEXT,
                                    isGrounded = false
                                )
                            )
                            Toast.makeText(context, "Yangi suhbat boshlandi", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Yangi suhbat", tint = Color.White)
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
                .testTag("ai_assistant_screen")
        ) {
            // Chat message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        onArticleClick = onSelectArticle,
                        onToggleLike = { isLike -> onToggleLike(message.id, isLike) }
                    )
                }

                // Quick Reply Chips
                if (messages.size <= 2) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "💡 Tezkor mavzu tanlang:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = UzLegalTextSecondary
                                ),
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(quickReplyChips) { chipText ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(20.dp),
                                        border = BorderStroke(1.dp, UzLegalBorder),
                                        modifier = Modifier.clickable {
                                            val prompt = when (chipText) {
                                                "💼 Ish va mehnat" -> "Ish joyim va mehnat shartnomam bo'yicha maslahat kerak."
                                                "💍 Oila va nikoh" -> "Oila, nikoh yoki aliment masalasi bo'yicha yordam bering."
                                                "🏠 Uy-joy va ijara" -> "Kvartira ijarasi shartnomasi bo'yicha maslahat so'ramoqchiman."
                                                "📜 Qarz va shartnoma" -> "Qarz va shartnoma majburiyatlari bo'yicha huquqiy yordam kerak."
                                                "🏛 Meros" -> "Meros bo'lish va merosxo'rlik huquqi bo'yicha maslahat bering."
                                                "🚗 Yo'l harakati va jarimalar" -> "Yo'l harakati qoidalari va jarimalar bo'yicha savolim bor."
                                                "⚖️ Jinoyat yoki huquqbuzarlik" -> "Jinoyat yoki ma'muriy huquqbuzarlik bo'yicha maslahat kerak."
                                                "🏢 Davlat organlari bilan muammo" -> "Davlat organlari va mansabdor shaxslarga murojaat bo'yicha yordam bering."
                                                else -> "Aynan biror huquqiy masala bo'yicha maslahat so'ramoqchiman."
                                            }
                                            sendMessage(prompt)
                                        }
                                    ) {
                                        Text(
                                            text = chipText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = UzLegalPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(UzLegalPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = UzLegalPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "UzLegal AI javob tayyorlamoqda...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = UzLegalTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.5.dp, UzLegalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Vaziyatingizni yozing...", fontSize = 13.sp, color = UzLegalTextSecondary) },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UzLegalPrimary,
                            unfocusedBorderColor = UzLegalBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { sendMessage(inputText) },
                        enabled = inputText.isNotBlank() && !isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = UzLegalPrimary,
                            disabledContainerColor = UzLegalPrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("ai_chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Yuborish",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getFullCodeTitle(codeId: String): String {
    return when (codeId.lowercase()) {
        "fk" -> "Fuqarolik kodeksi"
        "jk" -> "Jinoyat kodeksi"
        "mk" -> "Mehnat kodeksi"
        "jpk" -> "Jinoyat-protsessual kodeksi"
        "fpk" -> "Fuqarolik protsessual kodeksi"
        "ok" -> "Oila kodeksi"
        "sk" -> "Soliq kodeksi"
        "yk" -> "Uy-joy kodeksi"
        "mjoek", "mjtk" -> "Ma'muriy javobgarlik to'g'risidagi kodeks"
        else -> "O'zbekiston Respublikasi Kodeksi"
    }
}

@Composable
fun FormattedLegalText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val annotatedString = remember(text) {
        buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { index, line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("**") && trimmed.endsWith("**") && trimmed.length > 4) {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = UzLegalPrimary
                        )
                    ) {
                        append(trimmed.removeSurrounding("**"))
                    }
                } else {
                    val parts = line.split("**")
                    parts.forEachIndexed { pIdx, part ->
                        if (pIdx % 2 == 1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(part)
                            }
                        } else {
                            append(part)
                        }
                    }
                }
                if (index < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = color,
            lineHeight = 22.sp
        ),
        modifier = modifier
    )
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onArticleClick: (String) -> Unit,
    onToggleLike: (Boolean) -> Unit
) {
    val isUser = message.sender == "user"
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            Card(
                colors = CardDefaults.cardColors(containerColor = UzLegalPrimary),
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 4.dp
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.widthIn(max = 310.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, UzLegalBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Avatar and AI badge header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(UzLegalPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "UzLegal AI Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "UzLegal AI",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(UzLegalSuccess)
                        )
                    }

                    // Main response text with rich markdown formatting
                    FormattedLegalText(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Cited Legal Articles
                    if (message.citedArticles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = UzLegalBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📜 Rasmiy huquqiy asoslar:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        message.citedArticles.forEach { art ->
                            val fullCodeTitle = getFullCodeTitle(art.codeId)
                            Surface(
                                color = UzLegalPrimary.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(0.5.dp, UzLegalPrimary.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onArticleClick(art.id) }
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = UzLegalPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${art.articleNumberStr}: ${art.title}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = UzLegalPrimary
                                            )
                                        )
                                        Text(
                                            text = fullCodeTitle,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = UzLegalTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action Bar for Assistant Messages: Like, Dislike, Copy, Share
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = UzLegalBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleLike(true) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (message.likedState == true) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = "Ma'qul keldi",
                                    tint = if (message.likedState == true) UzLegalPrimary else UzLegalTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { onToggleLike(false) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (message.likedState == false) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                    contentDescription = "Ma'qul kelmadi",
                                    tint = if (message.likedState == false) Color.Red else UzLegalTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (message.likedState != null) {
                                Text(
                                    text = if (message.likedState == true) "👍 Ma'qul" else "👎 Bildirildi",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (message.likedState == true) UzLegalPrimary else Color.Red
                                    ),
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                            }

                            // Copy button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("AI Legal Answer", message.text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Nusxalandi", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Nusxalash",
                                    tint = UzLegalTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Share button
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "${message.text}\n\n— UzLegal AI yuridik yordamchisi")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Maslahatni ulashish"))
                                },
                                modifier = Modifier.size(28.dp)
                                ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Ulashish",
                                    tint = UzLegalTextSecondary,
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
