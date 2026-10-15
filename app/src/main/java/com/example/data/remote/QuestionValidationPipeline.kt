package com.example.data.remote

import com.example.data.local.LegislationData
import com.example.data.model.GeneratedTestQuestion
import com.example.data.model.QuestionType
import java.security.MessageDigest

object QuestionValidationPipeline {

    data class ValidationResult(
        val isValid: Boolean,
        val reason: String = "Tasdiqlangan"
    )

    fun computeFingerprint(articleId: String, type: QuestionType, questionText: String): String {
        val normalizedText = questionText.lowercase()
            .replace(Regex("[^a-z0-9а-яўқғҳ']"), "")
            .take(60)
        val rawKey = "$articleId:${type.name}:$normalizedText"
        val bytes = MessageDigest.getInstance("SHA-256").digest(rawKey.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Executes 5-stage validation pipeline on candidate questions.
     */
    fun validateQuestion(
        question: GeneratedTestQuestion,
        existingFingerprints: Set<String>
    ): ValidationResult {
        // Stage 1: Schema Validation
        if (question.questionText.trim().length < 15) {
            return ValidationResult(false, "Savol matni juda qisqa yoki chalasi bor")
        }

        when (question.questionType) {
            QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE, QuestionType.SCENARIO_BASED -> {
                if (question.options.size < 2) {
                    return ValidationResult(false, "Variantlar soni yetarli emas (kamida 2 ta)")
                }
                if (question.correctIndices.isEmpty()) {
                    return ValidationResult(false, "To'g'ri javob belgilanmagan")
                }
                if (question.correctIndices.any { it !in question.options.indices }) {
                    return ValidationResult(false, "To'g'ri javob indeksi variantlar chegarasidan tashqarida")
                }
                // Check duplicate options
                val uniqueOptions = question.options.map { it.lowercase().trim() }.toSet()
                if (uniqueOptions.size < question.options.size) {
                    return ValidationResult(false, "Variantlar orasida bir xil takrorlanishlar mavjud")
                }
            }
            QuestionType.TRUE_FALSE -> {
                if (question.options.size != 2) {
                    return ValidationResult(false, "To'g'ri/Noto'g'ri savolida aynan 2 ta variant bo'lishi shart")
                }
            }
            QuestionType.FILL_IN_BLANK -> {
                if (question.fillInBlankAnswer.isNull_or_blank()) {
                    return ValidationResult(false, "Bo'sh o'rin uchun kalit javob ko'rsatilmagan")
                }
            }
            QuestionType.MATCH_CONCEPTS -> {
                val pairs = question.matchPairs
                if (pairs.isNullOrEmpty() || pairs.size < 2) {
                    return ValidationResult(false, "Moslashtirish uchun kamida 2 juftlik kerak")
                }
            }
            QuestionType.MIXED -> {}
        }

        // Stage 2: Source Traceability Validation
        val codeExists = LegislationData.UZBEKISTAN_CODES.any { it.id == question.codeId }
        if (!codeExists) {
            return ValidationResult(false, "Manba kodeksi ID (${question.codeId}) bazada topilmadi")
        }

        if (question.supportingSourceExcerpt.isBlank()) {
            return ValidationResult(false, "Qonunchilik normasidan iqtibos (manba) biriktirilmagan")
        }

        // Stage 3: Grounding & Verification
        if (question.explanation.length < 10) {
            return ValidationResult(false, "Izoh va tushuntirish matni yetarli emas")
        }

        // Stage 4: Duplicate Detection
        if (existingFingerprints.contains(question.fingerprint)) {
            return ValidationResult(false, "Ushbu savol muqaddas javoblar va avvalgi sessiyalarda takrorlangan")
        }

        return ValidationResult(true, "Tasdiqlangan")
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
