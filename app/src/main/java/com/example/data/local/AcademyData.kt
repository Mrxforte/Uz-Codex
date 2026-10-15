package com.example.data.local

import com.example.data.model.Course
import com.example.data.model.Lesson
import com.example.data.model.QuizQuestion
import com.example.data.model.UserAchievement

object AcademyData {

    val AVAILABLE_TOPICS: List<String> = listOf(
        "Jinoyat huquqi",
        "Fuqarolik huquqi",
        "Mehnat huquqi",
        "Ma'muriy huquq",
        "Oila huquqi",
        "Soliq huquqi",
        "Protsessual huquq",
        "Bojxona va Iqtisod",
        "Konstitutsiya va Saylov",
        "Yer va Uy-joy"
    )

    val COURSES: List<Course> = listOf(
        Course(
            id = "course_basics",
            title = "O'zbekiston huquq tizimi asoslari",
            description = "Konstitutsiya, normativ-huquqiy hujjatlar iyerarxiyasi, qonun va kodeks farqlari, fuqarolik erkinliklari.",
            level = "Boshlang'ich",
            totalLessons = 3,
            estimatedMinutes = 20,
            icon = "school",
            xpReward = 50,
            lessons = listOf(
                Lesson(
                    id = "l_basics_1",
                    courseId = "course_basics",
                    orderNumber = 1,
                    title = "Normativ-huquqiy hujjatlar iyerarxiyasi",
                    keyPoints = listOf(
                        "O'zbekiston Respublikasi Konstitutsiyasi oliy yuridik kuchga ega.",
                        "Kodekslar va qonunlar Konstitutsiyaga zid bo'lishi mumkin emas.",
                        "Qonunosti hujjatlari qonunlarga muvofiq qabul qilinadi."
                    ),
                    articleReferences = listOf("Konstitutsiya 15-modda", "Normativ-huquqiy hujjatlar to'g'risidagi qonun"),
                    explanationText = "O'zbekistonda barcha qonunlar va normativ-huquqiy hujjatlar Konstitutsiya asosida qabul qilinadi. Kodeks esa muayyan bir sohani tizimli tartibga soluvchi asosiy qonun hujjati hisoblanadi.",
                    xpReward = 15
                ),
                Lesson(
                    id = "l_basics_2",
                    courseId = "course_basics",
                    orderNumber = 2,
                    title = "Fuqarolarning konstitutsiyaviy huquq va majburiyatlari",
                    keyPoints = listOf(
                        "Yashash huquqi, erkinlik va shaxsiy daxlsizlik.",
                        "Hech kim sud qarorisiz 48 soatdan ortiq ushlab turilishi mumkin emas (Habeas Corpus).",
                        "Malakali yuridik yordam olish huquqi barcha bosqichlarda kafolatlanadi."
                    ),
                    articleReferences = listOf("Konstitutsiya 25, 27, 29-moddalar"),
                    explanationText = "Yangi tahrirdagi Konstitutsiyada inson qadr-qimmati eng oliy qadriyat deb e'lon qilindi. Shaxsni ushlab turish faqat sud nazorati ostida amalga oshiriladi.",
                    xpReward = 15
                ),
                Lesson(
                    id = "l_basics_3",
                    courseId = "course_basics",
                    orderNumber = 3,
                    title = "Huquqiy hujjatlarni tahlil qilish qoidalari",
                    keyPoints = listOf(
                        "Modda, qism, band va xatboshilar tuzilishi.",
                        "Gipoteza, dispozitsiya va sanksiya tushunchalari.",
                        "Tasdiqlangan milliy huquqiy bazadan yangilanishlarni tekshirish."
                    ),
                    articleReferences = listOf("Qonunchilik texnikasi qoidalari"),
                    explanationText = "Qonun normasini to'g'ri qo'llash uchun dispozitsiya va sanksiyani to'g'ri farqlash zarur.",
                    xpReward = 20
                )
            )
        ),
        Course(
            id = "course_criminal",
            title = "Jinoyat va Jinoyat-protsessual huquqi",
            description = "Qasd, ehtiyotsizlik, zaruriy mudofaa, aybsizlik prezumpsiyasi va sudgacha tergov huquqlari.",
            level = "Talaba / Amaliyotchi",
            totalLessons = 3,
            estimatedMinutes = 35,
            icon = "gavel",
            xpReward = 75,
            lessons = listOf(
                Lesson(
                    id = "l_crim_1",
                    courseId = "course_criminal",
                    orderNumber = 1,
                    title = "Qasd va ehtiyotsizlik: Ayb shakllari",
                    keyPoints = listOf(
                        "To'g'ri qasd: Oqibatni anglaydi va uni xohlaydi.",
                        "Egri qasd: Oqibatga ongli ravishda yo'l qo'yadi.",
                        "Ehtiyotsizlik orqasida o'limga sabab bo'lish (JK 102)."
                    ),
                    articleReferences = listOf("JK 21, 22-moddalar"),
                    explanationText = "Janjal vaqtida yuzga zarba berilganda o'limga sabab bo'lish qasddan odam o'ldirish emas, balki JK 104 3-qism yoki JK 102 bilan malakalanishi mumkin.",
                    xpReward = 25
                ),
                Lesson(
                    id = "l_crim_2",
                    courseId = "course_criminal",
                    orderNumber = 2,
                    title = "Zaruriy mudofaa va uning chegaralari",
                    keyPoints = listOf(
                        "Tajovuz real va bevosita bo'lishi shart.",
                        "Mudofaa vositasi xavflilikka muvofiq bo'lishi lozim.",
                        "Hayotga tajovuzda chegaradan chiqish bo'lmaydi."
                    ),
                    articleReferences = listOf("JK 37-modda"),
                    explanationText = "Shaxs tajovuzdan o'zini yoki boshqalarni himoya qilish huquqiga ega.",
                    xpReward = 25
                ),
                Lesson(
                    id = "l_crim_3",
                    courseId = "course_criminal",
                    orderNumber = 3,
                    title = "Aybsizlik prezumpsiyasi va advokat kafolati",
                    keyPoints = listOf(
                        "Shaxs aybsizligini isbotlashi shart emas.",
                        "Bartaraf etib bo'lmaydigan shubhalar ayblanuvchi foydasiga hal etiladi.",
                        "Advokatsiz olingan iqrorlik ko'rsatmalari dalil bo'lmaydi."
                    ),
                    articleReferences = listOf("JPK 23, 50-moddalar"),
                    explanationText = "Hech kim qonuniy kuchga kirgan sud hukmisiz jinoyatchi deb atalishi mumkin emas.",
                    xpReward = 25
                )
            )
        )
    )

