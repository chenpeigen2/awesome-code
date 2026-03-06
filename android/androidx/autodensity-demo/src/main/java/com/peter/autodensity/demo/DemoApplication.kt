package com.peter.autodensity.demo

import android.app.Application
import com.peter.autodensity.api.AutoDensity
import com.peter.autodensity.api.DensityAware
import com.peter.autodensity.api.DensityConfig

/**
 * Demo Application
 * 展示如何初始化 AutoDensity
 */
class DemoApplication : Application(), DensityAware {

    override fun onCreate() {
        super.onCreate()

        // 配置：设计稿宽度 360dp，启用调试
        val config = DensityConfig(
            designWidthDp = 660,
            debug = true,
            forceDesignWidth = true  // 不强制，使用 baseWidthDp 限制
        )

        // 初始化
        AutoDensity.init(this, config)

        // 如果需要强制使用 designWidthDp（忽略 baseWidthDp 限制）
        // AutoDensity.setForceDesignWidth(true)
        // AutoDensity.setDesignWidth(80)  // 设置很小的值让界面变大
    }

    // 启用密度适配
    override fun shouldAdaptDensity(): Boolean = true
}
