package com.peter.autodensity

/**
 * Density适配接口
 * Activity或Application实现此接口来控制是否启用自动密度适配
 */
interface IDensity {

    /**
     * 是否启用自动密度适配
     * @return true 启用, false 禁用
     */
    fun shouldAdaptAutoDensity(): Boolean = true

    /**
     * 获取UI基准宽度(dp)
     * 用于基于宽度的等比缩放，返回0表示不启用
     * 例如：返回360表示以360dp为基准进行等比缩放
     * @return 基准宽度(dp)，0表示不启用
     */
    fun getRatioUiBaseWidthDp(): Int = 0
}
