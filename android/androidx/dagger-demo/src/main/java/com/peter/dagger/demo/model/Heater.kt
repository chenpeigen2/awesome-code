package com.peter.dagger.demo.model

/**
 * Heater - 加热器接口
 *
 * 在 Dagger2 中，这是一个将被注入的接口
 */
interface Heater {
    fun on()
    fun off()
    fun isHot(): Boolean
}