package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peter.room.demo.db.entity.MigrationUser

/**
 * 迁移演示用户 DAO
 */
@Dao
interface MigrationUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: MigrationUser): Long

    @Query("SELECT * FROM migration_users ORDER BY createdAt DESC")
    suspend fun getAll(): List<MigrationUser>

    @Query("DELETE FROM migration_users")
    suspend fun deleteAll()
}
