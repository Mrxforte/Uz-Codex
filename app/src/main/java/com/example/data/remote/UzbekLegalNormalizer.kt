package com.example.data.remote

import java.util.Locale

object UzbekLegalNormalizer {

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

    private val commonSuffixes = listOf(
        "ningish", "ningiz", "larning", "lardagi", "larga", "lardan", "larda",
        "imni", "imiz", "ingiz", "ing", "ning", "dagi", "lar", "dan", "dan",
        "da", "ga", "ni", "si", "i"
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
        return normalizeApostrophesAndAccents(latinText)
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9'\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Light morphological stemmer for Uzbek legal terms.
     */
    fun stemWord(word: String): String {
        var stemmed = word.lowercase()
        if (stemmed.length <= 4) return stemmed

        for (suf in commonSuffixes) {
            if (stemmed.endsWith(suf) && stemmed.length - suf.length >= 3) {
                stemmed = stemmed.substring(0, stemmed.length - suf.length)
                break
            }
        }
        return stemmed
    }

    fun tokenizeAndStem(text: String): List<String> {
        val normalized = normalizeText(text)
        val stopWords = setOf(
            "nima", "qanday", "qaysi", "uchun", "haqida", "bilan", "keyin", "oldin",
            "agar", "bo'lsa", "bolsa", "qilsa", "qilishi", "kerak", "bor", "yo'q",
            "yoq", "menga", "mening", "o'z", "oz", "boshqa", "har", "hamma", "barcha",
            "mumkin", "emas", "edi", "ekan", "biron", "bir", "bo'yicha", "boyicha"
        )

        return normalized.split(" ")
            .filter { it.length >= 3 && !stopWords.contains(it) }
            .map { stemWord(it) }
    }
}
