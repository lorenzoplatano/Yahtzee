package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.localization.AppLanguage
import com.example.yahtzee.localization.LocalLocalizationManager
import com.example.yahtzee.ui.theme.SettingsTheme

// Stato iniziale delle impostazioni
private val initialSettings = SettingsState(
    language = AppLanguage.ITALIAN
)

data class SettingsState(
    val language: AppLanguage
)

@Composable
fun Settings(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
) {
    SettingsTheme(darkTheme = isDarkTheme) {
        SettingsContent(
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange,
            onHomeClick = { navController.navigate("homepage") },
            onLanguageChange = onLanguageChange
        )
    }
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onHomeClick: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val localizationManager = LocalLocalizationManager.current
    var settingsState by remember { 
        mutableStateOf(SettingsState(language = localizationManager.getCurrentLanguage())) 
    }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Icona Home
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.onSurface,
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
            ThemeSwitchRow(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
            SettingsRow(
                label = "${stringResource(id = R.string.language)}: ${settingsState.language.displayName}"
            ) {
                showLanguageDialog = true
            }
            SettingsRow(
                label = stringResource(id = R.string.rules)
            ) {
                showRulesDialog = true
            }
            SettingsRow(
                label = stringResource(id = R.string.reset_settings)
            ) {
                // Reset lingua e tema
                val defaultLanguage = initialSettings.language
                settingsState = initialSettings
                onThemeChange(false)
                if (defaultLanguage != localizationManager.getCurrentLanguage()) {
                    onLanguageChange(defaultLanguage)
                }
            }
        }

        if (showLanguageDialog) {
            LanguageDialog(
                currentLanguage = settingsState.language,
                onSelectLanguage = { lang ->
                    settingsState = settingsState.copy(language = lang)
                    // Informa l'attività principale del cambio lingua
                    onLanguageChange(lang)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        if (showRulesDialog) {
            RulesDialog(
                onDismiss = { showRulesDialog = false }
            )
        }
    }
}

@Composable
fun ThemeSwitchRow(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
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
        val themeText = if (isDarkTheme) 
            stringResource(id = R.string.dark_theme)
        else 
            stringResource(id = R.string.light_theme)
            
        Text(
            text = "${stringResource(id = R.string.theme)}: $themeText",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { checked -> onThemeChange(checked) }
        )
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
fun LanguageDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.select_language)) },
        text = {
            Column {
                Text(
                    AppLanguage.ITALIAN.displayName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLanguage(AppLanguage.ITALIAN)
                        }
                        .padding(8.dp),
                    fontWeight = if (currentLanguage == AppLanguage.ITALIAN) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    AppLanguage.ENGLISH.displayName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLanguage(AppLanguage.ENGLISH)
                        }
                        .padding(8.dp),
                    fontWeight = if (currentLanguage == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Normal
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
fun RulesDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(id = R.string.rules_title), 
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    stringResource(id = R.string.game_rules),
                    fontSize = 15.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.ok_button))
            }
        }
    )
}