package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.room.demo.databinding.FragmentAdvancedBinding
import com.peter.room.demo.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 高级查询演示 Fragment
 * 演示 Room 的高级功能：Flow 观察、事务、复杂查询等
 */
class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase

    companion object {
        fun newInstance() = AdvancedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        
        // 演示高级查询
        demonstrateAdvancedQueries()
    }

    private fun demonstrateAdvancedQueries() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    val sb = StringBuilder()
                    
                    // 1. 复杂查询示例 - 薪资统计
                    val departments = database.departmentDao().getAll()
                    if (departments.isNotEmpty()) {
                        sb.appendLine("=== 薪资统计 (聚合查询) ===")
                        departments.forEach { dept ->
                            val avgSalary = database.employeeDao()
                                .getAverageSalaryByDepartment(dept.id)
                            sb.appendLine("${dept.name}: 平均薪资 ¥${String.format("%.0f", avgSalary ?: 0.0)}")
                        }
                        sb.appendLine()
                    }
                    
                    // 2. 模糊搜索示例
                    val searchResults = database.employeeDao().searchByName("张")
                    sb.appendLine("=== 模糊搜索 '张' ===")
                    searchResults.forEach { emp ->
                        sb.appendLine("找到: ${emp.name} - ${emp.position}")
                    }
                    sb.appendLine()
                    
                    // 3. 条件查询 - 高薪员工
                    val highSalaryEmployees = database.employeeDao().getByMinSalary(15000.0)
                    sb.appendLine("=== 薪资 >= 15000 的员工 ===")
                    highSalaryEmployees.forEach { emp ->
                        sb.appendLine("${emp.name}: ¥${String.format("%.0f", emp.salary)}")
                    }
                    sb.appendLine()
                    
                    // 4. 多对多关系查询
                    val coursesWithStudents = database.courseDao().getCoursesWithStudents()
                    sb.appendLine("=== 课程选课人数统计 ===")
                    coursesWithStudents.forEach { cws ->
                        sb.appendLine("${cws.course.name}: ${cws.students.size} 人选修")
                    }
                    
                    sb.toString()
                }
                
                // 显示结果
                showResults(results)
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "演示失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showResults(result: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}