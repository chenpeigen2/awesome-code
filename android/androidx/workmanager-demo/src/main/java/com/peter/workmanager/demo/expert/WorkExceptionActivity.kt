package com.peter.workmanager.demo.expert

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.worker.RetryWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 异常处理和重试示例 Activity
 * 
 * 演示如何处理任务异常和配置重试策略。
 */
class WorkExceptionActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WorkExceptionActivity"
    }

    private lateinit var binding: com.peter.workmanager.demo.databinding.ActivityWorkExceptionBinding
    private lateinit var workManager: WorkManager
    private var workId: java.util.UUID? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.peter.workmanager.demo.databinding.ActivityWorkExceptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        setupButtons()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            startWork()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelWork()
        }
    }

    private fun startWork() {
        logBuilder.clear()
        appendLog("创建重试任务...")
        
        // 获取配置
        val backoffDelay = binding.etBackoffDelay.text.toString().toLongOrNull() ?: 10
        
        val backoffPolicy = if (binding.rbLinear.isChecked) {
            BackoffPolicy.LINEAR
        } else {
            BackoffPolicy.EXPONENTIAL
        }
        
        appendLog("重试策略: $backoffPolicy, 初始延迟: ${backoffDelay}秒")
        
        // 创建带重试策略的任务
        val workRequest = OneTimeWorkRequestBuilder<RetryWorker>()
            .setBackoffCriteria(
                backoffPolicy,
                backoffDelay,
                TimeUnit.SECONDS
            )
            .build()
        
        workId = workRequest.id
        
        workManager.enqueue(workRequest)
        
        appendLog("任务已入队")
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        updateButtonState(isRunning = true)
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                appendLog("状态: 已入队")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("执行中", R.color.status_running)
                val retryCount = workInfo.runAttemptCount
                binding.tvRetryCount.text = "重试次数: $retryCount"
                appendLog("状态: 执行中 (尝试 #${retryCount + 1})")
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("成功", R.color.status_succeeded)
                val result = workInfo.outputData.getString(RetryWorker.KEY_RESULT)
                val retryCount = workInfo.outputData.getInt(RetryWorker.KEY_RETRY_COUNT, 0)
                appendLog("状态: 成功 ✓")
                appendLog("结果: $result")
                binding.tvRetryCount.text = "总尝试次数: ${retryCount + 1}"
                updateButtonState(isRunning = false)
            }
            WorkInfo.State.FAILED -> {
                updateStatus("失败", R.color.status_failed)
                appendLog("状态: 失败 ✗")
                updateButtonState(isRunning = false)
            }
            WorkInfo.State.CANCELLED -> {
                updateStatus("已取消", R.color.status_cancelled)
                appendLog("状态: 已取消")
                updateButtonState(isRunning = false)
            }
            else -> {}
        }
    }

    private fun cancelWork() {
        workId?.let {
            workManager.cancelWorkById(it)
            appendLog("请求取消任务...")
        }
    }

    private fun updateStatus(status: String, colorRes: Int) {
        binding.tvStatus.text = status
        binding.tvStatus.setTextColor(getColor(colorRes))
    }

    private fun updateButtonState(isRunning: Boolean) {
        binding.btnStart.isEnabled = !isRunning
        binding.btnCancel.isEnabled = isRunning
    }

    private fun appendLog(message: String) {
        val timestamp = dateFormat.format(Date())
        logBuilder.append("[$timestamp] $message\n")
        binding.tvLog.text = logBuilder.toString()
        Log.d(TAG, message)
    }
}
