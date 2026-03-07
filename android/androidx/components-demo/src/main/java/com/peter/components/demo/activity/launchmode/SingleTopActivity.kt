package com.peter.components.demo.activity.launchmode

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityLaunchmodeBinding

/**
 * SingleTop 启动模式
 * 
 * 知识点：
 * 1. 如果目标实例已在栈顶，则复用该实例，调用 onNewIntent()
 * 2. 如果目标实例不在栈顶，则创建新实例
 * 3. 适用于：消息详情页、通知点击等场景
 */
class SingleTopActivity : AppCompatActivity() {

    companion object {
        private var launchCount = 0
    }

    private lateinit var binding: ActivityLaunchmodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchmodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchCount++
        updateInfo("onCreate")

        setupButtons()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        updateInfo("onNewIntent - 复用栈顶实例")
        Toast.makeText(this, "复用栈顶实例，调用 onNewIntent", Toast.LENGTH_SHORT).show()
    }

    private fun updateInfo(status: String) {
        binding.tvTaskId.text = taskId.toString()
        binding.tvLaunchCount.text = "SingleTop 实例数: $launchCount ($status)"
    }

    private fun setupButtons() {
        binding.btnStandard.setOnClickListener {
            startActivity(android.content.Intent(this, StandardActivity::class.java))
        }
        binding.btnSingleTop.setOnClickListener {
            // 如果当前已在栈顶，会复用实例，调用 onNewIntent
            startActivity(android.content.Intent(this, SingleTopActivity::class.java))
        }
        binding.btnSingleTask.setOnClickListener {
            startActivity(android.content.Intent(this, SingleTaskActivity::class.java))
        }
        binding.btnSingleInstance.setOnClickListener {
            startActivity(android.content.Intent(this, SingleInstanceActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launchCount--
    }
}
