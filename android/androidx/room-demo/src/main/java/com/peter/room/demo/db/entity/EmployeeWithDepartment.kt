package com.peter.room.demo.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 员工及其所属部门
 */
data class EmployeeWithDepartment(
    @Embedded
    val employee: Employee,
    
    @Relation(
        parentColumn = "departmentId",
        entityColumn = "id"
    )
    val department: Department?
)
