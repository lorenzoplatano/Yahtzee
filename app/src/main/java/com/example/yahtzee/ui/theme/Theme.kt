
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


val HomeButtonGradient = listOf(HomeButtonStart, HomeButtonEnd)
val SinglePlayerGradient = listOf(SinglePlayerStart, SinglePlayerEnd)
val MultiPlayerGradient = listOf(MultiPlayerStart, MultiPlayerEnd)
val SettingsGradient = listOf(SettingsStart, SettingsEnd)
val HistoryGradient = listOf(HistoryStart, HistoryEnd)
val SettingsButtonStart = Color(0xFF43CEA2)
val SettingsButtonEnd = Color(0xFF185A9D)
val SettingsButtonGradient = listOf(SettingsButtonStart, SettingsButtonEnd)




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
fun RedGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Red100, Red500, Red700)
                )
            )
    ) {
        content()
    }
}

// Temi specifici per schermata

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
fun SettingsTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else BlueColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        if (darkTheme) {
            DarkGradientBackground {
                content()
            }
        } else {
            BlueGradientBackground {
                content()
            }
        }
    }
}


@Composable
fun HomeTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else BlueColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        if (darkTheme) {
            DarkGradientBackground {
                content()
            }
        } else {
            BlueGradientBackground {
                content()
            }
        }
    }
}


@Composable
fun HistoryTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else BlueColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        if (darkTheme) {
            DarkGradientBackground {
                content()
            }
        } else {
            BlueGradientBackground {
                content()
            }
        }
    }
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
