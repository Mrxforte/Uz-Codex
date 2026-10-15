package com.example.data.local

import com.example.data.model.LegislationUpdate
import com.example.data.remote.GeminiLegalAssistant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object UpdatesData {

    val RECENT_UPDATES: List<LegislationUpdate> = listOf(
        LegislationUpdate(
            id = "upd_pf_154",
            codeName = "Prezident Farmoni (PF-154)",
            articleNumber = "PF-154-sonli Farmon",
            amendingLaw = "O'zbekiston Respublikasi Prezidentining 'Raqamli qonunchilik va sud-huquq islohotlarini jadallashtirish to'g'risida'gi Farmoni",
            adoptedDate = "10.01.2025",
            effectiveDate = "15.01.2025",
            summaryOfChange = "Barcha sud qarorlari va normativ-huquqiy hujjatlar loyihalarini Sun'iy Intelekt va ochiq ma'lumotlar platformasi orqali e'lon qilish tartibi belgilandi.",
            oldWording = "Eski tartibda sud qarorlari va qonunchilik hujjatlari faqat idoralararo tizimlarda saqlanar edi.",
            newWording = "Barcha qabul qilingan Prezident farmonlari va qarorlari, shuningdek sud amaliyoti umumlashtirilgan ochiq reyestrda real vaqt rejimida e'lon qilib boriladi.",
            importance = "Yuqori"
        ),
        LegislationUpdate(
            id = "upd_pq_88",
            codeName = "Prezident Qarori (PQ-88)",
            articleNumber = "PQ-88-sonli Qaror",
            amendingLaw = "Tadbirkorlik subyektlarining huquqiy himoyasi va investitsiyaviy muhitni yaxshilash to'g'risida",
            adoptedDate = "05.02.2025",
            effectiveDate = "10.02.2025",
            summaryOfChange = "Tadbirkorlik subyektlarida tekshirishlar o'tkazishga moratorium uzaytirildi va biznesni noqonuniy aralashuvlardan himoya qilish kuchaytirildi.",
            oldWording = "Nazorat qiluvchi organlar ruxsatnoma va xabardor qilish tartibida tekshiruvlar o'tkazar edi.",
            newWording = "Tekshirishlar faqat 'Xavfni tahlil etish' elektron tizimi xulosasi va Biznes-ombudsman kelishuvi bilan amalga oshiriladi.",
            importance = "Yuqori"
        ),
        LegislationUpdate(
            id = "upd_mk_guarantees",
            codeName = "Mehnat kodeksi",
            articleNumber = "112, 253-moddalar",
            amendingLaw = "O'zbekiston Respublikasining O'RQ-798-sonli yangi tahrirdagi Mehnat kodeksi",
            adoptedDate = "28.10.2022",
            effectiveDate = "30.04.2023",
            summaryOfChange = "Ish haqi kechiktirilgan har bir kun uchun Markaziy bank qayta moliyalash stavkasi bo'yicha majburiy foizlar (kompensatsiya) to'lash kafolati kiritildi.",
            oldWording = "Eski Mehnat kodeksida ish haqi kechiktirilganda ish beruvchining foiz to'lash majburiyati qonun darajasida aniq belgilab qo'yilmagan edi.",
            newWording = "Ish beruvchi tomonidan ish haqi belgilangan muddatdan kechiktirilgan taqdirda, ish beruvchi xodimga har bir kechiktirilgan kun uchun Markaziy bank qayta moliyalash stavkasi miqdorida foizlar to'lashi shart.",
            importance = "Yuqori"
        ),
        LegislationUpdate(
            id = "upd_jk_cybercrime",
            codeName = "Jinoyat kodeksi",
            articleNumber = "168-modda (Firibgarlik), 278-modda",
            amendingLaw = "O'RQ-901-sonli Qonun (Axborot texnologiyalari va bank kartalari sohasidagi jinoyatlar uchun javobgarlik)",
            adoptedDate = "19.01.2024",
            effectiveDate = "20.04.2024",
            summaryOfChange = "Bank kartalari va elektron to'lov tizimlaridan foydalanib firibgarlik qilish og'irlashtiruvchi holat sifatida belgilandi.",
            oldWording = "Firibgarlik moddasida axborot texnologiyalari vositalari umumiy dispozitsiya ostida ko'rilgan.",
            newWording = "Bank kartalari, elektron pul mablag'lari yoki axborot tizimlaridan foydalanib sodir etilgan firibgarlik uchun jazo 5 yildan 8 yilgacha ozodlikdan mahrum qilishgacha oshirildi.",
            importance = "Yuqori"
        ),
        LegislationUpdate(
            id = "upd_fpk_remote_court",
            codeName = "Fuqarolik protsessual kodeksi",
            articleNumber = "209-modda",
            amendingLaw = "O'RQ-867-sonli Qonun (Sud ishlarini raqamlashtirish va videokonferensaloqa)",
            adoptedDate = "15.09.2023",
            effectiveDate = "16.12.2023",
            summaryOfChange = "Fuqarolik sud majlislarida masofadan turib mobil ilova va videokonferensaloqa orqali to'laqonli ishtirok etish qonuniylashtirildi.",
            oldWording = "Ishtirokchilar faqat tuman sudining maxsus jihozlangan xonasidan turib boshqa sud majlisiga ulanishi mumkin edi.",
            newWording = "Sud jarayoni ishtirokchilari o'z shaxsini elektron identifikatsiya (OneID yoki biometriya) orqali tasdiqlab, shaxsiy kompyuter yoki smartfon orqali sud majlisida ishtirok etishga haqli.",
            importance = "O'rta"
        )
    )

    /**
     * Dynamically fetches live legal decrees, resolutions, and laws via AI/Online Legal Service.
     */
    suspend fun fetchLiveDecreesAndUpdates(): List<LegislationUpdate> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                O'zbekiston Respublikasining eng so'nggi va muhim Prezident Farmonlari (PF-sonli), Prezident Qarorlari (PQ-sonli) hamda Qonunchilikdagi o'zgarishlaridan 5 tasini JSON ro'yxati shaklida qaytaring.
                Format (JSON Array ONLY, no markdown wrapping):
                [
                  {
                    "id": "online_pf_1",
                    "codeName": "Prezident Farmoni",
                    "articleNumber": "PF-sonli Farmon",
                    "amendingLaw": "Hujjatning to'liq rasmiy nomi",
                    "adoptedDate": "01.02.2025",
                    "effectiveDate": "05.02.2025",
                    "summaryOfChange": "Farmon / Qarorning qisqacha mazmuni va kiritilgan asosiy yangilik",
                    "oldWording": "Ilgari amal qilgan tartib",
                    "newWording": "Yangi o'rnatilgan tartib va qoidalar",
                    "importance": "Yuqori"
                  }
                ]
            """.trimIndent()

            val aiResponse = GeminiLegalAssistant.askLegalQuestion(prompt, emptyList())
            val rawText = aiResponse.text.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = JSONArray(rawText)
            val list = mutableListOf<LegislationUpdate>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    LegislationUpdate(
                        id = obj.optString("id", "online_$i"),
                        codeName = obj.optString("codeName", "Prezident Hujjati"),
                        articleNumber = obj.optString("articleNumber", "PF/PQ-sonli"),
                        amendingLaw = obj.optString("amendingLaw", "O'zbekiston Respublikasi Normativ Hujjati"),
                        adoptedDate = obj.optString("adoptedDate", "2025-yil"),
                        effectiveDate = obj.optString("effectiveDate", "Amalda"),
                        summaryOfChange = obj.optString("summaryOfChange", "Yangi huquqiy norma va tartib o'rnatildi."),
                        oldWording = obj.optString("oldWording", "Oldingi tartib va amaliyot."),
                        newWording = obj.optString("newWording", "Yangi tasdiqlangan qoidalar."),
                        importance = obj.optString("importance", "Yuqori")
                    )
                )
            }

            if (list.isNotEmpty()) {
                list + RECENT_UPDATES
            } else {
                RECENT_UPDATES
            }
        } catch (e: Exception) {
            e.printStackTrace()
            RECENT_UPDATES
        }
    }
}
