package com.peter.room.demo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.peter.room.demo.db.converter.DateConverter
import com.peter.room.demo.db.converter.ListConverter
import com.peter.room.demo.db.converter.PriorityConverter
import com.peter.room.demo.db.dao.ArticleDao
import com.peter.room.demo.db.dao.ContactDao
import com.peter.room.demo.db.dao.CourseDao
import com.peter.room.demo.db.dao.DepartmentDao
import com.peter.room.demo.db.dao.EmployeeDao
import com.peter.room.demo.db.dao.StudentCourseDao
import com.peter.room.demo.db.dao.StudentDao
import com.peter.room.demo.db.dao.TaskDao
import com.peter.room.demo.db.dao.UserDao
import com.peter.room.demo.db.entity.Article
import com.peter.room.demo.db.entity.Contact
import com.peter.room.demo.db.entity.Course
import com.peter.room.demo.db.entity.Department
import com.peter.room.demo.db.entity.Employee
import com.peter.room.demo.db.entity.Student
import com.peter.room.demo.db.entity.StudentCourseCrossRef
import com.peter.room.demo.db.entity.Task
import com.peter.room.demo.db.entity.User

/**
 * Room 数据库
 */
@Database(
    entities = [
        User::class,
        Department::class,
        Employee::class,
        Student::class,
        Course::class,
        StudentCourseCrossRef::class,
        Task::class,
        Contact::class,
        Article::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    ListConverter::class,
    PriorityConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun departmentDao(): DepartmentDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun studentDao(): StudentDao
    abstract fun courseDao(): CourseDao
    abstract fun studentCourseDao(): StudentCourseDao
    abstract fun taskDao(): TaskDao
    abstract fun contactDao(): ContactDao
    abstract fun articleDao(): ArticleDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `contacts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `street` TEXT NOT NULL,
                        `city` TEXT NOT NULL,
                        `zipCode` TEXT NOT NULL,
                        `work_street` TEXT NOT NULL,
                        `work_city` TEXT NOT NULL,
                        `work_zipCode` TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `articles` USING `fts4`(`title`, `content`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "room_demo_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
