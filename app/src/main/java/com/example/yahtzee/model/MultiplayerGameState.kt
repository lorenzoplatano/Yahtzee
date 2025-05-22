package com.example.yahtzee.model

data class MultiplayerGameState(
    val diceValues: List<Int> = List(5) { 1 },
    val heldDice: List<Boolean> = List(5) { false },
    val remainingRolls: Int = 3,
    val hasRolledAtLeastOnce: Boolean = false,
    val scoreMapPlayer1: Map<String, Int?> = emptyMap(),
    val scoreMapPlayer2: Map<String, Int?> = emptyMap(),
    val isPlayer1Turn: Boolean = true,
    val gameEnded: Boolean = false
)