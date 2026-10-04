package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shortcuts")
data class ShortcutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shortcutId: String,
    val targetPackage: String,
    val targetActivity: String,
    val targetAppName: String,
    val shortcutName: String,
    val iconFileName: String,
    val iconShape: String = "ROUNDED",
    val iconScale: Float = 1.0f,
    val iconRotation: Float = 0f,
    val iconOffsetX: Float = 0f,
    val iconOffsetY: Float = 0f,
    val iconPadding: Int = 12,
    val backgroundColorHex: String = "#0F1728",
    val isTransparentBg: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
