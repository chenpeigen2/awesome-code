package com.peter.coroutine.demo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象 (DAO)
 *
 * 定义数据库操作接口，所有操作都是 suspend 函数，
 * Room 会自动在后台线程执行这些操作。
 *
 * ## Room + 协程的优势
 * 1. **主线程安全**: Room 确保 suspend 函数不会在主线程执行 I/O 操作
 * 2. **事务支持**: @Transaction 注解支持协程事务
 * 3. **Flow 支持**: 查询可以返回 Flow，自动监听数据变化
 *
 * ## 使用示例
 * ```kotlin
 * // 插入用户
 * userDao.insert(UserEntity(name = "张三", email = "zhangsan@example.com"))
 *
 * // 查询所有用户
 * val users = userDao.getAll()
 *
 * // 使用 Flow 监听数据变化
 * userDao.getAllFlow().collect { users ->
 *     // 数据变化时自动更新
 * }
 * ```
 */
@Dao
interface UserDao {

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    suspend fun getAll(): List<UserEntity>

    /**
     * 使用 Flow 查询所有用户
     *
     * 当数据发生变化时，Flow 会自动发射新的数据
     *
     * @return 用户列表的 Flow
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<UserEntity>>

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户实体，如果不存在返回 null
     */
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): UserEntity?

    /**
     * 根据名称搜索用户
     *
     * @param name 用户名（模糊匹配）
     * @return 匹配的用户列表
     */
    @Query("SELECT * FROM users WHERE name LIKE '%' || :name || '%'")
    suspend fun searchByName(name: String): List<UserEntity>

    /**
     * 插入用户
     *
     * @param user 要插入的用户
     * @return 插入的行 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    /**
     * 批量插入用户
     *
     * @param users 要插入的用户列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    /**
     * 更新用户
     *
     * @param user 要更新的用户
     * @return 更新的行数
     */
    @Update
    suspend fun update(user: UserEntity): Int

    /**
     * 删除用户
     *
     * @param user 要删除的用户
     */
    @Delete
    suspend fun delete(user: UserEntity)

    /**
     * 根据 ID 删除用户
     *
     * @param id 用户 ID
     * @return 删除的行数
     */
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    /**
     * 删除所有用户
     */
    @Query("DELETE FROM users")
    suspend fun deleteAll()

    /**
     * 获取用户总数
     *
     * @return 用户数量
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getCount(): Int
}
