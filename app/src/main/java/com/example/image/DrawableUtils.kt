package com.example.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object DrawableUtils {

    fun drawableToBitmap(drawable: Drawable, targetSize: Int = 256): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val bmp = drawable.bitmap
            if (bmp.width == targetSize && bmp.height == targetSize && bmp.config == Bitmap.Config.ARGB_8888) {
                return bmp.copy(Bitmap.Config.ARGB_8888, true)
            }
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else targetSize
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else targetSize

        val bitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val aspect = width.toFloat() / height.toFloat()
        val drawW: Int
        val drawH: Int
        if (aspect >= 1f) {
            drawW = targetSize
            drawH = (targetSize / aspect).toInt().coerceAtLeast(1)
        } else {
            drawH = targetSize
            drawW = (targetSize * aspect).toInt().coerceAtLeast(1)
        }

        val left = (targetSize - drawW) / 2
        val top = (targetSize - drawH) / 2

        drawable.setBounds(left, top, left + drawW, top + drawH)
        drawable.draw(canvas)
        return bitmap
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val dir = File(context.filesDir, "shortcut_icons")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun loadBitmapFromFile(context: Context, fileName: String): Bitmap? {
        val file = File(File(context.filesDir, "shortcut_icons"), fileName)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    fun deleteBitmapFile(context: Context, fileName: String): Boolean {
        val file = File(File(context.filesDir, "shortcut_icons"), fileName)
        return if (file.exists()) file.delete() else false
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
