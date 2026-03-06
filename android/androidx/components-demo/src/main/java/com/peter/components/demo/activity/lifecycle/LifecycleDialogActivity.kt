package com.peter.components.demo.activity.lifecycle

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * 对话框风格 Activity
 *
 * 在 Manifest 中设置 android:theme="@style/Theme.AppCompat.Dialog"
 *
 * 特点：
 * 1. 看起来像对话框，实际上是 Activity
 * 2. 覆盖在原 Activity 上，但原 Activity 仍然可见
 * 3. 原Activity只会执行 onPause，不会执行 onStop
 */
class LifecycleDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_dialog)

        findViewById<Button>(R.id.btnClose).setOnClickListener {
            finish()
        }
    }
}
