package com.peter.components.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Parcelable 示例类
 *
 * 使用 Kotlin Parcelize 插件自动生成实现
 *
 * @Parcelize 会自动生成：
 * 1. writeToParcel()：序列化
 * 2. createFromParcel()：反序列化
 * 3. newArray()：数组创建
 */
@Parcelize
data class ParcelableUser(
    val id: Int,
    val name: String,
    val email: String,
    val scores: List<Int>
) : Parcelable

/**
 * Serializable 示例类
 *
 * serialVersionUID 用于版本控制：
 * - 反序列化时会检查 serialVersionUID 是否一致
 * - 如果不一致会抛出 InvalidClassException
 * - 建议显式声明以避免自动生成带来的问题
 */
data class SerializableProduct(
    val id: String,
    val name: String,
    val price: Double,
    val description: String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
