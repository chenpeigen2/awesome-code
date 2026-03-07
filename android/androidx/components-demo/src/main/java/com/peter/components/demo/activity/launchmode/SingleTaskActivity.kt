package com.peter.components.demo.activity.launchmode

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityLaunchmodeBinding

/**
 * SingleTask 启动模式
 * 
 * 知识点：
 * 1. 如果目标实例已存在于某个任务栈，则复用该实例，调用 onNewIntent()
 * 2. 复用时，清除该实例上面的所有 Activity
 * 3. 如果不存在，则创建新实例
 * 4. 适用于：应用主页、购物车主页等
 */
class SingleTaskActivity : AppCompatActivity() {

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
        updateInfo("onNewIntent - 栈内复用")
        Toast.makeText(this, "栈内复用，清除上方所有 Activity", Toast.LENGTH_SHORT).show()
    }

    private fun updateInfo(status: String) {
        binding.tvTaskId.text = taskId.toString()
        binding.tvLaunchCount.text = "SingleTask 实例数: $launchCount ($status)"
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
