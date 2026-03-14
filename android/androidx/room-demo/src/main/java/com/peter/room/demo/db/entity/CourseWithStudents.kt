package com.peter.room.demo.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * 课程及其选课学生 (多对多关系)
 */
data class CourseWithStudents(
    @Embedded
    val course: Course,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = StudentCourseCrossRef::class,
            parentColumn = "courseId",
            entityColumn = "studentId"
        )
    )
    val students: List<Student>
)
