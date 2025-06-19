package com.example.yahtzee.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.model.*
import com.example.yahtzee.repository.GameHistoryRepository
import com.example.yahtzee.viewmodel.HistoryViewModel
import com.example.yahtzee.ui.theme.violaceo
import com.example.yahtzee.ui.theme.blu_chiaro
import com.example.yahtzee.screens.components.GenericButton
import com.example.yahtzee.viewmodel.HistoryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel  // ✅ Aggiungi questo parametro
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    val uiState by viewModel.uiState

    // Responsive calculations
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    val screenHeight = with(density) { configuration.screenHeightDp.dp }

    val scaleFactor = (screenWidth.value / 400f).coerceIn(0.8f, 1.2f)
    val headerPadding = (screenHeight.value * 0.08f).dp.coerceAtLeast(32.dp).coerceAtMost(60.dp)
    val isCompactHeight = screenHeight < 700.dp

    // Unica dichiarazione richiesta
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.sfondo_generale),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent overlay (alpha fisso)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background.copy(alpha = 0.4f))
        )

        // Home icon (top-left) con GenericButton
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = headerPadding.coerceAtLeast(32.dp).coerceAtMost(60.dp),
                    start = (screenWidth * 0.03f).coerceAtLeast(8.dp).coerceAtMost(16.dp)
                )
        ) {
            GenericButton(
                text = "",
                icon = Icons.Default.Home,
                onClick = { navController.navigate("homepage") },
                modifier = Modifier.size(48.dp),
                showIconOnly = true
            )
        }

        // Settings icon (top-right) con GenericButton
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = headerPadding.coerceAtLeast(32.dp).coerceAtMost(60.dp),
                    end = (screenWidth * 0.03f).coerceAtLeast(8.dp).coerceAtMost(16.dp)
                )
        ) {
            GenericButton(
                text = "",
                icon = Icons.Default.Settings,
                onClick = { navController.navigate("settings") },
                modifier = Modifier.size(48.dp),
                showIconOnly = true
            )
        }

        // History card - responsive positioning
        Card(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth(0.9f)
                .padding(
                    top = (headerPadding + (48 * scaleFactor).dp).coerceAtLeast(120.dp),
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .align(Alignment.TopCenter)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header con gradiente come singleplayer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(listOf(violaceo, blu_chiaro)),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.history),
                        style = if (isCompactHeight)
                            MaterialTheme.typography.headlineMedium
                        else
                            MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Header della tabella con gradiente e bordi smussati
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(listOf(violaceo, blu_chiaro)),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        SortableColumnHeader(
                            title = stringResource(R.string.date),
                            isSorted = uiState.sortColumn == SortColumn.DATE,
                            ascending = uiState.sortOrder == SortOrder.ASC,
                            onClick = { viewModel.onSortChange(SortColumn.DATE) },
                            headerColor = Color.White
                        )
                    }
                    VerticalDivider(
                        color = Color.White.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier
                            .height(24.dp)
                            .padding(horizontal = 2.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        SortableColumnHeader(
                            title = stringResource(R.string.score_label),
                            isSorted = uiState.sortColumn == SortColumn.SCORE,
                            ascending = uiState.sortOrder == SortOrder.ASC,
                            onClick = { viewModel.onSortChange(SortColumn.SCORE) },
                            headerColor = Color.White
                        )
                    }
                }

                // History list con divider tra le entry
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = if (isCompactHeight)
                                (screenHeight - 200.dp).coerceAtLeast(200.dp)
                            else
                                (screenHeight - 250.dp).coerceAtLeast(300.dp)
                        )
                ) {
                    items(uiState.history) { entry ->
                        HistoryRow(entry)
                        HorizontalDivider(
                            thickness = 0.7.dp,
                            color = colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SortableColumnHeader(
    title: String,
    isSorted: Boolean,
    ascending: Boolean,
    onClick: () -> Unit,
    headerColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = headerColor
        )
        if (isSorted) {
            Icon(
                imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = headerColor
            )
        }
    }
}

@Composable
fun HistoryRow(entry: GameHistoryEntry) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = dateFormat.format(entry.date),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = entry.score.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}