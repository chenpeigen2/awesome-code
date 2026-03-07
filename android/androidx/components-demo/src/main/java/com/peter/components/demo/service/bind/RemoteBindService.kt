package com.peter.components.demo.service.bind

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.peter.components.demo.service.IRemoteService
import java.util.concurrent.TimeUnit

/**
 * 远程绑定服务示例 (AIDL)
 *
 * 知识点：
 * 1. 使用 AIDL (Android Interface Definition Language) 定义接口
 * 2. 实现 Stub 类提供跨进程通信能力
 * 3. 客户端通过 IBinder 调用远程方法
 * 4. 同一应用内也可模拟远程绑定（不同进程）
 *
 * 配置要求：
 * - AndroidManifest.xml 中设置 android:exported="true"
 * - 如需跨应用，还需配置 intent-filter
 *
 * 进程配置示例（在 AndroidManifest.xml 中）：
 * ```xml
 * <service
 *     android:name=".service.bind.RemoteBindService"
 *     android:exported="true"
 *     android:process=":remote" />  <!-- 独立进程 -->
 * ```
 */
class RemoteBindService : Service() {

    companion object {
        private const val TAG = "RemoteBindService"
    }

    // 服务启动时间
    private var startTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        startTime = System.currentTimeMillis()
        Log.d(TAG, "onCreate - 服务创建，进程: ${android.os.Process.myPid()}")
    }

    /**
     * AIDL Stub 实现
     * Stub 是 AIDL 自动生成的抽象类，实现了 IBinder 接口
     */
    private val binder = object : IRemoteService.Stub() {

        override fun getRandomNumber(): Int {
            Log.d(TAG, "getRandomNumber 被调用")
            return (0..100).random()
        }

        override fun getUptimeSeconds(): Long {
            val uptime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime)
            Log.d(TAG, "getUptimeSeconds 被调用，返回: $uptime")
            return uptime
        }

        override fun isRunning(): Boolean {
            Log.d(TAG, "isRunning 被调用")
            return true
        }

        override fun sendMessage(message: String?): String {
            Log.d(TAG, "sendMessage 被调用，消息: $message")
            return "服务端收到: $message (PID: ${android.os.Process.myPid()})"
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            Log.d(TAG, "basicTypes: int=$anInt, long=$aLong, bool=$aBoolean, " +
                    "float=$aFloat, double=$aDouble, string=$aString")
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind - action: ${intent?.action}")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        // 返回 true 表示下次客户端绑定时会收到 onRebind 回调
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}