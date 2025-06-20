package com.example.yahtzee.repository

import com.example.yahtzee.db.GameHistoryDao
import com.example.yahtzee.db.GameHistoryEntity

class GameHistoryRepository(
    private val gameHistoryDao: GameHistoryDao,  // ✅ Riceve DAO direttamente
) {

    // ✅ Metodi esistenti per cronologia (invariati)
    suspend fun getAllHistory(): List<GameHistoryEntity> {
        return gameHistoryDao.getAllHistory()
    }

    suspend fun getHighestScore(): Int? {
        return gameHistoryDao.getHighestScore()
    }

    suspend fun insertGameHistory(entry: GameHistoryEntity) {
        gameHistoryDao.insertGameHistory(entry)
    }

}