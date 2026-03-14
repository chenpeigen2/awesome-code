package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.peter.room.demo.db.entity.StudentCourseCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * 学生-课程关联 DAO
 */
@Dao
interface StudentCourseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: StudentCourseCrossRef): Long
    
    @Update
    suspend fun update(crossRef: StudentCourseCrossRef)
    
    @Delete
    suspend fun delete(crossRef: StudentCourseCrossRef)
    
    @Query("DELETE FROM student_course_cross_ref")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM student_course_cross_ref WHERE studentId = :studentId")
    suspend fun getByStudentId(studentId: Long): List<StudentCourseCrossRef>
    
    @Query("SELECT * FROM student_course_cross_ref WHERE courseId = :courseId")
    suspend fun getByCourseId(courseId: Long): List<StudentCourseCrossRef>
    
    @Query("DELETE FROM student_course_cross_ref WHERE studentId = :studentId AND courseId = :courseId")
    suspend fun deleteByStudentAndCourse(studentId: Long, courseId: Long)
    
    @Query("UPDATE student_course_cross_ref SET score = :score WHERE studentId = :studentId AND courseId = :courseId")
    suspend fun updateScore(studentId: Long, courseId: Long, score: Double)
    
    @Query("SELECT * FROM student_course_cross_ref WHERE studentId = :studentId AND courseId = :courseId LIMIT 1")
    suspend fun getByStudentAndCourse(studentId: Long, courseId: Long): StudentCourseCrossRef?
    
    @Query("SELECT AVG(score) FROM student_course_cross_ref WHERE courseId = :courseId AND score IS NOT NULL")
    suspend fun getAverageScoreByCourse(courseId: Long): Double?
    
    @Query("SELECT * FROM student_course_cross_ref")
    fun observeAll(): Flow<List<StudentCourseCrossRef>>
}
