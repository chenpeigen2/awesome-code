package com.peter.coroutine.demo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room 数据库类
 *
 * 本数据库演示 Room 与协程的集成使用。
 *
 * ## Room 数据库配置
 * - **entities**: 数据库包含的实体类
 * - **version**: 数据库版本号
 * - **exportSchema**: 是否导出 Schema 文件
 *
 * ## 单例模式
 * 使用 companion object 实现单例，确保整个应用只有一个数据库实例。
 *
 * ## 使用示例
 * ```kotlin
 * // 获取数据库实例
 * val database = AppDatabase.getInstance(context)
 *
 * // 获取 DAO
 * val userDao = database.userDao()
 *
 * // 使用协程进行数据库操作
 * lifecycleScope.launch {
 *     userDao.insert(UserEntity(name = "张三", email = "test@example.com"))
 *     val users = userDao.getAll()
 * }
 * ```
 *
 * ## Room + 协程注意事项
 * 1. Room 的 suspend 函数自动在 IO 线程执行
 * 2. 不需要手动切换到 IO 线程
 * 3. 数据库操作默认是事务性的
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取用户 DAO
     */
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "coroutine_demo_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库单例实例
         *
         * @param context 应用上下文
         * @return AppDatabase 实例
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // 允许主线程查询（仅用于演示，生产环境不推荐）
                // .allowMainThreadQueries()
                // 数据库迁移策略（版本升级时使用）
                // .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
