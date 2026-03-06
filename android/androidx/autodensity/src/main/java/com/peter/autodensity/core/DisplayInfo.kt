package com.peter.autodensity.core

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 显示信息
 *
 * 封装了屏幕相关的所有参数，方便计算和调试
 */
data class DisplayInfo(
    /**
     * 屏幕密度 = densityDpi / 160
     */
    val density: Float,

    /**
     * 字体缩放密度 = density * fontScale
     */
    val scaledDensity: Float,

    /**
     * 屏幕 DPI
     */
    val densityDpi: Int,

    /**
     * 字体缩放比例
     */
    val fontScale: Float,

    /**
     * 屏幕宽度（像素）
     */
    val widthPixels: Int,

    /**
     * 屏幕高度（像素）
     */
    val heightPixels: Int
) {
    companion object {
        /**
         * 从 Context 创建 DisplayInfo
         */
        fun from(context: Context): DisplayInfo {
            val metrics = context.resources.displayMetrics
            val config = context.resources.configuration

            return DisplayInfo(
                density = metrics.density,
                scaledDensity = metrics.scaledDensity,
                densityDpi = metrics.densityDpi,
                fontScale = config.fontScale,
                widthPixels = metrics.widthPixels,
                heightPixels = metrics.heightPixels
            )
        }

        /**
         * 从 DisplayMetrics 创建 DisplayInfo
         */
        fun from(metrics: DisplayMetrics, fontScale: Float = 1f): DisplayInfo {
            return DisplayInfo(
                density = metrics.density,
                scaledDensity = metrics.scaledDensity,
                densityDpi = metrics.densityDpi,
                fontScale = fontScale,
                widthPixels = metrics.widthPixels,
                heightPixels = metrics.heightPixels
            )
        }

        /**
         * 获取屏幕实际尺寸（像素）
         */
        fun getRealScreenSize(context: Context): Point {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()

            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size)
            } else {
                display.getSize(size)
            }

            return size
        }
    }

    /**
     * 屏幕宽度（dp）
     */
    val widthDp: Float
        get() = widthPixels / density

    /**
     * 屏幕高度（dp）
     */
    val heightDp: Float
        get() = heightPixels / density

    /**
     * 转换为可读字符串
     */
    override fun toString(): String = buildString {
        appendLine("DisplayInfo:")
        appendLine("  density       = $density")
        appendLine("  scaledDensity = $scaledDensity")
        appendLine("  densityDpi    = $densityDpi")
        appendLine("  fontScale     = $fontScale")
        appendLine("  screen        = ${widthPixels}x${heightPixels} px")
        appendLine("  screen        = ${widthDp}x${heightDp} dp")
    }
}
