package com.peter.components.demo.activity.lifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityLifecycleTranslucentBinding

/**
 * 半透明背景的 Activity
 * 
 * 知识点：
 * 1. 使用透明主题，设置 android:windowIsTranslucent
 * 2. 可以透过看到下方的 Activity
 * 3. 启动后下方 Activity 执行 onPause（不会执行 onStop）
 */
class LifecycleTranslucentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLifecycleTranslucentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLifecycleTranslucentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}
