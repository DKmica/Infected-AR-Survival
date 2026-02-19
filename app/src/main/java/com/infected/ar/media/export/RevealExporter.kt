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
        return File(dir, "$name.gif").also {
            if (!it.exists()) {
                // Minimal 1x1 transparent GIF placeholder.
                val gifBytes = byteArrayOf(
                    0x47, 0x49, 0x46, 0x38, 0x39, 0x61,
                    0x01, 0x00, 0x01, 0x00,
                    0x80.toByte(), 0x00, 0x00,
                    0x00, 0x00, 0x00,
                    0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                    0x21, 0xF9.toByte(), 0x04, 0x01, 0x00, 0x00, 0x00, 0x00,
                    0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
                    0x02, 0x02, 0x44, 0x01, 0x00,
                    0x3B
                )
                it.writeBytes(gifBytes)
            }
        }
    }
}
