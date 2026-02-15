package com.peter.datastore.json

import com.peter.datastore.basic.PreferencesDataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class JsonDataStoreHelper(val preferencesHelper: PreferencesDataStoreHelper) {

    suspend inline fun <reified T> save(key: String, value: T) {
        preferencesHelper.putString(key, JsonSerializer.encode(value))
    }

    inline fun <reified T> getFlow(key: String, defaultValue: T): Flow<T> {
        return preferencesHelper.getString(key).map { jsonString ->
            if (jsonString.isEmpty()) defaultValue
            else try { JsonSerializer.decode(jsonString) } 
                 catch (e: Exception) { defaultValue }
        }
    }

    suspend inline fun <reified T> get(key: String, defaultValue: T): T {
        return getFlow(key, defaultValue).first()
    }

    suspend fun remove(key: String) {
        preferencesHelper.remove(key)
    }
}
