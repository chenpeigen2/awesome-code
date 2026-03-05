package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 链式任务 Worker C
 * 
 * 接收 B 的输出。
 */
class ChainWorkerC(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "ChainWorkerC"
        const val KEY_INPUT = "output_b"
        const val KEY_OUTPUT = "output_c"
    }

    override fun doWork(): Result {
        Log.d(TAG, "链式任务 C 开始执行")
        
        try {
            val inputFromB = inputData.getString(KEY_INPUT) ?: "无输入"
            Log.d(TAG, "从 B 接收: $inputFromB")
            
            Thread.sleep(1000)
            
            val output = "$inputFromB -> C 处理完成"
            Log.d(TAG, "链式任务 C 执行完成, 输出: $output")
            
            return Result.success(
                workDataOf(KEY_OUTPUT to output)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "链式任务 C 执行失败", e)
            return Result.failure()
        }
    }
}
