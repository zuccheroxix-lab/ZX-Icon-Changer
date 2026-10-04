package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.ShortcutEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.ZXBadge
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxBlue
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonRed
import com.example.ui.theme.ZxSurfaceDark
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShortcutsManagerScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val shortcuts by viewModel.shortcuts.collectAsState()
    var shortcutToDelete by remember { mutableStateOf<ShortcutEntity?>(null) }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "KELOLA ICON",
                subtitle = "SEMUA SHORTCUT TERSIMPAN",
                showBack = true,
                onBackClick = { viewModel.navigateBack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (shortcuts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ZxSurfaceVariantDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ZxCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum Ada Shortcut ZX",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Semua icon yang Anda buat akan muncul di sini untuk dikelola, disematkan ulang, atau dihapus.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ZxTextSecondaryDark,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ZXButton(
                        text = "Buat Icon Baru",
                        onClick = { viewModel.navigateTo(Screen.APP_LIST) },
                        icon = Icons.Default.Add,
                        testTag = "manager_empty_add_btn"
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ZXLimitationBanner()
                }

                item {
                    Text(
                        text = "${shortcuts.size} Shortcut Tersimpan di Database Lokal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ZxCyan
                    )
                }

                items(shortcuts, key = { it.id }) { item ->
                    ManagerShortcutItem(
                        shortcut = item,
                        onLaunch = {
                            viewModel.launchTargetApp(context, item.targetPackage, item.targetActivity)
                        },
                        onPin = {
                            viewModel.pinExistingShortcut(context, item)
                        },
                        onDelete = {
                            shortcutToDelete = item
                        },
                        loadBitmap = {
                            viewModel.shortcutRepository.loadShortcutIconBitmap(item)
                        }
                    )
                }
            }
        }
    }

    shortcutToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { shortcutToDelete = null },
            title = {
                Text("Hapus Shortcut?", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Menghapus '${target.shortcutName}' hanya akan menghapus shortcut dan icon custom dari penyimpanan lokal aplikasi ini. Aplikasi target '${target.targetAppName}' tidak akan di-uninstall.",
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
private fun ManagerShortcutItem(
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
            .testTag("manager_item_${shortcut.shortcutId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
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
                        color = ZxCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = shortcut.targetPackage,
                        style = MaterialTheme.typography.labelSmall,
                        color = ZxTextSecondaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dibuat: " + SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                        Date(shortcut.createdAt)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = ZxTextSecondaryDark
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onLaunch, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Buka", tint = ZxCyan)
                    }
                    IconButton(onClick = onPin, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.PushPin, contentDescription = "Sematkan Ulang", tint = ZxBlue)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ZxNeonRed)
                    }
                }
            }
        }
    }
}
