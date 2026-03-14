package com.peter.room.demo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.peter.room.demo.db.converter.DateConverter
import com.peter.room.demo.db.converter.ListConverter
import com.peter.room.demo.db.converter.Priority
import com.peter.room.demo.db.converter.PriorityConverter
import java.util.Date

/**
 * 任务实体 - 演示类型转换器
 */
@Entity(tableName = "tasks")
@TypeConverters(DateConverter::class, ListConverter::class, PriorityConverter::class)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Date?,
    val tags: List<String>,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date()
)
