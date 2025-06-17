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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.ui.theme.HomeTheme

@Composable
fun ModernGameButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6)  // Purple
    )
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Homepage(navController: NavController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    HomeTheme(darkTheme = isDarkTheme) {}

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Vuoi uscire?") },
            text = { Text("Sei sicuro di voler chiudere l'app?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text("Esci")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Immagine di sfondo
        Image(
            painter = painterResource(id = R.drawable.chunky),
            // R.drawable.background_game
            // R.drawable.home_background
            // R.drawable.game_bg
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.62f)) // 60% dello spazio sopra - bottone leggermente sotto la metà

            if (!showModeDialog) {
                ModernGameButton(
                    text = "PLAY",
                    icon = Icons.Default.PlayArrow,
                    onClick = { showModeDialog = true },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    gradientColors = listOf(
                        Color(0xFF667EEA), // Blue gradient
                        Color(0xFF764BA2)  // Purple gradient
                    )
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsanti di gioco esistenti
                    ModernGameButton(
                        text = "SINGLEPLAYER",
                        icon = Icons.Default.Person,
                        onClick = { navController.navigate("game") },
                        modifier = Modifier.fillMaxWidth(),
                        gradientColors = listOf(
                            Color(0xFF4ECDC4), // Teal gradient
                            Color(0xFF44A08D)  // Green gradient
                        )
                    )

                    ModernGameButton(
                        text = "MULTIPLAYER",
                        icon = Icons.Default.Groups,
                        onClick = { navController.navigate("game_1vs1") },
                        modifier = Modifier.fillMaxWidth(),
                        gradientColors = listOf(
                            Color(0xFFFF6B6B), // Red gradient
                            Color(0xFFFF8E53)  // Orange gradient
                        )
                    )
                    // Spazio tra i pulsanti di gioco e quelli di navigazione
                    Spacer(modifier = Modifier.height(8.dp))

                    // Nuovi pulsanti di navigazione
                    ModernGameButton(
                        text = "IMPOSTAZIONI",
                        icon = Icons.Default.Settings,
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.fillMaxWidth(),
                        gradientColors = listOf(
                            Color(0xFF9C27B0), // Purple gradient
                            Color(0xFF673AB7)  // Deep Purple gradient
                        )
                    )

                    ModernGameButton(
                        text = "STORICO",
                        icon = Icons.Default.History,
                        onClick = { navController.navigate("history") },
                        modifier = Modifier.fillMaxWidth(),
                        gradientColors = listOf(
                            Color(0xFFFF9800), // Orange gradient
                            Color(0xFFFF5722)  // Deep Orange gradient
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))
        }

    }
}