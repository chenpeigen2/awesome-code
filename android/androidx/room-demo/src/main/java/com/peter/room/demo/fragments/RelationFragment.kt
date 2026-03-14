package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.room.demo.R
import com.peter.room.demo.databinding.FragmentRelationBinding
import com.peter.room.demo.db.AppDatabase
import com.peter.room.demo.db.entity.Course
import com.peter.room.demo.db.entity.Department
import com.peter.room.demo.db.entity.Employee
import com.peter.room.demo.db.entity.Student
import com.peter.room.demo.db.entity.StudentCourseCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 关系映射演示 Fragment
 * 演示 Room 的一对多和多对多关系
 */
class RelationFragment : Fragment() {

    private var _binding: FragmentRelationBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase

    companion object {
        fun newInstance() = RelationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnLoadDepartment.setOnClickListener {
            loadDepartmentWithEmployees()
        }
        
        binding.btnLoadStudent.setOnClickListener {
            loadStudentWithCourses()
        }
    }

    private fun loadDepartmentWithEmployees() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 先插入测试数据
                insertTestDataIfNeeded()
                
                // 加载部门及其员工
                val departmentsWithEmployees = withContext(Dispatchers.IO) {
                    database.departmentDao().getDepartmentsWithEmployees()
                }
                
                if (departmentsWithEmployees.isEmpty()) {
                    binding.tvDepartmentResult.text = "暂无数据"
                } else {
                    val result = buildString {
                        departmentsWithEmployees.forEach { deptWithEmp ->
                            append("📁 ${deptWithEmp.department.name} (${deptWithEmp.department.location})\n")
                            deptWithEmp.employees.forEach { emp ->
                                append("   👤 ${emp.name} - ${emp.position}\n")
                            }
                            append("\n")
                        }
                    }
                    binding.tvDepartmentResult.text = result.trim()
                }
                binding.tvDepartmentResult.visibility = View.VISIBLE
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadStudentWithCourses() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 先插入测试数据
                insertTestDataIfNeeded()
                
                // 加载学生及其课程
                val studentsWithCourses = withContext(Dispatchers.IO) {
                    database.studentDao().getStudentsWithCourses()
                }
                
                if (studentsWithCourses.isEmpty()) {
                    binding.tvStudentResult.text = "暂无数据"
                } else {
                    val result = buildString {
                        studentsWithCourses.forEach { studentWithCourse ->
                            append("🎓 ${studentWithCourse.student.name} (${studentWithCourse.student.studentNo})\n")
                            studentWithCourse.courses.forEach { course ->
                                append("   📚 ${course.name} - ${course.credit}学分\n")
                            }
                            append("\n")
                        }
                    }
                    binding.tvStudentResult.text = result.trim()
                }
                binding.tvStudentResult.visibility = View.VISIBLE
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun insertTestDataIfNeeded() {
        withContext(Dispatchers.IO) {
            // 检查是否已有数据
            val existingDepts = database.departmentDao().getAll()
            if (existingDepts.isNotEmpty()) return@withContext
            
            // 插入部门数据
            val dept1 = database.departmentDao().insert(Department(name = "研发部", location = "A栋3楼"))
            val dept2 = database.departmentDao().insert(Department(name = "产品部", location = "B栋2楼"))
            val dept3 = database.departmentDao().insert(Department(name = "市场部", location = "C栋1楼"))
            
            // 插入员工数据
            database.employeeDao().insert(Employee(name = "张三", position = "高级工程师", salary = 25000.0, departmentId = dept1))
            database.employeeDao().insert(Employee(name = "李四", position = "中级工程师", salary = 18000.0, departmentId = dept1))
            database.employeeDao().insert(Employee(name = "王五", position = "产品经理", salary = 20000.0, departmentId = dept2))
            database.employeeDao().insert(Employee(name = "赵六", position = "市场专员", salary = 12000.0, departmentId = dept3))
            
            // 插入学生数据
            val student1 = database.studentDao().insert(Student(name = "小明", studentNo = "S001", grade = 1))
            val student2 = database.studentDao().insert(Student(name = "小红", studentNo = "S002", grade = 2))
            val student3 = database.studentDao().insert(Student(name = "小刚", studentNo = "S003", grade = 1))
            
            // 插入课程数据
            val course1 = database.courseDao().insert(Course(name = "数据结构", credit = 4, teacher = "王教授"))
            val course2 = database.courseDao().insert(Course(name = "算法设计", credit = 3, teacher = "李教授"))
            val course3 = database.courseDao().insert(Course(name = "数据库原理", credit = 3, teacher = "张教授"))
            
            // 插入选课关系（多对多）
            database.studentCourseDao().insert(StudentCourseCrossRef(studentId = student1, courseId = course1))
            database.studentCourseDao().insert(StudentCourseCrossRef(studentId = student1, courseId = course2))
            database.studentCourseDao().insert(StudentCourseCrossRef(studentId = student2, courseId = course2))
            database.studentCourseDao().insert(StudentCourseCrossRef(studentId = student2, courseId = course3))
            database.studentCourseDao().insert(StudentCourseCrossRef(studentId = student3, courseId = course1))
            database.studentCourseDao().insert(StudentCourseCrossRef(studentId = student3, courseId = course3))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
