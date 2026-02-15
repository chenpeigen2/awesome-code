package com.peter.datastore

import android.content.Context
import kotlinx.coroutines.flow.first

class DataStoreMigrationManager(
    private val context: Context,
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val jsonHelper: JsonDataStoreHelper
) {
    private val prefs = context.getSharedPreferences("migration_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SCHEMA_VERSION = "schema_version"
        private const val CURRENT_SCHEMA_VERSION = 1
    }

    suspend fun migrateIfNeeded() {
        val currentVersion = prefs.getInt(KEY_SCHEMA_VERSION, 0)
        if (currentVersion < CURRENT_SCHEMA_VERSION) {
            for (version in currentVersion + 1..CURRENT_SCHEMA_VERSION) {
                when (version) {
                    1 -> migrateToV1()
                }
            }
            prefs.edit().putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION).apply()
        }
    }

    private suspend fun migrateToV1() {
        android.util.Log.d("Migration", "Migrating to v1: Initial setup")
        
        val existingUserName = preferencesHelper.getString(DataStoreConfig.Keys.USER_NAME).first()
        if (existingUserName.isEmpty()) {
            val userPrefs = UserPreferences(
                userName = "Default User",
                userAge = 18,
                isLoggedIn = false
            )
            jsonHelper.saveUserPreferences(userPrefs)
        }
        
        val appSettings = AppSettings(
            version = 1,
            theme = "light",
            fontSize = 14,
            language = "zh",
            notificationsEnabled = true
        )
        jsonHelper.saveAppSettings(appSettings)
        
        preferencesHelper.putInt(DataStoreConfig.Keys.SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
    }

    fun getCurrentVersions(): Map<String, Int> {
        return mapOf(
            "Schema" to prefs.getInt(KEY_SCHEMA_VERSION, 0),
            "DataStore" to preferencesHelper.getInt(DataStoreConfig.Keys.SCHEMA_VERSION).first()
        )
    }

    suspend fun resetAllMigrations() {
        prefs.edit().clear().apply()
        preferencesHelper.remove(DataStoreConfig.Keys.SCHEMA_VERSION)
    }
}
