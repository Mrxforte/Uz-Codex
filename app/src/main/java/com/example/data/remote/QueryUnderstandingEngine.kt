package com.example.data.remote

enum class ScopeCategory {
    GREETING_OR_ACKNOWLEDGMENT,
    IN_SCOPE_LEGAL_QUESTION,
    LEGAL_SITUATION_NEEDS_CLARIFICATION,
    LEGAL_DOCUMENT_EXPLANATION,
    URGENT_SAFETY_RELATED,
    UNRELATED_REQUEST
}

enum class InternalCategory {
    SOCIAL,             // greetings, thanks, friendly conversation, ordinary human interaction
    GENERAL,            // everyday questions and general explanations
    EMOTIONAL_SUPPORT,  // listening, acknowledging feelings, helping think through difficult situation
    LEGAL,              // explicit legal questions
    LEGAL_SITUATION,    // everyday situation with possible legal implications
    CLARIFICATION,      // follow-up question about an earlier topic
    MIXED               // message containing both conversational and legal needs
}

data class InternalInterpretationSchema(
    val intent: String, // e.g. "greeting", "thanks", "emotional_disclosure", "legal_question", "unrelated_request", "urgent_threat", "document_explanation", "correction"
    val userGoal: String, // e.g. "social_chat", "vent_emotions", "understand_options", "communication_guidance", "safety_and_emergency", "redirect"
    val situationSummary: String,
    val emotionalCues: List<String>, // e.g. ["anxiety", "frustration", "neutral"]
    val knownFacts: List<String>,
    val uncertainFacts: List<String>,
    val missingCriticalFact: String?,
    val needsClarification: Boolean,
    val needsLegalRetrieval: Boolean,
    val urgency: String, // "normal", "urgent_safety"
    val nextAction: String, // "respond_naturally", "acknowledge_and_listen", "ask_clarification", "retrieve_legal_sources", "safety_guidance", "polite_redirect"
    val responseStyle: String // "conversational", "empathetic", "clarifying", "structured_legal", "emergency"
)

data class ConversationState(
    val userObjective: String = "",
    val situationSummary: String = "",
    val confirmedFacts: List<String> = emptyList(),
    val unconfirmedClaims: List<String> = emptyList(),
    val sequenceOfEvents: List<String> = emptyList(),
    val partiesInvolved: List<String> = emptyList(),
    val askedQuestions: List<String> = emptyList(),
    val receivedAnswers: List<String> = emptyList(),
    val corrections: List<String> = emptyList(),
    val outstandingUncertainties: List<String> = emptyList(),
    val requestedStyle: String = "conversational",
    val isLitigationDesired: Boolean = true
)

data class UserQueryAnalysis(
    val originalQuery: String,
    val normalizedQuery: String,
    val scopeCategory: ScopeCategory,
    val internalCategory: InternalCategory = InternalCategory.LEGAL,
    val schema: InternalInterpretationSchema,
    val conversationState: ConversationState,
    val isSeriousDisclosure: Boolean = false,
    val isGreeting: Boolean = (scopeCategory == ScopeCategory.GREETING_OR_ACKNOWLEDGMENT),
    val isEmotionalSharing: Boolean = (schema.intent == "emotional_disclosure"),
    val isUnmatchedDomain: Boolean = false,
    val detectedDomains: List<String> = emptyList(),
    val detectedIssues: List<String> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val missingFacts: List<String> = emptyList(),
    val requiresClarification: Boolean = (scopeCategory == ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION),
    val clarificationQuestion: String? = null,
    val mixedLegalQuery: String? = null
)

object QueryUnderstandingEngine {

