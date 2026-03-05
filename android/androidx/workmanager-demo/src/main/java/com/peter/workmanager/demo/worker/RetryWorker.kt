package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 重试 Worker 示例
 * 
 * 演示任务失败后的重试机制。
 * 返回 Result.retry() 会触发重试。
 */
class RetryWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "RetryWorker"
        const val KEY_RETRY_COUNT = "retry_count"
        const val KEY_RESULT = "result"
    }

    override fun doWork(): Result {
        // 获取当前重试次数
        val runAttemptCount = runAttemptCount
        Log.d(TAG, "重试任务开始执行, 第 $runAttemptCount 次尝试")
        
        try {
            // 模拟前两次失败，第三次成功
            if (runAttemptCount < 2) {
                Log.d(TAG, "模拟失败，触发重试")
                Thread.sleep(1000)
                return Result.retry()
            }
            
            // 模拟成功
            Thread.sleep(1000)
            
            Log.d(TAG, "重试任务执行成功, 共尝试 ${runAttemptCount + 1} 次")
            return Result.success(
                workDataOf(
                    KEY_RETRY_COUNT to runAttemptCount,
                    KEY_RESULT to "成功! 共尝试 ${runAttemptCount + 1} 次"
                )
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "重试任务执行失败", e)
            return Result.failure()
        }
    }
}
