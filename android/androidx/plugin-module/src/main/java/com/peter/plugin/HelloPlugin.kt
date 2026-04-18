package com.peter.plugin

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Hello 插件
 * 演示完整的插件生命周期和参数处理
 */
class HelloPlugin : IPlugin {
    private var state = PluginState.CREATED
    private var params: Map<String, String> = emptyMap()
    private var createTime: Long = 0

    override fun getMeta(): PluginMeta {
        return PluginMeta(
            name = "Hello Plugin",
            version = "2.0.0",
            description = "演示插件生命周期和参数化执行",
            author = "PluginDemo",
            capabilities = listOf("greeting", "time")
        )
    }

    override fun onCreate(context: PluginContext) {
        state = PluginState.CREATED
        params = context.params
        createTime = System.currentTimeMillis()
    }

    override fun onStart() {
        state = PluginState.STARTED
    }

    override fun onStop() {
        state = PluginState.STOPPED
    }

    override fun onDestroy() {
        state = PluginState.DESTROYED
        params = emptyMap()
    }

    override fun getState(): PluginState = state

    override fun execute(params: Map<String, String>): String {
        val name = params["name"] ?: this.params["name"] ?: "World"
        val lang = params["lang"] ?: this.params["lang"] ?: "zh"
        val greeting = when (lang) {
            "en" -> "Hello"
            "ja" -> "こんにちは"
            "ko" -> "안녕하세요"
            else -> "你好"
        }
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return "$greeting, $name!\n当前时间: $time\n插件创建于: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(createTime))}\n插件状态: $state"
    }
}
