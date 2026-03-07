package com.peter.coroutine.demo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户实体类
 *
 * Room 数据库的用户表实体，演示协程与 Room 的集成。
 *
 * @property id 主键，自动生成
 * @property name 用户名
 * @property email 用户邮箱
 * @property createdAt 创建时间戳
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
