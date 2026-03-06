package com.peter.components.demo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.core.view.WindowCompat

/**
 * Application 类
 *
 * 使用 ActivityLifecycleCallbacks 统一处理所有 Activity 的状态栏问题
 */
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // 设置状态栏颜色
                activity.window.statusBarColor = activity.getColor(R.color.primary_dark)
                // 让布局文件的 fitsSystemWindows 生效
                WindowCompat.setDecorFitsSystemWindows(activity.window, true)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}