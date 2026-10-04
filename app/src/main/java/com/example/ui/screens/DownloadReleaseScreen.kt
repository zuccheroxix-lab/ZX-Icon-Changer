package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark

@Composable
fun DownloadReleaseScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "UNDUH & RILIS",
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
            // Main Release Card
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
                        text = "LATEST RELEASE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = ZxCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ZXBadge(text = "Versi: 1.0.0", color = ZxCyan)
                        ZXBadge(text = "Status: Stable", color = ZxNeonGreen)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Rilis stabil resmi dengan arsitektur Android native modern. Bebas iklan, tanpa analitik pelacak, dan 100% diproses secara lokal pada perangkat Anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Official Download APK Button
                    ZXButton(
                        text = "DOWNLOAD APK RESMI",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UpdateRepository.LATEST_RELEASE_PAGE_URL)).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        icon = Icons.Default.Download,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("download_screen_apk_button")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // GitHub Repository Button
                    ZXOutlinedButton(
                        text = "Buka Repository GitHub",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UpdateRepository.REPO_URL)).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        icon = Icons.Default.Language,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("download_screen_github_button")
                    )
                }
            }

            // Check for Update Card
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Periksa Pembaruan Sistem",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cek apakah ada rilis terbaru di repository GitHub resmi zuccheroxix-lab/ZX-Icon-Changer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ZxTextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (isCheckingUpdate) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = ZxCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Menghubungi GitHub API...",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZxCyan
                            )
                        }
                    } else {
                        ZXOutlinedButton(
                            text = "Check for Update",
                            onClick = {
                                viewModel.checkForUpdates("1.0.0")
                            },
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_screen_check_update_btn")
                        )
                    }
                }
            }

            // Specs Card
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SPESIFIKASI BUILD & DISTRIBUSI",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ZxCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SpecRow(label = "Package Name", value = "com.aistudio.zxiconchanger.app")
                    SpecRow(label = "Build Variant", value = "Release (Arm64, x86_64, Armeabi-v7a)")
                    SpecRow(label = "Min Android", value = "Android 7.0 (API 24)")
                    SpecRow(label = "Target Android", value = "Android 16 (API 36)")
                    SpecRow(label = "CI/CD Pipeline", value = "GitHub Actions Workflow")
                }
            }

            ZXLimitationBanner()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = ZxTextMutedDark)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
    }
}
