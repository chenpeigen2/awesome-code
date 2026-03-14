package com.peter.mmkv.demo

/**
 * MMKV 功能类型枚举
 */
enum class MMKVFeature {
    // 基本操作
    BASIC_WRITE,        // 写入数据
    BASIC_READ,         // 读取数据
    BASIC_DELETE,       // 删除数据
    BASIC_CONTAINS,     // 检查键是否存在
    
    // 数据类型
    TYPE_INT,           // Int 类型
    TYPE_LONG,          // Long 类型
    TYPE_FLOAT,         // Float 类型
    TYPE_DOUBLE,        // Double 类型
    TYPE_BOOLEAN,       // Boolean 类型
    TYPE_STRING,        // String 类型
    TYPE_STRING_SET,    // Set<String> 类型
    TYPE_BYTE_ARRAY,    // ByteArray 类型
    
    // 高级功能
    ADVANCED_MULTI_INSTANCE,    // 多实例
    ADVANCED_ENCRYPT,           // 加密存储
    ADVANCED_MIGRATE,           // 迁移 SharedPreferences
    ADVANCED_BACKUP,            // 备份与恢复
    ADVANCED_CLEAR,             // 清空数据
    ADVANCED_ALL_KEYS,          // 获取所有键
    ADVANCED_SIZE,              // 存储大小
}

/**
 * 功能分类
 */
enum class FeatureCategory(val displayName: String) {
    BASIC("基本操作"),
    DATA_TYPE("数据类型"),
    ADVANCED("高级功能")
}

/**
 * 功能项数据模型
 */
data class FeatureItem(
    val feature: MMKVFeature,
    val title: String,
    val description: String,
    val category: FeatureCategory
)
