package com.example.ui.screens

import android.graphics.Color as AndroidColor
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatShapes
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.image.IconShape
import com.example.image.PresetIcons
import com.example.ui.EditorSource
import com.example.ui.MainViewModel
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXSectionHeader
import com.example.ui.components.ZXSliderControl
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxBlue
import com.example.ui.theme.ZxBorderDark
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonGreen
import com.example.ui.theme.ZxSurfaceDark
import com.example.ui.theme.ZxSurfaceElevatedDark
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextMutedDark
import com.example.ui.theme.ZxTextSecondaryDark

@Composable
fun IconEditorScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val previewBitmap by viewModel.previewBitmap.collectAsState()
    val shortcutName by viewModel.shortcutName.collectAsState()
    val iconShape by viewModel.iconShape.collectAsState()
    val iconScale by viewModel.iconScale.collectAsState()
    val iconRotation by viewModel.iconRotation.collectAsState()
    val iconOffsetX by viewModel.iconOffsetX.collectAsState()
    val iconOffsetY by viewModel.iconOffsetY.collectAsState()
    val iconPadding by viewModel.iconPadding.collectAsState()
    val backgroundColorInt by viewModel.backgroundColorInt.collectAsState()
    val isTransparentBg by viewModel.isTransparentBg.collectAsState()
    val showBorder by viewModel.showBorder.collectAsState()
    val borderColorInt by viewModel.borderColorInt.collectAsState()
    val editorSource by viewModel.editorSource.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val selectedApp by viewModel.selectedApp.collectAsState()

    // Activity Result Launchers for media & file picking
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setSourceBitmapFromUri(context, uri)
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setSourceBitmapFromUri(context, uri)
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Source, 1: Transform, 2: Shape & Style

    val backgroundColors = listOf(
        AndroidColor.parseColor("#0F1728"), // Obsidian Cyber
        AndroidColor.parseColor("#000000"), // Pure Black
        AndroidColor.parseColor("#00E5FF"), // Neon Cyan
        AndroidColor.parseColor("#0051FF"), // Vivid Blue
        AndroidColor.parseColor("#7928CA"), // Cyber Purple
        AndroidColor.parseColor("#10B981"), // Emerald Matrix
        AndroidColor.parseColor("#F43F5E"), // Neon Crimson
        AndroidColor.parseColor("#1E293B"), // Slate Stealth
        AndroidColor.parseColor("#FFFFFF")  // Pure White
    )

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "ZX ICON STUDIO",
                subtitle = "EDITOR ICON & SHORTCUT",
                showBack = true,
                onBackClick = { viewModel.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { viewModel.resetEditor() },
                        modifier = Modifier.testTag("editor_reset_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Preview Card & Grid Simulation
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
                    Text(
                        text = "LIVE PREVIEW RESOLUSI ASLI (256x256)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ZxCyan
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Launcher Icon Simulation Preview
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isTransparentBg) Color.Transparent else Color(backgroundColorInt)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewBitmap != null) {
                            Image(
                                bitmap = previewBitmap!!.asImageBitmap(),
                                contentDescription = "Preview Icon",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            CircularProgressIndicator(color = ZxCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = shortcutName.ifBlank { "Shortcut Baru" },
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "Target: ${selectedApp?.appName ?: "Aplikasi"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ZxTextSecondaryDark
                    )
                }
            }

            // Shortcut Name Customization Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "NAMA SHORTCUT DI LAUNCHER",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ZxCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = shortcutName,
                    onValueChange = { viewModel.setShortcutName(it) },
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
                        .testTag("editor_shortcut_name_input")
                )
                Text(
                    text = "Bisa dinamai bebas tanpa mengubah nama APK asli (cth: 'Chrome' -> 'ZX Browser')",
                    style = MaterialTheme.typography.labelSmall,
                    color = ZxTextMutedDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Tab Navigation for Editor Tools
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ZxCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ZxCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Sumber") },
                    icon = { Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Transform") },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Bentuk & Warna") },
                    icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Tab 0: Sumber Icon
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ZXSectionHeader(
                        title = "Pilih Sumber Gambar",
                        subtitle = "Gunakan icon bawaan, galeri perangkat, atau preset ZX"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ZXOutlinedButton(
                            text = "Icon Asli",
                            onClick = { viewModel.setSourceType(EditorSource.APP_ICON) },
                            icon = Icons.Default.Apps,
                            modifier = Modifier.weight(1f),
                            borderColor = if (editorSource == EditorSource.APP_ICON) ZxCyan else ZxBorderDark
                        )
                        ZXOutlinedButton(
                            text = "Galeri",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            icon = Icons.Default.AddPhotoAlternate,
                            modifier = Modifier.weight(1f),
                            borderColor = if (editorSource == EditorSource.GALLERY) ZxCyan else ZxBorderDark
                        )
                        ZXOutlinedButton(
                            text = "File",
                            onClick = { filePickerLauncher.launch("image/*") },
                            icon = Icons.Default.Folder,
                            modifier = Modifier.weight(1f),
                            borderColor = ZxBorderDark
                        )
                    }

                    // Preset Icons Carousel
                    Text(
                        text = "PRESET FUTURISTIK ZX",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ZxCyan
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(PresetIcons.list, key = { it.id }) { preset ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ZxSurfaceVariantDark)
                                    .border(1.dp, ZxBorderDark, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setSourcePreset(preset) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = preset.name,
                                    tint = ZxCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tab 1: Transformasi Gambar
            if (selectedTab == 1) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ZXSectionHeader(
                        title = "Transformasi & Posisi",
                        subtitle = "Ubah ukuran, rotasi, padding, dan letak icon"
                    )

                    // Zoom / Scale
                    ZXSliderControl(
                        label = "Zoom / Skala",
                        value = iconScale,
                        onValueChange = { viewModel.setIconScale(it) },
                        valueRange = 0.4f..2.5f,
                        valueDisplay = "${String.format("%.2f", iconScale)}x"
                    )

                    // Rotation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rotasi Sudut",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ZXOutlinedButton(
                            text = "+90°",
                            onClick = { viewModel.rotate90() },
                            icon = Icons.Default.CropRotate,
                            modifier = Modifier.width(100.dp)
                        )
                    }

                    ZXSliderControl(
                        label = "Sudut Rotasi",
                        value = iconRotation,
                        onValueChange = { viewModel.setIconRotation(it) },
                        valueRange = 0f..360f,
                        valueDisplay = "${iconRotation.toInt()}°"
                    )

                    // Padding / Margin
                    ZXSliderControl(
                        label = "Padding Bingkai",
                        value = iconPadding.toFloat(),
                        onValueChange = { viewModel.setIconPadding(it.toInt()) },
                        valueRange = 0f..60f,
                        valueDisplay = "${iconPadding}px"
                    )

                    // Pan Offset X & Y
                    ZXSliderControl(
                        label = "Geser Horizontal (X)",
                        value = iconOffsetX,
                        onValueChange = { viewModel.setIconOffset(it, iconOffsetY) },
                        valueRange = -80f..80f,
                        valueDisplay = "${iconOffsetX.toInt()}px"
                    )

                    ZXSliderControl(
                        label = "Geser Vertikal (Y)",
                        value = iconOffsetY,
                        onValueChange = { viewModel.setIconOffset(iconOffsetX, it) },
                        valueRange = -80f..80f,
                        valueDisplay = "${iconOffsetY.toInt()}px"
                    )
                }
            }

            // Tab 2: Bentuk & Warna Background
            if (selectedTab == 2) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ZXSectionHeader(
                        title = "Bentuk Icon",
                        subtitle = "Pilih bentuk topeng masking icon"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconShape.values().forEach { shape ->
                            FilterChip(
                                selected = iconShape == shape,
                                onClick = { viewModel.setIconShape(shape) },
                                label = { Text(shape.label.split(" ").first()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ZxCyan,
                                    selectedLabelColor = ZxSurfaceDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    ZXSectionHeader(
                        title = "Warna Latar Belakang",
                        subtitle = "Pilih palet futuristik atau transparan"
                    )

                    // Transparent Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Latar Belakang Transparan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isTransparentBg,
                            onCheckedChange = { viewModel.setTransparentBg(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ZxCyan,
                                checkedTrackColor = ZxSurfaceVariantDark
                            )
                        )
                    }

                    if (!isTransparentBg) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(backgroundColors) { c ->
                                val isSelected = backgroundColorInt == c
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(c))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) ZxCyan else ZxBorderDark,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.setBackgroundColor(c) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Terpilih",
                                            tint = if (c == AndroidColor.WHITE) Color.Black else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Border Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Border Garis Neon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = showBorder,
                            onCheckedChange = { viewModel.setBorderSettings(it, borderColorInt) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ZxCyan,
                                checkedTrackColor = ZxSurfaceVariantDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Create Shortcut Button
            ZXButton(
                text = if (isProcessing) "Sedang Memproses..." else "Buat Icon & Pasang Shortcut",
                onClick = { viewModel.createShortcut(context) },
                icon = Icons.Default.AutoAwesome,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("editor_create_shortcut_btn")
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
