package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.yahtzee.logic.GameController


class SinglePlayerGameViewModel : ViewModel() {
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
}
