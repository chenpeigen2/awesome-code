package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 唯一任务 Worker 示例
 * 
 * 用于演示 UniqueWork 的功能。
 */
class UniqueWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "UniqueWorker"
        const val KEY_INSTANCE_ID = "instance_id"
    }

    override fun doWork(): Result {
        val instanceId = inputData.getString(KEY_INSTANCE_ID) ?: "unknown"
        Log.d(TAG, "唯一任务开始执行, instance: $instanceId")
        
        try {
            // 模拟长时间任务
            for (i in 1..5) {
                if (isStopped) {
                    Log.d(TAG, "任务被取消, instance: $instanceId")
                    return Result.failure()
                }
                
                Thread.sleep(2000)
                Log.d(TAG, "执行进度: $i/5, instance: $instanceId")
            }
            
            Log.d(TAG, "唯一任务执行完成, instance: $instanceId")
            return Result.success(
                workDataOf(KEY_INSTANCE_ID to instanceId)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "唯一任务执行失败", e)
            return Result.failure()
        }
    }
}
