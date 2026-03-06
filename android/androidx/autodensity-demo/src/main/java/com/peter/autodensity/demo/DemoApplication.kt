package com.peter.autodensity.demo

import android.app.Application
import com.peter.autodensity.AutoDensity
import com.peter.autodensity.AutoDensityConfig
import com.peter.autodensity.IDensity

/**
 * Demo Application
 * 展示如何初始化 AutoDensity
 */
class DemoApplication : Application(), IDensity {

    override fun onCreate() {
        super.onCreate()

        // 启用调试日志（可选）
        AutoDensityConfig.debugEnabled = true

        // 初始化自动密度适配
        AutoDensity.init(this)
    }

    override fun shouldAdaptAutoDensity(): Boolean = true
}
