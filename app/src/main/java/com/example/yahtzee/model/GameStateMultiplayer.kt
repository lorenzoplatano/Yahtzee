package com.example.yahtzee.model

data class GameStateMultiplayer(
    val diceValues: List<Int?>,  // Modificato da List<Int> a List<Int?> per supportare valori nulli
    val scoreMap: MutableMap<String, Int?>,
    val remainingRolls: Int,
    val canSelectScore: Boolean,
    val heldDice: List<Boolean>,
    val gameEnded: Boolean
)
