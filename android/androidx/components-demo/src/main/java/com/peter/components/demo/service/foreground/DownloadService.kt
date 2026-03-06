package com.peter.components.demo.service.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.peter.components.demo.R
import com.peter.components.demo.service.ServiceMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 前台服务示例 - 下载服务
 *
 * ═══════════════════════════════════════════════════════════════
 * 前台服务详解
 * ═══════════════════════════════════════════════════════════════
 *
 * 特点：
 * 1. 必须显示通知（用户可见）
 * 2. 不受后台限制影响
 * 3. 系统优先级更高，不易被杀死
 * 4. 适合需要长时间运行的任务
 *
 * 使用场景：
 * - 音乐播放
 * - 文件下载/上传
 * - 位置追踪
 * - VoIP 通话
 *
 * Android 14+ 要求：
 * 1. 必须声明 foregroundServiceType
 * 2. 需要对应的权限
 * 3. 调用 startForeground 时指定类型
 *
 * ═══════════════════════════════════════════════════════════════
 * 通知渠道 (NotificationChannel)
 * ═══════════════════════════════════════════════════════════════
 *
 * Android 8.0+ 必须创建通知渠道：
 * - id：渠道唯一标识
 * - name：用户可见的渠道名称
 * - importance：通知重要性级别
 */
class DownloadService : Service() {

    companion object {
        private const val TAG = "DownloadService"
        private const val CHANNEL_ID = "download_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private val binder = LocalBinder()
    private var downloadJob: Job? = null
    private var progress = 0
    private var isDownloading = false

    inner class LocalBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val downloadUrl = intent?.getStringExtra("download_url") ?: ""

        if (!isDownloading) {
            startDownload(downloadUrl)
        }

        /**
         * 前台服务启动
         *
         * Android 8.0+ 必须在 5 秒内调用 startForeground
         * 否则会抛出 ANR 或停止服务
         *
         * Android 14+ 需要指定 foregroundServiceType
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(0),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification(0))
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        downloadJob?.cancel()
        super.onDestroy()
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "下载服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示下载进度"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * 创建下载通知
     */
    private fun createNotification(progress: Int): Notification {
        // 点击通知打开的 Activity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, ServiceMainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("文件下载")
            .setContentText("下载进度: $progress%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(pendingIntent)
            .setProgress(100, progress, false)
            .setOngoing(true) // 不可滑动删除
            .build()
    }

    /**
     * 更新通知
     */
    private fun updateNotification(progress: Int) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(progress))
    }

    /**
     * 开始下载任务
     */
    private fun startDownload(url: String) {
        isDownloading = true

        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "开始下载: $url")

            while (isActive && progress < 100) {
                delay(500) // 模拟下载延迟
                progress += 5
                Log.d(TAG, "下载进度: $progress%")
                updateNotification(progress)
            }

            if (progress >= 100) {
                Log.d(TAG, "下载完成")
                isDownloading = false
                // 下载完成，停止前台服务
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    /**
     * 获取当前进度
     */
    fun getProgress(): Int = progress

    /**
     * 取消下载
     */
    fun cancelDownload() {
        downloadJob?.cancel()
        isDownloading = false
        progress = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}
