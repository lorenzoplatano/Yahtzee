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

// Repository per gestire lo stato dei giochi salvati
class GameSaveRepository(
    private val context: Context
) {

    private val gson = Gson()
    private val SAVED_GAMES_KEY = stringPreferencesKey("saved_games_container")


    private val Context.gameStateDataStore: DataStore<Preferences> by preferencesDataStore("game_saves")

    // Salva lo stato di un gioco in modalità singola
    suspend fun saveSinglePlayerGame(gameState: SavedSinglePlayerGame) {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            singlePlayerGame = gameState,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }


    // Salva lo stato di un gioco in modalità multiplayer
    suspend fun saveMultiplayerGame(gameState: SavedMultiplayerGame) {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            multiplayerGame = gameState,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }

    // Carica lo stato del gioco salvato in modalità singola
    suspend fun loadSavedSinglePlayerGame(): SavedSinglePlayerGame? {
        return loadSavedGamesContainer().singlePlayerGame
    }


    // Carica lo stato del gioco salvato in modalità multiplayer
    suspend fun loadSavedMultiplayerGame(): SavedMultiplayerGame? {
        return loadSavedGamesContainer().multiplayerGame
    }


    // Cancella lo stato del gioco salvato in modalità singola
    suspend fun clearSavedSinglePlayerGame() {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            singlePlayerGame = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }


    // Cancella lo stato del gioco salvato in modalità multiplayer
    suspend fun clearSavedMultiplayerGame() {
        val currentContainer = loadSavedGamesContainer()
        val updatedContainer = currentContainer.copy(
            multiplayerGame = null,
            lastUpdated = System.currentTimeMillis()
        )
        saveSavedGamesContainer(updatedContainer)
    }


    // Salva il contenitore dei giochi salvati nel DataStore
    private suspend fun saveSavedGamesContainer(container: SavedGamesContainer) {
        val json = gson.toJson(container)
        context.gameStateDataStore.edit { preferences ->
            preferences[SAVED_GAMES_KEY] = json
        }
    }

    // Carica il contenitore dei giochi salvati dal DataStore
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