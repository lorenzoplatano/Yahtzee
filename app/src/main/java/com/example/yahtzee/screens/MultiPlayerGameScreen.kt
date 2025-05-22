
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel

@Composable
fun MultiplayerGameScreen(navController: NavController, viewModel: MultiplayerGameViewModel = viewModel()) {
    val state = viewModel.state
    var showResetDialog by remember { mutableStateOf(false) }
    val previewScores = viewModel.previewScores()

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
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEBEE), Color(0xFFF8BBD0)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color.Gray,
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
                color = Color(0xFF880E4F),
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
                color = if (state.gameEnded) Color.Red else Color.Black,
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
                                if (state.heldDice[index]) Color(0xFFD81B60) else Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.5.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable(enabled = state.remainingRolls < 3 && !state.gameEnded) {
                                viewModel.toggleHold(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.heldDice[index]) Color.White else Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF880E4F), shape = RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            ) {
                MultiplayerTableRow("COMBINATION", "Player 1", "Player 2", header = true)

                viewModel.combinations.forEach { combination ->
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFF880E4F))

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
                    color = Color(0xFF880E4F)
                )
                MultiplayerTableRow("Bonus", bonus1.toString(), bonus2.toString(), bold = true)
                MultiplayerTableRow("Total", totalScore1.toString(), totalScore2.toString(), bold = true)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xFF880E4F))
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.rollDice() },
                enabled = state.remainingRolls > 0 && !state.gameEnded,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text("Roll (${state.remainingRolls})")
            }
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text("Reset")
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
        header -> Color(0xFF880E4F)
        enabled -> Color(0xFFF8BBD0)
        else -> Color.Transparent
    }

    val textColor = when {
        header -> Color.White
        enabled -> Color.Black
        else -> Color.DarkGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
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
