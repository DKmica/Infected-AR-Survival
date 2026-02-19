package com.infected.ar.ui.screens.upload

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.infected.ar.data.db.InfectionEntity
import com.infected.ar.media.export.RevealExporter
import com.infected.ar.media.export.ShareHelper
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel
import com.infected.ar.ui.navigation.Routes
import com.infected.ar.ui.viewmodel.ZombifyEditorViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Composable
fun UploadPickerScreen(nav: NavController) {
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        nav.navigate(Routes.FaceSelect)
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader("Upload Photo Infect")
        PrimaryAction("Pick from gallery") { pickMedia.launch(ActivityResultContracts.PickVisualMedia.ImageOnly) }
        PrimaryAction("Take photo") { nav.navigate(Routes.FaceSelect) }
    }
}

@Composable
fun FaceSelectCropScreen(nav: NavController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader("Select Face")
        Text("ML Kit face detection placeholder: choose detected face then crop.")
        Spacer(Modifier.height(12.dp))
        PrimaryAction("Continue to Editor") { nav.navigate(Routes.ZombifyEditor) }
    }
}

@Composable
fun ZombifyEditorScreen(nav: NavController, vm: AppViewModel, editorVm: ZombifyEditorViewModel = viewModel()) {
    val context = LocalContext.current
    val exporter = remember { RevealExporter(context) }
    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Zombify Editor")
        Text("Before/After toggle placeholder")
        listOf("Rot" to editorVm.rot, "Veins" to editorVm.veins, "Blood" to editorVm.blood, "Bruises" to editorVm.bruises, "EyeGlow" to editorVm.eyeGlow).forEach { (n, v) ->
            Text(n)
            Slider(value = v, onValueChange = { editorVm.updateSlider(n, it) })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CLASSIC", "RUNNER", "DEMON").forEach {
                FilterChip(selected = editorVm.style == it, onClick = { editorVm.updateStyle(it) }, label = { Text(it) })
            }
        }
        PrimaryAction("Save") {
            val sliders = Json.encodeToString(mapOf("rot" to editorVm.rot, "veins" to editorVm.veins, "blood" to editorVm.blood, "bruises" to editorVm.bruises, "eyeGlow" to editorVm.eyeGlow))
            vm.saveInfection(
                InfectionEntity(UUID.randomUUID().toString(), System.currentTimeMillis(), "UPLOAD", editorVm.style, (editorVm.rot * 100).toInt(), sliders, "", null, null, null)
            )
            nav.navigate(Routes.MyInfections)
        }
        PrimaryAction("Share") {
            val bmp = Bitmap.createBitmap(480, 480, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp)
            c.drawColor(Color.BLACK)
            Paint().apply { color = Color.RED; textSize = 36f }.also { c.drawText("INFECTED", 130f, 240f, it) }
            val file = exporter.exportBeforeAfterPng(bmp, "share_${System.currentTimeMillis()}")
            ShareHelper.shareFile(context, file, "image/png")
        }
        PrimaryAction("Generate Reveal Clip") { nav.navigate(Routes.ExportShare) }
    }
}

@Composable
fun ExportShareScreen(nav: NavController) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Export & Share")
        Text("MVP fallback: GIF placeholder export implemented; MP4 via MediaCodec/Muxer is next step.")
        PrimaryAction("Back Home") { nav.navigate(Routes.Home) }
    }
}
