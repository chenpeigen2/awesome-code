package com.peter.lifecycle.demo.mvvm

import kotlinx.coroutines.delay

/**
 * 数据层 - Repository
 * 
 * 职责：
 * 1. 数据获取（网络、数据库、缓存）
 * 2. 数据缓存策略
 * 3. 数据转换
 * 
 * 好处：
 * - 分离数据源和业务逻辑
 * - 方便测试（可以 mock Repository）
 * - 数据缓存策略统一管理
 */
class UserRepository {

    /**
     * 获取用户列表
     */
    suspend fun getUsers(): Result<List<User>> {
        return try {
            // 模拟网络延迟
            delay(1000)
            
            // 模拟数据
            val users = listOf(
                User("1", "张三", "zhangsan@example.com", 25),
                User("2", "李四", "lisi@example.com", 30),
                User("3", "王五", "wangwu@example.com", 28)
            )
            
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取用户详情
     */
    suspend fun getUser(id: String): Result<User> {
        return try {
            delay(800)
            
            // 模拟数据
            val user = User(id, "用户_$id", "user$id@example.com", 20 + id.hashCode() % 30)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新用户
     */
    suspend fun updateUser(user: User): Result<User> {
        return try {
            delay(500)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * 用户数据类
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int
)
