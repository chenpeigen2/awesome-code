package com.peter.room.demo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 课程实体 (多对多关系)
 */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val credit: Int,
    val teacher: String
)
