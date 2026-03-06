package com.peter.components.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * 静态注册的广播接收者
 *
 * ═══════════════════════════════════════════════════════════════
 * 静态注册 vs 动态注册
 * ═══════════════════════════════════════════════════════════════
 *
 * 静态注册（Manifest）：
 * 优点：
 * - 应用未启动也能接收广播
 * - 适合监听系统事件
 *
 * 缺点：
 * - Android 8.0+ 限制隐式广播
 * - 每次接收都会启动应用
 *
 * 动态注册（代码）：
 * 优点：
 * - 可以接收隐式广播
 * - 灵活控制注册/注销时机
 *
 * 缺点：
 * - 组件销毁后无法接收
 * - 需要手动管理生命周期
 *
 * ═══════════════════════════════════════════════════════════════
 * Android 8.0+ 隐式广播限制
 * ═══════════════════════════════════════════════════════════════
 *
 * 以下广播仍然可以静态注册：
 * - BOOT_COMPLETED（需要权限）
 * - LOCKED_BOOT_COMPLETED
 * - TIMEZONE_CHANGED
 * - TIME_SET
 * - LOCALE_CHANGED
 *
 * 大部分隐式广播需要动态注册
 */
class StaticReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "StaticReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")

        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "开机完成")
                // 可以在这里启动服务
            }
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isAirplaneMode = intent.getBooleanExtra("state", false)
                Log.d(TAG, "飞行模式: $isAirplaneMode")
                Toast.makeText(
                    context,
                    "飞行模式: ${if (isAirplaneMode) "开启" else "关闭"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
