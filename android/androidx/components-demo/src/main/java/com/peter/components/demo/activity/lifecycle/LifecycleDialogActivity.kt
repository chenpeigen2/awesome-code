package com.peter.components.demo.activity.lifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityLifecycleDialogBinding

/**
 * 对话框样式的 Activity
 * 
 * 知识点：
 * 1. 使用 Theme.Material3.DayNight.Dialog 主题
 * 2. 启动后下方 Activity 执行 onPause
 * 3. 关闭后下方 Activity 执行 onResume
 */
class LifecycleDialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLifecycleDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLifecycleDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}
