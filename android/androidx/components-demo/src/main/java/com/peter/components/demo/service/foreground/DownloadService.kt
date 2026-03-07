package com.peter.components.demo.service.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.peter.components.demo.MainActivity
import com.peter.components.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 前台服务示例
 * 
 * 知识点：
 * 1. startForeground() 显示通知，提升优先级
 * 2. Android 8.0+ 需要创建 NotificationChannel
 * 3. Android 14+ 需要声明 foregroundServiceType
 * 4. 必须在 AndroidManifest.xml 声明权限
 * 
 * 前台服务类型 (Android 14+):
 * - camera, microphone, location
 * - connectedDevice, mediaPlayback, mediaProjection
 * - phoneCall, remoteMessaging, shortService
 * - specialUse, systemExempted
 */
class DownloadService : Service() {

    companion object {
        private const val TAG = "DownloadService"
        private const val CHANNEL_ID = "download_channel"
        private const val NOTIFICATION_ID = 2001

        const val ACTION_START = "action_start"
        const val ACTION_STOP = "action_stop"

        // 下载进度 Flow，供外部监听
        private val _downloadProgress = MutableStateFlow(-1)
        val downloadProgress: StateFlow<Int> = _downloadProgress
    }

    private var downloadJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startDownload()
            ACTION_STOP -> stopDownload()
        }
        return START_NOT_STICKY
    }

    private fun startDownload() {
        // 启动前台服务
        startForeground(NOTIFICATION_ID, createNotification(0))

        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            for (progress in 0..100 step 5) {
                _downloadProgress.value = progress
                updateNotification(progress)
                delay(500)
            }
            _downloadProgress.value = -1
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun stopDownload() {
        downloadJob?.cancel()
        _downloadProgress.value = -1
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "下载服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "文件下载进度通知"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(progress: Int): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("文件下载")
            .setContentText("进度: $progress%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(pendingIntent)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(progress: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(progress))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        downloadJob?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
