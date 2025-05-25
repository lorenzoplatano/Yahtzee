
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel
import com.example.yahtzee.ui.theme.MultiPlayerTheme // Per il tema dinamico blu/rosso

@Composable
fun MultiplayerGameScreen(
    navController: NavController,
    viewModel: MultiplayerGameViewModel = viewModel()
) {
    val state = viewModel.state
    var showResetDialog by remember { mutableStateOf(false) }
    val previewScores = viewModel.previewScores()

    // qui viene eseguito lo switch tra giocatore 1 (il blu) e giocatore 2 (il rosso)
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
            modifier = Modifier
                .fillMaxSize()

        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .size(32.dp)
                    .clickable { navController.navigate("homepage") }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 96.dp, start = 16.dp, end = 16.dp, bottom = 90.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YAHTZEE - Multiplayer",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = when {
                        state.gameEnded -> "Partita Terminata"
                        state.isPlayer1Turn -> "Turno: Player 1"
                        else -> "Turno: Player 2"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (state.gameEnded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    state.diceValues.forEachIndexed { index, value ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    if (state.heldDice[index]) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                                .clickable(enabled = state.remainingRolls < 3 && !state.gameEnded) {
                                    viewModel.toggleHold(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = value.toString(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.heldDice[index]) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))

                ) {
                    MultiplayerTableRow(
                        "COMBINATION", "Player 1", "Player 2",
                        header = true,
                        isPlayer1Turn = state.isPlayer1Turn
                    )

                    viewModel.combinations.forEachIndexed { index, combination ->
                        if (index != 0) {
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)
                        }

                        val player1Score = state.scoreMapPlayer1[combination]
                        val player2Score = state.scoreMapPlayer2[combination]
                        val previewScore = previewScores[combination]

                        val isEnabled = !state.gameEnded &&
                                state.hasRolledAtLeastOnce &&
                                ((state.isPlayer1Turn && player1Score == null) || (!state.isPlayer1Turn && player2Score == null))

                        MultiplayerTableRow(
                            combination,
                            player1Score?.toString() ?: previewScore.takeIf { state.isPlayer1Turn }?.toString() ?: "",
                            player2Score?.toString() ?: previewScore.takeIf { !state.isPlayer1Turn }?.toString() ?: "",
                            enabled = isEnabled,
                            onClick = {
                                if (isEnabled) viewModel.selectScore(combination)
                            },
                            bold = false,
                            isPlayer1Turn = state.isPlayer1Turn
                        )
                    }

                    val upper1 = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                    val upperSum1 = upper1.mapNotNull { state.scoreMapPlayer1[it] }.sum()
                    val bonus1 = if (upperSum1 >= 63) 35 else 0
                    val totalScore1 = state.scoreMapPlayer1.filterKeys { it != "Bonus" }.values.filterNotNull().sum() + bonus1

                    val upper2 = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                    val upperSum2 = upper2.mapNotNull { state.scoreMapPlayer2[it] }.sum()
                    val bonus2 = if (upperSum2 >= 63) 35 else 0
                    val totalScore2 = state.scoreMapPlayer2.filterKeys { it != "Bonus" }.values.filterNotNull().sum() + bonus2

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    MultiplayerTableRow("Bonus", bonus1.toString(), bonus2.toString(), bold = true, isPlayer1Turn = state.isPlayer1Turn)
                    MultiplayerTableRow("Total", totalScore1.toString(), totalScore2.toString(), bold = true, isPlayer1Turn = state.isPlayer1Turn)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(MaterialTheme.colorScheme.primary)
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
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Text("Roll (${state.remainingRolls})", color = MaterialTheme.colorScheme.onSecondary)
                }
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }
    }
}

@Composable
fun MultiplayerTableRow(
    combination: String,
    player1Score: String,
    player2Score: String,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    header: Boolean = false,
    bold: Boolean = false,
    isPlayer1Turn: Boolean = true
) {
    val backgroundColor = when {
        header -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
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
                else
                    Modifier.background(backgroundColor)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Text(
            text = combination,
            modifier = Modifier.weight(2f),
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = 16.sp
        )
        Text(
            text = player1Score,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = 16.sp
        )
        Text(
            text = player2Score,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = 16.sp
        )
    }
}
