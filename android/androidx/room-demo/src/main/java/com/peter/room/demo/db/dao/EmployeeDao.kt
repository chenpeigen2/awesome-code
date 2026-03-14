package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.peter.room.demo.db.entity.Employee
import com.peter.room.demo.db.entity.EmployeeWithDepartment
import kotlinx.coroutines.flow.Flow

/**
 * 员工 DAO
 */
@Dao
interface EmployeeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: Employee): Long
    
    @Update
    suspend fun update(employee: Employee)
    
    @Delete
    suspend fun delete(employee: Employee)
    
    @Query("DELETE FROM employees")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM employees")
    suspend fun getAll(): List<Employee>
    
    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Long): Employee?
    
    @Query("SELECT * FROM employees WHERE departmentId = :departmentId")
    suspend fun getByDepartmentId(departmentId: Long): List<Employee>
    
    @Transaction
    @Query("SELECT * FROM employees")
    suspend fun getEmployeesWithDepartment(): List<EmployeeWithDepartment>
    
    @Transaction
    @Query("SELECT * FROM employees WHERE id = :employeeId")
    suspend fun getEmployeeWithDepartment(employeeId: Long): EmployeeWithDepartment?
    
    // 高级查询示例
    @Query("SELECT * FROM employees WHERE salary >= :minSalary ORDER BY salary DESC")
    suspend fun getByMinSalary(minSalary: Double): List<Employee>
    
    @Query("SELECT * FROM employees WHERE name LIKE '%' || :keyword || '%'")
    suspend fun searchByName(keyword: String): List<Employee>
    
    @Query("SELECT AVG(salary) FROM employees WHERE departmentId = :departmentId")
    suspend fun getAverageSalaryByDepartment(departmentId: Long): Double?
    
    @Transaction
    @Query("SELECT * FROM employees")
    fun observeEmployeesWithDepartment(): Flow<List<EmployeeWithDepartment>>
}
