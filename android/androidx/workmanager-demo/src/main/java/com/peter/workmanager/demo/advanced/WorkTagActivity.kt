package com.peter.workmanager.demo.advanced

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.peter.workmanager.demo.R
import com.peter.workmanager.demo.databinding.ActivityWorkTagBinding
import com.peter.workmanager.demo.worker.TagWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 任务标签示例 Activity
 * 
 * 演示如何使用标签管理和取消一组任务。
 */
class WorkTagActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "WorkTagActivity"
    }

    private lateinit var binding: ActivityWorkTagBinding
    private lateinit var workManager: WorkManager
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()
    private var taskCounter = 0
    private val taskAdapter = TaskAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        workManager = WorkManager.getInstance(this)
        
        binding.recyclerView.adapter = taskAdapter
        
        setupButtons()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            startTaggedWork()
        }
        
        binding.btnQuery.setOnClickListener {
            queryTaggedWork()
        }
        
        binding.btnCancel.setOnClickListener {
            cancelTaggedWork()
        }
    }

    private fun startTaggedWork() {
        val tag = binding.etTag.text.toString()
        if (tag.isBlank()) {
            appendLog("请输入标签名称")
            return
        }
        
        taskCounter++
        appendLog("添加带标签 '$tag' 的任务...")
        
        val inputData = workDataOf(
            TagWorker.KEY_TASK_NAME to "Task #$taskCounter"
        )
        
        // 创建带标签的任务
        val workRequest = OneTimeWorkRequestBuilder<TagWorker>()
            .addTag(tag)  // 添加标签
            .setInputData(inputData)
            .build()
        
        workManager.enqueue(workRequest)
        
        appendLog("任务已入队, ID: ${workRequest.id}")
        
        // 自动查询
        queryTaggedWork()
    }

    private fun queryTaggedWork() {
        val tag = binding.etTag.text.toString()
        if (tag.isBlank()) {
            appendLog("请输入标签名称")
            return
        }
        
        appendLog("查询标签 '$tag' 下的任务...")
        
        workManager.getWorkInfosByTagLiveData(tag).observe(this, Observer { workInfos ->
            val tasks = workInfos?.map { workInfo ->
                val taskName = workInfo.outputData.getString(TagWorker.KEY_TASK_NAME) ?: "Unknown"
                TaskItem(
                    id = workInfo.id.toString().take(8),
                    name = taskName,
                    state = workInfo.state.name
                )
            } ?: emptyList()
            
            taskAdapter.submitList(tasks)
            binding.tvTaskCount.text = "任务数量: ${tasks.size}"
            
            appendLog("找到 ${tasks.size} 个任务")
        })
    }

    private fun cancelTaggedWork() {
        val tag = binding.etTag.text.toString()
        if (tag.isBlank()) {
            appendLog("请输入标签名称")
            return
        }
        
        workManager.cancelAllWorkByTag(tag)
        appendLog("已取消所有带标签 '$tag' 的任务")
    }

    private fun appendLog(message: String) {
        val timestamp = dateFormat.format(Date())
        logBuilder.append("[$timestamp] $message\n")
        binding.tvLog.text = logBuilder.toString()
        Log.d(TAG, message)
    }
}

/**
 * 任务列表项
 */
data class TaskItem(
    val id: String,
    val name: String,
    val state: String
)

/**
 * 任务列表适配器
 */
class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    
    private var items: List<TaskItem> = emptyList()
    
    fun submitList(newItems: List<TaskItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_status, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvTaskName)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvTaskStatus)

        fun bind(item: TaskItem) {
            tvName.text = "${item.name} (${item.id})"
            tvStatus.text = item.state
            
            val color = when (item.state) {
                "ENQUEUED" -> itemView.context.getColor(R.color.status_enqueued)
                "RUNNING" -> itemView.context.getColor(R.color.status_running)
                "SUCCEEDED" -> itemView.context.getColor(R.color.status_succeeded)
                "FAILED" -> itemView.context.getColor(R.color.status_failed)
                "CANCELLED" -> itemView.context.getColor(R.color.status_cancelled)
                else -> itemView.context.getColor(R.color.status_idle)
            }
            tvStatus.setTextColor(color)
        }
    }
}
