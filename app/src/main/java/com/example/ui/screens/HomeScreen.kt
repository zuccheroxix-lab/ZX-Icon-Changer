package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShortcutEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.ZXBadge
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXSectionHeader
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxBlue
import com.example.ui.theme.ZxBorderDark
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonGreen
import com.example.ui.theme.ZxNeonRed
import com.example.ui.theme.ZxSurfaceDark
import com.example.ui.theme.ZxSurfaceElevatedDark
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val shortcuts by viewModel.shortcuts.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val filteredApps = viewModel.getFilteredApps()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var shortcutToDelete by remember { mutableStateOf<ShortcutEntity?>(null) }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "ZX ICON CHANGER",
                subtitle = "ZUCCHERO XANN • DASHBOARD",
                actions = {
                    IconButton(
                        onClick = { viewModel.loadInstalledApps(true) },
                        modifier = Modifier.testTag("home_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Aplikasi",
                            tint = ZxCyan
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.BACKUP_RESTORE) },
                        modifier = Modifier.testTag("home_backup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = "Backup & Restore",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.DOWNLOAD_RELEASE) },
                        modifier = Modifier.testTag("home_download_apk_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download APK",
                            tint = ZxCyan
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                        modifier = Modifier.testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.APP_LIST) },
                containerColor = ZxCyan,
                contentColor = ZxSurfaceDark,
                shape = CircleShape,
                modifier = Modifier
                    .border(2.dp, ZxBlue, CircleShape)
                    .testTag("home_fab_add_icon")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat Icon Baru",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Android Limitation Notice Banner
            item {
                ZXLimitationBanner()
            }

            // Metrics Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Apps detected
                    ZXCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_apps_installed"),
                        isGlow = false
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Aplikasi",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ZxTextSecondaryDark
                                )
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = ZxCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isLoadingApps) "..." else "${filteredApps.size}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Terdeteksi di HP",
                                style = MaterialTheme.typography.labelSmall,
                                color = ZxCyan
                            )
                        }
                    }

                    // Shortcuts created
                    ZXCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_shortcuts_created"),
                        isGlow = true
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Icon ZX",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ZxTextSecondaryDark
                                )
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ZxCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${shortcuts.size}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = ZxCyan
                            )
                            Text(
                                text = "Shortcut Dibuat",
                                style = MaterialTheme.typography.labelSmall,
                                color = ZxTextSecondaryDark
                            )
                        }
                    }
                }
            }

            // Action Buttons Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ZXButton(
                        text = "Pilih Aplikasi",
                        onClick = { viewModel.navigateTo(Screen.APP_LIST) },
                        icon = Icons.Default.Apps,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_button_select_app")
                    )

                    ZXOutlinedButton(
                        text = "Kelola Icon",
                        onClick = { viewModel.navigateTo(Screen.SHORTCUTS_MANAGER) },
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_button_manage_icons")
                    )
                }
            }

            // Quick App Search Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Cari aplikasi terpasang...",
                            color = ZxTextMutedDark,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ZxCyan
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZxCyan,
                        unfocusedBorderColor = ZxBorderDark,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_quick_search_field")
                )
            }

            // Shortcuts Created Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZXSectionHeader(
                        title = "Icon Alternatif ZX",
                        subtitle = "${shortcuts.size} shortcut aktif"
                    )
                    if (shortcuts.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.navigateTo(Screen.SHORTCUTS_MANAGER) },
                            modifier = Modifier.testTag("home_view_all_shortcuts")
                        ) {
                            Text("Lihat Semua", color = ZxCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (shortcuts.isEmpty()) {
                item {
                    ZXCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_empty_shortcuts_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(ZxSurfaceVariantDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ZxCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Icon Alternatif",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pilih aplikasi apapun dan ubah icon serta namanya menjadi tampilan futuristik ZX.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZxTextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ZXButton(
                                text = "Buat Icon Sekarang",
                                onClick = { viewModel.navigateTo(Screen.APP_LIST) },
                                icon = Icons.Default.Add,
                                testTag = "home_empty_cta_button"
                            )
                        }
                    }
                }
            } else {
                items(shortcuts.take(5), key = { it.id }) { shortcut ->
                    ShortcutItemCard(
                        shortcut = shortcut,
                        onLaunch = {
                            viewModel.launchTargetApp(context, shortcut.targetPackage, shortcut.targetActivity)
                        },
                        onPin = {
                            viewModel.pinExistingShortcut(context, shortcut)
                        },
                        onDelete = {
                            shortcutToDelete = shortcut
                        },
                        loadBitmap = {
                            viewModel.shortcutRepository.loadShortcutIconBitmap(shortcut)
                        }
                    )
                }
            }

            // Quick App Selection Section
            item {
                ZXSectionHeader(
                    title = "Aplikasi Terpasang",
                    subtitle = "Sentuh aplikasi untuk membuat icon baru"
                )
            }

            items(filteredApps.take(6), key = { it.packageName }) { app ->
                AppMiniRowItem(
                    app = app,
                    onClick = { viewModel.selectApp(app) },
                    loadIcon = { viewModel.appRepository.getAppIconBitmap(app.packageName) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    shortcutToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { shortcutToDelete = null },
            title = {
                Text("Hapus Shortcut?", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Shortcut '${target.shortcutName}' akan dihapus dari aplikasi ZX Icon Changer. Aplikasi target '${target.targetAppName}' tidak akan di-uninstall.",
                    color = ZxTextSecondaryDark
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteShortcut(target)
                        shortcutToDelete = null
                    }
                ) {
                    Text("Hapus", color = ZxNeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { shortcutToDelete = null }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = ZxSurfaceDark
        )
    }
}

@Composable
fun ShortcutItemCard(
    shortcut: ShortcutEntity,
    onLaunch: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    loadBitmap: suspend () -> Bitmap?
) {
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = shortcut.iconFileName) {
        value = loadBitmap()
    }

    ZXCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shortcut_item_${shortcut.shortcutId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ZxSurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = shortcut.shortcutName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ZxCyan
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shortcut.shortcutName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Target: ${shortcut.targetAppName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ZxTextSecondaryDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ZXBadge(text = shortcut.iconShape, color = ZxCyan)
                }
            }

            // Actions
            IconButton(onClick = onLaunch, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Buka Target",
                    tint = ZxCyan
                )
            }
            IconButton(onClick = onPin, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Sematkan ke Launcher",
                    tint = ZxBlue
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Shortcut",
                    tint = ZxNeonRed
                )
            }
        }
    }
}

@Composable
fun AppMiniRowItem(
    app: com.example.data.model.InstalledApp,
    onClick: () -> Unit,
    loadIcon: suspend () -> Bitmap
) {
    val iconBitmap by produceState<Bitmap?>(initialValue = null, key1 = app.packageName) {
        value = loadIcon()
    }

    ZXCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_mini_row_${app.packageName}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ZxSurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap!!.asImageBitmap(),
                        contentDescription = app.appName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = ZxCyan
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = ZxTextMutedDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Pilih",
                tint = ZxCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
