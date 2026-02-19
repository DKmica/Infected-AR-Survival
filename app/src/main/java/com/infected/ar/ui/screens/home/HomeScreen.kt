package com.infected.ar.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel

@Composable
fun HomeScreen(
    vm: AppViewModel,
    onLive: () -> Unit,
    onUpload: () -> Unit,
    onSurvival: () -> Unit,
    onLibrary: () -> Unit,
    onSkins: () -> Unit,
    onSettings: () -> Unit
) {
    val settings by vm.settings.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Daily Challenge: Share one infection for +20 coins")
        Text("Coins: ${settings.coins}")
        PrimaryAction("Claim Daily +20") { vm.rewardCoins(20) }
        PrimaryAction("Live Infect", onLive)
        PrimaryAction("Upload Photo Infect", onUpload)
        PrimaryAction("Survival Mini-Game", onSurvival)
        Divider()
        PrimaryAction("My Infections", onLibrary)
        PrimaryAction("Skins", onSkins)
        PrimaryAction("Settings", onSettings)
    }
}
