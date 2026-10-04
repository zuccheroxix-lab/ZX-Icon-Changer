package com.example.data.model

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val targetActivity: String,
    val versionName: String,
    val isSystemApp: Boolean,
    val installedTime: Long,
    val lastUpdateTime: Long,
    val shortcutCount: Int = 0
)
