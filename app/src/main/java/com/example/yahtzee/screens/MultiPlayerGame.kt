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
import androidx.navigation.NavController
import com.example.yahtzee.logic.GameLogic

@Composable
fun GameScreenMultiplayer(navController: NavController) {
    var diceValues by remember { mutableStateOf(List(5) { (1..6).random() }) }
    var heldDice by remember { mutableStateOf(List(5) { false }) }
    var remainingRolls by remember { mutableStateOf(3) }
    var hasRolledAtLeastOnce by remember { mutableStateOf(false) }

    var scoreMapPlayer1 by remember { mutableStateOf(mutableMapOf<String, Int?>()) }
    var scoreMapPlayer2 by remember { mutableStateOf(mutableMapOf<String, Int?>()) }

    var isPlayer1Turn by remember { mutableStateOf(true) }
    var gameEnded by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val logic = GameLogic()

    val combinations = listOf(
        "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "3 of a Kind", "4 of a Kind", "Full House", "Small Straight", "Large Straight",
        "Yahtzee", "Chance"
    )

    fun resetGame() {
        diceValues = List(5) { (1..6).random() }
        heldDice = List(5) { false }
        remainingRolls = 3
        hasRolledAtLeastOnce = false
        scoreMapPlayer1 = mutableMapOf()
        scoreMapPlayer2 = mutableMapOf()
        isPlayer1Turn = true
        gameEnded = false
    }

    gameEnded = combinations.all { combo ->
        (scoreMapPlayer1[combo] != null) && (scoreMapPlayer2[combo] != null)
    }

    val currentScoreMap = if (isPlayer1Turn) scoreMapPlayer1 else scoreMapPlayer2

    val previewScores = remember(diceValues, hasRolledAtLeastOnce, currentScoreMap) {
        if (hasRolledAtLeastOnce) {
            combinations.associateWith { combo ->
                if (currentScoreMap[combo] == null) {
                    logic.calculateScore(combo, diceValues, currentScoreMap)
                } else null
            }
        } else emptyMap()
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Sei sicuro?") },
            text = { Text("Vuoi davvero ricominciare la partita? I progressi attuali andranno persi.") },
            confirmButton = {
                TextButton(onClick = {
                    resetGame()
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
                    gameEnded -> "Partita Terminata"
                    isPlayer1Turn -> "Turno: Player 1"
                    else -> "Turno: Player 2"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = if (gameEnded) Color.Red else Color.Black,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                diceValues.forEachIndexed { index, value ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                if (heldDice[index]) Color(0xFFD81B60) else Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.5.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable(enabled = remainingRolls < 3 && !gameEnded) {
                                heldDice = heldDice.toMutableList().also { it[index] = !it[index] }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (heldDice[index]) Color.White else Color.Black
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

                combinations.forEach { combination ->
                    Divider(color = Color(0xFF880E4F), thickness = 1.dp)

                    val player1Score = scoreMapPlayer1[combination]
                    val player2Score = scoreMapPlayer2[combination]

                    val previewScore = previewScores[combination]

                    val isEnabled = !gameEnded &&
                            hasRolledAtLeastOnce &&
                            ((isPlayer1Turn && player1Score == null) || (!isPlayer1Turn && player2Score == null))

                    MultiplayerTableRow(
                        combination,
                        player1Score?.toString() ?: previewScore.takeIf { isPlayer1Turn }?.toString() ?: "",
                        player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }?.toString() ?: "",
                        enabled = isEnabled,
                        onClick = {
                            if (!isEnabled) return@MultiplayerTableRow
                            val scoreToSet = logic.calculateScore(combination, diceValues, currentScoreMap)
                            if (isPlayer1Turn) {
                                scoreMapPlayer1 = scoreMapPlayer1.toMutableMap().also { it[combination] = scoreToSet }
                            } else {
                                scoreMapPlayer2 = scoreMapPlayer2.toMutableMap().also { it[combination] = scoreToSet }
                            }
                            remainingRolls = 3
                            hasRolledAtLeastOnce = false
                            heldDice = List(5) { false }
                            isPlayer1Turn = !isPlayer1Turn
                            diceValues = List(5) { (1..6).random() }
                        },
                        bold = false,
                        isPlayer1Turn = isPlayer1Turn // <-- Passaggio chiave
                    )
                }

                val upper1 = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                val upperSum1 = upper1.mapNotNull { scoreMapPlayer1[it] }.sum()
                val bonus1 = if (upperSum1 >= 63) 35 else 0
                val totalScore1 = scoreMapPlayer1.filterKeys { it != "Bonus" }.values.filterNotNull().sum() + bonus1

                val upper2 = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                val upperSum2 = upper2.mapNotNull { scoreMapPlayer2[it] }.sum()
                val bonus2 = if (upperSum2 >= 63) 35 else 0
                val totalScore2 = scoreMapPlayer2.filterKeys { it != "Bonus" }.values.filterNotNull().sum() + bonus2

                Divider(color = Color(0xFF880E4F), thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))
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
                onClick = {
                    if (remainingRolls > 0 && !gameEnded) {
                        diceValues = diceValues.mapIndexed { index, value ->
                            if (heldDice[index]) value else (1..6).random()
                        }
                        remainingRolls -= 1
                        hasRolledAtLeastOnce = true
                    }
                },
                enabled = remainingRolls > 0 && !gameEnded,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text("Roll ($remainingRolls)")
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
    onClick: (() -> Unit)? = null,
    header: Boolean = false,
    bold: Boolean = false,
    isPlayer1Turn: Boolean = true
) {
    val backgroundColor = when {
        header -> Color(0xFF880E4F)
        enabled -> Color(0xFFF8BBD0)
        else -> Color.White
    }
    val textColorHeader = if (header) Color.White else Color(0xFF880E4F)
    val textStyle = if (bold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    else MaterialTheme.typography.bodyLarge

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = combination,
            modifier = Modifier.weight(1.8f),
            color = textColorHeader,
            style = textStyle
        )
        Text(
            text = player1Score,
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = enabled && isPlayer1Turn && onClick != null) { onClick?.invoke() },
            textAlign = TextAlign.Center,
            color = textColorHeader,
            style = textStyle
        )
        Text(
            text = player2Score,
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = enabled && !isPlayer1Turn && onClick != null) { onClick?.invoke() },
            textAlign = TextAlign.Center,
            color = textColorHeader,
            style = textStyle
        )
    }
}
