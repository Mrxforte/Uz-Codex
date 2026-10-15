package com.example.data.local

import com.example.data.model.LegalArticle
import com.example.data.model.LegalCode

object LegislationData {

    val UZBEKISTAN_CODES: List<LegalCode> = listOf(
        LegalCode(
            id = "fk",
            officialUzbekName = "O'zbekiston Respublikasining Fuqarolik kodeksi",
            englishName = "Civil Code of the Republic of Uzbekistan",
            shortName = "Fuqarolik kodeksi",
            documentNumber = "№ 163-I / 256-I",
            adoptionDate = "21.12.1995",
            effectiveDate = "01.03.1997",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/111181",
            description = "Mulk huquqi, shartnomalar, majburiyatlar va fuqarolik muomalasi ishtirokchilarining huquqiy holatini belgilovchi bosh qonun hujjati.",
            totalArticlesEstimate = 1199,
            category = "Xususiy huquq",
            iconName = "balance"
        ),
        LegalCode(
            id = "fpk",
            officialUzbekName = "O'zbekiston Respublikasining Fuqarolik protsessual kodeksi",
            englishName = "Civil Procedure Code",
            shortName = "Fuqarolik protsessual kodeksi",
            documentNumber = "O'RQ-460",
            adoptionDate = "22.01.2018",
            effectiveDate = "01.04.2018",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/3517337",
            description = "Fuqarolik ishlari bo'yicha sudlarda ishlarni ko'rib chiqish va hal etish tartib-taomillarini tartibga soladi.",
            totalArticlesEstimate = 459,
            category = "Protsessual huquq",
            iconName = "court"
        ),
        LegalCode(
            id = "jk",
            officialUzbekName = "O'zbekiston Respublikasining Jinoyat kodeksi",
            englishName = "Criminal Code",
            shortName = "Jinoyat kodeksi",
            documentNumber = "№ 2012-XII",
            adoptionDate = "22.09.1994",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/111453",
            description = "Qaysi ijtimoiy xavfli qilmishlar jinoyat ekanligini, ularni sodir etgan shaxslarga qo'llaniladigan jazo turlarini belgilaydi.",
            totalArticlesEstimate = 302,
            category = "Jinoiy huquq",
            iconName = "shield"
        ),
        LegalCode(
            id = "jpk",
            officialUzbekName = "O'zbekiston Respublikasining Jinoyat-protsessual kodeksi",
            englishName = "Criminal Procedure Code",
            shortName = "Jinoyat-protsessual kodeksi",
            documentNumber = "№ 2013-XII",
            adoptionDate = "22.09.1994",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/111460",
            description = "Jinoyat ishlarini qo'zg'atish, tergov qilish, sudda ko'rish va hukm chiqarishda shaxs huquqlarini himoya qilish tartibi.",
            totalArticlesEstimate = 611,
            category = "Protsessual huquq",
            iconName = "gavel"
        ),
        LegalCode(
            id = "jik",
            officialUzbekName = "O'zbekiston Respublikasining Jinoyat-ijroiya kodeksi",
            englishName = "Criminal Executive Code",
            shortName = "Jinoyat-ijroiya kodeksi",
            documentNumber = "№ 409-I",
            adoptionDate = "25.04.1997",
            effectiveDate = "01.09.1997",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/163629",
            description = "Jinoiy jazolarni ijro etish va mahkumlarning huquqiy maqomini belgilovchi qonun hujjatlari to'plami.",
            totalArticlesEstimate = 197,
            category = "Jinoiy huquq",
            iconName = "lock"
        ),
        LegalCode(
            id = "ipk",
            officialUzbekName = "O'zbekiston Respublikasining Iqtisodiy protsessual kodeksi",
            englishName = "Economic Procedure Code",
            shortName = "Iqtisodiy protsessual kodeks",
            documentNumber = "O'RQ-461",
            adoptionDate = "24.01.2018",
            effectiveDate = "01.04.2018",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/3523891",
            description = "Tadbirkorlik va iqtisodiy sohadagi nizolarni iqtisodiy sudlarda ko'rib chiqish tartibi.",
            totalArticlesEstimate = 348,
            category = "Protsessual huquq",
            iconName = "business"
        ),
        LegalCode(
            id = "msiyk",
            officialUzbekName = "O'zbekiston Respublikasining Ma'muriy sud ishlarini yuritish to'g'risidagi kodeksi",
            englishName = "Code of Administrative Court Proceedings",
            shortName = "Ma'muriy sud ishlarini yuritish kodeksi",
            documentNumber = "O'RQ-462",
            adoptionDate = "25.01.2018",
            effectiveDate = "01.04.2018",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/3527129",
            description = "Davlat organlari va mansabdor shaxslarning qarorlari hamda harakatlari ustidan berilgan shikoyatlarni sudda ko'rish.",
            totalArticlesEstimate = 286,
            category = "Protsessual huquq",
            iconName = "account_balance"
        ),
        LegalCode(
            id = "mjtk",
            officialUzbekName = "O'zbekiston Respublikasining Ma'muriy javobgarlik to'g'risidagi kodeksi",
            englishName = "Code of Administrative Responsibility",
            shortName = "Ma'muriy javobgarlik kodeksi",
            documentNumber = "№ 2015-XII",
            adoptionDate = "22.09.1994",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/97664",
            description = "Ma'muriy huquqbuzarliklar tarkibi, ma'muriy jazolar va ularni qo'llash tartibini belgilaydi.",
            totalArticlesEstimate = 352,
            category = "Ommaviy huquq",
            iconName = "warning"
        ),
        LegalCode(
            id = "mk",
            officialUzbekName = "O'zbekiston Respublikasining Mehnat kodeksi",
            englishName = "Labour Code (New Edition)",
            shortName = "Mehnat kodeksi",
            documentNumber = "O'RQ-798",
            adoptionDate = "28.10.2022",
            effectiveDate = "30.04.2023",
            status = "Amalda (Yangi tahrir)",
            lexUzUrl = "https://lex.uz/docs/6257288",
            description = "Xodim va ish beruvchi o'rtasidagi mehnat munosabatlari, ish haqi, mehnat ta'tillari va xavfsiz mehnat kafolatlari.",
            totalArticlesEstimate = 581,
            category = "Ijtimoiy huquq",
            iconName = "work"
        ),
        LegalCode(
            id = "ok",
            officialUzbekName = "O'zbekiston Respublikasining Oila kodeksi",
            englishName = "Family Code",
            shortName = "Oila kodeksi",
            documentNumber = "№ 607-I",
            adoptionDate = "30.04.1998",
            effectiveDate = "01.09.1998",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/104720",
            description = "Nikoh tuzish va bekor qilish, er-xotin, ota-ona va bolalarning shaxsiy va mulkiy munosabatlari, aliment masalalari.",
            totalArticlesEstimate = 238,
            category = "Ijtimoiy huquq",
            iconName = "family"
        ),
        LegalCode(
            id = "sk",
            officialUzbekName = "O'zbekiston Respublikasining Soliq kodeksi",
            englishName = "Tax Code (New Edition)",
            shortName = "Soliq kodeksi",
            documentNumber = "O'RQ-599",
            adoptionDate = "30.12.2019",
            effectiveDate = "01.01.2020",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/4674902",
            description = "Soliqlar va yig'imlarni hisoblash, to'lash tartibi, soliq to'lovchilar va soliq organlarining huquq va majburiyatlari.",
            totalArticlesEstimate = 485,
            category = "Iqtisodiy huquq",
            iconName = "receipt"
        ),
        LegalCode(
            id = "bk",
            officialUzbekName = "O'zbekiston Respublikasining Bojxona kodeksi",
            englishName = "Customs Code",
            shortName = "Bojxona kodeksi",
            documentNumber = "O'RQ-399",
            adoptionDate = "20.01.2016",
            effectiveDate = "21.04.2016",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/2876354",
            description = "Tovarlar va transport vositalarini O'zbekiston bojxona chegarasi orqali olib o'tish va bojxona rasmiylashtiruvi qoidalari.",
            totalArticlesEstimate = 413,
            category = "Iqtisodiy huquq",
            iconName = "local_shipping"
        ),
        LegalCode(
            id = "byk",
            officialUzbekName = "O'zbekiston Respublikasining Byudjet kodeksi",
            englishName = "Budget Code",
            shortName = "Byudjet kodeksi",
            documentNumber = "O'RQ-360",
            adoptionDate = "26.12.2013",
            effectiveDate = "01.01.2014",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/2304949",
            description = "O'zbekiston byudjet tizimini shakllantirish, daromadlar va xarajatlarni taqsimlash, byudjet intizomini ta'minlash tartibi.",
            totalArticlesEstimate = 193,
            category = "Iqtisodiy huquq",
            iconName = "account_balance_wallet"
        ),
        LegalCode(
            id = "yk",
            officialUzbekName = "O'zbekiston Respublikasining Yer kodeksi",
            englishName = "Land Code",
            shortName = "Yer kodeksi",
            documentNumber = "№ 598-I",
            adoptionDate = "30.04.1998",
            effectiveDate = "01.07.1998",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/149947",
            description = "Yer uchastkalariga egalik qilish, foydalanish, ijara va xususiylashtirish munosabatlarini tartibga soladi.",
            totalArticlesEstimate = 91,
            category = "Resurs huquqi",
            iconName = "terrain"
        ),
        LegalCode(
            id = "ujk",
            officialUzbekName = "O'zbekiston Respublikasining Uy-joy kodeksi",
            englishName = "Housing Code",
            shortName = "Uy-joy kodeksi",
            documentNumber = "№ 713-I",
            adoptionDate = "24.12.1998",
            effectiveDate = "01.04.1999",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/106136",
            description = "Fuqarolarning uy-joyga bo'lgan huquqlarini amalga oshirish, uy-joy fondidan foydalanish va kommunal xizmatlar tartibi.",
            totalArticlesEstimate = 141,
            category = "Ijtimoiy huquq",
            iconName = "home"
        ),
        LegalCode(
            id = "shk",
            officialUzbekName = "O'zbekiston Respublikasining Shaharsozlik kodeksi",
            englishName = "Urban Planning Code (New Edition)",
            shortName = "Shaharsozlik kodeksi",
            documentNumber = "O'RQ-676",
            adoptionDate = "22.02.2021",
            effectiveDate = "24.05.2021",
            status = "Amalda (Yangi tahrir)",
            lexUzUrl = "https://lex.uz/docs/5301825",
            description = "Aholi punktlarini loyihalashtirish, qurish va shaharsozlik faoliyatini yuritish qoidalari.",
            totalArticlesEstimate = 85,
            category = "Iqtisodiy huquq",
            iconName = "apartment"
        ),
        LegalCode(
            id = "hk",
            officialUzbekName = "O'zbekiston Respublikasining Havo kodeksi",
            englishName = "Air Code",
            shortName = "Havo kodeksi",
            documentNumber = "№ 863-XII",
            adoptionDate = "07.05.1993",
            effectiveDate = "01.09.1993",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/141094",
            description = "O'zbekiston havo hududidan foydalanish, aviatsiya faoliyati va havo transporti orqali tashish qoidalari.",
            totalArticlesEstimate = 135,
            category = "Transport huquqi",
            iconName = "flight"
        ),
        LegalCode(
            id = "sayk",
            officialUzbekName = "O'zbekiston Respublikasining Saylov kodeksi",
            englishName = "Election Code",
            shortName = "Saylov kodeksi",
            documentNumber = "O'RQ-544",
            adoptionDate = "25.06.2019",
            effectiveDate = "26.06.2019",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/4386848",
            description = "O'zbekiston Respublikasi Prezidenti, Oliy Majlis va mahalliy kengashlar deputatlari saylovlarini o'tkazish tartibi.",
            totalArticlesEstimate = 103,
            category = "Konstitutsiyaviy huquq",
            iconName = "how_to_vote"
        ),
        LegalCode(
            id = "suk",
            officialUzbekName = "O'zbekiston Respublikasining Suv kodeksi",
            englishName = "Water Code (Water and Water Use Law)",
            shortName = "Suv kodeksi / Qonuni",
            documentNumber = "№ 837-XII",
            adoptionDate = "06.05.1993",
            effectiveDate = "01.07.1993",
            status = "Amalda",
            lexUzUrl = "https://lex.uz/docs/91795",
            description = "Suv resurslaridan oqilona foydalanish, suv obyektlarini muhofaza qilish va gidrotexnik inshootlar xavfsizligi.",
            totalArticlesEstimate = 119,
            category = "Resurs huquqi",
            iconName = "water_drop"
        )
    )

