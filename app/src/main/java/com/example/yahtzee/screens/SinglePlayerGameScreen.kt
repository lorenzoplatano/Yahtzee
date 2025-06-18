package com.example.yahtzee.screens

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.ui.theme.SinglePlayerTheme
import com.example.yahtzee.ui.theme.yahtzeeCardColor
import com.example.yahtzee.ui.theme.yahtzeeTableColor
import com.example.yahtzee.ui.theme.yahtzeeMainTextColor
import com.example.yahtzee.ui.theme.yahtzeeDividerColor
import com.example.yahtzee.ui.theme.yahtzeeFontFamily
import com.example.yahtzee.viewmodel.SinglePlayerGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.yahtzee.screens.components.Dice
import com.example.yahtzee.screens.components.GameControlButtons
import com.example.yahtzee.screens.components.HomeButton
import com.example.yahtzee.screens.components.MultiDiceRow

@Composable
fun SinglePlayerGameScreen(navController: NavController, isDarkTheme: Boolean, shakeTrigger: Int = 0) {
    val context = LocalContext.current
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
    var showHomeDialog by remember { mutableStateOf(false) }
    val previewScores = viewModel.previewScores()
    var showPreviews by remember { mutableStateOf(true) }

    val allDiceHeld = state.diceValues.filterNotNull().isNotEmpty() &&
            state.diceValues.filterIndexed { idx, value ->
                value != null && state.heldDice[idx]
            }.size == state.diceValues.filterNotNull().size

    val allCombinations = listOf(
        "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "Three of a Kind", "Four of a Kind", "Full House",
        "Small Straight", "Large Straight", "Yahtzee", "Chance"
    )

    val combinationLabels = mapOf(
        "Aces" to stringResource(R.string.aces),
        "Twos" to stringResource(R.string.twos),
        "Threes" to stringResource(R.string.threes),
        "Fours" to stringResource(R.string.fours),
        "Fives" to stringResource(R.string.fives),
        "Sixes" to stringResource(R.string.sixes),
        "Three of a Kind" to stringResource(R.string.three_of_a_kind),
        "Four of a Kind" to stringResource(R.string.four_of_a_kind),
        "Full House" to stringResource(R.string.full_house),
        "Small Straight" to stringResource(R.string.small_straight),
        "Large Straight" to stringResource(R.string.large_straight),
        "Yahtzee" to stringResource(R.string.yahtzee),
        "Chance" to stringResource(R.string.chance)
    )

    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
    val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
    val bonus = if (upperSum >= 63) 35 else 0
    val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus
    val progressBonusText = "${upperSum.coerceAtMost(63)}/63"

    var isRolling by remember { mutableStateOf(false) }
    var animationDone by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

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

    fun startNewGameDirectly() {
        viewModel.resetGame()
    }
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            rollDiceWithAnimation()
        }
    }
    // --- COLORI E FONT CENTRALIZZATI ---
    val cardBackground = yahtzeeCardColor(isDarkTheme)
    val tableBackground = yahtzeeTableColor(isDarkTheme)
    val titleColor = yahtzeeMainTextColor(isDarkTheme)
    val dividerColor = yahtzeeDividerColor(isDarkTheme)
    val fontFamily = yahtzeeFontFamily

    SinglePlayerTheme {
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(stringResource(R.string.dialog_title), color = titleColor, fontFamily = fontFamily) },
                text = { Text(stringResource(R.string.dialog_reset_text), color = titleColor, fontFamily = fontFamily) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetGame()
                        showResetDialog = false
                    }) { Text(stringResource(R.string.confirm), color = titleColor, fontFamily = fontFamily) }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text(stringResource(R.string.cancel), color = titleColor, fontFamily = fontFamily) }
                },
                containerColor = cardBackground
            )
        }

        if (showHomeDialog) {
            AlertDialog(
                onDismissRequest = { showHomeDialog = false },
                title = { Text(stringResource(id = R.string.dialog_title), color = titleColor, fontFamily = fontFamily) },
                text = { Text(stringResource(id = R.string.dialog_home_text), color = titleColor, fontFamily = fontFamily) },
                confirmButton = {
                    TextButton(onClick = {
                        showHomeDialog = false
                        navController.navigate("homepage") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text(stringResource(id = R.string.confirm), color = titleColor, fontFamily = fontFamily)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHomeDialog = false }) {
                        Text(stringResource(id = R.string.cancel), color = titleColor, fontFamily = fontFamily)
                    }
                },
                containerColor = cardBackground
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Sfondo
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

            // Home Button in alto a destra
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
                // Dadi
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
                            diceValues = state.diceValues.map { it ?: 1 },
                            heldDice = state.heldDice,
                            onDiceClick = { index ->
                                if (state.remainingRolls < 3 && !state.gameEnded) {
                                    viewModel.toggleHold(index)
                                }
                            },
                            enabled = state.remainingRolls < 3 && !state.gameEnded,
                            diceSize = diceSize,
                            isRolling = isRolling,
                            isSinglePlayer = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height((16 * scaleFactor).dp))

                // Tabella dei punteggi
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
                            // Header tabella
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
                                        text = stringResource(R.string.combination),
                                        modifier = Modifier.weight(2f),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = (13 * scaleFactor).sp,
                                        fontFamily = fontFamily
                                    )
                                    Text(
                                        text = stringResource(R.string.score),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = (13 * scaleFactor).sp,
                                        fontFamily = fontFamily
                                    )
                                }
                            }

                            // Righe combinazioni
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = (4 * scaleFactor).dp, vertical = (2 * scaleFactor).dp)
                            ) {
                                allCombinations.forEachIndexed { index, combination ->
                                    if (index != 0) {
                                        HorizontalDivider(
                                            thickness = (0.2 * scaleFactor).dp,
                                            color = dividerColor
                                        )
                                    }

                                    val currentScore = state.scoreMap[combination]
                                    val previewScore = if (showPreviews && animationDone) previewScores[combination] else null
                                    val isEnabled = state.canSelectScore &&
                                            currentScore == null &&
                                            !state.gameEnded &&
                                            animationDone

                                    SinglePlayerTableRow(
                                        combination = combinationLabels[combination] ?: combination,
                                        score = currentScore,
                                        previewScore = previewScore,
                                        enabled = isEnabled,
                                        onClick = { if (isEnabled) viewModel.selectScore(combination) },
                                        bold = false,
                                        alternate = index % 2 == 1,
                                        fontSize = (13 * scaleFactor).sp,
                                        compactPadding = true,
                                        selected = currentScore != null,
                                        scaleFactor = scaleFactor,
                                        isDarkTheme = isDarkTheme,
                                        fontFamily = fontFamily
                                    )
                                }

                                HorizontalDivider(
                                    thickness = (0.2 * scaleFactor).dp,
                                    color = dividerColor
                                )

                                // Bonus row
                                SinglePlayerTableRow(
                                    combination = stringResource(R.string.bonus) + " ($progressBonusText)",
                                    score = bonus,
                                    bold = true,
                                    alternate = false,
                                    fontSize = (14 * scaleFactor).sp,
                                    compactPadding = true,
                                    scaleFactor = scaleFactor,
                                    isDarkTheme = isDarkTheme,
                                    fontFamily = fontFamily
                                )

                                HorizontalDivider(
                                    thickness = (0.2 * scaleFactor).dp,
                                    color = dividerColor
                                )

                                // Total row
                                SinglePlayerTableRow(
                                    combination = stringResource(R.string.total_score).uppercase(),
                                    score = totalScore,
                                    bold = true,
                                    alternate = false,
                                    fontSize = (16 * scaleFactor).sp,
                                    compactPadding = true,
                                    scaleFactor = scaleFactor,
                                    isDarkTheme = isDarkTheme,
                                    fontFamily = fontFamily
                                )
                            }
                        }
                    }
                }

                // Card fine partita centrata con stile coerente
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
                                Text(
                                    text = stringResource(R.string.final_score, totalScore),
                                    fontSize = (22 * scaleFactor).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ECDC4),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontFamily = fontFamily
                                )

                                if (viewModel.isNewHighScore) {
                                    Spacer(modifier = Modifier.height((12 * scaleFactor).dp))
                                    Text(
                                        text = stringResource(R.string.new_record),
                                        fontSize = (20 * scaleFactor).sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFFC107),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        fontFamily = fontFamily
                                    )
                                }

                                Spacer(modifier = Modifier.height((24 * scaleFactor).dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = { startNewGameDirectly() },
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
                                                        listOf(Color(0xFF4ECDC4), Color(0xFF2AB7CA)),
                                                        startX = 0f,
                                                        endX = Float.POSITIVE_INFINITY
                                                    ),
                                                    shape = RoundedCornerShape((16 * scaleFactor).dp)
                                                )
                                                .padding(vertical = (6 * scaleFactor).dp),
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
                                                    fontFamily = fontFamily
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

            // Bottoni di controllo in basso
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
fun SinglePlayerTableRow(
    combination: String,
    score: Int?,
    previewScore: Int? = null,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    bold: Boolean = false,
    alternate: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    compactPadding: Boolean = false,
    selected: Boolean = false,
    scaleFactor: Float = 1f,
    isDarkTheme: Boolean = false,
    fontFamily: androidx.compose.ui.text.font.FontFamily? = null
) {
    val backgroundColor = when {
        enabled -> if (isDarkTheme) Color(0xFF2C2F34) else Color(0xFFF0F9FF)
        selected -> if (isDarkTheme) Color(0xFF23272E) else Color(0xFFE0F7FA)
        alternate -> if (isDarkTheme) Color(0xFF23272E) else Color(0xFFFAFAFA)
        else -> Color.Transparent
    }

    val textColor = when {
        enabled -> if (isDarkTheme) Color.White else Color(0xFF1E40AF)
        bold -> yahtzeeMainTextColor(isDarkTheme)
        score != null -> Color(0xFF4ECDC4)
        else -> if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF4A5568)
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
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = combination,
                modifier = Modifier.weight(2f),
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF4A5568),
                fontSize = fontSize,
                fontFamily = fontFamily
            )
            Text(
                text = score?.toString() ?: previewScore?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = fontSize,
                fontFamily = fontFamily
            )
        }
    }
}