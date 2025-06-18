package com.example.yahtzee.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.model.*
import com.example.yahtzee.viewmodel.HistoryViewModel
import com.example.yahtzee.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController,
    isDarkTheme: Boolean
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

    // Responsive calculations
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    val screenHeight = with(density) { configuration.screenHeightDp.dp }

    val scaleFactor = (screenWidth.value / 400f).coerceIn(0.8f, 1.2f)
    val headerPadding = (screenHeight.value * 0.08f).dp.coerceAtLeast(32.dp).coerceAtMost(60.dp)
    val isCompactHeight = screenHeight < 700.dp

    // Colori dinamici dal tema
    val cardBackground = if (isDarkTheme) CardDark else CardLight
    val overlayColor = if (isDarkTheme) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.3f)
    val titleColor = mainTextColor(isDarkTheme)

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.sfondo_generale),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )

        // Home icon (top-left)
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = headerPadding.coerceAtLeast(32.dp).coerceAtMost(60.dp),
                    start = (screenWidth * 0.03f).coerceAtLeast(8.dp).coerceAtMost(16.dp)
                )
                .size((40 * scaleFactor).dp.coerceAtLeast(36.dp).coerceAtMost(48.dp))
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(10.dp)
                )
                .zIndex(1f)
                .clickable { navController.navigate("homepage") },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size((20 * scaleFactor).dp.coerceAtLeast(18.dp).coerceAtMost(24.dp))
                )
            }
        }

        // Settings icon (top-right)
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = headerPadding.coerceAtLeast(32.dp).coerceAtMost(60.dp),
                    end = (screenWidth * 0.03f).coerceAtLeast(8.dp).coerceAtMost(16.dp)
                )
                .size((40 * scaleFactor).dp.coerceAtLeast(36.dp).coerceAtMost(48.dp))
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(10.dp)
                )
                .zIndex(1f)
                .clickable { navController.navigate("settings") },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Impostazioni",
                    tint = Color.White,
                    modifier = Modifier.size((20 * scaleFactor).dp.coerceAtLeast(18.dp).coerceAtMost(24.dp))
                )
            }
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
            colors = CardDefaults.cardColors(containerColor = cardBackground)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Storico Partite",
                    style = if (isCompactHeight)
                        MaterialTheme.typography.headlineMedium
                    else
                        MaterialTheme.typography.displaySmall,
                    color = titleColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SortableColumnHeader(
                        title = "Data",
                        isSorted = uiState.sortColumn == SortColumn.DATE,
                        ascending = uiState.sortOrder == SortOrder.ASC,
                        onClick = { viewModel.onSortChange(SortColumn.DATE) },
                        isDarkTheme = isDarkTheme
                    )
                    SortableColumnHeader(
                        title = "Punteggio",
                        isSorted = uiState.sortColumn == SortColumn.SCORE,
                        ascending = uiState.sortOrder == SortOrder.ASC,
                        onClick = { viewModel.onSortChange(SortColumn.SCORE) },
                        isDarkTheme = isDarkTheme
                    )
                }

                // History list with height constraints for smaller screens
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
                        HistoryRow(entry, isDarkTheme)
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
    isDarkTheme: Boolean
) {
    val titleColor = mainTextColor(isDarkTheme)
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor
        )
        if (isSorted) {
            Icon(
                imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = titleColor
            )
        }
    }
}

@Composable
fun HistoryRow(entry: GameHistoryEntry, isDarkTheme: Boolean) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val titleColor = mainTextColor(isDarkTheme)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = dateFormat.format(entry.date),
            style = MaterialTheme.typography.bodyMedium,
            color = titleColor
        )
        Text(
            text = entry.score.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = titleColor
        )
    }
}