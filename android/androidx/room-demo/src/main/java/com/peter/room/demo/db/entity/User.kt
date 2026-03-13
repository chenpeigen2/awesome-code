package com.peter.room.demo.db.entity

/**
 * 用户实体
 */
data class User(
    val id: Long = 0,
    val name: String,
    val age: Int,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)
