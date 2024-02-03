package com.sample.feature.window

import android.content.Context
import android.content.Intent

data class WindowParams(
    val fullScreenMode: FullScreenMode = FullScreenMode.IMMERSIVE,
    val cutoutMode: CutoutMode = CutoutMode.DEFAULT,
    val decorFitsSystemWindows: Boolean = true,
    val addPaddingSafeArea: Boolean = false,
    val hideStatusBar: Boolean = false,
    val hideNavigationBar: Boolean = false,
    val isLightStatusBar: Boolean = true,
    val isLightNavigationBar: Boolean = true,
) {
    constructor(intent: Intent?) : this(
        getFullScreenMode(intent),
        getCutoutMode(intent),
        getDecorFitSystemWindows(intent),
        getAddPaddingSafeArea(intent),
        getHideStatusBar(intent),
        getHideNavigationBar(intent),
        isLightStatusBar(intent),
        isLightNavigationBar(intent),
    )

    companion object {
        private const val KEY_FULL_SCREEN_MODE = "Key.FullScreenMode"
        private const val KEY_CUTOUT_MODE = "Key.CutoutMode"
        private const val KEY_DECOR_FIT_SYSTEM_WINDOWS = "Key.DecorFitSystemWindows"
        private const val KEY_ADD_PADDING_SAFE_AREA = "Key.AddPaddingSafeArea"
        private const val KEY_HIDE_STATUS_BAR = "Key.HideStatusBar"
        private const val KEY_HIDE_NAVIGATION_BAR = "Key.HideNavigationBar"
        private const val KEY_IS_LIGHT_STATUS_BAR = "Key.IsLightStatusBar"
        private const val KEY_IS_LIGHT_NAVIGATION_BAR = "Key.IsLightNavigationBar"

        fun getFullScreenMode(intent: Intent?): FullScreenMode {
            val name = intent?.getStringExtra(KEY_FULL_SCREEN_MODE) ?: return FullScreenMode.NONE
            return runCatching {
                FullScreenMode.valueOf(name)
            }.getOrDefault(FullScreenMode.NONE)
        }

        fun getCutoutMode(intent: Intent?): CutoutMode {
            val name = intent?.getStringExtra(KEY_CUTOUT_MODE) ?: return CutoutMode.DEFAULT
            return runCatching {
                CutoutMode.valueOf(name)
            }.getOrDefault(CutoutMode.DEFAULT)
        }

        fun getDecorFitSystemWindows(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(KEY_DECOR_FIT_SYSTEM_WINDOWS, true) ?: true
        }

        fun getAddPaddingSafeArea(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(KEY_ADD_PADDING_SAFE_AREA, false) ?: false
        }

        fun getHideStatusBar(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(KEY_HIDE_STATUS_BAR, false) ?: false
        }

        fun getHideNavigationBar(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(KEY_HIDE_NAVIGATION_BAR, false) ?: false
        }

        fun isLightStatusBar(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(KEY_IS_LIGHT_STATUS_BAR, true) ?: true
        }

        fun isLightNavigationBar(intent: Intent?): Boolean {
            return intent?.getBooleanExtra(KEY_IS_LIGHT_NAVIGATION_BAR, true) ?: true
        }
    }

    fun buildIntent(context: Context): Intent {
        return Intent(context, WindowSampleActivity::class.java).apply {
            putExtra(KEY_FULL_SCREEN_MODE, fullScreenMode.name)
            putExtra(KEY_CUTOUT_MODE, cutoutMode.name)
            putExtra(KEY_DECOR_FIT_SYSTEM_WINDOWS, decorFitsSystemWindows)
            putExtra(KEY_ADD_PADDING_SAFE_AREA, addPaddingSafeArea)
            putExtra(KEY_HIDE_STATUS_BAR, hideStatusBar)
            putExtra(KEY_HIDE_NAVIGATION_BAR, hideNavigationBar)
            putExtra(KEY_IS_LIGHT_STATUS_BAR, isLightStatusBar)
            putExtra(KEY_IS_LIGHT_NAVIGATION_BAR, isLightNavigationBar)
        }
    }
}

enum class FullScreenMode {
    LEAN_BACK,
    IMMERSIVE,
    STICKY_IMMERSIVE,
    NONE;
}

enum class CutoutMode {
    DEFAULT,
    SHORT_EDGES,
    ALWAYS,
    NEVER;
}