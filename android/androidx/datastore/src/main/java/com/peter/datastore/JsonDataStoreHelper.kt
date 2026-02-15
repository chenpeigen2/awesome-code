package com.peter.datastore

import kotlinx.coroutines.flow.Flow
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

    suspend fun clearObject(key: String) {
        preferencesHelper.remove(key)
    }
}
