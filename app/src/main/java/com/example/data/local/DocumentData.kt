package com.example.data.local

import com.example.data.model.DocumentTemplate
import com.example.data.model.TemplateField

object DocumentData {

    val TEMPLATES: List<DocumentTemplate> = listOf(
        // --- MEHNAT MUNOSABATLARI ---
        DocumentTemplate(
            id = "doc_salary_claim",
            title = "Kechiktirilgan oylik maoshni to'lash to'g'risida ariza",
            category = "Mehnat",
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
            id = "doc_leave_request",
            title = "Yillik mehnat ta'tili berish haqida ariza",
            category = "Mehnat",
            description = "Xodim tomonidan har yilgi pullik asosiy mehnat ta'tiliga chiqish uchun ish beruvchi nomiga yoziladigan rasmiy ariza.",
            statutoryBasis = "O'zbekiston Respublikasi Mehnat kodeksining 216, 217-moddalari",
            fields = listOf(
                TemplateField("employer_title", "Tashkilot nomi va rahbari F.I.O.", "Masalan: 'Oazis' MChJ direktori N. Zokirovga"),
                TemplateField("employee_title", "Arizachining lavozimi va F.I.O.", "Masalan: Buxgalter Sobirova Madina"),
                TemplateField("leave_days", "Ta'til kuni soni (kalendar kuni)", "Masalan: 21 kalendar kuni"),
                TemplateField("start_date", "Ta me'yoriy boshlanish sanasi", "Masalan: 2026-yil 1-iyuldan")
            ),
            instructions = "Ta'til jadvaliga muvofiq ta'til boshlanishidan kamida 14 kun oldin topshirilishi tavsiya etiladi."
        ),
        DocumentTemplate(
            id = "doc_resignation_request",
            title = "O'z xohishiga ko'ra ishdan bo'shash haqida ariza",
            category = "Mehnat",
            description = "Xodim tomonidan mehnat shartnomasini o'z tashabbusi bilan bekor qilish to'g'risida beriladigan yozma ogohlantirish arizasi.",
            statutoryBasis = "O'zbekiston Respublikasi Mehnat kodeksining 160-moddasi",
            fields = listOf(
                TemplateField("employer_title", "Tashkilot va rahbar nomi", "Masalan: 'Navqiron' MChJ direktori Sh. Umarovga"),
                TemplateField("employee_title", "Xodimning lavozimi va F.I.O.", "Masalan: Sotuvchi-konsultant Karimov Bobur"),
                TemplateField("last_work_day", "Oxirgi ish kuni sanasi", "Masalan: 2026-yil 30-oktabr")
            ),
            instructions = "Xodim ish beruvchini 14 kalendar kun oldin yozma ravishda ogohlantirishi shart."
        ),

        // --- OILA HUQUQI ---
        DocumentTemplate(
            id = "doc_alimony_claim",
            title = "Voyaga yetmagan bola uchun aliment undirish haqida da'vo ariza",
            category = "Oila",
            description = "Ota yoki onadan voyaga yetmagan farzandlar ta'minoti uchun sud tartibida har oylik aliment undirish to'g'risida fuqarolik sudiga da'vo ariza.",
            statutoryBasis = "O'zbekiston Respublikasi Oila kodeksining 96, 99-moddalari",
            fields = listOf(
                TemplateField("court_name", "Fuqarolik ishlari bo'yicha tumanlararo sudi nomi", "Masalan: Fuqarolik ishlari bo'yicha Yakkasaroy tumanlararo sudiga"),
                TemplateField("plaintiff_info", "Da'vogar (Ona/Ota) F.I.O., manzili va teli", "Masalan: Ismoilova Nigora Akmalovna, Toshkent sh. Chilonzor 5-12, tel: 901234567"),
                TemplateField("defendant_info", "Javobgar F.I.O., ish joyi va manzili", "Masalan: Ismoilov Jasur Botirovich, 'UzAerospace' DUK muhandisi"),
                TemplateField("child_info", "Farzand(lar)ning F.I.O. va tug'ilgan sanasi", "Masalan: Ismoilov Timur Jasurovich, 12.05.2018-yilda tug'ilgan"),
                TemplateField("alimony_share", "Undiriladigan aliment miqdori", "Masalan: Ish haqi va barcha daromadlarining 1/4 qismi miqdorida")
            ),
            instructions = "Tug'ilganlik haqida guvohnoma va nikoh/nikohdan ajralganlik guvohnomasi nusxalari ilova qilinadi. Da'vogar aliment davolarida davlat bojidan ozod qilingan."
        ),
        DocumentTemplate(
            id = "doc_divorce_claim",
            title = "Nikohni bekor qilish to'g'risida da'vo ariza",
            category = "Oila",
            description = "O'rtada voyaga yetmagan farzandlar yoki mulkiy nizo bo'lganda fuqarolik ishlari bo'yicha sudga nikohni bekor qilish haqida beriladigan da'vo ariza.",
            statutoryBasis = "O'zbekiston Respublikasi Oila kodeksining 38, 40-moddalari",
            fields = listOf(
                TemplateField("court_name", "Fuqarolik sudi nomi", "Masalan: Fuqarolik ishlari bo'yicha Mirzo Ulug'bek tumanlararo sudiga"),
                TemplateField("plaintiff_info", "Da'vogar F.I.O., manzili va tel", "Masalan: Axmedova Shahnoza Mirzayevna"),
                TemplateField("defendant_info", "Javobgar F.I.O. va manzili", "Masalan: Axmedov Otabek Alisherovich"),
                TemplateField("marriage_date", "Nikoh qayd etilgan sana va FHDYo bo'limi", "Masalan: 12.10.2019-yil, Yunusobod tuman FHDYo"),
                TemplateField("divorce_reasons", "Nikohni bekor qilish sabablari", "Masalan: xarakterlar mos kelmasligi va oilaviy kelishmovchiliklar tufayli 1 yildan beri alohida yashab kelinmoqda", isMultiline = true)
            ),
            instructions = "Nikoh guvohnomasi asl nusxasi, bolalarning metrikasi va davlat boji to'langanligi haqida kvitansiya ilova qilinadi."
        ),

        // --- BIZNES VA SHARTNOMALAR ---
        DocumentTemplate(
            id = "doc_lease_contract",
            title = "Ko'chmas mulk (Uy-joy/Bino) ijarasi shartnomasi",
            category = "Biznes",
            description = "Ijara beruvchi va ijaraga oluvchi o'rtasida turar-joy yoki bino-inshootni vaqtincha foydalanishga berish bo'yicha standart shartnoma loyihasi.",
            statutoryBasis = "O'zbekiston Respublikasi Fuqarolik kodeksining 535, 574-moddalari",
            fields = listOf(
                TemplateField("lessor_name", "Ijaraga beruvchi (Mulkdor) F.I.O. va pasporti", "Masalan: Salimov Valijon, Pasport AA 1234567"),
                TemplateField("lessee_name", "Ijaraga oluvchi F.I.O. va pasporti", "Masalan: Oripov Jamshid, Pasport AB 7654321"),
                TemplateField("property_address", "Ijaraga berilayotgan ko'chmas mulk manzili", "Masalan: Toshkent shahar, Yunusobod tumani, 4-mavze, 15-uy, 28-xonavdon"),
                TemplateField("monthly_rent", "Oylik ijara haqi (so'mda)", "Masalan: 4 000 000 (to'rt million) so'm"),
                TemplateField("lease_period", "Ijara muddati", "Masalan: 11 oy muddatga (2026-yil 1-noyabrgacha)")
            ),
            instructions = "Soliq organining ijara reyestridan (ijara.soliq.uz) elektron ro'yxatdan o'tkazilishi shart."
        ),
        DocumentTemplate(
            id = "doc_loan_contract",
            title = "Fuqarolar o'rtasida Qarz shartnomasi (Tilxat bilan)",
            category = "Biznes",
            description = "Pul mablag'larini qarzga berish va ma'lum muddatda qaytarib olish majburiyatini belgilovchi huquqiy shartnoma.",
            statutoryBasis = "O'zbekiston Respublikasi Fuqarolik kodeksining 732, 733-moddalari",
            fields = listOf(
                TemplateField("lender_info", "Qarz beruvchi F.I.O., pasport va manzili", "Masalan: Qodirov Alisher, Pasport AA 9876543"),
                TemplateField("borrower_info", "Qarz oluvchi F.I.O., pasport va manzili", "Masalan: Ergashboev Zokir, Pasport AB 1122334"),
                TemplateField("loan_amount", "Qarz summasi (so'mda/valyutada)", "Masalan: 25 000 000 (yigirma besh million) so'm"),
                TemplateField("repayment_date", "Qarzsni qaytarishning oxirgi sanasi", "Masalan: 2026-yil 31-dekabrga qadar")
            ),
            instructions = "Summa bazaviy hisoblash miqdorining 10 baravaridan oshganda yozma shaklda tuzilishi shart. Notarial tasdiqlash tavsiya etiladi."
        ),
        DocumentTemplate(
            id = "doc_car_power_attorney",
            title = "Avtotransport vositasini boshqarish ishonchnomasi",
            category = "Biznes",
            description = "Avtomobil egasi tomonidan boshqa shaxsga avtotransport vositasini boshqarish huquqini beruvchi rasmiy ishonchnoma matritsasi.",
            statutoryBasis = "O'zbekiston Respublikasi Fuqarolik kodeksining 134, 135-moddalari",
            fields = listOf(
                TemplateField("owner_info", "Avtomobil mulkdori F.I.O. va pasporti", "Masalan: Usmonov Botir"),
                TemplateField("driver_info", "Ishonch bildiruvchi (Haydovchi) F.I.O. va pasporti", "Masalan: Usmonov Sarvar"),
                TemplateField("car_model", "Avtomobil rusumi va davlat raqami", "Masalan: Chevrolet Cobalt, Davlat raqami 01 A 777 AA"),
                TemplateField("car_body_num", "Kuzov va dvigatel raqami", "Masalan: Kuzov № XWB12345, Dvigatel № 67890"),
                TemplateField("validity_period", "Ishonchnoma amal qilish muddati", "Masalan: 3 (uch) yil muddatga")
            ),
            instructions = "Notarius tomonidan e-notarius.uz orqali majburiy notarial tasdiqlanishi shart."
        ),

        // --- ISTE'MOLCHILAR HUQUQI VA MURAJAAT ---
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
            category = "Iste'molchilar huquqi",
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
            category = "Iste'molchilar huquqi",
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
                "doc_alimony_claim" -> {
                    appendLine("SUDGA: ${values["court_name"] ?: "________________"}")
                    appendLine("Da'vogar: ${values["plaintiff_info"] ?: "________________"}")
                    appendLine("Javobgar: ${values["defendant_info"] ?: "________________"}\n")
                    appendLine("                       DA'VO ARIZA")
                    appendLine("            (Aliment undirish to'g'risida)\n")
                    appendLine("   Men va javobgar o'rtasida tuzilgan nikohdan farzandimiz ${values["child_info"] ?: "________________"} tug'ilgan.")
                    appendLine("   Hozirgi kunda javobgar farzand ta'minoti uchun ixtiyoriy ravishda moddiy yordam ko'rsatmay kelmoqda.")
                    appendLine("\n   Oila kodeksining 96, 99-moddalariga muvofiq, ota-ona voyaga yetmagan bolalariga ta'minot berishi shart.")
                    appendLine("\n   YUQORIDAGILARDAN KELIB CHIQIB, SUTDAN SO'RAYMAN:")
                    appendLine("   Javobgardan mening foydamga voyaga yetmagan farzandimiz ta'minoti uchun ${values["alimony_share"] ?: "1/4 qismi"} miqdorida har oylik aliment undirish haqida hal qiluv qarori chiqarishingizni.")
                }
                "doc_lease_contract" -> {
                    appendLine("             KO'CHMAS MULK IJARASI SHARTNOMASI\n")
                    appendLine("1. SHARTNOMA TARAFLARI:")
                    appendLine("   Ijaraga beruvchi: ${values["lessor_name"] ?: "________________"}")
                    appendLine("   Ijaraga oluvchi: ${values["lessee_name"] ?: "________________"}\n")
                    appendLine("2. SHARTNOMA PREDMETI:")
                    appendLine("   Ijaraga beruvchi o'ziga tegishli bo'lgan quyidagi manzildagi ko'chmas mulkni ijaraga beradi:")
                    appendLine("   Manzil: ${values["property_address"] ?: "________________"}\n")
                    appendLine("3. HISOBLASHISH TARTIBI:")
                    appendLine("   Oylik ijara haqi: ${values["monthly_rent"] ?: "0"} so'm etib belgilanadi.")
                    appendLine("   Ijara muddati: ${values["lease_period"] ?: "11 oy"}.\n")
                    appendLine("4. TARAFLARNING MAJBURIYATLARI:")
                    appendLine("   - Ijaraga oluvchi mulkni asrab-avaylashi va kommunal to'lovlarni o'z vaqtida to'lashi shart.")
                    appendLine("   - Ushbu shartnoma O'zbekiston Respublikasi FK 574-moddasi asosida rasmiylashtirildi.")
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
