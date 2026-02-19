package com.infected.ar.media.overlay

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import kotlin.math.max

object ZombifyRenderer {
    fun render(source: Bitmap, faceRect: Rect?, style: String, sliders: Map<String, Float>): Bitmap {
        val out = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(out)
        val rect = faceRect ?: Rect(0, 0, source.width, source.height)

        val rot = sliders["rot"] ?: 0.5f
        val veins = sliders["veins"] ?: 0.5f
        val blood = sliders["blood"] ?: 0.5f
        val bruises = sliders["bruises"] ?: 0.5f
        val eyeGlow = sliders["eyeGlow"] ?: 0.7f

        val tone = Paint().apply {
            color = when (style) {
                "DEMON" -> Color.argb((80 + (rot * 90)).toInt(), 150, 10, 20)
                "RUNNER" -> Color.argb((70 + (rot * 80)).toInt(), 120, 30, 30)
                else -> Color.argb((60 + (rot * 70)).toInt(), 90, 45, 45)
            }
        }
        canvas.drawRect(rect, tone)

        val veinsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb((90 + veins * 120).toInt(), 130, 20, 30)
            strokeWidth = 2f + veins * 6f
        }
        repeat(6) { i ->
            val y = rect.top + (rect.height() * (i + 1) / 7f)
            canvas.drawLine(rect.left.toFloat(), y, rect.right.toFloat(), y + (i % 2) * 10f, veinsPaint)
        }

        val bruisePaint = Paint().apply {
            color = Color.argb((40 + bruises * 120).toInt(), 80, 40, 70)
        }
        canvas.drawCircle(rect.exactCenterX(), rect.exactCenterY(), max(12f, rect.width() * 0.22f), bruisePaint)

        val bloodPaint = Paint().apply {
            color = Color.argb((40 + blood * 170).toInt(), 150, 0, 0)
        }
        canvas.drawRect(rect.left.toFloat(), rect.bottom - rect.height() * 0.15f, rect.right.toFloat(), rect.bottom.toFloat(), bloodPaint)

        val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb((80 + eyeGlow * 160).toInt(), 255, 50, 50)
        }
        val eyeY = rect.top + rect.height() * 0.35f
        val eyeOffset = rect.width() * 0.18f
        val eyeRadius = max(6f, rect.width() * 0.06f)
        canvas.drawCircle(rect.centerX() - eyeOffset, eyeY, eyeRadius, eyePaint)
        canvas.drawCircle(rect.centerX() + eyeOffset, eyeY, eyeRadius, eyePaint)

        val desat = Paint().apply {
            colorFilter = PorterDuffColorFilter(Color.argb(40, 40, 40, 40), PorterDuff.Mode.MULTIPLY)
        }
        canvas.drawBitmap(out, 0f, 0f, desat)

        return out
    }

    fun parseRect(raw: String?): Rect? {
        if (raw.isNullOrBlank()) return null
        val parts = raw.split(',')
        if (parts.size != 4) return null
        return runCatching {
            Rect(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt())
        }.getOrNull()
    }

    fun encodeRect(rect: Rect?): String? = rect?.let { "${it.left},${it.top},${it.right},${it.bottom}" }
}
