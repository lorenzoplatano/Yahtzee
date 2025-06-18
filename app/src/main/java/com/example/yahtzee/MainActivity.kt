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

        setContent {
            // Stato globale per il tema (chiaro/scuro)
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            // Fornisci il LocalizationManager a tutta l'app
            CompositionLocalProvider(LocalLocalizationManager provides getLocalizationManager()) {
                // Applica il tema UNA SOLA VOLTA globalmente
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

    @Composable
    fun YahtzeeApp(
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit,
        onLanguageChange: (AppLanguage) -> Unit
    ) {
        val navController = rememberNavController()
        val context = this

        // Stato per triggerare lo shake nelle schermate di gioco
        var singlePlayerShakeTrigger by remember { mutableStateOf(0) }
        var multiPlayerShakeTrigger by remember { mutableStateOf(0) }

        // Setup ShakeDetector solo quando serve (cioè quando siamo in una schermata di gioco)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val shakeDetector = remember {
            ShakeDetector {
                // Determina quale schermata di gioco è attiva e triggera il relativo stato
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                when (currentRoute) {
                    "game" -> singlePlayerShakeTrigger++
                    "game_1vs1" -> multiPlayerShakeTrigger++
                }
            }
        }

        // Registra/deregistra il listener in base al ciclo di vita della composable
        DisposableEffect(Unit) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(
                shakeDetector,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
            onDispose {
                sensorManager.unregisterListener(shakeDetector)
            }
        }

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
            composable("game_1vs1") {
                MultiplayerGameScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    shakeTrigger = multiPlayerShakeTrigger
                )
            }
            composable("game") {
                SinglePlayerGameScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    shakeTrigger = singlePlayerShakeTrigger
                )
            }
        }
    }
}