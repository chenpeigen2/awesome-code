package com.peter.workmanager.demo.basic

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityOneTimeWorkBinding
import com.peter.workmanager.demo.worker.DelayWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 一次性任务示例 Activity
 * 
 * 演示 OneTimeWorkRequest 的初始延迟功能。
 */
class OneTimeWorkActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "OneTimeWorkActivity"
    }

    private lateinit var binding: ActivityOneTimeWorkBinding
    private lateinit var workManager: WorkManager
    private var workId: java.util.UUID? = null
    private var countDownTimer: CountDownTimer? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOneTimeWorkBinding.inflate(layoutInflater)
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
        appendLog("创建一次性任务...")
        
        // 获取延迟时间（秒）
        val delaySeconds = binding.etDelay.text.toString().toLongOrNull() ?: 5
        
        // 创建带初始延迟的一次性任务
        val workRequest = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .build()
        
        workId = workRequest.id
        
        // 入队任务
        workManager.enqueue(workRequest)
        
        appendLog("任务已入队, 延迟 $delaySeconds 秒后执行")
        
        // 显示倒计时
        startCountDown(delaySeconds * 1000)
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        updateButtonState(isRunning = true)
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队 (等待延迟)", R.color.status_enqueued)
                appendLog("状态: 已入队，等待延迟时间到达")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("执行中", R.color.status_running)
                appendLog("状态: 执行中...")
                binding.tvCountdown.visibility = android.view.View.GONE
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("成功", R.color.status_succeeded)
                appendLog("状态: 成功 ✓")
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

    private fun startCountDown(delayMillis: Long) {
        countDownTimer?.cancel()
        
        binding.tvCountdown.visibility = android.view.View.VISIBLE
        
        countDownTimer = object : CountDownTimer(delayMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvCountdown.text = "倒计时: ${seconds}秒"
            }

            override fun onFinish() {
                binding.tvCountdown.text = "延迟时间已到，等待执行..."
            }
        }.start()
    }

    private fun cancelWork() {
        countDownTimer?.cancel()
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

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