    val KEY_ARTICLES: List<LegalArticle> = listOf(
        // Jinoyat kodeksi
        LegalArticle(
            id = "jk_97",
            codeId = "jk",
            chapter = "I Bo'lim. Shaxsga qarshi jinoyatlar. Hayotga qarshi jinoyatlar",
            articleNumber = 97,
            articleNumberStr = "97-modda",
            title = "Qasddan odam o'ldirish",
            content = """1. Qasddan odam o'ldirish — o'n yildan o'n besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.
2. Javobgarlikni og'irlashtiradigan holatlarda qasddan odam o'ldirish, ya'ni:
a) ikki yoki undan ortiq shaxsni;
b) homiladorligi aybdorga ayon bo'lgan ayolni;
v) ojiz ahvolda ekanligi aybdorga ayon bo'lgan shaxsni;
g) o'z xizmat yoki fuqarolik burchini bajarishi munosabati bilan shaxsni yoki uning yaqin qarindoshlarini;
d) boshqa shaxslarning hayoti uchun xavfli bo'lgan usulda;
e) ommaviy tartibsizliklar jarayonida;
j) o'ta shafqatsizlik bilan;
z) g'arazli niyatlarda;
i) milliy, irqiy, etnik yoki diniy adovat zamirida;
l) bir guruh shaxslar yoki uyushgan guruh tomonidan sodir etilgan bo'lsa —
o'n besh yildan yigirma besh yilgacha ozodlikdan mahrum qilish yoki umrbod ozodlikdan mahrum qilish jazosi tayinlanadi.""",
            lexUzUrl = "https://lex.uz/docs/111453#111585",
            effectiveDate = "01.04.1995 (oxirgi o'zgartirishlar bilan)",
            status = "Amalda",
            relatedArticleIds = listOf("jk_98", "jk_102", "jk_104", "jk_37", "jpk_23")
        ),
        LegalArticle(
            id = "jk_98",
            codeId = "jk",
            chapter = "Hayotga qarshi jinoyatlar",
            articleNumber = 98,
            articleNumberStr = "98-modda",
            title = "Kuchli ruhiy hayajonlanish holatida qasddan odam o'ldirish",
            content = """Jabrlanuvchi tomonidan qilingan g'ayriqonuniy zo'rlik yoki og'ir haqorat yoxud uning boshqa g'ayriqonuniy harakatlari (harakatsizligi) tufayli to'satdan vujudga kelgan kuchli ruhiy hayajonlanish (fiziologik affekt) holatida qasddan odam o'ldirish —
besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.""",
            lexUzUrl = "https://lex.uz/docs/111453#111589",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jk_97", "jk_102")
        ),
        LegalArticle(
            id = "jk_102",
            codeId = "jk",
            chapter = "Hayotga qarshi jinoyatlar",
            articleNumber = 102,
            articleNumberStr = "102-modda",
            title = "Ehtiyotsizlik orqasida odam o'ldirish",
            content = """1. Ehtiyotsizlik orqasida odam o'ldirish — ikki yilgacha axloq tuzatish ishlari yoki bir yildan uch yilgacha ozodlikni cheklash yoxud uch yilgacha ozodlikdan mahrum qilish bilan jazolanadi.
2. Ikki yoki undan ortiq shaxsning o'limiga sabab bo'lgan ehtiyotsizlik orqasida odam o'ldirish — uch yildan besh yilgacha ozodlikni cheklash yoxud uch yildan besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.""",
            lexUzUrl = "https://lex.uz/docs/111453#111597",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jk_97", "jk_104")
        ),
        LegalArticle(
            id = "jk_104",
            codeId = "jk",
            chapter = "Sog'liqqa qarshi jinoyatlar",
            articleNumber = 104,
            articleNumberStr = "104-modda",
            title = "Qasddan badanga og'ir shikast yetkazish",
            content = """1. Qasddan badanga og'ir shikast yetkazish, ya'ni hayot uchun xavfli bo'lgan qasddan tan jarohati yetkazish — uch yildan besh yilgacha ozodlikni cheklash yoxud uch yildan besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.
...
3. Qasddan badanga og'ir shikast yetkazish:
d) jabrlanuvchining o'limiga sabab bo'lsa —
sakkiz yildan o'n yilgacha ozodlikdan mahrum qilish bilan jazolanadi. (DIQQAT: Ushbu bandda shaxsning qasdi o'ldirishga emas, faqat tan jarohati yetkazishga qaratilgan bo'ladi, o'lim esa ehtiyotsizlik oqibatida yuz beradi).""",
            lexUzUrl = "https://lex.uz/docs/111453#111603",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jk_97", "jk_102")
        ),
        LegalArticle(
            id = "jk_37",
            codeId = "jk",
            chapter = "Qilmishning jinoiyligini istisno qiladigan holatlar",
            articleNumber = 37,
            articleNumberStr = "37-modda",
            title = "Zaruriy mudofaa",
            content = """1. Shaxsning o'zini yoki boshqa shaxslarning hayoti, sog'lig'i va huquqlarini, jamiyat yoki davlat manfaatlarini qonunga xilof tajovuzdan tajovuzchiga zarar yetkazgan holda himoya qilishi zaruriy mudofaa deb topiladi va bu jinoyat hisoblanmaydi.
2. Tajovuzchining hayotiga bevosita real xavf tug'ilgan hollarda zaruriy mudofaa chegarasidan chetga chiqish deb baholanmaydi.""",
            lexUzUrl = "https://lex.uz/docs/111453#111512",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jk_97", "jk_104")
        ),
        // Jinoyat-protsessual kodeksi
        LegalArticle(
            id = "jpk_23",
            codeId = "jpk",
            chapter = "Asosiy prinsiplar",
            articleNumber = 23,
            articleNumberStr = "23-modda",
            title = "Aybsizlik prezumpsiyasi",
            content = """1. Gumon qilinuvchi, ayblanuvchi yoki sudlanuvchi uning jinoyat sodir etishda aybdorligi qonunda nazarda tutilgan tartibda isbotlanmaguncha va sudning qonuniy kuchga kirgan hukmi bilan aniqlanmaguncha aybsiz hisoblanadi.
2. Aybdorlikka oid barcha bartaraf etib bo'lmaydigan shubhalar gumon qilinuvchi, ayblanuvchi yoki sudlanuvchining foydasiga hal qilinishi lozim.
3. Shaxs o'zining aybsizligini isbotlab berishi shart emas.""",
            lexUzUrl = "https://lex.uz/docs/111460#111678",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jpk_46", "jpk_50")
        ),
        LegalArticle(
            id = "jpk_50",
            codeId = "jpk",
            chapter = "Himoyachi",
            articleNumber = 50,
            articleNumberStr = "50-modda",
            title = "Himoyachining ishtirok etishi shart bo'lgan hollar",
            content = """Surishtiruvchi, tergovchi, prokuror va sud quyidagi hollarda himoyachining ishda ishtirok etishini ta'minlashi shart:
1) voyaga yetmaganlarning ishlarida;
2) jismoniy yoki ruhiy nuqsonlari tufayli himoyalanish huquqini o'zi amalga oshirolmaydigan shaxslarning ishlarida;
3) sudlov yuritilayotgan tilni bilmaydigan shaxslarning ishlarida;
4) o'n yildan ortiq muddatga ozodlikdan mahrum qilish yoki umrbod ozodlikdan mahrum qilish jazosi tayinlanishi mumkin bo'lgan jinoyatlarni sodir etishda gumon qilinayotgan yoki ayblanayotgan shaxslarning ishlarida.""",
            lexUzUrl = "https://lex.uz/docs/111460#111745",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jpk_23", "jk_97")
        ),
        // Mehnat kodeksi
        LegalArticle(
            id = "mk_112",
            codeId = "mk",
            chapter = "Ish vaqti",
            articleNumber = 112,
            articleNumberStr = "112-modda",
            title = "Ish vaqtining normal davomiyligi",
            content = """Xodim uchun ish vaqtining normal davomiyligi haftasiga qirq soatdan oshishi mumkin emas.
Olti kunlik ish haftasida har kungi ishning davomiyligi yetti soatdan, besh kunlik ish haftasida esa sakkiz soatdan ortib ketmasligi shart.""",
            lexUzUrl = "https://lex.uz/docs/6257288#6257790",
            effectiveDate = "30.04.2023",
            status = "Amalda",
            relatedArticleIds = listOf("mk_253")
        ),
        LegalArticle(
            id = "mk_253",
            codeId = "mk",
            chapter = "Mehnatga haq to'lash",
            articleNumber = 253,
            articleNumberStr = "253-modda",
            title = "Ish haqini to'lash muddatlari va kechiktirilganlik uchun javobgarlik",
            content = """Ish haqi har yarim oyda kamida bir marta to'lanishi lozim. Ish haqini to'lash kunlari jamoa shartnomasida yoki ichki mehnat tartibi qoidalarida belgilanadi.
Ish beruvchi tomonidan ish haqi belgilangan muddatdan kechiktirilgan taqdirda, ish beruvchi xodimga har bir kechiktirilgan kun uchun O'zbekiston Respublikasi Markaziy bankining qayta moliyalash stavkasi miqdorida foizlar (pul kompensatsiyasi) to'lashi shart.""",
            lexUzUrl = "https://lex.uz/docs/6257288#6258120",
            effectiveDate = "30.04.2023",
            status = "Amalda",
            relatedArticleIds = listOf("mk_112", "fk_14")
        ),
        // Fuqarolik kodeksi
        LegalArticle(
            id = "fk_14",
            codeId = "fk",
            chapter = "Fuqarolik huquqlarini himoya qilish",
            articleNumber = 14,
            articleNumberStr = "14-modda",
            title = "Zararni qoplash",
            content = """1. Huquqi buzilgan shaxs o'ziga yetkazilgan zararning to'liq qoplanishini talab qilishi mumkin.
2. Zarar deganda huquqi buzilgan shaxsning buzilgan huquqini tiklash uchun qilgan yoki qilishi lozim bo'lgan xarajatlari, uning mol-mulki yo'qolishi yoki shikastlanishi (haqiqiy zarar), shuningdek uning huquqlari buzilmaganida odatdagi fuqarolik muomalasi sharoitida olishi mumkin bo'lgan daromadlari (boy berilgan foyda) tushuniladi.""",
            lexUzUrl = "https://lex.uz/docs/111181#111245",
            effectiveDate = "01.03.1997",
            status = "Amalda",
            relatedArticleIds = listOf("fk_535", "fk_985")
        ),
        LegalArticle(
            id = "fk_535",
            codeId = "fk",
            chapter = "Mulk ijarasi",
            articleNumber = 535,
            articleNumberStr = "535-modda",
            title = "Mulk ijarasi shartnomasi",
            content = """Mulk ijarasi shartnomasi bo'yicha ijaraga beruvchi ijarachiga mol-mulkni vaqtincha egalik qilish va foydalanish yoki faqat foydalanish uchun haq evaziga topshirish majburiyatini oladi.
Ko'chmas mulk ijarasi shartnomasi bir yildan ortiq muddatga tuzilgan bo'lsa, davlat ro'yxatidan o'tkazilishi shart.""",
            lexUzUrl = "https://lex.uz/docs/111181#111890",
            effectiveDate = "01.03.1997",
            status = "Amalda",
            relatedArticleIds = listOf("fk_14", "ujk")
        ),
        LegalArticle(
            id = "fk_985",
            codeId = "fk",
            chapter = "Zarar yetkazishdan kelib chiqadigan majburiyatlar",
            articleNumber = 985,
            articleNumberStr = "985-modda",
            title = "Yetkazilgan zararni qoplash majburiyatining umumiy asoslari",
            content = """1. G'ayriqonuniy harakat (harakatsizlik) tufayli fuqaroning shaxsiga yoki mol-mulkiga yetkazilgan zarar, shuningdek yuridik shaxsga yetkazilgan zarar, zararni yetkazgan shaxs tomonidan to'liq hajmda qoplanishi lozim.
2. Zarar yetkazgan shaxs, agar zarar o'z aybi bilan yetkazilmaganligini isbotlasa, zararni qoplashdan ozod qilinadi.""",
            lexUzUrl = "https://lex.uz/docs/111181#112450",
            effectiveDate = "01.03.1997",
            status = "Amalda",
            relatedArticleIds = listOf("fk_14", "mjtk_128")
        ),
        // Oila kodeksi
        LegalArticle(
            id = "ok_96",
            codeId = "ok",
            chapter = "Ota-ona va bolalarning aliment majburiyatlari",
            articleNumber = 96,
            articleNumberStr = "96-modda",
            title = "Ota-onaning voyaga yetmagan bolalariga ta'minot berish majburiyati",
            content = """Ota-ona voyaga yetmagan bolalariga ta'minot berishi shart. Voyaga yetmagan bolalariga ta'minot berish majburiyatini ixtiyoriy ravishda bajarmagan ota (ona)dan sud tartibida aliment undiriladi.""",
            lexUzUrl = "https://lex.uz/docs/104720#104880",
            effectiveDate = "01.09.1998",
            status = "Amalda",
            relatedArticleIds = listOf("ok_99")
        ),
        // Ma'muriy javobgarlik to'g'risidagi kodeks
        LegalArticle(
            id = "mjtk_313",
            codeId = "mjtk",
            chapter = "Qarorlar ustidan shikoyat berish",
            articleNumber = 313,
            articleNumberStr = "313-modda",
            title = "Ma'muriy huquqbuzarlik to'g'risidagi ish yuzasidan chiqarilgan qaror ustidan shikoyat qilish",
            content = """Ma'muriy huquqbuzarlik to'g'risidagi ish yuzasidan chiqarilgan qaror ustidan huquqbuzarlik sodir etgan shaxs, shuningdek jabrlanuvchi qaror nusxasi topshirilgan yoki ma'lum qilingan kundan boshlab o'n kun ichida yuqori turuvchi organga (mansabdor shaxsga) yoki ma'muriy sudga shikoyat qilishi mumkin.""",
            lexUzUrl = "https://lex.uz/docs/97664#98210",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("msiyk")
        )
    )

    fun getCodeById(id: String): LegalCode? = UZBEKISTAN_CODES.find { it.id == id }

    fun getArticlesForCode(codeId: String): List<LegalArticle> =
        KEY_ARTICLES.filter { it.codeId == codeId }

    fun searchArticles(query: String): List<LegalArticle> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return KEY_ARTICLES
        return KEY_ARTICLES.filter { article ->
            article.articleNumberStr.lowercase().contains(q) ||
            article.title.lowercase().contains(q) ||
            article.content.lowercase().contains(q) ||
            article.chapter.lowercase().contains(q)
        }
    }
}
