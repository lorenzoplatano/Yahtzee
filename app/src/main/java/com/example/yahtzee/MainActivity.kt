package com.example.yahtzee

import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.util.AppLanguage
import com.example.yahtzee.util.LocalLocalizationManager
import com.example.yahtzee.util.LocalizationManager
import com.example.yahtzee.util.SettingsManager
import com.example.yahtzee.screens.*
import com.example.yahtzee.ui.theme.YahtzeeTheme
import com.example.yahtzee.util.ShakeDetector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    private var savedLanguage: AppLanguage? = null

    companion object {
        private var localizationManager = LocalizationManager()

        fun getLocalizationManager(): LocalizationManager {
            return localizationManager
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // Carica la lingua salvata dalle preferenze PRIMA di creare il contesto
        if (!::settingsManager.isInitialized) {
            settingsManager = SettingsManager(newBase.applicationContext)
        }
        // Carica la lingua in modo sincrono
        val language = runBlocking {
            settingsManager.languageFlow.first()
        }
        savedLanguage = language
        localizationManager.setLanguage(language)
        val context = getLocalizationManager().applyLanguage(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Raccoglie i flussi delle impostazioni come stati in Compose
            val isDarkTheme by settingsManager.isDarkThemeFlow.collectAsState(initial = false)
            val isShakeEnabled by settingsManager.isShakeEnabledFlow.collectAsState(initial = true)
            val currentLanguage by settingsManager.languageFlow.collectAsState(initial = savedLanguage ?: AppLanguage.ITALIAN)

            // Aggiorna il LocalizationManager solo se cambia la lingua
            LaunchedEffect(currentLanguage) {
                localizationManager.setLanguage(currentLanguage)
            }

            CompositionLocalProvider(LocalLocalizationManager provides localizationManager) {
                YahtzeeTheme(darkTheme = isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        YahtzeeApp(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { newValue ->
                                lifecycleScope.launch {
                                    settingsManager.setDarkTheme(newValue)
                                }
                            },
                            onLanguageChange = { language ->
                                lifecycleScope.launch {
                                    settingsManager.setLanguage(language)
                                    localizationManager.setLanguage(language)
                                    recreateActivity()
                                }
                            },
                            isShakeEnabled = isShakeEnabled,
                            onShakeToggle = { newValue ->
                                lifecycleScope.launch {
                                    settingsManager.setShakeEnabled(newValue)
                                }
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
        onLanguageChange: (AppLanguage) -> Unit,
        isShakeEnabled: Boolean,
        onShakeToggle: (Boolean) -> Unit
    ) {
        // Il resto del codice di YahtzeeApp rimane invariato
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