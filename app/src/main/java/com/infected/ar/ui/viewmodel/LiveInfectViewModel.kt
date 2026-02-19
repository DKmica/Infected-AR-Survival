package com.infected.ar.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveInfectViewModel : ViewModel() {
    var intensity by mutableFloatStateOf(0.7f)
        private set
    var style by mutableStateOf("CLASSIC")
        private set
    var infecting by mutableStateOf(false)
        private set
    var faceCount by mutableIntStateOf(0)
        private set
    var infectionStage by mutableStateOf("IDLE")
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun onIntensityChanged(value: Float) {
        intensity = value
    }

    fun onStyleChanged(value: String) {
        style = value
    }

    fun onFacesDetected(count: Int) {
        faceCount = count
        if (count == 0 && !infecting) {
            message = "No face detected. Point camera at a face."
        } else if (count > 0) {
            message = null
        }
    }

    fun triggerInfectionSequence(onComplete: () -> Unit) {
        if (faceCount == 0) {
            message = "Cannot infect: no face detected."
            return
        }
        viewModelScope.launch {
            infecting = true
            infectionStage = "GLITCH"
            delay(650)
            infectionStage = "SPREAD"
            delay(650)
            infectionStage = "FINAL"
            delay(700)
            infecting = false
            infectionStage = "IDLE"
            onComplete()
        }
    }

    fun dismissMessage() {
        message = null
    }
}
