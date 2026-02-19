package com.infected.ar.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.infected.ar.media.export.ShareHelper
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel
import java.io.File

@Composable
fun MyInfectionsScreen(vm: AppViewModel, onDetail: (String) -> Unit) {
    val items by vm.infections.collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        GlitchHeader("My Infections")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { inf ->
                Card(Modifier.fillMaxWidth().clickable { onDetail(inf.id) }) {
                    Column(Modifier.padding(12.dp)) {
                        Text(inf.style, fontWeight = FontWeight.Bold)
                        Text(inf.sourceType)
                        Text("Intensity ${inf.intensity}")
                    }
                }
            }
        }
    }
}

@Composable
fun InfectionDetailScreen(id: String, vm: AppViewModel) {
    val items by vm.infections.collectAsState()
    val item = items.firstOrNull { it.id == id }
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Infection Detail")
        Text("ID: $id")
        Text("Style: ${item?.style ?: "Unknown"}")
        Text("Before/after + Media3 preview placeholder")
        PrimaryAction("Share Again") {
            val f = File(context.filesDir, "shared/placeholder.txt").apply { parentFile?.mkdirs(); writeText("INFECTED") }
            ShareHelper.shareFile(context, f, "text/plain")
        }
        item?.let { current -> PrimaryAction("Delete") { vm.deleteInfection(current) } }
    }
}
