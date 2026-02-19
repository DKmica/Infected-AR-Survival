package com.infected.ar.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ImageCaptureFileFactory {
    fun createTempImageUri(context: Context): Uri {
        val dir = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
