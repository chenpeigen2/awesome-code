package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.peter.room.demo.db.converter.Priority
import com.peter.room.demo.db.entity.Task
import kotlinx.coroutines.flow.Flow

/**
 * 任务 DAO - 演示类型转换器
 */
@Dao
interface TaskDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long
    
    @Update
    suspend fun update(task: Task)
    
    @Delete
    suspend fun delete(task: Task)
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAll(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?
    
    @Query("SELECT * FROM tasks WHERE priority = :priority")
    suspend fun getByPriority(priority: Priority): List<Task>
    
    @Query("SELECT * FROM tasks WHERE isCompleted = :completed")
    suspend fun getByCompleted(completed: Boolean): List<Task>
    
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Task>>
}
