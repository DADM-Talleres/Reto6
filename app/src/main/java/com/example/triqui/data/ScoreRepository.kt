package com.example.triqui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scoreDataStore by preferencesDataStore(name = "score_prefs")

data class ScoreData(
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0
)

class ScoreRepository(private val context: Context) {
    private object Keys {
        val HUMAN_WINS = intPreferencesKey("human_wins")
        val COMPUTER_WINS = intPreferencesKey("computer_wins")
        val TIES = intPreferencesKey("ties")
    }

    val scoreFlow: Flow<ScoreData> = context.scoreDataStore.data.map { prefs ->
        ScoreData(
            humanWins = prefs[Keys.HUMAN_WINS] ?: 0,
            computerWins = prefs[Keys.COMPUTER_WINS] ?: 0,
            ties = prefs[Keys.TIES] ?: 0
        )
    }

    suspend fun incrementHumanWins() {
        context.scoreDataStore.edit { it[Keys.HUMAN_WINS] = (it[Keys.HUMAN_WINS] ?: 0) + 1}
    }

    suspend fun incrementComputerWins() {
        context.scoreDataStore.edit { it[Keys.COMPUTER_WINS] = (it[Keys.COMPUTER_WINS] ?: 0) + 1 }
    }

    suspend fun incrementTies() {
        context.scoreDataStore.edit { it[Keys.TIES] = (it[Keys.TIES] ?: 0) + 1 }
    }
}