    fun analyzeQuery(query: String, history: List<ChatMessage> = emptyList()): UserQueryAnalysis {
        val raw = query.trim()
        val norm = UzbekTextNormalizer.normalizeText(raw)

        // 1. Compile Conversation State from history + current input
        val conversationState = buildConversationState(norm, history)

        // 2. User Correction Detection
        val isUserCorrection = norm.contains("sudga bermoqchi emasman") ||
                norm.contains("sud shart emas") ||
                norm.contains("sudga bermoqchimasman") ||
                norm.contains("sud shartmas")

        // 3. Negation Check for Violence/Assault
        val isNegatedViolence = norm.contains("urmedi") || norm.contains("urmagan") || norm.contains("urmadi") || norm.contains("urgani yo'q") || norm.contains("urgani yoq")

        // 4. History Extraction for previous facts & established context
        val historyTexts = history.map { UzbekTextNormalizer.normalizeText(it.text) }
        val allHistoryText = historyTexts.joinToString(" ")

        val hasOrderMentionInHistory = allHistoryText.contains("buyruq") ||
                allHistoryText.contains("prikaz") ||
                allHistoryText.contains("yozma") ||
                allHistoryText.contains("ogohlantirish") ||
                allHistoryText.contains("og'zaki")

        val hasLoanProofInHistory = allHistoryText.contains("tilxat") ||
                allHistoryText.contains("shartnoma") ||
                allHistoryText.contains("bank") ||
                allHistoryText.contains("og'zaki") ||
                allHistoryText.contains("cheq")

        val historyDomains = mutableListOf<String>()
        val historyIssues = mutableListOf<String>()

        if (allHistoryText.contains("ishdan") || allHistoryText.contains("mehnat")) {
            historyDomains.add("labor")
            historyIssues.add("unlawful_dismissal")
        }
        if (allHistoryText.contains("qizil") || allHistoryText.contains("svetofor") || allHistoryText.contains("prava") || allHistoryText.contains("jarima")) {
            historyDomains.add("traffic")
            if (allHistoryText.contains("qizil") || allHistoryText.contains("svetofor")) historyIssues.add("red_light_violation")
            if (allHistoryText.contains("prava") || allHistoryText.contains("pravasiz")) historyIssues.add("driving_without_license")
        }
        if (allHistoryText.contains("qarz") || allHistoryText.contains("pulimni") || allHistoryText.contains("aldashdi")) {
            historyDomains.add("civil")
            historyIssues.add("debt_recovery")
        }

        // 5. Emotional / Personal Situation Sharing (Mode B)
        val emotionalPhrases = listOf(
            "boshimga juda yomon ish tushdi", "boshimga yomon ish tushdi",
            "yomon ish tushdi", "juda tushkunlikdaman", "juda yomon ahvoldaman",
            "menga juda og'ir", "menga juda qiyin", "ko'nglim g'ash",
            "kayfiyatim rasvo", "rasvo", "siqilyapman", "kayfiyatim yomon",
            "xafa", "alam", "dildang", "kayfiyatim yoq", "kayfiyatim yo'q"
        )
        val isEmotionalSharing = (emotionalPhrases.any { norm.contains(it) } || isNegatedViolence) && !containsSpecificLegalProblemKeywords(norm)

        // 6. Greeting or Acknowledgment Check (Mode A / "salom brat qalaysan")
        val isGreetingOrAck = checkIsGreetingOrAck(norm)

        // 7. Unrelated Request Check (Mode E)
        val nonLegalKeywords = listOf(
            "python", "java", "javascript", "code", "kod", "loop", "function", "algoritm", "html", "css",
            "barcelona", "real madrid", "futbol", "kino", "ob havo", "pogoda", "retsept", "ovqat", "hazil", "musiqa"
        )
        val hasNonLegalKeyword = nonLegalKeywords.any { norm.contains(it) }
        val hasLegalKeywordsInCurrentQuery = containsSpecificLegalProblemKeywords(norm)

        val domains = mutableListOf<String>()
        val issues = mutableListOf<String>()
        val searchQueries = mutableListOf<String>()
        val missingFacts = mutableListOf<String>()

        // 8. Urgent Safety / Serious Criminal Disclosures (Mode F)
        val lowerRaw = raw.lowercase()
        val isSeriousDisclosure = lowerRaw.contains("zo'rladim") || lowerRaw.contains("zo’rladim") || lowerRaw.contains("zoʻrladim") ||
                norm.contains("zorladim") || norm.contains("zo'rladim") || norm.contains("zo rladim") ||
                norm.contains("zurladim") || norm.contains("nomusiga") || norm.contains("nomus") ||
                norm.contains("zorlash") || norm.contains("zo'rlash") || norm.contains("zo rlash") ||
                norm.contains("зўрладим") || norm.contains("номусига")

        val isUrgentThreat = (norm.contains("o'ldirish bilan tahdid") ||
                norm.contains("o'ldirmoqchi") || (norm.contains("urdi") && !isNegatedViolence) || norm.contains("meni urishmoqchi") ||
                norm.contains("tazyiq") || norm.contains("tahdid qilyapti") ||
                norm.contains("hayotimga xavf") || norm.contains("juda xavotirdaman sudga")) && !isNegatedViolence

        if (isSeriousDisclosure) {
            domains.add("criminal")
            issues.add("sexual_violence_disclosure")
            searchQueries.add("jk_118")
            searchQueries.add("118-modda")
            searchQueries.add("nomusga tegish 118-modda")
            searchQueries.add("jpk_23")
            searchQueries.add("23-modda")
            searchQueries.add("aybsizlik prezumpsiyasi 23-modda")
            searchQueries.add("jpk_50")
            searchQueries.add("50-modda")
            searchQueries.add("himoyachi ishtiroki 50-modda")
        }
        if (isUrgentThreat) {
            domains.add("criminal")
            issues.add("threat_and_safety_risk")
            searchQueries.add("shaxsiy daxlsizlik va xavfsizlik 37-modda")
            searchQueries.add("prokuraturaga murojaat 313-modda")
        }

        val isDocumentExplanation = norm.contains("jarimani tushunmadim") ||
                norm.contains("protokol tuzishdi") || norm.contains("bayonnoma tuzishdi") ||
                norm.contains("qarorda qaysi modda") || norm.contains("shartnoma bandi")

        val isUnmatchedDomain = norm.contains("kosmik") || norm.contains("stansiya") ||
                norm.contains("oyda") || norm.contains("kosmos") || norm.contains("mars") || norm.contains("marsda")

        // Traffic
        if (norm.contains("qizil") || norm.contains("svetofor") || norm.contains("red light")) {
            domains.add("traffic")
            issues.add("red_light_violation")
            searchQueries.add("mjtk_128")
            searchQueries.add("128-modda")
            searchQueries.add("128-4-modda")
            searchQueries.add("svetoforning taqiqlovchi ishorasiga bo'ysunmaslik")
        }
        if (norm.contains("remen") || norm.contains("kamar") || norm.contains("seat belt")) {
            domains.add("traffic")
            issues.add("seat_belt_violation")
            searchQueries.add("mjtk_125")
            searchQueries.add("125-modda")
            searchQueries.add("xavfsizlik kamarini taqish 125-modda")
        }
        if (norm.contains("prava") || norm.contains("pravasiz") || norm.contains("guvohnomasiz") || norm.contains("gaylar") || norm.contains("gai")) {
            domains.add("traffic")
            issues.add("driving_without_license")
            searchQueries.add("mjtk_135")
            searchQueries.add("135-modda")
            searchQueries.add("haydovchilik guvohnomasi bo'lmagan 135-modda")
        }

        // Labor
        if (norm.contains("ishdan") || norm.contains("haydashdi") || norm.contains("bo'shatishdi") || norm.contains("prikaz") || norm.contains("uvolnenie") || norm.contains("bosim") || norm.contains("mehnat") || norm.contains("shartnoma") || norm.contains("ishga kiritmayapti")) {
            domains.add("labor")
            issues.add("unlawful_dismissal")
            searchQueries.add("mk_161")
            searchQueries.add("161-modda")
            searchQueries.add("mehnat shartnomasini bekor qilish 161-modda")

            val currentHasOrder = norm.contains("buyruq") || norm.contains("ogohlantirish") || norm.contains("og'zaki") || norm.contains("yozma")
            if (!currentHasOrder && !hasOrderMentionInHistory && norm.length < 40 && !norm.contains("bekor qilish") && !norm.contains("kimlar")) {
                missingFacts.add("written_order_or_notice")
            }
        }
        if (norm.contains("zarplata") || norm.contains("maosh") || norm.contains("oylik") || norm.contains("oylig") || norm.contains("ish haqi") || norm.contains("kechiktirishyapti") || norm.contains("tashamadi") || norm.contains("tashlamadi")) {
            domains.add("labor")
            issues.add("salary_delay")
            searchQueries.add("mk_253")
            searchQueries.add("253-modda")
            searchQueries.add("ish haqini to'lash 253-modda")
        }

        // Civil Debt
        if (norm.contains("qarz") || norm.contains("pulimni yeb ketdi") || norm.contains("aldashdi") || norm.contains("dolg")) {
            domains.add("civil")
            issues.add("debt_recovery")
            searchQueries.add("fk_732")
            searchQueries.add("732-modda")
            searchQueries.add("qarz shartnomasi majburiyatlari 732-modda")

            val currentHasLoanProof = norm.contains("tilxat") || norm.contains("shartnoma") || norm.contains("bank") || norm.contains("og'zaki")
            if (!currentHasLoanProof && !hasLoanProofInHistory && (norm.contains("yeb ketdi") || norm.contains("aldashdi") || norm == "qarz berdim qaytarmayapti")) {
                missingFacts.add("loan_proof")
            }
        }

        // Housing
        if (norm.contains("ijara") || norm.contains("kvartira") || norm.contains("uyni") || norm.contains("arenda") || norm.contains("uyimni tortib")) {
            domains.add("housing")
            issues.add("tenancy_dispute")
            searchQueries.add("fk_535")
            searchQueries.add("535-modda")
            searchQueries.add("turar joy ijarasi shartnomasi 535-modda")
        }

        // Family
        if (norm.contains("ajrash") || norm.contains("razvod") || norm.contains("xotinim") || norm.contains("erim") || norm.contains("aliment")) {
            domains.add("family")
            issues.add("divorce_custody_support")
            searchQueries.add("ok_99")
            searchQueries.add("ok_96")
            searchQueries.add("99-modda")
            searchQueries.add("nikohni bekor qilish va aliment 96-modda")
        }

        // Ambiguous Slang / Dialect Document Withholding Check
        val ambiguousPhrases = listOf(
            "hujjatimni olib qo'yishdi", "dokumntimni olib qo'yishdi", "dokumentimni olib qo'yishdi", "hujjatimni olib",
            "boshimga yomon ish tushdi",
            "menga bosim o'tkazishmoqchi", "bosim o'tkazishmoqchi",
            "ishda muammo bo'lyapti",
            "haqqimni berishmayapti",
            "menga nisbatan nohaqlik bo'ldi", "nohaqlik bo'ldi",
            "uydan haydashyapti",
            "mashinamni olib qo'yishdi",
            "menga tegishli narsani berishmayapti",
            "meni aldashdi nima qilay",
            "ko'chamizda janjal bo'ldi", "janjal bo'ldi",
            "bir kishi bilan urishib qoldik", "urishib qoldik",
            "menga og'zaki aytishdi",
            "shartnomaga qo'l qo'ydirishdi"
        )

        if (norm.contains("siqishyapti")) {
            missingFacts.add("pressure_type")
        } else if (norm.contains("hujjatimni olib") || norm.contains("dokumntimni olib") || norm.contains("dokumentimni olib") || norm.contains("uni olib qo'yishdi")) {
            missingFacts.add("withheld_item_type")
        } else if (ambiguousPhrases.any { norm.contains(it) || norm == it }) {
            missingFacts.add("vague_complaint_details")
        }

        // Direct Article Matching (e.g. "JK 97", "MJTK 128", "125-modda")
        val articleMatch = Regex("([0-9]{1,3})\\s*[-–—]?\\s*modda", RegexOption.IGNORE_CASE).find(norm)
        if (articleMatch != null) {
            val artNum = articleMatch.groupValues[1]
            val artNumStr = "$artNum-modda"
            if (norm.contains("jk") || norm.contains("jinoyat")) {
                searchQueries.add(0, "jk_$artNum")
            } else if (norm.contains("fk") || norm.contains("fuqarolik")) {
                searchQueries.add(0, "fk_$artNum")
            } else if (norm.contains("mk") || norm.contains("mehnat")) {
                searchQueries.add(0, "mk_$artNum")
            } else if (norm.contains("mjtk") || norm.contains("mjoek") || norm.contains("mamuriy")) {
                searchQueries.add(0, "mjtk_$artNum")
            }
            searchQueries.add(artNumStr)
        }

        // Contextual Follow-Up Handling
        val isContextualFollowUp = norm.contains("endi boshimga nima tushadi") ||
                norm.contains("sudga bersam bo'ladimi") ||
                norm.contains("qancha kompensatsiya") ||
                (norm.contains("nima bo'ladi") && !norm.contains("zo'rladim"))

        if (isContextualFollowUp && historyIssues.isNotEmpty()) {
            domains.addAll(historyDomains)
            issues.addAll(historyIssues)
            if (historyIssues.contains("unlawful_dismissal")) {
                searchQueries.add("mk_161")
                searchQueries.add("161-modda")
                searchQueries.add("mehnat shartnomasini bekor qilish 161-modda")
            }
            if (historyIssues.contains("red_light_violation") || historyIssues.contains("driving_without_license")) {
                searchQueries.add("mjtk_128")
                searchQueries.add("128-modda")
                searchQueries.add("128-4-modda")
                searchQueries.add("mjtk_135")
                searchQueries.add("135-modda")
            }
        }

        if (searchQueries.isEmpty() && !isUnmatchedDomain) {
            val expanded = UzbekTextNormalizer.expandLegalSynonyms(norm)
            searchQueries.addAll(expanded)
        }

        val requiresClarification = missingFacts.isNotEmpty() && !isContextualFollowUp

        // Build InternalCategory
        val internalCategory = when {
            isEmotionalSharing -> InternalCategory.EMOTIONAL_SUPPORT
            isGreetingOrAck && !containsSpecificLegalProblemKeywords(norm) -> InternalCategory.SOCIAL
            hasNonLegalKeyword && hasLegalKeywordsInCurrentQuery -> InternalCategory.MIXED
            hasNonLegalKeyword -> InternalCategory.GENERAL
            isContextualFollowUp -> InternalCategory.CLARIFICATION
            requiresClarification -> InternalCategory.CLARIFICATION
            norm.contains("modda") || norm.contains("kodeks") || norm.contains("qonun") -> InternalCategory.LEGAL
            else -> InternalCategory.LEGAL_SITUATION
        }

        // Build InternalInterpretationSchema
        val scopeCategory = when {
            isEmotionalSharing -> ScopeCategory.GREETING_OR_ACKNOWLEDGMENT
            isGreetingOrAck && !containsSpecificLegalProblemKeywords(norm) -> ScopeCategory.GREETING_OR_ACKNOWLEDGMENT
            hasNonLegalKeyword && !hasLegalKeywordsInCurrentQuery -> ScopeCategory.UNRELATED_REQUEST
            isSeriousDisclosure || isUrgentThreat -> ScopeCategory.URGENT_SAFETY_RELATED
            isDocumentExplanation -> ScopeCategory.LEGAL_DOCUMENT_EXPLANATION
            requiresClarification -> ScopeCategory.LEGAL_SITUATION_NEEDS_CLARIFICATION
            else -> ScopeCategory.IN_SCOPE_LEGAL_QUESTION
        }

        val schema = InternalInterpretationSchema(
            intent = when {
                isUserCorrection -> "correction"
                isEmotionalSharing -> "emotional_disclosure"
                isGreetingOrAck && !containsSpecificLegalProblemKeywords(norm) -> "greeting_or_thanks"
                hasNonLegalKeyword && !hasLegalKeywordsInCurrentQuery -> "unrelated_request"
                isSeriousDisclosure -> "criminal_disclosure"
                isUrgentThreat -> "urgent_threat"
                isDocumentExplanation -> "document_explanation"
                requiresClarification -> "ambiguous_complaint"
                else -> "legal_question"
            },
            userGoal = when {
                isUserCorrection -> "communication_guidance"
                isEmotionalSharing -> "vent_emotions"
                isGreetingOrAck -> "social_chat"
                hasNonLegalKeyword && !hasLegalKeywordsInCurrentQuery -> "redirect"
                isSeriousDisclosure || isUrgentThreat -> "safety_and_emergency"
                requiresClarification -> "explore_options"
                else -> "understand_options"
            },
            situationSummary = raw.take(150),
            emotionalCues = if (isEmotionalSharing || isUrgentThreat) listOf("anxiety", "frustration") else listOf("neutral"),
            knownFacts = conversationState.confirmedFacts,
            uncertainFacts = conversationState.unconfirmedClaims,
            missingCriticalFact = missingFacts.firstOrNull(),
            needsClarification = requiresClarification,
            needsLegalRetrieval = scopeCategory == ScopeCategory.IN_SCOPE_LEGAL_QUESTION || scopeCategory == ScopeCategory.LEGAL_DOCUMENT_EXPLANATION || scopeCategory == ScopeCategory.URGENT_SAFETY_RELATED,
            urgency = if (isUrgentThreat || isSeriousDisclosure) "urgent_safety" else "normal",
            nextAction = when {
                isUserCorrection -> "provide_communication_advice"
                isEmotionalSharing -> "acknowledge_and_listen"
                isGreetingOrAck && !containsSpecificLegalProblemKeywords(norm) -> "respond_naturally"
                hasNonLegalKeyword && !hasLegalKeywordsInCurrentQuery -> "polite_redirect"
                isSeriousDisclosure -> "safety_guidance"
                isUrgentThreat -> "safety_guidance"
                requiresClarification -> "ask_clarification"
                else -> "retrieve_legal_sources"
            },
            responseStyle = when {
                isEmotionalSharing -> "empathetic"
                isGreetingOrAck && !containsSpecificLegalProblemKeywords(norm) -> "conversational"
                hasNonLegalKeyword && !hasLegalKeywordsInCurrentQuery -> "conversational"
                isSeriousDisclosure || isUrgentThreat -> "emergency"
                requiresClarification -> "clarifying"
                else -> "structured_legal"
            }
        )

        val clarificationQuestion = when {
            missingFacts.contains("written_order_or_notice") ->
                "Tushundim, bu noqulay vaziyat. Sizni rasmiy buyruq bilan ishdan bo‘shatishdimi yoki og‘zaki ravishda ishga kelmaslikni aytishdimi?"
            missingFacts.contains("loan_proof") ->
                "Tushundim. Qarz yoki kelishuv bo'yicha yozma tilxat, shartnoma yoki bank o'tkazmasi kabi tasdiqlovchi hujjat bormi, yoki faqat og'zaki bo'lganmi?"
            missingFacts.contains("traffic_protocol_type") ->
                "Tushundim. Siz mashinada svetoforning qizil chirog'ida o'tdingizmi va sizga YHXX xodimi tomonidan protokol tuzildimi yoki radar tushirdimi?"
            missingFacts.contains("withheld_item_type") ->
                "Nimani olib qo'yishdi — hujjat, telefon yoki boshqa narsanimi?"
            missingFacts.contains("pressure_type") ->
                "Qaysi ma'noda aytyapsiz — ishda bosim qilishyaptimi yoki boshqa vaziyatmi?"
            missingFacts.contains("vague_complaint_details") ->
                "Tushundim, bu noqulay vaziyat. Sizga aniq yordam bera olishim uchun, holat haqida biroz batafsilroq ma'lumot bera olasizmi?"
            else -> "Sizga amaliy va aniq huquqiy yordam berishim uchun vaziyatingiz haqida biroz aniqroq ma'lumot bera olasizmi?"
        }

        return UserQueryAnalysis(
            originalQuery = raw,
            normalizedQuery = norm,
            scopeCategory = scopeCategory,
            internalCategory = internalCategory,
            schema = schema,
            conversationState = conversationState,
            isSeriousDisclosure = isSeriousDisclosure,
            isGreeting = (scopeCategory == ScopeCategory.GREETING_OR_ACKNOWLEDGMENT),
            isEmotionalSharing = (schema.intent == "emotional_disclosure"),
            isUnmatchedDomain = isUnmatchedDomain || (hasNonLegalKeyword && !hasLegalKeywordsInCurrentQuery),
            detectedDomains = domains.distinct(),
            detectedIssues = issues.distinct(),
            searchQueries = searchQueries.distinct(),
            missingFacts = missingFacts,
            requiresClarification = requiresClarification,
            clarificationQuestion = clarificationQuestion,
            mixedLegalQuery = if (hasNonLegalKeyword && hasLegalKeywordsInCurrentQuery) raw else null
        )
    }

