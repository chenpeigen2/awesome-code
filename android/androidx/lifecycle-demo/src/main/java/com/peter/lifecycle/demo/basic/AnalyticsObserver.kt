package com.peter.lifecycle.demo.basic

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * 数据统计 Observer
 * 
 * 演示如何获取当前生命周期状态
 */
class AnalyticsObserver : DefaultLifecycleObserver {
    
    private val TAG = "AnalyticsObserver"
    private var startTime: Long = 0

    override fun onResume(owner: LifecycleOwner) {
        startTime = System.currentTimeMillis()
        Log.d(TAG, "onResume: 开始统计页面停留时间")
        
        // 获取当前生命周期状态
        val currentState = owner.lifecycle.currentState
        Log.d(TAG, "当前状态: $currentState")
        
        // 状态判断
        when (currentState) {
            Lifecycle.State.RESUMED -> Log.d(TAG, "Activity 处于前台且可交互")
            Lifecycle.State.STARTED -> Log.d(TAG, "Activity 可见但不可交互")
            Lifecycle.State.CREATED -> Log.d(TAG, "Activity 已创建但不可见")
            Lifecycle.State.DESTROYED -> Log.d(TAG, "Activity 已销毁")
            Lifecycle.State.INITIALIZED -> Log.d(TAG, "Activity 初始化")
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        val duration = System.currentTimeMillis() - startTime
        Log.d(TAG, "onPause: 页面停留时间 ${duration}ms")
        
        // 检查是否至少处于 STARTED 状态
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "Activity 至少处于 STARTED 状态")
        }
    }
}