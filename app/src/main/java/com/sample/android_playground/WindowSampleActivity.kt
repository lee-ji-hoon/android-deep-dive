package com.sample.android_playground

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import com.sample.android_playground.databinding.ActivityWindowSampleBinding
import com.sample.android_playground.window.CutoutMode
import com.sample.android_playground.window.FullScreenMode
import com.sample.android_playground.window.WindowParams
import kotlin.math.max
import com.google.android.material.R as MaterialR

class WindowSampleActivity : AppCompatActivity() {

    private val binding by lazy { ActivityWindowSampleBinding.inflate(layoutInflater) }

    private var systemBarsBehavior = 0
    private var insetsType = 0

    private var systemModeFlag = View.SYSTEM_UI_FLAG_VISIBLE
    private var fitSystemFlag = 0
    private var statusBarFlag = 0
    private var navigationBarFlag = 0
    private var lightStatusBarFlag = 0
    private var lightNavigationBarFlag = 0

    private val windowFlag
        get() = systemModeFlag or
                fitSystemFlag or
                statusBarFlag or
                navigationBarFlag or
                lightStatusBarFlag or
                lightNavigationBarFlag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val param = WindowParams(intent)
        setUpInsetsType()
        setUpCutoutMode(param.cutoutMode)
        setUpDecorFitSystemWindows(param.decorFitsSystemWindows)
        setUpAddPaddingSafeArea(param.decorFitsSystemWindows, param.addPaddingSafeArea)
        setUpHideStatusBar(param.hideStatusBar)
        setUpHideNavigationBar(param.hideNavigationBar)
        setUpIsLightStatusBar(param.isLightStatusBar)
        setUpIsLightNavigationBar(param.isLightNavigationBar)
        setUpFullScreenMode(param.fullScreenMode)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun setUpInsetsType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            systemBarsBehavior =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    WindowInsetsController.BEHAVIOR_DEFAULT
                } else {
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH
                }

            insetsType = WindowInsets.Type.captionBar()
        }
    }

    private fun setUpFullScreenMode(fullScreenMode: FullScreenMode) {
        binding.fullscreenModeLeanback.isEnabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.R

        binding.fullscreenMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.fullscreen_mode_leanback -> enableFullscreenModeLeanBack()
                R.id.fullscreen_mode_immersive -> enableFullscreenModeImmersive()
                R.id.fullscreen_mode_sticky_immersive -> enableFullscreenModeStickyImmersive()
                R.id.fullscreen_mode_none -> enableFullscreenModeNone()
            }
        }
        val fullScreenModeId = when (fullScreenMode) {
            FullScreenMode.LEAN_BACK -> R.id.fullscreen_mode_leanback
            FullScreenMode.IMMERSIVE -> R.id.fullscreen_mode_immersive
            FullScreenMode.STICKY_IMMERSIVE -> R.id.fullscreen_mode_sticky_immersive
            FullScreenMode.NONE -> R.id.fullscreen_mode_none
        }
        binding.fullscreenMode.check(fullScreenModeId)
    }

    private fun setUpCutoutMode(cutoutMode: CutoutMode) {
        binding.cutoutModeDefault.isEnabled = isVersionAtLeastP()
        binding.cutoutModeShortEdges.isEnabled = isVersionAtLeastP()
        binding.cutoutModeAlways.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        binding.cutoutModeNever.isEnabled = isVersionAtLeastP()

        if (isVersionAtLeastP()) {
            when (cutoutMode) {
                CutoutMode.DEFAULT -> enableCutoutModeDefault()
                CutoutMode.SHORT_EDGES -> enableCutoutModeShortEdges()
                CutoutMode.ALWAYS -> enableCutoutModeAlways()
                CutoutMode.NEVER -> enableCutoutModeNever()
            }

            // cutout 은 check 이벤트 태우면 Activity Restart 하기 때문에 값 설정 후 이벤트 설정
            val cutoutModeId = when (cutoutMode) {
                CutoutMode.DEFAULT -> R.id.cutout_mode_default
                CutoutMode.SHORT_EDGES -> R.id.cutout_mode_short_edges
                CutoutMode.ALWAYS -> R.id.cutout_mode_always
                CutoutMode.NEVER -> R.id.cutout_mode_never
            }
            binding.cutoutMode.check(cutoutModeId)
            binding.cutoutMode.setOnCheckedChangeListener { _, _ ->
                restartWindowActivity()
            }
        }
    }

    /**
     * SYSTEM_UI_FLAG_LAYOUT_STABLE
     * - SafeArea 영역을 유지하여 전체화면을 만듬
     * - SafeArea 만큼의 Padding 이 항상 있음
     *
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     * - Status Bar 의 전체화면이였을 때의 영역을 유지함
     *
     * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
     * - Navigation Bar 의 전체화면이였을 때의 영역을 유지함
     * - "이름과 다르게 Status Bar 에 영향을 줌" <- 버그인듯 Deprecated 되서 잘못 동작 하는 것 일수도
     */
    private fun setUpDecorFitSystemWindows(fit: Boolean) {
        enableDecorFitSystemWindows(fit)
        binding.fullscreenSettingDecorFitsSystemWindows.isChecked = fit
        binding.fullscreenSettingDecorFitsSystemWindows.setOnCheckedChangeListener { _, isChecked ->
            enableDecorFitSystemWindows(isChecked)
        }
    }

    /**
     * CutoutMode 가 Default, Never 일때는 Cutout 정보를 가져올 수 없다
     * Short Edges, Always 일때 Cutout 정보를 가지고 와서 Padding 을 추가한다
     * 만약 decorFitsSystemWindows 가 true 라면 이미 insets 이 적용된 상태라 Padding 을 따로 추가할 필요가 없다
     */
    private fun enableAddPaddingSafeAreaCheckBox(
        decorFitSystemWindows: Boolean,
    ) {
        binding.cutoutSettingAddPaddingSafeArea.isEnabled = !decorFitSystemWindows && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    private fun setUpAddPaddingSafeArea(
        decorFitSystemWindows: Boolean,
        addPadding: Boolean,
    ) {
        enableAddPaddingSafeAreaCheckBox(decorFitSystemWindows)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableAddPaddingSafeArea(addPadding)
            binding.cutoutSettingAddPaddingSafeArea.isChecked = addPadding
            binding.cutoutSettingAddPaddingSafeArea.setOnCheckedChangeListener { _, isChecked ->
                enableAddPaddingSafeArea(isChecked)
            }
        }
    }

    private fun setUpHideStatusBar(hide: Boolean) {
        enableHideStatusBar(hide)
        binding.fullscreenSettingHideStatusBar.isChecked = hide
        binding.fullscreenSettingHideStatusBar.setOnCheckedChangeListener { _, isChecked ->
            enableHideStatusBar(isChecked)
        }
    }

    private fun setUpHideNavigationBar(hide: Boolean) {
        enableHideNavigationBar(hide)
        binding.fullscreenSettingHideNavigationBar.isChecked = hide
        binding.fullscreenSettingHideNavigationBar.setOnCheckedChangeListener { _, isChecked ->
            enableHideNavigationBar(isChecked)
        }
    }

    private fun setUpIsLightStatusBar(isLight: Boolean) {
        binding.statusBarMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.status_bar_mode_light -> enableLightStatusBarMode(true)
                R.id.status_bar_mode_dark -> enableLightStatusBarMode(false)
            }
        }
        val statusBarModeId = when (isLight) {
            true -> R.id.status_bar_mode_light
            false -> R.id.status_bar_mode_dark
        }
        binding.statusBarMode.check(statusBarModeId)
    }

    private fun setUpIsLightNavigationBar(isLight: Boolean) {
        binding.navigationBarMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.navigation_bar_mode_light -> enableLightNavigationMode(true)
                R.id.navigation_bar_mode_dark -> enableLightNavigationMode(false)
            }
        }
        val navigationModeId = when (isLight) {
            true -> R.id.navigation_bar_mode_light
            false -> R.id.navigation_bar_mode_dark
        }
        binding.navigationBarMode.check(navigationModeId)
    }

    private fun resetFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            /**
             * 시작시 window.insetsController?.systemBarsBehavior 값은 0 으로 시작
             * WindowInsetsController.BEHAVIOR_DEFAULT = 1 이고
             * WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH = 0 인데
             * Android 12 (S - 31) 이상에서는 두 값이 동일하게 동작한다
             */
            window.insetsController?.systemBarsBehavior =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    WindowInsetsController.BEHAVIOR_DEFAULT
                } else {
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH
                }
            if (getFullScreenMode() == FullScreenMode.NONE) {
                window.insetsController?.show(WindowInsets.Type.systemBars())
            } else {
                window.insetsController?.hide(WindowInsets.Type.systemBars())
            }
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or
                    lightStatusBarFlag or
                    lightNavigationBarFlag
        }
    }

    private fun refreshFullScreenMode() {
        resetFullScreenMode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(insetsType)
            enableAddPaddingSafeArea(binding.cutoutSettingAddPaddingSafeArea.isChecked)
        } else {
            window.decorView.postDelayed({
                window.decorView.systemUiVisibility = windowFlag
            }, 100)
        }
    }

    /**
     * Android 11 (R - 30) 부터는
     * systemUiVisibility 가 deprecated 되서
     * WindowInsetController 를 이용하는게 권장
     *
     * Window Inset 을 처리하기 위한 4가지 InsetsType
     *
     * statusBars
     * - 상태 바
     * navigationBars
     * - 네비게이션 바
     * captionBar
     * - Android 10 (Q - 29) 에서 추가된 접근성 자막 기능이 추가 되었고
     * - Android 11 (R - 30) 부터는 Window Insets 을 통해 자막 영역을 구할 수 있음
     * ime (Input Method Editor)
     * - 키보드 입력 영역
     */

    /**
     * LeanBack
     * 아무화면이나 탭을 하여 전체화면을 종료하는 모드
     *
     * https://developer.android.com/about/versions/12/features?hl=ko#immersive-mode-improvements
     * Android 12 (S - 31) 에서
     * WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH 가 deprecated 되어
     * LeanBack 모드가 사라짐
     *
     * Android 11 (R - 30) 이상
     * - WindowInsetsController.BEHAVIOR_DEFAULT 로 동작 SystemBar 를 Swipe 하면 다시 노출
     * Android 11 (R - 30) 미만
     * - View.SYSTEM_UI_FLAG_FULLSCREEN 설정시 화면 아무곳이나 touch 하면 SystemBar 다시 노출
     *
     * View.SYSTEM_UI_FLAG_FULLSCREEN
     * - 풀 스크린 모드
     * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
     * - 하단 네비게이션 숨기기
     */
    @SuppressLint("WrongConstant")
    private fun enableFullscreenModeLeanBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH
            }
        } else {
            systemModeFlag = 0
        }
        disableSystemBarSetting()
        refreshFullScreenMode()
    }

    /**
     * Immersive Mode
     * System Bar 를 Swipe 하여 전체화면을 종료할 수 있는 모드
     * Android 11 (R - 30) 이상
     * - WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE 로 설정할 수 있다
     * Android 11 (R - 30) 미만
     * - View.SYSTEM_UI_FLAG_IMMERSIVE 로 설정
     */
    @SuppressLint("WrongConstant")
    private fun enableFullscreenModeImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            systemBarsBehavior =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    WindowInsetsController.BEHAVIOR_DEFAULT
                } else {
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
                }
        } else {
            systemModeFlag = View.SYSTEM_UI_FLAG_IMMERSIVE
        }
        disableSystemBarSetting()
        refreshFullScreenMode()
    }

    /**
     * Sticky Immersive Mode
     * System Bar 를 Swipe 하여 System Bar 를 볼 수 있고 시간이 지나면 다시 전체화면 모드로 돌아온다
     * 그렇기 떄문에 System Bar 가 반투명하게 유지된다
     * Android 11 (R - 30) 이상
     * - WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE 로 설정할 수 있다
     * Android 11 (R - 30) 미만
     * - View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY 로 설정
     */
    private fun enableFullscreenModeStickyImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            systemModeFlag = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        disableSystemBarSetting()
        refreshFullScreenMode()
    }

    private fun enableFullscreenModeNone() {
        enableSystemBarSetting()
        resetFullScreenMode()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsType = WindowInsets.Type.systemBars().inv()
        } else {
            statusBarFlag = View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            navigationBarFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
        }
    }

    private fun enableSystemBarSetting() {
        binding.fullscreenSettingHideNavigationBar.isEnabled = true
        binding.fullscreenSettingHideStatusBar.isEnabled = true
    }

    private fun disableSystemBarSetting() {
        binding.fullscreenSettingHideNavigationBar.isEnabled = false
        binding.fullscreenSettingHideStatusBar.isEnabled = false
        binding.fullscreenSettingHideNavigationBar.isChecked = false
        binding.fullscreenSettingHideStatusBar.isChecked = false
    }

    /**
     * Cutout 은 기기 전면에 중요한 센서를 위한 공간
     * Android 9 (P - 28) 이상인 경우 Display Cutout 지원
     * - 기본적으로 StatusBar 높이는 Cutout 높이까지 확장되어야 한다
     * - 전체화면 또는 가로모드에서는 Cutout 영역이 레터박스 (검은 영역) 처리되어야 한다
     */

    private fun getFullScreenMode(): FullScreenMode {
        return when {
            binding.fullscreenModeLeanback.isChecked -> FullScreenMode.LEAN_BACK
            binding.fullscreenModeImmersive.isChecked -> FullScreenMode.IMMERSIVE
            binding.fullscreenModeStickyImmersive.isChecked -> FullScreenMode.STICKY_IMMERSIVE
            else -> FullScreenMode.NONE
        }
    }

    private fun getCutoutMode(): CutoutMode {
        return when {
            binding.cutoutModeShortEdges.isChecked -> CutoutMode.SHORT_EDGES
            binding.cutoutModeAlways.isChecked -> CutoutMode.ALWAYS
            binding.cutoutModeNever.isChecked -> CutoutMode.NEVER
            else -> CutoutMode.DEFAULT
        }
    }

    /**
     * Cutout 은 처음 실행할때 설정되면 그 후에는 설정해도 값이 바뀌지 않는다
     * 그래서 Activity 를 재실행하는 방법으로 테스트
     */
    private fun restartWindowActivity() {
        val param = WindowParams(
            fullScreenMode = getFullScreenMode(),
            cutoutMode = getCutoutMode(),
            decorFitsSystemWindows = binding.fullscreenSettingDecorFitsSystemWindows.isChecked,
            addPaddingSafeArea = binding.cutoutSettingAddPaddingSafeArea.isChecked,
            hideStatusBar = binding.fullscreenSettingHideStatusBar.isChecked,
            hideNavigationBar = binding.fullscreenSettingHideNavigationBar.isChecked,
            isLightStatusBar = binding.statusBarModeLight.isChecked,
            isLightNavigationBar = binding.navigationBarModeLight.isChecked,
        )
        val intent = param.buildIntent(this)
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
        startActivity(intent, options.toBundle())
    }

    /**
     * WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
     * - Default 값
     * - 세로 모드에서는 Content 가 Cutout 영역으로 렌더링
     * - "가로 모드"에서는 레터박스 처리 된다
     */
    private fun enableCutoutModeDefault() {
        if (isVersionAtLeastP()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        }
    }

    /**
     * WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
     * - 짧은 가장자리에 Cutout 이 있으면 렌더링을 한다
     */
    private fun enableCutoutModeShortEdges() {
        if (isVersionAtLeastP()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    /**
     * WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
     * - Android 11 (R - 30) 부터 사용 가능
     * - 레터박스에 컨텐츠를 표시한다
     */
    private fun enableCutoutModeAlways() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
    }

    /**
     * WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
     * - 컷아웃 영역은 무조건 레터박스로 표시한다
     */
    private fun enableCutoutModeNever() {
        if (isVersionAtLeastP()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }

    private fun enableDecorFitSystemWindows(fit: Boolean) {
        enableAddPaddingSafeAreaCheckBox(fit)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, fit)
        } else {
            fitSystemFlag = when (fit) {
                true -> fitSystemFlag and View.SYSTEM_UI_FLAG_LAYOUT_STABLE.inv() and
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv() and
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION.inv()

                false -> fitSystemFlag or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
        }
        refreshFullScreenMode()
    }

    private fun enableAddPaddingSafeArea(addPadding: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (addPadding) {
                binding.root.setOnApplyWindowInsetsListener { v, windowInsets ->
                    val cutout = windowInsets.displayCutout
                    val insets = windowInsets.getInsets(WindowInsets.Type.systemBars() and windowFlag.inv())
                    if (cutout != null) {
                        v.updatePadding(
                            max(insets.left, cutout.safeInsetLeft),
                            max(insets.top, cutout.safeInsetTop),
                            max(insets.right, cutout.safeInsetRight),
                            max(insets.bottom, cutout.safeInsetBottom)
                        )
                    } else {
                        v.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
                    }
                    v.setOnApplyWindowInsetsListener(null)
                    windowInsets
                }
                binding.root.requestApplyInsets()
            } else {
                binding.root.updatePadding(0, 0, 0, 0)
            }
        }
    }

    private fun enableHideStatusBar(hide: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsType = when (hide) {
                true -> insetsType or WindowInsets.Type.statusBars()
                false -> insetsType and WindowInsets.Type.statusBars().inv()
            }
        } else {
            statusBarFlag = when (hide) {
                true -> statusBarFlag or View.SYSTEM_UI_FLAG_FULLSCREEN
                false -> statusBarFlag and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            }
        }
        refreshFullScreenMode()
    }

    private fun enableHideNavigationBar(hide: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsType = when (hide) {
                true -> insetsType or WindowInsets.Type.navigationBars()
                false -> insetsType and WindowInsets.Type.navigationBars().inv()
            }
        } else {
            navigationBarFlag = when (hide) {
                true -> navigationBarFlag or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                false -> navigationBarFlag and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            }
        }
        refreshFullScreenMode()
    }

    @SuppressLint("PrivateResource")
    private fun enableLightStatusBarMode(isLight: Boolean) {
        window.statusBarColor = when (isLight) {
            true -> Color.WHITE
            false -> getColor(MaterialR.color.m3_sys_color_light_primary)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val value = when (isLight) {
                true -> WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                false -> 0
            }
            window.insetsController?.setSystemBarsAppearance(
                value,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            )
        } else {
            lightStatusBarFlag = when (isLight) {
                true -> lightStatusBarFlag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                false -> lightStatusBarFlag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            refreshFullScreenMode()
        }
    }

    private fun enableLightNavigationMode(isLight: Boolean) {
        window.navigationBarColor = when (isLight) {
            true -> Color.WHITE
            false -> Color.BLACK
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val value = when (isLight) {
                true -> WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                false -> 0
            }
            window.insetsController?.setSystemBarsAppearance(
                value,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        } else {
            lightNavigationBarFlag = when (isLight) {
                true -> lightNavigationBarFlag or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                false -> lightNavigationBarFlag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            refreshFullScreenMode()
        }
    }
}

private fun isVersionAtLeastP(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}