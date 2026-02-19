package com.infected.ar.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "infected_settings")

data class SettingsState(
    val onboardingCompleted: Boolean = false,
    val defaultStyle: String = "CLASSIC",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val watermarkEnabled: Boolean = true,
    val coins: Int = 120,
    val ownedSkins: Set<String> = emptySet()
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val OnboardingCompleted = booleanPreferencesKey("onboardingCompleted")
        val DefaultStyle = stringPreferencesKey("defaultStyle")
        val SoundEnabled = booleanPreferencesKey("soundEnabled")
        val HapticsEnabled = booleanPreferencesKey("hapticsEnabled")
        val WatermarkEnabled = booleanPreferencesKey("watermarkEnabled")
        val Coins = intPreferencesKey("coins")
        val OwnedSkins = stringSetPreferencesKey("ownedSkins")
    }

    val settings: Flow<SettingsState> = context.settingsDataStore.data.map { pref: Preferences ->
        SettingsState(
            onboardingCompleted = pref[Keys.OnboardingCompleted] ?: false,
            defaultStyle = pref[Keys.DefaultStyle] ?: "CLASSIC",
            soundEnabled = pref[Keys.SoundEnabled] ?: true,
            hapticsEnabled = pref[Keys.HapticsEnabled] ?: true,
            watermarkEnabled = pref[Keys.WatermarkEnabled] ?: true,
            coins = pref[Keys.Coins] ?: 120,
            ownedSkins = pref[Keys.OwnedSkins] ?: emptySet()
        )
    }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.settingsDataStore.edit { it[Keys.OnboardingCompleted] = value }
    }

    suspend fun updateStyle(style: String) {
        context.settingsDataStore.edit { it[Keys.DefaultStyle] = style }
    }

    suspend fun updateToggles(sound: Boolean, haptics: Boolean, watermark: Boolean) {
        context.settingsDataStore.edit {
            it[Keys.SoundEnabled] = sound
            it[Keys.HapticsEnabled] = haptics
            it[Keys.WatermarkEnabled] = watermark
        }
    }

    suspend fun addCoins(amount: Int) {
        context.settingsDataStore.edit {
            val current = it[Keys.Coins] ?: 120
            it[Keys.Coins] = (current + amount).coerceAtLeast(0)
        }
    }

    suspend fun purchaseSkin(name: String, price: Int): Boolean {
        var purchased = false
        context.settingsDataStore.edit {
            val currentCoins = it[Keys.Coins] ?: 120
            val owned = (it[Keys.OwnedSkins] ?: emptySet()).toMutableSet()
            if (!owned.contains(name) && currentCoins >= price) {
                owned += name
                it[Keys.OwnedSkins] = owned
                it[Keys.Coins] = currentCoins - price
                purchased = true
            }
        }
        return purchased
    }
}
