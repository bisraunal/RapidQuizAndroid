package com.busraunal.rapidquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.busraunal.rapidquiz.ui.categories.CategoryScreen
import com.busraunal.rapidquiz.ui.leaderboard.LeaderboardScreen
import com.busraunal.rapidquiz.ui.quiz.QuizScreen

@Composable
fun RapidQuizNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Category.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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
                onViewLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route) {
                        popUpTo(Screen.Category.route)
                    }
                }
            )
        }

        composable(route = Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
