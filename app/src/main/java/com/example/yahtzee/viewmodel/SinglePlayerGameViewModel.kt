package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.service.GameService
import com.example.yahtzee.db.GameHistoryEntity
import com.example.yahtzee.model.GameState
import com.example.yahtzee.model.SavedSinglePlayerGame
import com.example.yahtzee.repository.GameHistoryRepository
import com.example.yahtzee.repository.GameSaveRepository
import kotlinx.coroutines.launch

class SinglePlayerGameViewModel(
    private val gameHistoryRepository: GameHistoryRepository,
    private val gameSaveRepository: GameSaveRepository,
    private val gameService: GameService = GameService()
) : ViewModel() {

    var state by mutableStateOf(gameService.resetGame())
        private set

    var isNewHighScore by mutableStateOf(false)
        private set

    val combinations = GameService.combinations

    /**
     * Load saved game if exists using the new GameSaveRepository
     */
    fun loadSavedGameIfExists() {
        viewModelScope.launch {
            try {
                val savedGame = gameSaveRepository.loadSavedSinglePlayerGame()
                if (savedGame != null) {
                    // Convert SavedSinglePlayerGame to GameState
                    state = GameState(
                        diceValues = savedGame.diceValues,
                        scoreMap = savedGame.scoreMap.toMutableMap(),
                        remainingRolls = savedGame.remainingRolls,
                        canSelectScore = savedGame.canSelectScore,
                        heldDice = savedGame.heldDice,
                        gameEnded = savedGame.gameEnded
                    )
                }
            } catch (_: Exception) {
                // If loading fails, clear corrupted save and start fresh
                gameSaveRepository.clearSavedSinglePlayerGame()
            }
        }
    }

    fun rollDice() {
        if (state.remainingRolls > 0 && !state.gameEnded) {
            state = state.copy(
                diceValues = gameService.rollDice(state.diceValues, state.heldDice),
                remainingRolls = state.remainingRolls - 1,
                canSelectScore = true
            )
            // Save state after each action
            saveCurrentState()
        }
    }

    fun toggleHold(index: Int) {
        if (state.remainingRolls < 3 && !state.gameEnded && state.diceValues[index] != null) {
            val newHeld = state.heldDice.toMutableList().also { it[index] = !it[index] }
            state = state.copy(heldDice = newHeld)
            // Save state after each action
            saveCurrentState()
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

            } else {
                // Save state after each action
                saveCurrentState()
            }
        }
    }

    fun resetGame() {
        // Clear saved state first
        clearSavedState()

        // Then reset game state
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

    /**
     * Save game result to history (using GameHistoryRepository)
     */
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

    /**
     * Save current game state (using GameSaveRepository)
     */
    private fun saveCurrentState() {
        if (shouldSaveState()) {
            viewModelScope.launch {
                val upper = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
                val upperSum = upper.mapNotNull { state.scoreMap[it] }.sum()
                val bonus = if (upperSum >= 63) 35 else 0
                val currentScore = state.scoreMap.values.filterNotNull().sum() + bonus

                val savedGame = SavedSinglePlayerGame(
                    diceValues = state.diceValues,
                    scoreMap = state.scoreMap,
                    remainingRolls = state.remainingRolls,
                    canSelectScore = state.canSelectScore,
                    heldDice = state.heldDice,
                    gameEnded = state.gameEnded,
                    currentScore = currentScore
                )

                gameSaveRepository.saveSinglePlayerGame(savedGame)
            }
        }
    }

    /**
     * Check if state should be saved (only if game has started)
     */
    private fun shouldSaveState(): Boolean {
        return state.remainingRolls < 3 || state.scoreMap.values.any { it != null }
    }

    /**
     * Clear saved state
     */
    private fun clearSavedState() {
        viewModelScope.launch {
            gameSaveRepository.clearSavedSinglePlayerGame()
        }
    }
}