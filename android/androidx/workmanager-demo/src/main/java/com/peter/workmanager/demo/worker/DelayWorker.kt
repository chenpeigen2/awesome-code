package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * 延迟执行 Worker 示例
 * 
 * 用于演示 OneTimeWorkRequest 的初始延迟功能。
 */
class DelayWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "DelayWorker"
    }

    override fun doWork(): Result {
        Log.d(TAG, "延迟任务开始执行, thread: ${Thread.currentThread().name}")
        
        try {
            // 模拟短时间任务
            Thread.sleep(2000)
            
            Log.d(TAG, "延迟任务执行完成")
            return Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "延迟任务执行失败", e)
            return Result.failure()
        }
    }
}
