package com.example.yahtzee.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image // Potrebbe servire se usi un'immagine come sfondo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Potrebbe servire per le immagini di sfondo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Potrebbe servire per le immagini di sfondo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.R // Assicurati di importare il file R per le risorse, se usi immagini
import com.example.yahtzee.ui.theme.YahtzeeTheme

@Composable
fun Homepage(navController: NavController) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Vuoi uscire?") },
            text = { Text("Sei sicuro di voler chiudere l'app?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text("Esci")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9)), // Dal blu chiaro al blu
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )


        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Impostazioni",
            tint = Color.Gray, // Colore dell'icona
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(32.dp)
                .clickable { navController.navigate("settings") } // Naviga alla schermata impostazioni
        )

        // Contenuto centrale (pulsanti)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulsante Gioca
            Button(
                onClick = { navController.navigate("game") },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Icona Play",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Gioca", color = Color.White, fontSize = 18.sp)
                }
            }

            // Puoi aggiungere qui altri pulsanti se necessario
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    YahtzeeTheme {
        Homepage(navController = rememberNavController())
    }
}