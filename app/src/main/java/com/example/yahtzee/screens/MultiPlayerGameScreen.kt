package com.example.yahtzee.screens

import com.example.yahtzee.screens.components.GameControlButtons
import com.example.yahtzee.screens.components.HomeButton
import com.example.yahtzee.screens.components.MultiDiceRow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.viewmodel.MultiplayerGameViewModel
import com.example.yahtzee.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MultiplayerGameScreen(navController: NavController, isDarkTheme: Boolean, shakeTrigger: Int = 0) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val scaleFactor = remember {
        (screenWidth / 360.dp).coerceIn(0.85f, 1.2f)
    }

    val isCompactScreen = screenHeight < 600.dp

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

    // Colori dinamici per dark/light mode
    val cardBackground = if (isDarkTheme) BothCardDark else BothCardLight
    val tableBackground = if (isDarkTheme) TableDark else TableLight
    val titleColor = mainTextColor(isDarkTheme)
    val dividerColor = if (isDarkTheme) CardLight else CardDark

    MultiPlayerTheme(isPlayerOne = state.isPlayer1Turn) {
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(stringResource(id = R.string.dialog_title), color = titleColor, style = Typography.titleMedium) },
                text = { Text(stringResource(id = R.string.dialog_reset_text), color = titleColor, style = Typography.bodyMedium) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetGame()
                        showResetDialog = false
                    }) { Text(stringResource(id = R.string.confirm), color = titleColor, style = Typography.labelLarge) }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text(stringResource(id = R.string.cancel), color = titleColor, style = Typography.labelLarge) }
                },
                containerColor = cardBackground
            )
        }

        if (showHomeDialog) {
            AlertDialog(
                onDismissRequest = { showHomeDialog = false },
                title = { Text(stringResource(id = R.string.dialog_title), color = titleColor, style = Typography.titleMedium) },
                text = { Text(stringResource(id = R.string.dialog_home_text), color = titleColor, style = Typography.bodyMedium) },
                confirmButton = {
                    TextButton(onClick = {
                        showHomeDialog = false
                        navController.navigate("homepage") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text(stringResource(id = R.string.confirm), color = titleColor, style = Typography.labelLarge)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHomeDialog = false }) {
                        Text(stringResource(id = R.string.cancel), color = titleColor, style = Typography.labelLarge)
                    }
                },
                containerColor = cardBackground
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.sfondo_generale),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = headerPadding.coerceAtLeast(16.dp).coerceAtMost(40.dp),
                        end = (screenWidth * 0.04f).coerceAtLeast(12.dp)
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
                            containerColor = cardBackground
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
                            containerColor = tableBackground
                        ),
                        shape = RoundedCornerShape((14 * scaleFactor).dp)
                    ) {
                        Column {
                            // HEADER
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
                                        text = stringResource(id = R.string.combination),
                                        modifier = Modifier.weight(2f),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = (13 * scaleFactor).sp,
                                        style = Typography.titleSmall
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
                                            fontSize = (13 * scaleFactor).sp,
                                            style = Typography.titleSmall
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
                                            fontSize = (13 * scaleFactor).sp,
                                            style = Typography.titleSmall
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
                                            color = dividerColor
                                        )
                                    }
                                    when (combination) {
                                        "Bonus" -> {
                                            MultiplayerTableRow(
                                                combination = stringResource(id = R.string.bonus) + " ($progressBonusText1 | $progressBonusText2)",
                                                player1Score = bonus1,
                                                player2Score = bonus2,
                                                bold = true,
                                                alternate = index % 2 == 1,
                                                isPlayer1Turn = state.isPlayer1Turn,
                                                fontSize = (14 * scaleFactor).sp,
                                                compactPadding = true,
                                                scaleFactor = scaleFactor,
                                                isDarkTheme = isDarkTheme
                                            )
                                        }
                                        "Total" -> {
                                            MultiplayerTableRow(
                                                combination = stringResource(id = R.string.total_score),
                                                player1Score = totalScore1,
                                                player2Score = totalScore2,
                                                bold = true,
                                                alternate = index % 2 == 1,
                                                isPlayer1Turn = state.isPlayer1Turn,
                                                fontSize = (16 * scaleFactor).sp,
                                                compactPadding = true,
                                                scaleFactor = scaleFactor,
                                                isDarkTheme = isDarkTheme
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
                                                alternate = index % 2 == 1,
                                                isPlayer1Turn = state.isPlayer1Turn,
                                                fontSize = (13 * scaleFactor).sp,
                                                compactPadding = true,
                                                scaleFactor = scaleFactor,
                                                isDarkTheme = isDarkTheme
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
                                containerColor = cardBackground
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
                                    stringResource(id = R.string.player1) -> Color(0xFF4ECDC4)
                                    stringResource(id = R.string.player2) -> Color(0xFFFF6B6B)
                                    else -> Color(0xFF764BA2)
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
                                        color = winnerColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = Typography.titleLarge
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
                                        color = winnerColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = Typography.titleLarge
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
                                                            stringResource(id = R.string.player1) -> listOf(Color(0xFF4ECDC4), Color(0xFF2AB7CA))
                                                            stringResource(id = R.string.player2) -> listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
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
                                                    fontWeight = FontWeight.Bold,
                                                    style = Typography.labelLarge
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
    alternate: Boolean = false,
    isPlayer1Turn: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    compactPadding: Boolean = false,
    scaleFactor: Float = 1f,
    isDarkTheme: Boolean = false
) {
    val backgroundColor = when {
        enabled -> if (isDarkTheme) Color(0xFF2C2F34) else Color(0xFFF0F9FF)
        alternate -> if (isDarkTheme) Color(0xFF23272E) else Color(0xFFFAFAFA)
        else -> Color.Transparent
    }

    val textColor = when {
        enabled -> if (isDarkTheme) Color.White else Color(0xFF1E40AF)
        bold -> mainTextColor(isDarkTheme)
        else -> if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF4A5568)
    }

    val player1Color = Color(0xFF4ECDC4)
    val player2Color = Color(0xFFFF6B6B)

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
                            if (isDarkTheme) Color(0xFF4ECDC4) else Color(0xFF3B82F6),
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
                fontSize = fontSize,
                style = if (bold) Typography.titleMedium else Typography.bodyMedium
            )
            Text(
                text = player1Score?.toString() ?: previewScore.takeIf { isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = player1TextColor,
                fontSize = fontSize,
                style = Typography.bodyMedium
            )
            Text(
                text = player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = player2TextColor,
                fontSize = fontSize,
                style = Typography.bodyMedium
            )
        }
    }
}