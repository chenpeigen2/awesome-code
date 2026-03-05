package com.peter.workmanager.demo.basic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityWorkDataBinding
import com.peter.workmanager.demo.worker.DataWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 数据传递示例 Activity
 * 
 * 演示如何使用 Data 对象在任务间传递数据。
 */
class WorkDataActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WorkDataActivity"
    }

    private lateinit var binding: ActivityWorkDataBinding
    private lateinit var workManager: WorkManager
    private var workId: java.util.UUID? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkDataBinding.inflate(layoutInflater)
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
        
        binding.btnGetResult.setOnClickListener {
            getResult()
        }
    }

    private fun startWork() {
        logBuilder.clear()
        appendLog("创建数据传递任务...")
        
        // 获取输入数据
        val inputText = binding.etInput.text.toString()
        
        // 创建输入数据
        val inputData = workDataOf(
            DataWorker.KEY_INPUT_TEXT to inputText,
            DataWorker.KEY_INPUT_NUMBER to (1..100).random()
        )
        
        appendLog("输入数据: text=\"$inputText\"")
        
        // 创建任务请求
        val workRequest = OneTimeWorkRequestBuilder<DataWorker>()
            .setInputData(inputData)
            .build()
        
        workId = workRequest.id
        
        // 入队任务
        workManager.enqueue(workRequest)
        
        appendLog("任务已入队")
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        binding.btnStart.isEnabled = false
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        when (workInfo?.state) {
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
                appendLog("状态: 成功 ✓")
                binding.btnGetResult.isEnabled = true
                // 自动获取结果
                getResult()
            }
            WorkInfo.State.FAILED -> {
                updateStatus("失败", R.color.status_failed)
                appendLog("状态: 失败 ✗")
                binding.btnStart.isEnabled = true
            }
            WorkInfo.State.CANCELLED -> {
                updateStatus("已取消", R.color.status_cancelled)
                appendLog("状态: 已取消")
                binding.btnStart.isEnabled = true
            }
            else -> {}
        }
    }

    private fun getResult() {
        workId?.let { id ->
            workManager.getWorkInfoById(id).get()?.let { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    val outputText = workInfo.outputData.getString(DataWorker.KEY_OUTPUT_TEXT)
                    val outputLength = workInfo.outputData.getInt(DataWorker.KEY_OUTPUT_LENGTH, 0)
                    
                    val resultText = """
                        输出文本: $outputText
                        文本长度: $outputLength
                    """.trimIndent()
                    
                    binding.tvOutput.text = resultText
                    appendLog("获取结果成功")
                }
            }
        }
    }

    private fun updateStatus(status: String, colorRes: Int) {
        binding.tvStatus.text = status
        binding.tvStatus.setTextColor(getColor(colorRes))
    }

    private fun appendLog(message: String) {
        val timestamp = dateFormat.format(Date())
        logBuilder.append("[$timestamp] $message\n")
        binding.tvLog.text = logBuilder.toString()
        Log.d(TAG, message)
    }
}
