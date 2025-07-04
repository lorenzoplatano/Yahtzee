package com.example.yahtzee.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definizione degli schemi di colori usando i colori da Colors.kt

// Schema di colori scuro
private val DarkColorScheme = darkColorScheme(
    primary = BothCardDark,                // colore principale scuro
    onPrimary = Color.White,               // testo su primary
    primaryContainer = TableDark,          // contenitore principale scuro
    onPrimaryContainer = Color.White,      // testo su contenitore principale
    secondary = DividerDark,               // colore secondario scuro
    onSecondary = Color.White,             // testo su secondario
    background = TableDark,                // colore di sfondo scuro
    onBackground = Color.White,            // testo su sfondo
    surface = BothCardDark,                // superficie scura (es. card)
    onSurface = Color.White,               // testo su superficie
)

// Schema di colori chiaro
private val LightColorScheme = lightColorScheme(
    primary = BothCardLight,               // colore principale chiaro
    onPrimary = HomeDialogTitle,           // testo su primary
    primaryContainer = TableLight,         // contenitore principale chiaro
    onPrimaryContainer = HomeDialogTitle,  // testo su contenitore principale
    secondary = DividerLight,              // colore secondario chiaro
    onSecondary = HomeDialogTitle,         // testo su secondario
    background = TableLight,               // colore di sfondo chiaro
    onBackground = HomeDialogTitle,        // testo su sfondo
    surface = BothCardLight,               // superficie chiara (es. card)
    onSurface = HomeDialogTitle,           // testo su superficie
)

// Tema per l'app Yahtzee, imposta lo schema di colori
@Composable
fun YahtzeeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}