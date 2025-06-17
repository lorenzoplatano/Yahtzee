package com.example.yahtzee.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.screens.components.ModernGameButton
import com.example.yahtzee.ui.theme.HomeTheme
import kotlin.times



@Composable
fun Homepage(navController: NavController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    // Ottieni le dimensioni dello schermo per layout responsivi
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Calcola dimensioni responsive
    val isCompactScreen = screenHeight < 600.dp
    val buttonWidth = (screenWidth * 0.8f).coerceAtMost(400.dp)
    val buttonTextSize = if (isCompactScreen) 16.sp else 18.sp

    BackHandler {
        showExitDialog = true
    }

    HomeTheme(darkTheme = isDarkTheme) {}

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.dialog_home_title)) },
            text = { Text(stringResource(R.string.dialog_home_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Immagine di sfondo
        Image(
            painter = painterResource(id = R.drawable.chunky),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        // Overlay scuro per migliorare leggibilità
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Contenitore principale responsivo
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!showModeDialog) {
                // Visualizzazione pulsante iniziale a 2/3 dello schermo
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
                        gradientColors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                }
            } else {
                // Visualizzazione scelta modalità di gioco
                Card(
                    modifier = Modifier
                        .widthIn(max = 450.dp)
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
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
                            fontSize = buttonTextSize.times(1.1f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = Color(0xFF1A1A1A),
                        )

                        ModernGameButton(
                            text = stringResource(R.string.singleplayer),
                            icon = Icons.Default.Person,
                            onClick = { navController.navigate("game") },
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = buttonTextSize,
                            gradientColors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44A08D)
                            )
                        )

                        ModernGameButton(
                            text = stringResource(R.string.multiplayer),
                            icon = Icons.Default.Groups,
                            onClick = { navController.navigate("game_1vs1") },
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = buttonTextSize,
                            gradientColors = listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFFFF8E53)
                            )
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
                                gradientColors = listOf(
                                    Color(0xFF9C27B0),
                                    Color(0xFF673AB7)
                                )
                            )

                            ModernGameButton(
                                text = stringResource(R.string.history),
                                icon = Icons.Default.History,
                                onClick = { navController.navigate("history") },
                                modifier = Modifier.size(48.dp),
                                showIconOnly = true,
                                gradientColors = listOf(
                                    Color(0xFFFF9800),
                                    Color(0xFFFF5722)
                                )
                            )
                        }

                        TextButton(
                            onClick = { showModeDialog = false },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(stringResource(R.string.back), color = Color(0xFF764BA2))
                        }
                    }
                }
            }
        }
    }
}