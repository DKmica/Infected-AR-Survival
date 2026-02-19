package com.infected.ar.ui.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.infected.ar.InfectedApp
import com.infected.ar.data.datastore.SettingsState
import com.infected.ar.data.db.InfectionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as InfectedApp
    val settings: StateFlow<SettingsState> = app.settingsDataStore.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    val infections = app.infectionRepository.observeInfections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun completeOnboarding() = viewModelScope.launch {
        app.settingsDataStore.setOnboardingCompleted(true)
    }

    fun updateStyle(style: String) = viewModelScope.launch { app.settingsDataStore.updateStyle(style) }

    fun updateToggles(sound: Boolean, haptics: Boolean, watermark: Boolean) = viewModelScope.launch {
        app.settingsDataStore.updateToggles(sound, haptics, watermark)
    }

    fun saveInfection(item: InfectionEntity) = viewModelScope.launch { app.infectionRepository.save(item) }

    fun deleteInfection(item: InfectionEntity) = viewModelScope.launch { app.infectionRepository.delete(item) }

    fun purchaseSkin(name: String, price: Int) = viewModelScope.launch {
        app.settingsDataStore.purchaseSkin(name, price)
    }

    fun rewardCoins(amount: Int) = viewModelScope.launch {
        app.settingsDataStore.addCoins(amount)
    }
}
