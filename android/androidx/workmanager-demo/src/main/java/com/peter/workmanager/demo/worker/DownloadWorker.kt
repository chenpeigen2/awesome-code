package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * 文件下载 Worker 示例
 * 
 * 使用协程下载文件，支持进度更新。
 */
class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "DownloadWorker"
        const val KEY_URL = "url"
        const val KEY_PROGRESS = "progress"
        const val KEY_DOWNLOADED = "downloaded"
        const val KEY_TOTAL = "total"
        const val KEY_FILE_PATH = "file_path"
    }

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_URL) ?: return Result.failure()
        Log.d(TAG, "开始下载: $url")
        
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection()
                connection.connect()
                
                val totalSize = connection.contentLengthLong
                val fileName = url.substringAfterLast("/")
                val outputFile = File(applicationContext.cacheDir, fileName)
                
                Log.d(TAG, "文件大小: $totalSize bytes, 保存到: ${outputFile.absolutePath}")
                
                var downloaded = 0L
                val buffer = ByteArray(8192)
                
                connection.getInputStream().use { input ->
                    FileOutputStream(outputFile).use { output ->
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloaded += bytesRead
                            
                            // 更新进度
                            val progress = if (totalSize > 0) {
                                ((downloaded * 100) / totalSize).toInt()
                            } else {
                                -1
                            }
                            
                            setProgress(
                                workDataOf(
                                    KEY_PROGRESS to progress,
                                    KEY_DOWNLOADED to downloaded,
                                    KEY_TOTAL to totalSize
                                )
                            )
                        }
                    }
                }
                
                Log.d(TAG, "下载完成: ${outputFile.absolutePath}")
                
                Result.success(
                    workDataOf(
                        KEY_FILE_PATH to outputFile.absolutePath,
                        KEY_DOWNLOADED to downloaded
                    )
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "下载失败", e)
                Result.failure()
            }
        }
    }
}
