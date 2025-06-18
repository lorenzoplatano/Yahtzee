package com.example.yahtzee.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.localization.AppLanguage
import com.example.yahtzee.localization.LocalLocalizationManager
import com.example.yahtzee.screens.components.ModernGameButton
import com.example.yahtzee.ui.theme.SettingsTheme
import com.example.yahtzee.ui.theme.SettingsButtonGradient
import com.example.yahtzee.ui.theme.HomeDialogTitle
import com.example.yahtzee.ui.theme.HomeBackText

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
    val localizationManager = LocalLocalizationManager.current
    var settingsState by remember {
        mutableStateOf(SettingsState(language = localizationManager.getCurrentLanguage()))
    }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val isCompactScreen = screenHeight < 600.dp
    val buttonFontSize = if (isCompactScreen) 16.sp else 18.sp

    SettingsTheme(darkTheme = isDarkTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Sfondo
            Image(
                painter = painterResource(id = R.drawable.sfondo_generale),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay scuro
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))

            // Card contenente le impostazioni
            Card(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.displayMedium,
                        color = HomeDialogTitle,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    ModernGameButton(
                        text = stringResource(id = R.string.language) + ": ${settingsState.language.displayName}",
                        icon = Icons.Default.Person,
                        onClick = { showLanguageDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = buttonFontSize,
                        gradientColors = SettingsButtonGradient
                    )

                    ModernGameButton(
                        text = stringResource(id = R.string.theme),
                        icon = Icons.Default.Settings,
                        onClick = { onThemeChange(!isDarkTheme) },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = buttonFontSize,
                        gradientColors = SettingsButtonGradient
                    )

                    ModernGameButton(
                        text = stringResource(id = R.string.rules),
                        icon = Icons.Default.History,
                        onClick = { showRulesDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = buttonFontSize,
                        gradientColors = SettingsButtonGradient
                    )

                    ModernGameButton(
                        text = stringResource(id = R.string.reset_settings),
                        icon = Icons.Default.Settings,
                        onClick = {
                            settingsState = initialSettings
                            onThemeChange(false)
                            if (initialSettings.language != localizationManager.getCurrentLanguage()) {
                                onLanguageChange(initialSettings.language)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = buttonFontSize,
                        gradientColors = SettingsButtonGradient
                    )

                    TextButton(
                        onClick = { navController.navigate("homepage") }
                    ) {
                        Text(
                            text = stringResource(id = R.string.back),
                            style = MaterialTheme.typography.labelLarge,
                            color = HomeBackText
                        )
                    }
                }
            }

            if (showLanguageDialog) {
                LanguageDialog(
                    currentLanguage = settingsState.language,
                    onSelectLanguage = { lang ->
                        settingsState = settingsState.copy(language = lang)
                        onLanguageChange(lang)
                        showLanguageDialog = false
                    },
                    onDismiss = { showLanguageDialog = false }
                )
            }

            if (showRulesDialog) {
                RulesDialog(onDismiss = { showRulesDialog = false })
            }
        }
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
        title = {
            Text(
                stringResource(id = R.string.select_language),
                style = MaterialTheme.typography.headlineMedium,
                color = HomeDialogTitle
            )
        },
        text = {
            Column {
                Text(
                    AppLanguage.ITALIAN.displayName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.ITALIAN) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentLanguage == AppLanguage.ITALIAN) FontWeight.Bold else FontWeight.Normal,
                    color = HomeDialogTitle
                )
                Text(
                    AppLanguage.ENGLISH.displayName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.ENGLISH) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentLanguage == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Normal,
                    color = HomeDialogTitle
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
                style = MaterialTheme.typography.headlineMedium,
                color = HomeDialogTitle
            )
        },
        text = {
            Column {
                Text(
                    stringResource(id = R.string.game_rules),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(id = R.string.ok_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}