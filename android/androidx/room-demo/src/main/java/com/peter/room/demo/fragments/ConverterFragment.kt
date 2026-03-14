package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.room.demo.databinding.FragmentConverterBinding
import com.peter.room.demo.db.AppDatabase
import com.peter.room.demo.db.converter.Priority
import com.peter.room.demo.db.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * 类型转换器演示 Fragment
 * 演示 Room 的类型转换器功能
 */
class ConverterFragment : Fragment() {

    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase

    companion object {
        fun newInstance() = ConverterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        
        // 插入演示数据
        insertDemoTasks()
    }

    private fun insertDemoTasks() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val tasks = withContext(Dispatchers.IO) {
                    // 先清除旧数据
                    database.taskDao().deleteAll()
                    
                    // 插入演示任务，展示各种类型转换
                    val task1 = Task(
                        title = "完成项目报告",
                        description = "Q4季度项目总结报告",
                        priority = Priority.HIGH,
                        dueDate = Date(System.currentTimeMillis() + 86400000), // 明天
                        tags = listOf("工作", "重要", "报告")
                    )
                    
                    val task2 = Task(
                        title = "学习 Room 数据库",
                        description = "完成 Room 类型转换器学习",
                        priority = Priority.MEDIUM,
                        dueDate = Date(System.currentTimeMillis() + 172800000), // 后天
                        tags = listOf("学习", "Android", "Room")
                    )
                    
                    val task3 = Task(
                        title = "整理文档",
                        description = "整理项目技术文档",
                        priority = Priority.LOW,
                        dueDate = null, // 无截止日期
                        tags = listOf("文档")
                    )
                    
                    database.taskDao().insert(task1)
                    database.taskDao().insert(task2)
                    database.taskDao().insert(task3)
                    
                    // 查询并返回
                    database.taskDao().getAll()
                }
                
                // 显示结果
                showTasksResult(tasks)
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "演示失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTasksResult(tasks: List<Task>) {
        viewLifecycleOwner.lifecycleScope.launch {
            // 这里可以更新 UI 显示任务列表
            // 由于布局文件主要是说明性的，我们用 Toast 显示
            val message = buildString {
                append("已插入 ${tasks.size} 个任务演示类型转换器:\n")
                tasks.forEach { task ->
                    append("\n📌 ${task.title}")
                    append("\n   优先级: ${task.priority.name} (枚举→字符串)")
                    append("\n   截止日期: ${task.dueDate?.toString() ?: "无"} (Date→Long)")
                    append("\n   标签: ${task.tags.joinToString()} (List→String)")
                }
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}