package com.peter.workmanager.demo.advanced

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityWorkChainBinding
import com.peter.workmanager.demo.worker.ChainWorkerA
import com.peter.workmanager.demo.worker.ChainWorkerB
import com.peter.workmanager.demo.worker.ChainWorkerC
import com.peter.workmanager.demo.worker.ChainWorkerD
import com.peter.workmanager.demo.worker.ChainWorkerE
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 链式任务示例 Activity
 * 
 * 演示如何使用 WorkContinuation 创建任务链。
 */
class WorkChainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WorkChainActivity"
    }

    private lateinit var binding: ActivityWorkChainBinding
    private lateinit var workManager: WorkManager
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkChainBinding.inflate(layoutInflater)
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
            startChain()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelAll()
        }
    }

    private fun startChain() {
        logBuilder.clear()
        resetTaskStatuses()
        appendLog("创建链式任务...")
        
        // 创建各个任务
        val workA = OneTimeWorkRequestBuilder<ChainWorkerA>().build()
        val workB = OneTimeWorkRequestBuilder<ChainWorkerB>().build()
        val workC = OneTimeWorkRequestBuilder<ChainWorkerC>().build()
        val workD = OneTimeWorkRequestBuilder<ChainWorkerD>().build()
        val workE = OneTimeWorkRequestBuilder<ChainWorkerE>().build()
        
        appendLog("任务 A ID: ${workA.id}")
        appendLog("任务 B ID: ${workB.id}")
        appendLog("任务 C ID: ${workC.id}")
        appendLog("任务 D ID: ${workD.id}")
        appendLog("任务 E ID: ${workE.id}")
        
        // 创建任务链
        // 结构: A -> B -> C
        //         -> D -> E
        workManager
            .beginWith(workA)
            .then(listOf(workB, workD))  // B 和 D 并行执行
            .then(listOf(workC, workE))  // C 和 E 等待各自前置任务完成
            .enqueue()
        
        appendLog("任务链已入队")
        
        // 观察所有任务状态
        observeWork(workA.id, binding.tvTaskA, "A")
        observeWork(workB.id, binding.tvTaskB, "B")
        observeWork(workC.id, binding.tvTaskC, "C")
        observeWork(workD.id, binding.tvTaskD, "D")
        observeWork(workE.id, binding.tvTaskE, "E")
        
        updateButtonState(isRunning = true)
    }

    private fun observeWork(workId: java.util.UUID, textView: android.widget.TextView, name: String) {
        workManager.getWorkInfoByIdLiveData(workId).observe(this, Observer<WorkInfo?> { workInfo ->
            val status = when (workInfo?.state) {
                WorkInfo.State.ENQUEUED -> "等待开始"
                WorkInfo.State.BLOCKED -> "被阻塞"
                WorkInfo.State.RUNNING -> "执行中"
                WorkInfo.State.SUCCEEDED -> "完成 ✓"
                WorkInfo.State.FAILED -> "失败 ✗"
                WorkInfo.State.CANCELLED -> "已取消"
                else -> "未知"
            }
            
            textView.text = "Task$name: $status"
            
            val color = when (workInfo?.state) {
                WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> getColor(R.color.status_enqueued)
                WorkInfo.State.RUNNING -> getColor(R.color.status_running)
                WorkInfo.State.SUCCEEDED -> getColor(R.color.status_succeeded)
                WorkInfo.State.FAILED -> getColor(R.color.status_failed)
                WorkInfo.State.CANCELLED -> getColor(R.color.status_cancelled)
                else -> getColor(R.color.status_idle)
            }
            textView.setTextColor(color)
            
            appendLog("Task$name: $status")
        })
    }

    private fun resetTaskStatuses() {
        binding.tvTaskA.text = "TaskA: 等待开始"
        binding.tvTaskB.text = "TaskB: 等待开始"
        binding.tvTaskC.text = "TaskC: 等待开始"
        binding.tvTaskD.text = "TaskD: 等待开始"
        binding.tvTaskE.text = "TaskE: 等待开始"
        
        val idleColor = getColor(R.color.status_idle)
        binding.tvTaskA.setTextColor(idleColor)
        binding.tvTaskB.setTextColor(idleColor)
        binding.tvTaskC.setTextColor(idleColor)
        binding.tvTaskD.setTextColor(idleColor)
        binding.tvTaskE.setTextColor(idleColor)
    }

    private fun cancelAll() {
        workManager.cancelAllWork()
        appendLog("取消所有任务...")
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
