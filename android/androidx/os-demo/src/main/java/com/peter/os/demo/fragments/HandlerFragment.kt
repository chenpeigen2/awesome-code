package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.databinding.FragmentHandlerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handler/Looper/Message 使用示例
 * 
 * Handler: 用于发送和处理消息/Runnable
 * Looper: 消息循环，每个线程只能有一个
 * Message: 消息载体
 * MessageQueue: 消息队列
 */
class HandlerFragment : Fragment() {

    private var _binding: FragmentHandlerBinding? = null
    private val binding get() = _binding!!
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private var messageCount = 0
    
    // 自定义 Handler，处理消息
    private val customHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val log = "[$time] 收到消息: what=${msg.what}, arg1=${msg.arg1}, arg2=${msg.arg2}, obj=${msg.obj}"
            appendLog(log)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHandlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateThreadInfo()
    }

    private fun setupViews() {
        // 发送消息
        binding.btnSendMessage.setOnClickListener {
            val msg = customHandler.obtainMessage()
            msg.what = MESSAGE_WHAT
            msg.arg1 = messageCount++
            msg.arg2 = (0..100).random()
            msg.obj = "自定义数据 #${messageCount}"
            customHandler.sendMessage(msg)
            appendLog("发送消息: what=${msg.what}, arg1=${msg.arg1}")
        }

        // 延迟发送消息
        binding.btnSendDelayed.setOnClickListener {
            val msg = customHandler.obtainMessage(MESSAGE_DELAYED, messageCount++, 0, "延迟数据")
            customHandler.sendMessageDelayed(msg, 1000)
            appendLog("延迟 1 秒发送消息")
        }

        // 发送 Runnable
        binding.btnSendRunnable.setOnClickListener {
            mainHandler.post {
                val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                appendLog("[$time] Runnable 执行在: ${Thread.currentThread().name}")
            }
            appendLog("发送 Runnable 到主线程")
        }

        // Post Delayed
        binding.btnPostDelayed.setOnClickListener {
            mainHandler.postDelayed({
                val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                appendLog("[$time] 延迟 Runnable 执行")
            }, 2000)
            appendLog("发送延迟 2 秒的 Runnable")
        }

        // 移除回调
        binding.btnRemoveCallbacks.setOnClickListener {
            customHandler.removeCallbacksAndMessages(null)
            mainHandler.removeCallbacksAndMessages(null)
            appendLog("移除所有回调和消息")
        }

        // 清除日志
        binding.btnClearLog.setOnClickListener {
            binding.tvLog.text = ""
            messageCount = 0
        }
    }

    private fun updateThreadInfo() {
        val sb = StringBuilder()
        
        // 主线程信息
        sb.appendLine("=== 主线程信息 ===")
        sb.appendLine("线程名: ${Looper.getMainLooper().thread.name}")
        sb.appendLine("线程ID: ${Looper.getMainLooper().thread.id}")
        sb.appendLine()
        
        // Looper 信息
        sb.appendLine("=== Looper 信息 ===")
        sb.appendLine("Main Looper: ${Looper.getMainLooper()}")
        sb.appendLine("当前线程 Looper: ${Looper.myLooper()}")
        sb.appendLine("当前线程: ${Thread.currentThread().name}")
        sb.appendLine()
        
        // MessageQueue 信息
        sb.appendLine("=== MessageQueue ===")
        sb.appendLine("是否空闲: ${mainHandler.looper?.queue?.isIdle ?: "N/A"}")
        
        binding.tvThreadInfo.text = sb.toString()
    }

    private fun appendLog(log: String) {
        val currentText = binding.tvLog.text.toString()
        val newText = if (currentText.isEmpty()) log else "$currentText\n$log"
        binding.tvLog.text = newText
        
        // 滚动到底部
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customHandler.removeCallbacksAndMessages(null)
        mainHandler.removeCallbacksAndMessages(null)
        _binding = null
    }

    companion object {
        private const val MESSAGE_WHAT = 100
        private const val MESSAGE_DELAYED = 101
        
        fun newInstance() = HandlerFragment()
    }
}
