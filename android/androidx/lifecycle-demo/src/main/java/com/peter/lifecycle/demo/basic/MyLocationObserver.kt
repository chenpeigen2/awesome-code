package com.peter.lifecycle.demo.basic

import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 位置监听 Observer
 * 
 * 使用 DefaultLifecycleObserver 监听生命周期
 * 在 onResume 时开始监听，在 onPause 时停止监听
 */
class MyLocationObserver(
    private val context: Context
) : DefaultLifecycleObserver {

    private val TAG = "LocationObserver"
    private var locationManager: LocationManager? = null

    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "onCreate: 初始化 LocationManager")
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "onStart: Activity 可见但不可交互")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: 开始监听位置变化")
        // 实际项目中，这里应该请求位置更新
        // locationManager?.requestLocationUpdates(...)
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "onPause: 停止监听位置变化")
        // 实际项目中，这里应该移除位置更新
        // locationManager?.removeUpdates(...)
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop: Activity 不可见")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "onDestroy: 释放资源")
        locationManager = null
    }
}
