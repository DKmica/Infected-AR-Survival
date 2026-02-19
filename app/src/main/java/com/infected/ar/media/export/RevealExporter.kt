package com.infected.ar.media.export

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

class RevealExporter(private val context: Context) {
    fun exportBeforeAfterPng(bitmap: Bitmap, name: String): File {
        val dir = File(context.filesDir, "shared").apply { mkdirs() }
        return File(dir, "$name.png").also {
            FileOutputStream(it).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        }
    }

    fun exportRevealGifPlaceholder(name: String): File {
        val dir = File(context.filesDir, "shared").apply { mkdirs() }
        return File(dir, "$name.gif").also { if (!it.exists()) it.writeBytes(byteArrayOf()) }
    }
}
