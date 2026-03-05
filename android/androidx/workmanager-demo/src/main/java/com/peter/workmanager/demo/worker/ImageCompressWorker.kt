package com.peter.workmanager.demo.worker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * 图片压缩 Worker 示例
 * 
 * 使用协程压缩图片，支持设置质量和尺寸。
 */
class ImageCompressWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "ImageCompressWorker"
        const val KEY_INPUT_PATH = "input_path"
        const val KEY_QUALITY = "quality"
        const val KEY_MAX_WIDTH = "max_width"
        const val KEY_MAX_HEIGHT = "max_height"
        const val KEY_OUTPUT_PATH = "output_path"
        const val KEY_ORIGINAL_SIZE = "original_size"
        const val KEY_COMPRESSED_SIZE = "compressed_size"
        const val KEY_PROGRESS = "progress"
    }

    override suspend fun doWork(): Result {
        val inputPath = inputData.getString(KEY_INPUT_PATH) ?: return Result.failure()
        val quality = inputData.getInt(KEY_QUALITY, 80)
        val maxWidth = inputData.getInt(KEY_MAX_WIDTH, 1024)
        val maxHeight = inputData.getInt(KEY_MAX_HEIGHT, 1024)
        
        Log.d(TAG, "开始压缩图片: $inputPath, 质量: $quality%, 最大尺寸: ${maxWidth}x${maxHeight}")
        
        return withContext(Dispatchers.IO) {
            try {
                val inputFile = File(inputPath)
                val originalSize = inputFile.length()
                
                // 更新进度
                setProgress(workDataOf(KEY_PROGRESS to 10))
                
                // 解码图片
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(inputPath, options)
                
                // 计算采样率
                var sampleSize = 1
                while (options.outWidth / sampleSize > maxWidth || 
                       options.outHeight / sampleSize > maxHeight) {
                    sampleSize *= 2
                }
                
                setProgress(workDataOf(KEY_PROGRESS to 30))
                
                // 加载图片
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                val bitmap = BitmapFactory.decodeFile(inputPath, decodeOptions)
                    ?: return@withContext Result.failure()
                
                setProgress(workDataOf(KEY_PROGRESS to 50))
                
                // 缩放图片
                val scaledBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                    val ratio = minOf(
                        maxWidth.toFloat() / bitmap.width,
                        maxHeight.toFloat() / bitmap.height
                    )
                    val newWidth = (bitmap.width * ratio).toInt()
                    val newHeight = (bitmap.height * ratio).toInt()
                    Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                } else {
                    bitmap
                }
                
                setProgress(workDataOf(KEY_PROGRESS to 70))
                
                // 保存压缩后的图片
                val outputFile = File(
                    applicationContext.cacheDir,
                    "compressed_${System.currentTimeMillis()}.jpg"
                )
                
                FileOutputStream(outputFile).use { output ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
                }
                
                setProgress(workDataOf(KEY_PROGRESS to 100))
                
                val compressedSize = outputFile.length()
                Log.d(TAG, "压缩完成: ${originalSize}bytes -> ${compressedSize}bytes, 压缩率: ${(100 - compressedSize * 100 / originalSize)}%")
                
                Result.success(
                    workDataOf(
                        KEY_OUTPUT_PATH to outputFile.absolutePath,
                        KEY_ORIGINAL_SIZE to originalSize,
                        KEY_COMPRESSED_SIZE to compressedSize
                    )
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "压缩失败", e)
                Result.failure()
            }
        }
    }
}
