package com.peter.room.demo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 部门实体 (一对多关系的一方)
 */
@Entity(tableName = "departments")
data class Department(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val location: String
)