    /**
     * Large, authentic legal quiz question repository covering all 10 branches of Uzbekistan law.
     * Meticulously written with correct article citations and detailed educational rationales.
     */
    val QUIZ_QUESTIONS: List<QuizQuestion> = listOf(
        // ==================== JINOYAT HUQUQI (JK) ====================
        QuizQuestion(
            id = "q_jk_1",
            topic = "Jinoyat huquqi",
            questionText = "Agar shaxs janjal vaqtida bir musht urishi oqibatida ikkinchi shaxs yiqilib, toshga urilib vafot etsa va aybdorda o'ldirish qasdi bo'lmagan bo'lsa, qilmish qaysi modda bilan malakalanadi?",
            options = listOf(
                "JK 97-modda (Qasddan odam o'ldirish)",
                "JK 102-modda (Ehtiyotsizlik orqasida odam o'ldirish)",
                "JK 98-modda (Affekt holatida o'ldirish)",
                "Javobgarlik kelib chiqmaydi"
            ),
            correctIndex = 1,
            explanation = "Oliy Sud Plenumining tushuntirishiga ko'ra, agar zarba bevosita o'limga olib kelmay, shaxs yiqilib qattiq sirtga urilishi oqibatida vafot etsa, qilmish ehtiyotsizlik orqasida odam o'ldirish (JK 102) deb baholanadi.",
            verifiedCitation = "O'zbekiston Respublikasi JK 102-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_jk_2",
            topic = "Jinoyat huquqi",
            questionText = "O'zbekiston Respublikasi Jinoyat kodeksiga ko'ra, umumiy qoidaga binoan jinoiy javobgarlikka tortish yoshi necha yoshdan boshlanadi?",
            options = listOf(
                "14 yoshdan",
                "16 yoshdan",
                "18 yoshdan",
                "21 yoshdan"
            ),
            correctIndex = 1,
            explanation = "JK 17-moddasiga ko'ra, jinoyat sodir etgunga qadar 16 yoshga to'lgan shaxslar javobgarlikka tortiladi. Faqat ayrim o'ta og'ir jinoyatlar uchun javobgarlik 14 yoshdan boshlanadi.",
            verifiedCitation = "O'zbekiston Respublikasi JK 17-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_jk_3",
            topic = "Jinoyat huquqi",
            questionText = "Jinoyat kodeksining 37-moddasiga binoan, zaruriy mudofaa chegarasidan chetga chiqish deb nimaga aytiladi?",
            options = listOf(
                "Tajovuz qiluvchiga har qanday zarba berish",
                "Mudofaaning tajovuz xususiyati va xavfliligiga mutlaqo nomutanosibligi",
                "Qurolsiz tajovuzkorga qarshi baqirish",
                "Tajovuz vaqtida politsiyaga xabar bermaslik"
            ),
            correctIndex = 1,
            explanation = "Zaruriy mudofaa chegarasidan chetga chiqish — mudofaaning tajovuz xususiyati va xavfliligiga ochiqdan-ochiq nomutanosibligi hisoblanadi.",
            verifiedCitation = "O'zbekiston Respublikasi JK 37-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_jk_4",
            topic = "Jinoyat huquqi",
            questionText = "Shaxs tomonidan aldash yoki ishonchni suiiste'mol qilish yo'li bilan o'zganing mulkini qo'lga kiritish qaysi jinoyat tarkibini tashkil etadi?",
            options = listOf(
                "O'g'rilik (JK 169)",
                "Firibgarlik (JK 168)",
                "Talonchilik (JK 166)",
                "Bosqinchilik (JK 164)"
            ),
            correctIndex = 1,
            explanation = "JK 168-moddasiga muvofiq, firibgarlik — aldash yoki ishonchni suiiste'mol qilish yo'li bilan o'zganing mulkini qo'lga kiritish yoki mulk huquqini olishdir.",
            verifiedCitation = "O'zbekiston Respublikasi JK 168-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_jk_5",
            topic = "Jinoyat huquqi",
            questionText = "Jinoyat kodeksida ko'zda tutilgan umrbod ozodlikdan mahrum qilish jazosi kimlarga nisbatan qo'llanilishi mumkin EMAS?",
            options = listOf(
                "Ayollarga, 18 yoshga to'lmagan shaxslarga va 60 yoshdan oshgan erkaklarga",
                "Faqat chet el fuqarolariga",
                "Faqat birinchi marta jinoyat sodir etganlarga",
                "Barcha toifadagi shaxslarga qo'llanilaveradi"
            ),
            correctIndex = 0,
            explanation = "JK 51-moddasi 2-qismiga binoan, umrbod ozodlikdan mahrum qilish jazosi ayolga, o'n sakkiz yoshga to'lmasdan jinoyat sodir etgan shaxsga hamda oltmish yoshdan oshgan erkakka nisbatan tayinlanishi mumkin emas.",
            verifiedCitation = "O'zbekiston Respublikasi JK 51-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),

        // ==================== MEHNAT HUQUQI (MK - 2023) ====================
        QuizQuestion(
            id = "q_mk_1",
            topic = "Mehnat huquqi",
            questionText = "Yangi tahrirdagi Mehnat kodeksiga ko'ra, xodimlar uchun haftalik normal ish vaqti qancha soatdan oshishi mumkin emas?",
            options = listOf(
                "36 soatdan",
                "40 soatdan",
                "48 soatdan",
                "42 soatdan"
            ),
            correctIndex = 1,
            explanation = "Mehnat kodeksining 112-moddasiga ko'ra, xodim uchun haftalik normal ish vaqti qirq (40) soatdan oshishi mumkin emas.",
            verifiedCitation = "O'zbekiston Respublikasi MK 112-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_mk_2",
            topic = "Mehnat huquqi",
            questionText = "Xodimga yillik asosiy mehnat ta'tilining eng kam davomiyligi yangi Mehnat kodeksiga ko'ra necha kalendar kun etib belgilangan?",
            options = listOf(
                "15 ish kuni",
                "21 kalendar kuni",
                "24 kalendar kuni",
                "28 kalendar kuni"
            ),
            correctIndex = 1,
            explanation = "Yangi Mehnat kodeksining 216-moddasiga muvofiq, yillik asosiy mehnat ta'tilining eng kam muddati yigirma bir (21) kalendar kundan iborat.",
            verifiedCitation = "O'zbekiston Respublikasi MK 216-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_mk_3",
            topic = "Mehnat huquqi",
            questionText = "Yangi Mehnat kodeksiga binoan, ishga qabul qilishda dastlabki sinov muddati ko'pi bilan necha oydan oshishi mumkin emas?",
            options = listOf(
                "1 oydan",
                "2 oydan",
                "3 oydan (tashkilot rahbarlari uchun 6 oygacha)",
                "6 oydan"
            ),
            correctIndex = 2,
            explanation = "MK 130-moddasiga ko'ra, dastlabki sinov muddati uch oydan, tashkilot rahbarlari va ularning o'rinbosarlari, bosh buxgalterlar uchun esa olti oydan oshishi mumkin emas.",
            verifiedCitation = "O'zbekiston Respublikasi MK 130-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_mk_4",
            topic = "Mehnat huquqi",
            questionText = "Xodim o'z xohishiga ko'ra mehnat shartnomasini bekor qilish haqida ish beruvchini necha kun oldin yozma ravishda ogohlantirishi shart?",
            options = listOf(
                "3 kun oldin",
                "7 kun oldin",
                "14 kalendar kuni oldin",
                "1 oy oldin"
            ),
            correctIndex = 2,
            explanation = "MK 160-moddasiga muvofiq, xodim nomuayyan muddatli mehnat shartnomasini o'n to'rt kalendar kun oldin ish beruvchini yozma ravishda ogohlantirib bekor qilishga haqlidir.",
            verifiedCitation = "O'zbekiston Respublikasi MK 160-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_mk_5",
            topic = "Mehnat huquqi",
            questionText = "Yangi Mehnat kodeksiga ko'ra, homilador ayollar bilan mehnat shartnomasini ish beruvchining tashabbusi bilan bekor qilishga qaysi holatda yo'l qo'yiladi?",
            options = listOf(
                "Xodim malakasi yetarli bo'lmaganda",
                "Faqat korxona butunlay tugatilganda",
                "Xodim bir marta kech qolganda",
                "Hech qanday holatda yo'l qo'yilmaydi"
            ),
            correctIndex = 1,
            explanation = "MK 408-moddasiga muvofiq, homilador ayollar bilan tuzilgan mehnat shartnomasini bekor qilishga faqat korxona butunlay tugatilgan hollardagina yo'l qo'yiladi.",
            verifiedCitation = "O'zbekiston Respublikasi MK 408-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),

        // ==================== FUQAROLIK HUQUQI (FK) ====================
        QuizQuestion(
            id = "q_fk_1",
            topic = "Fuqarolik huquqi",
            questionText = "Fuqarolik qonunchiligiga ko'ra, fuqaro qaysi yoshga to'lgach to'liq muomala layoqatiga (to'la hajmda) ega bo'ladi?",
            options = listOf(
                "14 yoshga to'lgach",
                "16 yoshga to'lgach",
                "18 yoshga to'lgach",
                "21 yoshga to'lgach"
            ),
            correctIndex = 2,
            explanation = "FK 22-moddasiga ko'ra, fuqaroning o'z harakatlari bilan huquqlarga ega bo'lish va majburiyatlarni vujudga keltirish layoqati (muomala layoqati) voyaga yetganda, ya'ni 18 yoshga to'lgach to'la hajmda vujudga keladi.",
            verifiedCitation = "O'zbekiston Respublikasi FK 22-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_fk_2",
            topic = "Fuqarolik huquqi",
            questionText = "O'zbekiston Respublikasi Fuqarolik kodeksiga ko'ra umumiy da'vo muddati necha yilni tashkil etadi?",
            options = listOf(
                "1 yil",
                "3 yil",
                "5 yil",
                "10 yil"
            ),
            correctIndex = 1,
            explanation = "FK 150-moddasiga binoan, buzilgan huquqni himoya qilish uchun umumiy da'vo muddati uch (3) yil qilib belgilangan.",
            verifiedCitation = "O'zbekiston Respublikasi FK 150-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_fk_3",
            topic = "Fuqarolik huquqi",
            questionText = "Bir yildan ortiq muddatga tuzilgan ko'chmas mulk (bino, inshoot, uy-joy) ijara shartnomasi qanday rasmiylashtirilishi shart?",
            options = listOf(
                "Og'zaki kelishuv orqali",
                "Davlat ro'yxatidan o'tkazilishi shart",
                "Faqat mahalla fuqarolar yig'inida tasdiqlanishi kerak",
                "Faqat bank orqali hisob-kitob qilinishi kerak"
            ),
            correctIndex = 1,
            explanation = "FK 535 va 574-moddalariga muvofiq, bir yildan kam bo'lmagan muddatga tuzilgan ko'chmas mulk ijara shartnomasi davlat ro'yxatidan o'tkazilishi shart.",
            verifiedCitation = "O'zbekiston Respublikasi FK 574-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_fk_4",
            topic = "Fuqarolik huquqi",
            questionText = "Fuqaro mulkdor sifatida o'z mulkiga nisbatan qanday 3 ta asosiy vakolatlar majmuiga ega?",
            options = listOf(
                "Egalik qilish, foydalanish va tasarruf etish",
                "Sotish, ijaraga berish va garovga qo'yish",
                "Soliq to'lash, hisobga olish va sug'urtalash",
                "Ko'rikdan o'tkazish, ta'mirlash va ro'yxatga olish"
            ),
            correctIndex = 0,
            explanation = "FK 164-moddasiga binoan, mulk huquqi shaxsning o'ziga qarashli mol-mulkka o'z xohishi bilan egalik qilish, undan foydalanish va uni tasarruf etish huquqidan iboratdir.",
            verifiedCitation = "O'zbekiston Respublikasi FK 164-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_fk_5",
            topic = "Fuqarolik huquqi",
            questionText = "Zarar yetkazishdan kelib chiqadigan majburiyatlar (deliktlar) bo'yicha yetkazilgan zarar qanday hajmda qoplanishi shart?",
            options = listOf(
                "Faqat moddiy zarar 50 foiz miqdorida",
                "To'liq hajmda (haqiqiy zarar va boy berilgan foyda)",
                "Faqat sud belgilagan jarima miqdorida",
                "Faqat eng kam ish haqi miqdorida"
            ),
            correctIndex = 1,
            explanation = "FK 985-moddasiga binoan, g'ayriqonuniy harakat yoki harakatsizlik tufayli fuqaroning shaxsiga yoki mol-mulkiga yetkazilgan zarar uni yetkazgan shaxs tomonidan to'liq hajmda qoplanishi lozim.",
            verifiedCitation = "O'zbekiston Respublikasi FK 985-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),

        // ==================== MA'MURIY HUQUQ (MJtK) ====================
        QuizQuestion(
            id = "q_mjtk_1",
            topic = "Ma'muriy huquq",
            questionText = "Ma'muriy huquqbuzarlik to'g'risidagi qaror ustidan yuqori organga yoki sudga shikoyat berish muddati necha kun?",
            options = listOf(
                "3 kun",
                "10 kun",
                "1 oy",
                "6 oy"
            ),
            correctIndex = 1,
            explanation = "MJtKning 315-moddasiga binoan, ma'muriy huquqbuzarlik to'g'risidagi ish bo'yicha chiqarilgan qaror ustidan qaror nusxasi topshirilgan kundan boshlab o'n (10) kun ichida shikoyat berilishi mumkin.",
            verifiedCitation = "O'zbekiston Respublikasi MJtK 315-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_mjtk_2",
            topic = "Ma'muriy huquq",
            questionText = "Ma'muriy qamoqqa olish jazosi sud tomonidan ko'pi bilan necha sutkagacha tayinlanishi mumkin?",
            options = listOf(
                "5 sutkagacha",
                "10 sutkagacha",
                "15 sutkagacha (favqulodda holatda 30 sutkagacha)",
                "60 sutkagacha"
            ),
            correctIndex = 2,
            explanation = "MJtK 29-moddasiga ko'ra, ma'muriy qamoq o'n besh sutkagacha, favqulodda holat tartibi sharoitida esa o'ttiz sutkagacha bo'lgan muddatga belgilanadi.",
            verifiedCitation = "O'zbekiston Respublikasi MJtK 29-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_mjtk_3",
            topic = "Ma'muriy huquq",
            questionText = "Yo'l harakati qoidalarini buzganlik uchun jarimani 15 kun ichida ixtiyoriy to'laganda qanday chegirma taqdim etiladi?",
            options = listOf(
                "20 foiz chegirma",
                "30 foiz chegirma",
                "50 foiz chegirma",
                "70 foiz chegirma"
            ),
            correctIndex = 2,
            explanation = "MJtK 332-1-moddasiga ko'ra, huquqbuzar jarima solish to'g'risidagi qaror topshirilgan kundan boshlab o'n besh kun ichida jarima miqdorining 50 foizini to'lasa, qolgan qismidan ozod qilinadi.",
            verifiedCitation = "O'zbekiston Respublikasi MJtK 332-1-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_mjtk_4",
            topic = "Ma'muriy huquq",
            questionText = "Ma'muriy javobgarlikka tortish yoshi necha yoshdan boshlanadi?",
            options = listOf(
                "14 yoshdan",
                "16 yoshdan",
                "18 yoshdan",
                "21 yoshdan"
            ),
            correctIndex = 1,
            explanation = "MJtK 13-moddasiga ko'ra, ma'muriy huquqbuzarlik sodir etgan vaqtda o'n olti yoshga to'lgan shaxslar ma'muriy javobgarlikka tortiladi.",
            verifiedCitation = "O'zbekiston Respublikasi MJtK 13-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),

        // ==================== OILa HUQUQI (OK) ====================
        QuizQuestion(
            id = "q_ok_1",
            topic = "Oila huquqi",
            questionText = "O'zbekiston Respublikasi Oila kodeksiga ko'ra nikoh yoshi erkaklar va ayollar uchun necha yosh etib belgilangan?",
            options = listOf(
                "Erkaklar uchun 18, ayollar uchun 17 yosh",
                "Erkaklar va ayollar uchun 18 yosh",
                "Erkaklar va ayollar uchun 20 yosh",
                "Erkaklar uchun 19, ayollar uchun 18 yosh"
            ),
            correctIndex = 1,
            explanation = "Oila kodeksining 15-moddasiga binoan, nikoh yoshi erkaklar va ayollar uchun o'n sakkiz (18) yosh etib teng belgilangan.",
            verifiedCitation = "O'zbekiston Respublikasi OK 15-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_ok_2",
            topic = "Oila huquqi",
            questionText = "Er va xotinning nikoh davomida orttirgan mol-mulki qanday mulk hisoblanadi?",
            options = listOf(
                "Kimning nomiga rasmiylashtirilgan bo'lsa, o'shaning yakka mulki",
                "Ularning birgalikdagi umumiy mulki",
                "Davlat tasarrufidagi vaqtinchalik mulk",
                "Erning mutlaq mulki"
            ),
            correctIndex = 1,
            explanation = "OK 23-moddasiga muvofiq, er va xotinning nikoh davomida orttirgan mol-mulklari, agar nikoh shartnomasida boshqacha qoida belgilanmagan bo'lsa, ularning birgalikdagi umumiy mulki hisoblanadi.",
            verifiedCitation = "O'zbekiston Respublikasi OK 23-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_ok_3",
            topic = "Oila huquqi",
            questionText = "Ota-onadan bir nafar bola uchun undiriladigan aliment miqdori ota-onaning oylik ish haqining qancha qismini tashkil etadi?",
            options = listOf(
                "To'rtdan bir qismini (1/4)",
                "Uchdan bir qismini (1/3)",
                "Yarmini (1/2)",
                "Oltidan bir qismini (1/6)"
            ),
            correctIndex = 0,
            explanation = "OK 99-moddasiga binoan, voyaga yetmagan bolalarga aliment ota-onaning har oydagi ish haqi va boshqa daromadining bir bola uchun — to'rtdan bir qismi (1/4) miqdorida undiriladi.",
            verifiedCitation = "O'zbekiston Respublikasi OK 99-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_ok_4",
            topic = "Oila huquqi",
            questionText = "Nikoh shartnomasi qaysi organda majburiy tartibda tasdiqlanishi shart?",
            options = listOf(
                "FHDYo bo'limida",
                "Notarial idorada notarius tomonidan",
                "Mahalla qo'mitasida",
                "Sud majlisida"
            ),
            correctIndex = 1,
            explanation = "OK 30-moddasiga binoan, nikoh shartnomasi yozma shaklda tuziladi va notarial tartibda tasdiqlanishi shart. Notarial tasdiqlanmagan nikoh shartnomasi haqiqiy emas.",
            verifiedCitation = "O'zbekiston Respublikasi OK 30-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),

        // ==================== SOLIQ HUQUQI (SK) ====================
        QuizQuestion(
            id = "q_sk_1",
            topic = "Soliq huquqi",
            questionText = "O'zbekiston Respublikasida jismoniy shaxslardan olinadigan daromad solig'i (JSHODS)ning bazaviy stavkasi necha foizni tashkil etadi?",
            options = listOf(
                "10 foiz",
                "12 foiz",
                "15 foiz",
                "20 foiz"
            ),
            correctIndex = 1,
            explanation = "Soliq kodeksining 381-moddasiga muvofiq, O'zbekiston rezidenti bo'lgan jismoniy shaxslarning daromadlariga nisbatan 12 foizli soliq stavkasi qo'llaniladi.",
            verifiedCitation = "O'zbekiston Respublikasi SK 381-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_sk_2",
            topic = "Soliq huquqi",
            questionText = "Qo'shilgan qiymat solig'i (QQS / NDS)ning O'zbekiston Respublikasidagi amaldagi bazaviy stavkasi qancha?",
            options = listOf(
                "12 foiz",
                "15 foiz",
                "20 foiz",
                "10 foiz"
            ),
            correctIndex = 0,
            explanation = "Soliq kodeksining 258-moddasiga ko'ra, qo'shilgan qiymat solig'ining bazaviy stavkasi 12 foizni tashkil etadi (2023-yil 1-yanvardan 15% dan 12% ga tushirilgan).",
            verifiedCitation = "O'zbekiston Respublikasi SK 258-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_sk_3",
            topic = "Soliq huquqi",
            questionText = "Soliq to'g'risidagi qonunchilikda bartaraf etib bo'lmaydigan barcha noaniqliklar va ziddiyatlar kimning foydasiga talqin qilinadi?",
            options = listOf(
                "Soliq organlari foydasiga",
                "Soliq to'lovchi foydasiga",
                "Davlat byudjeti foydasiga",
                "Moliya vazirligi foydasiga"
            ),
            correctIndex = 1,
            explanation = "Soliq kodeksining 13-moddasiga binoan, soliq qonunchiligidagi barcha bartaraf etib bo'lmaydigan qarama-qarshiliklar va noaniqliklar soliq to'lovchining foydasiga talqin etiladi.",
            verifiedCitation = "O'zbekiston Respublikasi SK 13-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),

        // ==================== PROTSESSUAL HUQUQ (JPK, FPK, IPK) ====================
        QuizQuestion(
            id = "q_proc_1",
            topic = "Protsessual huquq",
            questionText = "Jinoyat-protsessual kodeksining 23-moddasiga binoan aybsizlik prezumpsiyasi printsipi qanday ifodalanadi?",
            options = listOf(
                "Gumon qilinuvchi o'z aybsizligini isbotlashi shart",
                "Aybdorlikka oid barcha bartaraf etib bo'lmaydigan shubhalar gumon qilinuvchi yoki sudlanuvchi foydasiga hal qilinadi",
                "Tergovchi qarori bilan shaxs aybdor deb hisoblanadi",
                "Prokuror sudsiz jazo qo'llashga haqli"
            ),
            correctIndex = 1,
            explanation = "JPK 23-moddasi 2-qismiga binoan, aybdorlikka oid barcha bartaraf etib bo'lmaydigan shubhalar gumon qilinuvchi, ayblanuvchi yoki sudlanuvchining foydasiga hal qilinishi shart.",
            verifiedCitation = "O'zbekiston Respublikasi JPK 23-moddasi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_proc_2",
            topic = "Protsessual huquq",
            questionText = "Fuqarolik protsessida birinchi instansiya sudi hal qiluv qarori ustidan apellyatsiya shikoyati berish muddati necha kun?",
            options = listOf(
                "10 kun",
                "1 oy",
                "2 oy",
                "6 oy"
            ),
            correctIndex = 1,
            explanation = "Fuqarolik protsessual kodeksining 385-moddasiga binoan, apellyatsiya shikoyati sud hal qiluv qarori qabul qilingan kundan e'tiboran bir (1) oy ichida berilishi mumkin.",
            verifiedCitation = "O'zbekiston Respublikasi FPK 385-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_proc_3",
            topic = "Protsessual huquq",
            questionText = "Gumon qilinuvchini ushlab turish muddati u huquqni muhofaza qiluvchi organga keltirilgan paytdan boshlab ko'pi bilan necha soatdan oshishi mumkin emas?",
            options = listOf(
                "24 soatdan",
                "48 soatdan",
                "72 soatdan",
                "5 kundan"
            ),
            correctIndex = 1,
            explanation = "Konstitutsiya va JPK 226-moddasiga ko'ra, shaxsni sud qarorisiz qirq sakkiz (48) soatdan ortiq ushlab turish mumkin emas.",
            verifiedCitation = "Konstitutsiya 27-modda, JPK 226-modda",
            xpReward = 10,
            difficulty = "O'rta"
        ),

        // ==================== BOJXONA VA IQTISOD (BK, ByK) ====================
        QuizQuestion(
            id = "q_bk_1",
            topic = "Bojxona va Iqtisod",
            questionText = "Bojxona kodeksiga muvofiq, O'zbekiston bojxona hududiga olib kiriladigan tovarlar uchun qo'llaniladigan bojxona rejimlari soni nechta?",
            options = listOf(
                "7 ta",
                "16 ta",
                "12 ta",
                "20 ta"
            ),
            correctIndex = 1,
            explanation = "Bojxona kodeksining 24-bobida tovarlar va transport vositalari uchun 16 ta bojxona rejimi (erkin muomala, eksport, tranzit, vaqtincha olib kirish va h.k.) belgilangan.",
            verifiedCitation = "O'zbekiston Respublikasi BK 24-bobi",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_byk_1",
            topic = "Bojxona va Iqtisod",
            questionText = "Byudjet kodeksiga binoan, O'zbekiston Respublikasining Davlat byudjeti qaysi qonun chiqaruvchi organ tomonidan qabul qilinadi?",
            options = listOf(
                "Vazirlar Mahkamasi",
                "Oliy Majlis Qonunchilik palatasi va Senati",
                "Markaziy Bank",
                "Hisob palatasi"
            ),
            correctIndex = 1,
            explanation = "Byudjet kodeksi va Konstitutsiyaga ko'ra, Davlat byudjeti Oliy Majlis Qonunchilik palatasi tomonidan qabul qilinadi va Senat tomonidan ma'qullanadi.",
            verifiedCitation = "O'zbekiston Respublikasi Byudjet kodeksi 26-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),

        // ==================== KONSTITUTSIYA VA SAYLOV ====================
        QuizQuestion(
            id = "q_konst_1",
            topic = "Konstitutsiya va Saylov",
            questionText = "Yangi tahrirdagi O'zbekiston Respublikasi Konstitutsiyasining 1-moddasiga binoan O'zbekiston qanday davlat deb e'lon qilingan?",
            options = listOf(
                "Faqat unitar va respublika",
                "Boshqaruvning mutlaq prezidentlik shaklidagi davlat",
                "Suveren, demokratik, huquqiy, ijtimoiy va dunyoviy davlat",
                "Konfederativ davlat"
            ),
            correctIndex = 2,
            explanation = "Yangi Konstitutsiyaning 1-moddasiga ko'ra: 'O'zbekiston — boshqaruvning respublika shakliga ega bo'lgan suveren, demokratik, huquqiy, ijtimoiy va dunyoviy davlat'.",
            verifiedCitation = "O'zbekiston Respublikasi Konstitutsiyasi 1-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_konst_2",
            topic = "Konstitutsiya va Saylov",
            questionText = "Saylov kodeksiga binoan, O'zbekiston Respublikasi Prezidentligiga nomzod ko'rsatish huquqiga kimlar ega?",
            options = listOf(
                "Har qanday fuqaro o'z-o'zini ko'rsatishi mumkin",
                "Faqat siyosiy partiyalar",
                "Faqat viloyat kengashlari",
                "Kasaba uyushmalari"
            ),
            correctIndex = 1,
            explanation = "Saylov kodeksining 62-moddasiga ko'ra, O'zbekiston Respublikasi Prezidentligiga nomzod ko'rsatish huquqiga faqat siyosiy partiyalar egadir.",
            verifiedCitation = "O'zbekiston Respublikasi Saylov kodeksi 62-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),
        QuizQuestion(
            id = "q_konst_3",
            topic = "Konstitutsiya va Saylov",
            questionText = "Yangi tahrirdagi Konstitutsiyaga binoan, O'zbekiston Respublikasi Prezidentining vakolat muddati necha yil qilib belgilangan?",
            options = listOf(
                "4 yil",
                "5 yil",
                "7 yil",
                "6 yil"
            ),
            correctIndex = 2,
            explanation = "Yangi Konstitutsiyaning 106-moddasiga ko'ra, O'zbekiston Respublikasi Prezidenti yetti (7) yil muddatga saylanadi.",
            verifiedCitation = "O'zbekiston Respublikasi Konstitutsiyasi 106-moddasi",
            xpReward = 5,
            difficulty = "Oson"
        ),

        // ==================== YER VA UY-JOY (YK, UJK, Shaharsozlik) ====================
        QuizQuestion(
            id = "q_yer_1",
            topic = "Yer va Uy-joy",
            questionText = "O'zbekiston Respublikasi Yer kodeksi va yangi qonunchilikka binoan qishloq xo'jaligiga mo'ljallanmagan yer uchastkalari qanday tartibda xususiylashtiriladi?",
            options = listOf(
                "Tuman hokimining to'g'ridan-to'g'ri qarori orqali",
                "'E-auksion' elektron savdo platformasi orqali ochiq auksionda",
                "Og'zaki kelishuv orqali",
                "Kollektiv a'zolik qarori bilan"
            ),
            correctIndex = 1,
            explanation = "Yer kodeksi va yer uchastkalarini xususiylashtirish to'g'risidagi qonunga muvofiq, bo'sh yer uchastkalari faqat 'E-auksion' elektron savdo platformasi orqali beriladi.",
            verifiedCitation = "O'zbekiston Respublikasi Yer kodeksi va qonunchilik hujjatlari",
            xpReward = 10,
            difficulty = "O'rta"
        ),
        QuizQuestion(
            id = "q_ujk_1",
            topic = "Yer va Uy-joy",
            questionText = "Uy-joy kodeksiga ko'ra, turar joy daxlsizligi qaysi asosda cheklanishi mumkin?",
            options = listOf(
                "Istalgan davlat xodimining og'zaki talabi bilan",
                "Faqat qonunda belgilangan hollarda yoki sud qarori asosida",
                "Faqat uy qo'mitasi raisi ruxsati bilan",
                "Kechasi cheklanishi mumkin emas"
            ),
            correctIndex = 1,
            explanation = "Konstitutsiyaning 31-moddasi va Uy-joy kodeksiga ko'ra, uy-joy daxlsizdir. Hech kim uy-joyga unda yashovchilarning xohishiga qarshi faqat qonunda belgilangan hollarda yoki sud qarori asosida kirishi mumkin.",
            verifiedCitation = "Konstitutsiya 31-modda, Uy-joy kodeksi",
            xpReward = 5,
            difficulty = "Oson"
        )
    )

    val ACHIEVEMENTS: List<UserAchievement> = listOf(
        UserAchievement("ach_first_steps", "Ilk Qadam", "Birinchi yuridik darsni tamomlang", "star", 1, 1, true, "12.10.2026"),
        UserAchievement("ach_streak_3", "Qat'iyatli Talaba", "Ketma-ket 3 kun tizimda o'rganish (Streak)", "local_fire_department", 3, 3, true, "Bugun"),
        UserAchievement("ach_quiz_master", "Viktorina Bilimdoni", "10 ta huquqiy savolga to'g'ri javob bering", "military_tech", 10, 7, false),
        UserAchievement("ach_case_solver", "Tahlilchi Yurist", "Birinchi huquqiy vaziyat (keys) tahlilini bajaring", "psychology", 1, 1, true, "Kecha"),
        UserAchievement("ach_code_reader", "Qonun Izlovchi", "19 ta kodeksning har birini kamida bir marta ko'zdan kechiring", "menu_book", 19, 6, false),
        UserAchievement("ach_exam_30", "Katta Imtihon", "30 savolli to'liq huquqiy testni muvaffaqiyatli topshiring", "emoji_events", 30, 0, false)
    )

    /**
     * Adaptive quiz generation engine:
     * Generates a random set of questions (default 30) filtered by user-selected topics.
     */
    fun generateAdaptiveQuiz(
        selectedTopics: Set<String>,
        questionCount: Int = 30
    ): List<QuizQuestion> {
        val pool = if (selectedTopics.isEmpty() || selectedTopics.contains("Barchasi")) {
            QUIZ_QUESTIONS
        } else {
            val filtered = QUIZ_QUESTIONS.filter { it.topic in selectedTopics }
            if (filtered.isNotEmpty()) filtered else QUIZ_QUESTIONS
        }

        // Shuffle questions for non-repeating variety
        val shuffled = pool.shuffled()

        // If target count is greater than available in narrow topic, cyclically repeat or supplement
        return if (shuffled.size >= questionCount) {
            shuffled.take(questionCount)
        } else {
            val result = ArrayList<QuizQuestion>()
            while (result.size < questionCount) {
                for (q in shuffled) {
                    if (result.size < questionCount) {
                        result.add(q)
                    } else break
                }
                if (shuffled.isEmpty()) break
            }
            result
        }
    }
}
