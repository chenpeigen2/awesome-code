package com.peter.dagger.demo.model

/**
 * Heater - 加热器接口
 *
 * 在 Dagger2 中，这是一个将被注入的接口
 */
interface Heater {
    /**
     * 打开加热器
     */
    fun on()

    /**
     * 关闭加热器
     */
    fun off()

    /**
     * 检查是否加热中
     */
    fun isHot(): Boolean
}
