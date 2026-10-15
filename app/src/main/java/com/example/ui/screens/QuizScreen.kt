package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class QuizStage {
    SETUP,
    ACTIVE,
    RESULTS,
    REVIEW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    repository: LegalRepository,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val availableTopics = remember { repository.getAvailableQuizTopics() }

    // Quiz Configuration State
    var selectedTopics by remember { mutableStateOf(setOf<String>()) } // Empty = All
    var selectedQuestionCount by remember { mutableIntStateOf(30) } // 10, 20, 30
    var isExamMode by remember { mutableStateOf(true) } // true = Exam with timer, false = Practice with immediate feedback
    var currentStage by remember { mutableStateOf(QuizStage.SETUP) }

    // Active Quiz Session State
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var userAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) } // index -> selected option index
    var confirmedAnswers by remember { mutableStateOf<Set<Int>>(emptySet()) } // question indices confirmed in practice mode
    var secondsRemaining by remember { mutableIntStateOf(1800) } // 30 minutes for exam
    var isTimerRunning by remember { mutableStateOf(false) }
    var showQuestionGridSheet by remember { mutableStateOf(false) }

    // Calculated Results
    val totalQuestions = questions.size
    val answeredCount = userAnswers.size
    val correctCount = remember(questions, userAnswers) {
        questions.mapIndexed { idx, q ->
            userAnswers[idx] == q.correctIndex
        }.count { it }
    }
    val scorePercentage = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val earnedXp = remember(correctCount, selectedQuestionCount) {
        correctCount * 10 + (if (selectedQuestionCount == 30 && correctCount >= 20) 100 else 0)
    }

    // Timer effect for exam mode
    LaunchedEffect(currentStage, isTimerRunning) {
        if (currentStage == QuizStage.ACTIVE && isExamMode && isTimerRunning) {
            while (secondsRemaining > 0 && currentStage == QuizStage.ACTIVE) {
                delay(1000L)
                secondsRemaining--
            }
            if (secondsRemaining <= 0 && currentStage == QuizStage.ACTIVE) {
                // Auto finish exam when time expires
                repository.addXp(earnedXp)
                currentStage = QuizStage.RESULTS
            }
        }
    }

    fun startQuiz() {
        val generated = repository.getAdaptiveQuizQuestions(selectedTopics, selectedQuestionCount)
        questions = generated
        currentIndex = 0
        userAnswers = emptyMap()
        confirmedAnswers = emptySet()
        secondsRemaining = when (selectedQuestionCount) {
            10 -> 600   // 10 mins
            20 -> 1200  // 20 mins
            else -> 1800 // 30 mins
        }
        isTimerRunning = true
        currentStage = QuizStage.ACTIVE
    }

    fun finishQuiz() {
        isTimerRunning = false
        coroutineScope.launch {
            repository.addXp(earnedXp)
        }
        currentStage = QuizStage.RESULTS
    }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = when (currentStage) {
                    QuizStage.SETUP -> "Huquqiy Test Tizimi"
                    QuizStage.ACTIVE -> if (isExamMode) "Imtihon (30 savol)" else "Mashg'ulot Testi"
                    QuizStage.RESULTS -> "Natijalar"
                    QuizStage.REVIEW -> "Xatolarni Tahlil Qilish"
                },
                subtitle = when (currentStage) {
                    QuizStage.ACTIVE -> "Savol ${currentIndex + 1} / ${questions.size}"
                    QuizStage.RESULTS -> "Umumiy natija: $scorePercentage%"
                    else -> "Moslashuvchan huquqiy bilim sinovi"
                },
                showBackButton = true,
                onBackClick = {
                    when (currentStage) {
                        QuizStage.SETUP -> onBack()
                        QuizStage.ACTIVE -> {
                            // If leaving active quiz, return to setup
                            currentStage = QuizStage.SETUP
                        }
                        QuizStage.RESULTS -> currentStage = QuizStage.SETUP
                        QuizStage.REVIEW -> currentStage = QuizStage.RESULTS
                    }
                },
                actions = {
                    if (currentStage == QuizStage.ACTIVE) {
                        // Timer pill
                        val minutes = secondsRemaining / 60
                        val seconds = secondsRemaining % 60
                        Surface(
                            color = if (secondsRemaining < 180) UzLegalError.copy(alpha = 0.2f) else UzLegalTealSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (secondsRemaining < 180) UzLegalError else UzLegalTealBorder
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (secondsRemaining < 180) UzLegalError else UzLegalTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%02d:%02d", minutes, seconds),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (secondsRemaining < 180) UzLegalError else UzLegalNavyHeadings,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Grid overview icon button
                        IconButton(
                            onClick = { showQuestionGridSheet = true },
                            modifier = Modifier.testTag("question_grid_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Savollar xaritasi",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("quiz_screen_container")
        ) {
            when (currentStage) {
                QuizStage.SETUP -> {
                    QuizSetupContent(
                        availableTopics = availableTopics,
                        selectedTopics = selectedTopics,
                        onToggleTopic = { topic ->
                            selectedTopics = if (selectedTopics.contains(topic)) {
                                selectedTopics - topic
                            } else {
                                selectedTopics + topic
                            }
                        },
                        onSelectAllTopics = {
                            selectedTopics = emptySet()
                        },
                        selectedCount = selectedQuestionCount,
                        onSelectCount = { selectedQuestionCount = it },
                        isExamMode = isExamMode,
                        onToggleMode = { isExamMode = it },
                        onStart = { startQuiz() }
                    )
                }

                QuizStage.ACTIVE -> {
                    val currentQuestion = questions.getOrNull(currentIndex)
                    if (currentQuestion != null) {
                        ActiveQuizContent(
                            question = currentQuestion,
                            currentIndex = currentIndex,
                            totalQuestions = questions.size,
                            userAnswer = userAnswers[currentIndex],
                            isPracticeMode = !isExamMode,
                            isConfirmed = confirmedAnswers.contains(currentIndex),
                            onSelectAnswer = { optIdx ->
                                userAnswers = userAnswers + (currentIndex to optIdx)
                            },
                            onConfirmPracticeAnswer = {
                                confirmedAnswers = confirmedAnswers + currentIndex
                            },
                            onNext = {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                } else {
                                    finishQuiz()
                                }
                            },
                            onPrev = {
                                if (currentIndex > 0) {
                                    currentIndex--
                                }
                            },
                            onFinish = { finishQuiz() }
                        )
                    }
                }

                QuizStage.RESULTS -> {
                    QuizResultsContent(
                        questions = questions,
                        userAnswers = userAnswers,
                        correctCount = correctCount,
                        totalCount = totalQuestions,
                        scorePercentage = scorePercentage,
                        earnedXp = earnedXp,
                        onReviewMistakes = { currentStage = QuizStage.REVIEW },
                        onRetake = { startQuiz() },
                        onChangeSettings = { currentStage = QuizStage.SETUP }
                    )
                }

                QuizStage.REVIEW -> {
                    QuizReviewContent(
                        questions = questions,
                        userAnswers = userAnswers,
                        onBackToResults = { currentStage = QuizStage.RESULTS }
                    )
                }
            }

            // Question Navigator Modal Bottom Sheet
            if (showQuestionGridSheet && currentStage == QuizStage.ACTIVE) {
                ModalBottomSheet(
                    onDismissRequest = { showQuestionGridSheet = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .navigationBarsPadding()
                    ) {
                        Text(
                            text = "Savollar jadvali",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${userAnswers.size} / ${questions.size} savolga javob berildi",
                            style = MaterialTheme.typography.bodySmall.copy(color = UzLegalSlateBody)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 280.dp)
                        ) {
                            items(questions.size) { idx ->
                                val isAnswered = userAnswers.containsKey(idx)
                                val isCurrent = idx == currentIndex

                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when {
                                                isCurrent -> UzLegalTeal
                                                isAnswered -> UzLegalTealSurface
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (isCurrent) UzLegalTealDark else UzLegalBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            currentIndex = idx
                                            showQuestionGridSheet = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                            color = when {
                                                isCurrent -> Color.White
                                                isAnswered -> UzLegalTealDark
                                                else -> UzLegalSlateBody
                                            }
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                showQuestionGridSheet = false
                                finishQuiz()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UzLegalNavyHeadings),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Imtihonni yakunlash")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 1: Adaptive Quiz Setup & Topic Selection
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuizSetupContent(
    availableTopics: List<String>,
    selectedTopics: Set<String>,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: () -> Unit,
    selectedCount: Int,
    onSelectCount: (Int) -> Unit,
    isExamMode: Boolean,
    onToggleMode: (Boolean) -> Unit,
    onStart: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(UzLegalTealSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = UzLegalTeal,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Moslashuvchan Test Platformasi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalNavyHeadings
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Kerakli qonunchilik sohalarini tanlang va tasodifiy 30 savolli professional imtihon tuzing.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UzLegalSlateBody,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }
        }

        // Section 1: Question Count Selector
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Savollar sonini tanlang",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(10, 20, 30).forEach { count ->
                            val isSelected = selectedCount == count
                            Surface(
                                color = if (isSelected) UzLegalTeal else UzLegalSurfaceVariantLight,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) UzLegalTealDark else UzLegalBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectCount(count) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$count ta",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else UzLegalNavyHeadings
                                        )
                                    )
                                    Text(
                                        text = if (count == 30) "To'liq imtihon" else "${count} daqiqa",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else UzLegalSlateBody
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Mode Selector
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Test rejimini tanlang",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Exam Mode
                        Surface(
                            color = if (isExamMode) UzLegalNavyHeadings else UzLegalSurfaceVariantLight,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isExamMode) UzLegalNavyHeadings else UzLegalBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onToggleMode(true) }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (isExamMode) Color.White else UzLegalNavyHeadings,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Imtihon rejimi",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExamMode) Color.White else UzLegalNavyHeadings
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vaqt hisobi bilan, yakuniy natijalar va batafsil tahlil.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        color = if (isExamMode) Color.White.copy(alpha = 0.8f) else UzLegalSlateBody,
                                        lineHeight = 14.sp
                                    )
                                )
                            }
                        }

                        // Practice Mode
                        Surface(
                            color = if (!isExamMode) UzLegalTeal else UzLegalSurfaceVariantLight,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (!isExamMode) UzLegalTealDark else UzLegalBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onToggleMode(false) }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = if (!isExamMode) Color.White else UzLegalTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Mashg'ulot",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (!isExamMode) Color.White else UzLegalNavyHeadings
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Har bir savoldan keyin darhol modda va sharhni ko'rish.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        color = if (!isExamMode) Color.White.copy(alpha = 0.8f) else UzLegalSlateBody,
                                        lineHeight = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Topic Selection
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Qonunchilik sohalari",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalNavyHeadings
                            )
                        )

                        TextButton(
                            onClick = onSelectAllTopics,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (selectedTopics.isEmpty()) "Barchasi tanlangan" else "Barchasini tanlash",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = UzLegalTeal
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Topic Checkbox Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTopics.forEach { topic ->
                            val isChecked = selectedTopics.isEmpty() || selectedTopics.contains(topic)
                            FilterChip(
                                selected = isChecked,
                                onClick = { onToggleTopic(topic) },
                                label = {
                                    Text(
                                        text = topic,
                                        fontSize = 12.sp,
                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UzLegalTealSurface,
                                    selectedLabelColor = UzLegalTealDark,
                                    selectedLeadingIconColor = UzLegalTealDark,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isChecked,
                                    borderColor = if (isChecked) UzLegalTeal else UzLegalBorder
                                )
                            )
                        }
                    }
                }
            }
        }

        // Start Test Action Button
        item {
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = UzLegalTeal),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_quiz_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tasodifiy $selectedCount talik Testni Boshlash",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Screen 2: Active Test Taking Screen
 */