    private fun buildConversationState(normQuery: String, history: List<ChatMessage>): ConversationState {
        val confirmed = mutableListOf<String>()
        val unconfirmed = mutableListOf<String>()
        val events = mutableListOf<String>()

        history.filter { it.sender == "user" }.forEach { msg ->
            val n = UzbekTextNormalizer.normalizeText(msg.text)
            if (n.contains("buyruq berdi") || n.contains("yozma buyruq")) confirmed.add("written_dismissal_order_issued")
            if (n.contains("tilxat bor") || n.contains("shartnoma bor")) confirmed.add("written_loan_agreement_exists")
            if (n.contains("inspektor protokol tuzdi")) confirmed.add("traffic_protocol_issued")
            events.add(msg.text.take(80))
        }

        val isLitigationDesired = !normQuery.contains("sudga bermoqchi emasman") &&
                !normQuery.contains("sud shart emas") &&
                !normQuery.contains("sudga bermoqchimasman")

        return ConversationState(
            userObjective = if (!isLitigationDesired) "communication_guidance" else "understand_legal_options",
            situationSummary = normQuery.take(100),
            confirmedFacts = confirmed.distinct(),
            unconfirmedClaims = unconfirmed.distinct(),
            sequenceOfEvents = events,
            isLitigationDesired = isLitigationDesired
        )
    }

