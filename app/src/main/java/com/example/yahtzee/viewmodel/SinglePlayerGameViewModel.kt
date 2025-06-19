package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.service.GameService
import com.example.yahtzee.db.GameHistoryEntity
import com.example.yahtzee.repository.GameHistoryRepository
import kotlinx.coroutines.launch

class SinglePlayerGameViewModel(
    private val gameHistoryRepository: GameHistoryRepository,
    private val gameService: GameService = GameService()  // ✅ Aggiungi GameService
) : ViewModel() {


    var state by mutableStateOf(gameService.resetGame())
        private set

    // Stato per tenere traccia se è stato battuto il record
    var isNewHighScore by mutableStateOf(false)
        private set

    val combinations = GameService.combinations

    fun rollDice() {
        if (state.remainingRolls > 0 && !state.gameEnded) {
            state = state.copy(
                diceValues = gameService.rollDice(state.diceValues, state.heldDice),
                remainingRolls = state.remainingRolls - 1,
                canSelectScore = true
            )
        }
    }

    fun toggleHold(index: Int) {
        // Un dado può essere bloccato solo se ha un valore (non è null) e dopo almeno un lancio
        if (state.remainingRolls < 3 && !state.gameEnded && state.diceValues[index] != null) {
            val newHeld = state.heldDice.toMutableList().also { it[index] = !it[index] }
            state = state.copy(heldDice = newHeld)
        }
    }

    fun selectScore(combination: String) {
        if (state.canSelectScore && state.scoreMap[combination] == null && !state.gameEnded) {
            val newScoreMap = state.scoreMap.toMutableMap()
            newScoreMap[combination] = gameService.calculateScore(combination, state.diceValues)
            val ended = gameService.isGameEnded(newScoreMap)
            state = state.copy(
                scoreMap = newScoreMap,
                remainingRolls = 3,
                canSelectScore = false,
                heldDice = List(5) { false },
                gameEnded = ended
            )
            if (ended) {
                saveGameResult()
            }
        }
    }

    fun resetGame() {
        state = gameService.resetGame()
        isNewHighScore = false
    }

    fun previewScores(): Map<String, Int?> {
        return if (state.canSelectScore) {
            combinations.associateWith { combination ->
                if (state.scoreMap[combination] == null) {
                    gameService.calculateScore(combination, state.diceValues)
                } else null
            }
        } else emptyMap()
    }

    private fun saveGameResult() {
        val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
        val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
        val bonus = if (upperSum >= 63) 35 else 0
        val totalScore = state.scoreMap.values.filterNotNull().sum() + bonus

        viewModelScope.launch {
            val highestScore = gameHistoryRepository.getHighestScore() ?: 0
            if (totalScore > highestScore) {
                isNewHighScore = true
            }
            gameHistoryRepository.insertGameHistory(
                GameHistoryEntity(
                    date = System.currentTimeMillis(),
                    score = totalScore
                )
            )
        }
    }
}
