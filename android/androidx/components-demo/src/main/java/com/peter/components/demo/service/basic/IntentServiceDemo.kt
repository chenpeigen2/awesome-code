package com.peter.components.demo.service.basic

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * IntentService 示例（已废弃，仅供参考）
 *
 * ═══════════════════════════════════════════════════════════════
 * IntentService 说明
 * ═══════════════════════════════════════════════════════════════
 *
 * IntentService 是 Service 的子类，特点：
 * 1. 自动在工作线程执行任务
 * 2. 任务按顺序执行（队列方式）
 * 3. 所有任务完成后自动销毁
 *
 * 从 Android 8.0 开始，IntentService 受到后台限制
 * 从 Android 11 (API 30) 开始，IntentService 被废弃
 *
 * ═══════════════════════════════════════════════════════════════
 * 替代方案
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. JobIntentService
 *    - 兼容 Android 8.0+ 的后台限制
 *    - 在 Android 8.0+ 上使用 JobScheduler
 *    - 在旧版本上使用 IntentService
 *
 * 2. WorkManager
 *    - 推荐的后台任务解决方案
 *    - 支持约束条件、重试、链式任务
 *    - 兼容所有 Android 版本
 *
 * 3. 自定义 Service + Coroutine
 *    - 使用协程在工作线程执行任务
 *    - 更灵活的控制
 */
@Suppress("DEPRECATION")
class IntentServiceDemo : IntentService("IntentServiceDemo") {

    companion object {
        private const val TAG = "IntentServiceDemo"
        const val ACTION_SYNC = "com.peter.components.demo.SYNC"
        const val EXTRA_DATA = "extra_data"
    }

    /**
     * 在工作线程中执行
     * 所有请求按顺序处理
     */
    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent: ${intent?.action}")

        when (intent?.action) {
            ACTION_SYNC -> {
                val data = intent.getStringExtra(EXTRA_DATA)
                performSync(data)
            }
        }
    }

    private fun performSync(data: String?) {
        Log.d(TAG, "开始同步: $data")
        // 模拟耗时操作
        Thread.sleep(3000)
        Log.d(TAG, "同步完成")
    }
}
