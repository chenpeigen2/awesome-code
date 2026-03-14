package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.peter.room.demo.db.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long
    
    @Update
    suspend fun update(user: User)
    
    @Delete
    suspend fun delete(user: User)
    
    @Query("DELETE FROM users")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAll(): List<User>
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): User?
    
    @Query("SELECT * FROM users WHERE name LIKE :name")
    suspend fun findByName(name: String): List<User>
}
