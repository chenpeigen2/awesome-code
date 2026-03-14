package com.peter.plugin

/**
 * 计算插件实现
 * 演示另一个插件实现
 */
class CalculatorPlugin : IPlugin {
    override fun getName(): String = "Calculator Plugin"
    
    override fun getVersion(): String = "2.0.0"
    
    override fun execute(): String {
        val a = 10
        val b = 20
        return """
            Calculator Plugin 执行结果:
            $a + $b = ${a + b}
            $a × $b = ${a * b}
            $a ÷ $b = ${a.toDouble() / b}
        """.trimIndent()
    }
}
