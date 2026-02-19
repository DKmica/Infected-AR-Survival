package com.infected.ar.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel

@Composable
fun SettingsScreen(vm: AppViewModel, onPrivacy: () -> Unit, onTerms: () -> Unit) {
    val settings by vm.settings.collectAsState()
    var sound by remember(settings.soundEnabled) { mutableStateOf(settings.soundEnabled) }
    var haptics by remember(settings.hapticsEnabled) { mutableStateOf(settings.hapticsEnabled) }
    var watermark by remember(settings.watermarkEnabled) { mutableStateOf(settings.watermarkEnabled) }
    var style by remember(settings.defaultStyle) { mutableStateOf(settings.defaultStyle) }
    val context = LocalContext.current
    val cameraGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val micGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Settings")
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Sound", Modifier.weight(1f)); Switch(sound, { sound = it }) }
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Haptics", Modifier.weight(1f)); Switch(haptics, { haptics = it }) }
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Watermark", Modifier.weight(1f)); Switch(watermark, { watermark = it }) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CLASSIC", "RUNNER", "DEMON").forEach {
                FilterChip(selected = style == it, onClick = { style = it; vm.updateStyle(it) }, label = { Text(it) })
            }
        }
        PrimaryAction("Save") { vm.updateToggles(sound, haptics, watermark) }
        PrimaryAction("Privacy Policy", onPrivacy)
        PrimaryAction("Terms", onTerms)
        PrimaryAction("Contact Support") {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@infectedar.app"))
            context.startActivity(intent)
        }
        Text("Camera permission: ${if (cameraGranted) "Granted" else "Denied"}")
        Text("Microphone permission: ${if (micGranted) "Granted" else "Denied"}")
    }
}
