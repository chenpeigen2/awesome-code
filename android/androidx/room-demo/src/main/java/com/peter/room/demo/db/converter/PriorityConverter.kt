package com.peter.room.demo.db.converter

import androidx.room.TypeConverter

/**
 * Priority 枚举类型转换器
 */
class PriorityConverter {
    
    @TypeConverter
    fun fromPriority(priority: Priority?): String? {
        return priority?.name
    }
    
    @TypeConverter
    fun toPriority(value: String?): Priority? {
        return value?.let { Priority.valueOf(it) }
    }
}
