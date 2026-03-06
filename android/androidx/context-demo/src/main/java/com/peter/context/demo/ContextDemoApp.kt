package com.peter.context.demo

import android.app.Application
import android.content.Context
import android.util.Log

/**
 * Context Demo Application
 * 
 * 展示全局 Context 的获取方式
 * 
 * 使用方式：
 * 1. ContextDemoApp.context - 获取全局 Application Context
 * 2. applicationContext - 任何 Context 都可以调用获取 Application Context
 */
class ContextDemoApp : Application() {

    companion object {
        private const val TAG = "ContextDemoApp"

        /**
         * 全局 Application Context
         * 
         * 注意：这是 Application Context，不是 Activity Context
         * 适用于：
         * - 需要全局 Context 的场景（如单例模式）
         * - 不需要 UI 相关操作的场景
         * 
         * 不适用于：
         * - 启动 Activity（需要添加 FLAG_ACTIVITY_NEW_TASK）
         * - 创建 Dialog（必须使用 Activity Context）
         * - LayoutInflater（可能丢失主题信息）
         */
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        
        Log.d(TAG, "Application onCreate")
        Log.d(TAG, "Application Context: $context")
        Log.d(TAG, "Package Name: ${context.packageName}")
        Log.d(TAG, "Application Info: ${context.applicationInfo}")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Application onTerminate")
    }
}
