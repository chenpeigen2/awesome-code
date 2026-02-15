package com.peter.datastore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataStoreTransaction(
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val jsonHelper: JsonDataStoreHelper
) {
    private val operations = mutableListOf<suspend () -> Unit>()

    fun putString(key: String, value: String): DataStoreTransaction {
        operations.add { preferencesHelper.putString(key, value) }
        return this
    }

    fun putInt(key: String, value: Int): DataStoreTransaction {
        operations.add { preferencesHelper.putInt(key, value) }
        return this
    }

    fun putLong(key: String, value: Long): DataStoreTransaction {
        operations.add { preferencesHelper.putLong(key, value) }
        return this
    }

    fun putFloat(key: String, value: Float): DataStoreTransaction {
        operations.add { preferencesHelper.putFloat(key, value) }
        return this
    }

    fun putDouble(key: String, value: Double): DataStoreTransaction {
        operations.add { preferencesHelper.putDouble(key, value) }
        return this
    }

    fun putBoolean(key: String, value: Boolean): DataStoreTransaction {
        operations.add { preferencesHelper.putBoolean(key, value) }
        return this
    }

    fun putStringSet(key: String, value: Set<String>): DataStoreTransaction {
        operations.add { preferencesHelper.putStringSet(key, value) }
        return this
    }

    fun putObject(key: String, value: Any): DataStoreTransaction {
        operations.add { jsonHelper.saveObject(key, value) }
        return this
    }

    fun putUserPreferences(userPreferences: UserPreferences): DataStoreTransaction {
        operations.add { jsonHelper.saveUserPreferences(userPreferences) }
        return this
    }

    fun putAppSettings(appSettings: AppSettings): DataStoreTransaction {
        operations.add { jsonHelper.saveAppSettings(appSettings) }
        return this
    }

    fun putComplexData(complexData: ComplexData): DataStoreTransaction {
        operations.add { jsonHelper.saveComplexData(complexData) }
        return this
    }

    fun remove(key: String): DataStoreTransaction {
        operations.add { preferencesHelper.remove(key) }
        return this
    }

    suspend fun execute(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            operations.forEach { operation ->
                operation()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("DataStoreTransaction", "Transaction failed", e)
            Result.failure(e)
        } finally {
            operations.clear()
        }
    }

    fun clear() {
        operations.clear()
    }
}

class TransactionManager(
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val jsonHelper: JsonDataStoreHelper
) {
    fun beginTransaction(): DataStoreTransaction {
        return DataStoreTransaction(preferencesHelper, jsonHelper)
    }

    suspend fun executeInTransaction(block: DataStoreTransaction.() -> Unit): Result<Unit> {
        val transaction = beginTransaction()
        try {
            block(transaction)
            return transaction.execute()
        } catch (e: Exception) {
            transaction.clear()
            return Result.failure(e)
        }
    }
}
