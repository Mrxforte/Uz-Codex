package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.ChatMessage
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

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
                    text = "Assalomu alaykum! 👋\n\nMen sizga O'zbekiston qonunchiligi bo'yicha yordam beraman. Savollaringizni yozing yoki quyidagi misollardan birini tanlang.",
                    isGrounded = true
                )
            )
        )
    }

    var inputText by remember { mutableStateOf(initialPrompt ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val presetPrompts = listOf(
        Pair("Qonunlar haqida savol", Icons.Default.Language),
        Pair("Hujjat tahlili", Icons.Default.Description),
        Pair("Amaliy maslahat", Icons.Default.Lightbulb),
        Pair("Maqola izlash", Icons.Default.Search)
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

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "AI Yordamchi",
                subtitle = "Google Gemini bilan aqlli yordam",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            messages = listOf(
                                ChatMessage(
                                    id = "welcome",
                                    sender = "assistant",
                                    text = "Assalomu alaykum! 👋\n\nMen sizga O'zbekiston qonunchiligi bo'yicha yordam beraman. Savollaringizni yozing yoki quyidagi misollardan birini tanlang."
                                )
                            )
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Tozalash", tint = Color.White)
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(
                        message = message,
                        onArticleClick = onSelectArticle
                    )
                }

                // 4 Prompt Buttons matching mockup
                if (messages.size <= 2) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            presetPrompts.forEach { (label, icon) ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val query = when (label) {
                                                "Qonunlar haqida savol" -> "O'zbekiston qonunchiligi bo'yicha qanday huquqiy kafolatlar mavjud?"
                                                "Hujjat tahlili" -> "Mehnat shartnomasida qanday majburiy shartlar bo'lishi kerak?"
                                                "Amaliy maslahat" -> "Xodim ishdan nohaq bo'shatilsa qanday choralar ko'rish lozim?"
                                                "Maqola izlash" -> "Jinoyat kodeksida odam o'ldirish va tan jarohati moddalari qanday farqlanadi?"
                                                else -> label
                                            }
                                            sendMessage(query)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = UzLegalPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = UzLegalPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
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
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = UzLegalPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Gemini qonunchilik asosida tahlil qilmoqda...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = UzLegalTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Input Bar matching mockup: "Savolingizni yozing..." + circular blue send button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, UzLegalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Savolingizni yozing...", fontSize = 13.sp, color = UzLegalTextSecondary) },
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

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onArticleClick: (String) -> Unit
) {
    val isUser = message.sender == "user"
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            if (!isUser) {
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
                Spacer(modifier = Modifier.width(8.dp))
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) UzLegalPrimary else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder) else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    )

                    if (message.citedArticles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = UzLegalBorder)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Asos qilingan moddalar:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        message.citedArticles.forEach { art ->
                            Surface(
                                color = UzLegalPrimary.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onArticleClick(art.id) }
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = UzLegalPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${art.articleNumberStr}: ${art.title}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = UzLegalPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (!isUser) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("AI Legal Answer", message.text)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Nusxalandi", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Nusxalash",
                                    tint = UzLegalTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
