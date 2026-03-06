package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.peter.components.demo.R

/**
 * 本地广播示例
 *
 * ═══════════════════════════════════════════════════════════════
 * LocalBroadcastManager 详解
 * ═══════════════════════════════════════════════════════════════
 *
 * LocalBroadcastManager 是 AndroidX 库提供的本地广播管理器
 *
 * 优点：
 * 1. 安全性：只在应用内传递，其他应用无法监听或发送
 * 2. 效率：不需要 IPC，性能更好
 * 3. 简洁：不需要在 Manifest 中注册
 *
 * 使用场景：
 * - Fragment 间通信
 * - Service 与 Activity 通信
 * - 应用内事件通知
 *
 * ═══════════════════════════════════════════════════════════════
 * 使用方式
 * ═══════════════════════════════════════════════════════════════
 *
 * 获取实例：
 * LocalBroadcastManager.getInstance(context)
 *
 * 注册：
 * registerReceiver(receiver, intentFilter)
 *
 * 注销：
 * unregisterReceiver(receiver)
 *
 * 发送：
 * sendBroadcast(intent)
 * sendBroadcastSync(intent) // 同步发送
 *
 * ═══════════════════════════════════════════════════════════════
 * 替代方案
 * ═══════════════════════════════════════════════════════════════
 *
 * 虽然 LocalBroadcastManager 已废弃，但仍然可以使用
 * 推荐替代方案：
 * 1. LiveData / Flow：响应式数据传递
 * 2. EventBus：事件总线
 * 3. RxJava：响应式编程
 */
class LocalBroadcastActivity : AppCompatActivity() {

    companion object {
        const val ACTION_LOCAL_BROADCAST = "com.peter.components.demo.LOCAL_BROADCAST"
        const val EXTRA_MESSAGE = "message"
    }

    private lateinit var tvResult: TextView
    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val localReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: "无消息"
            tvResult.text = "收到本地广播:\n$message\n\n时间: ${System.currentTimeMillis()}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_broadcast)

        tvResult = findViewById(R.id.tvResult)

        // 获取 LocalBroadcastManager 实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        // 注册本地广播接收者
        val filter = IntentFilter(ACTION_LOCAL_BROADCAST)
        localBroadcastManager.registerReceiver(localReceiver, filter)

        findViewById<Button>(R.id.btnSendLocal).setOnClickListener {
            sendLocalBroadcast()
        }
    }

    private fun sendLocalBroadcast() {
        val intent = Intent(ACTION_LOCAL_BROADCAST).apply {
            putExtra(EXTRA_MESSAGE, "这是来自 LocalBroadcastActivity 的消息")
        }

        // 发送本地广播
        localBroadcastManager.sendBroadcast(intent)

        // 或者同步发送（会等待所有接收者处理完成）
        // localBroadcastManager.sendBroadcastSync(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销本地广播接收者
        localBroadcastManager.unregisterReceiver(localReceiver)
    }
}
