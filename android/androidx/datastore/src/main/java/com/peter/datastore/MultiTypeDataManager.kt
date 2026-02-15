package com.peter.datastore

import kotlinx.coroutines.flow.first

class MultiTypeDataManager(
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val jsonHelper: JsonDataStoreHelper
) {
    data class UserData(
        val userName: String = "",
        val userAge: Int = 0,
        val isLoggedIn: Boolean = false,
        val lastLoginTime: Long = 0L,
        val score: Double = 0.0,
        val favoriteTags: Set<String> = emptySet()
    )

    data class AppConfiguration(
        val theme: String = "light",
        val fontSize: Int = 14,
        val language: String = "zh",
        val notificationsEnabled: Boolean = true
    )

    suspend fun saveUserData(userData: UserData) {
        preferencesHelper.putString(DataStoreConfig.Keys.USER_NAME, userData.userName)
        preferencesHelper.putInt(DataStoreConfig.Keys.USER_AGE, userData.userAge)
        preferencesHelper.putBoolean(DataStoreConfig.Keys.IS_LOGGED_IN, userData.isLoggedIn)
        preferencesHelper.putLong(DataStoreConfig.Keys.LAST_LOGIN_TIME, userData.lastLoginTime)
        preferencesHelper.putDouble(DataStoreConfig.Keys.SCORE, userData.score)
        preferencesHelper.putStringSet(DataStoreConfig.Keys.FAVORITE_TAGS, userData.favoriteTags)
    }

    suspend fun readUserData(): UserData {
        return UserData(
            userName = preferencesHelper.getString(DataStoreConfig.Keys.USER_NAME).first(),
            userAge = preferencesHelper.getInt(DataStoreConfig.Keys.USER_AGE).first(),
            isLoggedIn = preferencesHelper.getBoolean(DataStoreConfig.Keys.IS_LOGGED_IN).first(),
            lastLoginTime = preferencesHelper.getLong(DataStoreConfig.Keys.LAST_LOGIN_TIME).first(),
            score = preferencesHelper.getDouble(DataStoreConfig.Keys.SCORE).first(),
            favoriteTags = preferencesHelper.getStringSet(DataStoreConfig.Keys.FAVORITE_TAGS).first()
        )
    }

    suspend fun saveAppConfiguration(config: AppConfiguration) {
        val appSettings = AppSettings(
            theme = config.theme,
            fontSize = config.fontSize,
            language = config.language,
            notificationsEnabled = config.notificationsEnabled
        )
        jsonHelper.saveAppSettings(appSettings)
    }

    suspend fun readAppConfiguration(): AppConfiguration {
        val settings = jsonHelper.getAppSettings()
        return AppConfiguration(
            theme = settings.theme.ifEmpty { "light" },
            fontSize = if (settings.fontSize == 0) 14 else settings.fontSize,
            language = settings.language.ifEmpty { "zh" },
            notificationsEnabled = settings.notificationsEnabled
        )
    }

    suspend fun saveComplexItems(items: List<ComplexItem>) {
        val complexData = ComplexData(
            items = items,
            schemaVersion = DataStoreConfig.DefaultValues.CURRENT_SCHEMA_VERSION
        )
        jsonHelper.saveComplexData(complexData)
    }

    suspend fun readComplexItems(): List<ComplexItem> {
        return jsonHelper.getComplexData().items
    }
}
