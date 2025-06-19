package com.example.yahtzee.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.compositionLocalOf
import com.example.yahtzee.model.AppLanguage
import java.util.Locale



class LocalizationManager(private var currentLanguage: AppLanguage = AppLanguage.ITALIAN) {
    
    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

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