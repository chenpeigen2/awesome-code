package com.peter.plugin

/**
 * 计算器插件
 * 演示参数化执行，支持动态传入操作数和运算符
 */
class CalculatorPlugin : IPlugin {
    private var state = PluginState.CREATED

    override fun getMeta(): PluginMeta {
        return PluginMeta(
            name = "Calculator Plugin",
            version = "2.0.0",
            description = "参数化计算插件，支持四则运算",
            author = "PluginDemo",
            capabilities = listOf("calculate", "history")
        )
    }

    override fun onCreate(context: PluginContext) {
        state = PluginState.CREATED
    }

    override fun onStart() {
        state = PluginState.STARTED
    }

    override fun onStop() {
        state = PluginState.STOPPED
    }

    override fun onDestroy() {
        state = PluginState.DESTROYED
    }

    override fun getState(): PluginState = state

    override fun execute(params: Map<String, String>): String {
        val a = params["a"]?.toDoubleOrNull() ?: return "错误: 缺少参数 a"
        val b = params["b"]?.toDoubleOrNull() ?: return "错误: 缺少参数 b"
        val op = params["op"] ?: "+"

        val result = when (op) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0.0) a / b else return "错误: 除数不能为0"
            "%" -> if (b != 0.0) a % b else return "错误: 取模除数不能为0"
            "^" -> Math.pow(a, b)
            else -> return "错误: 不支持的运算符 '$op'\n支持: +, -, *, /, %, ^"
        }

        return "Calculator Plugin v${getMeta().version}\n运算: $a $op $b = $result\n状态: $state"
    }
}
