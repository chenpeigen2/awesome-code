package com.peter.components.demo.service.basic

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.peter.components.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 简单 Service 示例
 * 
 * 知识点：
 * 1. startService() 启动服务
 * 2. stopService() 或 stopSelf() 停止服务
 * 3. 生命周期：onCreate -> onStartCommand -> onDestroy
 * 4. onBind() 返回 null 表示不支持绑定
 * 
 * 注意：
 * - 默认运行在主线程，耗时操作需要开线程
 * - 多次 startService 只会多次调用 onStartCommand
 * - 服务需要要在 AndroidManifest.xml 中注册
 */
class SimpleService : Service() {

    companion object {
        private const val TAG = "SimpleService"
        private const val NOTIFICATION_ID = 1001
    }

    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: startId=$startId")

        // 在后台执行任务
        job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..10) {
                Log.d(TAG, "后台任务执行中: $i/10")
                delay(1000)
            }
            // 任务完成后自动停止
            stopSelf(startId)
        }

        // START_STICKY: 服务被杀死后会重建
        // START_NOT_STICKY: 服务被杀死后不会重建
        // START_REDELIVER_INTENT: 服务被杀死后重建并重新传递最后一个 Intent
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // 不支持绑定
        return null
    }
}
