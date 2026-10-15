package com.example.data.model

import android.content.Context
import com.example.data.local.LegislationData

enum class QuestionType(val titleUz: String, val descriptionUz: String) {
    SINGLE_CHOICE("Yagona to'g'ri javob", "4 ta Variantdan 1 ta to'g'ri javobni tanlash"),
    MULTIPLE_CHOICE("Bir nechta to'g'ri javob", "Bir nechta to'g'ri javob variantlarini belgilash"),
    TRUE_FALSE("To'g'ri / Noto'g'ri", "Norma tasdig'ining to'g'ri yoki noto'g'riligini aniqlash"),
    FILL_IN_BLANK("Bo'sh o'rinni to'ldiring", "Qonun normasida tushirib qoldirilgan kalit so'zni topish"),
    MATCH_CONCEPTS("Tushunchalarni moslashtiring", "Huquqiy tushunchalar va ularning ta'riflarini moslashtirish"),
    SCENARIO_BASED("Kazus va amaliy vaziyat", "Real sud va huquqiy vaziyat (kazus) bo'yicha tahlil"),
    MIXED("Aralash turdagilar", "Barcha turdagi savollardan tashkil topgan test")
}

enum class TestDifficulty(val titleUz: String, val descriptionUz: String) {
    EASY("Oson", "Normaning to'g'ridan-to'g'ri mazmunini tushunish"),
    MEDIUM("O'rta", "Normani amalda tahlil qilish va qo'llash"),
    HARD("Murakkab", "Istisnolar, shartlar va qiyosiy huquqiy tahlil"),
    MIXED("Aralash", "Turli darajadagi savollar jamlanmasi")
}

/**
 * Granular Source Selection Model:
 * Supports codes, chapters, individual articles, and article ranges.
 */
data class LegalSourceSelection(
    val selectedCodeIds: Set<String> = emptySet(),
    val selectedChapters: Map<String, Set<String>> = emptyMap(), // codeId -> Set<ChapterName>
    val selectedArticleIds: Set<String> = emptySet(),
    val selectedArticleRanges: Map<String, List<IntRange>> = emptyMap() // codeId -> List<IntRange>
) {
    fun isEmpty(): Boolean {
        return selectedCodeIds.isEmpty() &&
                selectedChapters.values.all { it.isEmpty() } &&
                selectedArticleIds.isEmpty() &&
                selectedArticleRanges.values.all { it.isEmpty() }
    }

    fun isAllCodesSelected(): Boolean {
        return selectedCodeIds.size >= LegislationData.UZBEKISTAN_CODES.size
    }

    /**
     * Resolves exact list of LegalArticles matching the source selection rules.
     * Enforces STRICT isolation: unselected codes are never returned.
     */
    fun resolveArticles(context: Context): List<LegalArticle> {
        val allArticles = LegislationData.getAllArticles(context)
        if (isEmpty()) return emptyList()

        return allArticles.filter { article ->
            val codeId = article.codeId

            // 1. Direct code-level match
            val isCodeSelected = selectedCodeIds.contains(codeId)

            // 2. Chapter-level match
            val codeChapters = selectedChapters[codeId] ?: emptySet()
            val isChapterSelected = codeChapters.contains(article.chapter)

            // 3. Article ID direct match
            val isArticleSelected = selectedArticleIds.contains(article.id)

            // 4. Article range match
            val ranges = selectedArticleRanges[codeId] ?: emptyList()
            val isRangeSelected = ranges.any { article.articleNumber in it }

            isCodeSelected || isChapterSelected || isArticleSelected || isRangeSelected
        }
    }

    fun getSelectionSummaryText(context: Context): String {
        if (isEmpty()) return "Manba tanlanmagan"
        if (isAllCodesSelected()) return "Barcha 19 ta Kodeks (To'liq baza)"

        val parts = mutableListOf<String>()
        if (selectedCodeIds.isNotEmpty()) {
            val names = selectedCodeIds.mapNotNull { LegislationData.getCodeById(it)?.shortName }
            parts.add("${names.size} ta kodeks (${names.take(2).joinToString(", ")}${if (names.size > 2) "..." else ""})")
        }

        val resolved = resolveArticles(context)
        if (selectedArticleIds.isNotEmpty() || selectedChapters.isNotEmpty() || selectedArticleRanges.isNotEmpty()) {
            parts.add("${resolved.size} ta modda biriktirilgan")
        }

        return parts.joinToString(" • ")
    }
}

/**
 * Full configuration for RAG test generation.
 */
data class TestGenerationConfig(
    val questionCount: Int = 10,
    val isMaxAvailableMode: Boolean = false,
    val selectedTypes: Set<QuestionType> = setOf(QuestionType.SINGLE_CHOICE, QuestionType.SCENARIO_BASED),
    val difficulty: TestDifficulty = TestDifficulty.MIXED,
    val includeExplanations: Boolean = true,
    val showAnswerImmediately: Boolean = false,
    val shuffleQuestionsAndOptions: Boolean = true,
    val excludePreviouslyGenerated: Boolean = true,
    val includeArticleReferences: Boolean = true,
    val generateSeparateTestsPerCode: Boolean = false,
    val timeLimitMinutes: Int? = 15,
    val language: String = "uz"
)

/**
 * Rich model for a RAG-generated legal question with exact source traceability.
 */
data class GeneratedTestQuestion(
    val id: String,
    val questionText: String,
    val questionType: QuestionType,
    val options: List<String> = emptyList(),
    val correctIndices: List<Int> = emptyList(), // Supports single & multiple answers
    val matchPairs: List<Pair<String, String>>? = null, // Left -> Right pairs for concept matching
    val fillInBlankAnswer: String? = null,
    val explanation: String,
    val codeId: String,
    val codeName: String,
    val chapter: String,
    val articleNumber: Int,
    val articleNumberStr: String,
    val articleTitle: String,
    val supportingSourceExcerpt: String,
    val difficulty: TestDifficulty,
    val fingerprint: String,
    val isValidated: Boolean = true,
    val validationResult: String = "Tasdiqlangan"
)

/**
 * A persisted set of generated questions with full configuration history.
 */
data class GeneratedTestSet(
    val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val config: TestGenerationConfig,
    val selectionSummary: String,
    val questions: List<GeneratedTestQuestion>
)
