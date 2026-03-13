package com.peter.room.demo.db.dao

import com.peter.room.demo.db.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 用户数据访问对象（模拟 Room DAO）
 * 
 * 由于 Kotlin 2.2.21 和 Room 2.6.1 的 KSP 兼容性问题，
 * 这里使用内存存储模拟 Room 的行为
 */
class UserDao {
    
    private val users = mutableListOf<User>()
    private val usersFlow = MutableStateFlow<List<User>>(emptyList())
    private var nextId = 1L
    
    suspend fun insert(user: User): Long {
        val newUser = user.copy(id = nextId++)
        users.add(0, newUser)
        usersFlow.value = users.toList()
        return newUser.id
    }
    
    suspend fun update(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            users[index] = user
            usersFlow.value = users.toList()
        }
    }
    
    suspend fun delete(user: User) {
        users.removeAll { it.id == user.id }
        usersFlow.value = users.toList()
    }
    
    suspend fun deleteAll() {
        users.clear()
        usersFlow.value = emptyList()
    }
    
    suspend fun getAll(): List<User> = users.toList()
    
    fun observeAll(): Flow<List<User>> = usersFlow.asStateFlow()
}