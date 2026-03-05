package com.peter.workmanager.demo.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * 数据同步 Worker 示例
 * 
 * 模拟从服务器同步数据。
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
        const val KEY_SYNC_TIME = "sync_time"
        const val KEY_SYNC_COUNT = "sync_count"
        const val KEY_ITEMS_SYNCED = "items_synced"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "开始数据同步, thread: ${Thread.currentThread().name}")
        
        return withContext(Dispatchers.IO) {
            try {
                // 模拟网络请求延迟
                delay(2000)
                
                // 模拟同步数据
                val itemsSynced = (10..100).random()
                
                delay(1000)
                
                val syncTime = Date().toString()
                Log.d(TAG, "数据同步完成, 同步 $itemsSynced 条数据")
                
                Result.success(
                    workDataOf(
                        KEY_SYNC_TIME to syncTime,
                        KEY_ITEMS_SYNCED to itemsSynced
                    )
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "数据同步失败", e)
                Result.failure()
            }
        }
    }
}
