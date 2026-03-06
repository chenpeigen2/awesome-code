package com.peter.layoutinflater.demo.dynamic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityDynamicViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 动态添加 View 示例
 * 
 * 【核心场景】
 * 1. 根据数据动态创建 View
 * 2. 用户交互触发添加 View
 * 3. 条件性添加不同的布局
 * 
 * 【关键方法】
 * - ViewGroup.addView(View)
 * - ViewGroup.addView(View, index)
 * - ViewGroup.addView(View, LayoutParams)
 * - ViewGroup.removeView(View)
 * - ViewGroup.removeAllViews()
 */
class DynamicViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDynamicViewBinding
    private val logBuilder = StringBuilder()
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 打印 LayoutInflater 信息
        printLayoutInflaterInfo()
        
        binding = ActivityDynamicViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        updateLog("初始化完成")
    }

    /**
     * 打印当前 Activity 的 LayoutInflater 信息
     */
    private fun printLayoutInflaterInfo() {
        val inflater = layoutInflater
        val sb = StringBuilder()
        sb.appendLine("========== DynamicViewActivity ==========")
        sb.appendLine("LayoutInflater 实例: $inflater")
        sb.appendLine("hashCode: ${inflater.hashCode()}")
        sb.appendLine("Factory: ${inflater.factory}")
        sb.appendLine("Factory2: ${inflater.factory2}")
        sb.appendLine("===========================================")
        android.util.Log.d("LayoutInflaterDemo", sb.toString())
    }

    private fun setupButtons() {
        binding.btnAddSimple.setOnClickListener {
            addViewSimple()
        }

        binding.btnAddWithData.setOnClickListener {
            addViewWithData()
        }

        binding.btnAddAtPosition.setOnClickListener {
            addViewAtPosition()
        }

        binding.btnClear.setOnClickListener {
            clearContainer()
        }
    }

    /**
     * 方式一：简单添加 View
     * 
     * 使用 attachToRoot = true，View 自动添加到容器
     */
    private fun addViewSimple() {
        counter++

        // 【方式一】使用 attachToRoot = true
        // View 会自动添加到容器，返回值是容器本身
        val inflater = LayoutInflater.from(this)
        val container: LinearLayout = binding.container

        // 返回值是 container，不是加载的 View
        inflater.inflate(R.layout.item_dynamic, container, true)

        // 获取刚添加的 View（最后一个子 View）
        val addedView = container.getChildAt(container.childCount - 1)

        // 设置内容
        addedView.findViewById<TextView>(R.id.tvIndex).text = counter.toString()
        addedView.findViewById<TextView>(R.id.tvMessage).text = "简单添加的 View #${counter}"

        // 设置删除按钮
        addedView.findViewById<Button>(R.id.btnRemove).setOnClickListener {
            container.removeView(addedView)
            updateLog("删除了 View #${counter}")
        }

        updateLog("简单添加: View #$counter (attachToRoot=true)")
    }

    /**
     * 方式二：先加载再添加（带数据处理）
     * 
     * 使用 attachToRoot = false，手动 addView
     * 可以在添加前对 View 进行处理
     */
    private fun addViewWithData() {
        counter++

        // 【方式二】attachToRoot = false，手动添加
        val inflater = LayoutInflater.from(this)
        val container: LinearLayout = binding.container

        // 加载布局，不自动添加
        val view = inflater.inflate(R.layout.item_dynamic, container, false)

        // 【优势】可以在添加前对 View 进行复杂处理
        val indexTextView = view.findViewById<TextView>(R.id.tvIndex)
        val messageTextView = view.findViewById<TextView>(R.id.tvMessage)
        val removeButton = view.findViewById<Button>(R.id.btnRemove)

        // 设置数据
        indexTextView.text = counter.toString()
        messageTextView.text = "带数据添加的 View #${counter}\n创建时间: ${getCurrentTime()}"

        // 设置删除按钮
        removeButton.setOnClickListener {
            container.removeView(view)
            updateLog("删除了 View #${counter}")
        }

        // 还可以设置其他属性
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(300).start()

        // 手动添加到容器
        container.addView(view)

        updateLog("带数据添加: View #$counter (attachToRoot=false)")
    }

    /**
     * 方式三：在指定位置添加 View
     * 
     * 使用 addView(view, index) 插入到特定位置
     */
    private fun addViewAtPosition() {
        counter++

        val inflater = LayoutInflater.from(this)
        val container: LinearLayout = binding.container

        val view = inflater.inflate(R.layout.item_dynamic, container, false)

        // 设置数据
        view.findViewById<TextView>(R.id.tvIndex).text = counter.toString()
        view.findViewById<TextView>(R.id.tvMessage).text = "插入位置的 View #${counter}"

        // 设置删除按钮
        view.findViewById<Button>(R.id.btnRemove).setOnClickListener {
            container.removeView(view)
            updateLog("删除了 View #${counter}")
        }

        // 【关键】在指定位置插入
        // 插入到开头（index = 0）
        val insertPosition = if (container.childCount > 0) {
            // 插入到第一个位置
            0
        } else {
            0
        }

        container.addView(view, insertPosition)

        updateLog("位置插入: View #$counter at index $insertPosition")
    }

    /**
     * 清空容器
     */
    private fun clearContainer() {
        val count = binding.container.childCount
        binding.container.removeAllViews()
        updateLog("清空容器: 移除了 $count 个 View")
        Toast.makeText(this, "已清空 $count 个 View", Toast.LENGTH_SHORT).show()
    }

    /**
     * 更新日志
     */
    private fun updateLog(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logBuilder.insert(0, "[$timestamp] $message\n")

        // 限制日志行数
        val lines = logBuilder.toString().split("\n")
        if (lines.size > 20) {
            logBuilder.clear()
            lines.take(20).forEach { line ->
                if (line.isNotEmpty()) {
                    logBuilder.append(line).append("\n")
                }
            }
        }

        binding.tvLog.text = logBuilder.toString()
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}

