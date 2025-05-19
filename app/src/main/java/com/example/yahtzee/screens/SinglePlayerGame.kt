package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.yahtzee.logic.GameLogic

@Composable
fun GameScreenSinglePlayer(navController: NavController) {
    var diceValues by remember { mutableStateOf(List(5) { (1..6).random() }) }
    var scoreMap by remember { mutableStateOf(mutableMapOf<String, Int?>()) }
    var remainingRolls by remember { mutableStateOf(3) }
    var canSelectScore by remember { mutableStateOf(false) }
    var heldDice by remember { mutableStateOf(List(5) { false }) }
    var gameEnded by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val Logic = GameLogic()

    fun resetGame() {
        diceValues = List(5) { (1..6).random() }
        scoreMap = mutableMapOf()
        remainingRolls = 3
        canSelectScore = false
        heldDice = List(5) { false }
        gameEnded = false
    }

    val combinations = listOf(
        "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "3 of a Kind", "4 of a Kind", "Full House", "Small Straight", "Large Straight",
        "Yahtzee", "Chance"
    )

    val previewScores = remember(diceValues, canSelectScore, scoreMap) {
        if (canSelectScore) {
            combinations.associateWith { combination ->
                if (scoreMap[combination] == null && combination != "Bonus") {
                    Logic.calculateScore(combination, diceValues, scoreMap)
                } else null
            }
        } else {
            emptyMap()
        }
    }

    gameEnded = combinations.filter { it != "Bonus" }.all { scoreMap[it] != null }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Sei sicuro?") },
            text = { Text("Vuoi davvero ricominciare la partita? I progressi attuali andranno persi.") },
            confirmButton = {
                TextButton(onClick = {
                    resetGame()
                    showResetDialog = false
                }) {
                    Text("Sì")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEBEE), Color(0xFFF8BBD0))
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
                .zIndex(1f)
                .clickable { navController.navigate("homepage") }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp) // spazio per i pulsanti fissi
                .verticalScroll(rememberScrollState())
                .padding(top = 96.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YAHTZEE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF880E4F),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (gameEnded) {
                Text(
                    text = "Partita Terminata!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
                Text(
                    text = "Punteggio finale: ${scoreMap.values.filterNotNull().sum()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(56.dp)
                ) {
                    Text("Nuova Partita", fontSize = 18.sp)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    diceValues.forEachIndexed { index, value ->
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    if (heldDice[index]) Color(0xFFD81B60) else Color.White,
                                    shape = MaterialTheme.shapes.small
                                )
                                .border(1.dp, Color.Gray)
                                .clickable(enabled = remainingRolls < 3) {
                                    heldDice = heldDice.toMutableList().also {
                                        it[index] = !it[index]
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                value.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (heldDice[index]) Color.White else Color.Black
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFF880E4F), shape = MaterialTheme.shapes.medium)
                        .padding(8.dp)
                ) {
                    TableRow("COMBINATION", null, null, {}, header = true)
                    combinations.forEach { combination ->
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFF880E4F))
                        TableRow(
                            combination = combination,
                            currentScore = scoreMap[combination],
                            previewScore = previewScores[combination],
                            onClick = {
                                scoreMap[combination] = Logic.calculateScore(combination, diceValues, scoreMap)
                                remainingRolls = 3
                                canSelectScore = false
                                heldDice = List(5) { false }
                            },
                            enabled = canSelectScore && scoreMap[combination] == null && combination != "Bonus"
                        )
                    }

                    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                    val upperSum = upper.mapNotNull { scoreMap[it] }.sum()
                    val bonus = if (upperSum >= 63) 35 else 0
                    val totalScore = scoreMap.filterKeys { it != "Bonus" }.values.filterNotNull().sum() + bonus

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = Color(0xFF880E4F))
                    TableRow("Bonus", bonus, null, {}, bold = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = Color(0xFF880E4F))
                    TableRow("Total Score", totalScore, null, {}, bold = true)
                }
            }
        }

        // 🔒 Pulsanti fissi in basso
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (remainingRolls > 0) {
                        diceValues = diceValues.mapIndexed { index, value ->
                            if (heldDice[index]) value else (1..6).random()
                        }
                        remainingRolls--
                        canSelectScore = true
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                enabled = !gameEnded
            ) {
                Text("Roll Dice ($remainingRolls)", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B))
            ) {
                Text("Reset", fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun TableRow(
    combination: String,
    currentScore: Int?,
    previewScore: Int? = null,
    onClick: () -> Unit,
    enabled: Boolean = false,
    header: Boolean = false,
    bold: Boolean = false
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
            modifier = Modifier.weight(1f),
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = 16.sp
        )
        Text(
            text = currentScore?.toString()
                ?: previewScore?.toString()
                ?: if (header) "SCORE" else "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = 16.sp
        )
    }
}