package com.infected.ar.ui.screens

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as CColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.infected.ar.data.db.InfectionEntity
import com.infected.ar.media.export.RevealExporter
import com.infected.ar.media.export.ShareHelper
import com.infected.ar.media.face.FaceDetectorProcessor
import com.infected.ar.ui.components.GlitchHeader
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel
import com.infected.ar.ui.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

@Composable
fun SplashScreen(onNext: () -> Unit) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        delay(1200)
        onNext()
    }
    Box(Modifier.fillMaxSize().background(CColor.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("INFECTED AR", style = MaterialTheme.typography.displaySmall, color = CColor.Red)
            Text("Turn anyone into a zombie.", color = CColor.LightGray)
        }
    }
}

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf("Turn anyone into a zombie.", "Live camera + photo uploads.", "Share your infection.")
    var index by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
        GlitchHeader("Welcome to INFECTED AR")
        Text(pages[index], style = MaterialTheme.typography.headlineMedium)
        PrimaryAction(if (index < 2) "Next" else "Start Infecting") {
            if (index < 2) index++ else onFinish()
        }
    }
}

@Composable
fun HomeScreen(onLive: () -> Unit, onUpload: () -> Unit, onSurvival: () -> Unit, onLibrary: () -> Unit, onSkins: () -> Unit, onSettings: () -> Unit) {
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

@Composable
fun UploadPickerScreen(nav: NavController) {
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        nav.navigate(Routes.FaceSelect)
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader("Upload Photo Infect")
        PrimaryAction("Pick from gallery") { 
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
        }
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
fun ZombifyEditorScreen(nav: NavController, vm: AppViewModel) {
    val context = LocalContext.current
    val exporter = remember { RevealExporter(context) }
    var rot by remember { mutableFloatStateOf(0.5f) }
    var veins by remember { mutableFloatStateOf(0.5f) }
    var blood by remember { mutableFloatStateOf(0.5f) }
    var bruises by remember { mutableFloatStateOf(0.5f) }
    var eyeGlow by remember { mutableFloatStateOf(0.7f) }
    var style by remember { mutableStateOf("CLASSIC") }
    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlitchHeader("Zombify Editor")
        Text("Before/After toggle placeholder")
        listOf("Rot" to rot, "Veins" to veins, "Blood" to blood, "Bruises" to bruises, "EyeGlow" to eyeGlow).forEach { (n, v) ->
            Text(n)
            Slider(value = v, onValueChange = {
                when (n) {
                    "Rot" -> rot = it
                    "Veins" -> veins = it
                    "Blood" -> blood = it
                    "Bruises" -> bruises = it
                    else -> eyeGlow = it
                }
            })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CLASSIC", "RUNNER", "DEMON").forEach {
                FilterChip(selected = style == it, onClick = { style = it }, label = { Text(it) })
            }
        }
        PrimaryAction("Save") {
            val sliders = Json.encodeToString(mapOf("rot" to rot, "veins" to veins, "blood" to blood, "bruises" to bruises, "eyeGlow" to eyeGlow))
            vm.saveInfection(
                InfectionEntity(UUID.randomUUID().toString(), System.currentTimeMillis(), "UPLOAD", style, (rot * 100).toInt(), sliders, "", null, null, null)
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

@Composable
fun LiveInfectScreen(nav: NavController, vm: AppViewModel) {
    val context = LocalContext.current
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted = it }
    var intensity by remember { mutableFloatStateOf(0.7f) }
    var style by remember { mutableStateOf("CLASSIC") }
    var infecting by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    var faceCount by remember { mutableIntStateOf(0) }
    val detector = remember { FaceDetectorProcessor { faceCount = it.size } }

    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }
    Box(Modifier.fillMaxSize()) {
        if (permissionGranted) {
            AndroidView(factory = { ctx ->
                PreviewView(ctx).apply {
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)
                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(surfaceProvider) }
                        val analysis = androidx.camera.core.ImageAnalysis.Builder()
                            .setTargetResolution(android.util.Size(640, 480))
                            .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { it.setAnalyzer(ContextCompat.getMainExecutor(ctx), detector) }
                        provider.unbindAll()
                        provider.bindToLifecycle(lifecycle, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis)
                    }, ContextCompat.getMainExecutor(ctx))
                }
            }, modifier = Modifier.fillMaxSize())
        } else {
            Text("Camera permission required", modifier = Modifier.align(Alignment.Center))
        }

        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(CColor.Transparent, CColor.Red.copy(alpha = if (infecting) 0.25f else 0.06f))
                )
            )
            if (faceCount > 0) {
                drawCircle(CColor.Red.copy(alpha = 0.35f), 70f, Offset(size.width / 2, size.height / 3))
            }
        }

        Column(Modifier.align(Alignment.BottomCenter).padding(12.dp).background(CColor.Black.copy(alpha = 0.5f)).padding(8.dp)) {
            Text("Faces: $faceCount")
            Slider(value = intensity, onValueChange = { intensity = it })
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("CLASSIC", "RUNNER", "DEMON").forEach {
                    AssistChip(onClick = { style = it }, label = { Text(it) })
                }
            }
            PrimaryAction("INFECT") {
                infecting = true
                vm.saveInfection(
                    InfectionEntity(UUID.randomUUID().toString(), System.currentTimeMillis(), "LIVE", style, (intensity * 100).toInt(), "{}", "", null, null, null)
                )
            }
            PrimaryAction("Back") { nav.popBackStack() }
        }
    }
}

