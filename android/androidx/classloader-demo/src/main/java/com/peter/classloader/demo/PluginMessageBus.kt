package com.peter.classloader.demo

import android.util.Log
import com.peter.plugin.IMessageBus
import com.peter.plugin.MessageListener
import com.peter.plugin.PluginMessage
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 宿主端消息总线实现
 * 在宿主 App 中创建，通过反射注入到插件中
 */
class PluginMessageBus : IMessageBus {
    companion object {
        private const val TAG = "PluginMessageBus"
    }

    private val subscribers = mutableMapOf<String, MutableList<MessageListener>>()
    private val history = mutableMapOf<String, MutableList<PluginMessage>>()

    override fun subscribe(topic: String, listener: MessageListener) {
        subscribers.getOrPut(topic) { CopyOnWriteArrayList() }.add(listener)
        Log.d(TAG, "订阅: $topic, 当前订阅者数: ${subscribers[topic]?.size}")
    }

    override fun unsubscribe(topic: String, listener: MessageListener) {
        subscribers[topic]?.remove(listener)
    }

    override fun publish(topic: String, message: PluginMessage) {
        history.getOrPut(topic) { mutableListOf() }.add(message)
        val listeners = subscribers[topic]?.toList() ?: return
        for (listener in listeners) {
            try {
                listener.onMessage(message)
            } catch (e: Exception) {
                Log.w(TAG, "消息投递失败", e)
            }
        }
        Log.d(TAG, "发布: [$topic] ${message.from}: ${message.content}")
    }

    override fun getHistory(topic: String): List<PluginMessage> {
        return history[topic]?.toList() ?: emptyList()
    }
}
