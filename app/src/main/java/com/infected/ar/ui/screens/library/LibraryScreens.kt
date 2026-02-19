package com.infected.ar.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
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

    val player = item?.revealVideoPath?.takeIf { it.isNotBlank() }?.let { path ->
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(File(path).toURI().toString()))
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(player) {
        onDispose { player?.release() }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Infection Detail")
        Text("ID: $id")
        Text("Style: ${item?.style ?: "Unknown"}")
        Text("Before: ${item?.beforeImagePath ?: "N/A"}")
        Text("After: ${item?.afterImagePath ?: "N/A"}")
        if (player != null) {
            AndroidView(
                factory = { PlayerView(it).apply { this.player = player } },
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
        } else {
            Text("Reveal clip unavailable for this infection.")
        }
        PrimaryAction("Share Again") {
            val shareTarget = item?.afterImagePath?.let { File(it) }?.takeIf { it.exists() }
                ?: File(context.filesDir, "shared/placeholder.txt").apply { parentFile?.mkdirs(); writeText("INFECTED") }
            val mime = if (shareTarget.extension.lowercase() == "png") "image/png" else "text/plain"
            ShareHelper.shareFile(context, shareTarget, mime)
        }
        item?.let { current -> PrimaryAction("Delete") { vm.deleteInfection(current) } }
    }
}
