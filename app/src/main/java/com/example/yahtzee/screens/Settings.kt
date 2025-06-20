package com.example.yahtzee.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.model.AppLanguage
import com.example.yahtzee.viewmodel.SettingsViewModel
import com.example.yahtzee.screens.components.GenericButton
import com.example.yahtzee.ui.theme.SettingsButtonGradient


@Composable
fun Settings(
    navController: NavController,
    viewModel: SettingsViewModel,
    onLanguageChange: (AppLanguage) -> Unit
) {


    val uiState = viewModel.uiState
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isCompactScreen = screenHeight < 600.dp
    val buttonFontSize = if (isCompactScreen) 16.sp else 18.sp

    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.sfondo_generale),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background.copy(alpha = 0.3f))
        )

        Card(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .align(Alignment.Center)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.displayMedium,
                    color = colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                GenericButton(
                    text = stringResource(id = R.string.language) + ": " + uiState.currentLanguage.getLocalizedName(),
                    icon = Icons.Default.Person,
                    onClick = { showLanguageDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = buttonFontSize,
                    gradientColors = SettingsButtonGradient
                )

                GenericButton(
                    text = if (uiState.isDarkTheme)
                        stringResource(id = R.string.dark_theme)
                    else
                        stringResource(id = R.string.light_theme),
                    icon = if (uiState.isDarkTheme)
                        Icons.Default.Brightness3
                    else
                        Icons.Default.Brightness7,
                    onClick = { viewModel.toggleTheme() },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = buttonFontSize,
                    gradientColors = SettingsButtonGradient
                )

                GenericButton(
                    text = if (uiState.isShakeEnabled)
                        stringResource(id = R.string.shake_enabled)
                    else
                        stringResource(id = R.string.shake_disabled),
                    icon = Icons.Default.Vibration,
                    onClick = { viewModel.toggleShake() },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = buttonFontSize,
                    gradientColors = SettingsButtonGradient
                )

                GenericButton(
                    text = stringResource(id = R.string.rules),
                    icon = Icons.Default.Book,
                    onClick = { showRulesDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = buttonFontSize,
                    gradientColors = SettingsButtonGradient
                )

                TextButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text(
                        text = stringResource(id = R.string.back),
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        if (showLanguageDialog) {
            LanguageDialog(
                currentLanguage = uiState.currentLanguage,
                onSelectLanguage = { lang ->
                    viewModel.setLanguage(lang)
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

@Composable
fun LanguageDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        title = {
            Text(
                stringResource(id = R.string.select_language),
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    AppLanguage.ITALIAN.getLocalizedName(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.ITALIAN) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentLanguage == AppLanguage.ITALIAN) FontWeight.Bold else FontWeight.Normal,
                    color = colorScheme.onSurface
                )
                Text(
                    AppLanguage.ENGLISH.getLocalizedName(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(AppLanguage.ENGLISH) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (currentLanguage == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Normal,
                    color = colorScheme.onSurface
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
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        title = {
            Text(
                stringResource(id = R.string.rules_title),
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    stringResource(id = R.string.game_rules),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(id = R.string.ok_button),
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.onSurface
                )
            }
        }
    )
}