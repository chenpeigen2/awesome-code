package com.peter.room.demo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 学生实体 (多对多关系)
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val studentNo: String,
    val grade: Int
)
