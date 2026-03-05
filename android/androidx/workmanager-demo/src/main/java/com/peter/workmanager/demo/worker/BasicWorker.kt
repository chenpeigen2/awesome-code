package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 基础 Worker 示例
 * 
 * 最简单的 Worker 实现，继承自 Worker 类。
 * 适用于执行同步阻塞任务。
 * 
 * 使用方式：
 * ```kotlin
 * val workRequest = OneTimeWorkRequestBuilder<BasicWorker>()
 *     .build()
 * WorkManager.getInstance(context).enqueue(workRequest)
 * ```
 */
class BasicWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "BasicWorker"
        const val KEY_RESULT = "result"
    }

    override fun doWork(): Result {
        Log.d(TAG, "开始执行基础任务, thread: ${Thread.currentThread().name}")
        
        try {
            // 模拟耗时操作
            for (i in 1..5) {
                // 检查是否被取消
                if (isStopped) {
                    Log.d(TAG, "任务被取消")
                    return Result.failure()
                }
                
                Thread.sleep(1000)
                Log.d(TAG, "执行进度: $i/5")
            }
            
            // 返回成功结果
            val outputData = workDataOf(KEY_RESULT to "任务执行成功!")
            Log.d(TAG, "任务执行完成")
            
            return Result.success(outputData)
            
        } catch (e: Exception) {
            Log.e(TAG, "任务执行失败", e)
            return Result.failure()
        }
    }
}
