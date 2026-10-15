package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.repository.LegalRepository
import com.example.ui.components.GlobalAIFab
import com.example.ui.components.UzLegalBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy { LegalRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(repository = repository)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    data class AIChat(val initialPrompt: String? = null) : Screen("ai_chat")
    object Bookmarks : Screen("bookmarks")
    object Profile : Screen("profile")
    object Legislation : Screen("legislation")
    data class CodeDetail(val codeId: String) : Screen("code_detail")
    data class ArticleDetail(val articleId: String) : Screen("article_detail")
    data class CaseAnalyzer(val initialScenarioId: String? = null) : Screen("case_analyzer")
    object Quiz : Screen("quiz")
    object Academy : Screen("academy")
    object DocumentGenerator : Screen("document_generator")
    object Updates : Screen("updates")
    object Offline : Screen("offline")
}

@Composable
fun MainAppContainer(repository: LegalRepository) {
    var screenStack by remember { mutableStateOf(listOf<Screen>(Screen.Home)) }
    val currentScreen = screenStack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen) {
        screenStack = screenStack + screen
    }

    fun navigateToTopLevel(route: String) {
        val topScreen: Screen = when (route) {
            "home" -> Screen.Home
            "search" -> Screen.Search
            "ai_chat" -> Screen.AIChat()
            "bookmarks" -> Screen.Bookmarks
            "profile" -> Screen.Profile
            else -> Screen.Home
        }
        screenStack = listOf(topScreen)
    }

    fun popBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    // Handle system back gesture
    BackHandler(enabled = screenStack.size > 1) {
        popBack()
    }

    val isTopLevel = currentScreen is Screen.Home ||
            currentScreen is Screen.Search ||
            currentScreen is Screen.AIChat ||
            currentScreen is Screen.Bookmarks ||
            currentScreen is Screen.Profile

    val showFab = currentScreen !is Screen.AIChat

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevel) {
                UzLegalBottomBar(
                    currentRoute = currentScreen.route,
                    onNavigate = { route -> navigateToTopLevel(route) }
                )
            }
        },
        floatingActionButton = {
            GlobalAIFab(
                visible = showFab,
                contextSnippet = when (currentScreen) {
                    is Screen.CodeDetail -> "Kodeks"
                    is Screen.ArticleDetail -> "Modda"
                    is Screen.CaseAnalyzer -> "Keys"
                    is Screen.Quiz -> "Test"
                    else -> null
                },
                hasBottomBar = isTopLevel,
                onClick = {
                    val contextualPrompt = when (val s = currentScreen) {
                        is Screen.CodeDetail -> {
                            val code = repository.getCodeById(s.codeId)
                            "Assalomu alaykum! Menga ${code?.shortName ?: "ushbu kodeks"} bo'yicha tushuntirish va amaliy misollar bering."
                        }
                        is Screen.ArticleDetail -> {
                            val article = repository.getArticleById(s.articleId)
                            "Assalomu alaykum! ${article?.articleNumberStr ?: "Ushbu modda"} (${article?.title ?: ""}) bo'yicha yuridik sharh va sud amaliyoti qanday?"
                        }
                        is Screen.CaseAnalyzer -> {
                            "Assalomu alaykum! Huquqiy vaziyat (keys) tahlilida qanday huquqiy normalar qo'llanilishi kerakligini tushuntirib bering."
                        }
                        is Screen.Quiz -> {
                            "Assalomu alaykum! Huquqiy test savollariga tayyorlanishda qaysi kodeks moddalariga ko'proq e'tibor qaratish kerak?"
                        }
                        else -> null
                    }
                    navigateTo(Screen.AIChat(initialPrompt = contextualPrompt))
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isTopLevel) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        repository = repository,
                        onNavigate = { route ->
                            when (route) {
                                "search" -> navigateTo(Screen.Search)
                                "case_analyzer" -> navigateTo(Screen.CaseAnalyzer())
                                "legislation" -> navigateTo(Screen.Legislation)
                                "document_generator" -> navigateTo(Screen.DocumentGenerator)
                                "updates" -> navigateTo(Screen.Updates)
                                "quiz" -> navigateTo(Screen.Quiz)
                                "academy" -> navigateTo(Screen.Academy)
                                "profile" -> navigateTo(Screen.Profile)
                                "offline" -> navigateTo(Screen.Offline)
                                else -> navigateToTopLevel(route)
                            }
                        },
                        onOpenScenario = { scenarioId ->
                            navigateTo(Screen.CaseAnalyzer(initialScenarioId = scenarioId))
                        }
                    )
                }

                is Screen.Search -> {
                    SearchScreen(
                        repository = repository,
                        onBack = { popBack() },
                        onSelectArticle = { articleId ->
                            navigateTo(Screen.ArticleDetail(articleId))
                        }
                    )
                }

                is Screen.AIChat -> {
                    AIAssistantScreen(
                        repository = repository,
                        initialPrompt = screen.initialPrompt,
                        onBack = { popBack() },
                        onSelectArticle = { articleId ->
                            navigateTo(Screen.ArticleDetail(articleId))
                        }
                    )
                }

                is Screen.Bookmarks -> {
                    ProfileScreen(
                        repository = repository,
                        onSelectArticle = { articleId ->
                            navigateTo(Screen.ArticleDetail(articleId))
                        }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        repository = repository,
                        onSelectArticle = { articleId ->
                            navigateTo(Screen.ArticleDetail(articleId))
                        }
                    )
                }

                is Screen.Legislation -> {
                    LegislationScreen(
                        repository = repository,
                        onSelectCode = { codeId ->
                            navigateTo(Screen.CodeDetail(codeId))
                        },
                        onBack = { popBack() }
                    )
                }

                is Screen.CodeDetail -> {
                    CodeDetailScreen(
                        codeId = screen.codeId,
                        repository = repository,
                        onBack = { popBack() },
                        onSelectArticle = { articleId ->
                            navigateTo(Screen.ArticleDetail(articleId))
                        }
                    )
                }

                is Screen.ArticleDetail -> {
                    ArticleDetailScreen(
                        articleId = screen.articleId,
                        repository = repository,
                        onBack = { popBack() },
                        onSelectRelatedArticle = { relId ->
                            navigateTo(Screen.ArticleDetail(relId))
                        }
                    )
                }

                is Screen.CaseAnalyzer -> {
                    CaseAnalyzerScreen(
                        repository = repository,
                        initialScenarioId = screen.initialScenarioId,
                        onBack = { popBack() },
                        onSelectArticle = { articleId ->
                            navigateTo(Screen.ArticleDetail(articleId))
                        }
                    )
                }

                is Screen.Quiz -> {
                    QuizScreen(
                        repository = repository,
                        onBack = { popBack() }
                    )
                }

                is Screen.Academy -> {
                    AcademyScreen(
                        repository = repository,
                        onStartQuiz = { navigateTo(Screen.Quiz) },
                        onNavigate = { route -> navigateToTopLevel(route) }
                    )
                }

                is Screen.DocumentGenerator -> {
                    DocumentGeneratorScreen(
                        repository = repository,
                        onBack = { popBack() }
                    )
                }

                is Screen.Updates -> {
                    UpdatesScreen(
                        repository = repository,
                        onBack = { popBack() }
                    )
                }

                is Screen.Offline -> {
                    OfflineScreen(
                        onNavigateToCodes = { navigateTo(Screen.Legislation) },
                        onBack = { popBack() }
                    )
                }
            }
        }
    }
}
