package com.example.yahtzee.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.R
import com.example.yahtzee.ui.theme.HomeTheme
import androidx.compose.material3.Icon

@Composable
fun Homepage(navController: NavController) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pulsante Gioca
        if (!showModeDialog) {
            // Pulsante iniziale "Play"
            Button(
                onClick = { showModeDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Icona Play",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Play", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                }
            }
        } else {

            Column(
                modifier = Modifier.fillMaxWidth(0.6f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        navController.navigate("game_1vs1")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("1 vs 1", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                }
                Button(
                    onClick = {
                        navController.navigate("game")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Singleplayer", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                }
            }
        }
    }

    // Icone in alto a destra (rimangono sopra il gradient)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, top = 40.dp)
            .wrapContentSize(Alignment.TopEnd),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Impostazioni",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(32.dp)
                .clickable { navController.navigate("settings") }
        )

        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Storico partite",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(28.dp)
                .clickable { navController.navigate("history") }
        )
    }
}