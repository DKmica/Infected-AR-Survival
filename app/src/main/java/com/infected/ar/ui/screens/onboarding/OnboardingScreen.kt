package com.infected.ar.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf("Turn anyone into a zombie.", "Live camera + photo uploads.", "Share your infection.")
    var index by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
        GlitchHeader("Welcome to INFECTED AR")
        Text(pages[index], style = MaterialTheme.typography.headlineMedium)
        PrimaryAction(if (index < 2) "Next" else "Start Infecting") {
            if (index < 2) index++ else onFinish()
        }
    }
}
