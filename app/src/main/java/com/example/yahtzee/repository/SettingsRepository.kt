package com.example.yahtzee.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.yahtzee.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

// Repository per gestire le impostazioni dell'applicazione
class SettingsRepository(private val context: Context) {

    companion object {

        private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        private val IS_SHAKE_ENABLED = booleanPreferencesKey("is_shake_enabled")
        private val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    val isDarkThemeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_DARK_THEME] ?: false }

    val isShakeEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_SHAKE_ENABLED] ?: true }

    val languageFlow: Flow<AppLanguage> = context.dataStore.data
        .map { preferences ->
            val code = preferences[LANGUAGE_CODE] ?: AppLanguage.ITALIAN.code
            AppLanguage.values().find { it.code == code } ?: AppLanguage.ITALIAN
        }


    // Funzioni per modificare le impostazioni

    // Imposta il tema
    suspend fun setDarkTheme(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDarkTheme
        }
    }

    // Abilita o disabilita il shake per lanciare i dadi
    suspend fun setShakeEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SHAKE_ENABLED] = isEnabled
        }
    }

    // Imposta la lingua dell'app
    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE] = language.code
        }
    }
}