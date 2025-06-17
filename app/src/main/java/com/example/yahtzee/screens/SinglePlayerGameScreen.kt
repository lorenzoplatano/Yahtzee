package com.example.yahtzee.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.ui.theme.SinglePlayerTheme
import com.example.yahtzee.viewmodel.SinglePlayerGameViewModel
import com.example.yahtzee.components.DiceWithDots
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun SinglePlayerGameScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val isCompactScreen = screenHeight < 600.dp
    val diceAreaWidth = screenWidth * 0.9f - 32.dp
    val diceSize = (diceAreaWidth / 5f).coerceAtMost(56.dp).coerceAtLeast(36.dp)
    val tableRowFontSize = if (isCompactScreen) 15.sp else 18.sp

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
    val coroutineScope = rememberCoroutineScope()

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

    fun startNewGameDirectly() {
        viewModel.resetGame()
    }

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

        var showHomeDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // SFONDO UGUALE AL MULTIPLAYER
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

            // Scrollabile per sicurezza su schermi piccoli
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Icona Home identica al multiplayer, più in basso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp) // più in alto rispetto a prima
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 16.dp)
                            .size(44.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .zIndex(1f)
                            .clickable { showHomeDialog = true },
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
                }
                Spacer(modifier = Modifier.height(8.dp))

                // DADI
                if (!state.gameEnded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(diceSize + 24.dp)
                            .padding(horizontal = 4.dp)
                            .offset(y = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            state.diceValues.forEachIndexed { index, value ->
                                Card(
                                    modifier = Modifier
                                        .size(diceSize)
                                        .shadow(
                                            elevation = if (state.heldDice[index]) 8.dp else 4.dp,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(
                                            enabled = state.remainingRolls < 3 && !state.gameEnded,
                                            onClick = { viewModel.toggleHold(index) }
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = if (state.heldDice[index] && value != null) {
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
                                                    )
                                                } else {
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFFF7FAFC), Color(0xFFEDF2F7))
                                                    )
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = if (state.heldDice[index] && value != null) 2.dp else 1.dp,
                                                color = if (state.heldDice[index] && value != null) Color.White else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (value != null) {
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
                                                DiceWithDots(
                                                    value = value,
                                                    size = diceSize * 0.8f,
                                                    dotColor = if (state.heldDice[index])
                                                        Color.White
                                                    else
                                                        Color(0xFF2D3748)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // TABELLA
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 500.dp, max = 650.dp) // più compatta e più in alto
                        .padding(horizontal = 8.dp)
                        .offset(y = 4.dp)
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
                                .padding(horizontal = 10.dp, vertical = 12.dp)
                        ) {
                            Row {
                                Text(
                                    text = stringResource(R.string.combination),
                                    modifier = Modifier.weight(2f),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = tableRowFontSize
                                )
                                Text(
                                    text = stringResource(R.string.score),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = tableRowFontSize
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            allCombinations.forEachIndexed { index, combination ->
                                if (index != 0) {
                                    HorizontalDivider(
                                        thickness = 0.3.dp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                                val currentScore = state.scoreMap[combination]
                                val previewScore = if (showPreviews) previewScores[combination] else null
                                val isEnabled = state.canSelectScore && currentScore == null && !state.gameEnded
                                MultiplayerSingleTableRowStyled(
                                    combination = combinationLabels[combination] ?: combination,
                                    score = currentScore,
                                    previewScore = previewScore,
                                    enabled = isEnabled,
                                    onClick = { if (isEnabled) viewModel.selectScore(combination) },
                                    bold = false,
                                    alternate = index % 2 == 1,
                                    fontSize = tableRowFontSize,
                                    compactPadding = true,
                                    selected = currentScore != null
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 1.dp,
                                color = Color(0xFF667EEA)
                            )
                            MultiplayerSingleTableRowStyled(
                                combination = "Bonus ($progressBonusText)",
                                score = bonus,
                                bold = true,
                                alternate = false,
                                fontSize = tableRowFontSize,
                                compactPadding = true
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 1.dp,
                                color = Color(0xFF667EEA)
                            )
                            MultiplayerSingleTableRowStyled(
                                combination = "TOTAL",
                                score = totalScore,
                                bold = true,
                                alternate = false,
                                fontSize = tableRowFontSize,
                                compactPadding = true
                            )
                        }
                    }
                }

                // Bottoni subito dopo la tabella, più in alto
                if (!state.gameEnded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .shadow(
                                            elevation = if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) 8.dp else 4.dp,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(
                                            enabled = state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld,
                                            onClick = { rollDiceWithAnimation() }
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) {
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
                                                    )
                                                } else {
                                                    Brush.horizontalGradient(
                                                        listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.3f))
                                                    )
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Roll",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = stringResource(R.string.roll, state.remainingRolls),
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showResetDialog = true },
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53))
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reset",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = stringResource(R.string.reset),
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (state.gameEnded) {
                    Spacer(modifier = Modifier.height(10.dp))
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
                                text = stringResource(R.string.game_ended),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53E3E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.final_score, totalScore),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )
                            if (viewModel.isNewHighScore) {
                                Text(
                                    text = stringResource(R.string.new_record),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFC107),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { startNewGameDirectly() },
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                Text(stringResource(R.string.new_game), fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Spacer finale per evitare che i bottoni tocchino la navigation bar
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (showHomeDialog) {
                AlertDialog(
                    onDismissRequest = { showHomeDialog = false },
                    title = { Text(stringResource(id = R.string.dialog_home_title)) },
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
        }
    }
}

@Composable
fun MultiplayerSingleTableRowStyled(
    combination: String,
    score: Int?,
    previewScore: Int? = null,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
    bold: Boolean = false,
    alternate: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    compactPadding: Boolean = false,
    selected: Boolean = false
) {
    val backgroundColor = when {
        enabled -> Color(0xFFF0F9FF)
        selected -> Color(0xFFE0F7FA)
        alternate -> Color(0xFFFAFAFA)
        else -> Color.Transparent
    }

    val textColor = when {
        enabled -> Color(0xFF1E40AF)
        bold -> Color(0xFF1A1A1A)
        else -> Color(0xFF4A5568)
    }

    val verticalPadding = if (compactPadding) 6.dp else 10.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 1.dp)
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
                .padding(horizontal = 12.dp, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = combination,
                modifier = Modifier.weight(2f),
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = fontSize
            )
            Text(
                text = score?.toString() ?: previewScore?.toString() ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = fontSize
            )
        }
    }
}