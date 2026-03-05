package com.peter.workmanager.demo.advanced

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityUniqueWorkBinding
import com.peter.workmanager.demo.worker.UniqueWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 唯一任务示例 Activity
 * 
 * 演示 UniqueWork 的各种策略。
 */
class UniqueWorkActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "UniqueWorkActivity"
        private const val UNIQUE_WORK_NAME = "unique_sync_work"
    }

    private lateinit var binding: ActivityUniqueWorkBinding
    private lateinit var workManager: WorkManager
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()
    private var instanceCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniqueWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        setupButtons()
        observeUniqueWork()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            startUniqueWork()
        }
        
        binding.btnStartAgain.setOnClickListener {
            startUniqueWorkAgain()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelUniqueWork()
        }
    }

    private fun startUniqueWork() {
        logBuilder.clear()
        instanceCounter++
        appendLog("创建唯一任务...")
        
        val inputData = workDataOf(
            UniqueWorker.KEY_INSTANCE_ID to "instance_$instanceCounter"
        )
        
        val workRequest = OneTimeWorkRequestBuilder<UniqueWorker>()
            .setInputData(inputData)
            .build()
        
        // 使用唯一名称入队
        workManager.enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,  // 首次使用 KEEP
            workRequest
        )
        
        appendLog("唯一任务已入队 (策略: KEEP)")
        appendLog("实例 ID: instance_$instanceCounter")
        
        updateButtonState(isRunning = true)
    }

    private fun startUniqueWorkAgain() {
        instanceCounter++
        appendLog("---")
        appendLog("尝试再次入队唯一任务...")
        
        val policy = getSelectedPolicy()
        
        val inputData = workDataOf(
            UniqueWorker.KEY_INSTANCE_ID to "instance_$instanceCounter"
        )
        
        val workRequest = OneTimeWorkRequestBuilder<UniqueWorker>()
            .setInputData(inputData)
            .build()
        
        // 使用选定的策略
        workManager.enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            policy,
            workRequest
        )
        
        appendLog("策略: $policy")
        appendLog("新实例 ID: instance_$instanceCounter")
    }

    private fun getSelectedPolicy(): ExistingWorkPolicy {
        return when {
            binding.rbReplace.isChecked -> ExistingWorkPolicy.REPLACE
            binding.rbKeep.isChecked -> ExistingWorkPolicy.KEEP
            binding.rbAppend.isChecked -> ExistingWorkPolicy.APPEND
            else -> ExistingWorkPolicy.KEEP
        }
    }

    private fun observeUniqueWork() {
        workManager.getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME)
            .observe(this, Observer { workInfos ->
                workInfos?.firstOrNull()?.let { workInfo ->
                    updateWorkInfo(workInfo)
                }
            })
    }

    private fun updateWorkInfo(workInfo: WorkInfo) {
        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                appendLog("状态: 已入队")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("执行中", R.color.status_running)
                appendLog("状态: 执行中...")
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("成功", R.color.status_succeeded)
                val instanceId = workInfo.outputData.getString(UniqueWorker.KEY_INSTANCE_ID)
                appendLog("状态: 成功 ✓ (实例: $instanceId)")
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

    private fun cancelUniqueWork() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
        appendLog("请求取消唯一任务...")
    }

    private fun updateStatus(status: String, colorRes: Int) {
        binding.tvStatus.text = status
        binding.tvStatus.setTextColor(getColor(colorRes))
    }

    private fun updateButtonState(isRunning: Boolean) {
        binding.btnStart.isEnabled = !isRunning
        binding.btnStartAgain.isEnabled = isRunning
        binding.btnCancel.isEnabled = isRunning
    }

    private fun appendLog(message: String) {
        val timestamp = dateFormat.format(Date())
        logBuilder.append("[$timestamp] $message\n")
        binding.tvLog.text = logBuilder.toString()
        Log.d(TAG, message)
    }
}
