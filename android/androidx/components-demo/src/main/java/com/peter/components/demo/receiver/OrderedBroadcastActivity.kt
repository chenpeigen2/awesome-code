package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityOrderedBroadcastBinding

/**
 * 有序广播示例
 * 
 * 知识点：
 * 1. sendOrderedBroadcast() 发送有序广播
 * 2. android:priority 设置接收者优先级 (-1000 ~ 1000)
 * 3. abortBroadcast() 中断广播传播
 * 4. setResultExtras() / getResultExtras() 传递数据
 * 
 * 注意：
 * - 同优先级按注册顺序接收
 * - 可以修改广播数据传递给下一个接收者
 * - 静态注册的优先级高于动态注册
 */
class OrderedBroadcastActivity : AppCompatActivity() {

    companion object {
        const val ACTION_ORDERED = "com.peter.components.demo.ORDERED_BROADCAST"
        private const val TAG = "OrderedBroadcast"
    }

    private lateinit var binding: ActivityOrderedBroadcastBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderedBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendOrdered.setOnClickListener {
            sendOrderedBroadcast()
        }

        binding.btnSendWithAbort.setOnClickListener {
            sendOrderedBroadcastWithAbort()
        }

        binding.btnClear.setOnClickListener {
            logBuilder.clear()
            binding.tvLog.text = ""
        }
    }

    private fun sendOrderedBroadcast() {
        logBuilder.clear()
        appendLog("发送有序广播...")
        
        val intent = Intent(ACTION_ORDERED)
        // 发送有序广播
        sendOrderedBroadcast(intent, null)
    }

    private fun sendOrderedBroadcastWithAbort() {
        logBuilder.clear()
        appendLog("发送有序广播（将中断传播）...")
        
        val intent = Intent(ACTION_ORDERED).apply {
            putExtra("abort", true)  // 通知 ReceiverA 中断
        }
        sendOrderedBroadcast(intent, null)
    }

    private fun appendLog(message: String) {
        logBuilder.append("$message\n")
        binding.tvLog.text = logBuilder.toString()
    }

    // 监听广播结果
    private val resultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            appendLog("最终结果接收")
        }
    }
}
