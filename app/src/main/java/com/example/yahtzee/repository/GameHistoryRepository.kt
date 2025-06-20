package com.example.yahtzee.repository

import com.example.yahtzee.db.GameHistoryDao
import com.example.yahtzee.db.GameHistoryEntity

// Repository per gestire l'inserimento e la lettura della cronologia delle partite singleplayer
class GameHistoryRepository(
    private val gameHistoryDao: GameHistoryDao,
) {

    // Ottiene tutta la cronologia delle partite
    suspend fun getAllHistory(): List<GameHistoryEntity> {
        return gameHistoryDao.getAllHistory()
    }

    // Ottiene il punteggio più alto dalla cronologia delle partite
    suspend fun getHighestScore(): Int? {
        return gameHistoryDao.getHighestScore()
    }

    // Inserisce una nuova voce nella cronologia delle partite
    suspend fun insertGameHistory(entry: GameHistoryEntity) {
        gameHistoryDao.insertGameHistory(entry)
    }

}