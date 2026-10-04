package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.settings.ThemeMode
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.AppDetailScreen
import com.example.ui.screens.AppListScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IconEditorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShortcutsManagerScreen
import com.example.ui.screens.SuccessScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val themeMode by mainViewModel.themeMode.collectAsState()

            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                MainAppContent(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen(viewModel = viewModel)
                Screen.APP_LIST -> AppListScreen(viewModel = viewModel)
                Screen.APP_DETAIL -> AppDetailScreen(viewModel = viewModel)
                Screen.ICON_EDITOR -> IconEditorScreen(viewModel = viewModel)
                Screen.SUCCESS -> SuccessScreen(viewModel = viewModel)
                Screen.SHORTCUTS_MANAGER -> ShortcutsManagerScreen(viewModel = viewModel)
                Screen.BACKUP_RESTORE -> BackupRestoreScreen(viewModel = viewModel)
                Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                Screen.DOWNLOAD_RELEASE -> com.example.ui.screens.DownloadReleaseScreen(viewModel = viewModel)
            }
        }
    }
}
