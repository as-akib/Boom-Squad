package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bomb_squad_prefs")

data class UserGameData(
    val unlockedLevel: Int = 1,
    val levelStars: Map<Int, Int> = emptyMap(),
    val lives: Int = 5,
    val lastLifeLostTimestamp: Long = 0L,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
) {
    val maxLives: Int = 5
    // 20 minutes per life in milliseconds
    val refillIntervalMs: Long = 20 * 60 * 1000L

    fun getEffectiveLives(currentTimeMs: Long): Pair<Int, Long> {
        if (lives >= maxLives) {
            return Pair(maxLives, 0L)
        }
        if (lastLifeLostTimestamp <= 0L) {
            return Pair(lives, refillIntervalMs)
        }
        val elapsed = currentTimeMs - lastLifeLostTimestamp
        if (elapsed <= 0L) {
            return Pair(lives, refillIntervalMs)
        }
        val livesToAdd = (elapsed / refillIntervalMs).toInt()
        val newLives = (lives + livesToAdd).coerceAtMost(maxLives)
        if (newLives >= maxLives) {
            return Pair(maxLives, 0L)
        }
        val remainingMs = refillIntervalMs - (elapsed % refillIntervalMs)
        return Pair(newLives, remainingMs)
    }
}

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val UNLOCKED_LEVEL = intPreferencesKey("unlocked_level")
        val LIVES = intPreferencesKey("lives")
        val LAST_LIFE_LOST_TIMESTAMP = longPreferencesKey("last_life_lost_timestamp")
        val LEVEL_STARS = stringPreferencesKey("level_stars_map") // "1:3,2:2,3:1"
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    }

    val userGameData: Flow<UserGameData> = context.dataStore.data.map { prefs ->
        val unlockedLevel = prefs[PreferencesKeys.UNLOCKED_LEVEL] ?: 1
        val lives = prefs[PreferencesKeys.LIVES] ?: 5
        val lastLifeLostTimestamp = prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] ?: 0L
        val sound = prefs[PreferencesKeys.SOUND_ENABLED] ?: true
        val haptics = prefs[PreferencesKeys.HAPTICS_ENABLED] ?: true
        val starsStr = prefs[PreferencesKeys.LEVEL_STARS] ?: ""

        val starsMap = if (starsStr.isNotBlank()) {
            starsStr.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val lvl = parts[0].toIntOrNull()
                    val star = parts[1].toIntOrNull()
                    if (lvl != null && star != null) lvl to star else null
                } else null
            }.toMap()
        } else {
            emptyMap()
        }

        UserGameData(
            unlockedLevel = unlockedLevel,
            levelStars = starsMap,
            lives = lives,
            lastLifeLostTimestamp = lastLifeLostTimestamp,
            soundEnabled = sound,
            hapticsEnabled = haptics
        )
    }

    suspend fun completeLevel(level: Int, starsEarned: Int) {
        context.dataStore.edit { prefs ->
            val currentUnlocked = prefs[PreferencesKeys.UNLOCKED_LEVEL] ?: 1
            if (level >= currentUnlocked) {
                prefs[PreferencesKeys.UNLOCKED_LEVEL] = level + 1
            }

            val starsStr = prefs[PreferencesKeys.LEVEL_STARS] ?: ""
            val mutableStars = if (starsStr.isNotBlank()) {
                starsStr.split(",").mapNotNull { entry ->
                    val parts = entry.split(":")
                    if (parts.size == 2) {
                        val lvl = parts[0].toIntOrNull()
                        val star = parts[1].toIntOrNull()
                        if (lvl != null && star != null) lvl to star else null
                    } else null
                }.toMap().toMutableMap()
            } else {
                mutableMapOf()
            }

            val existingStars = mutableStars[level] ?: 0
            if (starsEarned > existingStars) {
                mutableStars[level] = starsEarned
            }

            prefs[PreferencesKeys.LEVEL_STARS] = mutableStars.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    suspend fun loseLife(currentTimeMs: Long) {
        context.dataStore.edit { prefs ->
            val currentLives = prefs[PreferencesKeys.LIVES] ?: 5
            val newLives = (currentLives - 1).coerceAtLeast(0)
            prefs[PreferencesKeys.LIVES] = newLives
            if (currentLives == 5 || prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] == null || prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] == 0L) {
                prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] = currentTimeMs
            }
        }
    }

    suspend fun addLife(currentTimeMs: Long) {
        context.dataStore.edit { prefs ->
            val currentLives = prefs[PreferencesKeys.LIVES] ?: 5
            val newLives = (currentLives + 1).coerceAtMost(5)
            prefs[PreferencesKeys.LIVES] = newLives
            if (newLives >= 5) {
                prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] = 0L
            } else {
                prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] = currentTimeMs
            }
        }
    }

    suspend fun refillLives() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LIVES] = 5
            prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] = 0L
        }
    }

    suspend fun setSound(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun resetProgress() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.UNLOCKED_LEVEL] = 1
            prefs[PreferencesKeys.LIVES] = 5
            prefs[PreferencesKeys.LAST_LIFE_LOST_TIMESTAMP] = 0L
            prefs[PreferencesKeys.LEVEL_STARS] = ""
        }
    }
}
