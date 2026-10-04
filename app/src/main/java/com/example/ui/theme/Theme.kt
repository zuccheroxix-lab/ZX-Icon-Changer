package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZxDarkColorScheme = darkColorScheme(
    primary = ZxCyan,
    onPrimary = Color(0xFF03101C),
    primaryContainer = Color(0xFF003847),
    onPrimaryContainer = ZxCyanBright,
    secondary = ZxBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF002F6C),
    onSecondaryContainer = Color(0xFFB8D8FF),
    tertiary = ZxPurple,
    onTertiary = Color.White,
    background = ZxBackgroundDark,
    onBackground = ZxTextPrimaryDark,
    surface = ZxSurfaceDark,
    onSurface = ZxTextPrimaryDark,
    surfaceVariant = ZxSurfaceVariantDark,
    onSurfaceVariant = ZxTextSecondaryDark,
    outline = ZxBorderDark,
    error = ZxNeonRed,
    onError = Color.White
)

private val ZxLightColorScheme = lightColorScheme(
    primary = ZxDeepBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001A42),
    secondary = ZxCyan,
    onSecondary = Color(0xFF03101C),
    background = ZxBackgroundLight,
    onBackground = ZxTextPrimaryLight,
    surface = ZxSurfaceLight,
    onSurface = ZxTextPrimaryLight,
    surfaceVariant = ZxSurfaceVariantLight,
    onSurfaceVariant = ZxTextSecondaryLight,
    outline = ZxBorderLight,
    error = ZxNeonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature ZX brand colors for futuristic identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZxDarkColorScheme else ZxLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
