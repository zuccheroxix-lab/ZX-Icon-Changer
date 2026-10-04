package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.ZXBadge
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXSectionHeader
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppDetailScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val app = viewModel.selectedApp.collectAsState().value
    val shortcuts by viewModel.shortcuts.collectAsState()

    if (app == null) {
        viewModel.navigateBack()
        return
    }

    val existingShortcuts = shortcuts.filter { it.targetPackage == app.packageName }

    val iconBitmap by produceState<Bitmap?>(initialValue = null, key1 = app.packageName) {
        value = viewModel.appRepository.getAppIconBitmap(app.packageName)
    }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = app.appName,
                subtitle = "DETAIL TARGET APLIKASI",
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
            // Hero Icon Preview
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ZxSurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap!!.asImageBitmap(),
                        contentDescription = app.appName,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Names & Identifiers
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZxCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ZXBadge(text = "Versi ${app.versionName}", color = ZxCyan)
                    if (app.isSystemApp) {
                        ZXBadge(text = "Aplikasi Sistem", color = ZxTextSecondaryDark)
                    } else {
                        ZXBadge(text = "User Installed", color = ZxCyan)
                    }
                }
            }

            // Primary Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZXButton(
                    text = "Buat Icon Alternatif",
                    onClick = { viewModel.startEditorForSelectedApp(app) },
                    icon = Icons.Default.AutoAwesome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_detail_create_icon_btn")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ZXOutlinedButton(
                        text = "Buka Aplikasi",
                        onClick = {
                            viewModel.launchTargetApp(context, app.packageName, app.targetActivity)
                        },
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("app_detail_launch_app_btn")
                    )

                    ZXOutlinedButton(
                        text = "Info Aplikasi",
                        onClick = {
                            viewModel.openAppDetailsSettings(context, app.packageName)
                        },
                        icon = Icons.Default.Info,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("app_detail_app_info_btn")
                    )
                }
            }

            // Technical details card
            ZXCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMASI TEKNIS TARGET",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ZxCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow(label = "Target Activity", value = app.targetActivity)
                    DetailRow(
                        label = "Terakhir Diperbarui",
                        value = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(
                            Date(app.lastUpdateTime)
                        )
                    )
                    DetailRow(
                        label = "Pertama Dipasang",
                        value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                            Date(app.installedTime)
                        )
                    )
                }
            }

            // Existing shortcuts created for this app
            if (existingShortcuts.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ZXSectionHeader(
                        title = "Shortcut ZX Aktif (${existingShortcuts.size})",
                        subtitle = "Shortcut yang sudah dibuat untuk aplikasi ini"
                    )
                    existingShortcuts.forEach { sc ->
                        ShortcutItemCard(
                            shortcut = sc,
                            onLaunch = {
                                viewModel.launchTargetApp(context, sc.targetPackage, sc.targetActivity)
                            },
                            onPin = {
                                viewModel.pinExistingShortcut(context, sc)
                            },
                            onDelete = {
                                viewModel.deleteShortcut(sc)
                            },
                            loadBitmap = {
                                viewModel.shortcutRepository.loadShortcutIconBitmap(sc)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Limitation banner
            ZXLimitationBanner()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = ZxTextMutedDark)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
