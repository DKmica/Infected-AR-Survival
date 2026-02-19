package com.infected.ar.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction

@Composable
fun HomeScreen(
    onLive: () -> Unit,
    onUpload: () -> Unit,
    onSurvival: () -> Unit,
    onLibrary: () -> Unit,
    onSkins: () -> Unit,
    onSettings: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Daily Challenge: Share one infection for +20 coins")
        PrimaryAction("Live Infect", onLive)
        PrimaryAction("Upload Photo Infect", onUpload)
        PrimaryAction("Survival Mini-Game", onSurvival)
        Divider()
        PrimaryAction("My Infections", onLibrary)
        PrimaryAction("Skins", onSkins)
        PrimaryAction("Settings", onSettings)
    }
}
