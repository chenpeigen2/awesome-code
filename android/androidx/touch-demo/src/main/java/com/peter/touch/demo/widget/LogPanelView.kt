package com.peter.touch.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.touch.demo.R
import com.peter.touch.demo.databinding.ItemLogBinding
import com.peter.touch.demo.databinding.ViewLogPanelBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日志面板视图
 * 实时显示事件分发链路的日志
 */
class LogPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewLogPanelBinding
    private val logs = mutableListOf<LogItem>()
    private val adapter = LogAdapter(logs)
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private var maxLogs = 200
    private var autoScroll = true

    init {
        orientation = VERTICAL
        binding = ViewLogPanelBinding.inflate(LayoutInflater.from(context), this, true)
        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@LogPanelView.adapter
        }
    }

    private fun setupButtons() {
        binding.btnClear.setOnClickListener { clearLogs() }
        binding.btnAutoScroll.setOnClickListener { 
            autoScroll = !autoScroll
            updateAutoScrollButton()
        }
    }

    private fun updateAutoScrollButton() {
        binding.btnAutoScroll.text = if (autoScroll) "自动滚动: 开" else "自动滚动: 关"
    }

    /**
     * 添加日志
     * @param tag 日志标签，如 "Activity", "ViewGroup", "View"
     * @param method 方法名，如 "dispatchTouchEvent", "onTouchEvent"
     * @param action 事件类型，如 "DOWN", "MOVE", "UP"
     * @param result 返回值，如 "true", "false", "super"
     */
    fun log(tag: String, method: String, action: String, result: String = "") {
        val timestamp = dateFormat.format(Date())
        val logItem = LogItem(timestamp, tag, method, action, result)
        
        logs.add(logItem)
        
        // 限制日志数量
        if (logs.size > maxLogs) {
            logs.removeAt(0)
            adapter.notifyItemRemoved(0)
        }
        
        adapter.notifyItemInserted(logs.size - 1)
        
        if (autoScroll) {
            binding.recyclerView.scrollToPosition(logs.size - 1)
        }
    }

    /**
     * 添加分隔日志
     */
    fun logSeparator(text: String = "──────────────────────") {
        val timestamp = dateFormat.format(Date())
        val logItem = LogItem(timestamp, "───", text, "", "")
        logs.add(logItem)
        adapter.notifyItemInserted(logs.size - 1)
        
        if (autoScroll) {
            binding.recyclerView.scrollToPosition(logs.size - 1)
        }
    }

    /**
     * 清空日志
     */
    fun clearLogs() {
        logs.clear()
        adapter.notifyDataSetChanged()
    }

    /**
     * 设置最大日志数量
     */
    fun setMaxLogs(max: Int) {
        maxLogs = max
    }

    /**
     * 日志数据类
     */
    data class LogItem(
        val timestamp: String,
        val tag: String,
        val method: String,
        val action: String,
        val result: String
    ) {
        fun getTagColor(): Int {
            return when (tag) {
                "Activity" -> 0xFF4CAF50.toInt()      // 绿色
                "ViewGroup" -> 0xFF2196F3.toInt()     // 蓝色
                "View" -> 0xFFFF9800.toInt()          // 橙色
                "Child" -> 0xFFFF9800.toInt()         // 橙色
                "Outer" -> 0xFF9C27B0.toInt()         // 紫色
                "Inner" -> 0xFF00BCD4.toInt()         // 青色
                else -> 0xFF9E9E9E.toInt()            // 灰色
            }
        }

        fun getResultColor(): Int {
            return when (result) {
                "true" -> 0xFF4CAF50.toInt()          // 绿色
                "false" -> 0xFFF44336.toInt()         // 红色
                "super" -> 0xFFFF9800.toInt()         // 橙色
                else -> 0xFF9E9E9E.toInt()            // 灰色
            }
        }
    }

    /**
     * 日志适配器
     */
    private class LogAdapter(
        private val logs: MutableList<LogItem>
    ) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(logs[position])
        }

        override fun getItemCount(): Int = logs.size

        class ViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: LogItem) {
                binding.tvTimestamp.text = item.timestamp
                binding.tvTag.text = item.tag
                binding.tvTag.setTextColor(item.getTagColor())
                binding.tvMethod.text = item.method
                binding.tvAction.text = item.action
                
                if (item.result.isNotEmpty()) {
                    binding.tvResult.visibility = View.VISIBLE
                    binding.tvResult.text = "→ ${item.result}"
                    binding.tvResult.setTextColor(item.getResultColor())
                } else {
                    binding.tvResult.visibility = View.GONE
                }
            }
        }
    }
}
