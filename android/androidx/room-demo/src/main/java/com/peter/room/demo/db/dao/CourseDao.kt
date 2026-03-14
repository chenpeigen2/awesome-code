package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.peter.room.demo.db.entity.Course
import com.peter.room.demo.db.entity.CourseWithStudents
import kotlinx.coroutines.flow.Flow

/**
 * 课程 DAO
 */
@Dao
interface CourseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course): Long
    
    @Update
    suspend fun update(course: Course)
    
    @Delete
    suspend fun delete(course: Course)
    
    @Query("DELETE FROM courses")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM courses")
    suspend fun getAll(): List<Course>
    
    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getById(id: Long): Course?
    
    @Transaction
    @Query("SELECT * FROM courses")
    suspend fun getCoursesWithStudents(): List<CourseWithStudents>
    
    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseWithStudents(courseId: Long): CourseWithStudents?
    
    // 高级查询
    @Query("SELECT * FROM courses WHERE credit >= :minCredit")
    suspend fun getByMinCredit(minCredit: Int): List<Course>
    
    @Query("SELECT * FROM courses WHERE teacher = :teacher")
    suspend fun getByTeacher(teacher: String): List<Course>
    
    @Query("SELECT * FROM courses WHERE name LIKE '%' || :keyword || '%'")
    suspend fun searchByName(keyword: String): List<Course>
    
    @Transaction
    @Query("SELECT * FROM courses")
    fun observeCoursesWithStudents(): Flow<List<CourseWithStudents>>
}
