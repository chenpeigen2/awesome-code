package com.peter.components.demo.service.basic

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * 基础 Service 示例
 *
 * ═══════════════════════════════════════════════════════════════
 * Service 生命周期
 * ═══════════════════════════════════════════════════════════════
 *
 * startService 启动：
 * onCreate() → onStartCommand() → (运行中) → onDestroy()
 *
 * bindService 绑定：
 * onCreate() → onBind() → (连接中) → onUnbind() → onDestroy()
 *
 * 混合启动（先 start 后 bind）：
 * onCreate() → onStartCommand() → onBind() → onUnbind() → onDestroy()
 * 注：需要同时 stopSelf 和 unbind 才会销毁
 *
 * ═══════════════════════════════════════════════════════════════
 * onStartCommand 返回值
 * ═══════════════════════════════════════════════════════════════
 *
 * START_STICKY：
 * - 服务被杀死后会重建，但 Intent 为 null
 * - 适合不依赖启动命令的服务（如音乐播放）
 *
 * START_NOT_STICKY：
 * - 服务被杀死后不会重建
 * - 适合有明确开始/结束的任务
 *
 * START_REDELIVER_INTENT：
 * - 服务被杀死后重建，并重新传递最后一个 Intent
 * - 适合需要保证完成的任务（如文件下载）
 */
class SimpleService : Service() {

    companion object {
        private const val TAG = "SimpleService"
    }

    private var counter = 0
    private var isRunning = false
    private lateinit var workerThread: Thread

    /**
     * 服务创建时调用
     * 整个生命周期只调用一次
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    /**
     * 每次 startService 都会调用
     *
     * @param intent 启动服务的 Intent
     * @param flags 启动标志
     * @param startId 启动请求 ID
     * @return 返回值决定服务被杀死后的行为
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: intent=$intent, flags=$flags, startId=$startId")

        val command = intent?.getStringExtra("command") ?: "UNKNOWN"
        Log.d(TAG, "收到命令: $command")

        // 启动后台任务
        if (!isRunning) {
            startBackgroundTask()
        }

        // 返回 START_REDELIVER_INTENT 保证任务完成
        return START_REDELIVER_INTENT
    }

    /**
     * 绑定服务时调用
     * 返回 null 表示不支持绑定
     */
    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return null
    }

    /**
     * 服务销毁时调用
     * 用于释放资源
     */
    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        isRunning = false
        if (::workerThread.isInitialized && workerThread.isAlive) {
            workerThread.interrupt()
        }
        super.onDestroy()
    }

    private fun startBackgroundTask() {
        isRunning = true
        workerThread = Thread {
            Log.d(TAG, "后台任务开始")
            try {
                while (isRunning && counter < 10) {
                    Thread.sleep(1000)
                    counter++
                    Log.d(TAG, "后台任务进行中: $counter/10")

                    // 模拟任务完成
                    if (counter >= 10) {
                        Log.d(TAG, "后台任务完成")
                        // 可以在这里调用 stopSelf() 自动停止
                        // stopSelf()
                    }
                }
            } catch (e: InterruptedException) {
                Log.d(TAG, "后台任务被中断")
            }
        }.apply { start() }
    }
}
