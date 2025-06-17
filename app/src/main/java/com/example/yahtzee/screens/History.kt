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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.model.*
import com.example.yahtzee.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController,
    darkTheme: Boolean
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.sfondo_generale),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent overlay
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))

        // History card
        Card(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .align(Alignment.Center)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Storico Partite",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A1A1A)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SortableColumnHeader(
                        title = "Data",
                        isSorted = uiState.sortColumn == SortColumn.DATE,
                        ascending = uiState.sortOrder == SortOrder.ASC,
                        onClick = { viewModel.onSortChange(SortColumn.DATE) }
                    )
                    SortableColumnHeader(
                        title = "Punteggio",
                        isSorted = uiState.sortColumn == SortColumn.SCORE,
                        ascending = uiState.sortOrder == SortOrder.ASC,
                        onClick = { viewModel.onSortChange(SortColumn.SCORE) }
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.history) { entry ->
                        HistoryRow(entry)
                    }
                }
            }
        }

        // Navigation icons
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Impostazioni",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(32.dp)
                .clickable { navController.navigate("settings") }
        )

        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp)
                .size(32.dp)
                .clickable { navController.navigate("homepage") }
        )
    }
}

@Composable
fun SortableColumnHeader(
    title: String,
    isSorted: Boolean,
    ascending: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        )
        if (isSorted) {
            Icon(
                imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF1A1A1A)
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
        Text(text = dateFormat.format(entry.date), color = Color(0xFF1A1A1A))
        Text(text = entry.score.toString(), color = Color(0xFF1A1A1A))
    }
}
