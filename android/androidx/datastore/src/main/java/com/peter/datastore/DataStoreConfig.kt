package com.peter.datastore

object DataStoreConfig {
    const val PREFERENCES_NAME = "app_preferences"
    const val USER_PREFERENCES_NAME = "user_preferences"
    const val APP_SETTINGS_NAME = "app_settings"
    const val COMPLEX_DATA_NAME = "complex_data"
    
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
    }
    
    object DefaultValues {
        const val DEFAULT_STRING = ""
        const val DEFAULT_INT = 0
        const val DEFAULT_LONG = 0L
        const val DEFAULT_FLOAT = 0f
        const val DEFAULT_DOUBLE = 0.0
        const val DEFAULT_BOOLEAN = false
        const val SCHEMA_VERSION = 1
    }
}
