package com.example.yahtzee.service

import com.example.yahtzee.model.GameState

// Classe Service usata dalle ViewModel SinglePlayer e Multiplayer per gestire la logica del gioco
class GameService {
    companion object {
        val combinations = listOf(
            "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
            "Three of a Kind", "Four of a Kind", "Full House", "Small Straight", "Large Straight",
            "Yahtzee", "Chance"
        )
    }

    // Funzione per lanciare i dadi
    fun rollDice(currentDice: List<Int?>, held: List<Boolean>): List<Int> {
        return currentDice.mapIndexed { i, value ->
            if (held[i] && value != null) value else (1..6).random()
        }
    }

    // Funzione per calcolare il punteggio in base alla combinazione scelta e ai valori dei dadi
    fun calculateScore(
        combination: String,
        diceValues: List<Int?>
    ): Int {
        val nonNullValues = diceValues.filterNotNull()
        
        if (nonNullValues.isEmpty()) return 0
        
        val counts = nonNullValues.groupingBy { it }.eachCount()
        val total = nonNullValues.sum()

        return when (combination) {
            "Aces" -> nonNullValues.filter { it == 1 }.sum()
            "Twos" -> nonNullValues.filter { it == 2 }.sum()
            "Threes" -> nonNullValues.filter { it == 3 }.sum()
            "Fours" -> nonNullValues.filter { it == 4 }.sum()
            "Fives" -> nonNullValues.filter { it == 5 }.sum()
            "Sixes" -> nonNullValues.filter { it == 6 }.sum()
            "Three of a Kind" -> if (counts.values.any { it >= 3 }) total else 0
            "Four of a Kind" -> if (counts.values.any { it >= 4 }) total else 0
            "Full House" -> if (counts.values.contains(3) && counts.values.contains(2)) 25 else 0
            "Small Straight" -> {
                val unique = nonNullValues.toSet()
                val straights = listOf(
                    setOf(1, 2, 3, 4),
                    setOf(2, 3, 4, 5),
                    setOf(3, 4, 5, 6)
                )
                if (straights.any { it.all { n -> unique.contains(n) } }) 30 else 0
            }
            "Large Straight" -> {
                val sorted = nonNullValues.toSortedSet()
                if (sorted == setOf(1, 2, 3, 4, 5) || sorted == setOf(2, 3, 4, 5, 6)) 40 else 0
            }
            "Yahtzee" -> if (counts.values.any { it == 5 }) 50 else 0
            "Chance" -> total
            else -> 0
        }
    }

    // Funzione per verificare se il gioco è terminato, controllando se tutte le combinazioni hanno un punteggio
    fun isGameEnded(scoreMap: Map<String, Int?>): Boolean {
        return combinations.all { scoreMap[it] != null }
    }

    // Funzione per resettare lo stato del gioco, creando un nuovo GameState inizializzato
    fun resetGame(): GameState {
        return GameState(
            diceValues = List(5) { null },
            scoreMap = combinations.associateWith { null }.toMutableMap(),
            remainingRolls = 3,
            canSelectScore = false,
            heldDice = List(5) { false },
            gameEnded = false
        )
    }
}
