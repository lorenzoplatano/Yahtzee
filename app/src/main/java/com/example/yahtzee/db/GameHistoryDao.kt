package com.example.yahtzee.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Dao per la cronologia delle partite singleplayer nel database Room
@Dao
interface GameHistoryDao {
    // Inserisce una nuova voce nella cronologia delle partite
    @Insert
    suspend fun insertGameHistory(entry: GameHistoryEntity)

    // Ottiene tutta la cronologia delle partite
    @Query("SELECT * FROM game_history ORDER BY date DESC")
    suspend fun getAllHistory(): List<GameHistoryEntity>

    // Ottiene il punteggio più alto dalla cronologia delle partite
    @Query("SELECT MAX(score) FROM game_history")
    suspend fun getHighestScore(): Int?
}
