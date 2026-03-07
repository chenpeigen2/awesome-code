package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * 静态注册的广播接收器
 * 
 * 知识点：
 * 1. 在 AndroidManifest.xml 中注册
 * 2. exported="true" 允许接收外部广播
 * 3. 可以接收系统广播（开机启动、电量变化等）
 * 
 * 注意：
 * - Android 8.0+ 限制了静态注册隐式广播
 * - 大部分系统广播需要动态注册
 * - 开机启动、电量变化等少数系统广播仍可静态注册
 */
class StaticReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "StaticReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: action=${intent?.action}")
        
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "设备启动完成")
                // 可以在这里启动服务
            }
            Intent.ACTION_POWER_CONNECTED -> {
                Toast.makeText(context, "电源已连接", Toast.LENGTH_SHORT).show()
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Toast.makeText(context, "电源已断开", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
