package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.db.ShortcutDao
import com.example.data.model.ShortcutEntity
import com.example.image.DrawableUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ShortcutRepository(
    private val context: Context,
    private val shortcutDao: ShortcutDao
) {

    val allShortcuts: Flow<List<ShortcutEntity>> = shortcutDao.getAllShortcutsFlow()

    suspend fun saveShortcut(
        targetPackage: String,
        targetActivity: String,
        targetAppName: String,
        shortcutName: String,
        iconBitmap: Bitmap,
        iconShape: String,
        iconScale: Float,
        iconRotation: Float,
        iconOffsetX: Float,
        iconOffsetY: Float,
        iconPadding: Int,
        backgroundColorHex: String,
        isTransparentBg: Boolean
    ): ShortcutEntity = withContext(Dispatchers.IO) {
        val uniqueId = "zx_" + UUID.randomUUID().toString().take(12)
        val fileName = "$uniqueId.png"

        // Save bitmap file to local private storage
        DrawableUtils.saveBitmapToFile(context, iconBitmap, fileName)

        val entity = ShortcutEntity(
            shortcutId = uniqueId,
            targetPackage = targetPackage,
            targetActivity = targetActivity,
            targetAppName = targetAppName,
            shortcutName = shortcutName,
            iconFileName = fileName,
            iconShape = iconShape,
            iconScale = iconScale,
            iconRotation = iconRotation,
            iconOffsetX = iconOffsetX,
            iconOffsetY = iconOffsetY,
            iconPadding = iconPadding,
            backgroundColorHex = backgroundColorHex,
            isTransparentBg = isTransparentBg,
            createdAt = System.currentTimeMillis()
        )

        val rowId = shortcutDao.insertShortcut(entity)
        entity.copy(id = rowId)
    }

    suspend fun updateShortcut(
        shortcut: ShortcutEntity,
        newBitmap: Bitmap?
    ) = withContext(Dispatchers.IO) {
        if (newBitmap != null) {
            DrawableUtils.saveBitmapToFile(context, newBitmap, shortcut.iconFileName)
        }
        shortcutDao.updateShortcut(shortcut)
    }

    suspend fun deleteShortcut(shortcut: ShortcutEntity) = withContext(Dispatchers.IO) {
        DrawableUtils.deleteBitmapFile(context, shortcut.iconFileName)
        shortcutDao.deleteShortcut(shortcut)
    }

    suspend fun deleteShortcutById(id: Long) = withContext(Dispatchers.IO) {
        val shortcut = shortcutDao.getShortcutById(id)
        if (shortcut != null) {
            deleteShortcut(shortcut)
        }
    }

    suspend fun getShortcutById(id: Long): ShortcutEntity? = withContext(Dispatchers.IO) {
        shortcutDao.getShortcutById(id)
    }

    suspend fun loadShortcutIconBitmap(shortcut: ShortcutEntity): Bitmap? = withContext(Dispatchers.IO) {
        DrawableUtils.loadBitmapFromFile(context, shortcut.iconFileName)
    }

    /**
     * Export local shortcuts configuration and icons to a portable JSON string
     */
    suspend fun exportConfigJson(): String = withContext(Dispatchers.IO) {
        val list = shortcutDao.getAllShortcuts()
        val root = JSONObject()
        root.put("app", "ZX Icon Changer")
        root.put("brand", "ZUCCHERO XANN")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("shortcutId", item.shortcutId)
            obj.put("targetPackage", item.targetPackage)
            obj.put("targetActivity", item.targetActivity)
            obj.put("targetAppName", item.targetAppName)
            obj.put("shortcutName", item.shortcutName)
            obj.put("iconShape", item.iconShape)
            obj.put("iconScale", item.iconScale.toDouble())
            obj.put("iconRotation", item.iconRotation.toDouble())
            obj.put("iconOffsetX", item.iconOffsetX.toDouble())
            obj.put("iconOffsetY", item.iconOffsetY.toDouble())
            obj.put("iconPadding", item.iconPadding)
            obj.put("backgroundColorHex", item.backgroundColorHex)
            obj.put("isTransparentBg", item.isTransparentBg)
            obj.put("createdAt", item.createdAt)

            val bitmap = DrawableUtils.loadBitmapFromFile(context, item.iconFileName)
            if (bitmap != null) {
                obj.put("iconBase64", DrawableUtils.bitmapToBase64(bitmap))
            }
            array.put(obj)
        }
        root.put("shortcuts", array)
        root.toString(2)
    }

    /**
     * Import local configuration from JSON string
     */
    suspend fun importConfigJson(jsonStr: String): ImportResult = withContext(Dispatchers.IO) {
        var importedCount = 0
        var skippedCount = 0

        try {
            val root = JSONObject(jsonStr)
            val array = root.optJSONArray("shortcuts") ?: return@withContext ImportResult(0, 0, "Format backup tidak valid")

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val targetPackage = obj.getString("targetPackage")
                val shortcutName = obj.getString("shortcutName")
                val targetAppName = obj.optString("targetAppName", shortcutName)
                val targetActivity = obj.optString("targetActivity", "")
                val iconBase64 = obj.optString("iconBase64", "")

                val bitmap = if (iconBase64.isNotEmpty()) {
                    DrawableUtils.base64ToBitmap(iconBase64)
                } else null

                if (bitmap != null) {
                    val uniqueId = "zx_" + UUID.randomUUID().toString().take(12)
                    val fileName = "$uniqueId.png"
                    DrawableUtils.saveBitmapToFile(context, bitmap, fileName)

                    val entity = ShortcutEntity(
                        shortcutId = uniqueId,
                        targetPackage = targetPackage,
                        targetActivity = targetActivity,
                        targetAppName = targetAppName,
                        shortcutName = shortcutName,
                        iconFileName = fileName,
                        iconShape = obj.optString("iconShape", "ROUNDED"),
                        iconScale = obj.optDouble("iconScale", 1.0).toFloat(),
                        iconRotation = obj.optDouble("iconRotation", 0.0).toFloat(),
                        iconOffsetX = obj.optDouble("iconOffsetX", 0.0).toFloat(),
                        iconOffsetY = obj.optDouble("iconOffsetY", 0.0).toFloat(),
                        iconPadding = obj.optInt("iconPadding", 16),
                        backgroundColorHex = obj.optString("backgroundColorHex", "#0F1728"),
                        isTransparentBg = obj.optBoolean("isTransparentBg", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                    shortcutDao.insertShortcut(entity)
                    importedCount++
                } else {
                    skippedCount++
                }
            }
            ImportResult(importedCount, skippedCount, null)
        } catch (e: Exception) {
            ImportResult(importedCount, skippedCount, "Gagal membaca file: ${e.localizedMessage}")
        }
    }
}

data class ImportResult(
    val importedCount: Int,
    val skippedCount: Int,
    val error: String?
)
