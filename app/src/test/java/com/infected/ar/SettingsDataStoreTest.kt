package com.infected.ar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.infected.ar.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDataStoreTest {
    @Test
    fun writesAndReadsSettings() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = SettingsDataStore(context)
        store.setOnboardingCompleted(true)
        store.updateToggles(sound = false, haptics = true, watermark = false)
        val state = store.settings.first()
        assertTrue(state.onboardingCompleted)
        assertTrue(state.hapticsEnabled)
    }
}
