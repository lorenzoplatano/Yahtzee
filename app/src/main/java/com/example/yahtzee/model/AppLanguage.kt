package com.example.yahtzee.model

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