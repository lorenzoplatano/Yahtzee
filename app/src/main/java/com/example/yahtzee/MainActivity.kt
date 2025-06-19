package com.example.yahtzee

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
import com.example.yahtzee.util.ShakeDetector

class MainActivity : ComponentActivity() {
    companion object {
        private var localizationManager = LocalizationManager()
        // Rimuovi questa variabile statica
        // private var isShakeEnabled by mutableStateOf(true)
        fun getLocalizationManager(): LocalizationManager {
            return localizationManager
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val context = getLocalizationManager().applyLanguage(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Stato globale per il tema (chiaro/scuro)
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            // Stato globale per lo shake - con persistenza
            var isShakeEnabled by rememberSaveable {
                mutableStateOf(loadShakePreference())
            }

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
                                getLocalizationManager().setLanguage(language)
                                recreateActivity()
                            },
                            isShakeEnabled = isShakeEnabled,
                            onShakeToggle = { enabled ->
                                isShakeEnabled = enabled
                                saveShakePreference(enabled)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun recreateActivity() {
        recreate()
    }

    // Funzioni per salvare/caricare le preferenze shake
    private fun saveShakePreference(enabled: Boolean) {
        getSharedPreferences("yahtzee_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("shake_enabled", enabled)
            .apply()
    }

    private fun loadShakePreference(): Boolean {
        return getSharedPreferences("yahtzee_settings", Context.MODE_PRIVATE)
            .getBoolean("shake_enabled", true) // default true
    }

    @Composable
    fun YahtzeeApp(
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit,
        onLanguageChange: (AppLanguage) -> Unit,
        isShakeEnabled: Boolean,
        onShakeToggle: (Boolean) -> Unit
    ) {
        val navController = rememberNavController()
        val context = this

        // Stato persistente per controllare se mostrare la selezione modalità
        var showModeSelection by rememberSaveable { mutableStateOf(false) }

        // Stato per triggerare lo shake nelle schermate di gioco
        var singlePlayerShakeTrigger by remember { mutableStateOf(0) }
        var multiPlayerShakeTrigger by remember { mutableStateOf(0) }

        val shakeDetector = remember {
            ShakeDetector {
                // Controlla se lo shake è abilitato prima di eseguire l'azione
                if (isShakeEnabled) {
                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    when (currentRoute) {
                        "game" -> singlePlayerShakeTrigger++
                        "game_1vs1" -> multiPlayerShakeTrigger++
                    }
                }
            }
        }

        // Setup ShakeDetector solo quando serve (cioè quando siamo in una schermata di gioco)
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Registra/deregistra il listener in base al ciclo di vita della composable
        LaunchedEffect(navController) {
            val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                when (destination.route) {
                    "game", "game_1vs1" -> {
                        // Registra il listener solo se lo shake è abilitato
                        if (isShakeEnabled) {
                            shakeDetector.register(sensorManager)
                        }
                    }
                    else -> {
                        shakeDetector.unregister(sensorManager)
                    }
                }
            }
            navController.addOnDestinationChangedListener(listener)
        }

        // Aggiorna la registrazione del shake detector quando cambia isShakeEnabled
        LaunchedEffect(isShakeEnabled) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == "game" || currentRoute == "game_1vs1") {
                if (isShakeEnabled) {
                    shakeDetector.register(sensorManager)
                } else {
                    shakeDetector.unregister(sensorManager)
                }
            }
        }

        NavHost(navController = navController, startDestination = "homepage") {
            // ... altre composable routes ...

            composable("settings") {
                Settings(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onLanguageChange = onLanguageChange,
                    isShakeEnabled = isShakeEnabled,
                    onShakeToggle = onShakeToggle
                )
            }

            composable("game") {
                SinglePlayerGameScreen(
                    navController = navController,
                    shakeTrigger = singlePlayerShakeTrigger
                )
            }

            composable("game_1vs1") {
                MultiplayerGameScreen(
                    navController = navController,
                    shakeTrigger = multiPlayerShakeTrigger
                )
            }


        }
    }
}