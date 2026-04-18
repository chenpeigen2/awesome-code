package com.peter.plugin

/**
 * 消息插件
 * 演示插件间通信：订阅消息、发布消息、处理收到的消息
 */
class MessengerPlugin : IPlugin, MessageListener {
    private var state = PluginState.CREATED
    private var messageBus: IMessageBus? = null
    private val receivedMessages = mutableListOf<PluginMessage>()
    private var params: Map<String, String> = emptyMap()

    override fun getMeta(): PluginMeta {
        return PluginMeta(
            name = "Messenger Plugin",
            version = "1.0.0",
            description = "插件间通信演示：订阅/发布消息",
            author = "PluginDemo",
            capabilities = listOf("subscribe", "publish", "message_history")
        )
    }

    override fun onCreate(context: PluginContext) {
        state = PluginState.CREATED
        params = context.params
    }

    override fun onStart() {
        state = PluginState.STARTED
    }

    override fun onStop() {
        state = PluginState.STOPPED
    }

    override fun onDestroy() {
        messageBus = null
        receivedMessages.clear()
        state = PluginState.DESTROYED
    }

    override fun getState(): PluginState = state

    /**
     * 注入消息总线，由宿主 App 调用
     */
    fun setMessageBus(bus: IMessageBus, topics: List<String>) {
        messageBus = bus
        topics.forEach { topic ->
            bus.subscribe(topic, this)
        }
    }

    /**
     * 发布消息到指定主题
     */
    fun publish(topic: String, content: String) {
        messageBus?.publish(topic, PluginMessage(topic, getMeta().name, content))
    }

    /**
     * 收到其他插件发来的消息
     */
    override fun onMessage(message: PluginMessage) {
        receivedMessages.add(message)
    }

    /**
     * 获取所有收到的消息
     */
    fun getReceivedMessages(): List<PluginMessage> = receivedMessages.toList()

    override fun execute(params: Map<String, String>): String {
        val action = params["action"] ?: "status"
        return when (action) {
            "status" -> {
                val busConnected = messageBus != null
                "Messenger Plugin\n消息总线: ${if (busConnected) "已连接" else "未连接"}\n已接收消息: ${receivedMessages.size}条\n状态: $state"
            }
            "history" -> {
                if (receivedMessages.isEmpty()) {
                    "暂无收到的消息"
                } else {
                    receivedMessages.joinToString("\n---\n") { msg ->
                        "[${msg.topic}] from ${msg.from}: ${msg.content}"
                    }
                }
            }
            "publish" -> {
                val topic = params["topic"] ?: return "缺少参数 topic"
                val content = params["content"] ?: return "缺少参数 content"
                publish(topic, content)
                "已发布消息到 [$topic]: $content"
            }
            else -> "未知操作: $action\n支持: status, history, publish"
        }
    }
}
