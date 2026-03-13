package com.peter.room.demo.repository

import com.peter.room.demo.db.dao.UserDao
import com.peter.room.demo.db.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库
 */
class UserRepository(private val userDao: UserDao) {
    
    val allUsers: Flow<List<User>> = userDao.observeAll()
    
    suspend fun addUser(name: String, age: Int, email: String): Long {
        val user = User(name = name, age = age, email = email)
        return userDao.insert(user)
    }
    
    suspend fun updateUser(user: User) {
        userDao.update(user)
    }
    
    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }
    
    suspend fun deleteAllUsers() {
        userDao.deleteAll()
    }
}