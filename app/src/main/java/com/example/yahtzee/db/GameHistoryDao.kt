package com.example.yahtzee.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameHistoryDao {
    @Insert
    suspend fun insertGameHistory(entry: GameHistoryEntity)

    @Query("SELECT * FROM game_history ORDER BY date DESC")
    suspend fun getAllHistory(): List<GameHistoryEntity>
}
