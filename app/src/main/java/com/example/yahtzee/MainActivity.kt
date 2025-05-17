package com.example.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.screens.GameScreenMultiplayer
import com.example.yahtzee.screens.GameScreenSinglePlayer
import com.example.yahtzee.screens.History
import com.example.yahtzee.screens.Homepage
import com.example.yahtzee.screens.Settings
import com.example.yahtzee.ui.theme.YahtzeeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    YahtzeeApp()
                }
            }
        }
    }

    @Composable
    fun YahtzeeApp() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "homepage") {
            composable("homepage") { Homepage(navController) }
            composable("history") { History(navController) }
            composable("settings") { Settings(navController) }
            composable("game_1vs1") { GameScreenSinglePlayer(navController) } // <--- AGGIUNTA QUI
            composable("game") { GameScreenMultiplayer(navController) }
        }
    }

}



