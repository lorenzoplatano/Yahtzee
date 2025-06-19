package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.model.AppLanguage
import com.example.yahtzee.repository.SettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.example.yahtzee.model.SettingsState

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var uiState by mutableStateOf(SettingsState())
        private set

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkThemeFlow,
                settingsRepository.isShakeEnabledFlow,
                settingsRepository.languageFlow
            ) { isDarkTheme, isShakeEnabled, language ->
                SettingsState(
                    isDarkTheme = isDarkTheme,
                    isShakeEnabled = isShakeEnabled,
                    currentLanguage = language,
                    isLoading = false
                )
            }.collect { newState ->
                uiState = newState
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(!uiState.isDarkTheme)
        }
    }

    fun toggleShake() {
        viewModelScope.launch {
            settingsRepository.setShakeEnabled(!uiState.isShakeEnabled)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }
}