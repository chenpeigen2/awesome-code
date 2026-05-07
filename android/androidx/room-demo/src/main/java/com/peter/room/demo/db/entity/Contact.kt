package com.peter.room.demo.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    @Embedded val homeAddress: Address,
    @Embedded(prefix = "work_") val workAddress: Address
)
