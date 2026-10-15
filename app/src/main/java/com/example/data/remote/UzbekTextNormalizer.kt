package com.example.data.remote

import java.util.Locale

object UzbekTextNormalizer {

    private val cyrillicToLatinMap = mapOf(
        'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d",
        'е' to "e", 'ё' to "yo", 'ж' to "j", 'з' to "z", 'и' to "i",
        'й' to "y", 'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n",
        'о' to "o", 'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
        'у' to "u", 'ф' to "f", 'х' to "x", 'ц' to "ts", 'ч' to "ch",
        'ш' to "sh", 'ъ' to "'", 'ь' to "", 'э' to "e", 'ю' to "yu",
        'я' to "ya", 'ғ' to "g'", 'қ' to "q", 'ҳ' to "h", 'ў' to "o'",
        'А' to "A", 'Б' to "B", 'В' to "V", 'Г' to "G", 'Д' to "D",
        'Е' to "E", 'Ё' to "Yo", 'Ж' to "J", 'З' to "Z", 'И' to "I",
        'Й' to "Y", 'К' to "K", 'Л' to "L", 'М' to "M", 'Н' to "N",
        'О' to "O", 'П' to "P", 'Р' to "R", 'С' to "S", 'Т' to "T",
        'У' to "U", 'Ф' to "F", 'Х' to "X", 'Ц' to "Ts", 'Ч' to "Ch",
        'Ш' to "Sh", 'Ъ' to "'", 'Ь' to "", 'Э' to "E", 'Ю' to "Yu",
        'Я' to "Ya", 'Ғ' to "G'", 'Қ' to "Q", 'Ҳ' to "H", 'Ў' to "O'"
    )

    private val mobileSlangReplacements = mapOf(
        "nma" to "nima",
        "qlsam" to "qilsam",
        "boladi" to "bo'ladi",
        "boladimi" to "bo'ladimi",
        "oyligmni" to "oyligimni",
        "oylgm" to "oyligim",
        "tashamadi" to "tashlamadi",
        "tashalamadi" to "tashlamadi",
        "dokumntimni" to "hujjatimni",
        "dokumnt" to "hujjat",
        "dokment" to "hujjat",
        "dokument" to "hujjat",
        "nachalnik" to "ish beruvchi",
        "zakonniy" to "qonuniy",
        "problem" to "muammo",
        "problema" to "muammo",
        "ulaga" to "ularga",
        "haydashvotti" to "haydashayapti",
        "bo'shatishvotti" to "bo'shatishayapti",
        "qoyishvotti" to "qo'yishayapti",
        "zarpalata" to "ish haqi"
    )

    fun transliterateCyrillicToLatin(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val converted = cyrillicToLatinMap[ch]
            if (converted != null) {
                sb.append(converted)
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun normalizeApostrophesAndAccents(text: String): String {
        return text
            .replace('ʻ', '\'')
            .replace('ʼ', '\'')
            .replace('’', '\'')
            .replace('‘', '\'')
            .replace('`', '\'')
            .replace('“', '"')
            .replace('”', '"')
    }

    fun normalizeText(text: String): String {
        val latinText = transliterateCyrillicToLatin(text)
        var normalized = normalizeApostrophesAndAccents(latinText)
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9'\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        val words = normalized.split(" ")
        val replacedWords = words.map { word -> mobileSlangReplacements[word] ?: word }
        normalized = replacedWords.joinToString(" ")

        return normalized
    }

    fun expandLegalSynonyms(text: String): List<String> {
        val norm = normalizeText(text)
        val expansions = mutableListOf<String>()
        expansions.add(norm)

        // Traffic & Driving
        if (norm.contains("remen") || norm.contains("kamar")) {
            expansions.add("xavfsizlik kamarini taqish")
            expansions.add("125-modda")
        }
        if (norm.contains("qizil") || norm.contains("qizilda") || norm.contains("svetofor")) {
            expansions.add("svetoforning taqiqlovchi ishorasi")
            expansions.add("128-modda")
            expansions.add("128-4-modda")
        }
        if (norm.contains("prava") || norm.contains("pravasiz") || norm.contains("guvohnomasiz") || norm.contains("gaylar") || norm.contains("gai")) {
            expansions.add("haydovchilik guvohnomasi bo'lmagan")
            expansions.add("135-modda")
        }
        if (norm.contains("protokol") || norm.contains("bayonnoma")) {
            expansions.add("ma'muriy bayonnoma")
            expansions.add("313-modda")
        }

        // Labor & Employment
        if (norm.contains("ishdan") || norm.contains("haydashdi") || norm.contains("bo'shatishdi") || norm.contains("prikaz") || norm.contains("uvolnenie") || norm.contains("bosim") || norm.contains("ishga kiritmayapti") || norm.contains("tashamadi")) {
            expansions.add("mehnat shartnomasini bekor qilish")
            expansions.add("161-modda")
        }
        if (norm.contains("zarplata") || norm.contains("maosh") || norm.contains("oylik") || norm.contains("ish haqi") || norm.contains("kechiktirishyapti") || norm.contains("tashamadi")) {
            expansions.add("ish haqini to'lash")
            expansions.add("253-modda")
        }

        // Civil, Debt & Property
        if (norm.contains("qarz") || norm.contains("dolg") || norm.contains("pulimni yeb ketdi") || norm.contains("aldashdi") || norm.contains("tilxat")) {
            expansions.add("qarz shartnomasi majburiyatlari")
            expansions.add("732-modda")
            expansions.add("168-modda")
        }
        if (norm.contains("ijara") || norm.contains("kvartira") || norm.contains("arenda") || norm.contains("uyimni tortib")) {
            expansions.add("turar joy ijarasi shartnomasi")
            expansions.add("535-modda")
            expansions.add("577-modda")
        }

        // Family
        if (norm.contains("ajrash") || norm.contains("razvod") || norm.contains("xotinim") || norm.contains("erim") || norm.contains("aliment")) {
            expansions.add("nikohni bekor qilish va aliment")
            expansions.add("96-modda")
        }

        // Criminal & Safety
        if (norm.contains("zorladim") || norm.contains("zo'rladim") || norm.contains("zurladim") || norm.contains("nomusiga")) {
            expansions.add("nomusga tegish")
            expansions.add("118-modda")
        }
        if (norm.contains("o'ldir") || norm.contains("tahdid") || norm.contains("po'pisa")) {
            expansions.add("qasddan odam o'ldirish")
            expansions.add("97-modda")
        }

        return expansions.distinct()
    }
}
