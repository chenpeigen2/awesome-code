package com.peter.autodensity

import android.content.res.Configuration

/**
 * 显示配置信息
 * 存储density相关的配置参数
 */
data class DisplayConfig(
    var densityDpi: Int = 0,
    var density: Float = 0f,
    var scaledDensity: Float = 0f,
    var fontScale: Float = 1f,
    var defaultBitmapDensity: Int = 0,
    var windowWidthDp: Int = 0,
    var windowHeightDp: Int = 0
) {
    constructor(configuration: Configuration) : this(
        densityDpi = configuration.densityDpi,
        density = configuration.densityDpi / 160f,
        scaledDensity = configuration.densityDpi / 160f,
        fontScale = configuration.fontScale,
        defaultBitmapDensity = configuration.densityDpi,
        windowWidthDp = configuration.screenWidthDp,
        windowHeightDp = configuration.screenHeightDp
    )
}
