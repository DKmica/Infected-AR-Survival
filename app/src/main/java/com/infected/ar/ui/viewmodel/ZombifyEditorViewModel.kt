package com.infected.ar.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ZombifyEditorViewModel : ViewModel() {
    var rot by mutableFloatStateOf(0.5f)
        private set
    var veins by mutableFloatStateOf(0.5f)
        private set
    var blood by mutableFloatStateOf(0.5f)
        private set
    var bruises by mutableFloatStateOf(0.5f)
        private set
    var eyeGlow by mutableFloatStateOf(0.7f)
        private set
    var style by mutableStateOf("CLASSIC")
        private set

    fun updateSlider(name: String, value: Float) {
        when (name) {
            "Rot" -> rot = value
            "Veins" -> veins = value
            "Blood" -> blood = value
            "Bruises" -> bruises = value
            "EyeGlow" -> eyeGlow = value
        }
    }

    fun updateStyle(newStyle: String) {
        style = newStyle
    }
}
