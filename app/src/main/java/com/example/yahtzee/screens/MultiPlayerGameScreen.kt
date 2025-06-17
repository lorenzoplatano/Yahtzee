package com.example.yahtzee.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
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
import com.example.yahtzee.components.DiceWithDots
import com.example.yahtzee.components.GameDiceRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MultiplayerGameScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val isCompactScreen = screenHeight < 600.dp
    val diceSize = (screenWidth / 5.5f).coerceAtLeast(45.dp).coerceAtMost(65.dp)
    val tableRowFontSize = if (isCompactScreen) 12.sp else 13.sp

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
        "Aces" to "Aces",
        "Twos" to "Twos",
        "Threes" to "Threes",
        "Fours" to "Fours",
        "Fives" to "Fives",
        "Sixes" to "Sixes",
        "Three of a Kind" to "Three of a Kind",
        "Four of a Kind" to "Four of a Kind",
        "Full House" to "Full House",
        "Small Straight" to "Small Straight",
        "Large Straight" to "Large Straight",
        "Yahtzee" to "Yahtzee",
        "Chance" to "Chance"
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
    val coroutineScope = rememberCoroutineScope()

    // ANIMAZIONE DADI
    val diceAnimations = List(5) { index ->
        val randomEndRotation = remember(isRolling) { (-720..720).random().toFloat() }
        val rotation by animateFloatAsState(
            targetValue = if (isRolling && !state.heldDice[index]) randomEndRotation else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "diceRotation"
        )
        val scale by animateFloatAsState(
            targetValue = if (isRolling && !state.heldDice[index]) 0.8f else 1f,
            animationSpec = repeatable(
                iterations = 2,
                animation = tween(250, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "diceScale"
        )
        Pair(rotation, scale)
    }

    fun rollDiceWithAnimation() {
        if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) {
            coroutineScope.launch {
                isRolling = true
                showPreviews = false
                delay(550)
                viewModel.rollDice()
                delay(100)
                isRolling = false
                showPreviews = true
            }
        }
    }

    MultiPlayerTheme(isPlayerOne = state.isPlayer1Turn) {
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

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.chunky),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 60.dp, end = 16.dp)
                    .size(44.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .zIndex(1f)
                    .clickable { navController.navigate("homepage") },
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
                        contentDescription = "Home",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, start = 8.dp, end = 8.dp, bottom = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(85.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "YAHTZEE - Multiplayer",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = when {
                                state.gameEnded -> "Partita Terminata"
                                state.isPlayer1Turn -> "Turno: Player 1"
                                else -> "Turno: Player 2"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (state.gameEnded) Color(0xFFE53E3E) else Color(0xFF4A5568),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (!state.gameEnded) {
                    GameDiceRow(
                        diceValues = state.diceValues,
                        heldDice = state.heldDice,
                        onDiceClick = { index -> viewModel.toggleHold(index) },
                        enabled = state.remainingRolls < 3 && !state.gameEnded,
                        diceSize = diceSize,
                        isRolling = isRolling,
                        diceAnimations = diceAnimations
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.96f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                    ),
                                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        ) {
                            Row {
                                Text(
                                    text = "COMBINATION",
                                    modifier = Modifier.weight(2f),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Player 1",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Player 2",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            combinationKeys.forEachIndexed { index, combination ->
                                if (index != 0) {
                                    HorizontalDivider(
                                        thickness = 0.3.dp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                                val player1Score = state.scoreMapPlayer1[combination]
                                val player2Score = state.scoreMapPlayer2[combination]
                                val previewScore = if (showPreviews) previewScores[combination] else null
                                val isEnabled = !state.gameEnded &&
                                        state.hasRolledAtLeastOnce &&
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
                                    fontSize = tableRowFontSize,
                                    compactPadding = isCompactScreen
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 1.dp,
                                color = Color(0xFF667EEA)
                            )
                            MultiplayerTableRowStyled(
                                combination = "Bonus ($progressBonusText1 | $progressBonusText2)",
                                player1Score = bonus1,
                                player2Score = bonus2,
                                bold = true,
                                alternate = false,
                                isPlayer1Turn = state.isPlayer1Turn,
                                fontSize = tableRowFontSize,
                                compactPadding = isCompactScreen
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 1.dp,
                                color = Color(0xFF667EEA)
                            )
                            MultiplayerTableRowStyled(
                                combination = "TOTAL",
                                player1Score = totalScore1,
                                player2Score = totalScore2,
                                bold = true,
                                alternate = false,
                                isPlayer1Turn = state.isPlayer1Turn,
                                fontSize = tableRowFontSize,
                                compactPadding = isCompactScreen
                            )
                        }
                    }
                }
                if (state.gameEnded) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Partita Terminata!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53E3E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Player 1: $totalScore1 | Player 2: $totalScore2",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )
                            val winner = when {
                                totalScore1 > totalScore2 -> "Player 1 Vince!"
                                totalScore2 > totalScore1 -> "Player 2 Vince!"
                                else -> "Pareggio!"
                            }
                            Text(
                                text = winner,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ECDC4),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ModernGameActionButton(
                                text = "Nuova Partita",
                                icon = Icons.Default.Refresh,
                                onClick = { showResetDialog = true },
                                modifier = Modifier.fillMaxWidth(0.7f),
                                gradientColors = listOf(
                                    Color(0xFF4ECDC4),
                                    Color(0xFF44A08D)
                                )
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 40.dp)
                    .align(Alignment.BottomCenter)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModernGameActionButton(
                            text = "Roll (${state.remainingRolls})",
                            icon = Icons.Default.Refresh,
                            onClick = { rollDiceWithAnimation() },
                            enabled = state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld,
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44A08D)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        ModernGameActionButton(
                            text = "Reset",
                            icon = Icons.Default.Refresh,
                            onClick = { showResetDialog = true },
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFFFF8E53)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernGameActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6)
    )
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 6.dp else 3.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(gradientColors)
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.3f))
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
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
    fontSize: androidx.compose.ui.unit.TextUnit = 12.sp,
    compactPadding: Boolean = false
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

    val verticalPadding = if (compactPadding) 3.dp else 4.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 0.5.dp)
            .then(
                if (enabled) {
                    Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(6.dp))
                } else Modifier
            )
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = verticalPadding)
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
                color = textColor,
                fontSize = fontSize
            )
            Text(
                text = player2Score?.toString() ?: previewScore.takeIf { !isPlayer1Turn }?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = fontSize
            )
        }
    }
}