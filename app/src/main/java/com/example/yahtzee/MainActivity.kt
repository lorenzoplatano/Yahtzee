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

        // Stato globale per il tema (chiaro/scuro)
        var isDarkTheme by mutableStateOf(false)
        var isShakeEnabled by mutableStateOf(true)

        setContent {
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
                            onShakeToggle = { isShakeEnabled = it }
                        )
                    }
                }
            }
        }
    }

    private fun recreateActivity() {
        recreate()
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

        var showModeSelection by rememberSaveable { mutableStateOf(false) }
        var singlePlayerShakeTrigger by remember { mutableStateOf(0) }
        var multiPlayerShakeTrigger by remember { mutableStateOf(0) }

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val shakeDetector = remember {
            ShakeDetector {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                when (currentRoute) {
                    "game" -> singlePlayerShakeTrigger++
                    "game_1vs1" -> multiPlayerShakeTrigger++
                }
            }
        }

        // Listener per la navigazione e per l'attivazione shake
        DisposableEffect(isShakeEnabled, navController) {
            val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
                val isGameScreen = destination.route == "game" || destination.route == "game_1vs1"
                if (isGameScreen && isShakeEnabled) {
                    shakeDetector.register(sensorManager)
                } else {
                    shakeDetector.unregister(sensorManager)
                }
            }
            navController.addOnDestinationChangedListener(listener)

            onDispose {
                navController.removeOnDestinationChangedListener(listener)
                shakeDetector.unregister(sensorManager)
            }
        }

        // Aggiorna la registrazione del shake detector quando cambia isShakeEnabled
        LaunchedEffect(isShakeEnabled) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val isGameScreen = currentRoute == "game" || currentRoute == "game_1vs1"
            if (isGameScreen && isShakeEnabled) {
                shakeDetector.register(sensorManager)
            } else {
                shakeDetector.unregister(sensorManager)
            }
        }

        NavHost(navController = navController, startDestination = "homepage") {
            composable("homepage") {
                Homepage(
                    navController = navController,
                    showModeSelection = showModeSelection,
                    onModeSelectionChanged = { showModeSelection = it }
                )
            }
            composable("history") { HistoryScreen(navController) }
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
            composable("game_1vs1") {
                MultiplayerGameScreen(
                    navController = navController,
                    shakeTrigger = multiPlayerShakeTrigger
                )
            }
            composable("game") {
                SinglePlayerGameScreen(
                    navController = navController,
                    shakeTrigger = singlePlayerShakeTrigger
                )
            }
        }
    }
}