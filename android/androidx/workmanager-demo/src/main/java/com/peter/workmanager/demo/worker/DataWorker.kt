package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

/**
 * 数据传递 Worker 示例
 * 
 * 演示如何通过 Data 对象在任务间传递数据。
 */
class DataWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "DataWorker"
        
        // 输入参数键
        const val KEY_INPUT_TEXT = "input_text"
        const val KEY_INPUT_NUMBER = "input_number"
        
        // 输出参数键
        const val KEY_OUTPUT_TEXT = "output_text"
        const val KEY_OUTPUT_LENGTH = "output_length"
    }

    override fun doWork(): Result {
        Log.d(TAG, "数据传递任务开始执行")
        
        try {
            // 获取输入数据
            val inputText = inputData.getString(KEY_INPUT_TEXT) ?: "默认输入"
            val inputNumber = inputData.getInt(KEY_INPUT_NUMBER, 0)
            
            Log.d(TAG, "接收输入: text=$inputText, number=$inputNumber")
            
            // 模拟处理
            Thread.sleep(1500)
            
            // 生成输出数据
            val outputText = "处理完成: $inputText (数字: $inputNumber)"
            val outputLength = outputText.length
            
            val outputData = workDataOf(
                KEY_OUTPUT_TEXT to outputText,
                KEY_OUTPUT_LENGTH to outputLength
            )
            
            Log.d(TAG, "输出数据: text=$outputText, length=$outputLength")
            
            return Result.success(outputData)
            
        } catch (e: Exception) {
            Log.e(TAG, "数据传递任务执行失败", e)
            return Result.failure()
        }
    }
}
