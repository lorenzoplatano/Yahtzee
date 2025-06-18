package com.example.yahtzee.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.yahtzee.logic.GameController
import com.example.yahtzee.model.MultiplayerGameState


class MultiplayerGameViewModel : ViewModel() {
    private val logic = GameController()
    val combinations = GameController.combinations

    var state by mutableStateOf(MultiplayerGameState())
        private set

    fun rollDice() {
        if (state.remainingRolls > 0 && !state.gameEnded) {
            val newDice = logic.rollDice(state.diceValues, state.heldDice)
            state = state.copy(
                diceValues = newDice,
                remainingRolls = state.remainingRolls - 1,
                hasRolledAtLeastOnce = true
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
        if (state.gameEnded) return
        val currentScoreMap = if (state.isPlayer1Turn) state.scoreMapPlayer1 else state.scoreMapPlayer2
        if (currentScoreMap[combination] != null) return

        val score = logic.calculateScore(combination, state.diceValues, currentScoreMap)
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
    }

    fun resetGame() {
        state = MultiplayerGameState(
            diceValues = List(5) { (1..6).random() }
        )
    }

    fun previewScores(): Map<String, Int?> {
        val currentScoreMap = if (state.isPlayer1Turn) state.scoreMapPlayer1 else state.scoreMapPlayer2
        return if (state.hasRolledAtLeastOnce) {
            combinations.associateWith { combo ->
                if (currentScoreMap[combo] == null)
                    logic.calculateScore(combo, state.diceValues, currentScoreMap)
                else null
            }
        } else emptyMap()
    }
}
