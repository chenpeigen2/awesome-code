package com.peter.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.peter.datastore.proto.AppSettings
import com.peter.datastore.proto.ComplexData
import com.peter.datastore.proto.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            UserPreferences.parseFrom(input)
        } catch (exception: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            AppSettings.parseFrom(input)
        } catch (exception: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        t.writeTo(output)
    }
}

object ComplexDataSerializer : Serializer<ComplexData> {
    override val defaultValue: ComplexData = ComplexData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ComplexData {
        return try {
            ComplexData.parseFrom(input)
        } catch (exception: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: ComplexData, output: OutputStream) {
        t.writeTo(output)
    }
}

class ProtoDataStoreHelper(private val context: Context) {

    private val Context.userPreferencesDataStore: DataStore<UserPreferences> by androidx.datastore.dataStore(
        fileName = DataStoreConfig.USER_PREFERENCES_NAME + ".pb",
        serializer = UserPreferencesSerializer
    )

    private val Context.appSettingsDataStore: DataStore<AppSettings> by androidx.datastore.dataStore(
        fileName = DataStoreConfig.APP_SETTINGS_NAME + ".pb",
        serializer = AppSettingsSerializer
    )

    private val Context.complexDataDataStore: DataStore<ComplexData> by androidx.datastore.dataStore(
        fileName = DataStoreConfig.COMPLEX_DATA_NAME + ".pb",
        serializer = ComplexDataSerializer
    )

    private val userPreferencesStore = context.userPreferencesDataStore
    private val appSettingsStore = context.appSettingsDataStore
    private val complexDataStore = context.complexDataDataStore

    val userPreferences: Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    val appSettings: Flow<AppSettings> = appSettingsStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(AppSettings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    val complexData: Flow<ComplexData> = complexDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(ComplexData.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateUserPreferences(transform: (UserPreferences) -> UserPreferences) {
        userPreferencesStore.updateData { current ->
            transform(current)
        }
    }

    suspend fun updateAppSettings(transform: (AppSettings) -> AppSettings) {
        appSettingsStore.updateData { current ->
            transform(current)
        }
    }

    suspend fun updateComplexData(transform: (ComplexData) -> ComplexData) {
        complexDataStore.updateData { current ->
            transform(current)
        }
    }

    suspend fun clearUserPreferences() {
        userPreferencesStore.updateData { UserPreferences.getDefaultInstance() }
    }

    suspend fun clearAppSettings() {
        appSettingsStore.updateData { AppSettings.getDefaultInstance() }
    }

    suspend fun clearComplexData() {
        complexDataStore.updateData { ComplexData.getDefaultInstance() }
    }
}
