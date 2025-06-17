package com.example.yahtzee.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernGameButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6)  // Purple
    ),
    enabled: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    showTextOnly: Boolean = false,
    showIconOnly: Boolean = false
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 8.dp else 4.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (showIconOnly) 48.dp else 56.dp)
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(gradientColors)
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.3f))
                        )
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (!showTextOnly) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (!showIconOnly) {
                    if (!showTextOnly) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
