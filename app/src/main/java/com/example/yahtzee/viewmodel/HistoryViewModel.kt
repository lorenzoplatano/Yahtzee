package com.example.yahtzee.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.model.*
import com.example.yahtzee.db.GameHistoryEntity
import com.example.yahtzee.repository.GameHistoryRepository
import kotlinx.coroutines.launch
import java.util.*

class HistoryViewModel(
    private val gameHistoryRepository: GameHistoryRepository
) : ViewModel() {
    // Stato UI osservabile
    var uiState = mutableStateOf(HistoryState())
        private set

    init {
        loadHistory()
    }

    fun refreshHistory() {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val entities = gameHistoryRepository.getAllHistory()
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

    // Nuova funzione per aggiungere una partita in tempo reale
    fun addGameToHistory(entry: GameHistoryEntry) {
        val newHistory = uiState.value.history + entry
        updateSortedHistory(newHistory, uiState.value.sortColumn, uiState.value.sortOrder)
    }

    // Cambia la visibilità da private a internal
    internal fun updateSortedHistory(
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