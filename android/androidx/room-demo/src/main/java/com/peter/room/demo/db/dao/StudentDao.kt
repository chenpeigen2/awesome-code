package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.peter.room.demo.db.entity.Student
import com.peter.room.demo.db.entity.StudentWithCourses
import kotlinx.coroutines.flow.Flow

/**
 * 学生 DAO
 */
@Dao
interface StudentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student): Long
    
    @Update
    suspend fun update(student: Student)
    
    @Delete
    suspend fun delete(student: Student)
    
    @Query("DELETE FROM students")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM students")
    suspend fun getAll(): List<Student>
    
    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getById(id: Long): Student?
    
    @Query("SELECT * FROM students WHERE studentNo = :studentNo")
    suspend fun getByStudentNo(studentNo: String): Student?
    
    @Transaction
    @Query("SELECT * FROM students")
    suspend fun getStudentsWithCourses(): List<StudentWithCourses>
    
    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentWithCourses(studentId: Long): StudentWithCourses?
    
    // 高级查询
    @Query("SELECT * FROM students WHERE grade = :grade")
    suspend fun getByGrade(grade: Int): List<Student>
    
    @Query("SELECT * FROM students WHERE name LIKE '%' || :keyword || '%'")
    suspend fun searchByName(keyword: String): List<Student>
    
    @Transaction
    @Query("SELECT * FROM students")
    fun observeStudentsWithCourses(): Flow<List<StudentWithCourses>>
}
