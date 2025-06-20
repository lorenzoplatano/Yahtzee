package com.example.yahtzee.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.yahtzee.model.SavedGamesContainer
import com.example.yahtzee.model.SavedSinglePlayerGame
import com.example.yahtzee.model.SavedMultiplayerGame
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

/**
 * Repository dedicato esclusivamente ai salvataggi delle partite in corso
 * Usa DataStore per performance e sicurezza migliori rispetto a SharedPreferences
 */
class GameSaveRepository(
    private val context: Context
) {

    private val gson = Gson()
    private val SAVED_GAMES_KEY = stringPreferencesKey("saved_games_container")

    // DataStore extension per il context
    private val Context.gameStateDataStore: DataStore<Preferences> by preferencesDataStore("game_saves")

    /**
     * Salva una partita single player
     */
    suspend fun saveSinglePlayerGame(gameState: SavedSinglePlayerGame) {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            singlePlayerGame = gameState,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }

    /**
     * Salva una partita multiplayer
     */
    suspend fun saveMultiplayerGame(gameState: SavedMultiplayerGame) {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            multiplayerGame = gameState,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }

    /**
     * Carica partita single player salvata
     */
    suspend fun loadSavedSinglePlayerGame(): SavedSinglePlayerGame? {
        return loadSavedGamesContainer().singlePlayerGame
    }

    /**
     * Carica partita multiplayer salvata
     */
    suspend fun loadSavedMultiplayerGame(): SavedMultiplayerGame? {
        return loadSavedGamesContainer().multiplayerGame
    }


    /**
     * Cancella partita single player salvata
     */
    suspend fun clearSavedSinglePlayerGame() {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            singlePlayerGame = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }

    /**
     * Cancella partita multiplayer salvata
     */
    suspend fun clearSavedMultiplayerGame() {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            multiplayerGame = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }

    /**
     * Cancella tutti i salvataggi
     */

    // ========== METODI PRIVATI ==========

    private suspend fun saveSavedGamesContainer(container: SavedGamesContainer) {
        val json = gson.toJson(container)
        context.gameStateDataStore.edit { preferences ->
            preferences[SAVED_GAMES_KEY] = json
        }
    }

    private suspend fun loadSavedGamesContainer(): SavedGamesContainer {
        return try {
            val preferences = context.gameStateDataStore.data.first()
            val json = preferences[SAVED_GAMES_KEY]
            if (json != null) {
                gson.fromJson(json, SavedGamesContainer::class.java)
            } else {
                SavedGamesContainer()
            }
        } catch (_: Exception) {
            SavedGamesContainer()
        }
    }
}