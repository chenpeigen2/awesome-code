package com.peter.plugin

/**
 * 示例插件实现
 * 这个类将被动态加载演示
 */
class HelloPlugin : IPlugin {
    override fun getName(): String = "Hello Plugin"
    
    override fun getVersion(): String = "1.0.0"
    
    override fun execute(): String {
        return "Hello from HelloPlugin! 动态加载成功！\n时间: ${java.util.Date()}"
    }
}
