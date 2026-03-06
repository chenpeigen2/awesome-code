package com.peter.autodensity

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 反射工具类
 * 用于访问Android系统隐藏API
 */
object ReflectionHelper {

    /**
     * 获取字段值
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    fun <T> getFieldValue(clazz: Class<*>, obj: Any?, fieldName: String): T {
        val field = findField(clazz, fieldName)
        field.isAccessible = true
        return field[obj] as T
    }

    /**
     * 获取静态字段值
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    fun <T> getStaticFieldValue(clazz: Class<*>, fieldName: String): T {
        val field = findField(clazz, fieldName)
        field.isAccessible = true
        return field[null] as T
    }

    /**
     * 设置字段值
     */
    @Throws(Exception::class)
    fun setFieldValue(clazz: Class<*>, obj: Any?, fieldName: String, value: Any?) {
        val field = findField(clazz, fieldName)
        field.isAccessible = true
        field[obj] = value
    }

    /**
     * 调用实例方法
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    fun <T> invokeObject(
        clazz: Class<*>,
        obj: Any?,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        vararg args: Any?
    ): T {
        val method = findMethod(clazz, methodName, *parameterTypes)
        method.isAccessible = true
        return method.invoke(obj, *args) as T
    }

    /**
     * 调用静态方法
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    fun <T> invokeStatic(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        vararg args: Any?
    ): T {
        val method = findMethod(clazz, methodName, *parameterTypes)
        method.isAccessible = true
        return method.invoke(null, *args) as T
    }

    /**
     * 调用无返回值方法
     */
    @Throws(Exception::class)
    fun invoke(
        clazz: Class<*>,
        obj: Any?,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        vararg args: Any?
    ) {
        val method = findMethod(clazz, methodName, *parameterTypes)
        method.isAccessible = true
        method.invoke(obj, *args)
    }

    /**
     * 创建实例
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    fun <T> getConstructorInstance(clazz: Class<T>, parameterTypes: Array<Class<*>>, vararg args: Any?): T {
        val constructor: Constructor<T> = clazz.getDeclaredConstructor(*parameterTypes)
        constructor.isAccessible = true
        return constructor.newInstance(*args)
    }

    /**
     * 查找字段（包括父类）
     */
    private fun findField(clazz: Class<*>, fieldName: String): Field {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                currentClass = currentClass.superclass
            }
        }
        throw NoSuchFieldException("Field $fieldName not found in ${clazz.name}")
    }

    /**
     * 查找方法（包括父类）
     */
    private fun findMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredMethod(methodName, *parameterTypes)
            } catch (e: NoSuchMethodException) {
                currentClass = currentClass.superclass
            }
        }
        throw NoSuchMethodException("Method $methodName not found in ${clazz.name}")
    }
}
