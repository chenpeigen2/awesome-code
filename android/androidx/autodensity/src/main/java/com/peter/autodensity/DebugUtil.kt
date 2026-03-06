package com.peter.autodensity

import android.util.Log

/**
 * 调试工具类
 */
object DebugUtil {

    const val TAG = "AutoDensity"

    /**
     * 是否启用调试日志
     * 可通过 setDebugEnabled 开启
     */
    private var sDebugEnabled = false

    /**
     * 调试缩放值
     * < 0: 禁用自动密度
     * = 0: 默认
     * > 0: 使用指定缩放值
     */
    private var sDebugScale = 0f

    /**
     * 设置是否启用调试日志
     */
    fun setDebugEnabled(enabled: Boolean) {
        sDebugEnabled = enabled
    }

    /**
     * 设置调试缩放值
     */
    fun setDebugScale(scale: Float) {
        sDebugScale = scale
    }

    /**
     * 是否在调试模式下禁用自动密度
     */
    fun isDisableAutoDensityInDebugMode(): Boolean {
        return sDebugScale < 0
    }

    /**
     * 获取调试缩放值
     */
    fun getDebugScale(): Float {
        return sDebugScale
    }

    /**
     * 打印调试日志
     */
    fun printDensityLog(log: String) {
        if (sDebugEnabled) {
            Log.d(TAG, log)
        }
    }
}
