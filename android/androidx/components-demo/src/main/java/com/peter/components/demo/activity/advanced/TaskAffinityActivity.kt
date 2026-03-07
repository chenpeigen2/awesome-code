package com.peter.components.demo.activity.advanced

import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.databinding.ActivityTaskAffinityBinding

/**
 * TaskAffinity 示例
 * 
 * 知识点：
 * 1. TaskAffinity 定义 Activity 所属的任务栈
 * 2. 默认值为应用包名
 * 3. 配合 SingleTask 使用，可以创建新的任务栈
 * 4. allowTaskReparenting - 允许 Activity 重新归属任务栈
 * 
 * 本 Activity 在 AndroidManifest.xml 中配置了：
 * android:taskAffinity="com.peter.components.task"
 */
class TaskAffinityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskAffinityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAffinityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TaskAffinity 在 AndroidManifest.xml 中配置
        // 默认值是应用的包名
        binding.tvAffinity.text = "com.peter.components.task (Manifest 配置)"
        binding.tvTaskId.text = taskId.toString()

        binding.btnBackToMain.setOnClickListener {
            finish()
        }
    }
}