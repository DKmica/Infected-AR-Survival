package com.infected.ar

import android.content.Context
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.infected.ar.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDataStoreTest {
    @Test
    fun writesAndReadsSettings() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.dataStoreFile("infected_settings.preferences_pb").delete()
        val store = SettingsDataStore(context)
        store.setOnboardingCompleted(true)
        store.updateToggles(sound = false, haptics = true, watermark = false)
        store.addCoins(30)
        store.purchaseSkin("Necro Veins", 40)
        val state = store.settings.first()
        assertTrue(state.onboardingCompleted)
        assertTrue(state.hapticsEnabled)
        assertTrue(state.ownedSkins.contains("Necro Veins"))
        assertEquals(110, state.coins)
    }

    @Test
    fun rejectsPurchaseWhenInsufficientCoins() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.dataStoreFile("infected_settings.preferences_pb").delete()
        val store = SettingsDataStore(context)
        val bought = store.purchaseSkin("Demon Glow", 999)
        val state = store.settings.first()
        assertFalse(bought)
        assertFalse(state.ownedSkins.contains("Demon Glow"))
        assertEquals(120, state.coins)
    }
    @Test
    fun doesNotDoublePurchaseOwnedSkin() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.dataStoreFile("infected_settings.preferences_pb").delete()
        val store = SettingsDataStore(context)
        val first = store.purchaseSkin("Necro Veins", 40)
        val second = store.purchaseSkin("Necro Veins", 40)
        val state = store.settings.first()
        assertTrue(first)
        assertFalse(second)
        assertEquals(80, state.coins)
    }

}
