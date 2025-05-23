
package com.example.yahtzee.ui.theme

import android.os.Build
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
import androidx.compose.ui.platform.LocalContext



private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

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

// --- Gradient background composable ---

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

// --- Temi specifici per schermata ---

@Composable
fun Game1v1Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SinglePlayerTheme(
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
fun SettingsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueColorScheme,
        typography = Typography
    ) {
        BlueGradientBackground {
            content()
        }
    }
}

@Composable
fun HomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueColorScheme,
        typography = Typography
    ) {
        BlueGradientBackground {
            content()
        }
    }
}

@Composable
fun HistoryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueColorScheme,
        typography = Typography
    ) {
        BlueGradientBackground {
            content()
        }
    }
}

// --- Tema generale (fallback, per retrocompatibilità) ---

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
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}