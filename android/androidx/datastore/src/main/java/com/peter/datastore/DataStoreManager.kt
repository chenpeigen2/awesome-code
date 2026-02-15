package com.peter.datastore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.peter.datastore.basic.PreferencesDataStoreHelper
import com.peter.datastore.json.JsonDataStoreHelper
import com.peter.datastore.transaction.DataStoreTransaction
import com.peter.datastore.transaction.TransactionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


private fun <T> Flow<T>.distinctWithErrorHandling(): Flow<T> =
    this.distinctUntilChanged()
        .catch { e ->
            android.util.Log.e("DataStoreObserver", "Observation error", e)
        }


class DataStoreManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val preferencesHelper: PreferencesDataStoreHelper by lazy {
        PreferencesDataStoreHelper(context)
    }

    val jsonHelper: JsonDataStoreHelper by lazy {
        JsonDataStoreHelper(preferencesHelper)
    }

    private val transactionManager: TransactionManager by lazy {
        TransactionManager(preferencesHelper, jsonHelper)
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

    suspend fun putString(key: String, value: String) = preferencesHelper.putString(key, value)

    suspend fun putInt(key: String, value: Int) = preferencesHelper.putInt(key, value)

    suspend fun putLong(key: String, value: Long) = preferencesHelper.putLong(key, value)

    suspend fun putFloat(key: String, value: Float) = preferencesHelper.putFloat(key, value)

    suspend fun putDouble(key: String, value: Double) = preferencesHelper.putDouble(key, value)

    suspend fun putBoolean(key: String, value: Boolean) = preferencesHelper.putBoolean(key, value)

    suspend fun putStringSet(key: String, value: Set<String>) =
        preferencesHelper.putStringSet(key, value)

    fun getStringFlow(key: String, defaultValue: String = ""): Flow<String> =
        preferencesHelper.getString(key, defaultValue).distinctWithErrorHandling()

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> =
        preferencesHelper.getInt(key, defaultValue).distinctWithErrorHandling()

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> =
        preferencesHelper.getLong(key, defaultValue).distinctWithErrorHandling()

    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> =
        preferencesHelper.getFloat(key, defaultValue).distinctWithErrorHandling()

    fun getDoubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double> =
        preferencesHelper.getDouble(key, defaultValue).distinctWithErrorHandling()

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> =
        preferencesHelper.getBoolean(key, defaultValue).distinctWithErrorHandling()

    fun getStringSetFlow(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>> =
        preferencesHelper.getStringSet(key, defaultValue).distinctWithErrorHandling()

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

    suspend fun clear() = preferencesHelper.clear()

    suspend inline fun <reified T> putObject(key: String, value: T) =
        jsonHelper.save(key, value)

    inline fun <reified T> getObjectFlow(key: String, defaultValue: T): Flow<T> =
        jsonHelper.getFlow(key, defaultValue)

    suspend inline fun <reified T> getObject(key: String, defaultValue: T): T =
        jsonHelper.get(key, defaultValue)

    fun getStringLiveData(key: String, defaultValue: String = ""): LiveData<String> =
        getStringFlow(key, defaultValue).asLiveData()

    fun getIntLiveData(key: String, defaultValue: Int = 0): LiveData<Int> =
        getIntFlow(key, defaultValue).asLiveData()

    fun getBooleanLiveData(key: String, defaultValue: Boolean = false): LiveData<Boolean> =
        getBooleanFlow(key, defaultValue).asLiveData()

    fun beginTransaction(): DataStoreTransaction = transactionManager.beginTransaction()

    suspend fun executeInTransaction(block: DataStoreTransaction.() -> Unit): Result<Unit> =
        transactionManager.executeInTransaction(block)

    fun getAllPreferencesFlow(): Flow<Map<String, Any?>> = preferencesHelper.getAllPreferences()

    suspend fun <T> withTransaction(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (e: Exception) {
                android.util.Log.e("DataStoreManager", "Transaction failed", e)
                Result.failure(e)
            }
        }
}
