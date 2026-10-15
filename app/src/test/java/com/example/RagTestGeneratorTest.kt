package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LegislationData
import com.example.data.model.*
import com.example.data.remote.LegalRagRetrievalEngine
import com.example.data.remote.QuestionValidationPipeline
import com.example.data.remote.SmartRagQuestionGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RagTestGeneratorTest {

    private lateinit var context: Context
    private lateinit var retrievalEngine: LegalRagRetrievalEngine
    private lateinit var questionGenerator: SmartRagQuestionGenerator

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        retrievalEngine = LegalRagRetrievalEngine(context)
        questionGenerator = SmartRagQuestionGenerator(context)
        LegislationData.loadAllArticles(context)
    }

    @Test
    fun test1_SelectAll19Codes_ResolvesAllArticles() {
        val allCodeIds = LegislationData.UZBEKISTAN_CODES.map { it.id }.toSet()
        val selection = LegalSourceSelection(selectedCodeIds = allCodeIds)

        val resolved = retrievalEngine.resolveSelectedArticles(selection)
        assertTrue("Should resolve articles when all 19 codes selected", resolved.isNotEmpty())
        assertEquals("Selection should report all codes selected", true, selection.isAllCodesSelected())
    }

    @Test
    fun test2_SelectSingleCode_ResolvesOnlyThatCodeArticles() {
        val selection = LegalSourceSelection(selectedCodeIds = setOf("fk"))

        val resolved = retrievalEngine.resolveSelectedArticles(selection)
        assertTrue("Should resolve articles for 'fk'", resolved.isNotEmpty())
        assertTrue("All articles must belong to 'fk'", resolved.all { it.codeId == "fk" })
        assertFalse("Unselected codes like 'jk' must NOT be included", resolved.any { it.codeId == "jk" })
    }

    @Test
    fun test3_SelectMultipleCodes_StrictSourceIsolation() {
        val selectedCodes = setOf("fk", "jk", "mk")
        val selection = LegalSourceSelection(selectedCodeIds = selectedCodes)

        val resolved = retrievalEngine.resolveSelectedArticles(selection)
        assertTrue("Should resolve articles for selected 3 codes", resolved.isNotEmpty())
        assertTrue("Articles must belong ONLY to fk, jk, or mk", resolved.all { it.codeId in selectedCodes })
        assertFalse("Unselected code 'sk' must NOT be in retrieval pool", resolved.any { it.codeId == "sk" })
    }

    @Test
    fun test4_SelectSingleArticle_ResolvesOnlyTargetArticle() {
        val targetArticleId = "fk_14"
        val selection = LegalSourceSelection(selectedArticleIds = setOf(targetArticleId))

        val resolved = retrievalEngine.resolveSelectedArticles(selection)
        assertEquals("Should resolve exactly 1 target article", 1, resolved.size)
        assertEquals("Resolved article ID must match target", targetArticleId, resolved.first().id)
    }

    @Test
    fun test5_SelectMultipleArticlesAcrossCodes_ResolvesTargetArticles() {
        val targetIds = setOf("fk_14", "jk_97", "mk_112")
        val selection = LegalSourceSelection(selectedArticleIds = targetIds)

        val resolved = retrievalEngine.resolveSelectedArticles(selection)
        assertEquals("Should resolve all 3 target articles across codes", 3, resolved.size)
        assertEquals("Resolved IDs must equal target set", targetIds, resolved.map { it.id }.toSet())
    }

    @Test
    fun test6_CombineEntireCodeAndIndividualArticleSelections() {
        val selection = LegalSourceSelection(
            selectedCodeIds = setOf("ok"), // Entire Family Code
            selectedArticleIds = setOf("jk_97") // Plus Article 97 of Criminal Code
        )

        val resolved = retrievalEngine.resolveSelectedArticles(selection)
        assertTrue("Should contain Family Code articles", resolved.any { it.codeId == "ok" })
        assertTrue("Should contain Criminal Code article 97", resolved.any { it.id == "jk_97" })
        assertFalse("Unselected code 'sk' must NOT be included", resolved.any { it.codeId == "sk" })
    }

    @Test
    fun test7_QuestionValidationPipeline_FiltersInvalidAndDuplicateQuestions() {
        val validQuestion = GeneratedTestQuestion(
            id = "test_q1",
            questionText = "Fuqarolik kodeksining 14-moddasiga ko'ra zararni qoplash tartibi qanday?",
            questionType = QuestionType.SINGLE_CHOICE,
            options = listOf("To'liq qoplanadi", "50 foizi qoplanadi", "Qoplanmaydi", "Boshqa"),
            correctIndices = listOf(0),
            explanation = "14-moddada zarar to'liq hajmda qoplanishi belgilangan.",
            codeId = "fk",
            codeName = "Fuqarolik kodeksi",
            chapter = "Fuqarolik huquqlarini himoya qilish",
            articleNumber = 14,
            articleNumberStr = "14-modda",
            articleTitle = "Zararni qoplash",
            supportingSourceExcerpt = "1. Huquqi buzilgan shaxs o'ziga yetkazilgan zararning to'liq qoplanishini talab qilishi mumkin.",
            difficulty = TestDifficulty.EASY,
            fingerprint = "fp_test_1"
        )

        // 1. Test Valid Question
        val res1 = QuestionValidationPipeline.validateQuestion(validQuestion, emptySet())
        assertTrue("Valid question must pass validation", res1.isValid)

        // 2. Test Duplicate Detection
        val res2 = QuestionValidationPipeline.validateQuestion(validQuestion, setOf("fp_test_1"))
        assertFalse("Duplicate fingerprint question must be rejected", res2.isValid)

        // 3. Test Invalid Options Count
        val invalidOptionsQuestion = validQuestion.copy(options = listOf("Faqat 1 ta option"))
        val res3 = QuestionValidationPipeline.validateQuestion(invalidOptionsQuestion, emptySet())
        assertFalse("Question with < 2 options must be rejected", res3.isValid)
    }

    @Test
    fun test8_RagGenerator_BatchGenerationAndMaxAvailableMode() = runBlocking {
        val selection = LegalSourceSelection(selectedCodeIds = setOf("fk"))
        val config = TestGenerationConfig(
            questionCount = 10,
            isMaxAvailableMode = false,
            selectedTypes = setOf(QuestionType.SINGLE_CHOICE, QuestionType.TRUE_FALSE, QuestionType.SCENARIO_BASED),
            difficulty = TestDifficulty.MIXED
        )

        val questions = questionGenerator.generateQuestions(selection, config)
        assertTrue("Should generate RAG questions for Fuqarolik kodeksi", questions.isNotEmpty())
        assertTrue("All generated questions must belong to selected code 'fk'", questions.all { it.codeId == "fk" })
        assertTrue("Every generated question must have valid source excerpt", questions.all { it.supportingSourceExcerpt.isNotBlank() })
    }
}
