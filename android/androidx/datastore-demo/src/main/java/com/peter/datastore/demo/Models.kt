package com.peter.datastore.demo

import kotlinx.serialization.Serializable

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

object DemoConfig {
    object Keys {
        const val USER_NAME = "user_name"
        const val USER_AGE = "user_age"
        const val IS_LOGGED_IN = "is_logged_in"
        const val LAST_LOGIN_TIME = "last_login_time"
        const val THEME = "theme"
        const val FONT_SIZE = "font_size"
        const val LANGUAGE = "language"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val SCORE = "score"
        const val FAVORITE_TAGS = "favorite_tags"
        const val USER_DATA_JSON = "user_data_json"
        const val APP_CONFIG_JSON = "app_config_json"
        const val COMPLEX_ITEMS_JSON = "complex_items_json"
        const val SCHEMA_VERSION = "schema_version"
    }
}
