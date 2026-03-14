package com.peter.room.demo.db.converter

import androidx.room.TypeConverter

/**
 * List 类型转换器
 * 将 List 转换为 JSON 字符串存储
 */
class ListConverter {
    
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(SEPARATOR)
    }
    
    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(SEPARATOR)
    }
    
    companion object {
        private const val SEPARATOR = ","
    }
}
