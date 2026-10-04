package com.example.ui.screens

import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SystemUpdate
import com.example.ui.components.ZXButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.image.IconShape
import com.example.settings.ThemeMode
import com.example.ui.MainViewModel
import com.example.ui.components.ZXBadge
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXSectionHeader
import com.example.ui.components.ZXSliderControl
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonRed
import com.example.ui.theme.ZxSurfaceDark
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val currentThemeMode by viewModel.themeMode.collectAsState()
    var defaultShape by remember { mutableStateOf(viewModel.preferencesManager.defaultIconShape) }
    var defaultPadding by remember { mutableIntStateOf(viewModel.preferencesManager.defaultPadding) }
    var confirmDelete by remember { mutableStateOf(viewModel.preferencesManager.confirmBeforeDelete) }
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
    val updateCheckResult by viewModel.updateCheckResult.collectAsState()
    var isCheckingRelease by remember { mutableStateOf(false) }
    var releaseUnavailableMessage by remember { mutableStateOf<String?>(null) }

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "PENGATURAN",
                subtitle = "PREFERENSI SISTEM ZX",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Mode
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ZXSectionHeader(
                        title = "Tema Tampilan",
                        subtitle = "Pilih gaya visual antarmuka"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            FilterChip(
                                selected = currentThemeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = {
                                    Text(
                                        when (mode) {
                                            ThemeMode.DARK -> "Dark (ZX)"
                                            ThemeMode.LIGHT -> "Light"
                                            ThemeMode.SYSTEM -> "Sistem"
                                        }
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ZxCyan,
                                    selectedLabelColor = ZxSurfaceDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Default Shape & Padding for Editor
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ZXSectionHeader(
                        title = "Bentuk Bawaan Editor",
                        subtitle = "Bentuk icon otomatis saat membuka editor"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconShape.values().forEach { shape ->
                            FilterChip(
                                selected = defaultShape == shape.name,
                                onClick = {
                                    defaultShape = shape.name
                                    viewModel.setDefaultIconShape(shape.name)
                                },
                                label = { Text(shape.label.split(" ").first()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ZxCyan,
                                    selectedLabelColor = ZxSurfaceDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ZXSliderControl(
                        label = "Padding Bawaan",
                        value = defaultPadding.toFloat(),
                        onValueChange = {
                            defaultPadding = it.toInt()
                            viewModel.setDefaultPadding(it.toInt())
                        },
                        valueRange = 0f..40f,
                        valueDisplay = "${defaultPadding}px"
                    )
                }
            }

            // Confirm Delete Toggle
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Konfirmasi Sebelum Hapus",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tampilkan dialog konfirmasi saat ingin menghapus shortcut dari daftar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZxTextSecondaryDark
                        )
                    }

                    Switch(
                        checked = confirmDelete,
                        onCheckedChange = {
                            confirmDelete = it
                            viewModel.setConfirmBeforeDelete(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ZxCyan,
                            checkedTrackColor = ZxSurfaceVariantDark
                        )
                    )
                }
            }

            // Reset Settings
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reset Pengaturan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Mengembalikan tema, bentuk bawaan, dan preferensi aplikasi ke setelan standar ZX.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ZXOutlinedButton(
                        text = "Reset ke Pengaturan Awal",
                        onClick = { showResetDialog = true },
                        icon = Icons.Default.RestartAlt,
                        borderColor = ZxNeonRed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // About ZUCCHERO XANN / ZX Icon Changer
            ZXCard(
                modifier = Modifier.fillMaxWidth(),
                isGlow = true
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ZX ICON CHANGER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ZXBadge(text = "v1.0.0", color = ZxCyan)
                    }

                    Text(
                        text = "ENGINEERING BY ZUCCHERO XANN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = ZxCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Aplikasi pengubah icon alternatif dan pembuat shortcut launcher modern tanpa akses root, tanpa modifikasi APK asli, dan tanpa backend eksternal. Sepenuhnya offline dan aman untuk privasi Anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Arsitektur: Android Native • Jetpack Compose • Room Database • ShortcutManager API • Photo Picker",
                        style = MaterialTheme.typography.labelSmall,
                        color = ZxTextMutedDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // GitHub, Download APK, and Update Checker Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ZXButton(
                            text = if (isCheckingRelease) "Memeriksa Rilis..." else "Download APK (Official Release)",
                            onClick = {
                                coroutineScope.launch {
                                    isCheckingRelease = true
                                    val release = viewModel.updateRepository.getLatestReleaseInfo()
                                    isCheckingRelease = false
                                    if (release != null) {
                                        val url = release.downloadUrl ?: release.releaseUrl
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse(url)
                                        ).apply { flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK }
                                        context.startActivity(intent)
                                    } else {
                                        releaseUnavailableMessage = "APK release belum tersedia.\n\nRelease resmi belum dipublikasikan di repository GitHub. Silakan buat tag rilis (misal v1.0.0) di GitHub untuk membuat APK secara otomatis."
                                    }
                                }
                            },
                            icon = Icons.Default.Download,
                            enabled = !isCheckingRelease,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_download_latest_btn")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ZXOutlinedButton(
                                text = "GitHub",
                                onClick = {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(com.example.data.repository.UpdateRepository.REPO_URL)
                                    ).apply { flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK }
                                    context.startActivity(intent)
                                },
                                icon = Icons.Default.Language,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("settings_github_btn")
                            )

                            ZXOutlinedButton(
                                text = if (isCheckingUpdate) "Cek..." else "Check for Update",
                                onClick = { viewModel.checkForUpdates("1.0.0") },
                                icon = Icons.Default.CheckCircle,
                                enabled = !isCheckingUpdate,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("settings_check_update_btn")
                            )
                        }

                        ZXOutlinedButton(
                            text = "Halaman Unduh & Info Rilis",
                            onClick = { viewModel.navigateTo(com.example.ui.Screen.DOWNLOAD_RELEASE) },
                            icon = Icons.Default.SystemUpdate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_open_download_screen_btn")
                        )
                    }
                }
            }

            ZXLimitationBanner()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text("Reset Pengaturan?", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Pengaturan tema, padding, dan preferensi akan dikembalikan ke nilai awal. Shortcut yang telah dibuat tidak akan terhapus.",
                    color = ZxTextSecondaryDark
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllSettings()
                        defaultShape = viewModel.preferencesManager.defaultIconShape
                        defaultPadding = viewModel.preferencesManager.defaultPadding
                        confirmDelete = viewModel.preferencesManager.confirmBeforeDelete
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = ZxNeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = ZxSurfaceDark
        )
    }

    // Update Result Dialog
    updateCheckResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.clearUpdateCheckResult() },
            title = {
                Text(
                    text = when (result) {
                        is com.example.data.repository.UpdateCheckResult.Success -> "Pembaruan Tersedia!"
                        is com.example.data.repository.UpdateCheckResult.UpToDate -> "Versi Terkini"
                        is com.example.data.repository.UpdateCheckResult.NoReleaseFound -> "Status Rilis"
                        is com.example.data.repository.UpdateCheckResult.Error -> "Periksa Pembaruan"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                when (result) {
                    is com.example.data.repository.UpdateCheckResult.Success -> {
                        Column {
                            Text(
                                text = "Versi baru ${result.releaseInfo.tagName} telah dirilis di GitHub.",
                                color = ZxCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                            if (result.releaseInfo.releaseNotes.isNotBlank()) {
                                Text(
                                    text = result.releaseInfo.releaseNotes.take(300),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ZxTextSecondaryDark
                                )
                            }
                        }
                    }
                    is com.example.data.repository.UpdateCheckResult.UpToDate -> {
                        Text(
                            text = "Aplikasi Anda sudah dalam versi terbaru (v${result.currentVersion}). Tidak ada pembaruan baru saat ini.",
                            color = ZxTextSecondaryDark
                        )
                    }
                    is com.example.data.repository.UpdateCheckResult.NoReleaseFound -> {
                        Text(
                            text = result.message,
                            color = ZxTextSecondaryDark
                        )
                    }
                    is com.example.data.repository.UpdateCheckResult.Error -> {
                        Text(
                            text = result.message,
                            color = ZxTextSecondaryDark
                        )
                    }
                }
            },
            confirmButton = {
                if (result is com.example.data.repository.UpdateCheckResult.Success) {
                    TextButton(
                        onClick = {
                            val url = result.releaseInfo.downloadUrl ?: result.releaseInfo.releaseUrl
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                            viewModel.clearUpdateCheckResult()
                        }
                    ) {
                        Text("Unduh Update", color = ZxCyan, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(onClick = { viewModel.clearUpdateCheckResult() }) {
                        Text("Mengerti", color = ZxCyan, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (result is com.example.data.repository.UpdateCheckResult.Success) {
                    TextButton(onClick = { viewModel.clearUpdateCheckResult() }) {
                        Text("Nanti", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            },
            containerColor = ZxSurfaceDark
        )
    }

    // Release Unavailable Dialog
    releaseUnavailableMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { releaseUnavailableMessage = null },
            title = {
                Text("Informasi Rilis", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(msg, color = ZxTextSecondaryDark)
            },
            confirmButton = {
                TextButton(onClick = { releaseUnavailableMessage = null }) {
                    Text("Mengerti", color = ZxCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ZxSurfaceDark
        )
    }
}
