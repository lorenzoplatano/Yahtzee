package com.example.yahtzee.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

/**
 * Riga di dadi con puntini e animazione, riutilizzabile per singleplayer e multiplayer.
 * Passa la lista di animazioni (rotation, scale) per ogni dado.
 */
@Composable
fun GameDiceRow(
    diceValues: List<Int>,
    heldDice: List<Boolean>,
    onDiceClick: (Int) -> Unit,
    enabled: Boolean,
    diceSize: androidx.compose.ui.unit.Dp = 50.dp,
    isRolling: Boolean = false,
    diceAnimations: List<Pair<Float, Float>>
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            diceValues.forEachIndexed { index, value ->
                Card(
                    modifier = Modifier
                        .size(diceSize)
                        .shadow(
                            elevation = if (heldDice[index]) 6.dp else 3.dp,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = enabled) {
                            onDiceClick(index)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (heldDice[index]) {
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF7FAFC), Color(0xFFEDF2F7))
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (heldDice[index]) 2.dp else 1.dp,
                                color = if (heldDice[index]) Color.White else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(8.dp)
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
                            if (isRolling && !heldDice[index]) {
                                Text(
                                    text = "?",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (heldDice[index]) Color.White else Color(0xFF2D3748)
                                )
                            } else {
                                DiceWithDots(
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
    }
}