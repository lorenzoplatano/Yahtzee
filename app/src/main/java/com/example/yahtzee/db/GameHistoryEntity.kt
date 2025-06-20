package com.example.yahtzee.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity per la cronologia delle partite singleplayer nel database Room
@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val score: Int
)
