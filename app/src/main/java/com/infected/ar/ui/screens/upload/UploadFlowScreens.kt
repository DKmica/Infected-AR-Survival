package com.infected.ar.ui.screens.upload

import android.graphics.Rect
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.infected.ar.data.db.InfectionEntity
import com.infected.ar.media.export.RevealExporter
import com.infected.ar.media.export.ShareHelper
import com.infected.ar.media.face.PhotoFaceDetector
import com.infected.ar.media.overlay.ZombifyRenderer
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel
import com.infected.ar.ui.navigation.Routes
import com.infected.ar.ui.viewmodel.ZombifyEditorViewModel
import com.infected.ar.util.ImageCaptureFileFactory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun UploadPickerScreen(nav: NavController) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    var pendingCaptureUri by rememberSaveable { mutableStateOf<String?>(null) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            nav.currentBackStackEntry?.savedStateHandle?.set("uploadUri", uri.toString())
            nav.navigate(Routes.FaceSelect)
        }
    }
    val capturePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingCaptureUri
        if (success && uri != null) {
            nav.currentBackStackEntry?.savedStateHandle?.set("uploadUri", uri)
            nav.navigate(Routes.FaceSelect)
        }
    }

    LaunchedEffect(Unit) {
        nav.currentBackStackEntry?.savedStateHandle?.getStateFlow("uploadPickerError", "")?.collectLatest { msg ->
            if (msg.isNotBlank()) {
                snackbar.showSnackbar(msg)
                nav.currentBackStackEntry?.savedStateHandle?.set("uploadPickerError", "")
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SnackbarHost(hostState = snackbar)
        GlitchHeader("Upload Photo Infect")
        PrimaryAction("Pick from gallery") { pickMedia.launch(ActivityResultContracts.PickVisualMedia.ImageOnly) }
        PrimaryAction("Take photo") {
            val uri = ImageCaptureFileFactory.createTempImageUri(context)
            pendingCaptureUri = uri.toString()
            capturePhoto.launch(uri)
        }
    }
}

@Composable
fun FaceSelectCropScreen(nav: NavController) {
    val context = LocalContext.current
    val detector = remember { PhotoFaceDetector() }
    val uriString = nav.previousBackStackEntry?.savedStateHandle?.get<String>("uploadUri")
    val faces = remember { mutableStateListOf<Rect>() }
    var selectedFaceIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(uriString) {
        val localUri = uriString ?: return@LaunchedEffect
        val bitmap = detector.decodeBitmap(context, Uri.parse(localUri))
        val detected = detector.detectFaces(bitmap).map { it.boundingBox }
        faces.clear()
        faces.addAll(detected)
        selectedFaceIndex = if (detected.size == 1) 0 else null
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Select Face")
        if (uriString == null) {
            Text("No image selected. Return to picker.")
        } else {
            Text("Detected ${faces.size} face(s). Select one to continue.")
            if (faces.isEmpty()) Text("No faces found. Try another photo with a clearer face.")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(faces) { index, face ->
                    OutlinedCard(onClick = { selectedFaceIndex = index }) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Face ${index + 1}")
                            Text("Bounds: ${face.left},${face.top} → ${face.right},${face.bottom}")
                            if (selectedFaceIndex == index) Text("Selected")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        PrimaryAction("Continue to Editor") {
            if (selectedFaceIndex != null) {
                val faceRect = faces.getOrNull(selectedFaceIndex ?: -1)
                nav.currentBackStackEntry?.savedStateHandle?.set("selectedFace", selectedFaceIndex)
                nav.currentBackStackEntry?.savedStateHandle?.set("selectedFaceRect", ZombifyRenderer.encodeRect(faceRect))
                nav.currentBackStackEntry?.savedStateHandle?.set("uploadUri", uriString)
                nav.navigate(Routes.ZombifyEditor)
            }
        }
    }
}

@Composable
fun ZombifyEditorScreen(
    nav: NavController,
    vm: AppViewModel,
    editorVm: ZombifyEditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val exporter = remember { RevealExporter(context) }
    val detector = remember { PhotoFaceDetector() }
    val scope = rememberCoroutineScope()
    val selectedFace = nav.previousBackStackEntry?.savedStateHandle?.get<Int>("selectedFace")
    val selectedFaceRectRaw = nav.previousBackStackEntry?.savedStateHandle?.get<String>("selectedFaceRect")
    val uploadUri = nav.previousBackStackEntry?.savedStateHandle?.get<String>("uploadUri")

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Zombify Editor")
        Text("Before/After toggle placeholder")
        Text("Selected face: ${selectedFace?.plus(1) ?: "None"}")
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
            scope.launch {
                val rect = ZombifyRenderer.parseRect(selectedFaceRectRaw)
                val sliders = mapOf(
                    "rot" to editorVm.rot,
                    "veins" to editorVm.veins,
                    "blood" to editorVm.blood,
                    "bruises" to editorVm.bruises,
                    "eyeGlow" to editorVm.eyeGlow,
                    "selectedFace" to (selectedFace ?: -1).toFloat()
                )
                val sourceBitmap = uploadUri?.let { detector.decodeBitmap(context, Uri.parse(it)) }
                val rendered = sourceBitmap?.let { ZombifyRenderer.render(it, rect, editorVm.style, sliders) }
                val afterFile = rendered?.let { exporter.exportBeforeAfterPng(it, "after_${System.currentTimeMillis()}") }
                vm.saveInfection(
                    InfectionEntity(
                        UUID.randomUUID().toString(),
                        System.currentTimeMillis(),
                        "UPLOAD",
                        editorVm.style,
                        (editorVm.rot * 100).toInt(),
                        Json.encodeToString(sliders),
                        afterFile?.absolutePath ?: "",
                        uploadUri,
                        afterFile?.absolutePath,
                        null
                    )
                )
                nav.navigate(Routes.MyInfections)
            }
        }
        PrimaryAction("Share") {
            scope.launch {
                val rect = ZombifyRenderer.parseRect(selectedFaceRectRaw)
                val sliders = mapOf(
                    "rot" to editorVm.rot,
                    "veins" to editorVm.veins,
                    "blood" to editorVm.blood,
                    "bruises" to editorVm.bruises,
                    "eyeGlow" to editorVm.eyeGlow
                )
                val sourceBitmap = uploadUri?.let { detector.decodeBitmap(context, Uri.parse(it)) }
                val rendered = sourceBitmap?.let { ZombifyRenderer.render(it, rect, editorVm.style, sliders) }
                if (rendered != null) {
                    val file = exporter.exportBeforeAfterPng(rendered, "share_${System.currentTimeMillis()}")
                    ShareHelper.shareFile(context, file, "image/png")
                }
            }
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
