package com.example.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class ReleaseInfo(
    val tagName: String,
    val versionName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val releaseUrl: String,
    val downloadUrl: String?,
    val isNewer: Boolean
)

data class ApkAsset(
    val fileName: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    val sizeHuman: String,
    val isDebug: Boolean
)

data class ReleaseStatusInfo(
    val hasRelease: Boolean,
    val tagName: String,
    val releaseTitle: String,
    val publishedAt: String,
    val debugApk: ApkAsset?,
    val releaseApk: ApkAsset?,
    val errorMessage: String? = null
)

sealed class UpdateCheckResult {
    data class Success(val releaseInfo: ReleaseInfo) : UpdateCheckResult()
    data class UpToDate(val currentVersion: String) : UpdateCheckResult()
    data class NoReleaseFound(val message: String) : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

class UpdateRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    companion object {
        const val GITHUB_OWNER = "zuccheroxix-lab"
        const val GITHUB_REPO = "ZX-Icon-Changer"
        const val REPO_URL = "https://github.com/$GITHUB_OWNER/$GITHUB_REPO"
        const val ACTIONS_URL = "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/actions"
        const val LATEST_RELEASE_PAGE_URL = "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
        const val RELEASES_PAGE_URL = "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases"
        const val RELEASES_API_URL = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
        const val ALL_RELEASES_API_URL = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases"
    }

