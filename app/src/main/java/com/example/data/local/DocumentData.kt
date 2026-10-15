package com.example.data.local

import com.example.data.model.DocumentTemplate
import com.example.data.model.TemplateField

object DocumentData {

    val TEMPLATES: List<DocumentTemplate> = listOf(
        DocumentTemplate(
            id = "doc_salary_claim",
            title = "Kechiktirilgan oylik maoshni to'lash to'g'risida ariza",
            category = "Mehnat munosabatlari",
            description = "Ish beruvchiga o'z vaqtida to'lanmagan oylik ish haqi va unga hisoblangan foizlarni (kompensatsiya) to'lash talabi bilan taqdim etiladigan rasmiy ariza.",
            statutoryBasis = "O'zbekiston Respublikasi Mehnat kodeksining 253-moddasi",
            fields = listOf(
                TemplateField("employer_name", "Ish beruvchi korxona/tashkilot nomi", "Masalan: 'Grand Tex' MChJ rahbari A. Karimovga"),
                TemplateField("employee_name", "Arizachi (Xodim) F.I.O.", "Masalan: Rustamov Sardor Olimovich"),
                TemplateField("employee_position", "Lavozimingiz", "Masalan: Bosh muhandis"),
                TemplateField("unpaid_months", "Qaysi oylar uchun maosh to'lanmagan", "Masalan: 2026-yil avgust, sentabr va oktabr oylari"),
                TemplateField("total_amount", "To'lanmagan jami ish haqi summasi (so'mda)", "Masalan: 18 500 000 so'm"),
                TemplateField("bank_card", "Ish haqi o'tkazilishi lozim bo'lgan bank karta raqami", "Masalan: 8600 0000 0000 0000")
            ),
            instructions = "Arizani ikki nusxada chop eting. Bir nusxasini korxona devonxonasiga (kantselyariyasiga) topshirib, ikkinchi nusxasiga kirish raqami va sana qo'ydirib oling."
        ),
        DocumentTemplate(
            id = "doc_consumer_complaint",
            title = "Nuqsonli tovar uchun pulni qaytarish haqida talabnoma",
            category = "Iste'molchilar huquqi",
            description = "Sotib olingan nuqsonli yoki sifatsiz tovar uchun to'langan pul mablag'ini qaytarib olish yoki tovarni almashtirish to'g'risida sotuvchiga da'vo xati.",
            statutoryBasis = "O'zbekiston Respublikasining 'Iste'molchilar huquqlarini himoya qilish to'g'risida'gi Qonunining 13, 14, 18-moddalari",
            fields = listOf(
                TemplateField("seller_name", "Sotuvchi (Do'kon yoki MChJ) nomi", "Masalan: 'Texnomart' savdo do'koni rahbariyatiga"),
                TemplateField("buyer_name", "Iste'molchi F.I.O. va telefon raqami", "Masalan: Aliyev Jamshid +998901234567"),
                TemplateField("product_name", "Tovar nomi va modeli", "Masalan: Smartfon Samsung Galaxy S24"),
                TemplateField("purchase_date", "Sotib olingan sana va chek raqami", "Masalan: 15.09.2026-yil, Chek №4528"),
                TemplateField("defect_details", "Aniqlangan nuqson va kamchiliklar tavsifi", "Masalan: Ekrani o'z-o'zidan o'chib qolmoqda va zaryad olmayapti", isMultiline = true),
                TemplateField("demand_type", "Talabingiz (Pulni qaytarish yoki Yangisiga almashtirish)", "Masalan: To'langan 9 200 000 so'm pulni to'liq qaytarish")
            ),
            instructions = "Xarid cheki nusxasini va kafolat talonini talabnomaga ilova qiling. Sotuvchi talabni 10 kun ichida qanoatlantirishi shart."
        ),
        DocumentTemplate(
            id = "doc_admin_appeal",
            title = "Ma'muriy jarima qarori ustidan sudga shikoyat",
            category = "Ma'muriy huquq",
            description = "Yo'l harakati xavfsizligi yoki boshqa davlat organi tomonidan asossiz chiqarilgan ma'muriy jazo qarorini bekor qilish to'g'risida sudga shikoyat.",
            statutoryBasis = "O'zbekiston Respublikasi Ma'muriy javobgarlik to'g'risidagi kodeksining 313, 314-moddalari",
            fields = listOf(
                TemplateField("court_name", "Shikoyat yuborilayotgan Jinoyat ishlari bo'yicha tuman sudi", "Masalan: Toshkent shahar Chilonzor tuman sudi"),
                TemplateField("applicant_info", "Arizachi F.I.O, yashash manzili va tel", "Masalan: Bekmurodov Dilshod, Chilonzor 9-mavze, 12-uy"),
                TemplateField("decision_number", "Shikoyat qilinayotgan qaror raqami va sanasi", "Masalan: № AA 1234567-sonli, 05.10.2026-yil"),
                TemplateField("authority_name", "Qaror chiqargan organ / mansabdor shaxs", "Masalan: Toshkent shahar IIBB YHXB inspektori"),
                TemplateField("grounds", "Qaror nima uchun asossiz deb hisoblanadi (Dalillar)", "Masalan: Tezlikni oshirish vaqtida avtomobil belgilangan chegarada harakatlangan, radar ma'lumotlarida vaqt noaniq.", isMultiline = true)
            ),
            instructions = "Qaror nusxasi topshirilgan kundan boshlab 10 kunlik muddatni o'tkazib yubormaslik shart."
        ),
        DocumentTemplate(
            id = "doc_foi_request",
            title = "Davlat organiga axborot olish uchun rasmiy so'rovnoma",
            category = "Ommaviy axborot va murojaat",
            description = "Fuqarolarning davlat organlaridan o'z huquq va manfaatlariga daxldor bo'lgan axborotlarni olish huquqini amalga oshirish.",
            statutoryBasis = "'Davlat hokimiyati va boshqaruvi organlari faoliyatining ochiqligi to'g'risida'gi Qonun",
            fields = listOf(
                TemplateField("agency_name", "Davlat organi nomi", "Masalan: Samarqand viloyati Qurilish boshqarmasiga"),
                TemplateField("applicant_name", "Murojaatchi F.I.O.", "Masalan: Toshpulatov Anvar"),
                TemplateField("request_topic", "So'ralayotgan ma'lumot va hujjat mazmuni", "Masalan: Hududdagi yangi turar-joy qurilishi bo'yicha berilgan ruxsatnoma va ekologik xulosa nusxasi", isMultiline = true)
            ),
            instructions = "Davlat organi axborot so'roviga 15 kun muddat ichida yozma yoki elektron shaklda javob berishi qonunan shart."
        )
    )

