package com.example.shortcut

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ShortcutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val shortcutName = intent.getStringExtra("shortcut_name") ?: "Shortcut"
        Toast.makeText(
            context,
            "ZX: Shortcut '$shortcutName' berhasil disematkan ke Launcher!",
            Toast.LENGTH_SHORT
        ).show()
    }
}
