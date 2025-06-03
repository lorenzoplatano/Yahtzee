package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.ui.theme.SinglePlayerTheme
import com.example.yahtzee.viewmodel.SinglePlayerGameViewModel

@Composable
fun SinglePlayerGameScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val viewModel: SinglePlayerGameViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SinglePlayerGameViewModel(db) as T
            }
        }
    )

    val state = viewModel.state
    var showResetDialog by remember { mutableStateOf(false) }
    val previewScores = viewModel.previewScores()

    SinglePlayerTheme {
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Sei sicuro?") },
                text = { Text("Vuoi davvero ricominciare la partita? I progressi attuali andranno persi.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetGame()
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
        ) {
            // Icona Home in alto a destra
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.onSurface,
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
                    .padding(
                        top = 40.dp, // Più in alto!
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 120.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Titolo YAHTZEE più in alto
                Text(
                    text = "YAHTZEE",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp)) // Spazio tra titolo e dadi

                // Dadi più in alto
                if (!state.gameEnded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        state.diceValues.forEachIndexed { index, value ->
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        if (state.heldDice[index]) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                                    .clickable(enabled = state.remainingRolls < 3) {
                                        viewModel.toggleHold(index)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    value.toString(),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.heldDice[index]) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Spazio tra dadi e tabella
                Spacer(modifier = Modifier.height(8.dp))

                if (state.gameEnded) {
                    Text(
                        text = "Partita Terminata!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                    val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
                    val bonus = if (upperSum >= 63) 35 else 0
                    val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus
                    Text(
                        text = "Punteggio finale: $totalScore",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    ) {
                        TableRow("COMBINATION", null, null, {}, header = true)
                        viewModel.combinations.forEachIndexed { index, combination ->
                            if (index != 0) {
                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)
                            }
                            TableRow(
                                combination = combination,
                                currentScore = state.scoreMap[combination],
                                previewScore = previewScores[combination],
                                onClick = { viewModel.selectScore(combination) },
                                enabled = state.canSelectScore && state.scoreMap[combination] == null
                            )
                        }

                        val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                        val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
                        val bonus = if (upperSum >= 63) 35 else 0
                        val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)
                        TableRow("Bonus", bonus, null, {}, bold = true)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)
                        TableRow("Total Score", totalScore, null, {}, bold = true)
                    }
                }
            }

            // Bottoni leggermente più in alto rispetto al fondo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
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
        header -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.primaryContainer
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
                else
                    Modifier.background(backgroundColor)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 8.dp, vertical = if (header) 12.dp else 12.dp) // Dimensioni originali
    ) {
        Text(
            text = combination,
            modifier = Modifier.weight(1f),
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 16.sp else 16.sp // Dimensioni originali
        )
        Text(
            text = currentScore?.toString()
                ?: previewScore?.toString()
                ?: if (header) "SCORE" else "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 16.sp else 16.sp // Dimensioni originali
        )
    }
}