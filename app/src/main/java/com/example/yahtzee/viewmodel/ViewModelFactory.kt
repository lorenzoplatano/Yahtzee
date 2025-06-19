package com.example.yahtzee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yahtzee.service.GameService
import com.example.yahtzee.repository.GameHistoryRepository
import com.example.yahtzee.repository.SettingsRepository

/**
 * Factory per HistoryViewModel.
 * Inietta GameHistoryRepository come dipendenza.
 */
class HistoryViewModelFactory(
    private val gameHistoryRepository: GameHistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(gameHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Factory per SinglePlayerGameViewModel.
 * Inietta GameHistoryRepository come dipendenza.
 */
class SinglePlayerGameViewModelFactory(
    private val gameHistoryRepository: GameHistoryRepository,
    private val gameService: GameService = GameService()  // ✅ Aggiungi GameService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SinglePlayerGameViewModel::class.java)) {
            return SinglePlayerGameViewModel(gameHistoryRepository, gameService) as T  // ✅ Passa entrambi
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Factory per SettingsViewModel.
 * Inietta SettingsRepository come dipendenza.
 */
class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class MultiplayerGameViewModelFactory(
    private val gameService: GameService = GameService()  // ✅ Aggiungi GameService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiplayerGameViewModel::class.java)) {
            return MultiplayerGameViewModel(gameService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}