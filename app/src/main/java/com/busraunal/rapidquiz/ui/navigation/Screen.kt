package com.busraunal.rapidquiz.ui.navigation

sealed class Screen(val route: String) {
    object Category : Screen("categories")
    object Quiz : Screen("quiz/{slug}") {
        fun createRoute(slug: String) = "quiz/$slug"
    }
    object Leaderboard : Screen("leaderboard")
}
