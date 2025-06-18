package com.example.yahtzee.model

data class GameState(
    val diceValues: List<Int?>,
    val scoreMap: MutableMap<String, Int?>,
    val remainingRolls: Int,
    val canSelectScore: Boolean,
    val heldDice: List<Boolean>,
    val gameEnded: Boolean
)
