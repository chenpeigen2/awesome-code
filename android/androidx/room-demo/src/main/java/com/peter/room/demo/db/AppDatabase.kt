package com.peter.room.demo.db

import android.content.Context
import com.peter.room.demo.db.dao.UserDao

/**
 * 数据库单例（模拟 Room Database）
 */
object AppDatabase {
    
    private val userDao by lazy { UserDao() }
    
    fun getDatabase(context: Context): AppDatabase = this
    
    fun userDao(): UserDao = userDao
}