package com.peter.datastore

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserPreferences(
    val userName: String = "",
    val userAge: Int = 0,
    val isLoggedIn: Boolean = false,
    val lastLoginTime: Long = 0L,
    val favoriteTags: List<String> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)

@Serializable
data class AppSettings(
    val version: Int = 1,
    val theme: String = "light",
    val notificationsEnabled: Boolean = true,
    val fontSize: Int = 14,
    val language: String = "zh"
)

@Serializable
data class ComplexData(
    val items: List<ComplexItem> = emptyList(),
    val schemaVersion: Int = 1
)

@Serializable
data class ComplexItem(
    val id: Int = 0,
    val name: String = "",
    val value: Double = 0.0,
    val timestamp: Long = 0L
)

object JsonSerializer {
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }
    
    fun <T> encodeToString(value: T, serializer: kotlinx.serialization.KSerializer<T>): String {
        return json.encodeToString(serializer, value)
    }
    
    fun <T> decodeFromString(string: String, serializer: kotlinx.serialization.KSerializer<T>): T {
        return if (string.isEmpty()) {
            throw IllegalArgumentException("Empty JSON string")
        } else {
            json.decodeFromString(serializer, string)
        }
    }
    
    inline fun <reified T> encodeToString(value: T): String {
        return json.encodeToString(value)
    }
    
    inline fun <reified T> decodeFromString(string: String): T {
        return if (string.isEmpty()) {
            throw IllegalArgumentException("Empty JSON string")
        } else {
            json.decodeFromString(string)
        }
    }
}
