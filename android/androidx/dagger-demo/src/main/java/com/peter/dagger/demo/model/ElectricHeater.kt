package com.peter.dagger.demo.model

import javax.inject.Inject

/**
 * ElectricHeater - 电加热器实现
 *
 * 使用 @Inject 标记构造器，配合 @Binds 使用
 */
class ElectricHeater @Inject constructor() : Heater {

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
