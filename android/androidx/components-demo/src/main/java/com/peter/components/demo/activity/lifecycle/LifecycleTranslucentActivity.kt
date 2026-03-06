package com.peter.components.demo.activity.lifecycle

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * 透明 Activity
 *
 * 在 Manifest 中设置透明主题：
 * android:theme="@style/Theme.Components.Translucent"
 *
 * 特点：
 * 1. 窗口背景透明
 * 2. 可以透过看到下面的 Activity
 * 3. 原Activity只会执行 onPause，不会执行 onStop
 *
 * 使用场景：
 * - 引导页/新手教程
 * - 悬浮窗效果
 * - 转场动画
 */
class LifecycleTranslucentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_translucent)

        findViewById<Button>(R.id.btnClose).setOnClickListener {
            finish()
        }
    }
}
