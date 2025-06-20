package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.service.GameService
import com.example.yahtzee.model.GameStateMultiplayer
import com.example.yahtzee.model.SavedMultiplayerGame
import com.example.yahtzee.repository.GameSaveRepository
import kotlinx.coroutines.launch

class MultiplayerGameViewModel(
    private val gameSaveRepository: GameSaveRepository,
    private val gameService: GameService = GameService()
) : ViewModel() {
    val combinations = GameService.combinations

    var state by mutableStateOf(GameStateMultiplayer())
        private set

    /**
     * Load saved game if exists using the new GameSaveRepository
     */
    fun loadSavedGameIfExists() {
        viewModelScope.launch {
            try {
                val savedGame = gameSaveRepository.loadSavedMultiplayerGame()
                if (savedGame != null) {
                    // Convert SavedMultiplayerGame to GameStateMultiplayer
                    state = GameStateMultiplayer(
                        diceValues = savedGame.diceValues,
                        heldDice = savedGame.heldDice,
                        remainingRolls = savedGame.remainingRolls,
                        hasRolledAtLeastOnce = savedGame.hasRolledAtLeastOnce,
                        scoreMapPlayer1 = savedGame.scoreMapPlayer1,
                        scoreMapPlayer2 = savedGame.scoreMapPlayer2,
                        isPlayer1Turn = savedGame.isPlayer1Turn,
                        gameEnded = savedGame.gameEnded
                    )
                } else {
                    // Inizializza un nuovo gioco quando non c'è uno stato salvato
                    resetGame()
                }
            } catch (_: Exception) {
                // If loading fails, clear corrupted save and start fresh
                gameSaveRepository.clearSavedMultiplayerGame()
                resetGame()
            }
        }
    }

    fun rollDice() {
        if (state.remainingRolls > 0 && !state.gameEnded) {
            val newDice = gameService.rollDice(state.diceValues, state.heldDice)
            state = state.copy(
                diceValues = newDice,
                remainingRolls = state.remainingRolls - 1,
                hasRolledAtLeastOnce = true
            )
            // Save state after each action
            saveCurrentState()
        }
    }

    fun toggleHold(index: Int) {
        if (state.remainingRolls < 3 && !state.gameEnded) {
            val newHeld = state.heldDice.toMutableList().also { it[index] = !it[index] }
            state = state.copy(heldDice = newHeld)
            // Save state after each action
            saveCurrentState()
        }
    }

    fun selectScore(combination: String) {
        if (state.gameEnded) return
        val currentScoreMap = if (state.isPlayer1Turn) state.scoreMapPlayer1 else state.scoreMapPlayer2
        if (currentScoreMap[combination] != null) return

        val score = gameService.calculateScore(combination, state.diceValues)
        val newScoreMap1 = if (state.isPlayer1Turn)
            state.scoreMapPlayer1.toMutableMap().also { it[combination] = score }
        else state.scoreMapPlayer1
        val newScoreMap2 = if (!state.isPlayer1Turn)
            state.scoreMapPlayer2.toMutableMap().also { it[combination] = score }
        else state.scoreMapPlayer2

        val gameEnded = combinations.all { (newScoreMap1[it] != null) && (newScoreMap2[it] != null) }

        state = state.copy(
            scoreMapPlayer1 = newScoreMap1,
            scoreMapPlayer2 = newScoreMap2,
            isPlayer1Turn = !state.isPlayer1Turn,
            heldDice = List(5) { false },
            remainingRolls = 3,
            hasRolledAtLeastOnce = false,
            gameEnded = gameEnded
        )

        if (gameEnded) {
            // Clear saved game when finished
            clearSavedState()
        } else {
            // Save state after each action
            saveCurrentState()
        }
    }

    fun resetGame() {
        // Clear saved state first
        clearSavedState()

        // Then reset game state
        state = GameStateMultiplayer(
            diceValues = List(5) { 1 },  // Initial values instead of random
            heldDice = List(5) { false },
            remainingRolls = 3,
            hasRolledAtLeastOnce = false,
            scoreMapPlayer1 = combinations.associateWith { null },
            scoreMapPlayer2 = combinations.associateWith { null },
            isPlayer1Turn = true,
            gameEnded = false
        )
    }

    fun previewScores(): Map<String, Int?> {
        val currentScoreMap = if (state.isPlayer1Turn) state.scoreMapPlayer1 else state.scoreMapPlayer2
        return if (state.hasRolledAtLeastOnce) {
            combinations.associateWith { combo ->
                if (currentScoreMap[combo] == null)
                    gameService.calculateScore(combo, state.diceValues)
                else null
            }
        } else emptyMap()
    }

    /**
     * Save current game state (using GameSaveRepository)
     */
    private fun saveCurrentState() {
        if (shouldSaveState()) {
            viewModelScope.launch {
                val savedGame = SavedMultiplayerGame(
                    diceValues = state.diceValues,
                    heldDice = state.heldDice,
                    remainingRolls = state.remainingRolls,
                    hasRolledAtLeastOnce = state.hasRolledAtLeastOnce,
                    scoreMapPlayer1 = state.scoreMapPlayer1,
                    scoreMapPlayer2 = state.scoreMapPlayer2,
                    isPlayer1Turn = state.isPlayer1Turn,
                    gameEnded = state.gameEnded,
                    player1Name = "Player 1",
                    player2Name = "Player 2"
                )

                gameSaveRepository.saveMultiplayerGame(savedGame)
            }
        }
    }

    /**
     * Check if state should be saved (only if game has started)
     */
    private fun shouldSaveState(): Boolean {
        return state.hasRolledAtLeastOnce ||
                state.scoreMapPlayer1.values.any { it != null } ||
                state.scoreMapPlayer2.values.any { it != null }
    }

    /**
     * Clear saved state
     */
    private fun clearSavedState() {
        viewModelScope.launch {
            gameSaveRepository.clearSavedMultiplayerGame()
        }
    }
}