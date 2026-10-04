package com.example.shortcut

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

sealed class ShortcutCreationResult {
    data class Success(val shortcutId: String, val message: String) : ShortcutCreationResult()
    data class UnsupportedLauncher(val message: String) : ShortcutCreationResult()
    data class Failure(val error: String) : ShortcutCreationResult()
}

object ShortcutCreator {

    /**
     * Checks whether the current default launcher supports pinning shortcuts.
     */
    fun isPinShortcutSupported(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            shortcutManager?.isRequestPinShortcutSupported == true
        } else {
            ShortcutManagerCompat.isRequestPinShortcutSupported(context)
        }
    }

    /**
     * Creates a ShortcutInfo object and uses ShortcutManager to pin the custom icon
     * to the Android home screen, handling intent validation, permission checks, and callbacks.
     */
    fun createShortcut(
        context: Context,
        shortcutId: String,
        label: String,
        targetPackage: String,
        targetActivity: String?,
        iconBitmap: Bitmap
    ): ShortcutCreationResult {
        // 1. Validate target application existence
        val pm = context.packageManager
        val isInstalled = try {
            pm.getPackageInfo(targetPackage, 0)
            true
        } catch (e: Exception) {
            false
        }

        if (!isInstalled) {
            return ShortcutCreationResult.Failure("Aplikasi target '$targetPackage' tidak ditemukan di perangkat.")
        }

        // 2. Build the launch Intent
        val directIntent: Intent? = if (!targetActivity.isNullOrEmpty()) {
            Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(targetPackage, targetActivity)
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
        } else {
            pm.getLaunchIntentForPackage(targetPackage)?.apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
        }

        // Fallback to Proxy Activity if direct intent failed to resolve
        val finalIntent = directIntent ?: ShortcutProxyActivity.createIntent(
            context,
            targetPackage,
            targetActivity,
            shortcutId
        )

        // 3. Check Launcher Support & Permissions
        if (!isPinShortcutSupported(context)) {
            // Check legacy broadcast fallback
            return tryLegacyInstallShortcut(context, label, targetPackage, targetActivity, iconBitmap)
        }

        // 4. Use ShortcutManager & ShortcutInfo on Android O (API 26) and above
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val shortcutManager = context.getSystemService(ShortcutManager::class.java)
                    ?: return ShortcutCreationResult.Failure("ShortcutManager system service tidak tersedia.")

                if (!shortcutManager.isRequestPinShortcutSupported) {
                    return ShortcutCreationResult.UnsupportedLauncher(
                        "Launcher bawaan perangkat Anda menolak atau tidak mengizinkan penambahan shortcut otomatis."
                    )
                }

                // Construct Android native ShortcutInfo
                val shortcutIcon = Icon.createWithBitmap(iconBitmap)
                val pinShortcutInfo = ShortcutInfo.Builder(context, shortcutId)
                    .setShortLabel(label.trim())
                    .setLongLabel(label.trim())
                    .setIcon(shortcutIcon)
                    .setIntent(finalIntent)
                    .build()

                // Create confirmation callback intent
                val callbackIntent = Intent(context, ShortcutReceiver::class.java).apply {
                    action = "com.example.zxiconchanger.SHORTCUT_PINNED"
                    putExtra("shortcut_name", label)
                    putExtra("shortcut_id", shortcutId)
                }

                val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }

                val successCallback = PendingIntent.getBroadcast(
                    context,
                    shortcutId.hashCode(),
                    callbackIntent,
                    flag
                )

                // Request home screen launcher to pin the ShortcutInfo
                val pinned = shortcutManager.requestPinShortcut(
                    pinShortcutInfo,
                    successCallback.intentSender
                )

                if (pinned) {
                    ShortcutCreationResult.Success(
                        shortcutId = shortcutId,
                        message = "Shortcut '${label}' berhasil dikirim ke Launcher Android."
                    )
                } else {
                    ShortcutCreationResult.Failure(
                        "Launcher tidak merespon permintaan pin shortcut. Periksa izin di launcher."
                    )
                }
            } else {
                // Use ShortcutManagerCompat for backward compatibility
                val iconCompat = IconCompat.createWithBitmap(iconBitmap)
                val shortcutInfoCompat = ShortcutInfoCompat.Builder(context, shortcutId)
                    .setShortLabel(label.trim())
                    .setLongLabel(label.trim())
                    .setIcon(iconCompat)
                    .setIntent(finalIntent)
                    .setAlwaysBadged()
                    .build()

                val callbackIntent = Intent(context, ShortcutReceiver::class.java).apply {
                    action = "com.example.zxiconchanger.SHORTCUT_PINNED"
                    putExtra("shortcut_name", label)
                    putExtra("shortcut_id", shortcutId)
                }

                val successCallback = PendingIntent.getBroadcast(
                    context,
                    shortcutId.hashCode(),
                    callbackIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val pinned = ShortcutManagerCompat.requestPinShortcut(
                    context,
                    shortcutInfoCompat,
                    successCallback.intentSender
                )

                if (pinned) {
                    ShortcutCreationResult.Success(
                        shortcutId = shortcutId,
                        message = "Shortcut '${label}' berhasil dikirim ke Launcher."
                    )
                } else {
                    ShortcutCreationResult.Failure(
                        "Gagal menyematkan shortcut pada launcher."
                    )
                }
            }
        } catch (e: Exception) {
            ShortcutCreationResult.Failure("Gagal membuat shortcut: ${e.localizedMessage}")
        }
    }

    private fun tryLegacyInstallShortcut(
        context: Context,
        label: String,
        targetPackage: String,
        targetActivity: String?,
        iconBitmap: Bitmap
    ): ShortcutCreationResult {
        return try {
            val launchIntent = if (!targetActivity.isNullOrEmpty()) {
                Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(targetPackage, targetActivity)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                context.packageManager.getLaunchIntentForPackage(targetPackage)?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } ?: ShortcutProxyActivity.createIntent(context, targetPackage, targetActivity, label)

            val installIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent)
                putExtra(Intent.EXTRA_SHORTCUT_NAME, label)
                putExtra(Intent.EXTRA_SHORTCUT_ICON, iconBitmap)
                putExtra("duplicate", false)
            }
            context.sendBroadcast(installIntent)

            ShortcutCreationResult.Success(
                shortcutId = label,
                message = "Permintaan dikirim via legacy broadcast installer."
            )
        } catch (e: Exception) {
            ShortcutCreationResult.UnsupportedLauncher(
                "Launcher default Anda tidak mengizinkan penambahan shortcut secara otomatis. Silakan periksa pengaturan launcher atau gunakan launcher yang mendukung pin shortcut."
            )
        }
    }
}