@Composable
private fun ActiveQuizContent(
    question: QuizQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    userAnswer: Int?,
    isPracticeMode: Boolean,
    isConfirmed: Boolean,
    onSelectAnswer: (Int) -> Unit,
    onConfirmPracticeAnswer: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onFinish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Progress bar & badges
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = UzLegalTealSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = question.topic,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalTealDark
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${currentIndex + 1} / $totalQuestions",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = UzLegalNavyHeadings
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalQuestions },
                    color = UzLegalTeal,
                    trackColor = UzLegalBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        // Question Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "${currentIndex + 1}. ${question.questionText}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings,
                            lineHeight = 24.sp
                        )
                    )
                }
            }
        }

        // Options List
        itemsIndexed(question.options) { optIdx, optText ->
            val isSelected = userAnswer == optIdx
            val isCorrect = optIdx == question.correctIndex
            val showFeedback = isPracticeMode && isConfirmed

            val containerColor = when {
                showFeedback && isCorrect -> UzLegalSuccessContainer
                showFeedback && isSelected && !isCorrect -> UzLegalErrorContainer
                isSelected -> UzLegalTealSurface
                else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
                showFeedback && isCorrect -> UzLegalSuccess
                showFeedback && isSelected && !isCorrect -> UzLegalError
                isSelected -> UzLegalTeal
                else -> UzLegalBorder
            }

            Surface(
                color = containerColor,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !(isPracticeMode && isConfirmed)) {
                        onSelectAnswer(optIdx)
                    }
                    .testTag("quiz_option_$optIdx")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option letter indicator
                    val optionLetters = listOf("A", "B", "C", "D")
                    val letter = optionLetters.getOrElse(optIdx) { "${optIdx + 1}" }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    showFeedback && isCorrect -> UzLegalSuccess
                                    showFeedback && isSelected && !isCorrect -> UzLegalError
                                    isSelected -> UzLegalTeal
                                    else -> UzLegalSurfaceVariantLight
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected || (showFeedback && isCorrect)) Color.White else UzLegalNavyHeadings
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = optText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (showFeedback) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else if (isSelected) Icons.Default.Cancel else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isCorrect) UzLegalSuccess else if (isSelected) UzLegalError else Color.Transparent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Practice Mode Immediate Explanation Card
        if (isPracticeMode && isConfirmed) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UzLegalTealSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalTealBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = UzLegalTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rasmiy huquqiy sharh",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = UzLegalTealDark
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UzLegalNavyHeadings,
                                lineHeight = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Asos: ${question.verifiedCitation}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = UzLegalTeal
                            )
                        )
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Previous Button
                if (currentIndex > 0) {
                    OutlinedButton(
                        onClick = onPrev,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Oldingi")
                    }
                }

                // Check Button (Practice mode only)
                if (isPracticeMode && !isConfirmed && userAnswer != null) {
                    Button(
                        onClick = onConfirmPracticeAnswer,
                        colors = ButtonDefaults.buttonColors(containerColor = UzLegalNavyHeadings),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Tekshirish")
                    }
                }

                // Next or Finish Button
                val isLast = currentIndex == totalQuestions - 1
                Button(
                    onClick = {
                        if (isLast) onFinish() else onNext()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLast) UzLegalNavyHeadings else UzLegalTeal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(if (isLast) "Imtihonni topshirish" else "Keyingi")
                    if (!isLast) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

/**
 * Screen 3: Test Results & Comprehensive Score Breakdown
 */
@Composable
private fun QuizResultsContent(
    questions: List<QuizQuestion>,
    userAnswers: Map<Int, Int>,
    correctCount: Int,
    totalCount: Int,
    scorePercentage: Int,
    earnedXp: Int,
    onReviewMistakes: () -> Unit,
    onRetake: () -> Unit,
    onChangeSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Result Trophy Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Score Circle
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    scorePercentage >= 85 -> UzLegalSuccessContainer
                                    scorePercentage >= 60 -> UzLegalTealSurface
                                    else -> UzLegalWarningContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$scorePercentage%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        scorePercentage >= 85 -> UzLegalSuccess
                                        scorePercentage >= 60 -> UzLegalTeal
                                        else -> UzLegalWarning
                                    }
                                )
                            )
                            Text(
                                text = "$correctCount / $totalCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = UzLegalSlateBody,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when {
                            scorePercentage >= 90 -> "A'lo darajada yakunlandi! 🏆"
                            scorePercentage >= 75 -> "Yaxshi natija! 👏"
                            scorePercentage >= 50 -> "Qoniqarli bilim darajasi 📚"
                            else -> "Qayta tayyorlanish tavsiya etiladi ⚠️"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = UzLegalTeal,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "+$earnedXp XP qo'shildi!",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Section: Breakdown by Topic
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sohalar bo'yicha aniqlik",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val topicsMap = questions.mapIndexed { idx, q ->
                        q.topic to (userAnswers[idx] == q.correctIndex)
                    }.groupBy({ it.first }, { it.second })

                    topicsMap.forEach { (topic, results) ->
                        val correctInTopic = results.count { it }
                        val totalInTopic = results.size
                        val pct = (correctInTopic * 100) / totalInTopic

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = UzLegalNavyHeadings
                                    )
                                )
                                Text(
                                    text = "$correctInTopic / $totalInTopic ($pct%)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (pct >= 70) UzLegalSuccess else UzLegalSlateBody,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { pct / 100f },
                                color = if (pct >= 70) UzLegalSuccess else UzLegalTeal,
                                trackColor = UzLegalBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onReviewMistakes,
                    colors = ButtonDefaults.buttonColors(containerColor = UzLegalNavyHeadings),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("review_mistakes_button")
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Savollar va xatolarni tahlil qilish")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onChangeSettings,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Yangi parametrlar")
                    }

                    Button(
                        onClick = onRetake,
                        colors = ButtonDefaults.buttonColors(containerColor = UzLegalTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Qayta topshirish")
                    }
                }
            }
        }
    }
}

