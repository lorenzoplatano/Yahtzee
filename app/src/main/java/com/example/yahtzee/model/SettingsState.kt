package com.example.yahtzee.model


// Model per le impostazioni dell'app
data class SettingsState(
    val isDarkTheme: Boolean = false,
    val isShakeEnabled: Boolean = true,
    val currentLanguage: AppLanguage = AppLanguage.ITALIAN,
    val isLoading: Boolean = true
)