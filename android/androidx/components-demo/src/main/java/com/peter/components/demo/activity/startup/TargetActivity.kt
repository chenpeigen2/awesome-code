package com.peter.components.demo.activity.startup

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * 目标 Activity - 被启动的 Activity
 *
 * 知识点：
 * Activity 启动流程中的关键回调（按调用顺序）：
 *
 * 1. Activity.attach()
 *    - 绑定 Window、Application、Intent 等
 *    - 创建 PhoneWindow
 *    - 此时尚未调用 onCreate
 *
 * 2. onCreate()
 *    - Activity 创建，初始化界面
 *    - 调用 setContentView() 加载布局
 *    - 此时 View 树已创建但未测量
 *
 * 3. onStart()
 *    - Activity 即将可见
 *    - 此时 Activity 仍不可交互
 *
 * 4. onResume()
 *    - Activity 可见且可交互
 *    - 此时 View 已完成测量布局
 *    - 但首帧可能还未绘制完成
 *
 * 5. onWindowFocusChanged(true)
 *    - Window 获得焦点
 *    - 首帧绘制完成后触发
 *    - 此时用户可看到完整界面
 */
class TargetActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TargetActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 记录 onCreate 开始时间
        StartupTracker.logStage("TargetActivity.onCreate()", "开始创建界面")
        Log.d(TAG, "onCreate() called")

        super.onCreate(savedInstanceState)

        // 设置布局
        setContentView(R.layout.activity_target)

        // 更新界面显示
        findViewById<TextView>(R.id.tvLaunchInfo)?.text = """
            此 Activity 已通过标准流程启动

            启动方式：
            • Context.startActivity()
            • 经过 Instrumentation
            • 通过 Binder IPC 调用 ATMS
            • 系统调度后回调到本进程
        """.trimIndent()

        StartupTracker.logStage("setContentView()", "布局加载完成")
    }

    override fun onStart() {
        StartupTracker.logStage("TargetActivity.onStart()", "Activity 即将可见")
        Log.d(TAG, "onStart() called")
        super.onStart()
    }

    override fun onResume() {
        StartupTracker.logStage("TargetActivity.onResume()", "Activity 可见可交互")
        Log.d(TAG, "onResume() called")
        super.onResume()

        // 检测首帧绘制完成
        window.decorView.post {
            StartupTracker.logStage("首帧绘制完成", "用户可见完整界面")
            Log.d(TAG, "First frame drawn")
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            StartupTracker.logStage("onWindowFocusChanged(true)", "Window 获得焦点")
            Log.d(TAG, "onWindowFocusChanged(true)")
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause() called")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop() called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }
}
