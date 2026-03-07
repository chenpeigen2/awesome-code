package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.peter.components.demo.databinding.ActivityLocalBroadcastBinding

/**
 * 本地广播示例
 * 
 * 知识点：
 * 1. LocalBroadcastManager - 只在应用内部传递广播
 * 2. 更安全：其他应用无法发送或接收
 * 3. 更高效：不经过系统广播机制
 * 4. 无需权限声明
 * 
 * 注意：
 * - LocalBroadcastManager 已被标记为过时
 * - 推荐使用 LiveData / Flow 替代
 * - 或者使用其他事件总线框架
 */
class LocalBroadcastActivity : AppCompatActivity() {

    companion object {
        const val ACTION_LOCAL = "com.peter.components.demo.LOCAL_BROADCAST"
    }

    private lateinit var binding: ActivityLocalBroadcastBinding
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private var isRegistered = false
    private val logBuilder = StringBuilder()

    private val localReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val data = intent?.getStringExtra("data") ?: "无数据"
            appendLog("收到本地广播: $data")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        binding.btnRegister.setOnClickListener {
            registerLocalReceiver()
        }

        binding.btnUnregister.setOnClickListener {
            unregisterLocalReceiver()
        }

        binding.btnSend.setOnClickListener {
            sendLocalBroadcast()
        }
    }

    private fun registerLocalReceiver() {
        if (!isRegistered) {
            val filter = IntentFilter(ACTION_LOCAL)
            localBroadcastManager.registerReceiver(localReceiver, filter)
            isRegistered = true
            appendLog("本地广播接收器已注册")
        }
    }

    private fun unregisterLocalReceiver() {
        if (isRegistered) {
            localBroadcastManager.unregisterReceiver(localReceiver)
            isRegistered = false
            appendLog("本地广播接收器已注销")
        }
    }

    private fun sendLocalBroadcast() {
        val intent = Intent(ACTION_LOCAL).apply {
            putExtra("data", "来自本地广播的数据 ${System.currentTimeMillis() % 10000}")
        }
        localBroadcastManager.sendBroadcast(intent)
        appendLog("发送本地广播")
    }

    private fun appendLog(message: String) {
        logBuilder.append("$message\n")
        binding.tvLog.text = logBuilder.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterLocalReceiver()
    }
}
