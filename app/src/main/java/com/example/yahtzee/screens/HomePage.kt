package com.example.yahtzee.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.screens.components.GenericButton
import com.example.yahtzee.ui.theme.*

// Composable per l'homepage dell'app
@Composable
fun Homepage(
    navController: NavController,
    showModeSelection: Boolean = false,
    onModeSelectionChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val isCompactScreen = screenHeight < 600.dp
    val buttonTextSize = if (isCompactScreen) 16.sp else 18.sp

    val colorScheme = MaterialTheme.colorScheme

    val backgroundRes = if (!showModeSelection) R.drawable.chunky else R.drawable.sfondo_generale

    BackHandler {
        if (showModeSelection) {

            onModeSelectionChanged(false)
        } else {

            showExitDialog = true
        }
    }

    // Dialog per confermare l'uscita dall'app
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    stringResource(R.string.dialog_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onSurface
                )
            },
            text = {
                Text(
                    stringResource(R.string.dialog_end_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text(
                        stringResource(R.string.confirm),
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(
                        stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurface
                    )
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!showModeSelection) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                        .offset(y = (screenHeight * 0.20f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Bottone per selezionare la modalità di gioco
                    GenericButton(
                        text = stringResource(R.string.touch_to_continue),
                        icon = Icons.Default.PlayArrow,
                        onClick = { onModeSelectionChanged(true) },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = buttonTextSize,
                        gradientColors = HomeButtonGradient
                    )
                }
            } else {

                // Card che mostra le opzioni di gioco
                Card(
                    modifier = Modifier
                        .widthIn(max = 450.dp)
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isCompactScreen) 8.dp else 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.select_mode),
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        // Bottone per selezionare la modalità di gioco singolo
                        GenericButton(
                            text = stringResource(R.string.singleplayer),
                            icon = Icons.Default.Person,
                            onClick = {
                                navController.navigate("game")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = buttonTextSize,
                            gradientColors = SinglePlayerGradient
                        )

                        // Bottone per selezionare la modalità di gioco multiplayer
                        GenericButton(
                            text = stringResource(R.string.multiplayer),
                            icon = Icons.Default.Groups,
                            onClick = {
                                navController.navigate("game_1vs1")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = buttonTextSize,
                            gradientColors = MultiPlayerGradient
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {

                            // Bottone per accedere alle impostazioni
                            GenericButton(
                                text = stringResource(R.string.settings_title),
                                icon = Icons.Default.Settings,
                                onClick = { navController.navigate("settings") },
                                modifier = Modifier.size(48.dp),
                                showIconOnly = true,
                                gradientColors = SettingsGradient
                            )

                            // Bottone per accedere alla cronologia delle partite
                            GenericButton(
                                text = stringResource(R.string.history),
                                icon = Icons.Default.History,
                                onClick = { navController.navigate("history") },
                                modifier = Modifier.size(48.dp),
                                showIconOnly = true,
                                gradientColors = HistoryGradient
                            )
                        }

                        // Bottone per tornare indietro alla schermata principale
                        TextButton(
                            onClick = { onModeSelectionChanged(false) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                stringResource(R.string.back),
                                style = MaterialTheme.typography.labelLarge,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}