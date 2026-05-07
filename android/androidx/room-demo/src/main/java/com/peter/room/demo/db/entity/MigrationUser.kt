package com.peter.room.demo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 迁移演示用户实体 (版本 2)
 * 比 User 多了一个 phone 字段
 */
@Entity(tableName = "migration_users")
data class MigrationUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val email: String,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
