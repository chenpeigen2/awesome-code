package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 链式任务 Worker E
 * 
 * 接收 D 的输出。
 */
class ChainWorkerE(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "ChainWorkerE"
        const val KEY_INPUT = "output_d"
        const val KEY_OUTPUT = "output_e"
    }

    override fun doWork(): Result {
        Log.d(TAG, "链式任务 E 开始执行")
        
        try {
            val inputFromD = inputData.getString(KEY_INPUT) ?: "无输入"
            Log.d(TAG, "从 D 接收: $inputFromD")
            
            Thread.sleep(1000)
            
            val output = "$inputFromD -> E 处理完成"
            Log.d(TAG, "链式任务 E 执行完成, 输出: $output")
            
            return Result.success(
                workDataOf(KEY_OUTPUT to output)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "链式任务 E 执行失败", e)
            return Result.failure()
        }
    }
}
