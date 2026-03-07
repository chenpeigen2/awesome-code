package com.peter.coroutine.demo.data.model

/**
 * 用户数据模型 - 用于 Room 示例
 *
 * @property id 用户 ID
 * @property name 用户名
 * @property email 邮箱
 * @property createdAt 创建时间戳
 */
data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)
