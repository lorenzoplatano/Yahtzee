package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel
import com.example.yahtzee.ui.theme.MultiPlayerTheme
import com.example.yahtzee.ui.theme.SettingsTheme

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

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Home icon
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 32.dp, end = 16.dp)
                        .size(32.dp)
                        .zIndex(1f)
                        .clickable { navController.navigate("homepage") }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 0.dp, start = 8.dp, end = 8.dp, bottom = 96.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(72.dp))

                    // Title and turn info
                    Text(
                        text = "YAHTZEE - Multiplayer",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = when {
                            state.gameEnded -> "Partita Terminata"
                            state.isPlayer1Turn -> "Turno: Player 1"
                            else -> "Turno: Player 2"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (state.gameEnded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Dice row
                    if (!state.gameEnded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp, top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            state.diceValues.forEachIndexed { index, value ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            if (state.heldDice[index]) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = if (state.heldDice[index]) 3.dp else 2.dp,
                                            color = if (state.heldDice[index]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = state.remainingRolls < 3 && !state.gameEnded) {
                                            viewModel.toggleHold(index)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        value.toString(),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.heldDice[index]) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(0.dp))

                    // Header sticky della tabella
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "COMBINATION",
                            modifier = Modifier.weight(2f),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Player 1",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Player 2",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp
                        )
                    }

                    // Tabella punteggi scrollabile
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            )
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        allCombinations.forEachIndexed { index, combination ->
                            if (index != 0) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
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

                        // Calcolo bonus e totale per entrambi i giocatori
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
                            modifier = Modifier.padding(vertical = 2.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
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
                            modifier = Modifier.padding(vertical = 2.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
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

                    // Messaggio di fine partita e nuovo gioco
                    if (state.gameEnded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Partita Terminata!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
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

                        Text(
                            text = "Punteggio finale Player 1: $totalScore1",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Punteggio finale Player 2: $totalScore2",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Button(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .height(48.dp)
                                .fillMaxWidth(0.7f)
                        ) {
                            Text("Nuova Partita", fontSize = 18.sp)
                        }
                    }
                }

                // Bottoni in basso
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 56.dp)
                        .height(56.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.rollDice() },
                        enabled = state.remainingRolls > 0 && !state.gameEnded,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .height(48.dp)
                    ) {
                        Text(
                            "Roll (${state.remainingRolls})",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 18.sp
                        )
                    }
                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .height(48.dp)
                    ) {
                        Text(
                            "Reset",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 18.sp
                        )
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
        header -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.primaryContainer
        alternate -> MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        header -> MaterialTheme.colorScheme.onPrimary
        enabled -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (header)
                    Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(backgroundColor)
                else Modifier.background(backgroundColor)
            )
            .then(
                if (enabled) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)) else Modifier
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 10.dp, vertical = if (header) 12.dp else 14.dp)
    ) {
        Text(
            text = combination,
            modifier = Modifier.weight(2f),
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 18.sp else 18.sp
        )
        Text(
            text = player1Score?.toString() ?: previewScore.takeIf { isPlayer1Turn }?.toString() ?: if (header) "Player 1" else "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 18.sp else 18.sp
        )
        Text(
            text = player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }?.toString() ?: if (header) "Player 2" else "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 18.sp else 18.sp
        )
    }
}