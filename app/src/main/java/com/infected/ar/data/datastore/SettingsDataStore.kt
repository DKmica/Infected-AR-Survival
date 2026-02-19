package com.infected.ar.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "infected_settings")

data class SettingsState(
    val onboardingCompleted: Boolean = false,
    val defaultStyle: String = "CLASSIC",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val watermarkEnabled: Boolean = true
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val OnboardingCompleted = booleanPreferencesKey("onboardingCompleted")
        val DefaultStyle = stringPreferencesKey("defaultStyle")
        val SoundEnabled = booleanPreferencesKey("soundEnabled")
        val HapticsEnabled = booleanPreferencesKey("hapticsEnabled")
        val WatermarkEnabled = booleanPreferencesKey("watermarkEnabled")
    }

    val settings: Flow<SettingsState> = context.settingsDataStore.data.map { pref: Preferences ->
        SettingsState(
            onboardingCompleted = pref[Keys.OnboardingCompleted] ?: false,
            defaultStyle = pref[Keys.DefaultStyle] ?: "CLASSIC",
            soundEnabled = pref[Keys.SoundEnabled] ?: true,
            hapticsEnabled = pref[Keys.HapticsEnabled] ?: true,
            watermarkEnabled = pref[Keys.WatermarkEnabled] ?: true
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
}
