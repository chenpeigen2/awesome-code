package com.peter.components.demo.service.bind

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * 远程绑定服务示例 (AIDL)
 * 
 * 知识点：
 * 1. 使用 AIDL (Android Interface Definition Language)
 * 2. 跨进程通信 (IPC)
 * 3. Stub 类实现接口
 * 4. 客户端通过 IBinder 调用远程方法
 * 
 * 注意：
 * - 需要创建 .aidl 文件定义接口
 * - 服务需要设置 android:exported="true"
 * - 客户端和服务端需要相同的 AIDL 文件
 * 
 * AIDL 文件示例 (IRemoteService.aidl):
 * ```
 * package com.peter.components.demo.service;
 * 
 * interface IRemoteService {
 *     int getRandomNumber();
 *     void basicTypes(int anInt, long aLong, boolean aBoolean,
 *                     float aFloat, double aDouble, String aString);
 * }
 * ```
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
        // 实际项目中这里返回 AIDL 生成的 Stub
        // return object : IRemoteService.Stub() {
        //     override fun getRandomNumber(): Int = Random().nextInt(100)
        // }
        
        // 由于没有实际的 AIDL 文件，这里返回一个空的 Binder
        return object : android.os.Binder() {
            override fun onTransact(code: Int, data: android.os.Parcel, reply: android.os.Parcel?, flags: Int): Boolean {
                Log.d(TAG, "onTransact: code=$code")
                return super.onTransact(code, data, reply, flags)
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}
