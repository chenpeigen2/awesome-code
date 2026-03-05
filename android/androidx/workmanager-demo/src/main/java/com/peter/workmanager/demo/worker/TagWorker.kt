package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 标签任务 Worker 示例
 * 
 * 用于演示通过标签管理一组任务。
 */
class TagWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "TagWorker"
        const val KEY_TASK_NAME = "task_name"
    }

    override fun doWork(): Result {
        val taskName = inputData.getString(KEY_TASK_NAME) ?: "unknown"
        Log.d(TAG, "标签任务开始执行: $taskName")
        
        try {
            Thread.sleep(3000)
            
            Log.d(TAG, "标签任务执行完成: $taskName")
            return Result.success(
                workDataOf(KEY_TASK_NAME to taskName)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "标签任务执行失败: $taskName", e)
            return Result.failure()
        }
    }
}
