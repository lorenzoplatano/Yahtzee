/*
 * File: Font.kt
 * Qui definisci i tuoi font custom e la Typography centralizzata.
 */

package com.example.yahtzee.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



val YahtzeeFontFamily = FontFamily.Default

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

)