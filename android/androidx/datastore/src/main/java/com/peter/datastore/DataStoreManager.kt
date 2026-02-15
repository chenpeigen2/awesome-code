package com.peter.datastore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DataStoreManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val preferencesHelper: PreferencesDataStoreHelper by lazy {
        PreferencesDataStoreHelper(context)
    }
    
    private val jsonHelper: JsonDataStoreHelper by lazy {
        JsonDataStoreHelper(preferencesHelper)
    }
    
    private val reactiveManager: ReactiveDataStoreManager by lazy {
        ReactiveDataStoreManager(preferencesHelper, jsonHelper, scope)
    }
    
    private val transactionManager: TransactionManager by lazy {
        TransactionManager(preferencesHelper, jsonHelper)
    }
    
    private val migrationManager: DataStoreMigrationManager by lazy {
        DataStoreMigrationManager(context, preferencesHelper, jsonHelper)
    }

    companion object {
        @Volatile
        private var instance: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return instance ?: synchronized(this) {
                instance ?: DataStoreManager(context.applicationContext).also { instance = it }
            }
        }
    }

    suspend fun initialize() {
        migrationManager.migrateIfNeeded()
    }

    fun getPreferencesHelper(): PreferencesDataStoreHelper = preferencesHelper
    
    fun getJsonHelper(): JsonDataStoreHelper = jsonHelper

    suspend fun putString(key: String, value: String) = preferencesHelper.putString(key, value)
    
    suspend fun putInt(key: String, value: Int) = preferencesHelper.putInt(key, value)
    
    suspend fun putLong(key: String, value: Long) = preferencesHelper.putLong(key, value)
    
    suspend fun putFloat(key: String, value: Float) = preferencesHelper.putFloat(key, value)
    
    suspend fun putDouble(key: String, value: Double) = preferencesHelper.putDouble(key, value)
    
    suspend fun putBoolean(key: String, value: Boolean) = preferencesHelper.putBoolean(key, value)
    
    suspend fun putStringSet(key: String, value: Set<String>) = preferencesHelper.putStringSet(key, value)

    fun getStringFlow(key: String, defaultValue: String = ""): Flow<String> = 
        preferencesHelper.getString(key, defaultValue)
    
    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> = 
        preferencesHelper.getInt(key, defaultValue)
    
    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> = 
        preferencesHelper.getLong(key, defaultValue)
    
    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> = 
        preferencesHelper.getFloat(key, defaultValue)
    
    fun getDoubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double> = 
        preferencesHelper.getDouble(key, defaultValue)
    
    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> = 
        preferencesHelper.getBoolean(key, defaultValue)
    
    fun getStringSetFlow(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>> = 
        preferencesHelper.getStringSet(key, defaultValue)

    suspend fun getString(key: String, defaultValue: String = ""): String = 
        getStringFlow(key, defaultValue).first()
    
    suspend fun getInt(key: String, defaultValue: Int = 0): Int = 
        getIntFlow(key, defaultValue).first()
    
    suspend fun getLong(key: String, defaultValue: Long = 0L): Long = 
        getLongFlow(key, defaultValue).first()
    
    suspend fun getFloat(key: String, defaultValue: Float = 0f): Float = 
        getFloatFlow(key, defaultValue).first()
    
    suspend fun getDouble(key: String, defaultValue: Double = 0.0): Double = 
        getDoubleFlow(key, defaultValue).first()
    
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean = 
        getBooleanFlow(key, defaultValue).first()
    
    suspend fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> = 
        getStringSetFlow(key, defaultValue).first()

    suspend fun remove(key: String) = preferencesHelper.remove(key)
    
    suspend fun clearPreferences() = preferencesHelper.clear()

    fun getUserPreferencesFlow(): Flow<UserPreferences> = jsonHelper.getUserPreferencesFlow()
    
    fun getAppSettingsFlow(): Flow<AppSettings> = jsonHelper.getAppSettingsFlow()
    
    fun getComplexDataFlow(): Flow<ComplexData> = jsonHelper.getComplexDataFlow()

    suspend fun getUserPreferences(): UserPreferences = jsonHelper.getUserPreferences()
    
    suspend fun getAppSettings(): AppSettings = jsonHelper.getAppSettings()
    
    suspend fun getComplexData(): ComplexData = jsonHelper.getComplexData()

    suspend fun saveUserPreferences(userPreferences: UserPreferences) = 
        jsonHelper.saveUserPreferences(userPreferences)
    
    suspend fun saveAppSettings(appSettings: AppSettings) = 
        jsonHelper.saveAppSettings(appSettings)
    
    suspend fun saveComplexData(complexData: ComplexData) = 
        jsonHelper.saveComplexData(complexData)

    suspend fun <T> saveObject(key: String, value: T) where T : Any = 
        jsonHelper.saveObject(key, value)
    
    fun <T> getObjectFlow(key: String, defaultValue: T): Flow<T> where T : Any = 
        jsonHelper.getObjectFlow(key, defaultValue)
    
    suspend fun <T> getObject(key: String, defaultValue: T): T where T : Any = 
        jsonHelper.getObject(key, defaultValue)

    fun observeString(key: String, defaultValue: String = ""): DataStoreObserver<String> = 
        reactiveManager.observeString(key, defaultValue)
    
    fun observeInt(key: String, defaultValue: Int = 0): DataStoreObserver<Int> = 
        reactiveManager.observeInt(key, defaultValue)
    
    fun observeBoolean(key: String, defaultValue: Boolean = false): DataStoreObserver<Boolean> = 
        reactiveManager.observeBoolean(key, defaultValue)

    fun observeUserPreferences(): DataStoreObserver<UserPreferences> = 
        reactiveManager.observeUserPreferences()
    
    fun observeAppSettings(): DataStoreObserver<AppSettings> = 
        reactiveManager.observeAppSettings()

    fun getStringLiveData(key: String, defaultValue: String = ""): LiveData<String> = 
        getStringFlow(key, defaultValue).asLiveData()
    
    fun getIntLiveData(key: String, defaultValue: Int = 0): LiveData<Int> = 
        getIntFlow(key, defaultValue).asLiveData()
    
    fun getBooleanLiveData(key: String, defaultValue: Boolean = false): LiveData<Boolean> = 
        getBooleanFlow(key, defaultValue).asLiveData()

    fun beginTransaction(): DataStoreTransaction = transactionManager.beginTransaction()
    
    suspend fun executeInTransaction(block: DataStoreTransaction.() -> Unit): Result<Unit> = 
        transactionManager.executeInTransaction(block)

    suspend fun runMigrations() = migrationManager.migrateIfNeeded()
    
    fun getCurrentVersions(): Map<String, Int> = migrationManager.getCurrentVersions()
    
    suspend fun resetAllMigrations() = migrationManager.resetAllMigrations()

    fun getAllPreferencesFlow(): Flow<Map<String, Any?>> = preferencesHelper.getAllPreferences()

    suspend fun <T> withTransaction(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (e: Exception) {
            android.util.Log.e("DataStoreManager", "Transaction failed", e)
            Result.failure(e)
        }
    }
}
