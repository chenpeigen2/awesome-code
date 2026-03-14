package com.peter.dagger.demo.model

import javax.inject.Inject

/**
 * CoffeeMaker - 咖啡机制作器
 *
 * 使用 @Inject 标记构造器
 */
class CoffeeMaker @Inject constructor(
    private val heater: Heater,
    private val pump: Pump
) {

    fun brew(): String {
        val result = StringBuilder()

        result.appendLine("===== 开始制作咖啡 =====")

        heater.on()
        result.appendLine("1. 加热器已启动")

        Thread.sleep(500)
        result.appendLine("2. 水温已达到")

        pump.pump()
        result.appendLine("3. 泵水完成")

        heater.off()
        result.appendLine("4. 咖啡制作完成!")

        result.appendLine("===== ~~~~~~~~~~ =====")

        return result.toString()
    }

    override fun toString(): String {
        return """
            |CoffeeMaker(hashCode=${hashCode()})
            |  ├── heater: $heater
            |  └── pump: $pump
        """.trimMargin()
    }
}
