package com.peter.workmanager.demo.practice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityImageCompressBinding
import com.peter.workmanager.demo.worker.ImageCompressWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 图片压缩示例 Activity
 * 
 * 演示使用 WorkManager 压缩图片。
 */
class ImageCompressActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ImageCompressActivity"
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }

    private lateinit var binding: ActivityImageCompressBinding
    private lateinit var workManager: WorkManager
    private var selectedImageUri: Uri? = null
    private var workId: java.util.UUID? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageCompressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        setupSeekBar()
        setupButtons()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupSeekBar() {
        binding.seekBarQuality.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvQuality.text = "质量: $progress%"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    private fun setupButtons() {
        binding.btnSelectImage.setOnClickListener {
            selectImage()
        }
        
        binding.btnStart.setOnClickListener {
            startCompression()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelCompression()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                appendLog("已选择图片: $it")
                binding.btnStart.isEnabled = true
            }
        }
    }

    private fun startCompression() {
        logBuilder.clear()
        
        val imageUri = selectedImageUri
        if (imageUri == null) {
            appendLog("请先选择图片")
            return
        }
        
        appendLog("开始图片压缩...")
        
        val quality = binding.seekBarQuality.progress
        val maxWidth = binding.etWidth.text.toString().toIntOrNull() ?: 1024
        val maxHeight = binding.etHeight.text.toString().toIntOrNull() ?: 1024
        
        appendLog("配置: 质量=$quality%, 最大尺寸=${maxWidth}x${maxHeight}")
        
        // 将 Uri 复制到缓存目录
        val inputFile = copyUriToCache(imageUri)
        
        val inputData = workDataOf(
            ImageCompressWorker.KEY_INPUT_PATH to inputFile.absolutePath,
            ImageCompressWorker.KEY_QUALITY to quality,
            ImageCompressWorker.KEY_MAX_WIDTH to maxWidth,
            ImageCompressWorker.KEY_MAX_HEIGHT to maxHeight
        )
        
        val workRequest = OneTimeWorkRequestBuilder<ImageCompressWorker>()
            .setInputData(inputData)
            .build()
        
        workId = workRequest.id
        
        workManager.enqueue(workRequest)
        
        appendLog("压缩任务已入队")
        
        // 观察任务状态
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, workInfoObserver)
        
        updateButtonState(isRunning = true)
    }

    private fun copyUriToCache(uri: Uri): java.io.File {
        val inputFile = java.io.File(cacheDir, "input_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            inputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return inputFile
    }

    private val workInfoObserver = Observer<WorkInfo?> { workInfo ->
        // 更新进度
        val progress = workInfo?.progress?.getInt(ImageCompressWorker.KEY_PROGRESS, 0) ?: 0
        binding.progressBar.progress = progress
        binding.tvProgress.text = "进度: $progress%"
        
        when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> {
                updateStatus("已入队", R.color.status_enqueued)
                appendLog("状态: 已入队")
            }
            WorkInfo.State.RUNNING -> {
                updateStatus("压缩中", R.color.status_running)
                appendLog("状态: 压缩中 ($progress%)")
            }
            WorkInfo.State.SUCCEEDED -> {
                updateStatus("完成", R.color.status_succeeded)
                val outputPath = workInfo.outputData.getString(ImageCompressWorker.KEY_OUTPUT_PATH)
                val originalSize = workInfo.outputData.getLong(ImageCompressWorker.KEY_ORIGINAL_SIZE, 0)
                val compressedSize = workInfo.outputData.getLong(ImageCompressWorker.KEY_COMPRESSED_SIZE, 0)
                
                appendLog("状态: 压缩完成 ✓")
                appendLog("输出路径: $outputPath")
                appendLog("原始大小: ${originalSize / 1024.0} KB")
                appendLog("压缩后: ${compressedSize / 1024.0} KB")
                appendLog("压缩率: ${100 - compressedSize * 100 / originalSize}%")
                
                updateButtonState(isRunning = false)
            }
            WorkInfo.State.FAILED -> {
                updateStatus("失败", R.color.status_failed)
                appendLog("状态: 压缩失败 ✗")
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

    private fun cancelCompression() {
        workId?.let {
            workManager.cancelWorkById(it)
            appendLog("请求取消压缩...")
        }
    }

    private fun updateStatus(status: String, colorRes: Int) {
        binding.tvStatus.text = status
        binding.tvStatus.setTextColor(getColor(colorRes))
    }

    private fun updateButtonState(isRunning: Boolean) {
        binding.btnSelectImage.isEnabled = !isRunning
        binding.btnStart.isEnabled = !isRunning && selectedImageUri != null
        binding.btnCancel.isEnabled = isRunning
    }

    private fun appendLog(message: String) {
        val timestamp = dateFormat.format(Date())
        logBuilder.append("[$timestamp] $message\n")
        binding.tvLog.text = logBuilder.toString()
        Log.d(TAG, message)
    }
}
