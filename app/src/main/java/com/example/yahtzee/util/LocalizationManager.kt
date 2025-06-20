package com.example.yahtzee.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.compositionLocalOf
import com.example.yahtzee.model.AppLanguage
import java.util.Locale


// Classe per gestire la localizzazione dell'applicazione
class LocalizationManager(private var currentLanguage: AppLanguage = AppLanguage.ITALIAN) {

    // Imposta la lingua corrente
    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    // Restituisce la locale corrispondente alla lingua corrente
    fun getLocale(): Locale {
        return when (currentLanguage) {
            AppLanguage.ITALIAN -> Locale("it", "IT")
            AppLanguage.ENGLISH -> Locale("en", "US")
        }
    }

    // Applica la lingua corrente al contesto dell'applicazione
    fun applyLanguage(context: Context): Context {
        val locale = getLocale()
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
}


val LocalLocalizationManager = compositionLocalOf { LocalizationManager() }