package com.example.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.screens.HistoryScreen
import com.example.yahtzee.screens.Homepage
import com.example.yahtzee.screens.MultiplayerGameScreen
import com.example.yahtzee.screens.Settings
import com.example.yahtzee.screens.SinglePlayerGameScreen
import com.example.yahtzee.ui.theme.YahtzeeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Stato globale per il tema (chiaro/scuro)
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            YahtzeeTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    YahtzeeApp(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }

    @Composable
    fun YahtzeeApp(
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "homepage") {
            composable("homepage") { Homepage(navController) }
            composable("history") { HistoryScreen(navController, isDarkTheme) }
            composable("settings") { Settings(navController, isDarkTheme, onThemeChange) }
            composable("game_1vs1") { MultiplayerGameScreen(navController) }
            composable("game") { SinglePlayerGameScreen(navController) }
        }
    }
}
