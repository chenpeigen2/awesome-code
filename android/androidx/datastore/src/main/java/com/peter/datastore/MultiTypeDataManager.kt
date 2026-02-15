package com.peter.datastore

import android.content.Context
import com.peter.datastore.proto.AppSettings
import com.peter.datastore.proto.ComplexData
import com.peter.datastore.proto.UserPreferences
import com.peter.datastore.proto.complexData
import com.peter.datastore.proto.item
import kotlinx.coroutines.flow.first

class MultiTypeDataManager(
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val protoHelper: ProtoDataStoreHelper
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
        protoHelper.updateAppSettings { current ->
            current.toBuilder()
                .setTheme(config.theme)
                .setFontSize(config.fontSize)
                .setLanguage(config.language)
                .setNotificationsEnabled(config.notificationsEnabled)
                .build()
        }
    }

    suspend fun readAppConfiguration(): AppConfiguration {
        val settings = protoHelper.appSettings.first()
        return AppConfiguration(
            theme = settings.theme.ifEmpty { "light" },
            fontSize = if (settings.fontSize == 0) 14 else settings.fontSize,
            language = settings.language.ifEmpty { "zh" },
            notificationsEnabled = settings.notificationsEnabled
        )
    }

    suspend fun saveComplexItems(items: List<ComplexItem>) {
        protoHelper.updateComplexData { current ->
            val builder = current.toBuilder()
                .clearItems()
                .setSchemaVersion(DataStoreConfig.DefaultValues.SCHEMA_VERSION)
            
            items.forEach { item ->
                builder.addItems(
                    com.peter.datastore.proto.ComplexData.Item.newBuilder()
                        .setId(item.id)
                        .setName(item.name)
                        .setValue(item.value)
                        .setTimestamp(item.timestamp)
                        .build()
                )
            }
            builder.build()
        }
    }

    suspend fun readComplexItems(): List<ComplexItem> {
        val data = protoHelper.complexData.first()
        return data.itemsList.map { item ->
            ComplexItem(
                id = item.id,
                name = item.name,
                value = item.value,
                timestamp = item.timestamp
            )
        }
    }

    data class ComplexItem(
        val id: Int = 0,
        val name: String = "",
        val value: Double = 0.0,
        val timestamp: Long = 0L
    )
}
