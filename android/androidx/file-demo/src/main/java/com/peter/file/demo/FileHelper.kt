package com.peter.file.demo

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileHelper(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ==================== Internal Storage ====================

    suspend fun writeInternalFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.filesDir, fileName)
            file.writeText(content)
            "文件已保存到: ${file.absolutePath}"
        }
    }

    suspend fun readInternalFile(fileName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.filesDir, fileName)
            require(file.exists()) { "文件不存在" }
            file.readText()
        }
    }

    fun listInternalFiles(): List<File> = context.filesDir.listFiles()?.toList().orEmpty()

    fun deleteInternalFile(fileName: String): Boolean = File(context.filesDir, fileName).delete()

    suspend fun writeCacheFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.cacheDir, fileName)
            file.writeText(content)
            "缓存文件已保存到: ${file.absolutePath}"
        }
    }

    suspend fun readCacheFile(fileName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.cacheDir, fileName)
            require(file.exists()) { "缓存文件不存在" }
            file.readText()
        }
    }

    fun getFileInfo(fileName: String): FileInfo? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return null

        return FileInfo(
            name = file.name,
            path = file.absolutePath,
            size = file.length(),
            lastModified = dateFormat.format(Date(file.lastModified())),
            isDirectory = file.isDirectory,
            isFile = file.isFile
        )
    }

    // ==================== External Storage ====================

    fun getExternalStorageState(): StorageState = when (Environment.getExternalStorageState()) {
        Environment.MEDIA_MOUNTED -> StorageState.MOUNTED
        Environment.MEDIA_MOUNTED_READ_ONLY -> StorageState.READ_ONLY
        else -> StorageState.UNAVAILABLE
    }

    fun isExternalStorageAvailable(): Boolean = getExternalStorageState() == StorageState.MOUNTED

    suspend fun writeExternalFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(isExternalStorageAvailable()) { "外部存储不可用" }
            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(content)
            "文件已保存到: ${file.absolutePath}"
        }
    }

    suspend fun readExternalFile(fileName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.getExternalFilesDir(null), fileName)
            require(file.exists()) { "文件不存在" }
            file.readText()
        }
    }

    fun listExternalFiles(): List<File> = context.getExternalFilesDir(null)?.listFiles()?.toList().orEmpty()

    fun deleteExternalFile(fileName: String): Boolean = File(context.getExternalFilesDir(null), fileName).delete()

    suspend fun writeExternalCache(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.externalCacheDir, fileName)
            file.writeText(content)
            "外部缓存已保存到: ${file.absolutePath}"
        }
    }

    // ==================== Preferences ====================

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("file_demo_prefs", Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String): Boolean =
        sharedPreferences.edit().putString(key, value).commit()

    fun getString(key: String, defaultValue: String = ""): String =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue

    fun putInt(key: String, value: Int): Boolean =
        sharedPreferences.edit().putInt(key, value).commit()

    fun getInt(key: String, defaultValue: Int = 0): Int =
        sharedPreferences.getInt(key, defaultValue)

    fun putBoolean(key: String, value: Boolean): Boolean =
        sharedPreferences.edit().putBoolean(key, value).commit()

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    fun removeKey(key: String): Boolean = sharedPreferences.edit().remove(key).commit()

    fun clearAll(): Boolean = sharedPreferences.edit().clear().commit()

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)

    fun applyString(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    suspend fun exportPreferences(): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val content = sharedPreferences.all.entries.joinToString("\n") { (k, v) -> "$k=$v" }
            val fileName = "prefs_export_${System.currentTimeMillis()}.txt"
            File(context.filesDir, fileName).writeText(content)
            "偏好设置已导出到: $fileName"
        }
    }

    fun getAllPreferenceKeys(): Set<String> = sharedPreferences.all.keys

    // ==================== Scoped Storage ====================

    fun queryImages(): List<MediaImage> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        return context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                    add(MediaImage(uri, cursor.getString(nameColumn), cursor.getLong(sizeColumn), cursor.getLong(dateColumn)))
                }
            }
        }.orEmpty()
    }

    fun getStorageDirectories(): List<StorageDirectory> = buildList {
        add(StorageDirectory("内部存储", context.filesDir.absolutePath, StorageType.INTERNAL, context.filesDir.totalSpace, context.filesDir.freeSpace))
        add(StorageDirectory("内部缓存", context.cacheDir.absolutePath, StorageType.INTERNAL_CACHE, context.cacheDir.totalSpace, context.cacheDir.freeSpace))
        context.getExternalFilesDir(null)?.let { add(StorageDirectory("外部存储", it.absolutePath, StorageType.EXTERNAL, it.totalSpace, it.freeSpace)) }
        context.externalCacheDir?.let { add(StorageDirectory("外部缓存", it.absolutePath, StorageType.EXTERNAL_CACHE, it.totalSpace, it.freeSpace)) }
    }

    fun formatFileSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "%.2f KB".format(bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024))
        else -> "%.2f GB".format(bytes / (1024.0 * 1024 * 1024))
    }
}

data class FileInfo(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: String,
    val isDirectory: Boolean,
    val isFile: Boolean
)

enum class StorageState { MOUNTED, READ_ONLY, UNAVAILABLE }

enum class StorageType { INTERNAL, INTERNAL_CACHE, EXTERNAL, EXTERNAL_CACHE }

data class StorageDirectory(
    val name: String,
    val path: String,
    val type: StorageType,
    val totalSpace: Long,
    val freeSpace: Long
)

data class MediaImage(
    val uri: Uri,
    val name: String,
    val size: Long,
    val dateModified: Long
)
