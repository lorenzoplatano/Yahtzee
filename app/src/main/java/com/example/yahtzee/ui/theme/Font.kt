/*
 * File: Font.kt
 * Qui definisci i tuoi font custom e la Typography centralizzata.
 */

package com.example.yahtzee.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yahtzee.R


// 1. Definisci il tuo FontFamily custom (sostituisci i font con quelli che hai in res/font)
val YahtzeeFontFamily = FontFamily.Default
// 2. Typography centralizzata (Material3: usa il costruttore di default e copia solo quello che ti serve)
val Typography = Typography().copy(
    displayLarge = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = YahtzeeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
    // Puoi aggiungere altri stili se ti servono
)