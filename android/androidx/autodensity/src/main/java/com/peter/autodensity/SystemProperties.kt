package com.peter.autodensity

import android.util.Log

/**
 * 系统属性读取工具
 * 通过反射读取 android.os.SystemProperties
 */
object SystemProperties {

    private const val TAG = "SystemProperties"
    private const val SYSTEM_PROPERTIES_CLASS = "android.os.SystemProperties"

    private var sClass: Class<*>? = null
    private var sGetMethod: java.lang.reflect.Method? = null
    private var sGetIntMethod: java.lang.reflect.Method? = null

    init {
        try {
            sClass = Class.forName(SYSTEM_PROPERTIES_CLASS)
            sGetMethod = sClass?.getDeclaredMethod("get", String::class.java)
            sGetIntMethod = sClass?.getDeclaredMethod("getInt", String::class.java, Int::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get SystemProperties class: ${e.message}")
        }
    }

    /**
     * 获取字符串类型的系统属性
     */
    fun get(key: String, defaultValue: String? = null): String? {
        return try {
            if (defaultValue != null) {
                sGetMethod?.invoke(null, key, defaultValue) as? String ?: defaultValue
            } else {
                sGetMethod?.invoke(null, key) as? String
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get property $key: ${e.message}")
            defaultValue
        }
    }

    /**
     * 获取整数类型的系统属性
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return try {
            sGetIntMethod?.invoke(null, key, defaultValue) as? Int ?: defaultValue
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get int property $key: ${e.message}")
            defaultValue
        }
    }
}
