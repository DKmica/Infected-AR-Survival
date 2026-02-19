package com.infected.ar

import android.content.Context
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.infected.ar.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
}
