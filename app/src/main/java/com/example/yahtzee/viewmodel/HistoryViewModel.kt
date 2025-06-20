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
    var uiState = mutableStateOf(HistoryState())
        private set

    init {
        loadHistory()
    }

    fun refreshHistory() {
        loadHistory()
    }

    // Carica la cronologia dei giochi dal repository e aggiorna lo stato
    private fun loadHistory() {
        viewModelScope.launch {
            val entities = gameHistoryRepository.getAllHistory()
            val entries = entities.map { it.toGameHistoryEntry() }
            updateSortedHistory(entries, uiState.value.sortColumn, uiState.value.sortOrder)
        }
    }

    // Modifica lo stato della UI quando viene selezionata una nuova colonna di ordinamento
    fun onSortChange(column: SortColumn) {
        val newSortOrder = if (uiState.value.sortColumn == column) {
            if (uiState.value.sortOrder == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
        } else {
            SortOrder.DESC
        }
        updateSortedHistory(uiState.value.history, column, newSortOrder)
    }

    // Modifica l'ordinamento dello storico dei giochi in base alla colonna e all'ordine selezionati
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

// Converte un GameHistoryEntity in un GameHistoryEntry
private fun GameHistoryEntity.toGameHistoryEntry(): GameHistoryEntry {
    return GameHistoryEntry(
        date = Date(this.date),
        score = this.score
    )
}