/**
 * 【进阶】动态添加 View 的高级用法
 * 
 * 1. 使用 ViewStub 进行延迟加载
 * ```kotlin
 * // 在布局中使用 ViewStub
 * <ViewStub
 *     android:id="@+id/viewStub"
 *     android:layout="@layout/item_dynamic"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * 
 * // 代码中展开
 * val view = binding.viewStub.inflate()
 * // 或者
 * binding.viewStub.visibility = View.VISIBLE
 * ```
 * 
 * 2. 动态添加不同类型的布局
 * ```kotlin
 * fun addItem(type: Int) {
 *     val layoutRes = when (type) {
 *         TYPE_HEADER -> R.layout.item_header
 *         TYPE_CONTENT -> R.layout.item_content
 *         TYPE_FOOTER -> R.layout.item_footer
 *         else -> R.layout.item_default
 *     }
 *     val view = inflater.inflate(layoutRes, container, false)
 *     container.addView(view)
 * }
 * ```
 * 
 * 3. 使用 Merge 优化层级
 * ```xml
 * <!-- item_merge.xml -->
 * <merge xmlns:android="...">
 *     <TextView ... />
 *     <Button ... />
 * </merge>
 * ```
 * ```kotlin
 * // 加载 merge 布局时必须指定 parent
 * inflater.inflate(R.layout.item_merge, container, true)
 * ```
 * 
 * 4. 添加动画效果
 * ```kotlin
 * // 添加时动画
 * view.alpha = 0f
 * view.translationY = -100f
 * container.addView(view)
 * view.animate()
 *     .alpha(1f)
 *     .translationY(0f)
 *     .setDuration(300)
 *     .start()
 * 
 * // 删除时动画
 * view.animate()
 *     .alpha(0f)
 *     .translationX(-view.width.toFloat())
 *     .withEndAction { container.removeView(view) }
 *     .setDuration(200)
 *     .start()
 * ```
 */
