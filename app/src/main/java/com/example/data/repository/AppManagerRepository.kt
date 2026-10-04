package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.example.data.model.InstalledApp
import com.example.image.DrawableUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppManagerRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val iconCache = mutableMapOf<String, Bitmap>()
    private var cachedApps: List<InstalledApp> = emptyList()

    suspend fun getInstalledApps(forceRefresh: Boolean = false): List<InstalledApp> = withContext(Dispatchers.IO) {
        if (cachedApps.isNotEmpty() && !forceRefresh) {
            return@withContext cachedApps
        }

        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0)
        val appList = mutableListOf<InstalledApp>()
        val seenPackages = mutableSetOf<String>()

        for (resolveInfo in resolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            // Skip our own app if desired or keep it
            if (pkg == context.packageName) continue
            if (seenPackages.contains(pkg)) continue
            seenPackages.add(pkg)

            val label = try {
                resolveInfo.loadLabel(packageManager).toString()
            } catch (e: Exception) {
                pkg
            }

            val appInfo = resolveInfo.activityInfo.applicationInfo
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            var verName = "1.0"
            var installedTime = System.currentTimeMillis()
            var updateTime = System.currentTimeMillis()

            try {
                val pkgInfo = packageManager.getPackageInfo(pkg, 0)
                verName = pkgInfo.versionName ?: "1.0"
                installedTime = pkgInfo.firstInstallTime
                updateTime = pkgInfo.lastUpdateTime
            } catch (e: Exception) {
                // Ignore missing package info
            }

            appList.add(
                InstalledApp(
                    appName = if (label.isNotBlank()) label else pkg,
                    packageName = pkg,
                    targetActivity = resolveInfo.activityInfo.name,
                    versionName = verName,
                    isSystemApp = isSystem,
                    installedTime = installedTime,
                    lastUpdateTime = updateTime
                )
            )
        }

        // Sort by App Name alphabetically by default
        val sorted = appList.sortedBy { it.appName.lowercase() }
        cachedApps = sorted
        sorted
    }

    suspend fun getAppIconBitmap(packageName: String): Bitmap = withContext(Dispatchers.IO) {
        iconCache[packageName]?.let { return@withContext it }

        try {
            val drawable: Drawable = packageManager.getApplicationIcon(packageName)
            val bitmap = DrawableUtils.drawableToBitmap(drawable, 256)
            iconCache[packageName] = bitmap
            bitmap
        } catch (e: Exception) {
            val defaultDrawable = packageManager.defaultActivityIcon
            val bitmap = DrawableUtils.drawableToBitmap(defaultDrawable, 256)
            iconCache[packageName] = bitmap
            bitmap
        }
    }

    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
