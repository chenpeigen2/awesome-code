package com.peter.dagger.demo.model

/**
 * ElectricHeater - 电加热器实现
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

    override fun isHot(): Boolean = heating

    override fun toString(): String {
        return "ElectricHeater(hashCode=${hashCode()}, heating=$heating)"
    }
}