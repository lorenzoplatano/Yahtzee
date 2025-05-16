package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.ui.theme.YahtzeeTheme

@Composable
fun GameScreenSinglePlayer(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Home icon
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(32.dp)
                .clickable { navController.navigate("homepage") }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 96.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo grande
            Text(
                text = "YAHTZEE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Dadi dummy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                            .border(1.dp, Color.Gray)
                    )
                }
            }

            // Tabella punteggi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF0D47A1), shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                TableRow("COMBINATION", "SCORE", header = true)
                val combinations = listOf(
                    "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Bonus",
                    "3 of a Kind", "4 of a Kind", "Full House", "Small Straight", "Large Straight",
                    "Yahtzee"
                )
                combinations.forEach {
                    Divider(color = Color(0xFF0D47A1), thickness = 1.dp)
                    TableRow(it, "—")
                }
                Divider(color = Color(0xFF0D47A1), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                TableRow("Total Score", "—", bold = true)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pulsanti in basso ingranditi e ravvicinati
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /* dummy */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("Play", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { /* dummy */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Roll Dice", fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
fun TableRow(left: String, right: String, header: Boolean = false, bold: Boolean = false) {
    val textStyle = if (bold || header) {
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = left,
            style = textStyle,
            fontSize = 16.sp,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = right,
            style = textStyle,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    YahtzeeTheme {
        GameScreenSinglePlayer(navController = rememberNavController())
    }
}
