package com.example.data.local

import com.example.data.model.*

object CaseData {

    val EDUCATIONAL_SCENARIOS: List<LegalCaseScenario> = listOf(
        LegalCaseScenario(
            id = "fight_death_scenario",
            title = "Janjal oqibatida shaxs vafot etishi (Jinoyat huquqi tahlili)",
            category = "Jinoiy huquq",
            shortDescription = "Ko'chada janjal yuz bergan va urushgan shaxs yerga yiqilib vafot etgan. Qilmish qanday malakalanadi?",
            narrative = "Men do'stim bilan ko'chada kelayotganimda bir notanish shaxs bilan og'zaki mojaro kelib chiqdi. U menga tashlandi, men o'zimni himoya qilish maqsadida uning yuziga bir marotaba musht tushirdim. U orqaga tisarilib, boshi bilan yo'l chetidagi beton bordyurga urildi va hushini yo'qotdi. Tez tibbiy yordam chaqirdik, biroq shifoxonada vafot etdi. O'zbekiston qonunchiligi bo'yicha menga nima bo'ladi?",
            clarifyingQuestions = listOf(
                ClarifyingQuestion(
                    id = "q1",
                    question = "Zarba berish vaqtida shaxsning qasdi nimaga qaratilgan edi?",
                    options = listOf(
                        "Faqat o'zini himoya qilish va hujumni qaytarish",
                        "Tan jarohati yetkazish niyati bo'lgan",
                        "O'ldirish niyati yo'q edi, tasodifan bordyurga urildi"
                    ),
                    explanationWhyNeeded = "Jinoyat huquqida qasdning yo'nalishi (to'g'ri qasd, egri qasd, ehtiyotsizlik) javobgarlik darajasi va moddani aniqlashda hal qiluvchi ahamiyatga ega."
                ),
                ClarifyingQuestion(
                    id = "q2",
                    question = "Jabrlanuvchi tomonidan bevosita real hayotiy xavf tug'diruvchi hujum sodir etilganmi?",
                    options = listOf(
                        "Ha, uning qo'lida qurol yoki xavfli jism bor edi",
                        "Faqat musht bilan tashlangan edi",
                        "Faqat og'zaki so'kish va tahdid bo'lgan"
                    ),
                    explanationWhyNeeded = "Zaruriy mudofaa (JK 37-modda) holati yoki zaruriy mudofaa chegarasidan chetga chiqishni (JK 100-modda) baholash uchun zarur."
                ),
                ClarifyingQuestion(
                    id = "q3",
                    question = "Sud-tibbiy ekspertizasi o'lim sababini nima deb ko'rsatgan?",
                    options = listOf(
                        "Zarbaning o'zidan emas, yiqilib bordyurga urilish (kalla suyagi sinishi) natijasida",
                        "Bevalosita berilgan zarbadan",
                        "Hali sud-tibbiy ekspertizasi xulosasi tayyor emas"
                    ),
                    explanationWhyNeeded = "Harakat va oqibat o'rtasidagi sababiy bog'lanish (JK 104-modda 3-qism yoki JK 102-modda ehtiyotsizlik) ekspertiza orqali aniqlanadi."
                )
            ),
            factsExtracted = CaseFacts(
                partiesInvolved = listOf("Foydalanuvchi (zarba bergan shaxs)", "Jabrlanuvchi (vafot etgan notanish shaxs)", "Guvoh (do'sti)"),
                timeline = "Og'zaki nizo -> Tajovuz -> Bitta zarba berilishi -> Yiqilib bordyurga urilish -> Kasalxonada vafot etish",
                actionsAlleged = listOf("Bitta musht zarbasi berilgan", "Jabrlanuvchi yiqilib qattiq sirtga urilgan", "Tez tibbiy yordam chaqirilgan"),
                mentalStateIntent = "Dastlabki ma'lumotlarga ko'ra o'ldirish qasdi yo'q, ehtiyotsizlik yoki badanga shikast yetkazish gumoni",
                consequences = "Inson vafot etgan (og'ir oqibat)",
                availableEvidence = listOf("Do'stining guvohlik ko'rsatmasi", "Tez yordam chaqiruvi qaydlari", "Voqea joyi ko'zdan kechirish bayonnomasi"),
                missingCrucialFacts = listOf(
                    "Sud-tibbiy ekspertizasining yakuniy xulosasi (o'limning bevosita patologik sababi)",
                    "Hududdagi videokuzatuv kameralari yozuvlari",
                    "Zaruriy mudofaa chegarasi buzilgan-buzilmaganligi"
                )
            ),
            legalQualifications = listOf(
                LegalQualification(
                    articleCitation = "JK 104-modda 3-qism 'd' bandi",
                    codeName = "O'zbekiston Respublikasining Jinoyat kodeksi",
                    relevance = "Eng ko'p uchraydigan ehtimoliy malakalash",
                    statutoryLanguage = "Qasddan badanga og'ir shikast yetkazish jabrlanuvchining o'limiga sabab bo'lsa — sakkiz yildan o'n yilgacha ozodlikdan mahrum qilish bilan jazolanadi.",
                    likelihoodRating = "Muqobil ehtimol",
                    reasonWhyApplicable = "Agar sud-tibbiy ekspertiza berilgan zarba bevosita o'limga sabab bo'lgan og'ir tan jarohati ekanligini tasdiqlasa va shaxsda tan jarohati yetkazish qasdi bo'lgan deb topilsa."
                ),
                LegalQualification(
                    articleCitation = "JK 102-modda 1-qismi",
                    codeName = "O'zbekiston Respublikasining Jinoyat kodeksi",
                    relevance = "Ehtiyotsizlik orqasida odam o'ldirish",
                    statutoryLanguage = "Ehtiyotsizlik orqasida odam o'ldirish — ikki yilgacha axloq tuzatish ishlari yoki bir yildan uch yilgacha ozodlikni cheklash yoxud uch yilgacha ozodlikdan mahrum qilish bilan jazolanadi.",
                    likelihoodRating = "Yuqori ehtimol (Agar o'lim zarbadan emas, yiqilish natijasida yuz bergan bo'lsa)",
                    reasonWhyApplicable = "O'zbekiston Respublikasi Oliy Sudi Plenumining amaliyotiga ko'ra, agar shaxs uncha og'ir bo'lmagan zarba berganida jabrlanuvchi yiqilib boshi bilan tosh/bordyurga urilib vafot etsa va aybdorda tan jarohati yetkazish qasdi bo'lmasa, qilmish JK 102-modda bilan malakalanadi."
                ),
                LegalQualification(
                    articleCitation = "JK 37-modda (Zaruriy mudofaa)",
                    codeName = "O'zbekiston Respublikasining Jinoyat kodeksi",
                    relevance = "Jinoiy javobgarlikni to'liq istisno qiluvchi holat",
                    statutoryLanguage = "Shaxsning o'zini yoki boshqa shaxslarni qonunga xilof tajovuzdan himoya qilishi jinoyat hisoblanmaydi.",
                    likelihoodRating = "Himoya pozitsiyasi",
                    reasonWhyApplicable = "Agar jabrlanuvchining hujumi foydalanuvchining hayoti uchun real xavf tug'dirganligi isbotlansa va mudofaa chegarasi buzilmagan bo'lsa."
                ),
                LegalQualification(
                    articleCitation = "JK 97-modda (Qasddan odam o'ldirish)",
                    codeName = "O'zbekiston Respublikasining Jinoyat kodeksi",
                    relevance = "Dastlabki tergov organlari tomonidan tekshirilishi shart bo'lgan versiya",
                    statutoryLanguage = "Qasddan odam o'ldirish — o'n yildan o'n besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.",
                    likelihoodRating = "Kam ehtimolli (Qasd mavjud bo'lmaganda inkor etiladi)",
                    reasonWhyApplicable = "Ushbu modda faqat shaxsda jabrlanuvchini aynan o'ldirish maqsadi yoki uning o'limiga ongli ravishda yo'l qo'yish (egri qasd) bo'lgan taqdirdagina qo'llaniladi."
                )
            ),
            proceduralSafeguards = listOf(
                "JPK 23-modda: Aybsizlik prezumpsiyasi — shaxsning aybi qonunda belgilangan tartibda isbotlanmaguncha u aybsiz hisoblanadi. Barcha shubhalar gumon qilinuvchi foydasiga hal qilinadi.",
                "JPK 50-modda: Himoyachi (advokat) ishtirokining shartligi — og'ir oqibatli ishlarda dastlabki so'roqdan boshlab professional advokat ta'minlanishi shart.",
                "JPK 46-modda: O'ziga va yaqin qarindoshlariga qarshi ko'rsatma bermaslik huquqi (jim turish huquqi).",
                "Dalillarni soxtalashtirishga yoki yashirishga aslo urinmaslik — bu og'irlashtiruvchi javobgarlikka olib keladi."
            ),
            visualNodes = listOf(
                CaseMapNode("n1", "Ko'chadagi mojaro va o'zini himoya zarbasi", "FACT", "Foydalanuvchi bergan yagona musht zarbasi"),
                CaseMapNode("n2", "Qasd va Ehtiyotsizlik chegarasi", "ISSUE", "Qilmish JK 97, JK 104 3-qism yoki JK 102 bilan baholanadimi?"),
                CaseMapNode("n3", "JK 102-modda (Ehtiyotsizlikdan o'lim)", "LAW_ARTICLE", "3 yilgacha jazo. Zarbadan emas, yiqilishdan o'lim sodir bo'lganda.", "https://lex.uz/docs/111453#111597"),
                CaseMapNode("n4", "JK 104-modda 3-qism (Badanga shikast yetkazish oqibatida o'lim)", "LAW_ARTICLE", "8 yildan 10 yilgacha jazo. Zarba bevosita o'limga olib kelganda.", "https://lex.uz/docs/111453#111603"),
                CaseMapNode("n5", "JPK 23 va 50: Aybsizlik prezumpsiyasi & Advokat", "PROCEDURE", "Advokatsiz so'roqda qatnashmaslik, mustaqil sud-tibbiy ekspertiza talab qilish.", "https://lex.uz/docs/111460#111678"),
                CaseMapNode("n6", "Oliy Sud Plenumi Qarori № 13", "SOURCE", "Odam o'ldirish va tan jarohati yetkazishga doir sud amaliyoti tushuntirishlari.", "https://sud.uz")
            ),
            visualEdges = listOf(
                CaseMapEdge("n1", "n2", "Fakt huquqiy bahoga yo'naltiriladi"),
                CaseMapEdge("n2", "n3", "Ekspertiza bordyur jarohati desa"),
                CaseMapEdge("n2", "n4", "Ekspertiza zarba sababli desa"),
                CaseMapEdge("n3", "n5", "Tergov jarayonida kafolatlar"),
                CaseMapEdge("n4", "n5", "Jiddiy ayblovda advokat kafolati"),
                CaseMapEdge("n2", "n6", "Plenum amaliyotiga asoslanadi")
            ),
            urgentLegalDisclaimer = "DIQQAT VA SHOSHILINCH KO'RSATMA: Bu tahlil faqat o'quv-huquqiy ma'lumot hisoblanadi. Real vaziyatda zudlik bilan professional litsenziyalangan ADVOKATga murojaat qiling! Hech qachon dalillarni yashirishga, voqea joyidan qochishga yoki guvohlarga ta'sir o'tkazishga urinmang."
        ),
        LegalCaseScenario(
            id = "salary_delay_scenario",
            title = "Ish beruvchi 3 oydan buyon oylik maoshni to'lamayapti",
            category = "Mehnat huquqi",
            shortDescription = "MChJ rahbari moliyaviy qiyinchiliklarni ro'kach qilib oylik to'lashni kechiktirmoqda.",
            narrative = "Men xususiy korxonada 2 yildan buyon muhandis bo'lib ishlayman. Oxirgi 3 oy mobaynida ish beruvchi maoshimizni to'lamadi. 'Kompaniyada pul yo'q, xohlasangiz ishdan bo'shang' demoqda. Mehnat daftarcham va shartnomam bor. Qanday huquqlarim bor va pullarimni kompensatsiyasi bilan qanday undira olaman?",
            clarifyingQuestions = listOf(
                ClarifyingQuestion("q_sal_1", "Rasmiy mehnat shartnomangiz mavjudmi?", listOf("Ha, nusxasi qo'limda bor", "Tuzilgan lekin berilmagan", "Norasmiy ishlaganman"), "Talabning qonuniy asosini aniqlash uchun"),
                ClarifyingQuestion("q_sal_2", "Oylik hisoblanganligi haqida hisob-varaqasi (raschyotka) mavjudmi?", listOf("Ha, oylik hisoblangan ammo to'lanmagan", "Hisoblanmagan"), "Qarz summasini isbotlash uchun")
            ),
            factsExtracted = CaseFacts(
                partiesInvolved = listOf("Xodim (Muhandis)", "Ish beruvchi (MChJ rahbari)"),
                timeline = "2 yillik staj -> 3 oy kechiktirilgan oylik maosh",
                actionsAlleged = listOf("Ish beruvchi qonunda belgilangan muddatda haq to'lamagan", "Tazyiq o'tkazgan"),
                mentalStateIntent = "Majburiyatni qasddan bajarmaslik",
                consequences = "Xodimning moddiy qiyinchilikka uchrashi",
                availableEvidence = listOf("Mehnat shartnomasi", "Bank kartasi ko'chirmasi"),
                missingCrucialFacts = listOf("Ichki buyruqlar va hisoblangan oylik miqdori")
            ),
            legalQualifications = listOf(
                LegalQualification("MK 253-modda", "Mehnat kodeksi", "Ish haqini to'lash majburiyati va foizlar", "Ish beruvchi kechiktirilgan har bir kun uchun Markaziy bank qayta moliyalash stavkasi bo'yicha kompensatsiya to'laydi.", "To'liq qanoatlantiriladigan talab", "Qonuniy qat'iy norma"),
                LegalQualification("MJtK 49-modda", "Ma'muriy javobgarlik kodeksi", "Mehnat qonunchiligini buzish", "Mansabdor shaxsga BHMning 5 baravaridan 10 baravarigacha jarima.", "Ma'muriy jazo chorasi", "Davlat mehnat inspeksiyasi tomonidan qo'llaniladi")
            ),
            proceduralSafeguards = listOf(
                "Tuman Mehnat inspeksiyasiga shikoyat arizasi berish",
                "Fuqarolik ishlari bo'yicha tumanlararo sudiga sud buyrug'i (ariza) kiritish (davlat boji to'lanmaydi)"
            ),
            visualNodes = listOf(
                CaseMapNode("sn1", "3 oylik maosh to'lanmagan", "FACT", "Mehnat shartnomasi buzilgan"),
                CaseMapNode("sn2", "Qarz va penya hisoblash", "ISSUE", "MK 253 bo'yicha kompensatsiya"),
                CaseMapNode("sn3", "Mehnat inspeksiyasi yoki Sud buyrug'i", "PROCEDURE", "Sud buyrug'i 3 kunda ko'rib chiqiladi")
            ),
            visualEdges = listOf(
                CaseMapEdge("sn1", "sn2", "Huquqiy talab vujudga keladi"),
                CaseMapEdge("sn2", "sn3", "Sud orqali majburiy undiruv")
            ),
            urgentLegalDisclaimer = "Mehnat nizolari bo'yicha sudga da'vo arizasi berishda xodim sud xarajatlari va davlat bojidan to'liq ozod qilingan."
        )
    )

    fun getScenarioById(id: String): LegalCaseScenario? =
        EDUCATIONAL_SCENARIOS.find { it.id == id }
}
