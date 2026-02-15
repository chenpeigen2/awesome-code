package com.peter.datastore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataStoreTransaction(
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val protoHelper: ProtoDataStoreHelper
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

    fun remove(key: String): DataStoreTransaction {
        operations.add { preferencesHelper.remove(key) }
        return this
    }

    fun updateUserPreferences(transform: (com.peter.datastore.proto.UserPreferences) -> com.peter.datastore.proto.UserPreferences): DataStoreTransaction {
        operations.add { protoHelper.updateUserPreferences(transform) }
        return this
    }

    fun updateAppSettings(transform: (com.peter.datastore.proto.AppSettings) -> com.peter.datastore.proto.AppSettings): DataStoreTransaction {
        operations.add { protoHelper.updateAppSettings(transform) }
        return this
    }

    fun updateComplexData(transform: (com.peter.datastore.proto.ComplexData) -> com.peter.datastore.proto.ComplexData): DataStoreTransaction {
        operations.add { protoHelper.updateComplexData(transform) }
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
    private val protoHelper: ProtoDataStoreHelper
) {
    fun beginTransaction(): DataStoreTransaction {
        return DataStoreTransaction(preferencesHelper, protoHelper)
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