    private fun checkIsGreetingOrAck(normQuery: String): Boolean {
        if (normQuery.isBlank()) return true
        val singleWordGreetingOrAck = setOf(
            "salom", "assalom", "privet", "hello", "hi", "qalaysiz", "qandaysiz",
            "rahmat", "raxmat", "tashakkur", "spasibo", "arzimaydi", "ok",
            "tushundim", "xayr", "thanks", "brat"
        )
        val multiWordGreetingOrAck = listOf(
            "assalomu alaykum", "xayrli kun", "xayrli tong", "xayrli kech",
            "ishlar qanday", "ismingiz nima", "isming nima", "sen kimsan", "siz kimsiz",
            "tanishaylik", "sog' bo'ling", "ko'rishguncha", "zdravstvuyte",
            "dobryy den", "kak dela", "good morning", "how are you", "salom brat"
        )

        val queryTokens = normQuery.split(Regex("\\s+")).toSet()
        if (queryTokens.any { it in singleWordGreetingOrAck }) return true
        if (multiWordGreetingOrAck.any { normQuery.contains(it) }) return true
        if (normQuery.startsWith("ismim") || normQuery.startsWith("mening ismim") || normQuery.startsWith("меня зовут")) return true
        return false
    }

    private fun containsSpecificLegalProblemKeywords(normQuery: String): Boolean {
        val keywords = listOf(
            "ishdan", "haydashdi", "bo'shatishdi", "prikaz", "uvolnenie", "bosim", "oylik", "zarplata", "maosh", "mehnat", "shartnoma", "shartnomasini", "kiritmayapti",
            "qarz", "pulimni", "aldashdi", "tilxat", "dolg", "ijara", "kvartira", "arenda", "uyimni",
            "qizil", "svetofor", "remen", "kamar", "prava", "pravasiz", "gaylar", "jarima", "protokol", "bayonnoma",
            "sud", "sudga", "advokat", "prokuratura", "militsiya", "urdi", "o'ldir", "tahdid", "zorladim", "zo'rladim"
        )
        return keywords.any { normQuery.contains(it) }
    }
}
