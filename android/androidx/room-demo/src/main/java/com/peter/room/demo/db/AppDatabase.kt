package com.peter.room.demo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.peter.room.demo.db.converter.DateConverter
import com.peter.room.demo.db.converter.ListConverter
import com.peter.room.demo.db.converter.PriorityConverter
import com.peter.room.demo.db.dao.CourseDao
import com.peter.room.demo.db.dao.DepartmentDao
import com.peter.room.demo.db.dao.EmployeeDao
import com.peter.room.demo.db.dao.StudentCourseDao
import com.peter.room.demo.db.dao.StudentDao
import com.peter.room.demo.db.dao.TaskDao
import com.peter.room.demo.db.dao.UserDao
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
        Task::class
    ],
    version = 1,
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
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "room_demo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
