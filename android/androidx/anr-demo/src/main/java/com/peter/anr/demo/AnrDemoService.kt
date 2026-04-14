package com.peter.anr.demo

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * 用于演示 Service ANR 的 Service
 *
 * 注意：Service 的生命周期方法（onCreate, onStartCommand, onDestroy）都在主线程执行
 * 如果在这些方法中做耗时操作，会导致 ANR
 */
class AnrDemoService : Service() {

    var shouldBlock = false

    override fun onCreate() {
        super.onCreate()
        if (shouldBlock) {
            // 模拟耗时操作 → 会导致 ANR
            Thread.sleep(30_000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra("block", false) == true) {
            // 模拟耗时操作 → 会导致 ANR
            Thread.sleep(30_000)
        }
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
