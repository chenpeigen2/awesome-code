package com.peter.file.demo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 文件操作帮助类
 */
class FileHelper(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ==================== Internal Storage ====================

    /**
     * 写入文本到内部存储
     */
    suspend fun writeInternalFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            }
            Result.success("文件已保存到: ${file.absolutePath}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从内部存储读取文本
     */
    suspend fun readInternalFile(fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("文件不存在"))
            }
            val content = BufferedReader(InputStreamReader(FileInputStream(file))).use { reader ->
                reader.readText()
            }
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 列出内部存储所有文件
     */
    fun listInternalFiles(): List<File> {
        return context.filesDir.listFiles()?.toList() ?: emptyList()
    }

    /**
     * 删除内部存储文件
     */
    fun deleteInternalFile(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file.delete() else false
    }

    /**
     * 写入缓存文件
     */
    suspend fun writeCacheFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            }
            Result.success("缓存文件已保存到: ${file.absolutePath}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 读取缓存文件
     */
    suspend fun readCacheFile(fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.cacheDir, fileName)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("缓存文件不存在"))
            }
            val content = BufferedReader(InputStreamReader(FileInputStream(file))).use { reader ->
                reader.readText()
            }
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取文件信息
     */
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

    /**
     * 检查外部存储状态
     */
    fun getExternalStorageState(): StorageState {
        val state = Environment.getExternalStorageState()
        return when {
            Environment.MEDIA_MOUNTED == state -> StorageState.MOUNTED
            Environment.MEDIA_MOUNTED_READ_ONLY == state -> StorageState.READ_ONLY
            else -> StorageState.UNAVAILABLE
        }
    }

    /**
     * 检查外部存储是否可用
     */
    fun isExternalStorageAvailable(): Boolean {
        return getExternalStorageState() == StorageState.MOUNTED
    }

    /**
     * 写入外部文件目录
     */
    suspend fun writeExternalFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isExternalStorageAvailable()) {
                return@withContext Result.failure(Exception("外部存储不可用"))
            }
            val file = File(context.getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            }
            Result.success("文件已保存到: ${file.absolutePath}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 读取外部文件
     */
    suspend fun readExternalFile(fileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.getExternalFilesDir(null), fileName)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("文件不存在"))
            }
            val content = BufferedReader(InputStreamReader(FileInputStream(file))).use { reader ->
                reader.readText()
            }
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 列出外部文件
     */
    fun listExternalFiles(): List<File> {
        return context.getExternalFilesDir(null)?.listFiles()?.toList() ?: emptyList()
    }

    /**
     * 删除外部文件
     */
    fun deleteExternalFile(fileName: String): Boolean {
        val file = File(context.getExternalFilesDir(null), fileName)
        return if (file.exists()) file.delete() else false
    }

    /**
     * 写入外部缓存
     */
    suspend fun writeExternalCache(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.externalCacheDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            }
            Result.success("外部缓存已保存到: ${file.absolutePath}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== Preferences ====================

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("file_demo_prefs", Context.MODE_PRIVATE)
    }

    /**
     * 保存字符串
     */
    fun putString(key: String, value: String): Boolean {
        return sharedPreferences.edit().putString(key, value).commit()
    }

    /**
     * 获取字符串
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * 保存整数
     */
    fun putInt(key: String, value: Int): Boolean {
        return sharedPreferences.edit().putInt(key, value).commit()
    }

    /**
     * 获取整数
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * 保存布尔值
     */
    fun putBoolean(key: String, value: Boolean): Boolean {
        return sharedPreferences.edit().putBoolean(key, value).commit()
    }

    /**
     * 获取布尔值
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * 删除键
     */
    fun removeKey(key: String): Boolean {
        return sharedPreferences.edit().remove(key).commit()
    }

    /**
     * 清空所有偏好设置
     */
    fun clearAll(): Boolean {
        return sharedPreferences.edit().clear().commit()
    }

    /**
     * 注册偏好设置变化监听器
     */
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * 注销偏好设置变化监听器
     */
    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * 使用 apply 异步保存
     */
    fun applyString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    /**
     * 导出偏好设置到文件
     */
    suspend fun exportPreferences(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val allPrefs = sharedPreferences.all
            val sb = StringBuilder()
            allPrefs.forEach { (key, value) ->
                sb.append("$key=$value\n")
            }
            val fileName = "prefs_export_${System.currentTimeMillis()}.txt"
            writeInternalFile(fileName, sb.toString())
            Result.success("偏好设置已导出到: $fileName")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取所有偏好设置键
     */
    fun getAllPreferenceKeys(): Set<String> {
        return sharedPreferences.all.keys
    }

    // ==================== Scoped Storage ====================

    /**
     * 查询 MediaStore 图片
     */
    fun queryImages(): List<MediaImage> {
        val images = mutableListOf<MediaImage>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        context.contentResolver.query(
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

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getLong(sizeColumn)
                val date = cursor.getLong(dateColumn)
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())

                images.add(MediaImage(uri, name, size, date))
            }
        }

        return images
    }

    /**
     * 获取存储目录信息
     */
    fun getStorageDirectories(): List<StorageDirectory> {
        val directories = mutableListOf<StorageDirectory>()

        // Internal Files
        directories.add(StorageDirectory(
            name = "内部存储",
            path = context.filesDir.absolutePath,
            type = StorageType.INTERNAL,
            totalSpace = context.filesDir.totalSpace,
            freeSpace = context.filesDir.freeSpace
        ))

        // Internal Cache
        directories.add(StorageDirectory(
            name = "内部缓存",
            path = context.cacheDir.absolutePath,
            type = StorageType.INTERNAL_CACHE,
            totalSpace = context.cacheDir.totalSpace,
            freeSpace = context.cacheDir.freeSpace
        ))

        // External Files
        context.getExternalFilesDir(null)?.let { dir ->
            directories.add(StorageDirectory(
                name = "外部存储",
                path = dir.absolutePath,
                type = StorageType.EXTERNAL,
                totalSpace = dir.totalSpace,
                freeSpace = dir.freeSpace
            ))
        }

        // External Cache
        context.externalCacheDir?.let { dir ->
            directories.add(StorageDirectory(
                name = "外部缓存",
                path = dir.absolutePath,
                type = StorageType.EXTERNAL_CACHE,
                totalSpace = dir.totalSpace,
                freeSpace = dir.freeSpace
            ))
        }

        return directories
    }

    /**
     * 格式化文件大小
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
        }
    }
}

// 数据类
data class FileInfo(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: String,
    val isDirectory: Boolean,
    val isFile: Boolean
)

enum class StorageState {
    MOUNTED,
    READ_ONLY,
    UNAVAILABLE
}

enum class StorageType {
    INTERNAL,
    INTERNAL_CACHE,
    EXTERNAL,
    EXTERNAL_CACHE
}

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
