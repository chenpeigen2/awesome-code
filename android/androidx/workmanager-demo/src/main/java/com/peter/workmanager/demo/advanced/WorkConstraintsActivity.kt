package com.peter.workmanager.demo.advanced

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityWorkConstraintsBinding
import com.peter.workmanager.demo.worker.ConstraintsWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 约束条件示例 Activity
 * 
 * 演示如何设置任务执行的约束条件。
 */
class WorkConstraintsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WorkConstraintsActivity"
    }

    private lateinit var binding: ActivityWorkConstraintsBinding
    private lateinit var workManager: WorkManager
    private var workId: java.util.UUID? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkConstraintsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        setupNetworkSpinner()
        setupButtons()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupNetworkSpinner() {
        val networkTypes = arrayOf(
            "NOT_REQUIRED - 无网络要求",
            "CONNECTED - 任意网络",
            "UNMETERED - 非计费网络",
            "NOT_ROAMING - 非漫游网络",
            "METERED - 计费网络 (移动数据)"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, networkTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNetworkType.adapter = adapter
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
        appendLog("创建带约束的任务...")
        
        // 构建约束条件
        val constraints = buildConstraints()
        
        appendLog("约束条件: $constraints")
        
        // 创建任务请求
        val workRequest = OneTimeWorkRequestBuilder<ConstraintsWorker>()
            .setConstraints(constraints)
            .build()
        
        workId = workRequest.id
        
        // 入队任务
        workManager.enqueue(workRequest)
        
        appendLog("任务已入队")
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        updateButtonState(isRunning = true)
    }

    private fun buildConstraints(): Constraints {
        val builder = Constraints.Builder()
        
        // 网络约束
        val networkType = when (binding.spinnerNetworkType.selectedItemPosition) {
            0 -> NetworkType.NOT_REQUIRED
            1 -> NetworkType.CONNECTED
            2 -> NetworkType.UNMETERED
            3 -> NetworkType.NOT_ROAMING
            4 -> NetworkType.METERED
            else -> NetworkType.NOT_REQUIRED
        }
        builder.setRequiredNetworkType(networkType)
        
        // 其他约束
        if (binding.cbBatteryNotLow.isChecked) {
            builder.setRequiresBatteryNotLow(true)
        }
        if (binding.cbRequiresCharging.isChecked) {
            builder.setRequiresCharging(true)
        }
        if (binding.cbStorageNotLow.isChecked) {
            builder.setRequiresStorageNotLow(true)
        }
        if (binding.cbRequiresDeviceIdle.isChecked) {
            builder.setRequiresDeviceIdle(true)
        }
        
        return builder.build()
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                appendLog("状态: 已入队，等待满足约束条件")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("执行中", R.color.status_running)
                appendLog("状态: 执行中...")
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("成功", R.color.status_succeeded)
                val result = workInfo.outputData.getString(ConstraintsWorker.KEY_RESULT)
                appendLog("状态: 成功 ✓")
                appendLog("结果: $result")
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
