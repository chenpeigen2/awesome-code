package com.peter.datastore

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DataStoreObserver<T>(
    private val flow: Flow<T>,
    private val scope: CoroutineScope
) {
    private var observationJob: Job? = null

    fun observe(onChange: (T) -> Unit) {
        observationJob?.cancel()
        observationJob = scope.launch(Dispatchers.Main) {
            flow
                .distinctUntilChanged()
                .catch { e -> 
                    android.util.Log.e("DataStoreObserver", "Observation error", e)
                }
                .collect { value ->
                    onChange(value)
                }
        }
    }

    fun toLiveData(): LiveData<T> {
        return flow.asLiveData(scope.coroutineContext)
    }

    fun stopObserving() {
        observationJob?.cancel()
        observationJob = null
    }
}

class ReactiveDataStoreManager(
    private val preferencesHelper: PreferencesDataStoreHelper,
    private val protoHelper: ProtoDataStoreHelper,
    private val scope: CoroutineScope
) {
    fun observeString(key: String, defaultValue: String = ""): DataStoreObserver<String> {
        return DataStoreObserver(preferencesHelper.getString(key, defaultValue), scope)
    }

    fun observeInt(key: String, defaultValue: Int = 0): DataStoreObserver<Int> {
        return DataStoreObserver(preferencesHelper.getInt(key, defaultValue), scope)
    }

    fun observeLong(key: String, defaultValue: Long = 0L): DataStoreObserver<Long> {
        return DataStoreObserver(preferencesHelper.getLong(key, defaultValue), scope)
    }

    fun observeFloat(key: String, defaultValue: Float = 0f): DataStoreObserver<Float> {
        return DataStoreObserver(preferencesHelper.getFloat(key, defaultValue), scope)
    }

    fun observeDouble(key: String, defaultValue: Double = 0.0): DataStoreObserver<Double> {
        return DataStoreObserver(preferencesHelper.getDouble(key, defaultValue), scope)
    }

    fun observeBoolean(key: String, defaultValue: Boolean = false): DataStoreObserver<Boolean> {
        return DataStoreObserver(preferencesHelper.getBoolean(key, defaultValue), scope)
    }

    fun observeStringSet(key: String, defaultValue: Set<String> = emptySet()): DataStoreObserver<Set<String>> {
        return DataStoreObserver(preferencesHelper.getStringSet(key, defaultValue), scope)
    }

    fun observeUserPreferences(): DataStoreObserver<com.peter.datastore.proto.UserPreferences> {
        return DataStoreObserver(protoHelper.userPreferences, scope)
    }

    fun observeAppSettings(): DataStoreObserver<com.peter.datastore.proto.AppSettings> {
        return DataStoreObserver(protoHelper.appSettings, scope)
    }

    fun observeComplexData(): DataStoreObserver<com.peter.datastore.proto.ComplexData> {
        return DataStoreObserver(protoHelper.complexData, scope)
    }

    fun <T> combineObservations(
        vararg observers: DataStoreObserver<*>,
        transform: (List<Any?>) -> T
    ): Flow<T> {
        return kotlinx.coroutines.flow.combine(
            observers.map { it.flow }
        ) { values ->
            transform(values.toList())
        }
    }
}
