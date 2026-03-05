package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 约束条件 Worker 示例
 * 
 * 演示带约束条件的任务执行。
 */
class ConstraintsWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "ConstraintsWorker"
        const val KEY_RESULT = "result"
    }

    override fun doWork(): Result {
        Log.d(TAG, "约束条件任务开始执行")
        
        try {
            // 模拟任务
            Thread.sleep(2000)
            
            val result = "任务在满足约束条件后执行成功"
            Log.d(TAG, result)
            
            return Result.success(
                workDataOf(KEY_RESULT to result)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "约束条件任务执行失败", e)
            return Result.failure()
        }
    }
}
