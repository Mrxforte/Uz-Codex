package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.Lesson
import com.example.data.model.UserLevel
import com.example.data.repository.LegalRepository
import com.example.ui.components.UzLegalTopBar
import com.example.ui.components.XPLevelBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AcademyScreen(
    repository: LegalRepository,
    onStartQuiz: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.getUserProfile().collectAsState(initial = null)
    val courses = remember { repository.getAllCourses() }
    val achievements = remember { repository.getAchievements() }
    val completedLessons by repository.getLessonProgress().collectAsState(initial = emptyList())

    val xp = userProfile?.xp ?: 120
    val streak = userProfile?.streakDays ?: 3
    val currentLevel = remember(xp) { UserLevel.getLevelForXp(xp) }

    var selectedLessonForDialog by remember { mutableStateOf<Lesson?>(null) }

    Scaffold(
        topBar = {
            UzLegalTopBar(
                title = "Huquqiy Akademiya",
                subtitle = "O'rganish, testlar va tajriba ballari (XP)"
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
                .testTag("academy_screen")
        ) {
            // Level & XP Progress
            item {
                XPLevelBadge(
                    levelTitle = currentLevel.title,
                    xp = xp,
                    streakDays = streak
                )
            }

            // Quick Exam / Quiz Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LegalGoldLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartQuiz() }
                        .testTag("btn_take_quiz")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(LegalGoldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Interaktiv Yuridik Viktorina",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LegalNavyPrimary
                                )
                            )
                            Text(
                                text = "5 ta savolga to'g'ri javob bering va darajangizni oshiring!",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F))
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Boshlash",
                            tint = LegalNavyPrimary
                        )
                    }
                }
            }

            // Learning Courses
            item {
                Text(
                    text = "O'quv yo'nalishlari va darslar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            items(courses) { course ->
                CourseCard(
                    course = course,
                    completedLessons = completedLessons.map { it.lessonId },
                    onSelectLesson = { lesson ->
                        selectedLessonForDialog = lesson
                    }
                )
            }

            // Achievements Section
            item {
                Text(
                    text = "Yutuqlar va medallar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            items(achievements) { ach ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LegalLightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (ach.isUnlocked) LegalGold else LegalLightBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (ach.isUnlocked) LegalNavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ach.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = ach.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        if (ach.isUnlocked) {
                            Surface(
                                color = LegalSuccessContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Erishildi",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = LegalSuccess,
                                        fontWeight = FontWeight.Bold
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

    // Lesson Reader Dialog
    if (selectedLessonForDialog != null) {
        val lesson = selectedLessonForDialog!!
        val isAlreadyDone = completedLessons.any { it.lessonId == lesson.id }

        AlertDialog(
            onDismissRequest = { selectedLessonForDialog = null },
            title = {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text(
                            text = lesson.explanationText,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                        )
                    }

                    item {
                        Text(
                            text = "Asosiy qoidalar:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        lesson.keyPoints.forEach { pt ->
                            Text(text = "• $pt", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    item {
                        Text(
                            text = "Qonuniy asos: ${lesson.articleReferences.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LegalNavyPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isAlreadyDone) {
                            coroutineScope.launch {
                                repository.completeLesson(lesson.id, lesson.courseId, lesson.xpReward)
                            }
                        }
                        selectedLessonForDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LegalNavyPrimary)
                ) {
                    Text(if (isAlreadyDone) "Yopish" else "Darsni yakunlash (+${lesson.xpReward} XP)")
                }
            }
        )
    }
}

@Composable
fun CourseCard(
    course: Course,
    completedLessons: List<String>,
    onSelectLesson: (Lesson) -> Unit
) {
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
                Surface(
                    color = LegalNavyPrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = course.level,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LegalNavyPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "+${course.xpReward} XP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LegalGoldDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Darslar ro'yxati:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            course.lessons.forEach { lesson ->
                val isDone = completedLessons.contains(lesson.id)
                Surface(
                    color = if (isDone) LegalSuccessContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLesson(lesson) }
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = if (isDone) LegalSuccess else LegalNavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${lesson.orderNumber}. ${lesson.title}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "+${lesson.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall.copy(color = LegalGoldDark)
                        )
                    }
                }
            }
        }
    }
}
