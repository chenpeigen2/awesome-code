package com.peter.room.demo.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * 学生及其选修课程 (多对多关系)
 */
data class StudentWithCourses(
    @Embedded
    val student: Student,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = StudentCourseCrossRef::class,
            parentColumn = "studentId",
            entityColumn = "courseId"
        )
    )
    val courses: List<Course>
)
