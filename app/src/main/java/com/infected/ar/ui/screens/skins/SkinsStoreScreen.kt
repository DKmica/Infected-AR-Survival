package com.infected.ar.ui.screens.skins

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.navigation.AppViewModel

@Composable
fun SkinsStoreScreen(vm: AppViewModel) {
    val settings by vm.settings.collectAsState()
    val packs = listOf("Necro Veins" to 40, "Crimson Rot" to 60, "Demon Glow" to 90)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader("Skins Store")
        Text("Coins: ${settings.coins}")
        packs.forEach { (name, price) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(name)
                        Text("Premium cosmetic pack")
                    }
                    Button(onClick = { vm.purchaseSkin(name, price) }) {
                        Text(if (settings.ownedSkins.contains(name)) "Owned" else "Buy $price")
                    }
                }
            }
        }
    }
}
