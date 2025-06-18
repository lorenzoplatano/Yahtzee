package com.example.yahtzee.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.compareTo

@Composable
fun MultiplayerGameScreen(navController: NavController) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Calcola fattori di scala basati sulle dimensioni dello schermo
    val scaleFactor = remember {
        (screenWidth / 360.dp).coerceIn(0.85f, 1.2f)
    }

    // Flag per schermi compatti
    val isCompactScreen = screenHeight < 600.dp

    // Calcolo dinamico delle dimensioni
    val diceAreaWidth = screenWidth * 0.9f - 32.dp
    val diceSize = (diceAreaWidth / 5f).coerceAtMost(56.dp).coerceAtLeast(36.dp)
    val headerPadding = screenHeight * 0.05f
    val bottomAreaHeight = screenHeight * 0.1f

    val viewModel: MultiplayerGameViewModel = viewModel()
    val state = viewModel.state
    var showResetDialog by remember { mutableStateOf(false) }
    val previewScores = viewModel.previewScores()
    var showPreviews by remember { mutableStateOf(true) }

    val allDiceHeld = state.diceValues.isNotEmpty() &&
            state.diceValues.filterIndexed { idx, _ -> state.heldDice[idx] }.size == state.diceValues.size

    val combinationKeys = listOf(
        "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "Three of a Kind", "Four of a Kind", "Full House",
        "Small Straight", "Large Straight", "Yahtzee", "Chance"
    )
    val combinationLabels = mapOf(
        "Aces" to stringResource(id = R.string.aces),
        "Twos" to stringResource(id = R.string.twos),
        "Threes" to stringResource(id = R.string.threes),
        "Fours" to stringResource(id = R.string.fours),
        "Fives" to stringResource(id = R.string.fives),
        "Sixes" to stringResource(id = R.string.sixes),
        "Three of a Kind" to stringResource(id = R.string.three_of_a_kind),
        "Four of a Kind" to stringResource(id = R.string.four_of_a_kind),
        "Full House" to stringResource(id = R.string.full_house),
        "Small Straight" to stringResource(id = R.string.small_straight),
        "Large Straight" to stringResource(id = R.string.large_straight),
        "Yahtzee" to stringResource(id = R.string.yahtzee),
        "Chance" to stringResource(id = R.string.chance)
    )

    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
    val upperSum1 = upper.mapNotNull { state.scoreMapPlayer1[it] }.sum()
    val bonus1 = if (upperSum1 >= 63) 35 else 0
    val totalScore1 = state.scoreMapPlayer1.values.filterNotNull().sum() + bonus1
    val upperSum2 = upper.mapNotNull { state.scoreMapPlayer2[it] }.sum()
    val bonus2 = if (upperSum2 >= 63) 35 else 0
    val totalScore2 = state.scoreMapPlayer2.values.filterNotNull().sum() + bonus2
    val progressBonusText1 = "${upperSum1.coerceAtMost(63)}/63"
    val progressBonusText2 = "${upperSum2.coerceAtMost(63)}/63"

    var isRolling by remember { mutableStateOf(false) }
    var showHomeDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Nuova variabile per sapere quando l'animazione è davvero finita
    var animationDone by remember { mutableStateOf(true) }

    fun rollDiceWithAnimation() {
        if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) {
            coroutineScope.launch {
                isRolling = true
                animationDone = false
                showPreviews = false

                // Aumenta il tempo di animazione per sincronizzarlo con l'animazione migliorata
                // che ora dura 700ms
                delay(800) // Leggermente più lungo dell'animazione dei dadi per garantire che sia completata

                viewModel.rollDice()

                // Piccolo ritardo aggiuntivo per permettere al giocatore di vedere i nuovi valori
                delay(200)

                isRolling = false
                animationDone = true
                showPreviews = true
            }
        }
    }

    MultiPlayerTheme(isPlayerOne = state.isPlayer1Turn) {
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(stringResource(id = R.string.dialog_title)) },
                text = { Text(stringResource(id = R.string.dialog_reset_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetGame()
                        showResetDialog = false
                    }) { Text(stringResource(id = R.string.confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text(stringResource(id = R.string.cancel))  }
                }
            )
        }

        if (showHomeDialog) {
            AlertDialog(
                onDismissRequest = { showHomeDialog = false },
                title = { Text(stringResource(id = R.string.dialog_title)) },
                text = { Text(stringResource(id = R.string.dialog_home_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        showHomeDialog = false
                        navController.navigate("homepage") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text(stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHomeDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.chunky),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            // Home button
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = headerPadding.coerceAtLeast(16.dp).coerceAtMost(40.dp),
                        end = (screenWidth * 0.04f).coerceAtLeast(12.dp)
                    )
                    .size((44 * scaleFactor).dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .zIndex(1f)
                    .clickable {
                        if (state.gameEnded) {
                        // Se la partita è finita, torna direttamente alla home
                        navController.navigate("homepage")
                    } else {
                        // Se la partita è ancora in corso, mostra il dialogo di conferma
                        showHomeDialog = true
                    }
                               },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(id = R.string.back),
                        tint = Color.White,
                        modifier = Modifier.size((22 * scaleFactor).dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = (headerPadding * 2).coerceAtLeast(80.dp).coerceAtMost(100.dp),
                        start = (screenWidth * 0.02f).coerceAtLeast(8.dp),
                        end = (screenWidth * 0.02f).coerceAtLeast(8.dp),
                        bottom = bottomAreaHeight.coerceAtLeast(40.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Card unica per i dadi con altezza relativa
                if (!state.gameEnded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height((diceSize + 24.dp).coerceAtMost(screenHeight * 0.12f))
                            .padding(horizontal = (4 * scaleFactor).dp)
                            .offset(y = (8 * scaleFactor).dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape((12 * scaleFactor).dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        MultiGameDiceRow(
                            diceValues = state.diceValues,
                            heldDice = state.heldDice,
                            onDiceClick = { index ->
                                if (state.remainingRolls < 3 && !state.gameEnded) {
                                    viewModel.toggleHold(index)
                                }
                            },
                            enabled = state.remainingRolls < 3 && !state.gameEnded,
                            diceSize = diceSize,
                            isRolling = isRolling,
                            isPlayer1Turn = state.isPlayer1Turn
                        )
                    }
                }

                Spacer(modifier = Modifier.height((16 * scaleFactor).dp))

                // TABELLA con altezza adattiva
                if (!state.gameEnded) {
                    val tableMaxHeight = if (isCompactScreen) {
                        screenHeight * 0.68f
                    } else {
                        screenHeight * 0.62f
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = (screenHeight * 0.40f).coerceAtLeast(400.dp),
                                max = tableMaxHeight.coerceAtMost(600.dp)
                            )
                            .padding(horizontal = (screenWidth * 0.02f).coerceAtLeast(8.dp))
                            .offset(y = (8 * scaleFactor).dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape((14 * scaleFactor).dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.96f)
                        ),
                        shape = RoundedCornerShape((14 * scaleFactor).dp)
                    ) {
                        Column {
                            // HEADER CON RETTANGOLO SEMITRASPARENTE SUL PLAYER DI TURNO
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                        ),
                                        shape = RoundedCornerShape(topStart = (14 * scaleFactor).dp, topEnd = (14 * scaleFactor).dp)
                                    )
                                    .padding(horizontal = (10 * scaleFactor).dp, vertical = (10 * scaleFactor).dp)
                            ) {
                                Row {
                                    Text(
                                        text = stringResource(id = R.string.back),
                                        modifier = Modifier.weight(2f),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = (13 * scaleFactor).sp
                                    )
                                    // Player 1 header con highlight se è il suo turno
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = (2 * scaleFactor).dp)
                                            .then(
                                                if (state.isPlayer1Turn)
                                                    Modifier
                                                        .background(
                                                            Color.White.copy(alpha = 0.22f),
                                                            shape = RoundedCornerShape((6 * scaleFactor).dp)
                                                        )
                                                        .border(
                                                            width = (2 * scaleFactor).dp,
                                                            color = Color.White.copy(alpha = 0.35f),
                                                            shape = RoundedCornerShape((6 * scaleFactor).dp)
                                                        )
                                                else Modifier
                                            )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.player1),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = (2 * scaleFactor).dp),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = (13 * scaleFactor).sp
                                        )
                                    }
                                    // Player 2 header con highlight se è il suo turno
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = (2 * scaleFactor).dp)
                                            .then(
                                                if (!state.isPlayer1Turn)
                                                    Modifier
                                                        .background(
                                                            Color.White.copy(alpha = 0.22f),
                                                            shape = RoundedCornerShape((6 * scaleFactor).dp)
                                                        )
                                                        .border(
                                                            width = (2 * scaleFactor).dp,
                                                            color = Color.White.copy(alpha = 0.35f),
                                                            shape = RoundedCornerShape((6 * scaleFactor).dp)
                                                        )
                                                else Modifier
                                            )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.player2),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = (2 * scaleFactor).dp),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = (13 * scaleFactor).sp
                                        )
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = (4 * scaleFactor).dp, vertical = (2 * scaleFactor).dp)
                            ) {
                                val allRows = combinationKeys + listOf("Bonus", "Total")
                                allRows.forEachIndexed { index, combination ->
                                    if (index != 0) {
                                        HorizontalDivider(
                                            thickness = (0.2 * scaleFactor).dp,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                    when (combination) {
                                        "Bonus" -> {
                                            MultiplayerTableRowStyled(
                                                combination = stringResource(id = R.string.bonus) + " ($progressBonusText1 | $progressBonusText2)",
                                                player1Score = bonus1,
                                                player2Score = bonus2,
                                                bold = true,
                                                alternate = index % 2 == 1,
                                                isPlayer1Turn = state.isPlayer1Turn,
                                                fontSize = (14 * scaleFactor).sp,
                                                compactPadding = true,
                                                scaleFactor = scaleFactor
                                            )
                                        }
                                        "Total" -> {
                                            MultiplayerTableRowStyled(
                                                combination = stringResource(id = R.string.total_score),
                                                player1Score = totalScore1,
                                                player2Score = totalScore2,
                                                bold = true,
                                                alternate = index % 2 == 1,
                                                isPlayer1Turn = state.isPlayer1Turn,
                                                fontSize = (16 * scaleFactor).sp,
                                                compactPadding = true,
                                                scaleFactor = scaleFactor
                                            )
                                        }
                                        else -> {
                                            val player1Score = state.scoreMapPlayer1[combination]
                                            val player2Score = state.scoreMapPlayer2[combination]
                                            // Mostra la preview solo se l'animazione è finita
                                            val previewScore = if (showPreviews && animationDone) previewScores[combination] else null
                                            // Abilita la selezione solo se l'animazione è finita
                                            val isEnabled = !state.gameEnded &&
                                                    state.hasRolledAtLeastOnce &&
                                                    showPreviews && animationDone &&
                                                    ((state.isPlayer1Turn && player1Score == null) || (!state.isPlayer1Turn && player2Score == null))
                                            MultiplayerTableRowStyled(
                                                combination = combinationLabels[combination] ?: combination,
                                                player1Score = player1Score,
                                                player2Score = player2Score,
                                                previewScore = previewScore,
                                                enabled = isEnabled,
                                                onClick = { if (isEnabled) viewModel.selectScore(combination) },
                                                bold = false,
                                                alternate = index % 2 == 1,
                                                isPlayer1Turn = state.isPlayer1Turn,
                                                fontSize = (13 * scaleFactor).sp,
                                                compactPadding = true,
                                                scaleFactor = scaleFactor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // CARD FINE PARTITA CENTRATA con dimensioni responsive
                if (state.gameEnded) {
                    Spacer(modifier = Modifier.height((32 * scaleFactor).dp))
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight()
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape((18 * scaleFactor).dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.97f)
                            ),
                            shape = RoundedCornerShape((18 * scaleFactor).dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = (32 * scaleFactor).dp, horizontal = (18 * scaleFactor).dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val winner = when {
                                    totalScore1 > totalScore2 -> stringResource(id = R.string.player1)
                                    totalScore2 > totalScore1 -> stringResource(id = R.string.player2)
                                    else -> null
                                }
                                // Determina il colore in base al vincitore
                                val winnerColor = when (winner) {
                                    "Player 1" -> Color(0xFF4ECDC4) // Colore del bottone Roll
                                    "Player 2" -> Color(0xFFFF6B6B) // Colore del bottone Reset
                                    else -> Color(0xFF764BA2) // Colore viola del bottone impostazioni (pareggio)
                                }
                                if (winner != null) {
                                    Text(
                                        text = stringResource(
                                            id = R.string.win_message,
                                            winner,
                                            totalScore1,
                                            totalScore2
                                        ),
                                        fontSize = (22 * scaleFactor).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = winnerColor, // Usa il colore dinamico
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = stringResource(
                                            id = R.string.pareggio,
                                            totalScore1,
                                            totalScore2
                                        ),
                                        fontSize = (22 * scaleFactor).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = winnerColor, // Usa il colore dinamico
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height((24 * scaleFactor).dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            // Avvia direttamente una nuova partita senza dialog
                                            viewModel.resetGame()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height((56 * scaleFactor).dp),
                                        shape = RoundedCornerShape((12 * scaleFactor).dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = when (winner) {
                                                            "Player 1" -> listOf(Color(0xFF4ECDC4), Color(0xFF2AB7CA))
                                                            "Player 2" -> listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                                                            else -> listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                                        },
                                                        startX = 0f,
                                                        endX = Float.POSITIVE_INFINITY
                                                    ),
                                                    shape = RoundedCornerShape((10 * scaleFactor).dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size((24 * scaleFactor).dp)
                                                )
                                                Spacer(modifier = Modifier.width((8 * scaleFactor).dp))
                                                Text(
                                                    text = stringResource(id = R.string.new_game),
                                                    fontSize = (16 * scaleFactor).sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(!state.gameEnded){// Bottoni in basso con dimensioni responsive
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (screenWidth * 0.03f).coerceAtLeast(12.dp),
                        end = (screenWidth * 0.03f).coerceAtLeast(12.dp),
                        bottom = (screenHeight * 0.03f).coerceAtLeast(56.dp)
                    )
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape((16 * scaleFactor).dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape((16 * scaleFactor).dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((68 * scaleFactor).dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            shape = RoundedCornerShape((16 * scaleFactor).dp)
                        )
                        .padding(horizontal = (8 * scaleFactor).dp, vertical = (6 * scaleFactor).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Pulsante "Roll" con dimensioni relative
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height((52 * scaleFactor).dp)
                                .shadow(
                                    elevation = if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) 8.dp else 4.dp,
                                    shape = RoundedCornerShape((12 * scaleFactor).dp)
                                )
                                .clip(RoundedCornerShape((12 * scaleFactor).dp))
                                .clickable(
                                    enabled = state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld,
                                    onClick = { rollDiceWithAnimation() }
                                ),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape((12 * scaleFactor).dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
                                        ),
                                        shape = RoundedCornerShape((12 * scaleFactor).dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) 1f else 0.6f),
                                        modifier = Modifier.size((20 * scaleFactor).dp)
                                    )
                                    Spacer(modifier = Modifier.width((8 * scaleFactor).dp))
                                    Text(
                                        text = stringResource(id = R.string.roll) + " (${state.remainingRolls})",
                                        color = Color.White.copy(alpha = if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) 1f else 0.6f),
                                        fontSize = (14 * scaleFactor).sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.padding(vertical = (4 * scaleFactor).dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width((10 * scaleFactor).dp))

                        // Pulsante "Reset" con dimensioni relative
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height((52 * scaleFactor).dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape((12 * scaleFactor).dp)
                                )
                                .clip(RoundedCornerShape((12 * scaleFactor).dp))
                                .clickable { showResetDialog = true },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape((12 * scaleFactor).dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                                        ),
                                        shape = RoundedCornerShape((12 * scaleFactor).dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size((20 * scaleFactor).dp)
                                    )
                                    Spacer(modifier = Modifier.width((8 * scaleFactor).dp))
                                    Text(
                                        text = stringResource(id = R.string.reset),
                                        color = Color.White,
                                        fontSize = (14 * scaleFactor).sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.padding(vertical = (4 * scaleFactor).dp)
                                    )}
                                }
                            }
                        }
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
    isPlayer1Turn: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    compactPadding: Boolean = false,
    scaleFactor: Float = 1f
) {
    val backgroundColor = when {
        enabled -> Color(0xFFF0F9FF)
        alternate -> Color(0xFFFAFAFA)
        else -> Color.Transparent
    }

    val textColor = when {
        enabled -> Color(0xFF1E40AF)
        bold -> Color(0xFF1A1A1A)
        else -> Color(0xFF4A5568)
    }

    // Colori specifici per i giocatori
    val player1Color = Color(0xFF4ECDC4) // Colore del bottone Roll
    val player2Color = Color(0xFFFF6B6B) // Colore del bottone Reset

// Determina il colore per il punteggio del Player 1
    val player1TextColor = when {
        player1Score != null -> player1Color // Se ha già un punteggio, usa il suo colore
        previewScore != null && isPlayer1Turn -> Color(0xFF1E40AF) // Preview solo se è il suo turno
        else -> textColor
    }

// Determina il colore per il punteggio del Player 2
    val player2TextColor = when {
        player2Score != null -> player2Color // Se ha già un punteggio, usa il suo colore
        previewScore != null && !isPlayer1Turn -> Color(0xFF1E40AF) // Preview solo se è il suo turno
        else -> textColor
    }

    // Calcola padding in base alle dimensioni dello schermo
    val horizontalPadding = (8 * scaleFactor).dp
    val verticalPadding = if (compactPadding) {
        (5.5f * scaleFactor).dp
    } else {
        (6f * scaleFactor).dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (2 * scaleFactor).dp, vertical = (0.5 * scaleFactor).dp)
            .then(
                if (enabled) {
                    Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape((6 * scaleFactor).dp))
                        .border(
                            (1 * scaleFactor).dp,
                            Color(0xFF3B82F6),
                            RoundedCornerShape((6 * scaleFactor).dp)
                        )
                } else Modifier
            )
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape((6 * scaleFactor).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            Text(
                text = combination,
                modifier = Modifier.weight(2f),
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = fontSize
            )
            Text(
                text = player1Score?.toString() ?: previewScore.takeIf { isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = player1TextColor, // Usa il colore specifico
                fontSize = fontSize
            )
            Text(
                text = player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = player2TextColor, // Usa il colore specifico
                fontSize = fontSize
            )
        }
    }
}

@Composable
fun MultiDiceWithDots(
    value: Int,
    size: androidx.compose.ui.unit.Dp,
    dotColor: Color = Color.Black
) {
    val dotSize = size / 5
    val padding = size / 5

    Canvas(
        modifier = Modifier.size(size)
    ) {
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val topLeft = Offset(padding.toPx(), padding.toPx())
        val topRight = Offset(this.size.width - padding.toPx(), padding.toPx())
        val centerLeft = Offset(padding.toPx(), center.y)
        val centerRight = Offset(this.size.width - padding.toPx(), center.y)
        val bottomLeft = Offset(padding.toPx(), this.size.height - padding.toPx())
        val bottomRight = Offset(this.size.width - padding.toPx(), this.size.height - padding.toPx())

        when (value) {
            1 -> drawCircle(
                color = dotColor,
                radius = dotSize.toPx() / 2,
                center = center,
                style = Fill
            )
            2 -> {
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topRight,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomLeft,
                    style = Fill
                )
            }
            3 -> {
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topRight,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = center,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomLeft,
                    style = Fill
                )
            }
            4 -> {
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topRight,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomRight,
                    style = Fill
                )
            }
            5 -> {
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topRight,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = center,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomRight,
                    style = Fill
                )
            }
            6 -> {
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topRight,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = centerLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = centerRight,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomLeft,
                    style = Fill
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = bottomRight,
                    style = Fill
                )
            }
        }
    }
}

@Composable
fun MultiGameDiceRow(
    diceValues: List<Int>,
    heldDice: List<Boolean>,
    onDiceClick: (Int) -> Unit,
    enabled: Boolean,
    diceSize: androidx.compose.ui.unit.Dp = 50.dp,
    isRolling: Boolean = false,
    isPlayer1Turn: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val scaleFactor = remember { (screenWidth / 360.dp).coerceIn(0.85f, 1.2f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding((14 * scaleFactor).dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val diceAnimations = List(diceValues.size) { index ->
            // Rotazione più casuale e variabile per ogni dado
            val randomEndRotation = remember(isRolling) {
                (-540..720).random().toFloat() + (index * 45f)
            }

            // Aggiungiamo una rotazione basata sul valore del dado per più varietà
            val valueBasedRotation = remember(diceValues[index], isRolling) {
                diceValues[index] * 60f
            }

            // Animazione di rotazione più fluida con effetto di rimbalzo
            val rotation by animateFloatAsState(
                targetValue = if (isRolling && !heldDice[index]) randomEndRotation + valueBasedRotation else 0f,
                animationSpec = tween(
                    durationMillis = 650,
                    easing = EaseOutBack
                ),
                label = "diceRotation"
            )

            // Animazione di scala che simula un lancio più naturale
            val scale by animateFloatAsState(
                targetValue = if (isRolling && !heldDice[index]) 0.85f else 1f,
                animationSpec = keyframes {
                    durationMillis = 700
                    0.7f at 0
                    1.1f at 350
                    0.95f at 500
                    1f at 700
                },
                label = "diceScale"
            )

            // Offset orizzontale per aggiungere movimento laterale
            val offsetX by animateFloatAsState(
                targetValue = if (isRolling && !heldDice[index]) 0f else 0f,
                animationSpec = keyframes {
                    durationMillis = 700
                    (-15f) at 0
                    10f at 250
                    (-5f) at 500
                    0f at 700
                },
                label = "offsetX"
            )

            Triple(rotation, scale, offsetX)
        }

        diceValues.forEachIndexed { index, value ->
            Card(
                modifier = Modifier
                    .size(diceSize)
                    .shadow(
                        elevation = if (heldDice[index]) 6.dp else 3.dp,
                        shape = RoundedCornerShape((8 * scaleFactor).dp)
                    )
                    .clickable(enabled = enabled) {
                        onDiceClick(index)
                    },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape((8 * scaleFactor).dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (heldDice[index]) {
                                if (isPlayer1Turn) {
                                    // Colori per Player 1 (verde-acqua)
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
                                    )
                                } else {
                                    // Colori per Player 2 (rosso-arancio)
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                                    )
                                }
                            } else {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFF7FAFC), Color(0xFFEDF2F7))
                                )
                            },
                            shape = RoundedCornerShape((8 * scaleFactor).dp)
                        )
                        .border(
                            width = if (heldDice[index]) (2 * scaleFactor).dp else (1 * scaleFactor).dp,
                            color = if (heldDice[index]) Color.White else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape((8 * scaleFactor).dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.8f)
                            .graphicsLayer {
                                rotationZ = diceAnimations[index].first
                                scaleX = diceAnimations[index].second
                                scaleY = diceAnimations[index].second
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        MultiDiceWithDots(
                            value = value,
                            size = diceSize * 0.8f,
                            dotColor = if (heldDice[index]) Color.White else Color(0xFF2D3748)
                        )
                    }
                }
            }
        }
    }
}