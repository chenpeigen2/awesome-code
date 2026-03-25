package com.peter.statusbar.demo

import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 状态栏工具类
 * 提供状态栏颜色、沉浸式、图标颜色、隐藏等功能
 */
object StatusBarHelper {

    private const val TAG_STATUS_BAR_VIEW = "status_bar_placeholder"

    /**
     * 从 View 中查找 Activity
     */
    private fun findActivity(view: View): Activity? {
        var context = view.context
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(view: View): Int {
        val activity = findActivity(view) ?: return 0
        return getStatusBarHeight(activity)
    }

    /**
     * 获取状态栏高度（Activity 版本）
     */
    fun getStatusBarHeight(activity: Activity): Int {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        return insets.top
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(view: View): Int {
        val activity = findActivity(view) ?: return 0
        return getNavigationBarHeight(activity)
    }

    /**
     * 获取导航栏高度（Activity 版本）
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        return insets.bottom
    }

    /**
     * 设置状态栏颜色
     * 通过在 DecorView 中添加占位 View 实现，不使用废弃的 statusBarColor API
     */
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int) {
        val decorView = activity.window.decorView as? FrameLayout ?: return
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        // 查找或创建状态栏占位 View
        var statusBarView = decorView.findViewWithTag<View>(TAG_STATUS_BAR_VIEW)
        if (statusBarView == null) {
            statusBarView = View(activity).apply {
                tag = TAG_STATUS_BAR_VIEW
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    0
                ).apply {
                    gravity = Gravity.TOP
                }
            }
            decorView.addView(statusBarView)

            // 监听 WindowInsets 变化，动态更新高度
            ViewCompat.setOnApplyWindowInsetsListener(decorView) { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                statusBarView.layoutParams.height = statusBarInsets.top
                statusBarView.requestLayout()
                insets
            }
        }

        // 设置颜色
        statusBarView.setBackgroundColor(color)

        // 立即尝试更新高度（如果 insets 已经可用）
        val currentInsets = decorView.rootWindowInsets
        if (currentInsets != null) {
            val statusBarInsets = ViewCompat.getRootWindowInsets(decorView)
                ?.getInsets(WindowInsetsCompat.Type.statusBars())
            if (statusBarInsets != null && statusBarInsets.top > 0) {
                statusBarView.layoutParams.height = statusBarInsets.top
                statusBarView.requestLayout()
            }
        }
    }

    /**
     * 设置状态栏透明
     */
    fun setStatusBarTransparent(activity: Activity) {
        val decorView = activity.window.decorView as? FrameLayout ?: return
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        // 移除状态栏占位 View
        val statusBarView = decorView.findViewWithTag<View>(TAG_STATUS_BAR_VIEW)
        if (statusBarView != null) {
            decorView.removeView(statusBarView)
        }
    }

    /**
     * 设置状态栏图标为深色（适用于浅色背景）
     */
    fun setStatusBarLightIcon(activity: Activity, isLight: Boolean) {
        val controller = activity.window.insetsController
        if (isLight) {
            // 深色图标 = 清除 LIGHT_STATUS_BARS 标志
            controller?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            // 浅色图标 = 设置 LIGHT_STATUS_BARS 标志
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    /**
     * 设置沉浸式模式 - 内容延伸到状态栏后面
     */
    fun setImmersiveMode(activity: Activity, fitsSystemWindows: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, fitsSystemWindows)
    }

    /**
     * 设置沉浸式全屏模式（滑动边缘恢复）
     */
    fun setImmersiveSticky(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * 设置低调模式（状态栏变暗）
     */
    fun setLowProfileMode(activity: Activity, enabled: Boolean) {
        if (enabled) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity.window.insetsController?.show(WindowInsets.Type.statusBars())
        }
    }

    /**
     * 隐藏状态栏
     */
    fun hideStatusBar(activity: Activity) {
        activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
    }

    /**
     * 显示状态栏
     */
    fun showStatusBar(activity: Activity) {
        activity.window.insetsController?.show(WindowInsets.Type.statusBars())
    }

    /**
     * 设置全屏模式（完全隐藏系统栏）
     */
    fun setFullscreenMode(activity: Activity, enabled: Boolean) {
        val controller = activity.window.insetsController
        if (enabled) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            controller?.hide(WindowInsets.Type.systemBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller?.show(WindowInsets.Type.systemBars())
            WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        }
    }

    /**
     * 获取刘海屏安全区域
     */
    fun getDisplayCutoutSafeInsets(view: View): WindowInsetsCompat? {
        return ViewCompat.getRootWindowInsets(view)
    }

    /**
     * 检查是否有刘海屏
     */
    fun hasDisplayCutout(activity: Activity): Boolean {
        val displayCutout = activity.window.decorView.rootWindowInsets?.displayCutout
        return displayCutout != null
    }

    /**
     * 获取刘海屏信息
     */
    fun getDisplayCutoutInfo(activity: Activity): DisplayCutoutInfo? {
        val displayCutout = activity.window.decorView.rootWindowInsets?.displayCutout
        if (displayCutout != null) {
            return DisplayCutoutInfo(
                safeInsetTop = displayCutout.safeInsetTop,
                safeInsetBottom = displayCutout.safeInsetBottom,
                safeInsetLeft = displayCutout.safeInsetLeft,
                safeInsetRight = displayCutout.safeInsetRight,
                boundingRects = displayCutout.boundingRects
            )
        }
        return null
    }

    /**
     * 设置刘海屏布局模式
     * @param shortEdges true = 允许内容渲染到刘海区域
     */
    fun setLayoutInDisplayCutoutMode(activity: Activity, shortEdges: Boolean) {
        val params = activity.window.attributes
        params.layoutInDisplayCutoutMode = if (shortEdges) {
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        } else {
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
        activity.window.attributes = params
    }

    /**
     * 重置状态栏到默认状态
     */
    fun resetStatusBar(activity: Activity) {
        showStatusBar(activity)
        setStatusBarLightIcon(activity, true)
        setImmersiveMode(activity, true)
        setStatusBarColor(activity, Color.TRANSPARENT)
    }
}

/**
 * 刘海屏信息数据类
 */
data class DisplayCutoutInfo(
    val safeInsetTop: Int,
    val safeInsetBottom: Int,
    val safeInsetLeft: Int,
    val safeInsetRight: Int,
    val boundingRects: List<android.graphics.Rect>
)
