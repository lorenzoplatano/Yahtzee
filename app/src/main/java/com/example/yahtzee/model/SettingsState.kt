package com.example.yahtzee.model



data class SettingsState(
    val isDarkTheme: Boolean = false,
    val isShakeEnabled: Boolean = true,
    val currentLanguage: AppLanguage = AppLanguage.ITALIAN,
    val isLoading: Boolean = true
)