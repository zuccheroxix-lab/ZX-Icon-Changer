package com.example.settings

import android.content.Context
import android.content.SharedPreferences

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("zx_icon_changer_prefs", Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() {
            val name = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK.name) ?: ThemeMode.DARK.name
            return try {
                ThemeMode.valueOf(name)
            } catch (e: Exception) {
                ThemeMode.DARK
            }
        }
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value.name).apply()

    var defaultIconShape: String
        get() = prefs.getString(KEY_DEFAULT_SHAPE, "ROUNDED") ?: "ROUNDED"
        set(value) = prefs.edit().putString(KEY_DEFAULT_SHAPE, value).apply()

    var defaultPadding: Int
        get() = prefs.getInt(KEY_DEFAULT_PADDING, 16)
        set(value) = prefs.edit().putInt(KEY_DEFAULT_PADDING, value).apply()

    var confirmBeforeDelete: Boolean
        get() = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        set(value) = prefs.edit().putBoolean(KEY_CONFIRM_DELETE, value).apply()

    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_DEFAULT_SHAPE = "pref_default_shape"
        private const val KEY_DEFAULT_PADDING = "pref_default_padding"
        private const val KEY_CONFIRM_DELETE = "pref_confirm_delete"
    }
}
