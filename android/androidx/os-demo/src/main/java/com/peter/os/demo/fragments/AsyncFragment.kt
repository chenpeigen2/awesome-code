package com.peter.os.demo.fragments

import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.os.demo.databinding.FragmentAsyncBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * CancellationSignal/ResultReceiver 异步操作示例
 * 
 * CancellationSignal: 取消信号
 * - cancel(): 发送取消信号
 * - isCanceled(): 是否已取消
 * - setOnCancelListener(): 取消监听
 * - throwIfCanceled(): 已取消则抛异常
 * 
 * ResultReceiver: 结果接收器
 * - send(resultCode, Bundle): 发送结果
 * - 可跨进程传递
 * - 在创建者的 Handler 上回调
 */
class AsyncFragment : Fragment() {

    private var _binding: FragmentAsyncBinding? = null
    private val binding get() = _binding!!
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    
    private var cancellationSignal: CancellationSignal? = null
    private var taskCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsyncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        showInfo()
    }

    private fun setupViews() {
        // 启动可取消任务
        binding.btnStartTask.setOnClickListener {
            startCancellableTask()
        }

        // 取消任务
        binding.btnCancelTask.setOnClickListener {
            cancelTask()
        }

        // 使用 ResultReceiver
        binding.btnResultReceiver.setOnClickListener {
            demonstrateResultReceiver()
        }

        // 清除日志
        binding.btnClearLog.setOnClickListener {
            binding.tvLog.text = ""
        }
    }

    private fun showInfo() {
        val sb = StringBuilder()
        
        sb.appendLine("=== CancellationSignal ===")
        sb.appendLine("用于取消异步操作的标准机制")
        sb.appendLine()
        sb.appendLine("常用方法:")
        sb.appendLine("• cancel() - 发送取消信号")
        sb.appendLine("• isCanceled() - 检查是否已取消")
        sb.appendLine("• setOnCancelListener() - 监听取消")
        sb.appendLine("• throwIfCanceled() - 已取消抛异常")
        sb.appendLine()
        sb.appendLine("使用场景:")
        sb.appendLine("• ContentProvider 查询")
        sb.appendLine("• 数据库操作")
        sb.appendLine("• 自定义异步任务")
        sb.appendLine()
        sb.appendLine("=== ResultReceiver ===")
        sb.appendLine("用于接收异步操作结果")
        sb.appendLine()
        sb.appendLine("常用方法:")
        sb.appendLine("• send(code, Bundle) - 发送结果")
        sb.appendLine("• onReceiveResult() - 接收回调")
        sb.appendLine()
        sb.appendLine("使用场景:")
        sb.appendLine("• Service 返回结果给 Activity")
        sb.appendLine("• IntentService 结果回调")
        sb.appendLine("• 跨组件通信")
        
        binding.tvInfo.text = sb.toString()
    }

    private fun startCancellableTask() {
        if (cancellationSignal != null) {
            appendLog("已有任务在运行，请先取消")
            return
        }
        
        val taskNumber = ++taskCount
        cancellationSignal = CancellationSignal()
        
        // 设置取消监听
        cancellationSignal?.setOnCancelListener {
            appendLog("任务#$taskNumber 收到取消通知")
        }
        
        appendLog("任务#$taskNumber 开始执行 (5秒)")
        updateStatus(true)
        
        // 启动异步任务
        executor.execute {
            try {
                for (i in 1..5) {
                    // 检查是否取消
                    if (cancellationSignal?.isCanceled == true) {
                        mainHandler.post {
                            appendLog("任务#$taskNumber 已取消 (进度: ${i * 20}%)")
                        }
                        return@execute
                    }
                    
                    // 模拟工作
                    Thread.sleep(1000)
                    
                    val progress = i * 20
                    mainHandler.post {
                        if (cancellationSignal?.isCanceled != true) {
                            appendLog("任务#$taskNumber 进度: $progress%")
                            binding.progressTask.progress = progress
                        }
                    }
                }
                
                mainHandler.post {
                    if (cancellationSignal?.isCanceled != true) {
                        appendLog("任务#$taskNumber 完成!")
                        cancellationSignal = null
                        updateStatus(false)
                        binding.progressTask.progress = 100
                    }
                }
                
            } catch (e: Exception) {
                mainHandler.post {
                    appendLog("任务#$taskNumber 异常: ${e.message}")
                }
            }
        }
    }

    private fun cancelTask() {
        cancellationSignal?.let {
            it.cancel()
            cancellationSignal = null
            updateStatus(false)
            binding.progressTask.progress = 0
        } ?: appendLog("没有运行中的任务")
    }

    private fun demonstrateResultReceiver() {
        appendLog("--- ResultReceiver 演示 ---")
        
        // 创建 ResultReceiver
        val resultReceiver = object : ResultReceiver(mainHandler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                when (resultCode) {
                    RESULT_SUCCESS -> {
                        val data = resultData?.getString("data") ?: ""
                        appendLog("收到成功结果: $data")
                    }
                    RESULT_ERROR -> {
                        val error = resultData?.getString("error") ?: "未知错误"
                        appendLog("收到错误结果: $error")
                    }
                    RESULT_PROGRESS -> {
                        val progress = resultData?.getInt("progress", 0) ?: 0
                        appendLog("收到进度更新: $progress%")
                    }
                }
            }
        }
        
        // 模拟异步操作发送结果
        appendLog("开始模拟异步操作...")
        
        executor.execute {
            // 发送进度
            for (i in 1..3) {
                Thread.sleep(500)
                val bundle = Bundle().apply {
                    putInt("progress", i * 33)
                }
                resultReceiver.send(RESULT_PROGRESS, bundle)
            }
            
            // 发送成功结果
            Thread.sleep(500)
            val bundle = Bundle().apply {
                putString("data", "操作完成! 时间: ${System.currentTimeMillis()}")
            }
            resultReceiver.send(RESULT_SUCCESS, bundle)
        }
    }

    private fun updateStatus(isRunning: Boolean) {
        if (isRunning) {
            binding.btnStartTask.isEnabled = false
            binding.btnCancelTask.isEnabled = true
        } else {
            binding.btnStartTask.isEnabled = true
            binding.btnCancelTask.isEnabled = false
        }
    }

    private fun appendLog(log: String) {
        val currentText = binding.tvLog.text.toString()
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        val newLog = "[$time] $log"
        val newText = if (currentText.isEmpty()) newLog else "$currentText\n$newLog"
        binding.tvLog.text = newText
        
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancellationSignal?.cancel()
        executor.shutdownNow()
        _binding = null
    }

    companion object {
        private const val RESULT_SUCCESS = 1
        private const val RESULT_ERROR = 2
        private const val RESULT_PROGRESS = 3
        
        fun newInstance() = AsyncFragment()
    }
}
