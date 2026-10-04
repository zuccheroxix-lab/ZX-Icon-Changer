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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.ZXButton
import com.example.ui.components.ZXCard
import com.example.ui.components.ZXLimitationBanner
import com.example.ui.components.ZXOutlinedButton
import com.example.ui.components.ZXTopBar
import com.example.ui.theme.ZxCyan
import com.example.ui.theme.ZxNeonGreen
import com.example.ui.theme.ZxSurfaceVariantDark
import com.example.ui.theme.ZxTextSecondaryDark

@Composable
fun SuccessScreen(viewModel: MainViewModel) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val context = LocalContext.current
    val shortcut = viewModel.lastCreatedShortcut.collectAsState().value
    val statusMessage = viewModel.statusMessage.collectAsState().value

    val iconBitmap by produceState<Bitmap?>(initialValue = null, key1 = shortcut?.iconFileName) {
        if (shortcut != null) {
            value = viewModel.shortcutRepository.loadShortcutIconBitmap(shortcut)
        }
    }

    Scaffold(
        topBar = {
            ZXTopBar(
                title = "BERHASIL DIBUAT",
                subtitle = "ZX SHORTCUT STATUS",
                showBack = false
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(ZxNeonGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Sukses",
                    tint = ZxNeonGreen,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Icon Berhasil Didaftarkan!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = statusMessage ?: "Permintaan penambahan shortcut telah dikirimkan ke Launcher Android Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = ZxTextSecondaryDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Result Preview Card
            if (shortcut != null) {
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
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(ZxSurfaceVariantDark),
                            contentAlignment = Alignment.Center
                        ) {
                            if (iconBitmap != null) {
                                Image(
                                    bitmap = iconBitmap!!.asImageBitmap(),
                                    contentDescription = shortcut.shortcutName,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = shortcut.shortcutName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Target: ${shortcut.targetAppName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ZxCyan
                        )
                        Text(
                            text = shortcut.targetPackage,
                            style = MaterialTheme.typography.labelSmall,
                            color = ZxTextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ZXLimitationBanner()

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (shortcut != null) {
                    ZXButton(
                        text = "Uji Buka Target Sekarang",
                        onClick = {
                            viewModel.launchTargetApp(context, shortcut.targetPackage, shortcut.targetActivity)
                        },
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("success_test_launch_btn")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ZXOutlinedButton(
                        text = "Buat Lagi",
                        onClick = { viewModel.navigateTo(Screen.APP_LIST) },
                        icon = Icons.Default.Add,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("success_create_another_btn")
                    )

                    ZXOutlinedButton(
                        text = "Selesai",
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        icon = Icons.Default.Home,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("success_done_btn")
                    )
                }
            }
        }
    }
}
