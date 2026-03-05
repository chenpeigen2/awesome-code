package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 链式任务 Worker B
 * 
 * 接收 A 的输出，处理后输出给后续任务。
 */
class ChainWorkerB(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "ChainWorkerB"
        const val KEY_INPUT = "output_a"
        const val KEY_OUTPUT = "output_b"
    }

    override fun doWork(): Result {
        Log.d(TAG, "链式任务 B 开始执行")
        
        try {
            // 获取前置任务的输出
            val inputFromA = inputData.getString(KEY_INPUT) ?: "无输入"
            Log.d(TAG, "从 A 接收: $inputFromA")
            
            Thread.sleep(1000)
            
            val output = "$inputFromA -> B 处理完成"
            Log.d(TAG, "链式任务 B 执行完成, 输出: $output")
            
            return Result.success(
                workDataOf(KEY_OUTPUT to output)
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "链式任务 B 执行失败", e)
            return Result.failure()
        }
    }
}
