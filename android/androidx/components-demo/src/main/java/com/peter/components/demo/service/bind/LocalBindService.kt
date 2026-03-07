package com.peter.components.demo.service.bind

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.Random

/**
 * 本地绑定服务示例
 * 
 * 知识点：
 * 1. 继承 Binder 实现本地绑定
 * 2. bindService() 绑定服务
 * 3. unbindService() 解绑服务
 * 4. ServiceConnection 监听连接状态
 * 
 * 生命周期：
 * bindService -> onCreate -> onBind -> onServiceConnected
 * unbindService -> onUnbind -> onDestroy
 */
class LocalBindService : Service() {

    companion object {
        private const val TAG = "LocalBindService"
    }

    private val binder = LocalBinder()
    private val random = Random()

    /**
     * Binder 类，用于返回 Service 实例
     */
    inner class LocalBinder : Binder() {
        fun getService(): LocalBindService = this@LocalBindService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    /**
     * 暴露给客户端的方法
     */
    fun getRandomNumber(): Int {
        return random.nextInt(100)
    }

    /**
     * 更多可以暴露的方法
     */
    fun doSomething(data: String): String {
        return "Processed: $data"
    }
}
