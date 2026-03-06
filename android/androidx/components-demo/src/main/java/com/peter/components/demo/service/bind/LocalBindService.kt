package com.peter.components.demo.service.bind

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * 绑定服务示例 - 本地绑定
 *
 * ═══════════════════════════════════════════════════════════════
 * 绑定服务详解
 * ═══════════════════════════════════════════════════════════════
 *
 * 特点：
 * 1. 允许组件与服务进行交互
 * 2. 可以调用服务的方法
 * 3. 生命周期与绑定者绑定
 * 4. 所有客户端解绑后自动销毁
 *
 * 使用场景：
 * - 音乐播放器控制
 * - 实时数据获取
 * - IPC 通信（跨进程）
 *
 * 绑定过程：
 * 1. 客户端调用 bindService()
 * 2. 系统调用服务的 onCreate() 和 onBind()
 * 3. 客户端收到 IBinder 对象
 * 4. 通过 IBinder 调用服务方法
 * 5. 客户端调用 unbindService()
 * 6. 系统调用服务的 onUnbind() 和 onDestroy()
 *
 * ═══════════════════════════════════════════════════════════════
 * Binder 机制
 * ═══════════════════════════════════════════════════════════════
 *
 * Binder 是 Android IPC 的核心：
 * - 本地绑定：直接返回服务引用
 * - 远程绑定：通过 AIDL 定义接口
 *
 * 本地绑定的 Binder 实现简单：
 * 1. 继承 Binder
 * 2. 返回服务实例
 * 3. 客户端直接调用服务方法
 */
class LocalBindService : Service() {

    companion object {
        private const val TAG = "LocalBindService"
    }

    // 计数器
    private var counter = 0
    private var isCounting = false

    /**
     * Binder 实现
     *
     * 继承 Binder 类，提供获取服务实例的方法
     */
    inner class LocalBinder : Binder() {
        fun getService(): LocalBindService = this@LocalBindService
    }

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        startCounting()
    }

    /**
     * 返回 IBinder 供客户端使用
     */
    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    /**
     * 所有客户端解绑时调用
     *
     * @return true 表示下次绑定会调用 onRebind
     */
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return true
    }

    /**
     * 重新绑定时调用（onUnbind 返回 true 的情况）
     */
    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        isCounting = false
    }

    private fun startCounting() {
        isCounting = true
        Thread {
            while (isCounting) {
                Thread.sleep(1000)
                counter++
                Log.d(TAG, "Counter: $counter")
            }
        }.start()
    }

    // ==================== 暴露给客户端的方法 ====================

    /**
     * 获取当前数据
     */
    fun getData(): String {
        return "当前计数: $counter"
    }

    /**
     * 重置计数器
     */
    fun reset() {
        counter = 0
    }
}
