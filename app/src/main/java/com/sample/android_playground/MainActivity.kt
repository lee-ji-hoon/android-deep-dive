package com.sample.android_playground

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {

    private val leanbackFlags = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    private val immersiveFlags = (View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    private val immersiveSticky = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logWindowsSize()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            findViewById<RadioButton>(R.id.rb_leanback).isVisible = false
        }
        findViewById<RadioGroup>(R.id.rg_fullScreen).setOnCheckedChangeListener { _, checkedId ->
            resetWindowSettings()
            setFullScreenMode(checkedId)
        }
    }

    private fun setFullScreenMode(checkedId: Int) {
        when (checkedId) {
            R.id.rb_immersive -> enableImmersiveMode()
            R.id.rb_leanback -> enableLeanBackMode()
            R.id.rb_sticky_immersive -> enableStickyImmersiveMode()
        }
    }

    @Suppress("DEPRECATION")
    private fun resetWindowSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.insetsController?.run {
                show(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
        }
    }

    /**
     * Immersive Mode는 네비게이션 바와 상태 바를 모두 숨기고,
     * 사용자가 화면의 가장자리를 탭할 때만 나타나게 합니다.
     */
    @Suppress("DEPRECATION")
    private fun enableImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.insetsController?.run {
                hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
            }
        } else {
            window.decorView.systemUiVisibility = immersiveFlags
        }
    }

    /**
     * Leanback Mode는 사용자가 화면을 탭할 때 네비게이션과 상태 바가 나타나게 합니다.
     * OS 12 이상부터는 [WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH] 를 사용하면 된다는 글이 있지만 현재는 Deprecated 됐다.
     * [WindowInsetsController.BEHAVIOR_DEFAULT] 로 대체를 하라고 Warning이 아닌 Error로 보여주고 있어서 무조건 바꿔야 한다.
     * [WindowInsetsController.BEHAVIOR_DEFAULT] 은 기본 동작이 화면 가장자리를 터치하거나 제스처하면 보이게 되는데 Immersive랑 다를 것이 없다.
     * https://developer.android.com/about/versions/12/features#immersive-mode-improvements 해당 OS12 업데이트 내역을 보면 사용자에게 일관된 경험을 주기 위해서 제거한 것으로 보인다.
     */
    @Suppress("DEPRECATION")
    private fun enableLeanBackMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            window.decorView.systemUiVisibility = leanbackFlags
        }
    }

    /**
     * Sticky Immersive Mode는 네비게이션과 상태 바를 숨기지만,
     * 사용자가 화면의 가장자리를 스와이프할 때 잠깐 나타났다가 자동으로 다시 숨겨집니다.
     */
    @Suppress("DEPRECATION")
    private fun enableStickyImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.insetsController?.run {
                hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = immersiveSticky
        }
    }

    private fun logWindowsSize() {
        val mainView = findViewById<ViewGroup>(R.id.main)
        mainView.post {
            val statusBarHeight = ViewCompat.getRootWindowInsets(mainView)?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
            val navigationBarHeight = ViewCompat.getRootWindowInsets(mainView)?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0

            Log.d("지훈", "decorViewHeight: ${window.decorView.measuredHeight}")
            Log.d("지훈", "mainViewHeight: ${mainView.measuredHeight}")
            Log.d("지훈", "statusBarHeight: $statusBarHeight || navigationBarHeight: $navigationBarHeight")
            Log.d("지훈", "${mainView.measuredHeight + statusBarHeight + navigationBarHeight} == ${window.decorView.measuredHeight}")
        }
    }
}

val TAG = "지훈"