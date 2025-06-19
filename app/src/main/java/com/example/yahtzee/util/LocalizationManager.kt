package com.example.yahtzee.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.compositionLocalOf
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String) {
    ITALIAN("it", "Italiano"),
    ENGLISH("en", "English");

    fun getLocalizedName(): String {
        return when (this) {
            ITALIAN -> if (Locale.getDefault().language == "en") "Italian" else "Italiano"
            ENGLISH -> if (Locale.getDefault().language == "it") "Inglese" else "English"
        }
    }
}

class LocalizationManager(private var currentLanguage: AppLanguage = AppLanguage.ITALIAN) {
    
    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }
    
    fun getCurrentLanguage(): AppLanguage = currentLanguage
    
    fun getLocale(): Locale {
        return when (currentLanguage) {
            AppLanguage.ITALIAN -> Locale("it", "IT")
            AppLanguage.ENGLISH -> Locale("en", "US")
        }
    }
    
    fun applyLanguage(context: Context): Context {
        val locale = getLocale()
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
}

// CompositionLocal per accedere al LocalizationManager nell'applicazione
val LocalLocalizationManager = compositionLocalOf { LocalizationManager() }