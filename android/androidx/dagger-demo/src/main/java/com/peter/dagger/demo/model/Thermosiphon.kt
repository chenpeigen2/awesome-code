package com.peter.dagger.demo.model

/**
 * Thermosiphon - 热虹吸泵实现
 *
 * Dagger2 @Inject 示例:
 * class Thermosiphon @Inject constructor(
 *     private val heater: Heater
 * ) : Pump
 *
 * 构造器注入演示：Thermosiphon 需要 Heater 作为依赖
 */
class Thermosiphon(
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
