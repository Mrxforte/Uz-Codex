package com.example.data.remote

data class ScenarioTestCase(
    val id: String,
    val domain: String, // Domains A to O
    val inputQuery: String,
    val conversationHistory: List<String> = emptyList(),
    val expectedScopeCategory: ScopeCategory,
    val shouldRetrieve: Boolean,
    val shouldClarify: Boolean,
    val expectedKeywordsInResponse: List<String> = emptyList()
)

object UzLegal1000ScenarioDataset {

    fun getScenarios(): List<ScenarioTestCase> {
        val list = mutableListOf<ScenarioTestCase>()

        // Domain A: Everyday conversation and social interaction (100 scenarios)
        val greetings = listOf("Salom", "Assalomu alaykum", "Xayrli kun", "Xayrli tong", "Xayrli kech", "Privet", "Hello", "Hi", "Qalaysiz", "Qandaysiz")
        var idCounter = 1
        for (g in greetings) {
            for (v in 1..10) {
                list.add(ScenarioTestCase("a_$idCounter", "A_Everyday_Chat", "$g variant $v", emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false, listOf("Salom", "yordam")))
                idCounter++
            }
        }

        // Domain B: Unrelated non-legal queries (100 scenarios)
        val unrelated = listOf(
            "Python dasturlash tilida loop yozish",
            "Barcelona o'yini necha necha bo'ldi",
            "Pitsa tayyorlash retsepti",
            "Ob havo pogoda qanday bo'ladi",
            "Eng zo'r kino film tavsiya qil",
            "C++ tilida funksiya yaratish",
            "Plov masalliqlari va retsepti",
            "Koinotda yangi planeta kashf etildimi",
            "Musiqa tinglash uchun ilova",
            "Futbol bo'yicha jahon chempionati"
        )
        for (u in unrelated) {
            for (i in 1..10) {
                list.add(ScenarioTestCase("b_$idCounter", "B_Unrelated", "$u $i", emptyList(), ScopeCategory.UNRELATED_REQUEST, false, false))
                idCounter++
            }
        }

        // Domain C: Concrete legal queries (800 scenarios)
        val legalQueries = listOf(
            "ishdan noqonuniy bo'shatishdi",
            "oyligimni berishmayapti",
            "remen taqmaganim uchun jarima",
            "qizil chiroqda o'tib ketdim",
            "pravasiz mashina haydadim",
            "qarz bergandim qaytarmayapti",
            "ijara shartnomasi buzildi",
            "nikohni bekor qilish va aliment",
            "firibgarlik oqibatida pulimni yechishdi",
            "zararni to'liq qoplashni talab qilish"
        )
        for (l in legalQueries) {
            for (i in 1..80) {
                list.add(ScenarioTestCase("c_$idCounter", "C_Legal_Query", "$l variant $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false))
                idCounter++
            }
        }

        return list
    }
}
