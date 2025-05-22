package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.ui.theme.YahtzeeTheme

// Stato iniziale delle impostazioni
private val initialSettings = SettingsState(
    notificationsEnabled = true,
    language = "Italiano"
)

data class SettingsState(
    val notificationsEnabled: Boolean,
    val language: String
)

@Composable
fun Settings(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    SettingsContent(
        isDarkTheme = isDarkTheme,
        onThemeChange = onThemeChange,
        onHomeClick = { navController.navigate("homepage") }
    )
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onHomeClick: () -> Unit
) {
    // Stato delle impostazioni (eccetto il tema, che ora è gestito globalmente)
    var settingsState by remember { mutableStateOf(initialSettings) }
    // Stato dei dialog
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Icona Home
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(32.dp)
                .clickable { onHomeClick() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsRow(
                label = "Tema: ${if (isDarkTheme) "Scuro" else "Chiaro"}"
            ) {
                showThemeDialog = true
            }
            SettingsRow(
                label = "Lingua: ${settingsState.language}"
            ) {
                showLanguageDialog = true
            }
            // Notifiche: switch abilitazione/disabilitazione
            NotificationRow(
                enabled = settingsState.notificationsEnabled,
                onToggle = {
                    settingsState = settingsState.copy(notificationsEnabled = !settingsState.notificationsEnabled)
                }
            )
            // Reset impostazioni
            SettingsRow(
                label = "Reset Impostazioni"
            ) {
                settingsState = initialSettings
                onThemeChange(false) // Reset anche il tema a chiaro
            }
        }

        if (showThemeDialog) {
            ThemeDialog(
                currentThemeDark = isDarkTheme,
                onSelectTheme = { isDark ->
                    onThemeChange(isDark) // Cambia il tema globale!
                    showThemeDialog = false
                },
                onDismiss = { showThemeDialog = false }
            )
        }

        if (showLanguageDialog) {
            LanguageDialog(
                currentLanguage = settingsState.language,
                onSelectLanguage = { lang ->
                    settingsState = settingsState.copy(language = lang)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

@Composable
fun SettingsRow(label: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun NotificationRow(enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notifiche",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = enabled,
            onCheckedChange = { onToggle() }
        )
    }
}

@Composable
fun ThemeDialog(
    currentThemeDark: Boolean,
    onSelectTheme: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona Tema") },
        text = {
            Column {
                Text(
                    "Chiaro",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectTheme(false)
                        }
                        .padding(8.dp),
                    fontWeight = if (!currentThemeDark) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    "Scuro",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectTheme(true)
                        }
                        .padding(8.dp),
                    fontWeight = if (currentThemeDark) FontWeight.Bold else FontWeight.Normal
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
fun LanguageDialog(
    currentLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona Lingua") },
        text = {
            Column {
                Text(
                    "Italiano",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLanguage("Italiano")
                        }
                        .padding(8.dp),
                    fontWeight = if (currentLanguage == "Italiano") FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    "Inglese",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLanguage("Inglese")
                        }
                        .padding(8.dp),
                    fontWeight = if (currentLanguage == "Inglese") FontWeight.Bold else FontWeight.Normal
                )
            }
        },
        confirmButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    YahtzeeTheme {
        SettingsContent(
            isDarkTheme = false,
            onThemeChange = {},
            onHomeClick = {}
        )
    }
}
