package com.peter.datastore

import android.content.Context
import com.peter.datastore.proto.AppSettings
import com.peter.datastore.proto.ComplexData
import com.peter.datastore.proto.UserPreferences
import com.peter.datastore.proto.appSettings
import com.peter.datastore.proto.complexData
import com.peter.datastore.proto.userPreferences
import kotlinx.coroutines.flow.first

class DataStoreMigrationManager(
    private val context: Context,
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val protoHelper: ProtoDataStoreHelper
) {
    private val prefs = context.getSharedPreferences("migration_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PREFS_VERSION = "preferences_version"
        private const val KEY_USER_PREFS_VERSION = "user_preferences_version"
        private const val KEY_APP_SETTINGS_VERSION = "app_settings_version"
        private const val KEY_COMPLEX_DATA_VERSION = "complex_data_version"
        
        private const val CURRENT_PREFS_VERSION = 2
        private const val CURRENT_USER_PREFS_VERSION = 1
        private const val CURRENT_APP_SETTINGS_VERSION = 1
        private const val CURRENT_COMPLEX_DATA_VERSION = 1
    }

    suspend fun migrateIfNeeded() {
        migratePreferences()
        migrateUserPreferences()
        migrateAppSettings()
        migrateComplexData()
    }

    private suspend fun migratePreferences() {
        val currentVersion = prefs.getInt(KEY_PREFS_VERSION, 0)
        if (currentVersion < CURRENT_PREFS_VERSION) {
            for (version in currentVersion + 1..CURRENT_PREFS_VERSION) {
                when (version) {
                    1 -> migratePreferencesV1()
                    2 -> migratePreferencesV2()
                }
            }
            prefs.edit().putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION).apply()
        }
    }

    private suspend fun migratePreferencesV1() {
        android.util.Log.d("Migration", "Migrating preferences to v1: Initial setup")
    }

    private suspend fun migratePreferencesV2() {
        android.util.Log.d("Migration", "Migrating preferences to v2: Adding new fields")
        val hasOldKey = preferencesHelper.getString("old_user_name").first().isNotEmpty()
        if (hasOldKey) {
            val oldName = preferencesHelper.getString("old_user_name").first()
            preferencesHelper.putString(DataStoreConfig.Keys.USER_NAME, oldName)
            preferencesHelper.remove("old_user_name")
        }
    }

    private suspend fun migrateUserPreferences() {
        val currentVersion = prefs.getInt(KEY_USER_PREFS_VERSION, 0)
        if (currentVersion < CURRENT_USER_PREFS_VERSION) {
            for (version in currentVersion + 1..CURRENT_USER_PREFS_VERSION) {
                when (version) {
                    1 -> migrateUserPreferencesV1()
                }
            }
            prefs.edit().putInt(KEY_USER_PREFS_VERSION, CURRENT_USER_PREFS_VERSION).apply()
        }
    }

    private suspend fun migrateUserPreferencesV1() {
        android.util.Log.d("Migration", "Migrating user preferences to v1: Initial setup")
        protoHelper.updateUserPreferences { current ->
            if (current == UserPreferences.getDefaultInstance()) {
                userPreferences {
                    userName = "Default User"
                    userAge = 18
                    isLoggedIn = false
                }
            } else {
                current
            }
        }
    }

    private suspend fun migrateAppSettings() {
        val currentVersion = prefs.getInt(KEY_APP_SETTINGS_VERSION, 0)
        if (currentVersion < CURRENT_APP_SETTINGS_VERSION) {
            for (version in currentVersion + 1..CURRENT_APP_SETTINGS_VERSION) {
                when (version) {
                    1 -> migrateAppSettingsV1()
                }
            }
            prefs.edit().putInt(KEY_APP_SETTINGS_VERSION, CURRENT_APP_SETTINGS_VERSION).apply()
        }
    }

    private suspend fun migrateAppSettingsV1() {
        android.util.Log.d("Migration", "Migrating app settings to v1: Initial setup")
        protoHelper.updateAppSettings { current ->
            if (current == AppSettings.getDefaultInstance()) {
                appSettings {
                    version = 1
                    theme = "light"
                    fontSize = 14
                    language = "zh"
                    notificationsEnabled = true
                }
            } else {
                current
            }
        }
    }

    private suspend fun migrateComplexData() {
        val currentVersion = prefs.getInt(KEY_COMPLEX_DATA_VERSION, 0)
        if (currentVersion < CURRENT_COMPLEX_DATA_VERSION) {
            for (version in currentVersion + 1..CURRENT_COMPLEX_DATA_VERSION) {
                when (version) {
                    1 -> migrateComplexDataV1()
                }
            }
            prefs.edit().putInt(KEY_COMPLEX_DATA_VERSION, CURRENT_COMPLEX_DATA_VERSION).apply()
        }
    }

    private suspend fun migrateComplexDataV1() {
        android.util.Log.d("Migration", "Migrating complex data to v1: Initial setup")
        protoHelper.updateComplexData { current ->
            if (current == ComplexData.getDefaultInstance()) {
                complexData {
                    schemaVersion = 1
                }
            } else {
                current
            }
        }
    }

    fun getCurrentVersions(): Map<String, Int> {
        return mapOf(
            "Preferences" to prefs.getInt(KEY_PREFS_VERSION, 0),
            "UserPreferences" to prefs.getInt(KEY_USER_PREFS_VERSION, 0),
            "AppSettings" to prefs.getInt(KEY_APP_SETTINGS_VERSION, 0),
            "ComplexData" to prefs.getInt(KEY_COMPLEX_DATA_VERSION, 0)
        )
    }

    suspend fun resetAllMigrations() {
        prefs.edit().clear().apply()
    }
}
