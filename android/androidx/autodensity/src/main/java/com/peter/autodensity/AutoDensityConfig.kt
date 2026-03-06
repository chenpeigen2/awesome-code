package com.peter.autodensity

/**
 * 自动密度适配配置
 * 用于全局配置自动密度适配参数
 */
object AutoDensityConfig {

    /**
     * 标准DPI基准值
     * 默认440，可根据设计稿调整
     */
    var standardDpi: Float = 440f
        private set

    /**
     * 标准PPI基准值
     * 默认386，可根据设备调整
     */
    var standardPpi: Float = 386f
        private set

    /**
     * 是否启用调试日志
     */
    var debugEnabled: Boolean = false
        set(value) {
            field = value
            DebugUtil.setDebugEnabled(value)
        }

    /**
     * 设置标准DPI
     */
    fun setStandardDpi(dpi: Float) {
        standardDpi = dpi
    }

    /**
     * 设置标准PPI
     */
    fun setStandardPpi(ppi: Float) {
        standardPpi = ppi
    }

    /**
     * 获取标准缩放比例
     */
    fun getStandardScale(): Float {
        return standardDpi / standardPpi
    }
}
