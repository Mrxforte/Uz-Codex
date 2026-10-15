package com.example.data.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val level: String, // Boshlang'ich, Talaba, Amaliyotchi
    val totalLessons: Int,
    val estimatedMinutes: Int,
    val icon: String,
    val xpReward: Int,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val courseId: String,
    val orderNumber: Int,
    val title: String,
    val keyPoints: List<String>,
    val articleReferences: List<String>,
    val explanationText: String,
    val xpReward: Int = 10
)

data class QuizQuestion(
    val id: String,
    val topic: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val verifiedCitation: String,
    val xpReward: Int = 5,
    val difficulty: String = "O'rtacha"
)

data class UserAchievement(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val requiredProgress: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: String? = null
)

data class UserLevel(
    val levelNumber: Int,
    val title: String,
    val minXp: Int,
    val maxXp: Int
) {
    companion object {
        val ALL_LEVELS = listOf(
            UserLevel(1, "Huquqiy boshlovchi", 0, 99),
            UserLevel(2, "Qonun o'quvchisi", 100, 249),
            UserLevel(3, "Huquq izlovchi", 250, 499),
            UserLevel(4, "Huquq bilimdoni", 500, 899),
            UserLevel(5, "Tahlilchi", 900, 1499),
            UserLevel(6, "Ilg'or o'rganuvchi", 1500, 2299),
            UserLevel(7, "Huquq ustasi", 2300, 3499),
            UserLevel(8, "Ekspert darajasi", 3500, 100000)
        )

        fun getLevelForXp(xp: Int): UserLevel {
            return ALL_LEVELS.lastOrNull { xp >= it.minXp } ?: ALL_LEVELS.first()
        }
    }
}
