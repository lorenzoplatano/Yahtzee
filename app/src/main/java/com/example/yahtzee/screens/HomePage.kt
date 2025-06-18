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
import androidx.compose.ui.graphics.Color
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
import com.example.yahtzee.screens.components.ModernGameButton
import com.example.yahtzee.ui.theme.*
import kotlin.times

@Composable
fun Homepage(navController: NavController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val isCompactScreen = screenHeight < 600.dp
    val buttonWidth = (screenWidth * 0.8f).coerceAtMost(400.dp)
    val buttonTextSize = if (isCompactScreen) 16.sp else 18.sp

    val cardBackground = if (isDarkTheme) CardDark else CardLight
    val titleColor = mainTextColor(isDarkTheme)

    BackHandler {
        showExitDialog = true
    }

    HomeTheme(darkTheme = isDarkTheme) {}

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    stringResource(R.string.dialog_home_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = titleColor
                )
            },
            text = {
                Text(
                    stringResource(R.string.dialog_home_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = titleColor
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
                        color = titleColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(
                        stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = titleColor
                    )
                }
            }
        )
    }
    val backgroundRes = if (!showModeDialog) R.drawable.chunky else R.drawable.sfondo_generale

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
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!showModeDialog) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                        .offset(y = (screenHeight * 0.20f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ModernGameButton(
                        text = stringResource(R.string.touch_to_continue),
                        icon = Icons.Default.PlayArrow,
                        onClick = { showModeDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = buttonTextSize,
                        gradientColors = HomeButtonGradient
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .widthIn(max = 450.dp)
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
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
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )

                        ModernGameButton(
                            text = stringResource(R.string.singleplayer),
                            icon = Icons.Default.Person,
                            onClick = { navController.navigate("game") },
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = buttonTextSize,
                            gradientColors = SinglePlayerGradient
                        )

                        ModernGameButton(
                            text = stringResource(R.string.multiplayer),
                            icon = Icons.Default.Groups,
                            onClick = { navController.navigate("game_1vs1") },
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = buttonTextSize,
                            gradientColors = MultiPlayerGradient
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            ModernGameButton(
                                text = stringResource(R.string.settings_title),
                                icon = Icons.Default.Settings,
                                onClick = { navController.navigate("settings") },
                                modifier = Modifier.size(48.dp),
                                showIconOnly = true,
                                gradientColors = SettingsGradient
                            )

                            ModernGameButton(
                                text = stringResource(R.string.history),
                                icon = Icons.Default.History,
                                onClick = { navController.navigate("history") },
                                modifier = Modifier.size(48.dp),
                                showIconOnly = true,
                                gradientColors = HistoryGradient
                            )
                        }

                        TextButton(
                            onClick = { showModeDialog = false },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                stringResource(R.string.back),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isDarkTheme) CardLight else CardDark
                            )
                        }
                    }
                }
            }
        }
    }
}