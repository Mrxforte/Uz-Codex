package com.example.data.local

import android.content.Context
import com.example.data.model.LegalArticle
import com.example.data.model.LegalCode
import com.example.ui.screens.cleanPdfDocumentText
import org.json.JSONArray

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
            description = "O'zbekiston Byudjet tizimini shakllantirish, ijro etish hamda byudjet jarayoni ishtirokchilarining vakolatlarini belgilaydi.",
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
            description = "Suv resurslaridan oqilona foydalanish, suv obyektlarini muhofaza qilish va gidrotexnik inshootlar xavfsizligi.",
            totalArticlesEstimate = 119,
            category = "Resurs huquqi",
            iconName = "water_drop"
        )
    )

    val KEY_ARTICLES: List<LegalArticle> = listOf(
        // Fuqarolik kodeksi (fk)
        LegalArticle(
            id = "fk_14",
            codeId = "fk",
            chapter = "Fuqarolik huquqlarini himoya qilish",
            articleNumber = 14,
            articleNumberStr = "14-modda",
            title = "Zararni qoplash",
            content = """1. Huquqi buzilgan shaxs o'ziga yetkazilgan zararning to'liq qoplanishini talab qilishi mumkin.
2. Zarar deganda huquqi buzilgan shaxsning buzilgan huquqini tiklash uchun qilgan yoki qilishi lozim bo'lgan xarajatlari, uning mol-mulki yo'qolishi yoki shikastlanishi (haqiqiy zarar), shuningdek uning huquqlari buzilmaganida odatdagi fuqarolik muomalasi sharoitida olishi mumkin bo'lgan daromadlari (boy berilgan foyda) tushuniladi.""",
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
            effectiveDate = "01.03.1997",
            status = "Amalda",
            relatedArticleIds = listOf("fk_14", "ujk_1")
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
            effectiveDate = "01.03.1997",
            status = "Amalda",
            relatedArticleIds = listOf("fk_14", "mjtk_313")
        ),
        LegalArticle(
            id = "fk_732",
            codeId = "fk",
            chapter = "Qarz shartnomasi",
            articleNumber = 732,
            articleNumberStr = "732-modda",
            title = "Qarz shartnomasi",
            content = """Qarz shartnomasi bo'yicha bir taraf (qarz beruvchi) ikkinchi tarafga (qarz oluvchiga) pul yoki boshqa ashyolarni mulk qilib beradi, qarz oluvchi esa olingan summani shartnomada belgilangan muddatda qaytarish majburiyatini oladi. Fuqarolar o'rtasidagi qarz shartnomasi BHMning o'n baravaridan ortiq bo'lsa, yozma shaklda tuzilishi shart.""",
            effectiveDate = "01.03.1997",
            status = "Amalda",
            relatedArticleIds = listOf("fk_14", "fk_985")
        ),

        // Fuqarolik protsessual kodeksi (fpk)
        LegalArticle(
            id = "fpk_1",
            codeId = "fpk",
            chapter = "Asosiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Fuqarolik sud ishlarini yuritish to'g'risidagi qonun hujjatlari",
            content = """Fuqarolik ishlari bo'yicha sudlov ishlarini yuritish tartibi O'zbekiston Respublikasining Fuqarolik protsessual kodeksi hamda unga muvofiq qabul qilinadigan boshqa qonunlar bilan belgilanadi.""",
            effectiveDate = "01.04.2018",
            status = "Amalda"
        ),
        LegalArticle(
            id = "fpk_209",
            codeId = "fpk",
            chapter = "Sud majlisi va masofaviy ishtirok",
            articleNumber = 209,
            articleNumberStr = "209-modda",
            title = "Videokonferensaloqa rejimida sud majlisini o'tkazish",
            content = """Sud majlisi protsess ishtirokchilarining videokonferensaloqa tizimidan foydalangan holda masofadan turib ishtirok etishi orqali o'tkazilishi mumkin. Shaxsni identifikatsiyalash elektron raqamli tizimlar orqali amalga oshiriladi.""",
            effectiveDate = "01.04.2018",
            status = "Amalda"
        ),

        // Jinoyat kodeksi (jk)
        LegalArticle(
            id = "jk_97",
            codeId = "jk",
            chapter = "Shaxsga qarshi jinoyatlar",
            articleNumber = 97,
            articleNumberStr = "97-modda",
            title = "Qasddan odam o'ldirish",
            content = """1. Qasddan odam o'ldirish — o'n yildan o'n besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.
2. Javobgarlikni og'irlashtiradigan holatlarda qasddan odam o'ldirish, ya'ni ikki yoki undan ortiq shaxsni, ojiz ahvoldagi shaxsni yoki o'z fuqarolik burchini bajarishi munosabati bilan shaxsni o'ldirish — o'n besh yildan yigirma besh yilgacha ozodlikdan mahrum qilish yoki umrbod ozodlikdan mahrum qilish jazosi tayinlanadi.""",
            effectiveDate = "01.04.1995",
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
            content = """Jabrlanuvchi tomonidan qilingan g'ayriqonuniy zo'rlik yoki og'ir haqorat tufayli to'satdan vujudga kelgan kuchli ruhiy hayajonlanish holatida qasddan odam o'ldirish — besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.""",
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
            content = """1. Ehtiyotsizlik orqasida odam o'ldirish — ikki yilgacha axloq tuzatish ishlari yoki uch yilgacha ozodlikdan mahrum qilish bilan jazolanadi.""",
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
            content = """1. Qasddan badanga og'ir shikast yetkazish — uch yildan besh yilgacha ozodlikni cheklash yoxud uch yildan besh yilgacha ozodlikdan mahrum qilish bilan jazolanadi.
3. Jabrlanuvchining o'limiga sabab bo'lsa — sakkiz yildan o'n yilgacha ozodlikdan mahrum qilish bilan jazolanadi.""",
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
            content = """Shaxsning o'zini yoki boshqa shaxslarning hayoti va huquqlarini tajovuzdan tajovuzchiga zarar yetkazgan holda himoya qilishi zaruriy mudofaa deb topiladi va bu jinoyat hisoblanmaydi.""",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jk_97", "jk_104")
        ),
        LegalArticle(
            id = "jk_118",
            codeId = "jk",
            chapter = "Jinsiy erkinlikka qarshi jinoyatlar",
            articleNumber = 118,
            articleNumberStr = "118-modda",
            title = "Nomusga tegish",
            content = """Nomusga tegish, ya'ni zo'rlik ishlatib, do'q-po'pisa qilib yoki jabrlanuvchining ojizligidan foydalanib jinsiy ehtiyojni qondirish — besh yildan sakkiz yilgacha ozodlikdan mahrum qilish bilan jazolanadi.""",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jk_97", "jpk_23", "jpk_50")
        ),
        LegalArticle(
            id = "jk_168",
            codeId = "jk",
            chapter = "O'zgalar mol-mulkini talan-toroj qilish",
            articleNumber = 168,
            articleNumberStr = "168-modda",
            title = "Firibgarlik",
            content = """Oldash yoki ishonchni suiiste'mol qilish yo'li bilan o'zganing mol-mulkini yoki o'zganing mol-mulkiga bo'lgan huquqini qo'lga kiritish. Bank kartalari va axborot tizimlaridan foydalanib sodir etilgan firibgarlik uchun jazo 5 yildan 8 yilgacha oshirilgan.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),

        // Jinoyat-protsessual kodeksi (jpk)
        LegalArticle(
            id = "jpk_23",
            codeId = "jpk",
            chapter = "Asosiy prinsiplar",
            articleNumber = 23,
            articleNumberStr = "23-modda",
            title = "Aybsizlik prezumpsiyasi",
            content = """1. Gumon qilinuvchi, ayblanuvchi yoki sudlanuvchi uning jinoyat sodir etishda aybdorligi qonunda nazarda tutilgan tartibda isbotlanmaguncha va sudning qonuniy kuchga kirgan hukmi bilan aniqlanmaguncha aybsiz hisoblanadi.
2. Aybdorlikka oid barcha bartaraf etib bo'lmaydigan shubhalar gumon qilinuvchi, ayblanuvchi yoki sudlanuvchining foydasiga hal qilinishi lozim.""",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jpk_50")
        ),
        LegalArticle(
            id = "jpk_50",
            codeId = "jpk",
            chapter = "Himoyachi",
            articleNumber = 50,
            articleNumberStr = "50-modda",
            title = "Himoyachining ishtirok etishi shart bo'lgan hollar",
            content = """Surishtiruvchi, tergovchi, prokuror va sud quyidagi hollarda himoyachining ishda ishtirok etishini ta'minlashi shart: voyaga yetmaganlar, jismoniy yoki ruhiy nuqsonli shaxslar, tili bilmaydiganlar va og'ir jazo nazarda tutilgan ishlar bo'yicha.""",
            effectiveDate = "01.04.1995",
            status = "Amalda",
            relatedArticleIds = listOf("jpk_23")
        ),

        // Jinoyat-ijroiya kodeksi (jik)
        LegalArticle(
            id = "jik_1",
            codeId = "jik",
            chapter = "Umumiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Jinoyat-ijroiya qonun hujjatlarining vazifalari",
            content = """Jinoyat-ijroiya qonun hujjatlarining vazifalari jinoiy jazolarni ijro etish, mahkumlarni ahloqlanib, qayta tarbiyalanishiga ko'maklashish hamda yangi jinoyatlar sodir etilishining oldini olishdan iborat.""",
            effectiveDate = "01.09.1997",
            status = "Amalda"
        ),
        LegalArticle(
            id = "jik_8",
            codeId = "jik",
            chapter = "Mahkumlarning huquqiy maqomi",
            articleNumber = 8,
            articleNumberStr = "8-modda",
            title = "Mahkumlarning asosiy huquqlari",
            content = """Mahkumlar o'z shaxsiy xavfsizligini ta'minlash, tibbiy yordam olish, advokat bilan holi uchrashish hamda o'z huquqlari bo'yicha shikoyat va arizalar berish huquqiga ega.""",
            effectiveDate = "01.09.1997",
            status = "Amalda"
        ),

        // Iqtisodiy protsessual kodeks (ipk)
        LegalArticle(
            id = "ipk_1",
            codeId = "ipk",
            chapter = "Asosiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Iqtisodiy sud ishlarini yuritish vazifalari",
            content = """Iqtisodiy sud ishlarini yuritish vazifalari tadbirkorlik va iqtisodiy sohadagi buzilgan yoki nizolashilayotgan huquqlarni hamda qonun bilan qo'riqlanadigan manfaatlarni himoya qilishdan iborat.""",
            effectiveDate = "01.04.2018",
            status = "Amalda"
        ),
        LegalArticle(
            id = "ipk_25",
            codeId = "ipk",
            chapter = "Sudga taalluqlilik",
            articleNumber = 25,
            articleNumberStr = "25-modda",
            title = "Iqtisodiy sudga taalluqli ishlar",
            content = """Iqtisodiy sud yuridik shaxslar va yakka tartibdagi tadbirkorlar o'rtasidagi fuqarolik va ma'muriy huquqiy munosabatlardan kelib chiqadigan iqtisodiy nizolarni ko'rib chiqadi.""",
            effectiveDate = "01.04.2018",
            status = "Amalda"
        ),

        // Ma'muriy sud ishlarini yuritish kodeksi (msiyk)
        LegalArticle(
            id = "msiyk_1",
            codeId = "msiyk",
            chapter = "Asosiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Ma'muriy sud ishlarini yuritish vazifalari",
            content = """Ma'muriy sud ishlarini yuritish vazifasi fuqarolar va yuridik shaxslarning davlat organlari hamda mansabdor shaxslarning g'ayriqonuniy qarorlari va harakatlaridan himoyalanishini ta'minlashdan iborat.""",
            effectiveDate = "01.04.2018",
            status = "Amalda"
        ),
        LegalArticle(
            id = "msiyk_26",
            codeId = "msiyk",
            chapter = "Sudga taalluqlilik",
            articleNumber = 26,
            articleNumberStr = "26-modda",
            title = "Ma'muriy sudga taalluqli ishlar",
            content = """Ma'muriy sud davlat organlari, fuqarolarning o'zini o'zi boshqarish organlari hamda ularning mansabdor shaxslari qarorlari ustidan berilgan shikoyatlarni ko'rib chiqadi.""",
            effectiveDate = "01.04.2018",
            status = "Amalda"
        ),

        // Ma'muriy javobgarlik kodeksi (mjtk)
        LegalArticle(
            id = "mjtk_1",
            codeId = "mjtk",
            chapter = "Umumiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Ma'muriy javobgarlik to'g'risidagi qonun hujjatlari",
            content = """Ma'muriy javobgarlik to'g'risidagi qonun hujjatlari ushbu Kodeksdan, O'zbekiston Respublikasi qonunlaridan hamda ularga muvofiq qabul qilinadigan boshqa hujjatlardan iborat.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),
        LegalArticle(
            id = "mjtk_125",
            codeId = "mjtk",
            chapter = "Yo'l harakati qoidalarini buzish",
            articleNumber = 125,
            articleNumberStr = "125-modda",
            title = "Transport vositalaridan foydalanish va ularni boshqarish qoidalarini buzish",
            content = """Xavfsizlik kamarini taqmaslik, motoshlem kiyish va xavfsizlik qoidalariga rioya etmaslik — BHMning yarim baravari (0.5 BHM) miqdorida jarima solishga sabab bo'ladi.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),
        LegalArticle(
            id = "mjtk_128",
            codeId = "mjtk",
            chapter = "Yo'l harakati qoidalarini buzish",
            articleNumber = 128,
            articleNumberStr = "128-modda",
            title = "Haydovchilarning yo'l harakati qoidalarini buzishi",
            content = """Haydovchilarning svetoforning taqiqlovchi ishorasiga bo'ysinmasdan o'tishi, quvib o'tish yoki harakat yo'nalishini asossiz o'zgartirishi ma'muriy jarima solishga sabab bo'ladi.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),
        LegalArticle(
            id = "mjtk_128_4",
            codeId = "mjtk",
            chapter = "Yo'l harakati qoidalarini buzish",
            articleNumber = 128,
            articleNumberStr = "128-4-modda",
            title = "Svetoforning taqiqlovchi ishorasiga bo'ysunmasdan o'tish",
            content = """Svetoforning taqiqlovchi ishorasiga yoki regulyatorning taqiqlovchi ishorasiga bo'ysunmasdan o'tish — BHMning ikki baravari miqdorida jarima solishga sabab bo'ladi. Bir yil davomida takroran sodir etilsa — BHMning besh baravari miqdorida jarima solinadi.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),
        LegalArticle(
            id = "mjtk_135",
            codeId = "mjtk",
            chapter = "Yo'l harakati qoidalarini buzish",
            articleNumber = 135,
            articleNumberStr = "135-modda",
            title = "Haydovchilarning yonida haydovchilik guvohnomasi va boshqa hujjatlar bo'lmagan holda transport vositalarini boshqarishi",
            content = """Haydovchilarning yonida haydovchilik guvohnomasi, transport vositasi ro'yxatdan o'tkazilganligi to'g'risidagi hujjatlar bo'lmagan holda transport vositalarini boshqarishi — BHMning bir baravari miqdorida jarima solishga sabab bo'ladi.
Boshqarish huquqi bo'lmagan shaxslarning transport vositalarini boshqarishi — BHMning besh baravari miqdorida jarima solishga sabab bo'ladi.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),
        LegalArticle(
            id = "mjtk_313",
            codeId = "mjtk",
            chapter = "Qarorlar ustidan shikoyat berish",
            articleNumber = 313,
            articleNumberStr = "313-modda",
            title = "Ma'muriy ish yuzasidan chiqarilgan qaror ustidan shikoyat qilish",
            content = """Ma'muriy huquqbuzarlik to'g'risidagi ish yuzasidan chiqarilgan qaror ustidan qaror nusxasi topshirilgan kundan boshlab 10 kun ichida ma'muriy sudga yoki yuqori organga shikoyat qilinishi mumkin.""",
            effectiveDate = "01.04.1995",
            status = "Amalda"
        ),

        // Mehnat kodeksi (mk)
        LegalArticle(
            id = "mk_1",
            codeId = "mk",
            chapter = "Umumiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Mehnat to'g'risidagi qonunchilikning maqsadi",
            content = """Mehnat to'g'risidagi qonunchilikning maqsadi xodimlar hamda ish beruvchilar manfaatlarining maqbul muvozanatini ta'minlash va mehnat sohasidagi huquqlarni kafolatlashdir.""",
            effectiveDate = "30.04.2023",
            status = "Amalda"
        ),
        LegalArticle(
            id = "mk_161",
            codeId = "mk",
            chapter = "Mehnat shartnomasini bekor qilish",
            articleNumber = 161,
            articleNumberStr = "161-modda",
            title = "Mehnat shartnomasini ish beruvchining tashabbusi bilan bekor qilish",
            content = """Mehnat shartnomasi ish beruvchining tashabbusi bilan quyidagi asoslar bo'yicha bekor qilinishi mumkin: texnologiya, ishlab chiqarish va mehnatni tashkil etishdagi o'zgarishlar, xodimning malakasi yetarli bo'lmagani yoki mehnat majburiyatlarini bir marta qo'pol ravishda buzgani. Bekor qilish haqida xodim yozma ravishda ogohlantirilishi va tegishli buyruq chiqarilishi shart.""",
            effectiveDate = "30.04.2023",
            status = "Amalda",
            relatedArticleIds = listOf("mk_253", "fk_14")
        ),
        LegalArticle(
            id = "mk_112",
            codeId = "mk",
            chapter = "Ish vaqti",
            articleNumber = 112,
            articleNumberStr = "112-modda",
            title = "Ish vaqtining normal davomiyligi",
            content = """Xodim uchun ish vaqtining normal davomiyligi haftasiga qirq soatdan oshishi mumkin emas. Olti kunlik ish haftasida kunlik ish 7 soatdan, besh kunlik haftada 8 soatdan ortmasligi shart.""",
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
            content = """Ish haqi har yarim oyda kamida bir marta to'lanishi lozim. Ish haqi kechiktirilgan har bir kun uchun Markaziy bank qayta moliyalash stavkasi miqdorida kompensatsiya to'lanishi shart.""",
            effectiveDate = "30.04.2023",
            status = "Amalda",
            relatedArticleIds = listOf("mk_112", "fk_14")
        ),

        // Oila kodeksi (ok)
        LegalArticle(
            id = "ok_1",
            codeId = "ok",
            chapter = "Umumiy qoidalar",
            articleNumber = 1,
            articleNumberStr = "1-modda",
            title = "Oila to'g'risidagi qonun hujjatlarining asosiy prinsiplari",
            content = """Oila, onalik, otalik va bolalik davlat muhofazasidadir. Nikoh erkak va ayolning ixtiyoriy roziligi hamda teng huquqliligiga asoslanadi.""",
            effectiveDate = "01.09.1998",
            status = "Amalda"
        ),
        LegalArticle(
            id = "ok_96",
            codeId = "ok",
            chapter = "Aliment majburiyatlari",
            articleNumber = 96,
            articleNumberStr = "96-modda",
            title = "Ota-onaning voyaga yetmagan bolalariga ta'minot berish majburiyati",
            content = """Ota-ona voyaga yetmagan bolalariga ta'minot berishi shart. Ta'minot berish majburiyati ixtiyoriy bajarilmasa, sud tartibida aliment undiriladi.""",
            effectiveDate = "01.09.1998",
            status = "Amalda"
        ),
        LegalArticle(
            id = "ok_99",
            codeId = "ok",
            chapter = "Aliment majburiyatlari",
            articleNumber = 99,
            articleNumberStr = "99-modda",
            title = "Voyaga yetmagan bolalar uchun sud tartibida undiriladigan aliment miqdori",
            content = """Ota-onadan voyaga yetmagan bolalari uchun aliment oylik daromadining: 1 bola uchun 1/4 qismi, 2 bola uchun 1/3 qismi, 3 va undan ortiq bola uchun 1/2 qismi miqdorida undiriladi.""",
            effectiveDate = "01.09.1998",
            status = "Amalda"
        )
    )

    @Volatile
    private var loadedArticlesCache: List<LegalArticle>? = null

    fun loadAllArticles(context: Context): List<LegalArticle> {
        if (loadedArticlesCache != null) return loadedArticlesCache!!
        synchronized(this) {
            if (loadedArticlesCache != null) return loadedArticlesCache!!
            val list = mutableListOf<LegalArticle>()
            try {
                context.assets.open("legal_articles.json").bufferedReader().use { reader ->
                    val jsonString = reader.readText()
                    val jsonArray = JSONArray(jsonString)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(
                            LegalArticle(
                                id = obj.getString("id"),
                                codeId = obj.getString("codeId"),
                                chapter = obj.optString("chapter", "Umumiy qoidalar"),
                                articleNumber = obj.getInt("articleNumber"),
                                articleNumberStr = obj.getString("articleNumberStr"),
                                title = obj.getString("title"),
                                content = cleanPdfDocumentText(obj.getString("content")),
                                effectiveDate = obj.optString("effectiveDate", "01.01.2020"),
                                status = obj.optString("status", "Amalda")
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val finalResult = if (list.isNotEmpty()) list else KEY_ARTICLES
            loadedArticlesCache = finalResult
            return finalResult
        }
    }

    fun getAllArticles(context: Context? = null): List<LegalArticle> {
        if (context != null) {
            return loadAllArticles(context)
        }
        return loadedArticlesCache ?: KEY_ARTICLES
    }

    fun getCodeById(id: String): LegalCode? = UZBEKISTAN_CODES.find { it.id == id }

    fun getArticlesForCode(codeId: String, context: Context? = null): List<LegalArticle> {
        val all = getAllArticles(context)
        val found = all.filter { it.codeId == codeId }
        if (found.isNotEmpty()) return found

        val code = getCodeById(codeId) ?: return emptyList()
        return listOf(
            LegalArticle(
                id = "${code.id}_1",
                codeId = code.id,
                chapter = "1-bob. Umumiy qoidalar",
                articleNumber = 1,
                articleNumberStr = "1-modda",
                title = "${code.shortName}ning tartibga solish sohasi va vazifalari",
                content = "${code.officialUzbekName} (${code.documentNumber}) O'zbekiston Respublikasi hududida ${code.description} Ushbu qonun hujjatida belgilangan normalardan barcha jismoniy va yuridik shaxslar qat'iy foydalanishi hamda unga rioya etishi shart.",
                effectiveDate = code.effectiveDate,
                status = code.status
            )
        )
    }

    private fun normalizeApostrophes(str: String): String {
        return str.lowercase()
            .replace('ʻ', '\'')
            .replace('ʼ', '\'')
            .replace('’', '\'')
            .replace('‘', '\'')
            .replace('`', '\'')
    }

    fun searchArticles(query: String, context: Context? = null): List<LegalArticle> {
        val all = getAllArticles(context)
        val q = normalizeApostrophes(query.trim())
        if (q.isEmpty()) return KEY_ARTICLES

        // Direct article ID match (e.g. "mjtk_128", "jk_97", "mk_161", "mjtk_128_4")
        val cleanedQ = q.replace("-", "_").replace(" ", "_")
        val directMatch = all.find {
            it.id.equals(q, ignoreCase = true) ||
                    it.id.equals(cleanedQ, ignoreCase = true) ||
                    (q.contains("128") && (it.id == "mjtk_128" || it.id == "mjtk_128_4"))
        }
        if (directMatch != null) return listOf(directMatch)

        // Article number extraction e.g. "128-4-modda", "128-modda", "128", "128-4"
        val moddaMatch = Regex("([0-9]{1,3}(?:[-–—_][0-9]{1,2})?)").find(q)?.groupValues?.get(1)
        val matchedByModdaNum = if (moddaMatch != null) {
            val baseNum = moddaMatch.split(Regex("[-–—_]"))[0]
            all.filter {
                it.articleNumberStr.contains(moddaMatch) ||
                        it.articleNumber.toString() == moddaMatch ||
                        it.articleNumber.toString() == baseNum ||
                        it.id.contains(moddaMatch.replace("-", "_"))
            }
        } else emptyList()

        val stopWords = setOf(
            "nima", "qanday", "qaysi", "uchun", "haqida", "bilan", "keyin", "oldin",
            "agar", "bo'lsa", "bolsa", "qilsa", "qilishi", "kerak", "bor", "yo'q",
            "yoq", "menga", "mening", "o'z", "oz", "boshqa", "har", "hamma", "barcha",
            "mumkin", "emas", "edi", "ekan", "biron", "bir", "bo'yicha", "boyicha"
        )

        val tokens = q.split(Regex("\\s+"))
            .map { it.replace(Regex("[^a-z0-9'\\-_]"), "") }
            .filter { it.length >= 2 && !stopWords.contains(it) }

        val matchingCodeIds = UZBEKISTAN_CODES.filter { code ->
            val cName = normalizeApostrophes("${code.shortName} ${code.officialUzbekName} ${code.id}")
            tokens.any { tok -> cName.contains(tok) }
        }.map { it.id }.toSet()

        val scoredArticles = all.mapNotNull { article ->
            val artNum = normalizeApostrophes(article.articleNumberStr)
            val title = normalizeApostrophes(article.title)
            val content = normalizeApostrophes(article.content)
            val chapter = normalizeApostrophes(article.chapter)

            var score = 0

            if (matchedByModdaNum.contains(article)) {
                score += 80
            }

            if (article.id.equals(q, ignoreCase = true) || article.id.equals(cleanedQ, ignoreCase = true)) {
                score += 100
            }

            if (article.codeId in matchingCodeIds) {
                score += 15
            }

            if (q.isNotEmpty() && (title.contains(q) || content.contains(q))) {
                score += 50
            }

            for (tok in tokens) {
                if (artNum.contains(tok)) score += 30
                if (title.contains(tok)) score += 20
                if (chapter.contains(tok)) score += 10
                if (content.contains(tok)) score += 5

                if (tok.length >= 4) {
                    val stem = tok.take(4)
                    if (title.contains(stem)) score += 5
                    if (content.contains(stem)) score += 2
                }
            }

            if (score > 0) Pair(score, article) else null
        }.sortedByDescending { it.first }.map { it.second }

        if (scoredArticles.isNotEmpty()) {
            return scoredArticles
        }

        if (matchedByModdaNum.isNotEmpty()) {
            return matchedByModdaNum
        }

        return matchingCodeIds.flatMap { codeId -> getArticlesForCode(codeId, context) }
    }

    fun searchArticlesForQueries(queries: List<String>, context: Context? = null): List<LegalArticle> {
        if (queries.isEmpty()) return emptyList()
        val results = mutableListOf<LegalArticle>()
        val seenIds = mutableSetOf<String>()

        for (subQuery in queries) {
            val matches = searchArticles(subQuery, context)
            for (art in matches.take(3)) {
                if (!seenIds.contains(art.id)) {
                    seenIds.add(art.id)
                    results.add(art)
                }
            }
        }
        return results.take(6)
    }
}
