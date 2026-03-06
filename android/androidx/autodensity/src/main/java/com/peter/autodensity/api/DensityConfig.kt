package com.peter.autodensity.api

/**
 * 密度适配配置
 */
data class DensityConfig(
    /**
     * 设计稿宽度（dp）
     * 默认 360dp，这是常见的 Android 设计稿宽度
     */
    val designWidthDp: Int = 360,

    /**
     * 字体缩放比例
     * - null = 跟随系统设置
     * - 1.0f = 正常
     * - 1.15f = 大
     * - 1.3f = 超大
     */
    val fontScale: Float? = null,

    /**
     * 是否启用调试日志
     */
    val debug: Boolean = false,

    /**
     * 是否同时更新系统资源
     * 开启后 Resources.getSystem() 也会被适配
     */
    val updateSystemResources: Boolean = true,

    /**
     * 是否强制使用 designWidthDp（忽略 baseWidthDp 限制）
     *
     * - false（默认）：当 designWidthDp < baseWidthDp 时，会限制在 baseWidthDp
     * - true：强制使用 designWidthDp，不做任何限制
     */
    val forceDesignWidth: Boolean = false
)

/**
 * 实现 DensityAware 接口来控制是否启用适配
 *
 * 可以在 Application 或 Activity 上实现此接口
 */
interface DensityAware {
    /**
     * 是否启用密度适配
     * @return true 启用，false 禁用
     */
    fun shouldAdaptDensity(): Boolean = true
}

/**
 * 扩展接口，提供更多控制选项
 */
interface ActivityDensityAware : DensityAware {
    /**
     * 获取基准宽度用于限制界面过大
     *
     * 当 designWidthDp < baseWidthDp 时，会限制在 baseWidthDp
     * 例如：baseWidthDp = 360，designWidthDp = 80 时，实际效果会是 360dp
     *
     * 默认返回 -1，表示使用屏幕实际 dp 宽度作为基准
     * 返回 0 表示不启用限制
     *
     * @return 基准宽度 dp，-1 = 屏幕实际宽度，0 = 不限制
     */
    fun getBaseWidthDp(): Int = -1

    /**
     * 是否强制使用 designWidthDp（忽略 baseWidthDp 限制）
     * @return true 强制使用 designWidthDp
     */
    fun forceDesignWidth(): Boolean = true
}
