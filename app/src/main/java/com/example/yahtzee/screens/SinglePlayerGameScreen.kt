package com.example.yahtzee.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


@Composable
fun SinglePlayerGameScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val isCompactScreen = screenHeight < 600.dp

    // Calcola dimensioni responsive senza scrolling
    val diceSize = (screenWidth / 6).coerceAtLeast(40.dp).coerceAtMost(60.dp)

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

    // Stato per gestire la visibilità delle preview
    var showPreviews by remember { mutableStateOf(true) }

    // Verifica se tutti i dadi sono selezionati e non sono nulli
    val allDiceHeld = state.diceValues.filterNotNull().isNotEmpty() &&
            state.diceValues.filterIndexed { idx, value ->
                value != null && state.heldDice[idx]
            }.size == state.diceValues.filterNotNull().size

    // Lista delle chiavi costanti per la logica (in inglese)
    val allCombinations = listOf(
        "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "Three of a Kind", "Four of a Kind", "Full House",
        "Small Straight", "Large Straight", "Yahtzee", "Chance"
    )

    // Mappatura per la visualizzazione (da inglese a localizzato)
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

    // DICHIARAZIONE VARIABILI GLOBALI PER LA SCHERMATA
    val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
    val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
    val bonus = if (upperSum >= 63) 35 else 0
    val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus

    // Calcolo del progresso per il bonus
    val progressBonusText = "${upperSum.coerceAtMost(63)}/63"

    // Stato per l'animazione dei dadi
    var isRolling by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Lista di animazioni per ogni dado - ora per i contenuti interni
    val diceAnimations = List(5) { index ->
        val randomEndRotation = remember(isRolling) { (-720..720).random().toFloat() }

        val rotation by animateFloatAsState(
            targetValue = if (isRolling && !state.heldDice[index]) randomEndRotation else 0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "diceRotation"
        )

        // Animazione aggiuntiva per l'effetto tornado - scala interna che pulsa
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

    // Funzione per avviare l'animazione di lancio dei dadi
    fun rollDiceWithAnimation() {
        if (state.remainingRolls > 0 && !state.gameEnded && !allDiceHeld) {
            // Disabilita completamente le preview durante tutta la sequenza di animazione
            showPreviews = false

            // Avvia la sequenza di animazione
            coroutineScope.launch {
                isRolling = true

                delay(550)

                viewModel.rollDice()
                isRolling = false
                delay(800)

                showPreviews = true
            }
        }
    }

    // Funzione per avviare direttamente una nuova partita senza dialog
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
                .systemBarsPadding()
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
                    .clickable { showHomeDialog = true }
            )

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 60.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 84.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Dadi
                if (!state.gameEnded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        state.diceValues.forEachIndexed { index, value ->
                            Box(
                                modifier = Modifier
                                    .size(diceSize)
                                    .background(
                                        if (state.heldDice[index] && value != null)
                                            MaterialTheme.colorScheme.secondary
                                        else
                                            MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = if (state.heldDice[index] && value != null) 2.dp else 1.dp,
                                        color = if (state.heldDice[index] && value != null)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(
                                        enabled = state.remainingRolls < 3 && value != null && !isRolling
                                    ) {
                                        viewModel.toggleHold(index)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Mostra i punti del dado solo se non è nullo
                                // Ora l'animazione è applicata al contenuto interno
                                if (value != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(0.8f) // Lascia un po' di spazio intorno
                                            .graphicsLayer {
                                                // Applica l'animazione solo al contenuto
                                                rotationZ = diceAnimations[index].first
                                                scaleX = diceAnimations[index].second
                                                scaleY = diceAnimations[index].second
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        DiceWithDots(
                                            value = value,
                                            size = diceSize * 0.8f, // Ridotto leggermente per far posto all'animazione
                                            dotColor = if (state.heldDice[index])
                                                MaterialTheme.colorScheme.onSecondary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tabella punteggi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column {
                        // Header della tabella
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.combination),
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = tableRowFontSize
                            )
                            Text(
                                text = stringResource(R.string.score),
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
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                ),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            allCombinations.forEachIndexed { index, combination ->
                                if (index != 0) {
                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                }
                                TableRow(
                                    combination = combinationLabels[combination] ?: combination, // Usa la mappatura per il testo
                                    currentScore = state.scoreMap[combination],
                                    // Mostra le preview solo quando showPreviews è true
                                    previewScore = if (showPreviews) previewScores[combination] else null,
                                    onClick = { viewModel.selectScore(combination) },
                                    enabled = state.canSelectScore && state.scoreMap[combination] == null && !state.gameEnded,
                                    alternate = index % 2 == 1,
                                    fontSize = tableRowFontSize,
                                    compactPadding = true,
                                    selected = state.scoreMap[combination] != null
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 1.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            // Usiamo TableRow ma con un trucco per mostrare anche il progresso
                            TableRow(
                                combination = stringResource(R.string.bonus),
                                currentScore = bonus,
                                previewScore = null,
                                extraInfo = progressBonusText,  // Aggiungiamo il parametro per il progresso
                                onClick = {},
                                bold = true,
                                fontSize = tableRowFontSize,
                                compactPadding = true,
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 1.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableRow(
                                combination = stringResource(R.string.total_score),
                                currentScore = totalScore,
                                previewScore = null,
                                onClick = {},
                                bold = true,
                                fontSize = tableRowFontSize,
                                compactPadding = true,
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                if (state.gameEnded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.game_ended),
                        fontSize = tableRowFontSize.times(1.2f),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.final_score, totalScore),
                        fontSize = tableRowFontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Mostra il messaggio NUOVO RECORD! se appropriato
                    if (viewModel.isNewHighScore) {
                        Text(
                            text = stringResource(R.string.new_record),
                            fontSize = tableRowFontSize.times(1.3f),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFC107), // Colore oro
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        )
                    }

                    Button(
                        onClick = { startNewGameDirectly() }, // Usa la nuova funzione che avvia direttamente una nuova partita
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .height(40.dp)
                            .fillMaxWidth(0.7f)
                    ) {
                        Text(stringResource(R.string.new_game), fontSize = tableRowFontSize)
                    }
                }
            }

            // Bottoni in basso - mostrati solo se la partita NON è terminata
            if (!state.gameEnded) {
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
                            onClick = { rollDiceWithAnimation() },
                            enabled = state.remainingRolls > 0 && !allDiceHeld && !isRolling,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(44.dp)
                        ) {
                            Text(
                                stringResource(R.string.roll, state.remainingRolls),
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
                                stringResource(R.string.reset),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = tableRowFontSize.times(0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable che rappresenta un dado con i punti come in un dado reale.
 */
@Composable
fun DiceWithDots(
    value: Int,
    size: androidx.compose.ui.unit.Dp,
    dotColor: Color = Color.Black
) {
    val dotSize = size / 5
    val padding = size / 5

    Canvas(
        modifier = Modifier.size(size)
    ) {
        // Calcola le coordinate per posizionare i punti
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val topLeft = Offset(padding.toPx(), padding.toPx())
        val topRight = Offset(this.size.width - padding.toPx(), padding.toPx())
        val centerLeft = Offset(padding.toPx(), center.y)
        val centerRight = Offset(this.size.width - padding.toPx(), center.y)
        val bottomLeft = Offset(padding.toPx(), this.size.height - padding.toPx())
        val bottomRight = Offset(this.size.width - padding.toPx(), this.size.height - padding.toPx())

        // Disegna i punti in base al valore del dado
        when (value) {
            1 -> {
                // Solo il punto centrale
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = center,
                    style = Fill
                )
            }
            2 -> {
                // Punti in alto a destra e in basso a sinistra
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
                // Punti in alto a destra, al centro e in basso a sinistra
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
                // Punti ai quattro angoli
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
                // Punti ai quattro angoli e al centro
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
                // Sei punti, tre su ciascun lato
                drawCircle(
                    color = dotColor,
                    radius = dotSize.toPx() / 2,
                    center = topLeft,
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
                    center = bottomLeft,
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
                    center = centerRight,
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
fun TableRow(
    combination: String,
    currentScore: Int?,
    previewScore: Int? = null,
    extraInfo: String? = null,  // Nuovo parametro per mostrare informazioni extra
    onClick: () -> Unit,
    enabled: Boolean = false,
    header: Boolean = false,
    bold: Boolean = false,
    alternate: Boolean = false,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    compactPadding: Boolean = false,
    selected: Boolean = false,
    backgroundColor: Color? = null,  // Permette di sovrascrivere il colore di sfondo
    textColor: Color? = null  // Permette di sovrascrivere il colore del testo
) {
    val bgColor = backgroundColor ?: when {
        header -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.primaryContainer
        selected -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
        alternate -> MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        else -> MaterialTheme.colorScheme.surface
    }

    val txtColor = textColor ?: when {
        header -> MaterialTheme.colorScheme.onPrimary
        enabled -> MaterialTheme.colorScheme.onPrimaryContainer
        selected -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

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
                        .background(bgColor)
                else
                    Modifier.background(bgColor)
            )
            .then(
                if (enabled) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)) else Modifier
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(rowPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = combination,
                fontWeight = if (bold || header || selected) FontWeight.Bold else FontWeight.Normal,
                color = txtColor,
                fontSize = fontSize,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            // Mostra il testo extra se presente
            extraInfo?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Normal,
                    color = txtColor.copy(alpha = 0.7f),
                    fontSize = fontSize.times(0.75f),
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Text(
            text = currentScore?.toString()
                ?: previewScore?.toString()
                ?: if (header) "SCORE" else "",
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold || header || selected) FontWeight.Bold else FontWeight.Normal,
            color = txtColor,
            fontSize = fontSize,
            maxLines = 1
        )
    }
}