package com.peter.components.demo.activity.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R
import com.peter.components.demo.createExplicitIntentActivityIntent
import com.peter.components.demo.createImplicitIntentActivityIntent
import com.peter.components.demo.databinding.ActivityBasicBinding

/**
 * Activity 基础用法示例
 * 
 * 知识点：
 * 1. Activity 必须在 AndroidManifest.xml 中注册
 * 2. 继承 AppCompatActivity 获得兼容性支持
 * 3. setContentView() 设置布局，推荐使用 ViewBinding
 * 4. 使用 Intent 进行 Activity 跳转
 */
class BasicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExplicit.setOnClickListener {
            // 显式 Intent：明确指定目标组件
            startActivity(createExplicitIntentActivityIntent(this))
        }

        binding.btnImplicit.setOnClickListener {
            // 隐式 Intent：由系统匹配目标组件
            startActivity(createImplicitIntentActivityIntent(this))
        }
    }
}
