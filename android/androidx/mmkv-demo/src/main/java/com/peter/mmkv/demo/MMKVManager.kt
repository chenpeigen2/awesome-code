package com.peter.mmkv.demo

import android.content.Context
import android.content.SharedPreferences
import com.tencent.mmkv.MMKV
import org.json.JSONObject

/**
 * MMKV 管理工具类
 * 封装 MMKV 常用操作，提供统一的存储接口
 */
object MMKVManager {

    // 默认实例
    private var defaultMMKV: MMKV? = null
    
    // 自定义实例缓存
    private val instanceCache = mutableMapOf<String, MMKV>()

    /**
     * 初始化 MMKV（通常在 Application 中调用）
     */
    fun init(context: Context): String {
        return MMKV.initialize(context)
    }

    /**
     * 获取默认 MMKV 实例
     */
    fun getDefault(): MMKV {
        return defaultMMKV ?: MMKV.defaultMMKV().also { defaultMMKV = it }
    }

    /**
     * 获取自定义 MMKV 实例
     * @param mmapID 实例 ID，不同 ID 对应不同文件
     */
    fun getInstance(mmapID: String): MMKV {
        return instanceCache.getOrPut(mmapID) {
            MMKV.mmkvWithID(mmapID)
        }
    }

    /**
     * 获取加密的 MMKV 实例
     * @param mmapID 实例 ID
     * @param cryptKey 加密密钥
     */
    fun getEncryptedInstance(mmapID: String, cryptKey: String): MMKV {
        return instanceCache.getOrPut("${mmapID}_encrypted") {
            MMKV.mmkvWithID(mmapID, MMKV.SINGLE_PROCESS_MODE, cryptKey)
        }
    }

    /**
     * 获取多进程支持的 MMKV 实例
     * @param mmapID 实例 ID
     */
    fun getMultiProcessInstance(mmapID: String): MMKV {
        return instanceCache.getOrPut("${mmapID}_multi") {
            MMKV.mmkvWithID(mmapID, MMKV.MULTI_PROCESS_MODE)
        }
    }

    // ==================== 基本操作 ====================

    /**
     * 存储数据（自动推断类型）
     */
    fun put(key: String, value: Any?, instance: MMKV = getDefault()) {
        when (value) {
            null -> instance.removeValueForKey(key)
            is Int -> instance.encode(key, value)
            is Long -> instance.encode(key, value)
            is Float -> instance.encode(key, value)
            is Double -> instance.encode(key, value)
            is Boolean -> instance.encode(key, value)
            is String -> instance.encode(key, value)
            is Set<*> -> @Suppress("UNCHECKED_CAST")
                instance.encode(key, value as Set<String>)
            is ByteArray -> instance.encode(key, value)
            else -> instance.encode(key, value.toString())
        }
    }

    /**
     * 获取 Int 值
     */
    fun getInt(key: String, defaultValue: Int = 0, instance: MMKV = getDefault()): Int {
        return instance.decodeInt(key, defaultValue)
    }

    /**
     * 获取 Long 值
     */
    fun getLong(key: String, defaultValue: Long = 0L, instance: MMKV = getDefault()): Long {
        return instance.decodeLong(key, defaultValue)
    }

    /**
     * 获取 Float 值
     */
    fun getFloat(key: String, defaultValue: Float = 0f, instance: MMKV = getDefault()): Float {
        return instance.decodeFloat(key, defaultValue)
    }

    /**
     * 获取 Double 值
     */
    fun getDouble(key: String, defaultValue: Double = 0.0, instance: MMKV = getDefault()): Double {
        return instance.decodeDouble(key, defaultValue)
    }

    /**
     * 获取 Boolean 值
     */
    fun getBoolean(key: String, defaultValue: Boolean = false, instance: MMKV = getDefault()): Boolean {
        return instance.decodeBool(key, defaultValue)
    }

    /**
     * 获取 String 值
     */
    fun getString(key: String, defaultValue: String = "", instance: MMKV = getDefault()): String {
        return instance.decodeString(key, defaultValue) ?: defaultValue
    }

    /**
     * 获取 Set<String> 值
     */
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet(), instance: MMKV = getDefault()): Set<String> {
        return instance.decodeStringSet(key, defaultValue) ?: defaultValue
    }

    /**
     * 获取 ByteArray 值
     */
    fun getByteArray(key: String, defaultValue: ByteArray? = null, instance: MMKV = getDefault()): ByteArray? {
        return instance.decodeBytes(key, defaultValue)
    }

    /**
     * 检查键是否存在
     */
    fun contains(key: String, instance: MMKV = getDefault()): Boolean {
        return instance.containsKey(key)
    }

    /**
     * 删除指定键
     */
    fun remove(key: String, instance: MMKV = getDefault()) {
        instance.removeValueForKey(key)
    }

    /**
     * 删除多个键
     */
    fun remove(vararg keys: String, instance: MMKV = getDefault()) {
        instance.removeValuesForKeys(keys)
    }

    /**
     * 清空所有数据
     */
    fun clearAll(instance: MMKV = getDefault()) {
        instance.clearAll()
    }

    /**
     * 获取所有键
     */
    fun getAllKeys(instance: MMKV = getDefault()): Array<String> {
        return instance.allKeys() ?: emptyArray()
    }

    /**
     * 获取存储数据总大小（字节）
     */
    fun getTotalSize(instance: MMKV = getDefault()): Long {
        return instance.totalSize()
    }

    /**
     * 获取实际使用大小（字节）
     */
    fun getActualSize(instance: MMKV = getDefault()): Long {
        return instance.actualSize()
    }

    // ==================== 迁移 SharedPreferences ====================

    /**
     * 从 SharedPreferences 迁移数据到 MMKV
     * @param sp SharedPreferences 实例
     * @return 迁移的键值对数量
     */
    fun migrateFromSharedPreferences(sp: SharedPreferences, instance: MMKV = getDefault()): Int {
        val editor = instance.edit()
        var count = 0
        
        sp.all.forEach { (key, value) ->
            when (value) {
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is String -> editor.putString(key, value)
                is Set<*> -> @Suppress("UNCHECKED_CAST")
                    editor.putStringSet(key, value as Set<String>)
                else -> editor.putString(key, value.toString())
            }
            count++
        }
        
        return count
    }

    // ==================== 备份与恢复 ====================

    /**
     * 将所有数据导出为 JSON 字符串
     */
    fun exportToJSON(instance: MMKV = getDefault()): String {
        val json = JSONObject()
        getAllKeys(instance).forEach { key ->
            // MMKV 不直接支持获取值的类型，这里用字符串方式存储
            json.put(key, getString(key, instance = instance))
        }
        return json.toString()
    }

    /**
     * 从 JSON 字符串导入数据
     */
    fun importFromJSON(jsonString: String, instance: MMKV = getDefault()): Int {
        val json = JSONObject(jsonString)
        var count = 0
        json.keys().forEach { key ->
            instance.encode(key, json.getString(key))
            count++
        }
        return count
    }
}
