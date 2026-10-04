package com.example.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.ui.graphics.vector.ImageVector

data class PresetIconItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val category: String = "Futuristic"
)

object PresetIcons {

    val list: List<PresetIconItem> = listOf(
        PresetIconItem("rocket", "Cyber Rocket", Icons.Default.RocketLaunch),
        PresetIconItem("bolt", "Electric Bolt", Icons.Default.Bolt),
        PresetIconItem("terminal", "Matrix Terminal", Icons.Default.Terminal),
        PresetIconItem("code", "Cyber Code", Icons.Default.Code),
        PresetIconItem("gaming", "Arcade Game", Icons.Default.SportsEsports),
        PresetIconItem("audio", "Neon Beats", Icons.Default.Headphones),
        PresetIconItem("camera", "Tech Lens", Icons.Default.CameraAlt),
        PresetIconItem("chat", "Secure Comm", Icons.Default.Chat),
        PresetIconItem("web", "Global Net", Icons.Default.Language),
        PresetIconItem("shield", "Firewall", Icons.Default.Security),
        PresetIconItem("lock", "Cipher Lock", Icons.Default.Lock),
        PresetIconItem("key", "Access Key", Icons.Default.VpnKey),
        PresetIconItem("fingerprint", "Biometrics", Icons.Default.Fingerprint),
        PresetIconItem("diamond", "Core Prism", Icons.Default.Diamond),
        PresetIconItem("speed", "Hyper Speed", Icons.Default.Speed),
        PresetIconItem("compass", "Quantum Nav", Icons.Default.Explore),
        PresetIconItem("settings", "System Gear", Icons.Default.Settings)
    )

    /**
     * Renders a clean futuristic monogram / glyph bitmap programmatically
     */
    fun createMonogramBitmap(text: String, primaryColor: Int, bgColor: Int, size: Int = 256): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        if (bgColor != 0) {
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = bgColor
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            textSize = size * 0.45f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val yPos = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(text.take(3).uppercase(), size / 2f, yPos, textPaint)
        return bitmap
    }
}
