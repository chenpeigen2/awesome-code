package com.peter.dagger.demo.model

/**
 * ElectricHeater - 电加热器实现
 *
 * Dagger2 @Inject 示例:
 * class ElectricHeater @Inject constructor() : Heater
 *
 * @Inject 标记构造器表示这个类可以被 Dagger 自动创建
 */
class ElectricHeater : Heater {

    private var heating: Boolean = false

    override fun on() {
        heating = true
        println("ElectricHeater: ~~~~~ 加热中 ~~~~~")
    }

    override fun off() {
        heating = false
        println("ElectricHeater: 已关闭")
    }

    override fun isHot(): Boolean {
        return heating
    }

    override fun toString(): String {
        return "ElectricHeater(hashCode=${hashCode()}, heating=$heating)"
    }
}
