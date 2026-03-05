package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

/**
 * 协程 Worker 示例
 * 
 * 使用 Kotlin 协程执行异步任务。
 * 支持挂起函数，可以安全地进行长时间运行的操作。
 * 
 * 使用方式：
 * ```kotlin
 * val workRequest = OneTimeWorkRequestBuilder<MyCoroutineWorker>()
 *     .build()
 * WorkManager.getInstance(context).enqueue(workRequest)
 * ```
 */
class MyCoroutineWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "MyCoroutineWorker"
        const val KEY_PROGRESS = "progress"
        const val KEY_RESULT = "result"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "协程任务开始执行, thread: ${Thread.currentThread().name}")
        
        return try {
            val totalSteps = 10
            
            for (step in 1..totalSteps) {
                // 使用协程延迟，不阻塞线程
                delay(1000)
                
                // 更新进度
                val progress = (step * 100) / totalSteps
                setProgress(
                    workDataOf(
                        KEY_PROGRESS to progress
                    )
                )
                
                Log.d(TAG, "协程进度: $progress%, 步骤: $step/$totalSteps")
            }
            
            Log.d(TAG, "协程任务执行完成")
            Result.success(
                workDataOf(KEY_RESULT to "协程任务执行成功!")
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "协程任务执行失败", e)
            Result.failure()
        }
    }
}
