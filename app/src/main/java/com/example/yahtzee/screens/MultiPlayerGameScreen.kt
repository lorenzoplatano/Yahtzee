package com.example.yahtzee.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.screens.components.GameControlButtons
import com.example.yahtzee.screens.components.HomeButton
import com.example.yahtzee.screens.components.MultiDiceRow
import com.example.yahtzee.ui.theme.Typography
import com.example.yahtzee.ui.theme.violaceo
import com.example.yahtzee.ui.theme.blu_chiaro
import com.example.yahtzee.ui.theme.verde_acqua
import com.example.yahtzee.ui.theme.verde_azzurro
import com.example.yahtzee.ui.theme.arancio_rosso
import com.example.yahtzee.ui.theme.arancione
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MultiplayerGameScreen(
    navController: NavController,
    shakeTrigger: Int = 0,
    viewModel: MultiplayerGameViewModel  // ✅ Aggiungi questo parametro
) {
    val colorScheme = MaterialTheme.colorScheme

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val scaleFactor = remember {
        (screenWidth / 360.dp).coerceIn(0.85f, 1.2f)
    }

    val isCompactScreen = screenHeight < 700.dp

    val diceAreaWidth = screenWidth * 0.9f - 32.dp
    val diceSize = (diceAreaWidth / 5f).coerceAtMost(56.dp).coerceAtLeast(36.dp)
    val headerPadding = screenHeight * 0.05f
    val bottomAreaHeight = screenHeight * 0.1f

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
    var animationDone by remember { mutableStateOf(true) }

    fun rollDiceWithAnimation() {
        if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) {
            coroutineScope.launch {
                isRolling = true
                animationDone = false
                showPreviews = false
                delay(800)
                viewModel.rollDice()
                delay(200)
                isRolling = false
                animationDone = true
                showPreviews = true
            }
        }
    }

    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            rollDiceWithAnimation()
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(id = R.string.dialog_title), color = colorScheme.onSurface, style = Typography.titleMedium) },
            text = { Text(stringResource(id = R.string.dialog_reset_text), color = colorScheme.onSurface, style = Typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetGame()
                    showResetDialog = false
                }) { Text(stringResource(id = R.string.confirm), color = colorScheme.onSurface, style = Typography.labelLarge) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text(stringResource(id = R.string.cancel), color = colorScheme.onSurface, style = Typography.labelLarge) }
            },
            containerColor = colorScheme.surface
        )
    }

    if (showHomeDialog) {
        AlertDialog(
            onDismissRequest = { showHomeDialog = false },
            title = { Text(stringResource(id = R.string.dialog_title), color = colorScheme.onSurface, style = Typography.titleMedium) },
            text = { Text(stringResource(id = R.string.dialog_home_text), color = colorScheme.onSurface, style = Typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showHomeDialog = false
                    navController.navigate("homepage") {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text(stringResource(id = R.string.confirm), color = colorScheme.onSurface, style = Typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHomeDialog = false }) {
                    Text(stringResource(id = R.string.cancel), color = colorScheme.onSurface, style = Typography.labelLarge)
                }
            },
            containerColor = colorScheme.surface
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.sfondo_generale),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = headerPadding.coerceAtLeast(12.dp).coerceAtMost(40.dp),
                    start = (screenWidth * 0.05f).coerceAtLeast(16.dp)
                )
        ) {
            HomeButton(
                onClick = {
                    if (state.gameEnded) {
                        navController.navigate("homepage")
                    } else {
                        showHomeDialog = true
                    }
                },
                scaleFactor = scaleFactor
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = headerPadding.coerceAtLeast(16.dp).coerceAtMost(40.dp),
                    end = (screenWidth * 0.05f).coerceAtLeast(16.dp)
                )
        ) {
            SettingsButton(
                onClick = { navController.navigate("settings") },
                scaleFactor = scaleFactor
            )
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
            // Card unica per i dadi
            if (!state.gameEnded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height((diceSize + 24.dp).coerceAtMost(screenHeight * 0.12f))
                        .padding(horizontal = (4 * scaleFactor).dp)
                        .offset(y = (8 * scaleFactor).dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    shape = RoundedCornerShape((12 * scaleFactor).dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    MultiDiceRow(
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
                    screenHeight * 0.50f
                } else {
                    screenHeight * 0.62f
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .heightIn(max = tableMaxHeight)
                        .padding(horizontal = (screenWidth * 0.02f).coerceAtLeast(8.dp))
                        .offset(y = (8 * scaleFactor).dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape((14 * scaleFactor).dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    shape = RoundedCornerShape((14 * scaleFactor).dp)
                ) {
                    Column {
                        // HEADER con colori HomeButton
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(violaceo, blu_chiaro)
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = (if (isCompactScreen) (10 * scaleFactor) else (14 * scaleFactor)).dp,
                                        topEnd = (if (isCompactScreen) (10 * scaleFactor) else (14 * scaleFactor)).dp
                                    ))
                                .padding(
                                    horizontal = (if (isCompactScreen) (8 * scaleFactor) else (10 * scaleFactor)).dp,
                                    vertical = (if (isCompactScreen) (6 * scaleFactor) else (10 * scaleFactor)).dp
                                )
                        ) {
                            Row {
                                Text(
                                    text = stringResource(R.string.combination),
                                    modifier = Modifier.weight(2f),
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onPrimary,
                                    fontSize = (if (isCompactScreen) (11 * scaleFactor) else (13 * scaleFactor)).sp,
                                )
                                // Player 1 header
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
                                        fontSize = (if (isCompactScreen) (11 * scaleFactor) else (13 * scaleFactor)).sp,
                                        )
                                }
                                // Player 2 header
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
                                        fontSize = (if (isCompactScreen) (11 * scaleFactor) else (13 * scaleFactor)).sp,
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
                                when (combination) {
                                    "Bonus" -> {
                                        MultiplayerTableRow(
                                            combination = stringResource(id = R.string.bonus) + " ($progressBonusText1 | $progressBonusText2)",
                                            player1Score = bonus1,
                                            player2Score = bonus2,
                                            bold = true,
                                            isPlayer1Turn = state.isPlayer1Turn,
                                            fontSize = (if (isCompactScreen) (11 * scaleFactor) else (13 * scaleFactor)).sp,
                                            scaleFactor = scaleFactor,
                                            isCompactScreen = isCompactScreen,
                                            colorScheme = colorScheme
                                        )
                                    }
                                    "Total" -> {
                                        MultiplayerTableRow(
                                            combination = stringResource(id = R.string.total_score),
                                            player1Score = totalScore1,
                                            player2Score = totalScore2,
                                            bold = true,
                                            isPlayer1Turn = state.isPlayer1Turn,
                                            fontSize = (if (isCompactScreen) (11 * scaleFactor) else (13 * scaleFactor)).sp,
                                            scaleFactor = scaleFactor,
                                            isCompactScreen = isCompactScreen,
                                            colorScheme = colorScheme
                                        )
                                    }
                                    else -> {
                                        val player1Score = state.scoreMapPlayer1[combination]
                                        val player2Score = state.scoreMapPlayer2[combination]
                                        val previewScore = if (showPreviews && animationDone) previewScores[combination] else null
                                        val isEnabled = !state.gameEnded &&
                                                state.hasRolledAtLeastOnce &&
                                                showPreviews && animationDone &&
                                                ((state.isPlayer1Turn && player1Score == null) || (!state.isPlayer1Turn && player2Score == null))
                                        MultiplayerTableRow(
                                            combination = combinationLabels[combination] ?: combination,
                                            player1Score = player1Score,
                                            player2Score = player2Score,
                                            previewScore = previewScore,
                                            enabled = isEnabled,
                                            onClick = { if (isEnabled) viewModel.selectScore(combination) },
                                            bold = false,
                                            isPlayer1Turn = state.isPlayer1Turn,
                                            fontSize = (if (isCompactScreen) (10 * scaleFactor) else (12 * scaleFactor)).sp,
                                            scaleFactor = scaleFactor,
                                            isCompactScreen = isCompactScreen,
                                            colorScheme = colorScheme
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // CARD FINE PARTITA
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
                            containerColor = colorScheme.surface
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
                            val winnerColor = when (winner) {
                                stringResource(id = R.string.player1) -> verde_acqua
                                stringResource(id = R.string.player2) -> arancio_rosso
                                else -> colorScheme.tertiary
                            }
                            if (winner != null) {
                                Text(
                                    text = stringResource(
                                        id = R.string.win_message,
                                        winner,
                                        totalScore1,
                                        totalScore2
                                    ),
                                    color = winnerColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = Typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = stringResource(
                                        id = R.string.pareggio,
                                        totalScore1,
                                        totalScore2
                                    ),
                                    color = winnerColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = Typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height((24 * scaleFactor).dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
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
                                                        stringResource(id = R.string.player1) -> listOf(verde_acqua, verde_azzurro)
                                                        stringResource(id = R.string.player2) -> listOf(arancio_rosso, arancione)
                                                        else -> listOf(violaceo, blu_chiaro)
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
                                                style = Typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
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

        if (!state.gameEnded) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                GameControlButtons(
                    onRollClick = { rollDiceWithAnimation() },
                    onResetClick = { showResetDialog = true },
                    remainingRolls = state.remainingRolls,
                    allDiceHeld = allDiceHeld,
                    isGameEnded = state.gameEnded,
                    scaleFactor = scaleFactor,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
            }
        }
    }
}

@Composable
fun MultiplayerTableRow(
    combination: String,
    player1Score: Int?,
    player2Score: Int?,
    previewScore: Int? = null,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    bold: Boolean = false,
    isPlayer1Turn: Boolean = true,
    fontSize: TextUnit,
    scaleFactor: Float = 1f,
    isCompactScreen: Boolean = false,
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
    val backgroundColor = colorScheme.surface

    val textColor = when {
        enabled -> colorScheme.primary
        bold -> colorScheme.onSurface
        else -> colorScheme.onSurfaceVariant
    }

    // Colori dei punteggi come i pulsanti di controllo
    val player1Color = verde_acqua // Colore del pulsante lancia
    val player2Color = arancio_rosso // Colore del pulsante reset

    val player1TextColor = when {
        player1Score != null -> player1Color
        previewScore != null && isPlayer1Turn -> textColor
        else -> textColor
    }
    val player2TextColor = when {
        player2Score != null -> player2Color
        previewScore != null && !isPlayer1Turn -> textColor
        else -> textColor
    }

    val horizontalPadding = (8 * scaleFactor).dp
    val verticalPadding = (if (isCompactScreen) (2.5 * scaleFactor) else (5.5 * scaleFactor)).dp

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
                            colorScheme.primary,
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
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = combination,
                modifier = Modifier.weight(2f),
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = colorScheme.onSurface,
                fontSize = fontSize
            )
            Text(
                text = player1Score?.toString() ?: previewScore.takeIf { isPlayer1Turn }?.toString()
                ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = player1TextColor,
                fontSize = fontSize
            )
            Text(
                text = player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }
                    ?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = player2TextColor,
                fontSize = fontSize
            )
        }
    }
}
@Composable
fun SettingsButton(
    onClick: () -> Unit,
    scaleFactor: Float,
) {
    Card(
        modifier = Modifier
            .size((44 * scaleFactor).dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(violaceo, blu_chiaro)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .size((44 * scaleFactor).dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size((22 * scaleFactor).dp)
            )
        }
    }
}