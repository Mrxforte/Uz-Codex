package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val articleId: String,
    val codeId: String,
    val codeTitle: String,
    val articleNumber: String,
    val articleTitle: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_notes")
data class UserNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleId: String,
    val articleTitle: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_case_analyses")
data class SavedCaseAnalysisEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val scenarioCategory: String,
    val summary: String,
    val userNarrative: String,
    val qualificationSummary: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String = "Foydalanuvchi",
    val role: String = "Huquqshunoslik o'rganuvchisi",
    val xp: Int = 120,
    val streakDays: Int = 3,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val totalQuizzesAnswered: Int = 8,
    val totalCorrectAnswers: Int = 7
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey
    val lessonId: String,
    val courseId: String,
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "generated_test_sets")
data class GeneratedTestSetEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val questionCount: Int,
    val selectionSummary: String,
    val configJson: String
)

@Entity(tableName = "generated_questions")
data class GeneratedQuestionEntity(
    @PrimaryKey
    val id: String,
    val testSetId: String,
    val questionText: String,
    val questionType: String,
    val optionsJson: String,
    val correctIndicesJson: String,
    val matchPairsJson: String? = null,
    val fillInBlankAnswer: String? = null,
    val explanation: String,
    val codeId: String,
    val codeName: String,
    val chapter: String,
    val articleNumber: Int,
    val articleNumberStr: String,
    val articleTitle: String,
    val sourceExcerpt: String,
    val difficulty: String,
    val fingerprint: String,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "test_attempt_history")
data class TestAttemptHistoryEntity(
    @PrimaryKey
    val id: String,
    val testSetId: String,
    val testTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val scorePercentage: Int,
    val correctCount: Int,
    val totalCount: Int,
    val userAnswersJson: String,
    val durationSeconds: Int,
    val earnedXp: Int
)

@Entity(tableName = "generated_fingerprints")
data class GeneratedFingerprintEntity(
    @PrimaryKey
    val fingerprint: String,
    val generatedAt: Long = System.currentTimeMillis()
)
