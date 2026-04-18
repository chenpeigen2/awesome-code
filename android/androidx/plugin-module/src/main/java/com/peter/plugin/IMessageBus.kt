package com.peter.plugin

/**
 * 插件消息总线接口
 * 用于插件间通信，由宿主 App 实现，注入到插件中
 */
interface IMessageBus {
    /**
     * 注册消息监听
     * @param topic 消息主题
     * @param listener 消息监听器
     */
    fun subscribe(topic: String, listener: MessageListener)

    /**
     * 取消消息监听
     */
    fun unsubscribe(topic: String, listener: MessageListener)

    /**
     * 发布消息
     * @param topic 消息主题
     * @param message 消息内容
     */
    fun publish(topic: String, message: PluginMessage)

    /**
     * 获取指定主题的所有历史消息
     */
    fun getHistory(topic: String): List<PluginMessage>
}

/**
 * 消息监听器
 */
interface MessageListener {
    /**
     * 收到消息
     */
    fun onMessage(message: PluginMessage)
}

/**
 * 插件间通信消息
 */
data class PluginMessage(
    val topic: String,
    val from: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
