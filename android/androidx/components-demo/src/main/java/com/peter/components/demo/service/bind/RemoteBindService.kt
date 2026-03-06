package com.peter.components.demo.service.bind

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * 绑定服务示例 - 远程绑定（AIDL）
 *
 * ═══════════════════════════════════════════════════════════════
 * AIDL (Android Interface Definition Language)
 * ═══════════════════════════════════════════════════════════════
 *
 * AIDL 用于跨进程通信 (IPC)
 *
 * 使用场景：
 * - 多进程应用
 * - 提供服务给其他应用
 *
 * 开发步骤：
 * 1. 创建 .aidl 接口文件
 * 2. 实现接口 Stub
 * 3. 在 onBind 中返回 Stub
 * 4. 客户端绑定服务获取接口
 *
 * ═══════════════════════════════════════════════════════════════
 * 注意事项
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. AIDL 方法是同步的，不要在主线程调用
 * 2. 传递的对象必须实现 Parcelable
 * 3. Binder 事务缓冲区限制 1MB
 * 4. 注意处理 RemoteException
 */
class RemoteBindService : Service() {

    companion object {
        private const val TAG = "RemoteBindService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        // 实际项目中返回 AIDL Stub 实现
        // return IRemoteService.Stub.asInterface(...)
        return object : android.os.Binder() {
            override fun onTransact(code: Int, data: android.os.Parcel, reply: android.os.Parcel?, flags: Int): Boolean {
                Log.d(TAG, "onTransact: code=$code")
                return super.onTransact(code, data, reply, flags)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}
