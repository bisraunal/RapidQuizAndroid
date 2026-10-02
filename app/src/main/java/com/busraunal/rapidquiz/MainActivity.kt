package com.busraunal.rapidquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.busraunal.rapidquiz.ui.navigation.RapidQuizNavGraph
import com.busraunal.rapidquiz.ui.theme.RapidQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RapidQuizTheme {
                val navController = rememberNavController()
                RapidQuizNavGraph(navController = navController)
            }
        }
    }
}