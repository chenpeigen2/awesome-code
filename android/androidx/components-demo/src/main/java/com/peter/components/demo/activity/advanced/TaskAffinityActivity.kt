package com.peter.components.demo.activity.advanced

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.peter.components.demo.R

/**
 * Activity 进阶 - TaskAffinity
 *
 * ═══════════════════════════════════════════════════════════════
 * TaskAffinity 详解
 * ═══════════════════════════════════════════════════════════════
 *
 * TaskAffinity 表示 Activity 所属的任务栈
 *
 * 默认情况：
 * - 所有 Activity 的 taskAffinity 都是包名
 * - 即所有 Activity 默认在同一个任务栈
 *
 * 自定义 TaskAffinity：
 * - 在 Manifest 中设置 android:taskAffinity="xxx"
 * - 与 singleTask 或 FLAG_ACTIVITY_NEW_TASK 配合使用
 * - Activity 会在指定的任务栈中运行
 *
 * 使用场景：
 * 1. 将某些 Activity 分组到独立任务栈
 * 2. 实现多窗口应用
 * 3. 最近任务列表中显示为独立任务
 *
 * ═══════════════════════════════════════════════════════════════
 * 注意事项
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. TaskAffinity 不要与包名相同
 * 2. 需要配合 FLAG_ACTIVITY_NEW_TASK 或 singleTask 使用
 * 3. 影响 Activity 的启动行为和返回栈
 */
class TaskAffinityActivity : AppCompatActivity() {

    private lateinit var tvTaskInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_affinity)

        tvTaskInfo = findViewById(R.id.tvTaskInfo)
        updateTaskInfo()

        findViewById<Button>(R.id.btnActivityResult).setOnClickListener {
            startActivity(Intent(this, ActivityResultActivity::class.java))
        }

        findViewById<Button>(R.id.btnSharedElement).setOnClickListener {
            startActivity(Intent(this, SharedElementActivity::class.java))
        }

        findViewById<Button>(R.id.btnFragmentComm).setOnClickListener {
            startActivity(Intent(this, FragmentCommunicationActivity::class.java))
        }
    }

    private fun updateTaskInfo() {
        val info = StringBuilder()
        info.append("Task ID: ${taskId}\n")
        info.append("TaskAffinity: com.peter.components.task1\n")
        info.append("Class: ${this::class.java.simpleName}\n")
        info.append("HashCode: ${hashCode()}\n\n")
        info.append("说明：\n")
        info.append("此 Activity 有独立的 taskAffinity\n")
        info.append("与默认包名不同\n")
        info.append("所以在最近任务中会显示为独立任务")

        tvTaskInfo.text = info.toString()
    }
}
