package com.example.yahtzee.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel
import com.example.yahtzee.ui.theme.MultiPlayerTheme
import com.example.yahtzee.ui.theme.SettingsTheme

@Composable
fun ModernGameActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6)  // Purple
    )
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(gradientColors)
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.3f))
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MultiplayerGameScreen(
    navController: NavController,
    viewModel: MultiplayerGameViewModel = viewModel()
) {
    val state = viewModel.state
    var showResetDialog by remember { mutableStateOf(false) }
    val previewScores = viewModel.previewScores()
    val allCombinations = viewModel.combinations

    MultiPlayerTheme(isPlayerOne = state.isPlayer1Turn) {
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Sei sicuro?") },
                text = { Text("Vuoi davvero ricominciare la partita? I progressi attuali andranno persi.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetGame()
                        showResetDialog = false
                    }) { Text("Sì") }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text("Annulla") }
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Immagine di sfondo - stessa della homepage
            Image(
                painter = painterResource(id = R.drawable.chunky),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay per migliorare la leggibilità
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            // Home icon con stile moderno
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 16.dp)
                    .size(48.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .zIndex(1f)
                    .clickable { navController.navigate("homepage") },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp, start = 8.dp, end = 8.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(72.dp))

                // Title and turn info con sfondo semi-trasparente
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "YAHTZEE - Multiplayer",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = when {
                                state.gameEnded -> "Partita Terminata"
                                state.isPlayer1Turn -> "Turno: Player 1"
                                else -> "Turno: Player 2"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (state.gameEnded) Color(0xFFE53E3E) else Color(0xFF4A5568),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dice row con sfondo moderno
                if (!state.gameEnded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            state.diceValues.forEachIndexed { index, value ->
                                Card(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .shadow(
                                            elevation = if (state.heldDice[index]) 8.dp else 4.dp,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = state.remainingRolls < 3 && !state.gameEnded) {
                                            viewModel.toggleHold(index)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = if (state.heldDice[index]) {
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
                                                    )
                                                } else {
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFFF7FAFC), Color(0xFFEDF2F7))
                                                    )
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                width = if (state.heldDice[index]) 2.dp else 1.dp,
                                                color = if (state.heldDice[index]) Color.White else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            value.toString(),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (state.heldDice[index]) Color.White else Color(0xFF2D3748)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabella punteggi con sfondo moderno
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Header della tabella con gradiente
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                    ),
                                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 16.dp)
                        ) {
                            Row {
                                Text(
                                    text = "COMBINATION",
                                    modifier = Modifier.weight(2f),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Player 1",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Player 2",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // Contenuto scrollabile della tabella
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp)
                        ) {
                            allCombinations.forEachIndexed { index, combination ->
                                if (index != 0) {
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }

                                val player1Score = state.scoreMapPlayer1[combination]
                                val player2Score = state.scoreMapPlayer2[combination]
                                val previewScore = previewScores[combination]

                                val isEnabled = !state.gameEnded &&
                                        state.hasRolledAtLeastOnce &&
                                        ((state.isPlayer1Turn && player1Score == null) || (!state.isPlayer1Turn && player2Score == null))

                                MultiplayerTableRowStyled(
                                    combination = combination,
                                    player1Score = player1Score,
                                    player2Score = player2Score,
                                    previewScore = previewScore,
                                    enabled = isEnabled,
                                    onClick = { if (isEnabled) viewModel.selectScore(combination) },
                                    bold = false,
                                    alternate = index % 2 == 1,
                                    isPlayer1Turn = state.isPlayer1Turn
                                )
                            }

                            // Calcolo bonus e totale
                            val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                            val upperSum1 = upper.mapNotNull { state.scoreMapPlayer1[it] }.sum()
                            val bonus1 = if (upperSum1 >= 63) 35 else 0
                            val totalScore1 =
                                state.scoreMapPlayer1.filterKeys { it != "Bonus" }.values.filterNotNull()
                                    .sum() + bonus1

                            val upperSum2 = upper.mapNotNull { state.scoreMapPlayer2[it] }.sum()
                            val bonus2 = if (upperSum2 >= 63) 35 else 0
                            val totalScore2 =
                                state.scoreMapPlayer2.filterKeys { it != "Bonus" }.values.filterNotNull()
                                    .sum() + bonus2

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 2.dp,
                                color = Color(0xFF667EEA)
                            )
                            MultiplayerTableRowStyled(
                                combination = "Bonus",
                                player1Score = bonus1,
                                player2Score = bonus2,
                                bold = true,
                                alternate = false,
                                isPlayer1Turn = state.isPlayer1Turn
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 2.dp,
                                color = Color(0xFF667EEA)
                            )
                            MultiplayerTableRowStyled(
                                combination = "Total",
                                player1Score = totalScore1,
                                player2Score = totalScore2,
                                bold = true,
                                alternate = false,
                                isPlayer1Turn = state.isPlayer1Turn
                            )
                        }
                    }
                }

                // Messaggio di fine partita
                if (state.gameEnded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Partita Terminata!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53E3E)
                            )

                            val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                            val upperSum1 = upper.mapNotNull { state.scoreMapPlayer1[it] }.sum()
                            val bonus1 = if (upperSum1 >= 63) 35 else 0
                            val totalScore1 =
                                state.scoreMapPlayer1.values.filterNotNull().sum() + bonus1

                            val upperSum2 = upper.mapNotNull { state.scoreMapPlayer2[it] }.sum()
                            val bonus2 = if (upperSum2 >= 63) 35 else 0
                            val totalScore2 =
                                state.scoreMapPlayer2.values.filterNotNull().sum() + bonus2

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Punteggio finale Player 1: $totalScore1",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )
                            Text(
                                text = "Punteggio finale Player 2: $totalScore2",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            ModernGameActionButton(
                                text = "Nuova Partita",
                                icon = Icons.Default.Refresh,
                                onClick = { showResetDialog = true },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                gradientColors = listOf(
                                    Color(0xFF4ECDC4),
                                    Color(0xFF44A08D)
                                )
                            )
                        }
                    }
                }
            }

            // Bottoni in basso con stile moderno
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 56.dp)
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModernGameActionButton(
                            text = "Roll (${state.remainingRolls})",
                            icon = androidx.compose.material.icons.Icons.Default.Refresh,
                            onClick = { viewModel.rollDice() },
                            enabled = state.remainingRolls > 0 && !state.gameEnded,
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44A08D)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ModernGameActionButton(
                            text = "Reset",
                            icon = Icons.Default.Refresh,
                            onClick = { showResetDialog = true },
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFFFF8E53)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MultiplayerTableRowStyled(
    combination: String,
    player1Score: Int?,
    player2Score: Int?,
    previewScore: Int? = null,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    header: Boolean = false,
    bold: Boolean = false,
    alternate: Boolean = false,
    isPlayer1Turn: Boolean = true
) {
    val backgroundColor = when {
        enabled -> Color(0xFFF0F9FF) // Light blue for enabled
        alternate -> Color(0xFFFAFAFA) // Very light gray
        else -> Color.Transparent
    }

    val textColor = when {
        enabled -> Color(0xFF1E40AF) // Blue for enabled
        bold -> Color(0xFF1A1A1A) // Dark for bold
        else -> Color(0xFF4A5568) // Gray for normal
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .then(
                if (enabled) {
                    Modifier
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(8.dp))
                } else Modifier
            )
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = combination,
                modifier = Modifier.weight(2f),
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 15.sp
            )
            Text(
                text = player1Score?.toString() ?: previewScore.takeIf { isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 15.sp
            )
            Text(
                text = player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 15.sp
            )
        }
    }
}