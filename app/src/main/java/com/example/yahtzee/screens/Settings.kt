

package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Stato iniziale delle impostazioni
private val initialSettings = SettingsState(
    notificationsEnabled = true,
    language = "Italiano"
)

data class SettingsState(
    val notificationsEnabled: Boolean,
    val language: String
)

@Composable
fun Settings(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    SettingsContent(
        isDarkTheme = isDarkTheme,
        onThemeChange = onThemeChange,
        onHomeClick = { navController.navigate("homepage") }
    )
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onHomeClick: () -> Unit
) {
    // Stato delle impostazioni (eccetto il tema, che ora è gestito globalmente)
    var settingsState by remember { mutableStateOf(initialSettings) }
    // Stato dei dialog
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) } // Stato per dialog Regole

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
            // RIGHE IMPOSTAZIONI
            ThemeSwitchRow(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
            SettingsRow(
                label = "Lingua: ${settingsState.language}"
            ) {
                showLanguageDialog = true
            }
            NotificationRow(
                enabled = settingsState.notificationsEnabled,
                onToggle = {
                    settingsState = settingsState.copy(notificationsEnabled = !settingsState.notificationsEnabled)
                }
            )
            // Aggiunta riga Regole di Yahtzee
            SettingsRow(
                label = "Regole"
            ) {
                showRulesDialog = true
            }
            // Reset impostazioni
            SettingsRow(
                label = "Reset Impostazioni"
            ) {
                settingsState = initialSettings
                onThemeChange(false) // Reset anche il tema a chiaro
            }
        }

        if (showLanguageDialog) {
            LanguageDialog(
                currentLanguage = settingsState.language,
                onSelectLanguage = { lang ->
                    settingsState = settingsState.copy(language = lang)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        if (showRulesDialog) {
            RulesDialog(
                onDismiss = { showRulesDialog = false }
            )
        }
    }
}

@Composable
fun ThemeSwitchRow(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tema: ${if (isDarkTheme) "Scuro" else "Chiaro"}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { checked -> onThemeChange(checked) }
        )
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
fun NotificationRow(enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notifiche",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = enabled,
            onCheckedChange = { onToggle() }
        )
    }
}

@Composable
fun LanguageDialog(
    currentLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona Lingua") },
        text = {
            Column {
                Text(
                    "Italiano",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLanguage("Italiano")
                        }
                        .padding(8.dp),
                    fontWeight = if (currentLanguage == "Italiano") FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    "Inglese",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectLanguage("Inglese")
                        }
                        .padding(8.dp),
                    fontWeight = if (currentLanguage == "Inglese") FontWeight.Bold else FontWeight.Normal
                )
            }
        },
        confirmButton = {}
    )
}

// Funzione che mostra il regolamento di Yahtzee in un dialog
@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Regolamento Yahtzee", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "Sono previste diverse combinazioni che ogni giocatore deve realizzare lanciando i dadi. Ottenuta la combinazione il giocatore guadagna il punteggio previsto per la combinazione. Una combinazione non può essere ripetuta quindi il gioco termina dopo 13 turni di lancio dei dadi, anche quando non sono state realizzate tutte le combinazioni.\n\n" +
                            "Ad ogni turno il giocatore può lanciare i dadi tre volte. Al primo lancio il giocatore lancia tutti i dadi, mentre nei successivi due lanci il giocatore può scegliere di trattenere uno o più dadi favorevoli ad ottenere la combinazione cercata. Il giocatore può anche scegliere di non trattenere alcun dado o di non utilizzare successivi lanci, nel caso ad esempio si sia già realizzata una combinazione utile. Al termine dei tre lanci il giocatore deve segnare obbligatoriamente un punteggio in una delle caselle del segnapunti non ancora utilizzata. Se alla fine del turno di gioco non viene realizzata una delle possibili combinazioni ancora \"libera\" sul tabellone, il giocatore deve segnare \"0\" (zero) in una delle caselle ancora a sua disposizione.\n\n",
                    fontSize = 15.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

