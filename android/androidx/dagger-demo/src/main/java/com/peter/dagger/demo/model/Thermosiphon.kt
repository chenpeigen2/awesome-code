package com.peter.dagger.demo.model

import javax.inject.Inject

/**
 * Thermosiphon - 热虹吸泵实现
 *
 * 使用 @Inject 标记构造器，配合 @Binds 使用
 */
class Thermosiphon @Inject constructor(
    private val heater: Heater
) : Pump {

    override fun pump() {
        if (heater.isHot()) {
            println("Thermosiphon: => => => 泵水中 => => =>")
        } else {
            println("Thermosiphon: 加热器未启动，无法泵水")
        }
    }

    override fun toString(): String {
        return "Thermosiphon(hashCode=${hashCode()}, heater=$heater)"
    }
}
