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

    // Carica una partita salvata se esistente, altrimenti inizializza un nuovo gioco.
    fun loadSavedGameIfExists() {
        viewModelScope.launch {
            try {
                val savedGame = gameSaveRepository.loadSavedSinglePlayerGame()
                if (savedGame != null) {
                    state = GameState(
                        diceValues = savedGame.diceValues,
                        scoreMap = savedGame.scoreMap.toMutableMap(),
                        remainingRolls = savedGame.remainingRolls,
                        canSelectScore = savedGame.canSelectScore,
                        heldDice = savedGame.heldDice,
                        gameEnded = savedGame.gameEnded
                    )
                } else {

                    state = gameService.resetGame()
                    isNewHighScore = false
                }
            } catch (_: Exception) {
                gameSaveRepository.clearSavedSinglePlayerGame()

                state = gameService.resetGame()
                isNewHighScore = false
            }
        }
    }

    // Effettua il lancio dei dadi, aggiornando lo stato del gioco.
    fun rollDice() {
        if (state.remainingRolls > 0 && !state.gameEnded) {
            state = state.copy(
                diceValues = gameService.rollDice(state.diceValues, state.heldDice),
                remainingRolls = state.remainingRolls - 1,
                canSelectScore = true
            )

            saveCurrentState()
        }
    }

    // Gestisce lo stato di "tenuta" dei dadi specificati dall'indice.
    fun toggleHold(index: Int) {
        if (state.remainingRolls < 3 && !state.gameEnded && state.diceValues[index] != null) {
            val newHeld = state.heldDice.toMutableList().also { it[index] = !it[index] }
            state = state.copy(heldDice = newHeld)

            saveCurrentState()
        }
    }

    // Seleziona un punteggio per una combinazione specifica, aggiornando lo stato del gioco.
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
                clearSavedState()
            } else {
                saveCurrentState()
            }
        }
    }

    // Resetta il gioco, cancellando lo stato salvato e inizializzando un nuovo gioco.
    fun resetGame() {

        clearSavedState()

        state = gameService.resetGame()
        isNewHighScore = false
    }

    // Anteprima dei punteggi per le combinazioni disponibili, calcolando i punteggi solo se non sono già stati selezionati.
    fun previewScores(): Map<String, Int?> {
        return if (state.canSelectScore) {
            combinations.associateWith { combination ->
                if (state.scoreMap[combination] == null) {
                    gameService.calculateScore(combination, state.diceValues)
                } else null
            }
        } else emptyMap()
    }

    // Salva il risultato del gioco, calcolando il punteggio totale e verificando se è un nuovo record.
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

                val gameHistoryEntity = GameHistoryEntity(
                    date = System.currentTimeMillis(),
                    score = totalScore
                )

                gameHistoryRepository.insertGameHistory(gameHistoryEntity)


        }
    }

    // Salva lo stato corrente del gioco, calcolando il punteggio totale e verificando se è necessario salvare lo stato.
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

    // Verifica se lo stato del gioco deve essere salvato, basandosi sul numero di lanci rimanenti e sui punteggi selezionati.
    private fun shouldSaveState(): Boolean {
        return state.remainingRolls < 3 || state.scoreMap.values.any { it != null }
    }

    // Cancella lo stato salvato del gioco, ripristinando lo stato iniziale.
    private fun clearSavedState() {
        viewModelScope.launch {
            gameSaveRepository.clearSavedSinglePlayerGame()
        }
    }
}