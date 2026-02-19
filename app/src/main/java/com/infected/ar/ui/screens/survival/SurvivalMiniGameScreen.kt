package com.infected.ar.ui.screens.survival

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AColor
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.infected.ar.media.export.RevealExporter
import com.infected.ar.media.export.ShareHelper
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SurvivalMiniGameScreen(nav: NavController) {
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val context = LocalContext.current
    val exporter = remember { RevealExporter(context) }
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted = it }

    var hp by remember { mutableIntStateOf(12) }
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(1) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var scale by remember { mutableFloatStateOf(0.2f) }
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            scale = (scale + 0.08f).coerceAtMost(1.2f)
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (permissionGranted) {
            AndroidView(factory = { ctx ->
                PreviewView(ctx).apply {
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)
                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(surfaceProvider) }
                        provider.unbindAll()
                        provider.bindToLifecycle(lifecycle, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                    }, ContextCompat.getMainExecutor(ctx))
                }
            }, modifier = Modifier.fillMaxSize())
        }

        Canvas(Modifier.fillMaxSize().clickable {
            if (timeLeft > 0) {
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                hp -= 1
                score += 10 * combo
                combo++
                if (hp <= 0) {
                    hp = 12
                    scale = 0.2f
                }
            }
        }) {
            drawCircle(Color.Red.copy(alpha = 0.5f), radius = size.minDimension * scale, center = Offset(size.width / 2, size.height / 2))
        }

        Column(Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Time: $timeLeft")
            Text("Score: $score")
            Text("Combo: x$combo")
            Text("HP: $hp")
            Text(if (permissionGranted) "Camera background active" else "Camera permission denied")
        }
        if (timeLeft == 0) {
            Column(Modifier.align(Alignment.Center).background(Color.Black.copy(alpha = 0.6f)).padding(16.dp)) {
                Text("Run Over. Score $score")
                PrimaryAction("Share Result") {
                    val bmp = Bitmap.createBitmap(720, 720, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    canvas.drawColor(AColor.BLACK)
                    val paint = Paint().apply { color = AColor.RED; textSize = 48f }
                    canvas.drawText("INFECTED AR SURVIVAL", 120f, 220f, paint)
                    canvas.drawText("SCORE: $score", 220f, 340f, paint)
                    canvas.drawText("COMBO: x$combo", 220f, 420f, paint)
                    val file = exporter.exportBeforeAfterPng(bmp, "survival_${System.currentTimeMillis()}")
                    ShareHelper.shareFile(context, file, "image/png")
                }
                PrimaryAction("Back Home") { nav.navigate(Routes.Home) }
            }
        }
    }
}
