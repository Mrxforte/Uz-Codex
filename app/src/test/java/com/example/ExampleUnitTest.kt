package com.example

import com.example.data.local.AcademyData
import com.example.data.local.CaseData
import com.example.data.local.DocumentData
import com.example.data.local.LegislationData
import com.example.data.model.UserLevel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAllNineteenUzbekistanCodesArePresent() {
        val codes = LegislationData.UZBEKISTAN_CODES
        assertEquals(19, codes.size)

        val codeIds = codes.map { it.id }.toSet()
        assertTrue(codeIds.contains("fk"))    // Fuqarolik kodeksi
        assertTrue(codeIds.contains("fpk"))   // Fuqarolik protsessual kodeksi
        assertTrue(codeIds.contains("jk"))    // Jinoyat kodeksi
        assertTrue(codeIds.contains("jpk"))   // Jinoyat-protsessual kodeksi
        assertTrue(codeIds.contains("jik"))   // Jinoyat-ijroiya kodeksi
        assertTrue(codeIds.contains("ipk"))   // Iqtisodiy protsessual kodeks
        assertTrue(codeIds.contains("msiyk")) // Ma'muriy sud ishlarini yuritish kodeksi
        assertTrue(codeIds.contains("mjtk"))  // Ma'muriy javobgarlik kodeksi
        assertTrue(codeIds.contains("mk"))    // Mehnat kodeksi
        assertTrue(codeIds.contains("ok"))    // Oila kodeksi
        assertTrue(codeIds.contains("sk"))    // Soliq kodeksi
        assertTrue(codeIds.contains("bk"))    // Bojxona kodeksi
        assertTrue(codeIds.contains("byk"))   // Byudjet kodeksi
        assertTrue(codeIds.contains("yk"))    // Yer kodeksi
        assertTrue(codeIds.contains("ujk"))   // Uy-joy kodeksi
        assertTrue(codeIds.contains("shk"))   // Shaharsozlik kodeksi
        assertTrue(codeIds.contains("hk"))    // Havo kodeksi
        assertTrue(codeIds.contains("sayk"))  // Saylov kodeksi
        assertTrue(codeIds.contains("suk"))   // Suv kodeksi
    }

    @Test
    fun testHomicideCaseScenarioLogic() {
        val scenario = CaseData.getScenarioById("fight_death_scenario")
        assertNotNull(scenario)
        assertTrue(scenario!!.narrative.contains("musht"))
        assertTrue(scenario.clarifyingQuestions.isNotEmpty())
        assertTrue(scenario.legalQualifications.any { it.articleCitation.contains("104") })
        assertTrue(scenario.legalQualifications.any { it.articleCitation.contains("102") })
        assertTrue(scenario.proceduralSafeguards.any { it.contains("23") })
    }

    @Test
    fun testLevelAndXpProgression() {
        val level1 = UserLevel.getLevelForXp(50)
        assertEquals(1, level1.levelNumber)
        assertEquals("Huquqiy boshlovchi", level1.title)

        val level2 = UserLevel.getLevelForXp(150)
        assertEquals(2, level2.levelNumber)
        assertEquals("Qonun o'quvchisi", level2.title)

        val level8 = UserLevel.getLevelForXp(4000)
        assertEquals(8, level8.levelNumber)
        assertEquals("Ekspert darajasi", level8.title)
    }

    @Test
    fun testDocumentGeneration() {
        val template = DocumentData.getTemplateById("doc_salary_claim")
        assertNotNull(template)

        val values = mapOf(
            "employer_name" to "Oltin Voha MChJ",
            "employee_name" to "Azamat Urazimbetov",
            "employee_position" to "Bosh yurist",
            "unpaid_months" to "Avgust, Sentabr",
            "total_amount" to "15 000 000",
            "bank_card" to "8600 1234 5678 9999"
        )
        val text = DocumentData.generateFormattedDocument(template!!, values)
        assertTrue(text.contains("ARIZA"))
        assertTrue(text.contains("Azamat Urazimbetov"))
        assertTrue(text.contains("Mehnat kodeksining 253-moddasi"))
    }
}
