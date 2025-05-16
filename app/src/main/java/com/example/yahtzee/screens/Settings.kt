package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.ui.theme.YahtzeeTheme

@Composable
fun Settings(navController: NavController) {
    SettingsContent(
        onHomeClick = { navController.navigate("homepage") }
    )
}

@Composable
fun SettingsContent(onHomeClick: () -> Unit) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

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
        // Icona Home
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(32.dp)
                .clickable { onHomeClick() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsRow("Tema (Chiaro/Scuro)") {
                showThemeDialog = true
            }
            SettingsRow("Lingua") {
                showLanguageDialog = true
            }
            SettingsRow("Regole del Gioco")
            SettingsRow("Notifiche")
            SettingsRow("Reset Impostazioni")
        }

        if (showThemeDialog) {
            ThemeDialog(onDismiss = { showThemeDialog = false })
        }

        if (showLanguageDialog) {
            LanguageDialog(onDismiss = { showLanguageDialog = false })
        }
    }
}

@Composable
fun SettingsRow(label: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ThemeDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona Tema") },
        text = {
            Column {
                Text("Chiaro", modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // TODO: Applica tema chiaro
                        onDismiss()
                    }
                    .padding(8.dp)
                )
                Text("Scuro", modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // TODO: Applica tema scuro
                        onDismiss()
                    }
                    .padding(8.dp)
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
fun LanguageDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona Lingua") },
        text = {
            Column {
                Text("Italiano", modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // TODO: Imposta lingua italiana
                        onDismiss()
                    }
                    .padding(8.dp)
                )
                Text("Inglese", modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // TODO: Imposta lingua inglese
                        onDismiss()
                    }
                    .padding(8.dp)
                )
            }
        },
        confirmButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    YahtzeeTheme {
        SettingsContent(onHomeClick = {})
    }
}

