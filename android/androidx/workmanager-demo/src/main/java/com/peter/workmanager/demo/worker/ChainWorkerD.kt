package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 链式任务 Worker D
 * 
 * 与 B 并行执行，接收 A 的输出。
 */
class ChainWorkerD(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "ChainWorkerD"
        const val KEY_INPUT = "output_a"
        const val KEY_OUTPUT = "output_d"
    }

    override fun doWork(): Result {
        Log.d(TAG, "链式任务 D 开始执行")
        
        try {
            val inputFromA = inputData.getString(KEY_INPUT) ?: "无输入"
            Log.d(TAG, "从 A 接收: $inputFromA")
            
            Thread.sleep(1500)
            
            val output = "$inputFromA -> D 处理完成"
            Log.d(TAG, "链式任务 D 执行完成, 输出: $output")
            
            return Result.success(
                workDataOf(KEY_OUTPUT to output)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "链式任务 D 执行失败", e)
            return Result.failure()
        }
    }
}
