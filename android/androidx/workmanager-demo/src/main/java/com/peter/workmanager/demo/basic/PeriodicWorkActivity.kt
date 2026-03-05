package com.peter.workmanager.demo.basic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityPeriodicWorkBinding
import com.peter.workmanager.demo.worker.PeriodicWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 周期性任务示例 Activity
 * 
 * 演示 PeriodicWorkRequest 的使用方法。
 * 注意：最小间隔时间为 15 分钟。
 */
class PeriodicWorkActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PeriodicWorkActivity"
        private const val UNIQUE_WORK_NAME = "periodic_work"
    }

    private lateinit var binding: ActivityPeriodicWorkBinding
    private lateinit var workManager: WorkManager
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()
    private var executionCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeriodicWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        setupButtons()
        observePeriodicWork()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            startPeriodicWork()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelPeriodicWork()
        }
    }

    private fun startPeriodicWork() {
        logBuilder.clear()
        executionCount = 0
        appendLog("创建周期性任务...")
        
        // 获取间隔时间（分钟），最小 15 分钟
        var intervalMinutes = binding.etInterval.text.toString().toLongOrNull() ?: 15
        if (intervalMinutes < 15) {
            intervalMinutes = 15
            binding.etInterval.setText("15")
            appendLog("间隔时间已调整为最小值 15 分钟")
        }
        
        // 获取灵活执行窗口（分钟）
        val flexMinutes = binding.etFlexInterval.text.toString().toLongOrNull() ?: 5
        
        // 创建周期性任务请求
        val workRequest = PeriodicWorkRequestBuilder<PeriodicWorker>(
            intervalMinutes, TimeUnit.MINUTES,
            flexMinutes, TimeUnit.MINUTES
        ).build()
        
        // 使用唯一名称入队，防止重复
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
        appendLog("周期任务已启动, 间隔: $intervalMinutes 分钟, 灵活窗口: $flexMinutes 分钟")
        
        updateButtonState(isRunning = true)
        observePeriodicWork()
    }

    private fun observePeriodicWork() {
        workManager.getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME)
            .observe(this, { workInfos ->
                workInfos?.firstOrNull()?.let { workInfo ->
                    updateWorkInfo(workInfo)
                }
            })
    }

    private fun updateWorkInfo(workInfo: WorkInfo) {
        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                updateNextRunTime(workInfo)
                appendLog("状态: 已入队，等待下次执行")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("执行中", R.color.status_running)
                executionCount++
                binding.tvRunCount.text = "已执行次数: $executionCount"
                appendLog("状态: 执行中... (第 $executionCount 次)")
            }
            WorkInfo.State.SUCCEEDED -> {
                val executionTime = workInfo.outputData.getLong(PeriodicWorker.KEY_EXECUTION_COUNT, 0)
                appendLog("执行完成于: ${dateFormat.format(Date(executionTime))}")
            }
            WorkInfo.State.FAILED -> {
                appendLog("执行失败")
            }
            WorkInfo.State.CANCELLED -> {
                updateStatus("已取消", R.color.status_cancelled)
                appendLog("状态: 已取消")
                updateButtonState(isRunning = false)
            }
            else -> {}
        }
    }

    private fun updateNextRunTime(workInfo: WorkInfo) {
        // WorkManager 不直接提供下次执行时间，这里显示估计值
        binding.tvNextRun.text = "下次执行: 约 ${binding.etInterval.text} 分钟后"
    }

    private fun cancelPeriodicWork() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
        appendLog("请求取消周期任务...")
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
