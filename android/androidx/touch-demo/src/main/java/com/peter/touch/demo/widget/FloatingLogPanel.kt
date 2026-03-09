package com.peter.touch.demo.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peter.touch.demo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 悬浮窗日志面板
 * 可拖动、可展开/收起
 * 添加到 Activity 的 DecorView 上
 */
class FloatingLogPanel(private val activity: Activity) {

    private var floatingView: View? = null
    private var isExpanded = false
    private var isShowing = false
    
    private val logs = mutableListOf<LogItem>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private var maxLogs = 200
    private var autoScroll = true
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogAdapter
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var containerLayout: FrameLayout

    @SuppressLint("ClickableViewAccessibility")
    fun show() {
        if (isShowing || floatingView != null) return
        
        val decorView = activity.window.decorView as? ViewGroup ?: return
        
        floatingView = createFloatingView()
        
        // 添加到 DecorView
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            leftMargin = 50
            topMargin = 300
        }
//        activity.addContentView(floatingView, params)
        decorView.addView(floatingView, params)
        isShowing = true
    }

    fun hide() {
        if (!isShowing || floatingView == null) return
        
        val decorView = activity.window.decorView as? ViewGroup ?: return
        decorView.removeView(floatingView)
        floatingView = null
        isShowing = false
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createFloatingView(): View {
        containerLayout = FrameLayout(activity).apply {
            setBackgroundResource(R.drawable.bg_floating_log_collapsed)
            elevation = 16f
        }

        // 折叠状态视图 - 小圆形按钮
        collapsedView = FrameLayout(activity).apply {
            setBackgroundResource(R.drawable.bg_floating_log_collapsed)
            layoutParams = FrameLayout.LayoutParams(dp(56), dp(56))
            
            addView(ImageView(activity).apply {
                setImageResource(android.R.drawable.ic_menu_recent_history)
                layoutParams = FrameLayout.LayoutParams(dp(24), dp(24)).apply {
                    gravity = Gravity.CENTER
                }
            })
        }

        // 展开状态视图
        expandedView = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_floating_log)
            layoutParams = FrameLayout.LayoutParams(dp(320), dp(400))
            visibility = View.GONE
            // 防止触摸事件穿透到底层
            isClickable = true
            isFocusable = true

            // 标题栏
            addView(createHeaderView())
            // 日志列表
            addView(createLogListView())
        }

        containerLayout.addView(collapsedView)
        containerLayout.addView(expandedView)

        // 设置拖动和点击事件
        setupTouchEvents()

        return containerLayout
    }

    private fun createHeaderView(): View {
        return LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(8), dp(8))
            setBackgroundColor(0xFF1976D2.toInt())
            
            // 图标
            addView(ImageView(activity).apply {
                setImageResource(android.R.drawable.ic_menu_recent_history)
                layoutParams = LinearLayout.LayoutParams(dp(20), dp(20)).apply {
                    marginEnd = dp(8)
                }
            })
            
            // 标题
            addView(TextView(activity).apply {
                text = "日志面板"
                textSize = 14f
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
            
            // 日志数量
            addView(TextView(activity).apply {
                id = View.generateViewId()
                text = "0"
                textSize = 10f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(dp(6), dp(2), dp(6), dp(2))
                setBackgroundResource(R.drawable.bg_log_count)
                tag = "logCount"
            })
            
            // 自动滚动按钮
            addView(TextView(activity).apply {
                text = "自动"
                textSize = 10f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(dp(6), dp(2), dp(6), dp(2))
                setOnClickListener {
                    autoScroll = !autoScroll
                    text = if (autoScroll) "自动" else "手动"
                }
            })
            
            // 清空按钮
            addView(TextView(activity).apply {
                text = "清空"
                textSize = 10f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(dp(6), dp(2), dp(6), dp(2))
                setOnClickListener { clearLogs() }
            })
            
            // 收起按钮
            addView(ImageView(activity).apply {
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                layoutParams = LinearLayout.LayoutParams(dp(20), dp(20)).apply {
                    marginStart = dp(4)
                }
                setOnClickListener { collapse() }
            })
        }
    }

    private fun createLogListView(): View {
        adapter = LogAdapter(logs)
        
        recyclerView = RecyclerView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            layoutManager = LinearLayoutManager(activity)
            adapter = this@FloatingLogPanel.adapter
            setBackgroundColor(0xFFFAFAFA.toInt())
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        
        return recyclerView
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchEvents() {
        var lastX = 0
        var lastY = 0
        var isDragging = false

        containerLayout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX.toInt() - lastX)
                    val dy = (event.rawY.toInt() - lastY)
                    
                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                        isDragging = true
                        containerLayout.translationX += dx
                        containerLayout.translationY += dy
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        if (!isExpanded) {
                            expand()
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun expand() {
        isExpanded = true
        collapsedView.visibility = View.GONE
        expandedView.visibility = View.VISIBLE
        containerLayout.setBackgroundResource(R.drawable.bg_floating_log)
    }

    private fun collapse() {
        isExpanded = false
        expandedView.visibility = View.GONE
        collapsedView.visibility = View.VISIBLE
        containerLayout.setBackgroundResource(R.drawable.bg_floating_log_collapsed)
    }

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
        
        // 更新日志数量
        updateLogCount()
        
        if (autoScroll && isExpanded) {
            recyclerView.scrollToPosition(logs.size - 1)
        }
    }

    fun logSeparator(text: String = "──────────────────────") {
        val timestamp = dateFormat.format(Date())
        val logItem = LogItem(timestamp, "───", text, "", "")
        logs.add(logItem)
        adapter.notifyItemInserted(logs.size - 1)
        updateLogCount()
        
        if (autoScroll && isExpanded) {
            recyclerView.scrollToPosition(logs.size - 1)
        }
    }

    fun clearLogs() {
        logs.clear()
        adapter.notifyDataSetChanged()
        updateLogCount()
    }

    private fun updateLogCount() {
        if (::expandedView.isInitialized) {
            expandedView.findViewWithTag<TextView>("logCount")?.text = logs.size.toString()
        }
    }

    private fun dp(value: Int): Int {
        return (value * activity.resources.displayMetrics.density).toInt()
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
                "Activity" -> 0xFF4CAF50.toInt()
                "ViewGroup" -> 0xFF2196F3.toInt()
                "View" -> 0xFFFF9800.toInt()
                "Child" -> 0xFFFF9800.toInt()
                "Outer" -> 0xFF9C27B0.toInt()
                "Inner" -> 0xFF00BCD4.toInt()
                else -> 0xFF9E9E9E.toInt()
            }
        }

        fun getResultColor(): Int {
            return when (result) {
                "true" -> 0xFF4CAF50.toInt()
                "false" -> 0xFFF44336.toInt()
                "super" -> 0xFFFF9800.toInt()
                else -> 0xFF9E9E9E.toInt()
            }
        }
    }

    /**
     * 日志适配器
     */
    private class LogAdapter(
        private val logs: MutableList<LogItem>
    ) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LinearLayout(parent.context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(dp(parent.context, 4), dp(parent.context, 2), dp(parent.context, 4), dp(parent.context, 2))
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            
            // 创建子视图
            val timestamp = TextView(parent.context).apply {
                textSize = 9f
                setTextColor(0xFF9E9E9E.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            view.addView(timestamp)

            val tag = TextView(parent.context).apply {
                textSize = 10f
                setTypeface(null, Typeface.BOLD)
                setPadding(dp(parent.context, 4), 0, dp(parent.context, 4), 0)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            view.addView(tag)

            val method = TextView(parent.context).apply {
                textSize = 10f
                setTextColor(0xFF616161.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            view.addView(method)

            val action = TextView(parent.context).apply {
                textSize = 10f
                setTextColor(0xFF1565C0.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            view.addView(action)

            val result = TextView(parent.context).apply {
                textSize = 10f
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            view.addView(result)

            return ViewHolder(view, timestamp, tag, method, action, result)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(logs[position])
        }

        override fun getItemCount(): Int = logs.size

        class ViewHolder(
            itemView: View,
            private val tvTimestamp: TextView,
            private val tvTag: TextView,
            private val tvMethod: TextView,
            private val tvAction: TextView,
            private val tvResult: TextView
        ) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: LogItem) {
                tvTimestamp.text = item.timestamp
                tvTag.text = item.tag
                tvTag.setTextColor(item.getTagColor())
                tvMethod.text = item.method
                tvAction.text = item.action
                
                if (item.result.isNotEmpty()) {
                    tvResult.visibility = View.VISIBLE
                    tvResult.text = "→ ${item.result}"
                    tvResult.setTextColor(item.getResultColor())
                } else {
                    tvResult.visibility = View.GONE
                }
            }
        }
        
        private fun dp(context: android.content.Context, value: Int): Int {
            return (value * context.resources.displayMetrics.density).toInt()
        }
    }
}