package com.example.yahtzee

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.localization.AppLanguage
import com.example.yahtzee.localization.LocalLocalizationManager
import com.example.yahtzee.localization.LocalizationManager
import com.example.yahtzee.screens.HistoryScreen
import com.example.yahtzee.screens.Homepage
import com.example.yahtzee.screens.MultiplayerGameScreen
import com.example.yahtzee.screens.Settings
import com.example.yahtzee.screens.SinglePlayerGameScreen
import com.example.yahtzee.ui.theme.YahtzeeTheme

class MainActivity : ComponentActivity() {
    // Singleton per memorizzare la lingua selezionata
    companion object {
        private var localizationManager = LocalizationManager()
        
        fun getLocalizationManager(): LocalizationManager {
            return localizationManager
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // Applica la lingua corrente al context base
        val context = getLocalizationManager().applyLanguage(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            // Stato globale per il tema (chiaro/scuro)
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            
            CompositionLocalProvider(LocalLocalizationManager provides getLocalizationManager()) {
                YahtzeeTheme(darkTheme = isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        YahtzeeApp(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDarkTheme = it },
                            onLanguageChange = { language -> 
                                // Aggiorna la lingua nel LocalizationManager
                                getLocalizationManager().setLanguage(language)
                                
                                // Ricrea l'attività per applicare la nuova lingua
                                recreateActivity()
                            }
                        )
                    }
                }
            }
        }
    }
    
    private fun recreateActivity() {
        // Ricrea l'attività per applicare il cambio lingua
        recreate()
    }

    @Composable
    fun YahtzeeApp(
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit,
        onLanguageChange: (AppLanguage) -> Unit
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "homepage") {
            composable("homepage") { Homepage(navController, isDarkTheme) }
            composable("history") { HistoryScreen(navController, isDarkTheme) }
            composable("settings") { 
                Settings(
                    navController = navController, 
                    isDarkTheme = isDarkTheme, 
                    onThemeChange = onThemeChange,
                    onLanguageChange = onLanguageChange
                ) 
            }
            composable("game_1vs1") { MultiplayerGameScreen(navController) }
            composable("game") { SinglePlayerGameScreen(navController) }
        }
    }
}
