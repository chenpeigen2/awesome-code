package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.R
import com.peter.os.demo.databinding.FragmentHandlerThreadBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * HandlerThread 示例
 * 
 * HandlerThread: 自带 Looper 的线程
 * - 自动创建 Looper，无需手动调用 Looper.prepare()
 * - 适合需要在后台线程处理消息队列的场景
 * - getLooper(): 获取线程的 Looper
 * - quit(): 安全退出线程
 * - quitSafely(): 处理完消息后退出
 */
class HandlerThreadFragment : Fragment() {

    private var _binding: FragmentHandlerThreadBinding? = null
    private val binding get() = _binding!!
    
    private var handlerThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    
    private var taskCount = 0
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHandlerThreadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        // 启动 HandlerThread
        binding.btnStartThread.setOnClickListener {
            startHandlerThread()
        }

        // 停止 HandlerThread
        binding.btnStopThread.setOnClickListener {
            stopHandlerThread()
        }

        // 发送任务到后台线程
        binding.btnPostTask.setOnClickListener {
            postBackgroundTask()
        }

        // 发送延迟任务
        binding.btnPostDelayed.setOnClickListener {
            postDelayedTask()
        }

        // 发送消息
        binding.btnSendMessage.setOnClickListener {
            sendMessageToBackground()
        }

        // 清除日志
        binding.btnClearLog.setOnClickListener {
            binding.tvLog.text = ""
            taskCount = 0
        }
    }

    private fun startHandlerThread() {
        if (handlerThread != null && handlerThread!!.isAlive) {
            appendLog("HandlerThread 已在运行")
            return
        }

        // 创建并启动 HandlerThread
        handlerThread = HandlerThread("BackgroundWorker").apply {
            start()
        }

        // 获取 HandlerThread 的 Looper，创建 Handler
        backgroundHandler = Handler(handlerThread!!.looper) { msg ->
            val time = dateFormat.format(Date())
            appendLog("[$time] 后台线程收到消息: what=${msg.what}, 数据=${msg.obj}")
            true
        }

        updateStatus(true)
        appendLog("HandlerThread 启动: ${handlerThread!!.name}")
        appendLog("线程ID: ${handlerThread!!.threadId}")
        appendLog("Looper: ${handlerThread!!.looper}")
    }

    private fun stopHandlerThread() {
        if (handlerThread == null || !handlerThread!!.isAlive) {
            appendLog("HandlerThread 未运行")
            return
        }

        // 方式1: quit() - 立即退出
        // handlerThread!!.quit()
        
        // 方式2: quitSafely() - 处理完队列中的消息后退出
        handlerThread!!.quitSafely()
        
        appendLog("HandlerThread 已请求停止 (quitSafely)")
        
        handlerThread = null
        backgroundHandler = null
        updateStatus(false)
    }

    private fun postBackgroundTask() {
        val handler = backgroundHandler
        if (handler == null) {
            appendLog("请先启动 HandlerThread")
            return
        }

        val taskNumber = ++taskCount
        
        // 在后台线程执行任务
        handler.post {
            val threadName = Thread.currentThread().name
            val time = dateFormat.format(Date())
            
            // 模拟耗时操作
            Thread.sleep(500)
            
            // 回到主线程更新UI
            mainHandler.post {
                appendLog("[$time] 任务#$taskNumber 在 [$threadName] 完成")
            }
        }
        
        appendLog("任务#$taskNumber 已提交到后台线程")
    }

    private fun postDelayedTask() {
        val handler = backgroundHandler
        if (handler == null) {
            appendLog("请先启动 HandlerThread")
            return
        }

        val taskNumber = ++taskCount
        
        // 延迟 2 秒执行
        handler.postDelayed({
            val threadName = Thread.currentThread().name
            val time = dateFormat.format(Date())
            
            mainHandler.post {
                appendLog("[$time] 延迟任务#$taskNumber 在 [$threadName] 完成")
            }
        }, 2000)
        
        appendLog("延迟任务#$taskNumber 已提交 (2秒后执行)")
    }

    private fun sendMessageToBackground() {
        val handler = backgroundHandler
        if (handler == null) {
            appendLog("请先启动 HandlerThread")
            return
        }

        val msg = handler.obtainMessage()
        msg.what = 100
        msg.arg1 = ++taskCount
        msg.arg2 = (0..100).random()
        msg.obj = "自定义数据 #${taskCount}"
        
        handler.sendMessage(msg)
        appendLog("消息已发送: what=${msg.what}, arg1=${msg.arg1}")
    }

    private fun updateStatus(isRunning: Boolean) {
        if (isRunning) {
            binding.tvStatus.text = "运行中"
            binding.viewStatusDot.setBackgroundResource(R.drawable.bg_status_active)
            binding.btnStartThread.isEnabled = false
            binding.btnStopThread.isEnabled = true
        } else {
            binding.tvStatus.text = "已停止"
            binding.viewStatusDot.setBackgroundResource(R.drawable.bg_status_inactive)
            binding.btnStartThread.isEnabled = true
            binding.btnStopThread.isEnabled = false
        }
    }

    private fun appendLog(log: String) {
        val currentText = binding.tvLog.text.toString()
        val newText = if (currentText.isEmpty()) log else "$currentText\n$log"
        binding.tvLog.text = newText
        
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopHandlerThread()
        mainHandler.removeCallbacksAndMessages(null)
        _binding = null
    }

    companion object {
        fun newInstance() = HandlerThreadFragment()
    }
}
