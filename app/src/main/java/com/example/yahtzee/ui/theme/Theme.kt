
package com.example.yahtzee.ui.theme

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.yahtzee.R
import androidx.compose.ui.text.font.FontFamily

val HomeButtonGradient = listOf(HomeButtonStart, HomeButtonEnd)
val SinglePlayerGradient = listOf(SinglePlayerStart, SinglePlayerEnd)
val MultiPlayerGradient = listOf(MultiPlayerStart, MultiPlayerEnd)
val SettingsGradient = listOf(SettingsStart, SettingsEnd)
val HistoryGradient = listOf(HistoryStart, HistoryEnd)
val SettingsButtonStart = Color(0xFF43CEA2)
val SettingsButtonEnd = Color(0xFF185A9D)
val SettingsButtonGradient = listOf(SettingsButtonStart, SettingsButtonEnd)

val BothCardLight = Color.White.copy(alpha = 0.95f)
val BothCardDark = Color(0xFF23272E).copy(alpha = 0.92f)
val TableLight = Color.White.copy(alpha = 0.96f)
val TableDark = Color(0xFF23272E).copy(alpha = 0.96f)

fun yahtzeeCardColor(isDarkTheme: Boolean): Color = if (isDarkTheme) BothCardDark else BothCardLight
fun yahtzeeTableColor(isDarkTheme: Boolean): Color = if (isDarkTheme) TableDark else TableLight


@Composable
fun yahtzeeMainTextColor(isDarkTheme: Boolean): Color = mainTextColor(isDarkTheme)


fun yahtzeeDividerColor(isDarkTheme: Boolean): Color = if (isDarkTheme) Color(0xFF353A40) else Color(0xFFE2E8F0)
val yahtzeeFontFamily: FontFamily get() = YahtzeeFontFamily


@Composable
fun mainTextColor(darkTheme: Boolean): Color {
    return if (darkTheme) Color.White else HomeDialogTitle
}



private val DarkColorScheme = darkColorScheme(
    primary = Blue200,
    onPrimary = Blue900,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue50,
    secondary = Blue400,
    onSecondary = Blue900,
    background = Blue900,
    onBackground = Color.White,
    surface = Blue800,
    onSurface = Color.White,
)

private val BlueColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Blue700,
    onSecondary = Color.White,
    background = Blue50,
    onBackground = Blue900,
    surface = Blue100,
    onSurface = Blue900,
)

private val RedColorScheme = lightColorScheme(
    primary = Red500,
    onPrimary = Color.White,
    primaryContainer = Red100,
    onPrimaryContainer = Red900,
    secondary = Red700,
    onSecondary = Color.White,
    background = Red50,
    onBackground = Red900,
    surface = Red100,
    onSurface = Red900,
)

//  Gradient background composable serve a creare un box che riempie lo schermo e applica uno sfondo


@Composable
fun DarkGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Blue900, Blue800, Blue700)
                )
            )
    ) {
        content()
    }
}

@Composable
fun BlueGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Blue100, Blue400, Blue700)
                )
            )
    ) {
        content()
    }
}


@Composable
fun SinglePlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MultiPlayerTheme(
    isPlayerOne: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isPlayerOne) BlueColorScheme else RedColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun YahtzeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // <-- CORRETTO QUI!
        else -> BlueColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


