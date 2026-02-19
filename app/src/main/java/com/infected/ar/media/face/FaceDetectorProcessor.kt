package com.infected.ar.media.face

import android.annotation.SuppressLint
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

class FaceDetectorProcessor(
    private val onFaces: (List<Face>) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()
    )
    private val lastAnalyzedAt = AtomicLong(0L)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastAnalyzedAt.get() < 120L) {
            image.close(); return
        }
        lastAnalyzedAt.set(now)
        val mediaImage: Image = image.image ?: run { image.close(); return }
        val input = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
        detector.process(input)
            .addOnSuccessListener { faces -> onFaces(faces) }
            .addOnCompleteListener { image.close() }
    }
}
