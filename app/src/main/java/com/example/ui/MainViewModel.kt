package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.InstalledApp
import com.example.data.model.ShortcutEntity
import com.example.data.repository.AppManagerRepository
import com.example.data.repository.ImportResult
import com.example.data.repository.ShortcutRepository
import com.example.image.DrawableUtils
import com.example.image.IconConfig
import com.example.image.IconProcessor
import com.example.image.IconShape
import com.example.image.PresetIconItem
import com.example.image.PresetIcons
import com.example.settings.PreferencesManager
import com.example.settings.ThemeMode
import com.example.shortcut.ShortcutCreationResult
import com.example.shortcut.ShortcutCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Screen {
    HOME,
    APP_LIST,
    APP_DETAIL,
    ICON_EDITOR,
    SUCCESS,
    SHORTCUTS_MANAGER,
    BACKUP_RESTORE,
    SETTINGS,
    DOWNLOAD_RELEASE
}

enum class SortOrder {
    A_Z,
    Z_A,
    RECENT
}

enum class AppFilter {
    ALL,
    USER,
    SYSTEM
}

enum class EditorSource {
    APP_ICON,
    GALLERY,
    PRESET
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val shortcutRepository = ShortcutRepository(application, db.shortcutDao())
    val appRepository = AppManagerRepository(application)
    val preferencesManager = PreferencesManager(application)
    val updateRepository = com.example.data.repository.UpdateRepository()

