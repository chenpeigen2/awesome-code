package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.peter.room.demo.db.entity.Department
import com.peter.room.demo.db.entity.DepartmentWithEmployees
import kotlinx.coroutines.flow.Flow

/**
 * 部门 DAO
 */
@Dao
interface DepartmentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(department: Department): Long
    
    @Update
    suspend fun update(department: Department)
    
    @Delete
    suspend fun delete(department: Department)
    
    @Query("DELETE FROM departments")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM departments")
    suspend fun getAll(): List<Department>
    
    @Query("SELECT * FROM departments WHERE id = :id")
    suspend fun getById(id: Long): Department?
    
    @Transaction
    @Query("SELECT * FROM departments")
    suspend fun getDepartmentsWithEmployees(): List<DepartmentWithEmployees>
    
    @Transaction
    @Query("SELECT * FROM departments WHERE id = :departmentId")
    suspend fun getDepartmentWithEmployees(departmentId: Long): DepartmentWithEmployees?
    
    @Transaction
    @Query("SELECT * FROM departments")
    fun observeDepartmentsWithEmployees(): Flow<List<DepartmentWithEmployees>>
}
