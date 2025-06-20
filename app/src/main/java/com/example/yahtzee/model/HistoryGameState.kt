package com.example.yahtzee.model

import java.util.Date

// Model per la cronologia delle partite singleplayer
data class GameHistoryEntry(
    val date: Date,
    val score: Int
)

enum class SortColumn { DATE, SCORE }
enum class SortOrder { ASC, DESC }

data class HistoryState(
    val history: List<GameHistoryEntry> = emptyList(),
    val sortColumn: SortColumn = SortColumn.DATE,
    val sortOrder: SortOrder = SortOrder.DESC
)