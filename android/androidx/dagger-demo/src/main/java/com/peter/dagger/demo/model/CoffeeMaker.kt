package com.peter.dagger.demo.model

/**
 * CoffeeMaker - 咖啡机制作器
 *
 * Dagger2 @Inject 示例:
 * class CoffeeMaker @Inject constructor(
 *     private val heater: Heater,
 *     private val pump: Pump
 * )
 *
 * 这是 Dagger2 官方教程的经典示例
 * 演示了构造器注入的核心概念
 */
class CoffeeMaker(
    private val heater: Heater,
    private val pump: Pump
) {

    /**
     * 制作咖啡
     */
    fun brew(): String {
        val result = StringBuilder()

        result.appendLine("===== 开始制作咖啡 =====")

        // 1. 加热
        heater.on()
        result.appendLine("1. 加热器已启动")

        // 2. 等待加热
        Thread.sleep(500)
        result.appendLine("2. 水温已达到")

        // 3. 泵水
        pump.pump()
        result.appendLine("3. 泵水完成")

        // 4. 完成
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
