package com.example.data.local

import com.example.data.model.LegislationUpdate

object UpdatesData {

    val RECENT_UPDATES: List<LegislationUpdate> = listOf(
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
            sourceUrl = "https://lex.uz/docs/6257288",
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
            sourceUrl = "https://lex.uz/docs/111453",
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
            sourceUrl = "https://lex.uz/docs/3517337",
            importance = "O'rta"
        )
    )
}
