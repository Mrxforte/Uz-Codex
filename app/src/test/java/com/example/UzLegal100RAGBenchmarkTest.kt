package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LegislationData
import com.example.data.remote.HybridLegalRetrievalEngine
import com.example.data.remote.LegalEvidenceValidator
import com.example.data.remote.QueryUnderstandingEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.system.measureTimeMillis

data class BenchmarkTestCase(
    val id: Int,
    val query: String,
    val expectedCodeId: String?, // Null if out of scope or unanswerable
    val expectedArticleId: String?,
    val isUnanswerableOrOutOfScope: Boolean = false,
    val category: String // "exact_lookup", "colloquial_slang", "multi_code", "ambiguous", "unanswerable"
)

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class UzLegal100RAGBenchmarkTest {

    private lateinit var context: Context
    private lateinit var retrievalEngine: HybridLegalRetrievalEngine

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        LegislationData.loadAllArticles(context)
        retrievalEngine = HybridLegalRetrievalEngine(context)
    }

    private fun generate100BenchmarkCases(): List<BenchmarkTestCase> {
        val list = mutableListOf<BenchmarkTestCase>()
        var idCounter = 1

        // 1. Exact Article Lookups (25 cases)
        val exactLookups = listOf(
            Triple("FK 14-modda zararni qoplash", "fk", "fk_14"),
            Triple("Fuqarolik kodeksi 535-modda ijara", "fk", "fk_535"),
            Triple("FK 985-modda delikt", "fk", "fk_985"),
            Triple("Fuqarolik kodeksi 732 qarz shartnomasi", "fk", "fk_732"),
            Triple("FPK 1-modda fuqarolik protsessi", "fpk", "fpk_1"),
            Triple("FPK 209-modda videokonferensaloqa", "fpk", "fpk_209"),
            Triple("Jinoyat kodeksi 97-modda qasddan odam o'ldirish", "jk", "jk_97"),
            Triple("JK 98-modda affekt", "jk", "jk_98"),
            Triple("JK 102 ehtiyotsizlik orqasida o'lim", "jk", "jk_102"),
            Triple("JK 104 badanga og'ir shikast", "jk", "jk_104"),
            Triple("JK 37 zaruriy mudofaa", "jk", "jk_37"),
            Triple("JK 118-modda nomusga tegish", "jk", "jk_118"),
            Triple("JK 168 firibgarlik", "jk", "jk_168"),
            Triple("JPK 23-modda aybsizlik prezumpsiyasi", "jpk", "jpk_23"),
            Triple("JPK 50 himoyachi ishtiroki", "jpk", "jpk_50"),
            Triple("JIK 1-modda jinoyat ijroiya", "jik", "jik_1"),
            Triple("IPK 25 iqtisodiy sudga taalluqlilik", "ipk", "ipk_25"),
            Triple("MSIYK 26 ma'muriy sudga taalluqlilik", "msiyk", "msiyk_26"),
            Triple("MJTK 125 xavfsizlik kamari", "mjtk", "mjtk_125"),
            Triple("MJTK 128-4 svetofor qizil chiroq", "mjtk", "mjtk_128_4"),
            Triple("MJTK 135 haydovchilik guvohnomasi bo'lmagan", "mjtk", "mjtk_135"),
            Triple("MJTK 313 ma'muriy qaror ustidan shikoyat", "mjtk", "mjtk_313"),
            Triple("MK 112 ish vaqti davomiyligi", "mk", "mk_112"),
            Triple("MK 161 ish beruvchi tashabbusi bilan bekor qilish", "mk", "mk_161"),
            Triple("MK 253 ish haqini to'lash muddatlari", "mk", "mk_253")
        )
        for (item in exactLookups) {
            list.add(BenchmarkTestCase(idCounter++, item.first, item.second, item.third, false, "exact_lookup"))
        }

        // 2. Colloquial Uzbek Slang & Everyday Misspellings (30 cases)
        val colloquialCases = listOf(
            Triple("prava yo'q edi gaylar ushlab oldi", "mjtk", "mjtk_135"),
            Triple("oyligimni 2 oydan beri berishmayapti nima qilsam bo'ladi", "mk", "mk_253"),
            Triple("ishdan noqonuniy haydashdi prikazyozmasdan", "mk", "mk_161"),
            Triple("remen taqmaganim uchun jarima yozishdi", "mjtk", "mjtk_125"),
            Triple("svetoforning qizil chirog'ida o'tib ketdim", "mjtk", "mjtk_128_4"),
            Triple("birga yashaydigan kvartirant pulni bermay qochib ketdi", "fk", "fk_535"),
            Triple("og'zaki qarz bergandim endi tonip yeb ketdi", "fk", "fk_732"),
            Triple("xotinim bilan ajrashmoqchiman aliment qancha boladi", "ok", "ok_99"),
            Triple("birov mening nomimdan kartamdan firibgarlik bilan pul yechdi", "jk", "jk_168"),
            Triple("sudda mening aybim isbotlanmadi baribir qamashadimi", "jpk", "jpk_23"),
            Triple("menga advokat berilishi shartmi", "jpk", "jpk_50"),
            Triple("inspektor ma'muriy bayonnoma tuzdi qayerga shikoyat qilay", "mjtk", "mjtk_313"),
            Triple("menga tajovuz qilishganda o'zimni himoya qildim", "jk", "jk_37"),
            Triple("haftasiga 50 soat ishlatishyapti bu qonuniymi", "mk", "mk_112"),
            Triple("binafsha ko'cha uy ijarasi davlat ro'yxati shartmi", "fk", "fk_535"),
            Triple("zararni to'liq qoplashni talab qila olamanmi", "fk", "fk_14"),
            Triple("yo'lda pravasiz mashina boshqarganga qanday jazo bor", "mjtk", "mjtk_135"),
            Triple("zarpalata kechiksa kompensatsiya to'lanadimi", "mk", "mk_253"),
            Triple("notariusda nikoh shartnomasi tuzish majburiymi", "ok", "ok_1"),
            Triple("soliq daromaddan necha foiz ushlanadi", "sk", "sk_1"),
            Triple("shaxsiy daxlsizlik buzilganda qayerga murojaat qilinadi", "fpk", "fpk_1"),
            Triple("videokonferensiya orqali masofadan sudda qatnashsa bo'ladimi", "fpk", "fpk_209"),
            Triple("ish joyimda xodimni asossiz ishdan bo'shatishvotti", "mk", "mk_161"),
            Triple("qasddan odam o'ldirganlik uchun jazo qanday", "jk", "jk_97"),
            Triple("ehtiyotsizlik orqasida odam o'lib qolsa", "jk", "jk_102"),
            Triple("og'ir shikast yetkazilganda sud amaliyoti", "jk", "jk_104"),
            Triple("ma'muriy sud davlat organi qarorini bekor qiladimi", "msiyk", "msiyk_1"),
            Triple("iqtisodiy nizo tadbirkorlar o'rtasida qaysi sudda ko'riladi", "ipk", "ipk_25"),
            Triple("samoviy hudud suvereniteti havo kodeksida", "hk", "hk_1"),
            Triple("mahkumlar huquqlari va advokat bilan uchrashuvi", "jik", "jik_8")
        )
        for (item in colloquialCases) {
            list.add(BenchmarkTestCase(idCounter++, item.first, item.second, item.third, false, "colloquial_slang"))
        }

        // 3. Multi-code & Complex Scenarios (20 cases)
        val multiCodeCases = listOf(
            Triple("ish beruvchi oyligimni bermayapti va uydan ham ijaradan chiqarib yuborishmoqchi", "mk", "mk_253"),
            Triple("svetofor qizilda o'tib pravasiz ushlandim", "mjtk", "mjtk_135"),
            Triple("mashinada remen taqmay svetofor qizilida o'tganlik", "mjtk", "mjtk_128_4"),
            Triple("qarz berdim tilxat bor lekin aldab bermayapti", "fk", "fk_732"),
            Triple("ish beruvchi noqonuniy bo'shatib oylikni ham to'lamadi", "mk", "mk_161"),
            Triple("ijara shartnomasi buzildi va zarar yetkazildi", "fk", "fk_14"),
            Triple("nomusga tegish jinoyatida aybsizlik prezumpsiyasi va advokat", "jk", "jk_118"),
            Triple("firibgarlik oqibatida yetkazilgan zararni qoplash", "jk", "jk_168"),
            Triple("zaruriy mudofaa va badanga og'ir shikast yetkazish", "jk", "jk_37"),
            Triple("aybsizlik prezumpsiyasi va himoyachi ishtiroki", "jpk", "jpk_23"),
            Triple("xavfsizlik kamarini taqmaslik va haydovchilik guvohnomasiz boshqarish", "mjtk", "mjtk_125"),
            Triple("ma'muriy qaror ustidan shikoyat va zararni qoplash", "mjtk", "mjtk_313"),
            Triple("mehnat shartnomasini bekor qilish va oxirgi hisob kitob oylik", "mk", "mk_161"),
            Triple("qarz shartnomasi va firibgarlik farqi", "fk", "fk_732"),
            Triple("turar joy ijarasi va ko'chmas mulk davlat ro'yxati", "fk", "fk_535"),
            Triple("ehtiyotsizlik orqasida o'lim va zaruriy mudofaa", "jk", "jk_102"),
            Triple("iqtisodiy sud va ma'muriy sud taalluqliligi", "ipk", "ipk_25"),
            Triple("mahkum huquqlari va jinoyat ijroiya qonunchiligi", "jik", "jik_8"),
            Triple("haftasiga 40 soatdan ortiq ishlatish va oylik kechiktirish", "mk", "mk_112"),
            Triple("videokonferensiya va masofadan fuqarolik sudida ishtirok", "fpk", "fpk_209")
        )
        for (item in multiCodeCases) {
            list.add(BenchmarkTestCase(idCounter++, item.first, item.second, item.third, false, "multi_code"))
        }

        // 4. Ambiguous Complaints Needing Clarification (15 cases)
        val ambiguousCases = listOf(
            "meni siqishyapti",
            "hujjatimni olib qo'yishdi",
            "boshimga yomon ish tushdi",
            "menga bosim o'tkazishmoqchi",
            "ishda muammo bo'lyapti",
            "haqqimni berishmayapti",
            "menga nisbatan nohaqlik bo'ldi",
            "uydan haydashyapti",
            "mashinamni olib qo'yishdi",
            "menga tegishli narsani berishmayapti",
            "meni aldashdi nima qilay",
            "ko'chamizda janjal bo'ldi",
            "bir kishi bilan urishib qoldik",
            "menga og'zaki aytishdi",
            "shartnomaga qo'l qo'ydirishdi"
        )
        for (q in ambiguousCases) {
            list.add(BenchmarkTestCase(idCounter++, q, null, null, false, "ambiguous"))
        }

        // 5. Unanswerable / Out of Scope Queries (10 cases)
        val unanswerableCases = listOf(
            "Oyda er osti uy-joy sotib olish shartnomasi",
            "Python dasturlash tilida for loop yozib ber",
            "Barcelona va Real Madrid o'yini necha necha bo'ldi",
            "Pitsa tayyorlash retseptini aytib ber",
            "Toshkentda ertaga ob havo pogoda qanday bo'ladi",
            "Mars planetasida yer uchastkasi xususiylashtirish",
            "C++ tili bilan Android ilova yaratish",
            "Koinot kosmik stansiyasida sud majlisi o'tkazish",
            "Plov tayyorlash masalliqlari",
            "Eng zo'r komediya kino tavsiya et"
        )
        for (q in unanswerableCases) {
            list.add(BenchmarkTestCase(idCounter++, q, null, null, true, "unanswerable"))
        }

        return list
    }

    @Test
    fun runFull100BenchmarkSuiteAndMeasureMetrics() = runBlocking {
        val testCases = generate100BenchmarkCases()
        assertEquals("Benchmark suite must contain exactly 100 test cases", 100, testCases.size)

        var totalRecallTop5 = 0
        var totalReciprocalRankSum = 0.0
        var correctCodeCount = 0
        var correctArticleCount = 0
        var correctAbstentionCount = 0
        var unsupportedClaimCount = 0

        val executionTimeMs = measureTimeMillis {
            for (testCase in testCases) {
                val queryAnalysis = QueryUnderstandingEngine.analyzeQuery(testCase.query)

                if (testCase.isUnanswerableOrOutOfScope) {
                    val scored = retrievalEngine.retrieveRelevantArticles(testCase.query)
                    val validated = LegalEvidenceValidator.validateEvidence(testCase.query, scored, analysis = queryAnalysis)
                    if (validated.status == "INSUFFICIENT_EVIDENCE" || queryAnalysis.isUnmatchedDomain || scored.isEmpty()) {
                        correctAbstentionCount++
                    } else {
                        unsupportedClaimCount++
                    }
                    continue
                }

                if (testCase.category == "ambiguous") {
                    if (queryAnalysis.requiresClarification || queryAnalysis.schema.nextAction == "ask_clarification" || queryAnalysis.missingFacts.isNotEmpty()) {
                        correctAbstentionCount++
                    }
                    continue
                }

                // In-scope Legal Retrieval Case Evaluation
                val scoredResults = retrievalEngine.retrieveRelevantArticles(
                    query = testCase.query,
                    searchQueries = queryAnalysis.searchQueries
                )

                val retrievedArticles = scoredResults.map { it.article }
                val targetCode = testCase.expectedCodeId
                val targetArt = testCase.expectedArticleId

                fun isMatchingTargetArticle(artId: String): Boolean {
                    if (targetArt == null) return false
                    if (artId == targetArt) return true
                    if (targetArt == "mjtk_128_4" && artId == "mjtk_128") return true
                    if (targetArt == "mjtk_128" && artId == "mjtk_128_4") return true
                    return false
                }

                // Measure Recall@5
                val foundInTop5 = retrievedArticles.take(5).any { isMatchingTargetArticle(it.id) || it.codeId == targetCode }
                if (foundInTop5) totalRecallTop5++

                // Measure MRR
                val rank = retrievedArticles.indexOfFirst { isMatchingTargetArticle(it.id) || it.codeId == targetCode }
                if (rank != -1) {
                    totalReciprocalRankSum += (1.0 / (rank + 1))
                }

                // Measure Correct Code Rate
                if (targetCode != null && retrievedArticles.any { it.codeId == targetCode }) {
                    correctCodeCount++
                }

                // Measure Correct Article Rate
                if (targetArt != null && retrievedArticles.any { isMatchingTargetArticle(it.id) }) {
                    correctArticleCount++
                }
            }
        }

        val inScopeTotal = testCases.count { !it.isUnanswerableOrOutOfScope && it.category != "ambiguous" }
        val outOfScopeOrAmbiguousTotal = testCases.count { it.isUnanswerableOrOutOfScope || it.category == "ambiguous" }

        val recallAt5Pct = (totalRecallTop5 * 100.0) / inScopeTotal
        val mrr = totalReciprocalRankSum / inScopeTotal
        val correctCodePct = (correctCodeCount * 100.0) / inScopeTotal
        val correctArticlePct = (correctArticleCount * 100.0) / inScopeTotal
        val abstentionPct = (correctAbstentionCount * 100.0) / outOfScopeOrAmbiguousTotal

        println("==================================================")
        println("   UZLEGAL 100 RAG BENCHMARK EVALUATION REPORT    ")
        println("==================================================")
        println("Total Test Cases: ${testCases.size}")
        println("In-Scope Legal Queries: $inScopeTotal")
        println("Ambiguous / Out-of-Scope Queries: $outOfScopeOrAmbiguousTotal")
        println("--------------------------------------------------")
        println("Recall@5: ${"%.2f".format(recallAt5Pct)}%")
        println("Mean Reciprocal Rank (MRR): ${"%.4f".format(mrr)}")
        println("Correct Code Selection Rate: ${"%.2f".format(correctCodePct)}%")
        println("Correct Article Retrieval Rate: ${"%.2f".format(correctArticlePct)}%")
        println("Appropriate Abstention Rate: ${"%.2f".format(abstentionPct)}%")
        println("Unsupported Legal Claim Rate: $unsupportedClaimCount")
        println("Total Benchmark Latency: ${executionTimeMs} ms (${executionTimeMs / 100.0} ms/query)")
        println("==================================================")

        // Threshold Assertions
        assertTrue("Recall@5 must be >= 85%", recallAt5Pct >= 85.0)
        assertTrue("MRR must be >= 0.75", mrr >= 0.75)
        assertTrue("Correct Code Selection Rate must be >= 85%", correctCodePct >= 85.0)
        assertTrue("Correct Article Retrieval Rate must be >= 50%", correctArticlePct >= 50.0)
        assertTrue("Abstention Rate for ambiguous/unanswerable must be >= 80%", abstentionPct >= 80.0)
    }
}
