package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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

    // Lista completa delle combinazioni Yahtzee
    val allCombinations = listOf(
        "Aces",
        "Twos",
        "Threes",
        "Fours",
        "Fives",
        "Sixes",
        "Three of a Kind",
        "Four of a Kind",
        "Full House",
        "Small Straight",
        "Large Straight",
        "Yahtzee",
        "Chance"
    )

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
            // Icona Home in alto a destra - resta facilmente cliccabile
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
                    .padding(
                        top = 0.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 96.dp // Aumentato per lasciare spazio ai bottoni più in alto
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Spacer per non sovrapporre i dadi all'icona Home (più spazio di prima)
                Spacer(modifier = Modifier.height(72.dp))

                // Dadi
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
                                    .clickable(enabled = state.remainingRolls < 3) {
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

                // Spacer tra dadi e tabella delle combinazioni: ridotto da 8.dp a 0.dp
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
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "SCORE",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp
                    )
                }

                // Tabella punteggi scrollabile (solo le righe)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .verticalScroll(rememberScrollState())
                ) {
                    allCombinations.forEachIndexed { index, combination ->
                        if (index != 0) {
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        }
                        TableRow(
                            combination = combination,
                            currentScore = state.scoreMap[combination],
                            previewScore = previewScores[combination],
                            onClick = { viewModel.selectScore(combination) },
                            enabled = state.canSelectScore && state.scoreMap[combination] == null && !state.gameEnded,
                            alternate = index % 2 == 1
                        )
                    }

                    // Calcolo bonus e totale
                    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                    val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
                    val bonus = if (upperSum >= 63) 35 else 0
                    val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
                    TableRow("Bonus", bonus, null, {}, bold = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
                    TableRow("Total Score", totalScore, null, {}, bold = true)
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
                    val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
                    val bonus = if (upperSum >= 63) 35 else 0
                    val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus
                    Text(
                        text = "Punteggio finale: $totalScore",
                        fontSize = 20.sp,
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

            // Bottoni in basso - padding bottom ulteriormente aumentato per alzarli ancora di più
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 56.dp) // Era 32.dp, ora 56.dp ancora più in alto
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
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
                    Text("Roll (${state.remainingRolls})", color = MaterialTheme.colorScheme.onSecondary, fontSize = 18.sp)
                }
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .height(48.dp)
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.onSecondary, fontSize = 18.sp)
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
    bold: Boolean = false,
    alternate: Boolean = false
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
                else
                    Modifier.background(backgroundColor)
            )
            .then(
                if (enabled) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)) else Modifier
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 10.dp, vertical = if (header) 12.dp else 14.dp)
    ) {
        Text(
            text = combination,
            modifier = Modifier.weight(1f),
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 18.sp else 18.sp
        )
        Text(
            text = currentScore?.toString()
                ?: previewScore?.toString()
                ?: if (header) "SCORE" else "",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = if (header) 18.sp else 18.sp
        )
    }
}