package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import kotlin.random.Random

@Composable
fun GameScreenSinglePlayer(navController: NavController) {
    var diceValues by remember { mutableStateOf(List(5) { (1..6).random() }) }
    var selectedDice by remember { mutableStateOf(List(5) { false }) }
    var scoreMap by remember { mutableStateOf(mutableMapOf<String, Int?>()) }
    var remainingRolls by remember { mutableStateOf(3) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9)),
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
                .padding(top = 96.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YAHTZEE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1),
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
                                if (selectedDice[index]) Color(0xFF64B5F6) else Color.White,
                                shape = MaterialTheme.shapes.small
                            )
                            .border(2.dp, if (selectedDice[index]) Color(0xFF1976D2) else Color.Gray)
                            .clickable {
                                val newSelection = selectedDice.toMutableList()
                                newSelection[index] = !newSelection[index]
                                selectedDice = newSelection
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(value.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF0D47A1), shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                TableRow("COMBINATION", null, {}, header = true)

                val combinations = listOf(
                    "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Bonus",
                    "3 of a Kind", "4 of a Kind", "Full House", "Small Straight", "Large Straight",
                    "Yahtzee", "Chance"
                )

                combinations.forEach { combination ->
                    Divider(thickness = 1.dp, color = Color(0xFF0D47A1))
                    TableRow(
                        combination = combination,
                        currentScore = scoreMap[combination],
                        onClick = {
                            scoreMap[combination] = calculateScore(combination, diceValues, scoreMap)
                            remainingRolls = 3
                            // Quando si assegna un punteggio, resetta la selezione dadi
                            selectedDice = List(5) { false }
                        }
                    )
                }

                Divider(thickness = 1.dp, color = Color(0xFF0D47A1), modifier = Modifier.padding(vertical = 4.dp))
                TableRow("Total Score", scoreMap.values.filterNotNull().sum(), {}, bold = true)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        diceValues = List(5) { (1..6).random() }
                        selectedDice = List(5) { false }
                        scoreMap = mutableMapOf()
                        remainingRolls = 3
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("Play", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (remainingRolls > 0) {
                            diceValues = diceValues.mapIndexed { index, value ->
                                if (!selectedDice[index]) (1..6).random() else value
                            }
                            remainingRolls--
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Roll Dice ($remainingRolls)", fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
fun TableRow(
    combination: String,
    currentScore: Int?,
    onClick: () -> Unit,
    header: Boolean = false,
    bold: Boolean = false
) {
    val textStyle = if (bold || header) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = combination,
            style = textStyle,
            fontSize = 16.sp,
            modifier = Modifier.weight(1.5f)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = !header && currentScore == null) { onClick() },
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = currentScore?.toString() ?: "—",
                style = textStyle,
                fontSize = 16.sp,
                textAlign = TextAlign.End
            )
        }
    }
}

fun calculateScore(combination: String, diceValues: List<Int>, upperScores: Map<String, Int?>): Int {
    val counts = diceValues.groupingBy { it }.eachCount()
    val totalSum = diceValues.sum()

    return when (combination) {
        "Aces" -> diceValues.filter { it == 1 }.sum()
        "Twos" -> diceValues.filter { it == 2 }.sum()
        "Threes" -> diceValues.filter { it == 3 }.sum()
        "Fours" -> diceValues.filter { it == 4 }.sum()
        "Fives" -> diceValues.filter { it == 5 }.sum()
        "Sixes" -> diceValues.filter { it == 6 }.sum()

        "Bonus" -> {
            val upperTotal = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                .mapNotNull { upperScores[it] }
                .sum()
            if (upperTotal >= 63) 35 else 0
        }

        "3 of a Kind" -> if (counts.values.any { it >= 3 }) totalSum else 0
        "4 of a Kind" -> if (counts.values.any { it >= 4 }) totalSum else 0
        "Full House" -> if (counts.values.contains(3) && counts.values.contains(2)) 25 else 0
        "Small Straight" -> {
            val unique = diceValues.toSet()
            val straights = listOf(
                setOf(1, 2, 3, 4),
                setOf(2, 3, 4, 5),
                setOf(3, 4, 5, 6)
            )
            if (straights.any { it.all { num -> unique.contains(num) } }) 30 else 0
        }

        "Large Straight" -> {
            val sorted = diceValues.toSortedSet()
            if (sorted == setOf(1, 2, 3, 4, 5) || sorted == setOf(2, 3, 4, 5, 6)) 40 else 0
        }

        "Yahtzee" -> if (counts.values.any { it == 5 }) 50 else 0

        "Chance" -> totalSum

        else -> 0
    }
}
