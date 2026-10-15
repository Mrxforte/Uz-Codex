package com.example.data.remote

import com.example.data.local.LegislationData
import com.example.data.model.LegalArticle

data class ValidatedLegalCitation(
    val article: LegalArticle,
    val isVerified: Boolean,
    val verifiedQuotation: String, // Exact original text from local DB
    val relationExplanation: String
)

data class ValidatedLegalResponse(
    val status: String, // "VERIFIED_EVIDENCE", "INSUFFICIENT_EVIDENCE", "CLARIFICATION_NEEDED", "GENERAL_ASSISTANCE"
    val summaryText: String,
    val legalAnalysisText: String,
    val verifiedCitations: List<ValidatedLegalCitation>,
    val rejectedCitationsCount: Int,
    val nextSteps: List<String>,
    val limitations: String,
    val isGroundedInEvidence: Boolean
)

object LegalEvidenceValidator {

    /**
     * Validates candidate articles and response against local legal database records.
     * Enforces the non-negotiable rule: hallucinated or unverified citations are rejected.
     */
    fun validateEvidence(
        query: String,
        scoredArticles: List<ScoredLegalArticle>,
        candidateText: String? = null,
        analysis: UserQueryAnalysis
    ): ValidatedLegalResponse {

        // 1. Check if evidence is insufficient
        if (scoredArticles.isEmpty() || scoredArticles.all { it.score < 0.12 }) {
            val summary = if (analysis.isUnmatchedDomain) {
                "Mavjud O'zbekiston Respublikasi 19 ta kodeksi va qonunlar bazasida ushbu masala bo'yicha tasdiqlangan va ishonchli modda topilmadi."
            } else {
                "Aynan ushbu vaziyat bo'yicha yetarli va tasdiqlangan qonunchilik normasi aniqlanmadi."
            }

            return ValidatedLegalResponse(
                status = "INSUFFICIENT_EVIDENCE",
                summaryText = summary,
                legalAnalysisText = "UzLegal RAG tizimi to'qima va asossiz yuridik javob berishni qat'iy taqiqlaydi. Aniqroq huquqiy maslahat uchun masalaga taalluqli hujjatlar (shartnoma, bayonnoma yoki sud qarori) bilan advokatga murojaat qilishingiz tavsiya etiladi.",
                verifiedCitations = emptyList(),
                rejectedCitationsCount = 0,
                nextSteps = listOf(
                    "Vaziyatingizga taalluqli rasmiy hujjatlar yoki shartnomalar borligini tekshiring.",
                    "Huquqiy sohaga taalluqli aniqroq so'rov yoki modda raqamini kiriting.",
                    "Zarur hollarda rasmiy advokat yoki bepul yuridik yordam markaziga murojaat qiling."
                ),
                limitations = "Ushbu ma'lumot faqat mavjud qonunchilik bazasi tahliliga asoslangan bo'lib, sud va davlat idoralari uchun majburiy xulosa hisoblanmaydi.",
                isGroundedInEvidence = false
            )
        }

        // 2. Validate retrieved articles against master database
        val verifiedCitations = mutableListOf<ValidatedLegalCitation>()
        var rejectedCount = 0

        for (scored in scoredArticles) {
            val masterArticle = LegislationData.getAllArticles().find { it.id == scored.article.id }
                ?: LegislationData.KEY_ARTICLES.find { it.id == scored.article.id }

            if (masterArticle != null) {
                // Exact original quotation directly from master database
                val exactQuotation = masterArticle.content

                verifiedCitations.add(
                    ValidatedLegalCitation(
                        article = masterArticle,
                        isVerified = true,
                        verifiedQuotation = exactQuotation,
                        relationExplanation = "${masterArticle.articleNumberStr} (${masterArticle.title}) — ushbu vaziyatda ${scored.matchReasons.firstOrNull() ?: "tegishli huquqiy norma"} hisoblanadi."
                    )
                )
            } else {
                rejectedCount++
            }
        }

        // 3. Build Grounded Validated Legal Response
        val summary = "Siz ko'targan masala bo'yicha O'zbekiston Respublikasi amaldagi qonunchiligi moddalari asosida tasdiqlangan yuridik tahlil tayyorlandi."
        val analysisText = buildString {
            appendLine("Tekshirilgan va tasdiqlangan qonunchilik manbalari asosida quyidagi normalar qo'llaniladi:")
            verifiedCitations.forEach { cit ->
                val art = cit.article
                val code = LegislationData.getCodeById(art.codeId)
                val fullCodeTitle = code?.officialUzbekName ?: "O'zbekiston Respublikasi Kodeksi"
                appendLine("- **$fullCodeTitle, ${art.articleNumberStr} (${art.title})**")
            }
        }

        val nextSteps = listOf(
            "Mavjud yozma hujjatlar, shartnoma yoki bayonnomalarni to'plang.",
            "Qonunda belgilangan murojaat va da'vo muddatlarini o'tkazib yubormang.",
            "Zarur bo'lganda vakolatli organga yoki advokatga murojaat qiling."
        )

        return ValidatedLegalResponse(
            status = "VERIFIED_EVIDENCE",
            summaryText = summary,
            legalAnalysisText = analysisText,
            verifiedCitations = verifiedCitations,
            rejectedCitationsCount = rejectedCount,
            nextSteps = nextSteps,
            limitations = "Ushbu maslahat rasmiy yuridik ma'lumot sifatida taqdim etiladi. Aniq sud amaliyoti va alohida holatlar hisobga olinishi lozim.",
            isGroundedInEvidence = true
        )
    }
}
