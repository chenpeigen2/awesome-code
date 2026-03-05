package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 链式任务 Worker A
 * 
 * 第一个执行的任务，输出数据传递给后续任务。
 */
class ChainWorkerA(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "ChainWorkerA"
        const val KEY_OUTPUT = "output_a"
    }

    override fun doWork(): Result {
        Log.d(TAG, "链式任务 A 开始执行")
        
        try {
            Thread.sleep(1000)
            
            val output = "A 处理完成"
            Log.d(TAG, "链式任务 A 执行完成, 输出: $output")
            
            return Result.success(
                workDataOf(KEY_OUTPUT to output)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "链式任务 A 执行失败", e)
            return Result.failure()
        }
    }
}
