package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledApp
import com.example.ui.AppFilter
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.SortOrder
import com.example.ui.components.ZXBadge
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxBlue
import com.example.ui.theme.ZxBorderDark
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonGreen
import com.example.ui.theme.ZxSurfaceDark
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark

@Composable
fun AppListScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val isLoading by viewModel.isLoadingApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val appFilter by viewModel.appFilter.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()

    val filteredApps = viewModel.getFilteredApps()
    val shortcutPackages = shortcuts.map { it.targetPackage }.toSet()

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "PILIH APLIKASI",
                subtitle = "DAFTAR APLIKASI TERPASANG",
                showBack = true,
                onBackClick = { viewModel.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadInstalledApps(true) },
                        modifier = Modifier.testTag("app_list_refresh_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = ZxCyan
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        "Cari nama aplikasi atau package...",
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("app_list_search_input")
            )

            // Filter Chips (All, User, System) & Sort Order
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = appFilter == AppFilter.ALL,
                    onClick = { viewModel.setAppFilter(AppFilter.ALL) },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ZxCyan,
                        selectedLabelColor = ZxSurfaceDark
                    ),
                    modifier = Modifier.testTag("filter_all_apps")
                )
                FilterChip(
                    selected = appFilter == AppFilter.USER,
                    onClick = { viewModel.setAppFilter(AppFilter.USER) },
                    label = { Text("User Apps") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ZxCyan,
                        selectedLabelColor = ZxSurfaceDark
                    ),
                    modifier = Modifier.testTag("filter_user_apps")
                )
                FilterChip(
                    selected = appFilter == AppFilter.SYSTEM,
                    onClick = { viewModel.setAppFilter(AppFilter.SYSTEM) },
                    label = { Text("Sistem") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ZxCyan,
                        selectedLabelColor = ZxSurfaceDark
                    ),
                    modifier = Modifier.testTag("filter_system_apps")
                )

                Spacer(modifier = Modifier.weight(1f))

                // Sort toggle
                IconButton(
                    onClick = {
                        val next = when (sortOrder) {
                            SortOrder.A_Z -> SortOrder.Z_A
                            SortOrder.Z_A -> SortOrder.RECENT
                            SortOrder.RECENT -> SortOrder.A_Z
                        }
                        viewModel.setSortOrder(next)
                    },
                    modifier = Modifier.testTag("app_list_sort_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = "Urutan: ${sortOrder.name}",
                        tint = ZxCyan
                    )
                }
            }

            // Results count
            Text(
                text = "${filteredApps.size} aplikasi ditemukan • Urutan: ${sortOrder.name.replace("_", "-")}",
                style = MaterialTheme.typography.labelSmall,
                color = ZxTextSecondaryDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ZxCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Memuat daftar aplikasi dari sistem...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ZxTextSecondaryDark
                        )
                    }
                }
            } else if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada aplikasi yang cocok dengan pencarian.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ZxTextSecondaryDark
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        val hasCustomIcon = shortcutPackages.contains(app.packageName)
                        AppListItem(
                            app = app,
                            hasCustomIcon = hasCustomIcon,
                            onClick = { viewModel.selectApp(app) },
                            loadIcon = { viewModel.appRepository.getAppIconBitmap(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppListItem(
    app: InstalledApp,
    hasCustomIcon: Boolean,
    onClick: () -> Unit,
    loadIcon: suspend () -> Bitmap
) {
    val iconBitmap by produceState<Bitmap?>(initialValue = null, key1 = app.packageName) {
        value = loadIcon()
    }

    ZXCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_item_${app.packageName}"),
        onClick = onClick,
        isGlow = hasCustomIcon
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
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

            // App details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (hasCustomIcon) {
                        ZXBadge(text = "ZX CUSTOM", color = ZxCyan)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = ZxTextMutedDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "v${app.versionName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ZxTextSecondaryDark
                    )
                    if (app.isSystemApp) {
                        Text(
                            text = "• Sistem",
                            style = MaterialTheme.typography.labelSmall,
                            color = ZxTextMutedDark
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Pilih",
                tint = if (hasCustomIcon) ZxCyan else ZxBlue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
