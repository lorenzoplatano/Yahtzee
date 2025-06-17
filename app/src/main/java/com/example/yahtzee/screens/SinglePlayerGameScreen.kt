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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    
    val isCompactScreen = screenHeight < 600.dp
    
    // Calcola dimensioni responsive senza scrolling
    val diceSize = (screenWidth / 6).coerceAtLeast(40.dp).coerceAtMost(60.dp)
    val diceTextSize = (diceSize.value / 2).coerceAtLeast(16f).coerceAtMost(28f)
    val tableRowFontSize = if (isCompactScreen) 14.sp else 16.sp
    
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
    
    val allCombinations = listOf(
        "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "Three of a Kind", "Four of a Kind", "Full House",
        "Small Straight", "Large Straight", "Yahtzee", "Chance"
    )

    // DICHIARAZIONE VARIABILI GLOBALI PER LA SCHERMATA
    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
    val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
    val bonus = if (upperSum >= 63) 35 else 0
    val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus

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
                .systemBarsPadding() // Gestisce meglio gli spazi delle barre di sistema
        ) {
            // Icona Home in alto a destra
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(32.dp)
                    .zIndex(1f)
                    .clickable { navController.navigate("homepage") }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 60.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 84.dp // Spazio per i pulsanti in basso
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Dadi
                if (!state.gameEnded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp), // Ridotto padding
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        state.diceValues.forEachIndexed { index, value ->
                            Box(
                                modifier = Modifier
                                    .size(diceSize)
                                    .background(
                                        if (state.heldDice[index]) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp) // Ridotto corner radius
                                    )
                                    .border(
                                        width = if (state.heldDice[index]) 2.dp else 1.dp, // Ridotto border
                                        color = if (state.heldDice[index]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(8.dp) // Ridotto corner radius
                                    )
                                    .clickable(enabled = state.remainingRolls < 3) {
                                        viewModel.toggleHold(index)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    value.toString(),
                                    fontSize = diceTextSize.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.heldDice[index]) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp)) // Ridotto spacing

                // Tabella punteggi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Utilizza il peso per gestire lo spazio disponibile
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column {
                        // Header della tabella
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)) // Ridotto corner radius
                                .background(MaterialTheme.colorScheme.primary)
                                .border(
                                    width = 1.dp, // Ridotto border
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp) // Ridotto corner radius
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp), // Ridotto padding
                        ) {
                            Text(
                                text = "COMBINATION",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = tableRowFontSize
                            )
                            Text(
                                text = "SCORE",
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = tableRowFontSize
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp, // Ridotto border
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp) // Ridotto corner radius
                                )
                                .background(
                                    MaterialTheme.colorScheme.surface, 
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp) // Ridotto corner radius
                                ),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            allCombinations.forEachIndexed { index, combination ->
                                if (index != 0) {
                                    HorizontalDivider(
                                        thickness = 0.5.dp, // Ridotto thickness
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                }
                                TableRow(
                                    combination = combination,
                                    currentScore = state.scoreMap[combination],
                                    previewScore = previewScores[combination],
                                    onClick = { viewModel.selectScore(combination) },
                                    enabled = state.canSelectScore && state.scoreMap[combination] == null && !state.gameEnded,
                                    alternate = index % 2 == 1,
                                    fontSize = tableRowFontSize,
                                    compactPadding = true // Aggiunto per ridurre il padding nelle righe
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 1.dp), // Ridotto padding
                                thickness = 1.dp, // Ridotto thickness
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableRow(
                                combination = "Bonus",
                                currentScore = bonus,
                                previewScore = null,
                                onClick = {},
                                bold = true,
                                fontSize = tableRowFontSize,
                                compactPadding = true
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 1.dp), // Ridotto padding
                                thickness = 1.dp, // Ridotto thickness
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableRow(
                                combination = "Total Score",
                                currentScore = totalScore,
                                previewScore = null,
                                onClick = {},
                                bold = true,
                                fontSize = tableRowFontSize,
                                compactPadding = true
                            )
                        }
                    }
                }

                if (state.gameEnded) {
                    Spacer(modifier = Modifier.height(8.dp)) // Ridotto spacing
                    Text(
                        text = "Partita Terminata!",
                        fontSize = tableRowFontSize.times(1.2f),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp) // Ridotto padding
                    )
                    Text(
                        text = "Punteggio finale: $totalScore",
                        fontSize = tableRowFontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .padding(top = 8.dp) // Ridotto padding
                            .height(40.dp) // Ridotta altezza
                            .fillMaxWidth(0.7f)
                    ) {
                        Text("Nuova Partita", fontSize = tableRowFontSize)
                    }
                }
            }

            // Bottoni in basso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Transparent)
                    .padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.rollDice() },
                        enabled = state.remainingRolls > 0 && !state.gameEnded,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(44.dp)
                    ) {
                        Text(
                            "Roll (${state.remainingRolls})",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = tableRowFontSize.times(0.9f)
                        )
                    }
                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(44.dp)
                    ) {
                        Text(
                            "Reset",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = tableRowFontSize.times(0.9f)
                        )
                    }
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
    alternate: Boolean = false,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    compactPadding: Boolean = false
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
    
    // Usa padding ridotto quando compactPadding è true
    val rowPadding = if (compactPadding) {
        PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    } else {
        PaddingValues(horizontal = 10.dp, vertical = 8.dp)
    }

    Row(
        modifier = modifier
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
                if (enabled) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)) else Modifier
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(rowPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = combination,
            modifier = Modifier.weight(1f),
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = fontSize,
            maxLines = 1
        )
        Text(
            text = currentScore?.toString()
                ?: previewScore?.toString()
                ?: if (header) "SCORE" else "",
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            fontSize = fontSize,
            maxLines = 1
        )
    }
}