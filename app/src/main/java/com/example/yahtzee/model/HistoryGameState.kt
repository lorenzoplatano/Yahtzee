package com.example.yahtzee.model

import java.util.Date

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