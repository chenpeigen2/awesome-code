package com.peter.room.demo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.peter.room.demo.db.dao.MigrationUserDao
import com.peter.room.demo.db.entity.MigrationUser

/**
 * 迁移演示专用数据库
 * 独立于 AppDatabase，专门用于演示 Room 数据库迁移
 *
 * 版本说明：
 * - Version 1: migration_users (id, name, age, email, createdAt)
 * - Version 2: migration_users (id, name, age, email, phone, createdAt)
 */
@Database(
    entities = [MigrationUser::class],
    version = 2,
    exportSchema = false
)
abstract class MigrationDemoDatabase : RoomDatabase() {

    abstract fun migrationUserDao(): MigrationUserDao

    companion object {
        private const val DB_NAME = "migration_demo_database"

        /**
         * Migration(1, 2): 为 migration_users 表添加 phone 列
         * 使用 ALTER TABLE ADD COLUMN，为已有数据填充默认值 ""
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE migration_users ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
            }
        }

        /**
         * 创建 v2 版本的数据库 (含 Migration(1,2))
         * 如果存在 v1 数据库，会执行迁移保留数据
         */
        fun getV2Database(context: Context): MigrationDemoDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MigrationDemoDatabase::class.java,
                DB_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        /**
         * 删除迁移演示数据库文件
         */
        fun deleteDatabase(context: Context): Boolean {
            return context.deleteDatabase(DB_NAME)
        }
    }
}
