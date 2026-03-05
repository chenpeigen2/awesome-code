package com.peter.workmanager.demo.practice

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityDataSyncBinding
import com.peter.workmanager.demo.worker.SyncWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 数据同步示例 Activity
 * 
 * 演示使用 WorkManager 进行周期性数据同步。
 */
class DataSyncActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DataSyncActivity"
        private const val UNIQUE_SYNC_WORK_NAME = "data_sync_work"
    }

    private lateinit var binding: ActivityDataSyncBinding
    private lateinit var workManager: WorkManager
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()
    private var syncCount = 0
    private var lastSyncTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        setupSpinner()
        setupButtons()
        observeSyncWork()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupSpinner() {
        val intervals = arrayOf(
            "15 分钟",
            "30 分钟",
            "1 小时",
            "6 小时",
            "12 小时"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervals)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInterval.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            startPeriodicSync()
        }
        
        binding.btnSyncNow.setOnClickListener {
            syncNow()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelSync()
        }
    }

    private fun startPeriodicSync() {
        logBuilder.clear()
        appendLog("设置周期性同步...")
        
        // 获取间隔
        val intervalMinutes = when (binding.spinnerInterval.selectedItemPosition) {
            0 -> 15L
            1 -> 30L
            2 -> 60L
            3 -> 360L
            4 -> 720L
            else -> 15L
        }
        
        // 获取网络要求
        val networkType = when {
            binding.rbAny.isChecked -> NetworkType.CONNECTED
            binding.rbWifi.isChecked -> NetworkType.UNMETERED
            binding.rbMetered.isChecked -> NetworkType.METERED
            else -> NetworkType.CONNECTED
        }
        
        // 构建约束
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(networkType)
            .build()
        
        // 创建周期性同步任务
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        
        // 使用唯一名称入队
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
        appendLog("周期同步已启动")
        appendLog("间隔: $intervalMinutes 分钟")
        appendLog("网络要求: $networkType")
        
        updateButtonState(isRunning = true)
    }

    private fun syncNow() {
        appendLog("立即同步一次...")
        
        // 创建一次性同步任务
        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .build()
        
        workManager.enqueue(workRequest)
        
        // 观察这次同步
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, Observer { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val syncTime = workInfo.outputData.getString(SyncWorker.KEY_SYNC_TIME)
                        val itemsSynced = workInfo.outputData.getInt(SyncWorker.KEY_ITEMS_SYNCED, 0)
                        appendLog("同步完成: $itemsSynced 条数据")
                        syncCount++
                        lastSyncTime = syncTime
                        updateSyncInfo()
                    }
                    WorkInfo.State.FAILED -> {
                        appendLog("同步失败")
                    }
                    else -> {}
                }
            })
    }

    private fun observeSyncWork() {
        workManager.getWorkInfosForUniqueWorkLiveData(UNIQUE_SYNC_WORK_NAME)
            .observe(this, Observer { workInfos ->
                workInfos?.firstOrNull()?.let { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            updateStatus("等待同步", R.color.status_enqueued)
                        }
                        WorkInfo.State.RUNNING -> {
                            updateStatus("同步中", R.color.status_running)
                            appendLog("正在同步...")
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            updateStatus("同步完成", R.color.status_succeeded)
                            val syncTime = workInfo.outputData.getString(SyncWorker.KEY_SYNC_TIME)
                            val itemsSynced = workInfo.outputData.getInt(SyncWorker.KEY_ITEMS_SYNCED, 0)
                            syncCount++
                            lastSyncTime = syncTime
                            appendLog("同步完成: $itemsSynced 条数据")
                            updateSyncInfo()
                        }
                        WorkInfo.State.FAILED -> {
                            updateStatus("同步失败", R.color.status_failed)
                        }
                        WorkInfo.State.CANCELLED -> {
                            updateStatus("已取消", R.color.status_cancelled)
                            updateButtonState(isRunning = false)
                        }
                        else -> {}
                    }
                }
            })
    }

    private fun cancelSync() {
        workManager.cancelUniqueWork(UNIQUE_SYNC_WORK_NAME)
        appendLog("已取消周期同步")
    }

    private fun updateSyncInfo() {
        binding.tvLastSync.text = "上次同步: ${lastSyncTime ?: "从未"}"
        binding.tvSyncCount.text = "同步次数: $syncCount"
        
        // 估算下次同步时间
        val intervalMinutes = when (binding.spinnerInterval.selectedItemPosition) {
            0 -> 15L
            1 -> 30L
            2 -> 60L
            3 -> 360L
            4 -> 720L
            else -> 15L
        }
        binding.tvNextSync.text = "下次同步: 约 $intervalMinutes 分钟后"
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