    suspend fun checkForUpdates(currentVersion: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(RELEASES_API_URL)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "ZX-Icon-Changer-App")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.code == 404) {
                    return@withContext UpdateCheckResult.NoReleaseFound(
                        "Belum ada rilis publik di repository GitHub. Versi terpasang saat ini (v$currentVersion) adalah versi terbaru."
                    )
                }

                if (!response.isSuccessful) {
                    return@withContext UpdateCheckResult.Error(
                        "Gagal memeriksa pembaruan (Kode HTTP: ${response.code}). Periksa koneksi internet Anda."
                    )
                }

                val bodyStr = response.body?.string()
                    ?: return@withContext UpdateCheckResult.Error("Respon server kosong.")

                val json = JSONObject(bodyStr)
                val tagName = json.optString("tag_name", "")
                val releaseTitle = json.optString("name", tagName)
                val releaseNotes = json.optString("body", "")
                val releaseUrl = json.optString("html_url", LATEST_RELEASE_PAGE_URL)

                var directDownloadUrl: String? = null
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            directDownloadUrl = asset.optString("browser_download_url", null)
                            break
                        }
                    }
                }

                val remoteVer = tagName.removePrefix("v").trim()
                val isNewer = isVersionNewer(remoteVer, currentVersion)

                val releaseInfo = ReleaseInfo(
                    tagName = tagName,
                    versionName = remoteVer,
                    releaseTitle = releaseTitle,
                    releaseNotes = releaseNotes,
                    releaseUrl = releaseUrl,
                    downloadUrl = directDownloadUrl ?: releaseUrl,
                    isNewer = isNewer
                )

                if (isNewer) {
                    UpdateCheckResult.Success(releaseInfo)
                } else {
                    UpdateCheckResult.UpToDate(currentVersion)
                }
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error("Tidak dapat terhubung ke server GitHub (${e.localizedMessage}). Pastikan perangkat terhubung ke internet.")
        }
    }

    suspend fun getLatestReleaseInfo(): ReleaseInfo? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(RELEASES_API_URL)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "ZX-Icon-Changer-App")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyStr = response.body?.string() ?: return@withContext null
                val json = JSONObject(bodyStr)
                val tagName = json.optString("tag_name", "")
                val releaseTitle = json.optString("name", tagName)
                val releaseNotes = json.optString("body", "")
                val releaseUrl = json.optString("html_url", LATEST_RELEASE_PAGE_URL)

                var directDownloadUrl: String? = null
                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            directDownloadUrl = asset.optString("browser_download_url", null)
                            break
                        }
                    }
                }

                val remoteVer = tagName.removePrefix("v").trim()
                ReleaseInfo(
                    tagName = tagName,
                    versionName = remoteVer,
                    releaseTitle = releaseTitle,
                    releaseNotes = releaseNotes,
                    releaseUrl = releaseUrl,
                    downloadUrl = directDownloadUrl ?: releaseUrl,
                    isNewer = false
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchDetailedReleaseStatus(): ReleaseStatusInfo = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(ALL_RELEASES_API_URL)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "ZX-Icon-Changer-App")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.code == 404) {
                    return@withContext ReleaseStatusInfo(
                        hasRelease = false,
                        tagName = "-",
                        releaseTitle = "Belum Ada Rilis di GitHub",
                        publishedAt = "-",
                        debugApk = null,
                        releaseApk = null,
                        errorMessage = "Repository belum memiliki rilis publik di GitHub."
                    )
                }

                if (!response.isSuccessful) {
                    return@withContext ReleaseStatusInfo(
                        hasRelease = false,
                        tagName = "-",
                        releaseTitle = "Error Jaringan",
                        publishedAt = "-",
                        debugApk = null,
                        releaseApk = null,
                        errorMessage = "Gagal menghubungi GitHub API (Kode HTTP: ${response.code})."
                    )
                }

                val body = response.body?.string() ?: return@withContext ReleaseStatusInfo(
                    hasRelease = false,
                    tagName = "-",
                    releaseTitle = "Respon Kosong",
                    publishedAt = "-",
                    debugApk = null,
                    releaseApk = null
                )

                val jsonArray = JSONArray(body)
                if (jsonArray.length() == 0) {
                    return@withContext ReleaseStatusInfo(
                        hasRelease = false,
                        tagName = "-",
                        releaseTitle = "Belum Ada Rilis",
                        publishedAt = "-",
                        debugApk = null,
                        releaseApk = null,
                        errorMessage = "Belum ada rilis yang dipublikasikan di repository ini."
                    )
                }

                var latestTagName = ""
                var latestTitle = ""
                var latestPublished = ""
                var foundDebug: ApkAsset? = null
                var foundRelease: ApkAsset? = null

                for (i in 0 until jsonArray.length()) {
                    val rel = jsonArray.getJSONObject(i)
                    if (latestTagName.isEmpty()) {
                        latestTagName = rel.optString("tag_name", "")
                        latestTitle = rel.optString("name", latestTagName)
                        latestPublished = rel.optString("published_at", "").take(10)
                    }

                    val assets = rel.optJSONArray("assets") ?: continue
                    for (j in 0 until assets.length()) {
                        val asset = assets.getJSONObject(j)
                        val name = asset.optString("name", "")
                        if (!name.endsWith(".apk", ignoreCase = true)) continue

                        val dlUrl = asset.optString("browser_download_url", "")
                        val bytes = asset.optLong("size", 0L)
                        val sizeMb = if (bytes > 0) {
                            String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
                        } else {
                            "Tersedia"
                        }

                        val isDebug = name.contains("debug", ignoreCase = true)
                        if (isDebug && foundDebug == null) {
                            foundDebug = ApkAsset(name, dlUrl, bytes, sizeMb, true)
                        } else if (!isDebug && foundRelease == null) {
                            foundRelease = ApkAsset(name, dlUrl, bytes, sizeMb, false)
                        }
                    }
                }

                ReleaseStatusInfo(
                    hasRelease = foundDebug != null || foundRelease != null,
                    tagName = latestTagName,
                    releaseTitle = latestTitle,
                    publishedAt = latestPublished,
                    debugApk = foundDebug,
                    releaseApk = foundRelease,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            ReleaseStatusInfo(
                hasRelease = false,
                tagName = "-",
                releaseTitle = "Gagal Memeriksa",
                publishedAt = "-",
                debugApk = null,
                releaseApk = null,
                errorMessage = "Koneksi gagal: ${e.localizedMessage}"
            )
        }
    }

    private fun isVersionNewer(remote: String, local: String): Boolean {
        try {
            val rParts = remote.split(".").map { it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }
            val lParts = local.split(".").map { it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }

            val maxLen = maxOf(rParts.size, lParts.size)
            for (i in 0 until maxLen) {
                val r = rParts.getOrElse(i) { 0 }
                val l = lParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
            return false
        } catch (e: Exception) {
            return remote != local && remote.isNotBlank()
        }
    }
}
