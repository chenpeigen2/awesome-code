package com.peter.room.demo.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 部门及其员工 (一对多关系)
 */
data class DepartmentWithEmployees(
    @Embedded
    val department: Department,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "departmentId"
    )
    val employees: List<Employee>
)
