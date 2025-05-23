package com.example.yahtzee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.model.*
import com.example.yahtzee.db.GameHistoryDao
import com.example.yahtzee.db.GameHistoryEntity
import kotlinx.coroutines.launch
import java.util.*

class HistoryViewModel(
    private val dao: GameHistoryDao
) : ViewModel() {
    // Stato UI osservabile
    var uiState = androidx.compose.runtime.mutableStateOf(HistoryState())
        private set

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val entities = dao.getAllHistory()
            val entries = entities.map { it.toGameHistoryEntry() }
            updateSortedHistory(entries, uiState.value.sortColumn, uiState.value.sortOrder)
        }
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

// Funzione di estensione per convertire l'entity Room nel modello UI
private fun GameHistoryEntity.toGameHistoryEntry(): GameHistoryEntry {
    return GameHistoryEntry(
        date = Date(this.date),
        score = this.score
    )
}
