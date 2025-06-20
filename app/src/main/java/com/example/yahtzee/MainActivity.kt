package com.example.yahtzee

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.repository.GameHistoryRepository
import com.example.yahtzee.repository.SettingsRepository
import com.example.yahtzee.screens.*
import com.example.yahtzee.ui.theme.YahtzeeTheme
import com.example.yahtzee.model.AppLanguage
import com.example.yahtzee.repository.GameSaveRepository
import com.example.yahtzee.util.LocalLocalizationManager
import com.example.yahtzee.util.LocalizationManager
import com.example.yahtzee.util.ShakeDetector
import com.example.yahtzee.viewmodel.HistoryViewModel
import com.example.yahtzee.viewmodel.HistoryViewModelFactory
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel
import com.example.yahtzee.viewmodel.MultiplayerGameViewModelFactory
import com.example.yahtzee.viewmodel.SettingsViewModel
import com.example.yahtzee.viewmodel.SettingsViewModelFactory
import com.example.yahtzee.viewmodel.SinglePlayerGameViewModel
import com.example.yahtzee.viewmodel.SinglePlayerGameViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    // Inizializzazione delle configurazioni per selezione lingua e impostazioni prima del lancio dell'app

    private lateinit var settingsRepository: SettingsRepository
    private var savedLanguage: AppLanguage? = null

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(settingsRepository)
    }

    companion object {
        private var localizationManager = LocalizationManager()

        fun getLocalizationManager(): LocalizationManager {
            return localizationManager
        }
    }

    override fun attachBaseContext(newBase: Context) {
        if (!::settingsRepository.isInitialized) {
            settingsRepository = SettingsRepository(newBase.applicationContext)
        }
        val language = runBlocking {
            settingsRepository.languageFlow.first()
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
            val uiState = settingsViewModel.uiState
            val isDarkTheme = uiState.isDarkTheme
            val isShakeEnabled = uiState.isShakeEnabled
            val currentLanguage = uiState.currentLanguage

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
                            isShakeEnabled = isShakeEnabled,
                            settingsViewModel = settingsViewModel,
                            onLanguageChange = { language ->
                                lifecycleScope.launch {
                                    settingsRepository.setLanguage(language)
                                    localizationManager.setLanguage(language)
                                    recreateActivity()
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
        isShakeEnabled: Boolean,
        settingsViewModel: SettingsViewModel,
        onLanguageChange: (AppLanguage) -> Unit
    ) {

        // 1. Inizializzazione db, repositories e viewModels
        // 2. Definizione navigazione
        // 3. Gestione shake detection

        val navController = rememberNavController()
        val context = LocalContext.current


        val db = remember { AppDatabase.getDatabase(context) }

        val gameHistoryRepository = remember {
            GameHistoryRepository(db.gameHistoryDao())
        }

        val gameSaveRepository = remember {
            GameSaveRepository(context)
        }

        val singlePlayerGameViewModel: SinglePlayerGameViewModel = viewModel(
            factory = SinglePlayerGameViewModelFactory(
                gameHistoryRepository = gameHistoryRepository,
                gameSaveRepository = gameSaveRepository
            )
        )

        val multiplayerGameViewModel: MultiplayerGameViewModel = viewModel(
            factory = MultiplayerGameViewModelFactory(gameSaveRepository)
        )

        val historyViewModel: HistoryViewModel = viewModel(
            factory = HistoryViewModelFactory(gameHistoryRepository)
        )

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

        DisposableEffect(isShakeEnabled, navController) {
            val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
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
            composable("history") {
                HistoryScreen(
                    navController = navController,
                    viewModel = historyViewModel
                )
            }
            composable("settings") {
                Settings(
                    navController = navController,
                    viewModel = settingsViewModel,
                    onLanguageChange = onLanguageChange
                )
            }
            composable("game_1vs1") {
                MultiplayerGameScreen(
                    navController = navController,
                    shakeTrigger = multiPlayerShakeTrigger,
                    viewModel = multiplayerGameViewModel
                )
            }
            composable("game") {
                SinglePlayerGameScreen(
                    navController = navController,
                    shakeTrigger = singlePlayerShakeTrigger,
                    viewModel = singlePlayerGameViewModel
                )
            }
        }
    }
}