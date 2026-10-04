package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXSectionHeader
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonGreen
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val shortcuts by viewModel.shortcuts.collectAsState()

    var isProcessing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }

    // Export Document Launcher (SAF)
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isProcessing = true
                try {
                    val json = viewModel.shortcutRepository.exportConfigJson()
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            OutputStreamWriter(out).use { writer ->
                                writer.write(json)
                            }
                        }
                    }
                    resultText = "Ekspor Berhasil! Disimpan ke file dokumen lokal."
                    Toast.makeText(context, "Backup berhasil disimpan", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    resultText = "Gagal mengekspor: ${e.localizedMessage}"
                    Toast.makeText(context, "Gagal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                } finally {
                    isProcessing = false
                }
            }
        }
    }

    // Import Document Launcher (SAF)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isProcessing = true
                try {
                    val content = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            BufferedReader(InputStreamReader(input)).use { reader ->
                                reader.readText()
                            }
                        }
                    }

                    if (!content.isNullOrEmpty()) {
                        val res = viewModel.shortcutRepository.importConfigJson(content)
                        if (res.error != null) {
                            resultText = "Gagal memulihkan: ${res.error}"
                        } else {
                            resultText = "Pulihkan Berhasil! ${res.importedCount} shortcut dipulihkan (${res.skippedCount} dilewati)."
                            Toast.makeText(context, "Berhasil memulihkan ${res.importedCount} shortcut", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        resultText = "File kosong atau tidak dapat dibaca."
                    }
                } catch (e: Exception) {
                    resultText = "Kesalahan impor: ${e.localizedMessage}"
                } finally {
                    isProcessing = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "BACKUP & RESTORE",
                subtitle = "PENYIMPANAN LOKAL OFFLINE",
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
            // Privacy & Offline Promise Banner
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ZxNeonGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100% LOKAL & OFFLINE",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ZxNeonGreen
                        )
                        Text(
                            text = "Data shortcut, nama, dan icon disimpan di perangkat Anda sendiri. Tidak ada data yang diunggah ke internet/server.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZxTextSecondaryDark
                        )
                    }
                }
            }

            // Export Section
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ZXSectionHeader(
                        title = "Ekspor Cadangan (Backup)",
                        subtitle = "Simpan konfigurasi ${shortcuts.size} shortcut ke file JSON"
                    )
                    Text(
                        text = "File backup berisi daftar aplikasi target, label shortcut, bentuk icon, dan data bitmap yang dikonversi ke format portabel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ZXButton(
                        text = "Ekspor ke File JSON",
                        onClick = {
                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                            exportLauncher.launch("ZX_Icon_Changer_Backup_$timestamp.json")
                        },
                        icon = Icons.Default.CloudUpload,
                        enabled = !isProcessing && shortcuts.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("backup_export_btn")
                    )
                }
            }

            // Import Section
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ZXSectionHeader(
                        title = "Pulihkan Cadangan (Restore)",
                        subtitle = "Impor shortcut dari file JSON yang pernah Anda buat"
                    )
                    Text(
                        text = "Pilih file backup JSON dari penyimpanan perangkat Anda untuk memulihkan seluruh icon dan shortcut yang pernah dibuat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ZXOutlinedButton(
                        text = "Pilih File Backup JSON",
                        onClick = {
                            importLauncher.launch(arrayOf("application/json", "text/*"))
                        },
                        icon = Icons.Default.CloudDownload,
                        enabled = !isProcessing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("backup_import_btn")
                    )
                }
            }

            if (isProcessing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = ZxCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Sedang memproses data...", color = ZxCyan, style = MaterialTheme.typography.bodyMedium)
                }
            }

            resultText?.let { result ->
                ZXCard(
                    modifier = Modifier.fillMaxWidth(),
                    isGlow = true
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ZxCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            ZXLimitationBanner()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
