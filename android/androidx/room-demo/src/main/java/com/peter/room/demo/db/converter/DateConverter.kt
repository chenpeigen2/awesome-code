package com.peter.room.demo.db.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Date 类型转换器
 * Room 只支持基本类型，需要转换器来存储复杂类型
 */
class DateConverter {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