    // Update Checker State
    private val _updateCheckResult = MutableStateFlow<com.example.data.repository.UpdateCheckResult?>(null)
    val updateCheckResult: StateFlow<com.example.data.repository.UpdateCheckResult?> = _updateCheckResult.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    // Navigation Stack
    private val screenStack = mutableListOf(Screen.HOME)
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Preferences
    private val _themeMode = MutableStateFlow(preferencesManager.themeMode)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // Shortcuts from Room
    val shortcuts: StateFlow<List<ShortcutEntity>> = shortcutRepository.allShortcuts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Installed Apps
    private val _rawInstalledApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.A_Z)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _appFilter = MutableStateFlow(AppFilter.ALL)
    val appFilter: StateFlow<AppFilter> = _appFilter.asStateFlow()

    // Selection
    private val _selectedApp = MutableStateFlow<InstalledApp?>(null)
    val selectedApp: StateFlow<InstalledApp?> = _selectedApp.asStateFlow()

    // Editor State
    private val _editorSource = MutableStateFlow(EditorSource.APP_ICON)
    val editorSource: StateFlow<EditorSource> = _editorSource.asStateFlow()

    private val _sourceBitmap = MutableStateFlow<Bitmap?>(null)
    val sourceBitmap: StateFlow<Bitmap?> = _sourceBitmap.asStateFlow()

    private val _shortcutName = MutableStateFlow("")
    val shortcutName: StateFlow<String> = _shortcutName.asStateFlow()

    private val _iconShape = MutableStateFlow(IconShape.ROUNDED)
    val iconShape: StateFlow<IconShape> = _iconShape.asStateFlow()

    private val _iconScale = MutableStateFlow(1.0f)
    val iconScale: StateFlow<Float> = _iconScale.asStateFlow()

    private val _iconRotation = MutableStateFlow(0f)
    val iconRotation: StateFlow<Float> = _iconRotation.asStateFlow()

    private val _iconOffsetX = MutableStateFlow(0f)
    val iconOffsetX: StateFlow<Float> = _iconOffsetX.asStateFlow()

    private val _iconOffsetY = MutableStateFlow(0f)
    val iconOffsetY: StateFlow<Float> = _iconOffsetY.asStateFlow()

    private val _iconPadding = MutableStateFlow(16)
    val iconPadding: StateFlow<Int> = _iconPadding.asStateFlow()

    private val _backgroundColorInt = MutableStateFlow(Color.parseColor("#0F1728"))
    val backgroundColorInt: StateFlow<Int> = _backgroundColorInt.asStateFlow()

    private val _isTransparentBg = MutableStateFlow(false)
    val isTransparentBg: StateFlow<Boolean> = _isTransparentBg.asStateFlow()

    private val _showBorder = MutableStateFlow(true)
    val showBorder: StateFlow<Boolean> = _showBorder.asStateFlow()

    private val _borderColorInt = MutableStateFlow(Color.parseColor("#00E5FF"))
    val borderColorInt: StateFlow<Int> = _borderColorInt.asStateFlow()

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap.asStateFlow()

    private val _selectedPreset = MutableStateFlow<PresetIconItem?>(null)
    val selectedPreset: StateFlow<PresetIconItem?> = _selectedPreset.asStateFlow()

    // Processing & Success State
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _lastCreatedShortcut = MutableStateFlow<ShortcutEntity?>(null)
    val lastCreatedShortcut: StateFlow<ShortcutEntity?> = _lastCreatedShortcut.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadInstalledApps(false)
    }

    // Navigation Handlers
    fun navigateTo(screen: Screen) {
        if (screen == Screen.HOME) {
            screenStack.clear()
            screenStack.add(Screen.HOME)
        } else {
            screenStack.add(screen)
        }
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun loadInstalledApps(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoadingApps.value = true
            try {
                val list = appRepository.getInstalledApps(forceRefresh)
                _rawInstalledApps.value = list
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat aplikasi: ${e.localizedMessage}"
            } finally {
                _isLoadingApps.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setAppFilter(filter: AppFilter) {
        _appFilter.value = filter
    }

    fun getFilteredApps(): List<InstalledApp> {
        val query = _searchQuery.value.trim().lowercase()
        val filter = _appFilter.value
        val list = _rawInstalledApps.value.filter { app ->
            val matchesFilter = when (filter) {
                AppFilter.ALL -> true
                AppFilter.USER -> !app.isSystemApp
                AppFilter.SYSTEM -> app.isSystemApp
            }
            val matchesQuery = query.isEmpty() ||
                    app.appName.lowercase().contains(query) ||
                    app.packageName.lowercase().contains(query)
            matchesFilter && matchesQuery
        }

        return when (_sortOrder.value) {
            SortOrder.A_Z -> list.sortedBy { it.appName.lowercase() }
            SortOrder.Z_A -> list.sortedByDescending { it.appName.lowercase() }
            SortOrder.RECENT -> list.sortedByDescending { it.lastUpdateTime }
        }
    }

    fun selectApp(app: InstalledApp) {
        _selectedApp.value = app
        navigateTo(Screen.APP_DETAIL)
    }

    fun startEditorForSelectedApp(app: InstalledApp? = null) {
        val target = app ?: _selectedApp.value ?: return
        _selectedApp.value = target
        _shortcutName.value = target.appName

        // Apply defaults from preferences
        val defaultShapeName = preferencesManager.defaultIconShape
        _iconShape.value = try {
            IconShape.valueOf(defaultShapeName)
        } catch (e: Exception) {
            IconShape.ROUNDED
        }
        _iconPadding.value = preferencesManager.defaultPadding
        _iconScale.value = 1.0f
        _iconRotation.value = 0f
        _iconOffsetX.value = 0f
        _iconOffsetY.value = 0f
        _isTransparentBg.value = false
        _backgroundColorInt.value = Color.parseColor("#0F1728")
        _showBorder.value = true
        _borderColorInt.value = Color.parseColor("#00E5FF")
        _editorSource.value = EditorSource.APP_ICON

        // Load original app icon
        viewModelScope.launch {
            _isProcessing.value = true
            val bitmap = appRepository.getAppIconBitmap(target.packageName)
            _sourceBitmap.value = bitmap
            updatePreview()
            _isProcessing.value = false
            navigateTo(Screen.ICON_EDITOR)
        }
    }

    fun setSourceType(source: EditorSource) {
        _editorSource.value = source
        if (source == EditorSource.APP_ICON) {
            val app = _selectedApp.value
            if (app != null) {
                viewModelScope.launch {
                    val bitmap = appRepository.getAppIconBitmap(app.packageName)
                    _sourceBitmap.value = bitmap
                    updatePreview()
                }
            }
        }
    }

    fun setSourceBitmapFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    val square = DrawableUtils.drawableToBitmap(
                        android.graphics.drawable.BitmapDrawable(context.resources, bitmap),
                        256
                    )
                    _sourceBitmap.value = square
                    _editorSource.value = EditorSource.GALLERY
                    withContext(Dispatchers.Main) {
                        updatePreview()
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat gambar: ${e.localizedMessage}"
            }
        }
    }

    fun setSourcePreset(preset: PresetIconItem) {
        _selectedPreset.value = preset
        _editorSource.value = EditorSource.PRESET
        val monogramBmp = PresetIcons.createMonogramBitmap(
            preset.name.take(2),
            Color.WHITE,
            Color.TRANSPARENT,
            256
        )
        _sourceBitmap.value = monogramBmp
        updatePreview()
    }

    fun setShortcutName(name: String) {
        _shortcutName.value = name
    }

    fun setIconShape(shape: IconShape) {
        _iconShape.value = shape
        updatePreview()
    }

    fun setIconScale(scale: Float) {
        _iconScale.value = scale
        updatePreview()
    }

    fun setIconRotation(rotation: Float) {
        _iconRotation.value = rotation
        updatePreview()
    }

    fun rotate90() {
        _iconRotation.value = (_iconRotation.value + 90f) % 360f
        updatePreview()
    }

    fun setIconOffset(x: Float, y: Float) {
        _iconOffsetX.value = x
        _iconOffsetY.value = y
        updatePreview()
    }

    fun setIconPadding(padding: Int) {
        _iconPadding.value = padding
        updatePreview()
    }

    fun setBackgroundColor(colorInt: Int) {
        _backgroundColorInt.value = colorInt
        _isTransparentBg.value = false
        updatePreview()
    }

    fun setTransparentBg(isTransparent: Boolean) {
        _isTransparentBg.value = isTransparent
        updatePreview()
    }

    fun setBorderSettings(show: Boolean, colorInt: Int) {
        _showBorder.value = show
        _borderColorInt.value = colorInt
        updatePreview()
    }

    fun resetEditor() {
        val app = _selectedApp.value ?: return
        _shortcutName.value = app.appName
        _iconShape.value = IconShape.ROUNDED
        _iconScale.value = 1.0f
        _iconRotation.value = 0f
        _iconOffsetX.value = 0f
        _iconOffsetY.value = 0f
        _iconPadding.value = 16
        _backgroundColorInt.value = Color.parseColor("#0F1728")
        _isTransparentBg.value = false
        _showBorder.value = true
        _borderColorInt.value = Color.parseColor("#00E5FF")
        updatePreview()
    }

    fun updatePreview() {
        val src = _sourceBitmap.value ?: return
        val config = IconConfig(
            shape = _iconShape.value,
            scale = _iconScale.value,
            rotation = _iconRotation.value,
            offsetX = _iconOffsetX.value,
            offsetY = _iconOffsetY.value,
            padding = _iconPadding.value,
            backgroundColor = _backgroundColorInt.value,
            isTransparentBg = _isTransparentBg.value,
            showBorder = _showBorder.value,
            borderColor = _borderColorInt.value
        )
        val rendered = IconProcessor.processIcon(src, config, 256)
        _previewBitmap.value = rendered
    }

    fun createShortcut(context: Context) {
        val app = _selectedApp.value ?: run {
            _errorMessage.value = "Target aplikasi belum dipilih"
            return
        }
        val name = _shortcutName.value.trim().ifEmpty { app.appName }
        val finalBitmap = _previewBitmap.value ?: run {
            _errorMessage.value = "Icon belum siap dibuat"
            return
        }

        viewModelScope.launch {
            _isProcessing.value = true

            // 1. Save to Room database and local storage
            val bgHex = String.format("#%08X", _backgroundColorInt.value)
            val savedEntity = shortcutRepository.saveShortcut(
                targetPackage = app.packageName,
                targetActivity = app.targetActivity,
                targetAppName = app.appName,
                shortcutName = name,
                iconBitmap = finalBitmap,
                iconShape = _iconShape.value.name,
                iconScale = _iconScale.value,
                iconRotation = _iconRotation.value,
                iconOffsetX = _iconOffsetX.value,
                iconOffsetY = _iconOffsetY.value,
                iconPadding = _iconPadding.value,
                backgroundColorHex = bgHex,
                isTransparentBg = _isTransparentBg.value
            )

            // 2. Request pinned shortcut in Launcher via ShortcutManagerCompat
            val result = ShortcutCreator.createShortcut(
                context = context,
                shortcutId = savedEntity.shortcutId,
                label = name,
                targetPackage = app.packageName,
                targetActivity = app.targetActivity,
                iconBitmap = finalBitmap
            )

            _isProcessing.value = false

            when (result) {
                is ShortcutCreationResult.Success -> {
                    _lastCreatedShortcut.value = savedEntity
                    _statusMessage.value = result.message
                    navigateTo(Screen.SUCCESS)
                }
                is ShortcutCreationResult.UnsupportedLauncher -> {
                    _errorMessage.value = result.message
                    _lastCreatedShortcut.value = savedEntity
                    navigateTo(Screen.SUCCESS)
                }
                is ShortcutCreationResult.Failure -> {
                    _errorMessage.value = result.error
                }
            }
        }
    }

    fun pinExistingShortcut(context: Context, shortcut: ShortcutEntity) {
        viewModelScope.launch {
            val bitmap = shortcutRepository.loadShortcutIconBitmap(shortcut)
            if (bitmap != null) {
                val result = ShortcutCreator.createShortcut(
                    context = context,
                    shortcutId = shortcut.shortcutId,
                    label = shortcut.shortcutName,
                    targetPackage = shortcut.targetPackage,
                    targetActivity = shortcut.targetActivity,
                    iconBitmap = bitmap
                )
                when (result) {
                    is ShortcutCreationResult.Success -> {
                        _statusMessage.value = "Shortcut '${shortcut.shortcutName}' disematkan ulang ke launcher"
                    }
                    else -> {
                        _errorMessage.value = "Launcher menolak permintaan pin shortcut"
                    }
                }
            } else {
                _errorMessage.value = "File icon shortcut tidak ditemukan"
            }
        }
    }

    fun deleteShortcut(shortcut: ShortcutEntity) {
        viewModelScope.launch {
            shortcutRepository.deleteShortcut(shortcut)
            _statusMessage.value = "Shortcut '${shortcut.shortcutName}' dihapus dari daftar"
        }
    }

    fun launchTargetApp(context: Context, packageName: String, activityName: String? = null) {
        try {
            val intent = if (!activityName.isNullOrEmpty()) {
                Intent(Intent.ACTION_MAIN).apply {
                    component = android.content.ComponentName(packageName, activityName)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            } else {
                context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            }

            if (intent != null) {
                context.startActivity(intent)
            } else {
                _errorMessage.value = "Aplikasi tidak dapat dibuka atau tidak memiliki antarmuka utama"
            }
        } catch (e: Exception) {
            _errorMessage.value = "Gagal membuka aplikasi: ${e.localizedMessage}"
        }
    }

    fun openAppDetailsSettings(context: Context, packageName: String) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            _errorMessage.value = "Gagal membuka detail aplikasi: ${e.localizedMessage}"
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        preferencesManager.themeMode = mode
        _themeMode.value = mode
    }

    fun setDefaultIconShape(shape: String) {
        preferencesManager.defaultIconShape = shape
    }

    fun setDefaultPadding(padding: Int) {
        preferencesManager.defaultPadding = padding
    }

    fun setConfirmBeforeDelete(confirm: Boolean) {
        preferencesManager.confirmBeforeDelete = confirm
    }

    fun resetAllSettings() {
        preferencesManager.resetToDefaults()
        _themeMode.value = preferencesManager.themeMode
        _statusMessage.value = "Pengaturan dikembalikan ke setelan pabrik ZX"
    }

    fun clearMessages() {
        _statusMessage.value = null
        _errorMessage.value = null
    }

    fun checkForUpdates(currentVersion: String = "1.0.0") {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            _updateCheckResult.value = null
            val result = updateRepository.checkForUpdates(currentVersion)
            _updateCheckResult.value = result
            _isCheckingUpdate.value = false
        }
    }

    fun clearUpdateCheckResult() {
        _updateCheckResult.value = null
    }
}
