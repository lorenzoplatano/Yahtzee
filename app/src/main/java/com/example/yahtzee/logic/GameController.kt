package com.example.yahtzee.logic

import com.example.yahtzee.model.GameState

class GameController {
    companion object {
        val combinations = listOf(
            "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
            "Three of a Kind", "Four of a Kind", "Full House", "Small Straight", "Large Straight",
            "Yahtzee", "Chance"
        )
    }

    fun rollDice(currentDice: List<Int>, held: List<Boolean>): List<Int> {
        return currentDice.mapIndexed { i, value ->
            if (held[i]) value else (1..6).random()
        }
    }

    fun calculateScore(
        combination: String,
        diceValues: List<Int>,
        scoreMap: Map<String, Int?>
    ): Int {
        val counts = diceValues.groupingBy { it }.eachCount()
        val total = diceValues.sum()

        return when (combination) {
            "Aces" -> diceValues.filter { it == 1 }.sum()
            "Twos" -> diceValues.filter { it == 2 }.sum()
            "Threes" -> diceValues.filter { it == 3 }.sum()
            "Fours" -> diceValues.filter { it == 4 }.sum()
            "Fives" -> diceValues.filter { it == 5 }.sum()
            "Sixes" -> diceValues.filter { it == 6 }.sum()
            "Three of a Kind" -> if (counts.values.any { it >= 3 }) total else 0
            "Four of a Kind" -> if (counts.values.any { it >= 4 }) total else 0
            "Full House" -> if (counts.values.contains(3) && counts.values.contains(2)) 25 else 0
            "Small Straight" -> {
                val unique = diceValues.toSet()
                val straights = listOf(
                    setOf(1, 2, 3, 4),
                    setOf(2, 3, 4, 5),
                    setOf(3, 4, 5, 6)
                )
                if (straights.any { it.all { n -> unique.contains(n) } }) 30 else 0
            }
            "Large Straight" -> {
                val sorted = diceValues.toSortedSet()
                if (sorted == setOf(1, 2, 3, 4, 5) || sorted == setOf(2, 3, 4, 5, 6)) 40 else 0
            }
            "Yahtzee" -> if (counts.values.any { it == 5 }) 50 else 0
            "Chance" -> total
            else -> 0
        }
    }

    fun isGameEnded(scoreMap: Map<String, Int?>): Boolean {
        return combinations.all { scoreMap[it] != null }
    }

    fun resetGame(): GameState {
        return GameState(
            diceValues = List(5) { 1 },
            scoreMap = combinations.associateWith { null }.toMutableMap(),
            remainingRolls = 3,
            canSelectScore = false,
            heldDice = List(5) { false },
            gameEnded = false
        )
    }
}