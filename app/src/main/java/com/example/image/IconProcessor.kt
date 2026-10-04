package com.example.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class IconShape(val label: String) {
    ROUNDED("Rounded Squircle"),
    CIRCLE("Circle"),
    SQUARE("Square"),
    HEXAGON("Cyber Hexagon"),
    OCTAGON("Octagon Tech")
}

data class IconConfig(
    val shape: IconShape = IconShape.ROUNDED,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val padding: Int = 16,
    val backgroundColor: Int = Color.parseColor("#0F1728"),
    val isTransparentBg: Boolean = false,
    val showBorder: Boolean = true,
    val borderColor: Int = Color.parseColor("#00E5FF")
)

object IconProcessor {

    fun processIcon(
        sourceBitmap: Bitmap,
        config: IconConfig,
        targetSize: Int = 256
    ): Bitmap {
        val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val sizeF = targetSize.toFloat()
        val rectF = RectF(0f, 0f, sizeF, sizeF)

        // 1. Build clip path based on Shape
        val shapePath = createShapePath(config.shape, rectF)

        // 2. Draw Background inside shape
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        canvas.save()
        canvas.clipPath(shapePath)

        if (!config.isTransparentBg) {
            bgPaint.color = config.backgroundColor
            canvas.drawRect(rectF, bgPaint)
        }

        // 3. Draw Source Bitmap with Scale, Rotation, Offset, and Padding
        val effectiveSize = (sizeF - (config.padding * 2f)).coerceAtLeast(10f)
        val srcW = sourceBitmap.width.toFloat()
        val srcH = sourceBitmap.height.toFloat()

        val baseScale = effectiveSize / srcW.coerceAtLeast(srcH)
        val totalScale = baseScale * config.scale

        val matrix = Matrix()
        // Center the source bitmap
        matrix.postTranslate(-srcW / 2f, -srcH / 2f)
        // Apply rotation
        matrix.postRotate(config.rotation)
        // Apply scale
        matrix.postScale(totalScale, totalScale)
        // Move to target center + offset
        val centerX = sizeF / 2f + config.offsetX
        val centerY = sizeF / 2f + config.offsetY
        matrix.postTranslate(centerX, centerY)

        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            isDither = true
        }
        canvas.drawBitmap(sourceBitmap, matrix, iconPaint)

        canvas.restore()

        // 4. Draw Border stroke if enabled and not transparent or styled
        if (config.showBorder) {
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = config.borderColor
                strokeWidth = (targetSize * 0.025f).coerceIn(2f, 8f)
                isAntiAlias = true
            }
            canvas.drawPath(shapePath, strokePaint)
        }

        return output
    }

    private fun createShapePath(shape: IconShape, rect: RectF): Path {
        val path = Path()
        val w = rect.width()
        val h = rect.height()
        val pad = rect.width() * 0.02f // slight inset so border doesn't clip
        val inner = RectF(rect.left + pad, rect.top + pad, rect.right - pad, rect.bottom - pad)

        when (shape) {
            IconShape.CIRCLE -> {
                path.addOval(inner, Path.Direction.CW)
            }
            IconShape.ROUNDED -> {
                val cornerRadius = w * 0.22f
                path.addRoundRect(inner, cornerRadius, cornerRadius, Path.Direction.CW)
            }
            IconShape.SQUARE -> {
                val cornerRadius = w * 0.05f
                path.addRoundRect(inner, cornerRadius, cornerRadius, Path.Direction.CW)
            }
            IconShape.HEXAGON -> {
                val cx = inner.centerX()
                val cy = inner.centerY()
                val radius = min(inner.width(), inner.height()) / 2f
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60 - 30).toDouble())
                    val x = (cx + radius * cos(angle)).toFloat()
                    val y = (cy + radius * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
            }
            IconShape.OCTAGON -> {
                val cx = inner.centerX()
                val cy = inner.centerY()
                val radius = min(inner.width(), inner.height()) / 2f
                val cornerCut = radius * 0.4f
                val r = radius
                val x0 = cx - r + cornerCut
                val x1 = cx + r - cornerCut
                val y0 = cy - r + cornerCut
                val y1 = cy + r - cornerCut

                path.moveTo(x0, cy - r)
                path.lineTo(x1, cy - r)
                path.lineTo(cx + r, y0)
                path.lineTo(cx + r, y1)
                path.lineTo(x1, cy + r)
                path.lineTo(x0, cy + r)
                path.lineTo(cx - r, y1)
                path.lineTo(cx - r, y0)
                path.close()
            }
        }
        return path
    }
}
