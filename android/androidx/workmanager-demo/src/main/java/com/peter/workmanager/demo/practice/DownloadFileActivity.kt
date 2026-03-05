package com.peter.workmanager.demo.practice

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityDownloadFileBinding
import com.peter.workmanager.demo.worker.DownloadWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 文件下载示例 Activity
 * 
 * 演示使用 WorkManager 下载文件。
 */
class DownloadFileActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DownloadFileActivity"
    }

    private lateinit var binding: ActivityDownloadFileBinding
    private lateinit var workManager: WorkManager
    private var workId: java.util.UUID? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadFileBinding.inflate(layoutInflater)
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
            startDownload()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelDownload()
        }
    }

    private fun startDownload() {
        logBuilder.clear()
        appendLog("开始下载任务...")
        
        val url = binding.etUrl.text.toString()
        if (url.isBlank()) {
            appendLog("请输入下载地址")
            return
        }
        
        appendLog("URL: $url")
        
        val inputData = workDataOf(
            DownloadWorker.KEY_URL to url
        )
        
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .build()
        
        workId = workRequest.id
        
        workManager.enqueue(workRequest)
        
        appendLog("下载任务已入队")
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        updateButtonState(isRunning = true)
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        // 更新进度
        val progress = workInfo?.progress?.getInt(DownloadWorker.KEY_PROGRESS, -1) ?: -1
        val downloaded = workInfo?.progress?.getLong(DownloadWorker.KEY_DOWNLOADED, 0) ?: 0
        val total = workInfo?.progress?.getLong(DownloadWorker.KEY_TOTAL, 0) ?: 0
        
        if (progress >= 0) {
            binding.progressBar.progress = progress
            binding.tvProgress.text = "进度: $progress%"
        }
        
        if (total > 0) {
            val downloadedMB = downloaded / (1024.0 * 1024.0)
            val totalMB = total / (1024.0 * 1024.0)
            binding.tvSize.text = "文件大小: %.2f MB / %.2f MB".format(downloadedMB, totalMB)
        }
        
        when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                appendLog("状态: 已入队")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("下载中", R.color.status_running)
                if (progress >= 0) {
                    appendLog("下载进度: $progress%")
                }
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("完成", R.color.status_succeeded)
                val filePath = workInfo.outputData.getString(DownloadWorker.KEY_FILE_PATH)
                val totalDownloaded = workInfo.outputData.getLong(DownloadWorker.KEY_DOWNLOADED, 0)
                appendLog("状态: 下载完成 ✓")
                appendLog("保存路径: $filePath")
                appendLog("总大小: ${totalDownloaded / 1024.0 / 1024.0} MB")
                updateButtonState(isRunning = false)
            }
            WorkInfo.State.FAILED -> {
                updateStatus("失败", R.color.status_failed)
                appendLog("状态: 下载失败 ✗")
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

    private fun cancelDownload() {
        workId?.let {
            workManager.cancelWorkById(it)
            appendLog("请求取消下载...")
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
