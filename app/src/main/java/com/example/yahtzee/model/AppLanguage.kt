package com.example.yahtzee.model

import java.util.Locale

// Enum per le lingue dell'app
enum class AppLanguage(val code: String) {
    ITALIAN("it"),
    ENGLISH("en");

    fun getLocalizedName(): String {
        return when (this) {
            ITALIAN -> if (Locale.getDefault().language == "en") "Italian" else "Italiano"
            ENGLISH -> if (Locale.getDefault().language == "it") "Inglese" else "English"
        }
    }
}