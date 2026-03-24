package com.peter.file.demo

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
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

    // ==================== Internal Storage Extensions ====================

    fun createDirectory(dirName: String): Boolean {
        val dir = File(context.filesDir, dirName)
        return if (dir.exists()) {
            false // Directory already exists
        } else {
            dir.mkdirs()
        }
    }

    fun listDirectories(): List<File> = context.filesDir.listFiles()?.filter { it.isDirectory }?.toList().orEmpty()

    fun deleteDirectory(dirName: String): Boolean {
        val dir = File(context.filesDir, dirName)
        return if (dir.exists() && dir.isDirectory) {
            dir.deleteRecursively()
        } else {
            false
        }
    }

    suspend fun copyFile(sourceName: String, destName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = File(context.filesDir, sourceName)
            require(sourceFile.exists()) { "源文件不存在" }
            val destFile = File(context.filesDir, destName)
            sourceFile.copyTo(destFile, overwrite = true)
            "文件已复制: $sourceName -> $destName"
        }
    }

    suspend fun moveFile(sourceName: String, destName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = File(context.filesDir, sourceName)
            require(sourceFile.exists()) { "源文件不存在" }
            val destFile = File(context.filesDir, destName)
            sourceFile.copyTo(destFile, overwrite = true)
            sourceFile.delete()
            "文件已移动: $sourceName -> $destName"
        }
    }

    suspend fun appendToFile(fileName: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.filesDir, fileName)
            file.appendText(content)
            "内容已追加到: ${file.absolutePath}"
        }
    }

    suspend fun writeBytes(fileName: String, bytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.filesDir, fileName)
            file.writeBytes(bytes)
            "已写入 ${bytes.size} 字节到: ${file.absolutePath}"
        }
    }

    suspend fun readBytes(fileName: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.filesDir, fileName)
            require(file.exists()) { "文件不存在" }
            file.readBytes()
        }
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

    // ==================== External Storage Extensions ====================

    fun getPublicDirectories(): Map<String, File?> = mapOf(
        "Downloads" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "DCIM" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "Pictures" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "Music" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "Documents" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    )

    fun getStorageVolumes(): List<StorageVolumeInfo> {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        return storageManager.storageVolumes.map { volume ->
            StorageVolumeInfo(
                uuid = volume.uuid,
                description = volume.getDescription(context),
                isRemovable = volume.isRemovable,
                isPrimary = volume.isPrimary,
                isEmulated = volume.isEmulated,
                state = volume.state,
                totalSpace = if (volume.isPrimary) {
                    context.filesDir.totalSpace
                } else 0L,
                freeSpace = if (volume.isPrimary) {
                    context.filesDir.freeSpace
                } else 0L
            )
        }
    }

    // ==================== Preferences ====================

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("file_demo_prefs", Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit(commit = true) { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String = ""): String = sharedPreferences.getString(key, defaultValue) ?: defaultValue

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit(commit = true) { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int = sharedPreferences.getInt(key, defaultValue)

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit(commit = true) { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean = sharedPreferences.getBoolean(key, defaultValue)

    fun removeKey(key: String) {
        sharedPreferences.edit(commit = true) { remove(key) }
    }

    fun clearAll() {
        sharedPreferences.edit(commit = true) { clear() }
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)

    fun applyString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    suspend fun exportPreferences(): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val content = sharedPreferences.all.entries.joinToString("\n") { (k, v) -> "$k=$v" }
            val fileName = "prefs_export_${System.currentTimeMillis()}.txt"
            File(context.filesDir, fileName).writeText(content)
            "偏好设置已导出到: $fileName"
        }
    }

    fun getAllPreferenceKeys(): Set<String> = sharedPreferences.all.keys

    // ==================== Preferences Extensions ====================

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "encrypted_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun putEncryptedString(key: String, value: String) {
        encryptedPrefs.edit { putString(key, value) }
    }

    fun getEncryptedString(key: String, defaultValue: String = ""): String =
        encryptedPrefs.getString(key, defaultValue) ?: defaultValue

    private val _preferenceFlow = MutableSharedFlow<Pair<String, Any?>>()
    val preferenceFlow: SharedFlow<Pair<String, Any?>> = _preferenceFlow.asSharedFlow()

    private val flowListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        val value = sharedPreferences.all[key]
        GlobalScope.launch {
            _preferenceFlow.emit(Pair(key ?: "", value))
        }
    }

    fun startPreferenceFlow() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(flowListener)
    }

    fun stopPreferenceFlow() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(flowListener)
    }

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

    fun queryVideos(): List<MediaVideo> {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_MODIFIED
        )

        return context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)

            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())
                    add(MediaVideo(
                        uri = uri,
                        name = cursor.getString(nameColumn),
                        size = cursor.getLong(sizeColumn),
                        duration = cursor.getLong(durationColumn),
                        dateModified = cursor.getLong(dateColumn)
                    ))
                }
            }
        }.orEmpty()
    }

    fun queryAudio(): List<MediaAudio> {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATE_MODIFIED
        )

        return context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
                    add(MediaAudio(
                        uri = uri,
                        name = cursor.getString(nameColumn),
                        size = cursor.getLong(sizeColumn),
                        duration = cursor.getLong(durationColumn),
                        artist = cursor.getString(artistColumn),
                        dateModified = cursor.getLong(dateColumn)
                    ))
                }
            }
        }.orEmpty()
    }

    suspend fun deleteMedia(uri: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val deleted = context.contentResolver.delete(uri, null, null)
            deleted > 0
        }
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

    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
        } else {
            String.format("%02d:%02d", minutes, seconds % 60)
        }
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

data class StorageVolumeInfo(
    val uuid: String?,
    val description: String,
    val isRemovable: Boolean,
    val isPrimary: Boolean,
    val isEmulated: Boolean,
    val state: String,
    val totalSpace: Long,
    val freeSpace: Long
)

data class MediaImage(
    val uri: Uri,
    val name: String,
    val size: Long,
    val dateModified: Long
)

data class MediaVideo(
    val uri: Uri,
    val name: String,
    val size: Long,
    val duration: Long,
    val dateModified: Long
)

data class MediaAudio(
    val uri: Uri,
    val name: String,
    val size: Long,
    val duration: Long,
    val artist: String,
    val dateModified: Long
)
