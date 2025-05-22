package com.example.yahtzee.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yahtzee.model.*
import java.util.*

class HistoryViewModel : ViewModel() {
    // Stato UI osservabile
    var uiState = androidx.compose.runtime.mutableStateOf(HistoryState())
        private set

    init {
        loadHistory()
    }

    private fun loadHistory() {
        // Dummy data, in futuro da sostituire con dati dal DB
        val dummyData = listOf(
            GameHistoryEntry(Date(1719878400000), 250), // 2 luglio 2024
            GameHistoryEntry(Date(1719792000000), 320), // 1 luglio 2024
            GameHistoryEntry(Date(1719705600000), 180), // 30 giugno 2024
            GameHistoryEntry(Date(1719619200000), 400), // 29 giugno 2024
            GameHistoryEntry(Date(1719532800000), 290), // 28 giugno 2024
        )
        updateSortedHistory(dummyData, uiState.value.sortColumn, uiState.value.sortOrder)
    }

    fun onSortChange(column: SortColumn) {
        val newSortOrder = if (uiState.value.sortColumn == column) {
            if (uiState.value.sortOrder == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
        } else {
            SortOrder.DESC
        }
        updateSortedHistory(uiState.value.history, column, newSortOrder)
    }

    private fun updateSortedHistory(
        history: List<GameHistoryEntry>,
        sortColumn: SortColumn,
        sortOrder: SortOrder
    ) {
        val sorted = history.sortedWith(
            when (sortColumn) {
                SortColumn.DATE -> compareBy<GameHistoryEntry> { it.date }
                SortColumn.SCORE -> compareBy<GameHistoryEntry> { it.score }
            }.let { comparator ->
                if (sortOrder == SortOrder.DESC) comparator.reversed() else comparator
            }
        )
        uiState.value = uiState.value.copy(
            history = sorted,
            sortColumn = sortColumn,
            sortOrder = sortOrder
        )
    }
}