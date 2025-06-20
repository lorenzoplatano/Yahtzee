package com.example.yahtzee.screens.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yahtzee.R
import com.example.yahtzee.ui.theme.arancio_rosso
import com.example.yahtzee.ui.theme.arancione
import com.example.yahtzee.ui.theme.blu_chiaro
import com.example.yahtzee.ui.theme.blu_sbiadito
import com.example.yahtzee.ui.theme.perla
import com.example.yahtzee.ui.theme.perla_bianca
import com.example.yahtzee.ui.theme.perlina
import com.example.yahtzee.ui.theme.verde_acqua
import com.example.yahtzee.ui.theme.verde_azzurro
import com.example.yahtzee.ui.theme.violaceo


// Composable condivise tra singleplayer e multiplayer per disegnare i dadi e i pulsanti di controllo del gioco

// Composable per disegnare un dado
@Composable
fun Dice(
    value: Int,
    size: Dp,
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

// Composable per disegnare una riga di dadi
@Composable
fun MultiDiceRow(
    diceValues: List<Int>,
    heldDice: List<Boolean>,
    onDiceClick: (Int) -> Unit,
    enabled: Boolean,
    diceSize: Dp = 50.dp,
    isRolling: Boolean = false,
    isPlayer1Turn: Boolean = true,
    isSinglePlayer: Boolean = false
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

            val randomEndRotation = remember(isRolling) {
                (-540..720).random().toFloat() + (index * 45f)
            }


            val valueBasedRotation = remember(diceValues[index], isRolling) {
                diceValues[index] * 60f
            }


            val rotation by animateFloatAsState(
                targetValue = if (isRolling && !heldDice[index]) randomEndRotation + valueBasedRotation else 0f,
                animationSpec = tween(
                    durationMillis = 650,
                    easing = EaseOutBack
                ),
                label = "diceRotation"
            )


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


                                if (isSinglePlayer || isPlayer1Turn) {
                                    Brush.linearGradient(
                                        listOf(verde_acqua, verde_azzurro)
                                    )
                                } else {

                                    Brush.linearGradient(
                                        listOf(arancio_rosso, arancione)
                                    )
                                }
                            } else {
                                Brush.linearGradient(
                                    listOf(perla, perla_bianca)
                                )
                            },
                            shape = RoundedCornerShape((8 * scaleFactor).dp)
                        )
                        .border(
                            width = if (heldDice[index]) (2 * scaleFactor).dp else (1 * scaleFactor).dp,
                            color = if (heldDice[index]) Color.White else perlina,
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
                        Dice(
                            value = value,
                            size = diceSize * 0.8f,
                            dotColor = if (heldDice[index]) Color.White else blu_sbiadito
                        )
                    }
                }
            }
        }
    }
}

// Composable per il pulsante Home
@Composable
fun HomeButton(
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
            .zIndex(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(violaceo, blu_chiaro)
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
}

// Composable per i pulsanti di controllo del gioco: lancio dei dadi e reset del gioco
@Composable
fun GameControlButtons(
    onRollClick: () -> Unit,
    onResetClick: () -> Unit,
    remainingRolls: Int,
    allDiceHeld: Boolean,
    isGameEnded: Boolean,
    scaleFactor: Float,
    screenWidth: Dp,
    screenHeight: Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = (screenWidth * 0.03f).coerceAtLeast(12.dp),
                end = (screenWidth * 0.03f).coerceAtLeast(12.dp),
                bottom = (screenHeight * 0.03f).coerceAtLeast(56.dp)
            )
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
                        listOf(violaceo, blu_chiaro)
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

                // Pulsante per il lancio dei dadi
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height((52 * scaleFactor).dp)
                        .shadow(
                            elevation = if (remainingRolls > 0 && !allDiceHeld) 8.dp else 4.dp,
                            shape = RoundedCornerShape((12 * scaleFactor).dp)
                        )
                        .clip(RoundedCornerShape((12 * scaleFactor).dp))
                        .clickable(
                            enabled = remainingRolls > 0 && !isGameEnded && !allDiceHeld,
                            onClick = onRollClick
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape((12 * scaleFactor).dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                   listOf(verde_acqua, verde_azzurro)
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
                                tint = Color.White.copy(alpha = if (remainingRolls > 0 && !allDiceHeld) 1f else 0.6f),
                                modifier = Modifier.size((20 * scaleFactor).dp)
                            )
                            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))
                            Text(
                                text = stringResource(id = R.string.roll) + " (${remainingRolls})",
                                color = Color.White.copy(alpha = if (remainingRolls > 0 && !allDiceHeld) 1f else 0.6f),
                                fontSize = (14 * scaleFactor).sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.padding(vertical = (4 * scaleFactor).dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width((10 * scaleFactor).dp))

                // Pulsante per il reset del gioco
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height((52 * scaleFactor).dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape((12 * scaleFactor).dp)
                        )
                        .clip(RoundedCornerShape((12 * scaleFactor).dp))
                        .clickable(onClick = onResetClick),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape((12 * scaleFactor).dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(arancio_rosso, arancione)
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
                            )
                        }
                    }
                }
            }
        }
    }
}


// Composable per il pulsante delle impostazioni
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