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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.infected.ar.ui.components.GlitchHeader

@Composable
fun SkinsStoreScreen() {
    var coins by remember { mutableIntStateOf(120) }
    val packs = listOf("Necro Veins" to 40, "Crimson Rot" to 60, "Demon Glow" to 90)
    val owned = remember { mutableStateListOf<String>() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader("Skins Store")
        Text("Coins: $coins")
        packs.forEach { (name, price) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(name)
                        Text("Premium cosmetic pack")
                    }
                    Button(onClick = {
                        if (coins >= price && !owned.contains(name)) {
                            coins -= price
                            owned += name
                        }
                    }) {
                        Text(if (owned.contains(name)) "Owned" else "Buy $price")
                    }
                }
            }
        }
    }
}
