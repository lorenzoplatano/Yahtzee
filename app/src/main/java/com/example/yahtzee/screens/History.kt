package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.ui.theme.YahtzeeTheme

@Composable
fun History(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBBDEFB),
                        Color(0xFF90CAF9)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Icon impostazioni in alto a destra
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Impostazioni",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(32.dp)
                .clickable { navController.navigate("settings") }
        )

        // Icon home sotto impostazioni
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp)
                .size(32.dp)
                .clickable { navController.navigate("homepage") }
        )

        // Lista dello storico (dummy)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            // Lista di placeholder, simulazione di storico
            val placeholder = List(10) { "Partita #${it + 1}" }
            items(placeholder) { partita ->
                HistoryItem(name = partita)
            }
        }
    }
}

@Composable
fun HistoryItem(name: String) {
    // Composable placeholder per un singolo elemento di storico
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        // Contenuto dummy
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    YahtzeeTheme {
        val context = LocalContext.current
        History(navController = NavController(context))
    }
}

