package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReleaseStatusInfo
import com.example.data.repository.UpdateRepository
import com.example.ui.MainViewModel
import com.example.ui.components.ZXBadge
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonGreen
import com.example.ui.theme.ZxSurfaceDark
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark
import kotlinx.coroutines.launch

@Composable
fun DownloadReleaseScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var releaseStatus by remember { mutableStateOf<ReleaseStatusInfo?>(null) }
    var isLoadingStatus by remember { mutableStateOf(true) }
    var infoDialogMessage by remember { mutableStateOf<String?>(null) }

    fun refreshStatus() {
        coroutineScope.launch {
            isLoadingStatus = true
            releaseStatus = viewModel.updateRepository.fetchDetailedReleaseStatus()
            isLoadingStatus = false
        }
    }

    LaunchedEffect(Unit) {
        refreshStatus()
    }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "DOWNLOAD APK",
                subtitle = "DISTRIBUSI RESMI GITHUB",
                showBack = true,
                onBackClick = { viewModel.navigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Build & Release Status Card
            ZXCard(
                modifier = Modifier.fillMaxWidth(),
                isGlow = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = ZxCyan,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ZX Icon Changer",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "SISTEM DISTRIBUSI APK ASLI",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = ZxCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoadingStatus) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = ZxCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Menghubungi GitHub API...",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZxTextSecondaryDark
                            )
                        }
                    } else {
                        val hasAny = releaseStatus?.hasRelease == true
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ZXBadge(
                                text = "Build: ${if (hasAny) "Terverifikasi" else "Lokal (Ready)"}",
                                color = if (hasAny) ZxNeonGreen else ZxCyan
                            )
                            ZXBadge(
                                text = "Version: 1.0.0",
                                color = ZxCyan
                            )
                        }
                    }
                }
            }

            // Real Live Status Overview Card
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STATUS BUILD & RILIS GITHUB",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = ZxCyan
                        )
                        TextButton(
                            onClick = { refreshStatus() },
                            enabled = !isLoadingStatus
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = ZxCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh", style = MaterialTheme.typography.labelSmall, color = ZxCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    StatusItem(
                        label = "Build Status",
                        value = when {
                            isLoadingStatus -> "Memeriksa..."
                            releaseStatus?.hasRelease == true -> "Tersedia di GitHub Releases"
                            else -> "Belum ada rilis di GitHub (Repository baru)"
                        },
                        isHighlight = releaseStatus?.hasRelease == true
                    )

                    StatusItem(
                        label = "Debug APK",
                        value = when {
                            isLoadingStatus -> "Memeriksa..."
                            releaseStatus?.debugApk != null -> "${releaseStatus?.debugApk?.fileName} (${releaseStatus?.debugApk?.sizeHuman})"
                            else -> "Belum tersedia di GitHub"
                        }
                    )

                    StatusItem(
                        label = "Release APK",
                        value = when {
                            isLoadingStatus -> "Memeriksa..."
                            releaseStatus?.releaseApk != null -> "${releaseStatus?.releaseApk?.fileName} (${releaseStatus?.releaseApk?.sizeHuman})"
                            else -> "Belum tersedia"
                        }
                    )

                    StatusItem(
                        label = "Version",
                        value = "v1.0.0 (Tag: ${releaseStatus?.tagName?.ifBlank { "v1.0.0" } ?: "v1.0.0"})"
                    )

                    StatusItem(
                        label = "Build Date",
                        value = releaseStatus?.publishedAt?.ifBlank { "2026-10-04" } ?: "2026-10-04"
                    )
                }
            }

            // 1. DEBUG APK SECTION
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. DEBUG APK (INSTALASI CEPAT)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Build Debug APK yang ditandatangani otomatis dengan debug keystore. Langsung dapat dipasang di seluruh perangkat Android 7.0+ tanpa konfigurasi keystore tambahan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val debugApk = releaseStatus?.debugApk
                    if (debugApk != null) {
                        ZXButton(
                            text = "DOWNLOAD DEBUG APK (${debugApk.sizeHuman})",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(debugApk.downloadUrl)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            icon = Icons.Default.Download,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_debug_apk_btn")
                        )
                    } else {
                        ZXOutlinedButton(
                            text = "Download Debug APK (Belum Tersedia)",
                            onClick = {
                                infoDialogMessage = "Debug APK belum tersedia di GitHub Releases.\n\nWorkflow GitHub Actions akan otomatis membuat file 'ZX-Icon-Changer-debug.apk' saat Anda melakukan push commit ke branch main di repository GitHub."
                            },
                            icon = Icons.Default.Info,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_debug_apk_unavailable_btn")
                        )
                    }
                }
            }

            // 2. RELEASE APK SECTION
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. RELEASE APK (OPTIMAL)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Build Release APK teroptimasi ukuran file dan performa. Jika signing belum dikonfigurasi di GitHub Secrets, sistem otomatis menggunakan build fallback.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val releaseApk = releaseStatus?.releaseApk
                    if (releaseApk != null) {
                        ZXButton(
                            text = "DOWNLOAD RELEASE APK (${releaseApk.sizeHuman})",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(releaseApk.downloadUrl)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            icon = Icons.Default.Download,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_release_apk_btn")
                        )
                    } else {
                        ZXOutlinedButton(
                            text = "Download Release APK (Belum Tersedia)",
                            onClick = {
                                infoDialogMessage = "Release APK belum tersedia.\n\nFile Release APK memerlukan tag rilis (contoh: v1.0.0) di repository GitHub. Anda tetap dapat mengunduh Debug APK yang siap pakai."
                            },
                            icon = Icons.Default.Info,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_release_apk_unavailable_btn")
                        )
                    }
                }
            }

            // GitHub Repository Navigation Buttons
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NAVIGASI REPOSITORY RESMI",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ZxCyan
                    )

                    ZXOutlinedButton(
                        text = "Buka Halaman Releases GitHub",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UpdateRepository.RELEASES_PAGE_URL)).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        icon = Icons.Default.ListAlt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_github_releases_page_btn")
                    )

                    ZXOutlinedButton(
                        text = "Buka Repository Source Code",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UpdateRepository.REPO_URL)).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        icon = Icons.Default.Language,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_github_repo_btn")
                    )
                }
            }

            ZXLimitationBanner()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    infoDialogMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { infoDialogMessage = null },
            title = {
                Text("Status APK", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(msg, color = ZxTextSecondaryDark)
            },
            confirmButton = {
                TextButton(onClick = { infoDialogMessage = null }) {
                    Text("Mengerti", color = ZxCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ZxSurfaceDark
        )
    }
}

@Composable
private fun StatusItem(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = ZxTextMutedDark)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = if (isHighlight) ZxNeonGreen else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
