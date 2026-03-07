package com.peter.components.demo.activity.launchmode

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityLaunchmodeBinding

/**
 * SingleInstance 启动模式
 * 
 * 知识点：
 * 1. 创建一个独立的任务栈，全局只有一个实例
 * 2. 该任务栈中只有这一个 Activity
 * 3. 从该 Activity 启动其他 Activity，会在另一个任务栈中创建
 * 4. 适用于：独立的功能模块，如闹钟响铃界面
 */
class SingleInstanceActivity : AppCompatActivity() {

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
        updateInfo("onNewIntent - 全局单例")
        Toast.makeText(this, "全局单例，独立任务栈", Toast.LENGTH_SHORT).show()
    }

    private fun updateInfo(status: String) {
        binding.tvTaskId.text = "$taskId (独立任务栈)"
        binding.tvLaunchCount.text = "SingleInstance 实例数: $launchCount ($status)"
    }

    private fun setupButtons() {
        binding.btnStandard.setOnClickListener {
            startActivity(android.content.Intent(this, StandardActivity::class.java))
        }
        binding.btnSingleTop.setOnClickListener {
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
