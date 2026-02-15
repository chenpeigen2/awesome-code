package com.peter.datastore

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class JsonDataStoreHelper(private val preferencesHelper: PreferencesDataStoreHelper) {

    suspend fun <T> saveObject(key: String, value: T) where T : Any {
        val jsonString = JsonSerializer.encodeToString(value)
        preferencesHelper.putString(key, jsonString)
    }

    fun <T> getObjectFlow(key: String, defaultValue: T): Flow<T> where T : Any {
        return preferencesHelper.getString(key)
            .map { jsonString ->
                if (jsonString.isEmpty()) {
                    defaultValue
                } else {
                    try {
                        JsonSerializer.decodeFromString(jsonString)
                    } catch (e: Exception) {
                        android.util.Log.e("JsonDataStoreHelper", "Failed to parse JSON", e)
                        defaultValue
                    }
                }
            }
    }

    suspend fun <T> getObject(key: String, defaultValue: T): T where T : Any {
        return getObjectFlow(key, defaultValue).first()
    }

    suspend fun saveUserPreferences(userPreferences: UserPreferences) {
        saveObject(DataStoreConfig.Keys.USER_DATA_JSON, userPreferences)
    }

    fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return getObjectFlow(DataStoreConfig.Keys.USER_DATA_JSON, UserPreferences())
    }

    suspend fun getUserPreferences(): UserPreferences {
        return getObject(DataStoreConfig.Keys.USER_DATA_JSON, UserPreferences())
    }

    suspend fun saveAppSettings(appSettings: AppSettings) {
        saveObject(DataStoreConfig.Keys.APP_CONFIG_JSON, appSettings)
    }

    fun getAppSettingsFlow(): Flow<AppSettings> {
        return getObjectFlow(DataStoreConfig.Keys.APP_CONFIG_JSON, AppSettings())
    }

    suspend fun getAppSettings(): AppSettings {
        return getObject(DataStoreConfig.Keys.APP_CONFIG_JSON, AppSettings())
    }

    suspend fun saveComplexData(complexData: ComplexData) {
        saveObject(DataStoreConfig.Keys.COMPLEX_ITEMS_JSON, complexData)
    }

    fun getComplexDataFlow(): Flow<ComplexData> {
        return getObjectFlow(DataStoreConfig.Keys.COMPLEX_ITEMS_JSON, ComplexData())
    }

    suspend fun getComplexData(): ComplexData {
        return getObject(DataStoreConfig.Keys.COMPLEX_ITEMS_JSON, ComplexData())
    }

    suspend fun clearObject(key: String) {
        preferencesHelper.remove(key)
    }
}
