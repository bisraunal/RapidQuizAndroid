package com.busraunal.rapidquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.busraunal.rapidquiz.data.model.QuizSubmitRequest
import com.busraunal.rapidquiz.ui.categories.CategoryScreen
import com.busraunal.rapidquiz.ui.leaderboard.LeaderboardScreen
import com.busraunal.rapidquiz.ui.quiz.QuizScreen
import com.busraunal.rapidquiz.ui.result.ResultScreen

data class QuizResultData(
    val categoryName: String = "",
    val categorySlug: String = "",
    val totalQuestions: Int = 20,
    val answeredCount: Int = 0,
    val emptyCount: Int = 0,
    val totalTimeTaken: Double = 0.0,
    val submitRequest: QuizSubmitRequest = QuizSubmitRequest("", "", emptyList())
)

@Composable
fun RapidQuizNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Category.route
) {
    var lastQuizResult by remember { mutableStateOf<QuizResultData?>(null) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Home / Category Screen
        composable(route = Screen.Category.route) {
            CategoryScreen(
                onCategoryClick = { slug ->
                    navController.navigate(Screen.Quiz.createRoute(slug))
                },
                onLeaderboardClick = {
                    navController.navigate(Screen.Leaderboard.route)
                }
            )
        }

        // 2. Quiz Playing Screen
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("slug") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: "yazilim"
            QuizScreen(
                categorySlug = slug,
                onBackToCategories = {
                    navController.popBackStack(Screen.Category.route, inclusive = false)
                },
                onQuizCompleted = { catName, catSlug, totalQ, answered, empty, totalTime, submitReq ->
                    lastQuizResult = QuizResultData(
                        categoryName = catName,
                        categorySlug = catSlug,
                        totalQuestions = totalQ,
                        answeredCount = answered,
                        emptyCount = empty,
                        totalTimeTaken = totalTime,
                        submitRequest = submitReq
                    )
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Category.route)
                    }
                }
            )
        }

        // 3. Full-Page Result Screen (Web ResultView ile aynı)
        composable(route = Screen.Result.route) {
            val result = lastQuizResult
            if (result != null) {
                ResultScreen(
                    categoryName = result.categoryName,
                    categorySlug = result.categorySlug,
                    totalQuestions = result.totalQuestions,
                    answeredCount = result.answeredCount,
                    emptyCount = result.emptyCount,
                    totalTimeTaken = result.totalTimeTaken,
                    submitRequest = result.submitRequest,
                    onPlayAgain = {
                        navController.navigate(Screen.Quiz.createRoute(result.categorySlug)) {
                            popUpTo(Screen.Category.route)
                        }
                    },
                    onChooseCategory = {
                        navController.popBackStack(Screen.Category.route, inclusive = false)
                    }
                )
            } else {
                navController.popBackStack(Screen.Category.route, inclusive = false)
            }
        }

        // 4. Leaderboard Screen
        composable(route = Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateHome = {
                    navController.popBackStack(Screen.Category.route, inclusive = false)
                }
            )
        }
    }
}
