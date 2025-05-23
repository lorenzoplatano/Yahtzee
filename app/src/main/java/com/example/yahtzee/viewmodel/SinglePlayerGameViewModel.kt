package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yahtzee.logic.GameController
import com.example.yahtzee.db.AppDatabase
import com.example.yahtzee.db.GameHistoryEntity
import kotlinx.coroutines.launch

class SinglePlayerGameViewModel(
    private val db: AppDatabase
) : ViewModel() {
    private val controller = GameController()

    var state by mutableStateOf(controller.resetGame())
        private set

    val combinations = GameController.combinations

    fun rollDice() {
        if (state.remainingRolls > 0 && !state.gameEnded) {
            state = state.copy(
                diceValues = controller.rollDice(state.diceValues, state.heldDice),
                remainingRolls = state.remainingRolls - 1,
                canSelectScore = true
            )
        }
    }

    fun toggleHold(index: Int) {
        if (state.remainingRolls < 3 && !state.gameEnded) {
            val newHeld = state.heldDice.toMutableList().also { it[index] = !it[index] }
            state = state.copy(heldDice = newHeld)
        }
    }

    fun selectScore(combination: String) {
        if (state.canSelectScore && state.scoreMap[combination] == null && !state.gameEnded) {
            val newScoreMap = state.scoreMap.toMutableMap()
            newScoreMap[combination] = controller.calculateScore(combination, state.diceValues, state.scoreMap)
            val ended = controller.isGameEnded(newScoreMap)
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
        state = controller.resetGame()
    }

    fun previewScores(): Map<String, Int?> {
        return if (state.canSelectScore) {
            combinations.associateWith { combination ->
                if (state.scoreMap[combination] == null) {
                    controller.calculateScore(combination, state.diceValues, state.scoreMap)
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
            db.gameHistoryDao().insertGameHistory(
                GameHistoryEntity(
                    date = System.currentTimeMillis(),
                    score = totalScore
                )
            )
        }
    }
}
