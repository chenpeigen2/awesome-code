package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 周期性 Worker 示例
 * 
 * 用于演示 PeriodicWorkRequest 的周期执行功能。
 * 注意：最小间隔时间为 15 分钟。
 */
class PeriodicWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "PeriodicWorker"
        const val KEY_EXECUTION_COUNT = "execution_count"
    }

    override fun doWork(): Result {
        val currentTime = System.currentTimeMillis()
        Log.d(TAG, "周期任务开始执行, 时间: ${java.util.Date(currentTime)}")
        
        try {
            // 模拟短时间任务
            Thread.sleep(1000)
            
            // 返回执行时间
            val outputData = workDataOf(
                KEY_EXECUTION_COUNT to currentTime
            )
            
            Log.d(TAG, "周期任务执行完成")
            return Result.success(outputData)
            
        } catch (e: Exception) {
            Log.e(TAG, "周期任务执行失败", e)
            return Result.failure()
        }
    }
}
