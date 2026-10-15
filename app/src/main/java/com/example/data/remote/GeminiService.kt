package com.example.data.remote

import android.content.Context
import com.example.data.local.LegislationData
import com.example.data.model.LegalArticle

data class ChatMessage(
    val id: String,
    val sender: String, // "user", "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val citedArticles: List<LegalArticle> = emptyList(),
    val isGrounded: Boolean = true,
    val likedState: Boolean? = null // null = none, true = liked, false = disliked
)

object GeminiLegalAssistant {

    @Volatile
    private var structuredRagService: StructuredGeminiRagService? = null

    private fun getRagService(context: Context): StructuredGeminiRagService {
        return structuredRagService ?: synchronized(this) {
            structuredRagService ?: StructuredGeminiRagService(context.applicationContext).also {
                structuredRagService = it
            }
        }
    }

    suspend fun askLegalQuestion(
        context: Context,
        userQuery: String,
        conversationHistory: List<ChatMessage>
    ): ChatMessage {
        val service = getRagService(context)
        return service.processRagQuery(userQuery, conversationHistory)
    }

    // Overload for backward compatibility
    suspend fun askLegalQuestion(
        userQuery: String,
        conversationHistory: List<ChatMessage>
    ): ChatMessage {
        val analysis = QueryUnderstandingEngine.analyzeQuery(userQuery, conversationHistory)
        val relevantArticles: List<LegalArticle> = if (analysis.schema.needsLegalRetrieval && !analysis.isUnmatchedDomain) {
            LegislationData.searchArticlesForQueries(analysis.searchQueries)
        } else emptyList()

        val text = generateLocalGroundedAnswer(userQuery, relevantArticles, null, analysis)
        return ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "assistant",
            text = text,
            citedArticles = relevantArticles,
            isGrounded = relevantArticles.isNotEmpty()
        )
    }

    fun generateLocalGroundedAnswer(
        query: String,
        articles: List<LegalArticle>,
        userName: String?,
        analysis: UserQueryAnalysis
    ): String {
        val namePrefix = if (userName != null) "$userName, " else ""

        if (analysis.schema.nextAction == "provide_communication_advice" || !analysis.conversationState.isLitigationDesired) {
            return buildString {
                appendLine("Tushundim. Sudga murojaat qilmasdan, masala va e'tirozingizni ish beruvchi yoki ikkinchi tarafga madaniy va asosli yetkazish uchun quyidagi qadamlarni qo'llashingiz mumkin:")
                appendLine()
                appendLine("1. **Yozma e'tiroz yoki ariza tayyorlang**: Holat va talabingizni rasmiy yozma shaklda (mehnat kodeksi va shartnoma shartlariga tayanib) bayon qiling.")
                appendLine("2. **Muzokara o'tkazing**: Ish beruvchi yoki mas'ul rahbar bilan uchrashib, muammoni o'zaro kelishuv yo'li bilan hal etishni taklif eting.")
                appendLine("3. **Hujjat nusxasini saqlang**: Topshirilgan ariza yoki xatning qabul qilinganligi haqida belgi (shtamp yoki pochta kvitansiyasi) qo'ydirib oling.")
            }
        }

        if (analysis.schema.nextAction == "acknowledge_and_listen" || analysis.isEmotionalSharing) {
            return "Afsus, bugun siz uchun og‘ir o‘tganga o‘xshaydi. Nima bo‘ldi?"
        }

        if (analysis.schema.nextAction == "respond_naturally" || analysis.scopeCategory == ScopeCategory.GREETING_OR_ACKNOWLEDGMENT) {
            val norm = analysis.normalizedQuery
            if (norm.contains("rahmat") || norm.contains("raxmat") || norm.contains("tashakkur") || norm.contains("spasibo") || norm.contains("arzimaydi")) {
                return "Arzimaydi! Yana savolingiz bo‘lsa, bemalol yozing."
            }
            return if (userName != null) {
                "Va alaykum assalom, $userName! Sizga qanday yordam bera olaman?"
            } else {
                "Va alaykum assalom! Sizga qanday yordam bera olaman?"
            }
        }

        if (analysis.schema.nextAction == "polite_redirect" || analysis.scopeCategory == ScopeCategory.UNRELATED_REQUEST) {
            return "Men UzLegal — O‘zbekiston qonunchiligi va huquqiy masalalar bo‘yicha yordamchiman. Huquqiy savolingiz yoki hal qilmoqchi bo‘lgan vaziyatingiz bo‘lsa, yordam beraman."
        }

        if (analysis.isUnmatchedDomain) {
            return buildString {
                appendLine("### Vaziyatingiz")
                appendLine("${namePrefix}Mavjud O'zbekiston Respublikasi kodekslari va qonunlari bazasida ushbu masala bo'yicha tasdiqlangan modda topilmadi.")
                appendLine()
                appendLine("### Huquqiy tahlil")
                appendLine("Tizimimizdagi qonunchilik bazasi hozirda Fuqarolik, Jinoyat, Mehnat, Oila, Soliq, Uy-joy va Ma'muriy huquq sohalarini qamrab olgan. Ushbu maxsus masala bo'yicha O'zbekiston amaldagi qonunchiligida maxsus norma mavjud emas yoki qo'shimcha tadqiqot talab etiladi.")
            }
        }

        if (analysis.schema.nextAction == "safety_guidance" || analysis.scopeCategory == ScopeCategory.URGENT_SAFETY_RELATED) {
            if (analysis.isSeriousDisclosure) {
                return buildString {
                    appendLine("### Vaziyatingiz")
                    appendLine("${namePrefix}Siz nomusga tegish bilan bog'liq juda jiddiy va og'ir jinoyat-huquqiy holat haqida ma'lumot so'ramoqdasiz. Bu inson xavfsizligi va daxlsizligiga oid masaladir.")
                    appendLine()

                    appendLine("### Huquqiy tahlil")
                    appendLine("O'zbekiston Respublikasi Jinoyat kodeksining 118-moddasiga (Nomusga tegish) muvofiq, kuch ishlatib, do'q-po'pisa qilib yoki jabrlanuvchining ojizligidan foydalanib jinsiy ehtiyojni qondirish og'ir jinoyat hisoblanadi. Jinoyat-protsessual kodeksining 23-moddasiga ko'ra aybsizlik prezumpsiyasi amal qiladi va har bir shaxs malakali advokat himoyasiga huquqlidir.")
                    appendLine()

                    appendLine("### Tegishli qonunlar")
                    appendLine("1. **Jinoyat kodeksi, 118-modda (Nomusga tegish)**")
                    appendLine("2. **Jinoyat-protsessual kodeksi, 23-modda (Aybsizlik prezumpsiyasi)**")
                    appendLine("3. **Jinoyat-protsessual kodeksi, 50-modda (Himoyachining ishtiroki)**")
                    appendLine()

                    appendLine("### Mumkin bo‘lgan oqibatlar")
                    appendLine("Jinoyat kodeksining 118-moddasi bo'yicha aybdor deb topilgan shaxsga nisbatan qonunda og'ir ozodlikdan mahrum qilish jazosi nazarda tutilgan. Aniq jazo muddati sud tomonidan ish holatlarini o'rganib chiqqan holda belgilanadi.")
                    appendLine()

                    appendLine("### Nima qilish kerak?")
                    appendLine("1. **Jabrlangan shaxs xavfsizligini ta'minlang**: Biron-bir ko'rinishda tazyiq o'tkazish, tahdid qilish yoki muloqotga kirishish taqiqlanadi.")
                    appendLine("2. **Malakali advokatga murojaat qiling**: Zudlik bilan rasmiy advokat (himoyachi) yollang va barcha harakatlarni advokat ishtirokida amalga oshiring.")
                    appendLine("3. **Dalillarni yo'qotishga urinmang**: Dalillarni yo'qotish yoki guvohlarga ta'sir o'tkazish javobgarlikni og'irlashtiradi.")
                    appendLine()

                    appendLine("### Muhim eslatma")
                    appendLine("Ushbu ma'lumot faqat huquqiy tushuntirish berish maqsadida taqdim etildi. Qonuniy tartib va adolatni ta'minlash uchun advokat va huquqni muhofaza qiluvchi organlar bilan qonuniy hamkorlik qiling.")
                }
            } else {
                return buildString {
                    appendLine("Xavotiringizni tushunaman, bu juda jiddiy vaziyat. Birinchi navbatda o'zingiz va yaqinlaringiz xavfsizligini ta'minlash muhim.")
                    appendLine()
                    appendLine("**Zudlik bilan quyidagi ishonch xizmatlariga murojaat qilishingiz mumkin:**")
                    appendLine("- 🚔 **102** — IIV / Militsiya")
                    appendLine("- 🚨 **112** — FVV Qutqaruv xizmati")
                    appendLine("- 📞 **1146** — Tazyiq va zo'ravonlikdan jabrlangan xotin-qizlar uchun Ishonch telefoni")
                    appendLine("- ⚖️ **1102** — Prokuratura ishonch telefoni")
                }
            }
        }

        if (analysis.schema.nextAction == "ask_clarification" || analysis.scopeCategory == ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION) {
            return analysis.clarificationQuestion ?: "Sizga amaliy yordam berishim uchun vaziyatingiz haqida biroz aniqroq ma'lumot bera olasizmi?"
        }

        return buildString {
            appendLine("### Vaziyatingiz")
            when {
                analysis.detectedIssues.contains("tenancy_dispute") && analysis.detectedIssues.contains("salary_delay") -> {
                    appendLine("${namePrefix}Siz ikkita alohida huquqiy masala: kvartira ijarasi bo'yicha to'lov undirish va ish joyingizda ish haqining kechiktirilishi bo'yicha maslahat so'ramoqdasiz.")
                }
                analysis.detectedIssues.contains("red_light_violation") && analysis.detectedIssues.contains("seat_belt_violation") -> {
                    appendLine("${namePrefix}Siz ikkita alohida yo'l harakati qoidabuzarligi: xavfsizlik kamarini taqmaslik va svetoforning taqiqlovchi (qizil) ishorasiga bo'ysunmaslik bo'yicha maslahat so'ramoqdasiz.")
                }
                analysis.detectedIssues.contains("driving_without_license") -> {
                    appendLine("${namePrefix}Siz haydovchilik guvohnomasisiz (pravasiz) transport vositasini boshqarganlik yoki guvohnoma yonida bo'lmaganlik bo'yicha maslahat so'ramoqdasiz.")
                }
                analysis.detectedIssues.contains("unlawful_dismissal") -> {
                    appendLine("${namePrefix}Siz ish joyingizdan asossiz yoki noqonuniy bo'shatilganligingiz bo'yicha huquqiy yordam so'ramoqdasiz.")
                }
                analysis.detectedIssues.contains("debt_recovery") -> {
                    appendLine("${namePrefix}Siz berilgan qarz pulini qaytarib ololmayotganligingiz va qarz majburiyatlari bo'yicha maslahat so'ramoqdasiz.")
                }
                analysis.detectedIssues.contains("red_light_violation") -> {
                    appendLine("${namePrefix}Siz svetoforning taqiqlovchi (qizil) ishorasida o'tganlik bo'yicha YHXX protokoli va ma'muriy jarima haqida maslahat so'ramoqdasiz.")
                }
                else -> {
                    appendLine("${namePrefix}Siz ko'targan masala bo'yicha O'zbekiston Respublikasi amaldagi qonunchiligi tahlil qilindi.")
                }
            }
            appendLine()

            appendLine("### Huquqiy tahlil")
            when {
                analysis.detectedIssues.contains("red_light_violation") -> {
                    appendLine("Ma'muriy javobgarlik to'g'risidagi kodeksning 128-4-moddasiga muvofiq, svetoforning taqiqlovchi (qizil) ishorasiga bo'ysunmasdan o'tish ma'muriy jarima solishga sabab bo'ladi. Bir yil davomida takroran sodir etilsa, jarima miqdori BHMning 5 baravarigacha oshadi.")
                }
                analysis.detectedIssues.contains("driving_without_license") -> {
                    appendLine("Ma'muriy javobgarlik to'g'risidagi kodeksning 135-moddasiga binoan haydovchilik guvohnomasisiz (pravasiz) transport vositasini boshqarish ma'muriy huquqbuzarlik hisoblanadi.")
                }
                analysis.detectedIssues.contains("unlawful_dismissal") -> {
                    appendLine("Mehnat kodeksining 161-moddasiga muvofiq, ish beruvchi xodimni faqat Mehnat kodeksida nazarda tutilgan asoslar bo'yicha bo'shatishi mumkin.")
                }
                analysis.detectedIssues.contains("salary_delay") -> {
                    appendLine("Mehnat kodeksining 253-moddasiga muvofiq, ish beruvchi ish haqini o'z vaqtida to'lashi shart.")
                }
                else -> {
                    appendLine("Tekshirilgan va tasdiqlangan qonunchilik manbalari asosida tegishli normalar qo'llaniladi.")
                }
            }
            appendLine()

            appendLine("### Tegishli qonunlar")
            if (articles.isNotEmpty()) {
                articles.forEachIndexed { idx, art ->
                    val fullTitle = LegislationData.getCodeById(art.codeId)?.officialUzbekName ?: "O'zbekiston Qonuni"
                    appendLine("${idx + 1}. **$fullTitle, ${art.articleNumberStr} (${art.title})**")
                    appendLine("   \"${art.content.take(220)}...\"")
                }
            } else {
                appendLine("Aynan ushbu masala bo'yicha tasdiqlangan va ishonchli modda topilmadi.")
            }
            appendLine()

            appendLine("### Nima qilish kerak?")
            appendLine("1. Barcha yozma hujjatlar, tilxat, shartnoma yoki bayonnomalarni to'plang.")
            appendLine("2. Vakolatli davlat idorasiga yoki sudga rasmiy murojaat qiling.")
            appendLine("3. Zarurat bo'lganda malakali advokat xizmatidan foydalaning.")
            appendLine()

            appendLine("### Muhim eslatma")
            appendLine("Ushbu ma'lumot rasmiy huquqiy tushuntirish berish maqsadida taqdim etildi. Jarimalar bo'yicha 15 kun ichida 50% chegirma bilan to'lash imkoniyati mavjud.")
        }
    }
}
