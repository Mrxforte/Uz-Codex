package com.example.data.remote

/**
 * Mappings between colloquial Uzbek slang/misspellings and official legal terms.
 * Used strictly as retrieval expansion hints, NOT as proof of applicable law.
 */
object LegalSynonymDictionary {

    private val synonymMap = mapOf(
        "prava" to listOf("haydovchilik guvohnomasi", "135-modda", "mjtk_135"),
        "pravasiz" to listOf("haydovchilik guvohnomasisiz boshqarish", "135-modda", "mjtk_135"),
        "zarpalata" to listOf("ish haqi", "253-modda", "mk_253"),
        "oylik" to listOf("ish haqi", "mehnatga haq to'lash", "253-modda", "mk_253"),
        "oyligimni" to listOf("ish haqi", "253-modda", "mk_253"),
        "maosh" to listOf("ish haqi", "253-modda", "mk_253"),
        "tashamadi" to listOf("ish haqini to'lash muddatlari", "253-modda", "mk_253"),
        "tashlamadi" to listOf("ish haqini to'lash muddatlari", "253-modda", "mk_253"),
        "remen" to listOf("xavfsizlik kamari", "125-modda", "mjtk_125"),
        "qizil" to listOf("svetoforning taqiqlovchi ishorasi", "128-modda", "128-4-modda", "mjtk_128"),
        "qizilda" to listOf("svetoforning taqiqlovchi ishorasi", "128-modda", "128-4-modda", "mjtk_128"),
        "prikaz" to listOf("buyruq", "mehnat shartnomasini bekor qilish", "161-modda", "mk_161"),
        "uvolnenie" to listOf("mehnat shartnomasini bekor qilish", "161-modda", "mk_161"),
        "haydashdi" to listOf("mehnat shartnomasini bekor qilish", "161-modda", "mk_161"),
        "bo'shatishdi" to listOf("mehnat shartnomasini bekor qilish", "161-modda", "mk_161"),
        "bo'shatishvotti" to listOf("mehnat shartnomasini bekor qilish", "161-modda", "mk_161"),
        "dolg" to listOf("qarz majburiyati", "732-modda", "fk_732"),
        "tilxat" to listOf("qarz shartnomasi yozma shakli", "732-modda", "fk_732"),
        "arenda" to listOf("ijara shartnomasi", "535-modda", "fk_535"),
        "kvartirant" to listOf("turar joy ijarasi shartnomasi", "535-modda", "fk_535"),
        "ijara" to listOf("mulk ijarasi shartnomasi", "535-modda", "fk_535"),
        "razvod" to listOf("nikohni bekor qilish", "aliment", "96-modda", "99-modda", "ok_99"),
        "aliment" to listOf("voyaga yetmagan bolalarga aliment", "96-modda", "99-modda", "ok_99"),
        "shtraf" to listOf("ma'muriy jarima", "125-modda", "mjtk_125"),
        "protokol" to listOf("ma'muriy bayonnoma", "313-modda", "mjtk_313"),
        "dokument" to listOf("hujjat", "rasmiy hujjat"),
        "dokumnt" to listOf("hujjat"),
        "mudofaa" to listOf("zaruriy mudofaa", "37-modda", "jk_37"),
        "tajovuz" to listOf("zaruriy mudofaa", "37-modda", "jk_37"),
        "firibgarlik" to listOf("o'zgalar mol-mulkini talan-toroj qilish", "168-modda", "jk_168"),
        "daromaddan" to listOf("daromad solig'i", "381-modda", "sk_1"),
        "shikast" to listOf("badanga og'ir shikast yetkazish", "104-modda", "jk_104"),
        "affekt" to listOf("kuchli ruhiy hayajonlanish", "98-modda", "jk_98"),
        "ehtiyotsizlik" to listOf("ehtiyotsizlik orqasida odam o'ldirish", "102-modda", "jk_102"),
        "daxlsizlik" to listOf("shaxsiy daxlsizlik", "fpk_1"),
        "videokonferensiya" to listOf("videokonferensaloqa rejimida sud majlisini o'tkazish", "209-modda", "fpk_209"),
        "samoviy" to listOf("samoviy hudud suvereniteti", "1-modda", "hk_1"),
        "mahkum" to listOf("mahkumlarning asosiy huquqlari", "8-modda", "jik_8"),
        "iqtisodiy" to listOf("iqtisodiy sudga taalluqli ishlar", "25-modda", "ipk_25"),
        "ma'muriy" to listOf("ma'muriy sudga taalluqli ishlar", "26-modda", "msiyk_26")
    )

    fun getRetrievalHints(queryText: String): List<String> {
        val norm = UzbekLegalNormalizer.normalizeText(queryText)
        val words = norm.split(Regex("\\s+"))
        val hints = mutableListOf<String>()

        for (word in words) {
            val matches = synonymMap[word]
            if (matches != null) {
                hints.addAll(matches)
            }
        }

        // Phrase checks
        if (norm.contains("ishdan bo'shatish") || norm.contains("ishdan haydash") || norm.contains("ishdan haydashdi")) {
            hints.add("mehnat shartnomasini bekor qilish")
            hints.add("161-modda")
            hints.add("mk_161")
        }
        if (norm.contains("oylik bermayapti") || norm.contains("oyligimni berishmayapti") || norm.contains("berishmayapti")) {
            hints.add("ish haqini to'lash muddatlari")
            hints.add("253-modda")
            hints.add("mk_253")
        }
        if (norm.contains("zararni qoplash") || norm.contains("zararni to'liq")) {
            hints.add("zararni qoplash")
            hints.add("14-modda")
            hints.add("fk_14")
        }
        if (norm.contains("qarz bergandim") || norm.contains("qarzimni")) {
            hints.add("qarz shartnomasi")
            hints.add("732-modda")
            hints.add("fk_732")
        }
        if (norm.contains("shaxsiy daxlsizlik")) {
            hints.add("fuqarolik protsessi")
            hints.add("fpk_1")
        }
        if (norm.contains("videokonferensiya") || norm.contains("masofadan")) {
            hints.add("videokonferensaloqa rejimida sud majlisini o'tkazish")
            hints.add("209-modda")
            hints.add("fpk_209")
        }
        if (norm.contains("notariusda nikoh shartnomasi") || norm.contains("nikoh shartnomasi")) {
            hints.add("ok_1")
            hints.add("1-modda")
        }
        if (norm.contains("soliq daromaddan") || norm.contains("soliq")) {
            hints.add("sk_1")
        }
        if (norm.contains("qasddan odam o'ldirganlik") || norm.contains("odam o'ldir")) {
            hints.add("jk_97")
            hints.add("97-modda")
        }
        if (norm.contains("ehtiyotsizlik orqasida")) {
            hints.add("jk_102")
            hints.add("102-modda")
        }
        if (norm.contains("og'ir shikast")) {
            hints.add("jk_104")
            hints.add("104-modda")
        }
        if (norm.contains("ma'muriy sud davlat organi")) {
            hints.add("msiyk_1")
            hints.add("msiyk_26")
        }
        if (norm.contains("iqtisodiy nizo")) {
            hints.add("ipk_25")
        }
        if (norm.contains("samoviy hudud")) {
            hints.add("hk_1")
        }
        if (norm.contains("mahkumlar huquqlari") || norm.contains("mahkum huquqlari")) {
            hints.add("jik_8")
        }

        return hints.distinct()
    }
}