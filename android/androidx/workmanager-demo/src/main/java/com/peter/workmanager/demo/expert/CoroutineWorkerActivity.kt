package com.peter.workmanager.demo.expert

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityCoroutineWorkerBinding
import com.peter.workmanager.demo.worker.MyCoroutineWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 协程 Worker 示例 Activity
 * 
 * 演示 CoroutineWorker 的使用方法。
 */
class CoroutineWorkerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CoroutineWorkerActivity"
    }

    private lateinit var binding: ActivityCoroutineWorkerBinding
    private lateinit var workManager: WorkManager
    private var workId: java.util.UUID? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineWorkerBinding.inflate(layoutInflater)
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
        appendLog("创建协程任务...")
        
        // 创建协程 Worker 任务
        val workRequest = OneTimeWorkRequestBuilder<MyCoroutineWorker>()
            .build()
        
        workId = workRequest.id
        
        workManager.enqueue(workRequest)
        
        appendLog("协程任务已入队")
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        updateButtonState(isRunning = true)
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        // 更新进度
        val progress = workInfo?.progress?.getInt(MyCoroutineWorker.KEY_PROGRESS, 0) ?: 0
        binding.progressBar.progress = progress
        binding.tvProgress.text = "进度: $progress%"
        
        if (progress > 0) {
            appendLog("进度更新: $progress%")
        }
        
        when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                appendLog("状态: 已入队")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("执行中", R.color.status_running)
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("成功", R.color.status_succeeded)
                val result = workInfo.outputData.getString(MyCoroutineWorker.KEY_RESULT)
                appendLog("状态: 成功 ✓")
                appendLog(result ?: "完成")
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
