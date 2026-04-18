package com.peter.plugin

/**
 * 文本处理插件
 * 演示插件能力声明和多模式执行：大小写转换、字数统计、反转、加密
 */
class TextProcessorPlugin : IPlugin {
    private var state = PluginState.CREATED

    override fun getMeta(): PluginMeta {
        return PluginMeta(
            name = "Text Processor",
            version = "1.0.0",
            description = "文本处理插件：大小写转换、字数统计、反转、加密",
            author = "PluginDemo",
            capabilities = listOf("uppercase", "lowercase", "word_count", "reverse", "caesar_cipher")
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
        val text = params["text"] ?: return "错误: 缺少参数 text"
        val mode = params["mode"] ?: "word_count"

        val result = when (mode) {
            "uppercase" -> text.uppercase()
            "lowercase" -> text.lowercase()
            "word_count" -> {
                val chars = text.length
                val words = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
                val lines = text.lines().size
                "字符数: $chars\n单词数: $words\n行数: $lines"
            }
            "reverse" -> text.reversed()
            "caesar_cipher" -> {
                val shift = params["shift"]?.toIntOrNull() ?: 3
                text.map { c ->
                    if (c.isLetter()) {
                        val base = if (c.isUpperCase()) 'A' else 'a'
                        val code = ((c - base + shift) % 26 + 26) % 26
                        (base + code)
                    } else c
                }.joinToString("")
            }
            else -> return "未知模式: $mode\n支持: uppercase, lowercase, word_count, reverse, caesar_cipher"
        }

        return "Text Processor [mode=$mode]\n输入: \"$text\"\n结果: $result"
    }
}