@Composable
fun SurvivalMiniGameScreen(nav: NavController) {
    var hp by remember { mutableIntStateOf(12) }
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(1) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var scale by remember { mutableFloatStateOf(0.2f) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            scale = (scale + 0.08f).coerceAtMost(1.2f)
        }
    }
    Box(Modifier.fillMaxSize().background(CColor.Black)) {
        Canvas(Modifier.fillMaxSize().clickable {
            if (timeLeft > 0) {
                hp -= 1
                score += 10 * combo
                combo++
                if (hp <= 0) {
                    hp = 12
                    scale = 0.2f
                }
            }
        }) {
            drawCircle(CColor.Red.copy(alpha = 0.5f), radius = size.minDimension * scale, center = Offset(size.width/2, size.height/2))
        }
        Column(Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Time: $timeLeft")
            Text("Score: $score")
            Text("Combo: x$combo")
            Text("HP: $hp")
        }
        if (timeLeft == 0) {
            Column(Modifier.align(Alignment.Center).background(CColor.Black.copy(alpha = 0.6f)).padding(16.dp)) {
                Text("Run Over. Score $score")
                PrimaryAction("Share Result") {}
                PrimaryAction("Back Home") { nav.navigate(Routes.Home) }
            }
        }
    }
}

@Composable
fun MyInfectionsScreen(vm: AppViewModel, onDetail: (String) -> Unit) {
    val items by vm.infections.collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        GlitchHeader("My Infections")
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) { Text(name); Text("Premium cosmetic pack") }
                    Button(onClick = {
                        if (coins >= price && !owned.contains(name)) {
                            coins -= price
                            owned += name
                        }
                    }) { Text(if (owned.contains(name)) "Owned" else "Buy $price") }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(vm: AppViewModel, onPrivacy: () -> Unit, onTerms: () -> Unit) {
    val settings by vm.settings.collectAsState()
    var sound by remember(settings.soundEnabled) { mutableStateOf(settings.soundEnabled) }
    var haptics by remember(settings.hapticsEnabled) { mutableStateOf(settings.hapticsEnabled) }
    var watermark by remember(settings.watermarkEnabled) { mutableStateOf(settings.watermarkEnabled) }
    var style by remember(settings.defaultStyle) { mutableStateOf(settings.defaultStyle) }
    val context = LocalContext.current

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
        Text("Permission Status: handled gracefully when denied.")
    }
}

@Composable
fun LegalScreen(title: String) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GlitchHeader(title)
        Text("Local placeholder legal text for $title. Replace with production legal copy.")
    }
}
