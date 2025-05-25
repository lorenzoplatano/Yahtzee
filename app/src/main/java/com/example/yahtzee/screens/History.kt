
package com.example.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.model.*
import com.example.yahtzee.viewmodel.HistoryViewModel
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.ui.theme.HistoryTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val viewModel: HistoryViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HistoryViewModel(db.gameHistoryDao()) as T
            }
        }
    )

    val uiState by viewModel.uiState

    HistoryTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Icona impostazioni in alto a destra
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Impostazioni",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
                    .size(32.dp)
                    .clickable { navController.navigate("settings") }
            )

            // Icona home sotto impostazioni
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .size(32.dp)
                    .clickable { navController.navigate("homepage") }
            )

            // Tabella storico
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 4.dp)
                    ) {
                        HistoryTableHeader(
                            sortColumn = uiState.sortColumn,
                            sortOrder = uiState.sortOrder,
                            onSortChange = { viewModel.onSortChange(it) }
                        )
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.history) { entry ->
                                HistoryTableRow(entry)
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTableHeader(
    sortColumn: SortColumn,
    sortOrder: SortOrder,
    onSortChange: (SortColumn) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableHeaderCell(
            text = "Data",
            modifier = Modifier.weight(1f),
            isSorted = sortColumn == SortColumn.DATE,
            sortOrder = if (sortColumn == SortColumn.DATE) sortOrder else null,
            onClick = { onSortChange(SortColumn.DATE) }
        )
        VerticalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp),
            color = MaterialTheme.colorScheme.outline
        )
        TableHeaderCell(
            text = "Punteggio",
            modifier = Modifier.weight(1f),
            isSorted = sortColumn == SortColumn.SCORE,
            sortOrder = if (sortColumn == SortColumn.SCORE) sortOrder else null,
            onClick = { onSortChange(SortColumn.SCORE) }
        )
    }
}

@Composable
fun TableHeaderCell(
    text: String,
    modifier: Modifier = Modifier,
    isSorted: Boolean,
    sortOrder: SortOrder?,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        if (isSorted && sortOrder != null) {
            Icon(
                imageVector = if (sortOrder == SortOrder.ASC)
                    Icons.Default.ArrowUpward
                else
                    Icons.Default.ArrowDownward,
                contentDescription = if (sortOrder == SortOrder.ASC) "Ascendente" else "Discendente",
                modifier = Modifier
                    .size(16.dp)
                    .padding(start = 2.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun HistoryTableRow(entry: GameHistoryEntry) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateFormat.format(entry.date),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        VerticalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = entry.score.toString(),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

