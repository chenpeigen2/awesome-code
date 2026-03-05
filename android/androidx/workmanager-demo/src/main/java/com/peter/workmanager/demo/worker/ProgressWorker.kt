package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

/**
 * 进度更新 Worker 示例
 * 
 * 演示如何通过 setProgress 更新任务进度。
 * 使用 CoroutineWorker 支持挂起函数。
 */
class ProgressWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "ProgressWorker"
        const val KEY_PROGRESS = "progress"
        const val KEY_MESSAGE = "message"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "进度任务开始执行")
        
        try {
            val totalSteps = 10
            
            for (step in 1..totalSteps) {
                // 检查是否被取消
                if (isStopped) {
                    Log.d(TAG, "任务被取消")
                    return Result.failure()
                }
                
                // 更新进度
                val progress = (step * 100) / totalSteps
                setProgress(
                    workDataOf(
                        KEY_PROGRESS to progress,
                        KEY_MESSAGE to "正在处理第 $step/$totalSteps 步"
                    )
                )
                
                Log.d(TAG, "进度: $progress%, 步骤: $step/$totalSteps")
                
                // 模拟工作
                delay(1000)
            }
            
            Log.d(TAG, "进度任务执行完成")
            return Result.success(
                workDataOf(KEY_MESSAGE to "任务完成!")
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "进度任务执行失败", e)
            return Result.failure()
        }
    }
}
