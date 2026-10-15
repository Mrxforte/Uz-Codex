package com.example.data.remote

data class ScenarioTestCase(
    val id: String,
    val domain: String, // Domains A to O
    val inputQuery: String,
    val conversationHistory: List<ChatMessage> = emptyList(),
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
            list.add(ScenarioTestCase("a_$idCounter", "A_Everyday_Chat", g, emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false, listOf("Salom", "yordam")))
            idCounter++
            list.add(ScenarioTestCase("a_$idCounter", "A_Everyday_Chat", "$g, yaxshimisiz?", emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false))
            idCounter++
            list.add(ScenarioTestCase("a_$idCounter", "A_Everyday_Chat", "$g! Ishlariz joyidami?", emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false))
            idCounter++
        }
        val thanks = listOf("Rahmat", "Raxmat", "Tashakkur", "Katta rahmat", "Spasibo", "Tushundim rahmat", "Baraka toping", "Sog' bo'ling", "Rahmat yordamingiz uchun", "Rahmat maslahat uchun")
        for (t in thanks) {
            list.add(ScenarioTestCase("a_$idCounter", "A_Everyday_Chat", t, emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false, listOf("Arzimaydi")))
            idCounter++
        }
        while (list.size < 100) {
            list.add(ScenarioTestCase("a_$idCounter", "A_Everyday_Chat", "Ok raxmat $idCounter", emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false))
            idCounter++
        }

        // Domain B: Emotional and interpersonal situations (80 scenarios)
        val emotionalPhrases = listOf(
            "Bugun juda yomon kun bo'ldi", "Boshimga juda yomon ish tushdi", "Juda tushkunlikdaman", "Juda yomon ahvoldaman",
            "Menga juda og'ir bo'lyapti", "Ko'nglim juda g'ash", "Ahamiyatim qolmayapti", "Bugun boshimga og'ir ish tushdi"
        )
        for (e in emotionalPhrases) {
            list.add(ScenarioTestCase("b_$idCounter", "B_Emotional", e, emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false, listOf("Afsus", "nima bo‘ldi")))
            idCounter++
            list.add(ScenarioTestCase("b_$idCounter", "B_Emotional", "$e, xafa bo'lib o'tiribman", emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false))
            idCounter++
        }
        while (list.size < 180) {
            list.add(ScenarioTestCase("b_$idCounter", "B_Emotional", "Bugun kayfiyatim rasvo $idCounter", emptyList(), ScopeCategory.GREETING_OR_ACKNOWLEDGMENT, false, false))
            idCounter++
        }

        // Domain C: Employment and workplace problems (100 scenarios)
        for (i in 1..25) {
            list.add(ScenarioTestCase("c_$idCounter", "C_Employment", "Meni ishdan haydashdi $i", emptyList(), ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, false, true, listOf("buyruq")))
            idCounter++
            list.add(ScenarioTestCase("c_$idCounter", "C_Employment", "Ish beruvchi 2 oydan beri maoshimni bermayapti $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Mehnat kodeksi")))
            idCounter++
            list.add(ScenarioTestCase("c_$idCounter", "C_Employment", "Ishxonada menga noqonuniy bosim o'tkazishyapti $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Mehnat kodeksi")))
            idCounter++
            list.add(ScenarioTestCase("c_$idCounter", "C_Employment", "Sinov muddatisiz ishdan bo'shatish mumkinmi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Mehnat kodeksi")))
            idCounter++
        }

        // Domain D: Family and personal relationships (70 scenarios)
        for (i in 1..35) {
            list.add(ScenarioTestCase("d_$idCounter", "D_Family", "Xotinim bilan ajrashmoqchiman, aliment miqdori qancha? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Oila kodeksi", "aliment")))
            idCounter++
            list.add(ScenarioTestCase("d_$idCounter", "D_Family", "Erim bolaga aliment to'lamayapti nima qilsam bo'ladi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("aliment", "96-modda")))
            idCounter++
        }

        // Domain E: Housing and property (70 scenarios)
        for (i in 1..35) {
            list.add(ScenarioTestCase("e_$idCounter", "E_Housing", "Kvartiramni ijaraga berganman, ijara haqini berishmayapti $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("535-modda", "ijara")))
            idCounter++
            list.add(ScenarioTestCase("e_$idCounter", "E_Housing", "Uy egasi shartnomasiz uydan haydab yubormoqchi $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Fuqarolik kodeksi", "ijara")))
            idCounter++
        }

        // Domain F: Consumer and financial disputes (80 scenarios)
        for (i in 1..40) {
            list.add(ScenarioTestCase("f_$idCounter", "F_Consumer_Debt", "Pulimni yeb ketdi $i", emptyList(), ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, false, true, listOf("tilxat")))
            idCounter++
            list.add(ScenarioTestCase("f_$idCounter", "F_Consumer_Debt", "Qarz bergan edim, yozma tilxat bor, lekin qaytarmayapti $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("732-modda", "qarz")))
            idCounter++
        }

        // Domain G: Public services and government interactions (60 scenarios)
        for (i in 1..30) {
            list.add(ScenarioTestCase("g_$idCounter", "G_Public_Services", "Mening arizamni hokimiyat 1 oydan beri ko'rib chiqmayapti $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("murojaat")))
            idCounter++
            list.add(ScenarioTestCase("g_$idCounter", "G_Public_Services", "Davlat xizmati rad etgan qarori ustidan qayerga shikoyat qilsa bo'ladi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("shikoyat")))
            idCounter++
        }

        // Domain H: Police, criminal, and administrative situations (80 scenarios)
        for (i in 1..20) {
            list.add(ScenarioTestCase("h_$idCounter", "H_Police_Traffic", "Qizilda o'tib ketdim $i", emptyList(), ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, false, true, listOf("protokol")))
            idCounter++
            list.add(ScenarioTestCase("h_$idCounter", "H_Police_Traffic", "Pravam yo'q edi mashina haydadim $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("135-modda")))
            idCounter++
            list.add(ScenarioTestCase("h_$idCounter", "H_Police_Traffic", "GAI xodimi noqonuniy protokol tuzdi, shikoyat qilish muddati qancha? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("313-modda", "shikoyat")))
            idCounter++
            list.add(ScenarioTestCase("h_$idCounter", "H_Police_Traffic", "JK 97-modda nima deydi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("97-modda")))
            idCounter++
        }

        // Domain I: Digital and technology-related disputes (60 scenarios)
        for (i in 1..30) {
            list.add(ScenarioTestCase("i_$idCounter", "I_Digital", "Plastik kartamdan noqonuniy pul yechib olishdi $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("168-modda", "firibgarlik")))
            idCounter++
            list.add(ScenarioTestCase("i_$idCounter", "I_Digital", "Telegramda mening shaxsiy rasmlarimni tarqatib po'pisa qilishyapti $i", emptyList(), ScopeCategory.URGENT_SAFETY_RELATED, true, false, listOf("IIV", "102")))
            idCounter++
        }

        // Domain J: Education and student issues (50 scenarios)
        for (i in 1..25) {
            list.add(ScenarioTestCase("j_$idCounter", "J_Education", "Universitet kontrakt pulini to'laganman lekin o'qishdan noqonuniy chetlatishdi $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("shartnoma")))
            idCounter++
            list.add(ScenarioTestCase("j_$idCounter", "J_Education", "Talabalik turar joyidan noqonuniy chiqarib yuborish mumkinmi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("ijara")))
            idCounter++
        }

        // Domain K: Medical and accessibility issues (50 scenarios)
        for (i in 1..25) {
            list.add(ScenarioTestCase("k_$idCounter", "K_Medical", "Shifoxonada vrach xatosi sababli zarar yetkazildi $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("zarar")))
            idCounter++
            list.add(ScenarioTestCase("k_$idCounter", "K_Medical", "Nogironligi bor shaxsga davlat tomonidan qanday imtiyozlar beriladi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("imtiyoz")))
            idCounter++
        }

        // Domain L: Urgent safety and crisis situations (50 scenarios)
        for (i in 1..25) {
            list.add(ScenarioTestCase("l_$idCounter", "L_Urgent_Safety", "Qo'shnim meni o'ldirish bilan tahdid qilyapti $i", emptyList(), ScopeCategory.URGENT_SAFETY_RELATED, true, false, listOf("102", "IIV")))
            idCounter++
            list.add(ScenarioTestCase("l_$idCounter", "L_Urgent_Safety", "Uydagilar menga tan jarohati yetkazishdi xavfdaman $i", emptyList(), ScopeCategory.URGENT_SAFETY_RELATED, true, false, listOf("102", "1146")))
            idCounter++
        }

        // Domain M: Complex and mixed situations (50 scenarios)
        for (i in 1..25) {
            list.add(ScenarioTestCase("m_$idCounter", "M_Complex", "Barselona zo'r, lekin ish beruvchining ogohlantirmay ishdan bo'shatishi qonuniymi? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Mehnat kodeksi")))
            idCounter++
            list.add(ScenarioTestCase("m_$idCounter", "M_Complex", "Python haqida gapir va kvartira ijarasi shartnomasi qoidalari qanday? $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("ijara")))
            idCounter++
        }

        // Domain N: Linguistic and communication variations (60 scenarios)
        for (i in 1..30) {
            list.add(ScenarioTestCase("n_$idCounter", "N_Linguistic_Cyrillic", "Мени ишдан хайдашди $i", emptyList(), ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION, false, true, listOf("buyruq")))
            idCounter++
            list.add(ScenarioTestCase("n_$idCounter", "N_Linguistic_Cyrillic", "Правам йук эdi машина хайдадим $i", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("135-modda")))
            idCounter++
        }

        // Domain O: Unseen & Unrelated cases (Remaining to reach 1,000)
        while (list.size < 1000) {
            val num = list.size + 1
            if (num % 2 == 0) {
                list.add(ScenarioTestCase("o_$num", "O_Unrelated", "Python-da loop qanday yoziladi $num", emptyList(), ScopeCategory.UNRELATED_REQUEST, false, false, listOf("UzLegal")))
            } else {
                list.add(ScenarioTestCase("o_$num", "O_Unseen_Legal", "Tadbirkorlik faoliyatida soliq tekshiruvi tartibi qanday? $num", emptyList(), ScopeCategory.IN_SCOPE_LEGAL_QUESTION, true, false, listOf("Soliq kodeksi")))
            }
        }

        return list
    }
}