/**
 * Screen 4: Detailed Review of All Questions & Mistakes
 */
@Composable
private fun QuizReviewContent(
    questions: List<QuizQuestion>,
    userAnswers: Map<Int, Int>,
    onBackToResults: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        itemsIndexed(questions) { index, question ->
            val userAns = userAnswers[index]
            val isCorrect = userAns == question.correctIndex

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCorrect) UzLegalSuccess.copy(alpha = 0.5f) else UzLegalError.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isCorrect) UzLegalSuccessContainer else UzLegalErrorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "To'g'ri javob" else "Xato javob",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) UzLegalSuccess else UzLegalError
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = question.topic,
                            style = MaterialTheme.typography.labelSmall.copy(color = UzLegalSlateBody)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${index + 1}. ${question.questionText}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = UzLegalNavyHeadings
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Show correct option
                    Text(
                        text = "To'g'ri javob: ${question.options.getOrNull(question.correctIndex) ?: ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = UzLegalSuccess
                        )
                    )

                    if (!isCorrect && userAns != null) {
                        Text(
                            text = "Sizning javobingiz: ${question.options.getOrNull(userAns) ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UzLegalError
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = UzLegalTealSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Sharh: ${question.explanation}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = UzLegalNavyHeadings,
                                    fontSize = 11.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Manba: ${question.verifiedCitation}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = UzLegalTeal,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onBackToResults,
                colors = ButtonDefaults.buttonColors(containerColor = UzLegalNavyHeadings),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Natijalarga qaytish")
            }
        }
    }
}
