package com.infected.ar.ui.screens.live

import android.Manifest
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.infected.ar.data.db.InfectionEntity
import com.infected.ar.media.face.FaceDetectorProcessor
import com.infected.ar.ui.components.PrimaryAction
import com.infected.ar.ui.navigation.AppViewModel
import com.infected.ar.ui.viewmodel.LiveInfectViewModel
import java.util.UUID

@Composable
fun LiveInfectScreen(nav: NavController, appVm: AppViewModel, liveVm: LiveInfectViewModel = viewModel()) {
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted = it }
    val detector = remember { FaceDetectorProcessor { liveVm.onFacesDetected(it.size) } }

    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }
    Box(Modifier.fillMaxSize()) {
        if (permissionGranted) {
            AndroidView(factory = { ctx ->
                PreviewView(ctx).apply {
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)
                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(surfaceProvider) }
                        val analysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(640, 480))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
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
                    colors = listOf(Color.Transparent, Color.Red.copy(alpha = if (liveVm.infecting) 0.25f else 0.06f))
                )
            )
            if (liveVm.faceCount > 0) {
                drawCircle(Color.Red.copy(alpha = 0.35f), 70f, Offset(size.width / 2, size.height / 3))
            }
        }

        Column(
            Modifier.align(Alignment.BottomCenter)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Faces: ${liveVm.faceCount}")
            Slider(value = liveVm.intensity, onValueChange = liveVm::onIntensityChanged)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("CLASSIC", "RUNNER", "DEMON").forEach {
                    AssistChip(onClick = { liveVm.onStyleChanged(it) }, label = { Text(it) })
                }
            }
            PrimaryAction("INFECT") {
                liveVm.triggerInfection()
                appVm.saveInfection(
                    InfectionEntity(UUID.randomUUID().toString(), System.currentTimeMillis(), "LIVE", liveVm.style, (liveVm.intensity * 100).toInt(), "{}", "", null, null, null)
                )
            }
            PrimaryAction("Back") { nav.popBackStack() }
        }
    }
}
