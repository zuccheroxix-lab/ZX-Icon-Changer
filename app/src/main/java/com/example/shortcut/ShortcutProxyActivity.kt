package com.example.shortcut

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

/**
 * High-performance, transparent proxy launcher for shortcuts.
 * Validates target package existence and launches directly with proper flags.
 */
class ShortcutProxyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetPackage = intent.getStringExtra(EXTRA_TARGET_PACKAGE)
        val targetActivity = intent.getStringExtra(EXTRA_TARGET_ACTIVITY)

        if (targetPackage.isNullOrEmpty()) {
            Toast.makeText(this, "ZX: Target aplikasi tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            val launchIntent: Intent? = if (!targetActivity.isNullOrEmpty()) {
                Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(targetPackage, targetActivity)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            } else {
                packageManager.getLaunchIntentForPackage(targetPackage)?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            }

            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(
                    this,
                    "ZX: Aplikasi '$targetPackage' tidak dapat ditemukan atau telah dihapus",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "ZX Error: Gagal membuka aplikasi (${e.localizedMessage})",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            // Finish immediately without animation so it feels native and seamless
            finish()
            overridePendingTransition(0, 0)
        }
    }

    companion object {
        const val EXTRA_TARGET_PACKAGE = "extra_target_package"
        const val EXTRA_TARGET_ACTIVITY = "extra_target_activity"
        const val EXTRA_SHORTCUT_ID = "extra_shortcut_id"

        fun createIntent(
            context: android.content.Context,
            targetPackage: String,
            targetActivity: String?,
            shortcutId: String
        ): Intent {
            return Intent(context, ShortcutProxyActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra(EXTRA_TARGET_PACKAGE, targetPackage)
                putExtra(EXTRA_TARGET_ACTIVITY, targetActivity)
                putExtra(EXTRA_SHORTCUT_ID, shortcutId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}
