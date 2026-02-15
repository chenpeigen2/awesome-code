package com.peter.datastore.demo

import com.peter.datastore.basic.PreferencesDataStoreHelper
import com.peter.datastore.json.JsonDataStoreHelper
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
        preferencesHelper.putString(DemoConfig.Keys.USER_NAME, userData.userName)
        preferencesHelper.putInt(DemoConfig.Keys.USER_AGE, userData.userAge)
        preferencesHelper.putBoolean(DemoConfig.Keys.IS_LOGGED_IN, userData.isLoggedIn)
        preferencesHelper.putLong(DemoConfig.Keys.LAST_LOGIN_TIME, userData.lastLoginTime)
        preferencesHelper.putDouble(DemoConfig.Keys.SCORE, userData.score)
        preferencesHelper.putStringSet(DemoConfig.Keys.FAVORITE_TAGS, userData.favoriteTags)
    }

    suspend fun readUserData(): UserData {
        return UserData(
            userName = preferencesHelper.getString(DemoConfig.Keys.USER_NAME).first(),
            userAge = preferencesHelper.getInt(DemoConfig.Keys.USER_AGE).first(),
            isLoggedIn = preferencesHelper.getBoolean(DemoConfig.Keys.IS_LOGGED_IN).first(),
            lastLoginTime = preferencesHelper.getLong(DemoConfig.Keys.LAST_LOGIN_TIME).first(),
            score = preferencesHelper.getDouble(DemoConfig.Keys.SCORE).first(),
            favoriteTags = preferencesHelper.getStringSet(DemoConfig.Keys.FAVORITE_TAGS).first()
        )
    }

    suspend fun saveAppConfiguration(config: AppConfiguration) {
        val appSettings = AppSettings(
            theme = config.theme,
            fontSize = config.fontSize,
            language = config.language,
            notificationsEnabled = config.notificationsEnabled
        )
        jsonHelper.save(DemoConfig.Keys.APP_CONFIG_JSON, appSettings)
    }

    suspend fun readAppConfiguration(): AppConfiguration {
        val settings = jsonHelper.get(DemoConfig.Keys.APP_CONFIG_JSON, AppSettings())
        return AppConfiguration(
            theme = settings.theme.ifEmpty { "light" },
            fontSize = if (settings.fontSize == 0) 14 else settings.fontSize,
            language = settings.language.ifEmpty { "zh" },
            notificationsEnabled = settings.notificationsEnabled
        )
    }

    suspend fun saveComplexItems(items: List<ComplexItem>) {
        val complexData = ComplexData(items = items)
        jsonHelper.save(DemoConfig.Keys.COMPLEX_ITEMS_JSON, complexData)
    }

    suspend fun readComplexItems(): List<ComplexItem> {
        return jsonHelper.get(DemoConfig.Keys.COMPLEX_ITEMS_JSON, ComplexData()).items
    }
}
