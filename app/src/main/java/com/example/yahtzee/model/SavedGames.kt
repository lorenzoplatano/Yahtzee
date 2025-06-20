package com.example.yahtzee.model

data class SavedGamesContainer(
    val singlePlayerGame: SavedSinglePlayerGame? = null,
    val multiplayerGame: SavedMultiplayerGame? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)


data class SavedSinglePlayerGame(
    val diceValues: List<Int?>,
    val scoreMap: Map<String, Int?>,
    val remainingRolls: Int,
    val canSelectScore: Boolean,
    val heldDice: List<Boolean>,
    val gameEnded: Boolean,
    val currentScore: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)


data class SavedMultiplayerGame(
    val diceValues: List<Int>,
    val heldDice: List<Boolean>,
    val remainingRolls: Int,
    val hasRolledAtLeastOnce: Boolean,
    val scoreMapPlayer1: Map<String, Int?>,
    val scoreMapPlayer2: Map<String, Int?>,
    val isPlayer1Turn: Boolean,
    val gameEnded: Boolean,
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val timestamp: Long = System.currentTimeMillis()
)