    fun getTemplateById(id: String): DocumentTemplate? = TEMPLATES.find { it.id == id }

    fun generateFormattedDocument(template: DocumentTemplate, values: Map<String, String>): String {
        val dateStr = "2026-yil 15-oktabr"
        return buildString {
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("           RASMIY HUJJAT LOYIHASI")
            appendLine("         ${template.title.uppercase()}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

            when (template.id) {
                "doc_salary_claim" -> {
                    appendLine("Kimga: ${values["employer_name"] ?: "________________"}")
                    appendLine("Kimdan: ${values["employee_position"] ?: "xodim"} — ${values["employee_name"] ?: "________________"}\n")
                    appendLine("                            ARIZA\n")
                    appendLine("   Men O'zbekiston Respublikasi Mehnat kodeksining 253-moddasiga asoslanib ma'lum qilamanki, menga quyidagi davr uchun qonuniy ish haqi to'lanmasdan qolmoqda:")
                    appendLine("   Davr: ${values["unpaid_months"] ?: "________________"}")
                    appendLine("   To'lanishi lozim bo'lgan jami qarz: ${values["total_amount"] ?: "0"} so'm.")
                    appendLine("\n   Mehnat kodeksining 253-moddasiga asosan, ish beruvchi tomonidan ish haqi belgilangan muddatdan kechiktirilgan taqdirda, ish beruvchi xodimga har bir kechiktirilgan kun uchun Markaziy bank qayta moliyalash stavkasi miqdorida kompensatsiya to'lashi shart.")
                    appendLine("\n   Yuqoridagilardan kelib chiqib, SIZDAN:")
                    appendLine("   Menga tegishli bo'lgan ish haqi qarzini hamda kechiktirilgan har bir kun uchun hisoblangan qonuniy foizlarni quyidagi bank hisobvarag'imga zudlik bilan to'lab berishingizni so'rayman.")
                    appendLine("   Karta raqami: ${values["bank_card"] ?: "________________"}")
                    appendLine("\n   Aks holda, qonuniy huquqlarimni himoya qilish maqsadida Davlat mehnat inspeksiyasiga hamda Fuqarolik ishlari bo'yicha tumanlararo sudiga murojaat qilishga majbur bo'laman.")
                }
                "doc_consumer_complaint" -> {
                    appendLine("Kimga: ${values["seller_name"] ?: "________________"}")
                    appendLine("Iste'molchi: ${values["buyer_name"] ?: "________________"}\n")
                    appendLine("                         TALABNOMA\n")
                    appendLine("   Men ${values["purchase_date"] ?: "___"} kuni Sizning savdo tarmog'ingizdan ${values["product_name"] ?: "tovar"} xarid qilgan edim.")
                    appendLine("   Foydalanish jarayonida quyidagi jiddiy nuqsonlar aniqlandi:")
                    appendLine("   ${values["defect_details"] ?: "Nuqsonlar aniqlangan"}")
                    appendLine("\n   'Iste'molchilar huquqlarini himoya qilish to'g'risida'gi Qonunning 13-moddasiga asosan nuqsonli tovar sotilganda iste'molchi shartnomani bekor qilish va to'langan pulni qaytarib olishga haqli.")
                    appendLine("\n   Talabim: ${values["demand_type"] ?: "Pulni to'liq qaytarish"}")
                }
                else -> {
                    values.forEach { (k, v) ->
                        appendLine("${template.fields.find { it.key == k }?.label ?: k}: $v")
                    }
                    appendLine("\nQonuniy asos: ${template.statutoryBasis}")
                }
            }

            appendLine("\n\nSana: $dateStr")
            appendLine("Imzo: _____________________")
            appendLine("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("Eslatma: Ushbu hujjat 'O'zbekiston Huquqi' tizimi orqali avtomatik shakllantirildi.")
        }
    }
}
