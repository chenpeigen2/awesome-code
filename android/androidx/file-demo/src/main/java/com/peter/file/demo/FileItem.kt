package com.peter.file.demo

/**
 * 文件操作类型枚举
 */
enum class FileOperationType {
    // Internal Storage
    INTERNAL_WRITE,
    INTERNAL_READ,
    INTERNAL_LIST,
    INTERNAL_DELETE,
    INTERNAL_CACHE_WRITE,
    INTERNAL_CACHE_READ,
    INTERNAL_INFO,
    INTERNAL_CREATE_DIR,
    INTERNAL_LIST_DIRS,
    INTERNAL_DELETE_DIR,
    INTERNAL_COPY_FILE,
    INTERNAL_MOVE_FILE,
    INTERNAL_APPEND,
    INTERNAL_WRITE_BYTES,
    INTERNAL_READ_BYTES,

    // External Storage
    EXTERNAL_CHECK,
    EXTERNAL_WRITE,
    EXTERNAL_READ,
    EXTERNAL_LIST,
    EXTERNAL_DELETE,
    EXTERNAL_CACHE,
    EXTERNAL_PUBLIC_DIRS,
    EXTERNAL_VOLUMES,

    // Preferences
    PREF_PUT_STRING,
    PREF_GET_STRING,
    PREF_PUT_INT,
    PREF_GET_INT,
    PREF_PUT_BOOLEAN,
    PREF_GET_BOOLEAN,
    PREF_REMOVE,
    PREF_CLEAR,
    PREF_LISTENER,
    PREF_COMMIT_VS_APPLY,
    PREF_EXPORT,
    PREF_IMPORT,
    PREF_ENCRYPTED,
    PREF_LIVE_DATA,

    // Scoped Storage
    SCOPED_PICK_FILE,
    SCOPED_CREATE_DOC,
    SCOPED_QUERY_IMAGES,
    SCOPED_SAVE_IMAGE,
    SCOPED_PHOTO_PICKER,
    SCOPED_PERMISSION,
    SCOPED_PICK_VIDEO,
    SCOPED_PICK_AUDIO,
    SCOPED_PICK_FOLDER,
    SCOPED_DOWNLOAD_MANAGER,
    SCOPED_DELETE_MEDIA
}

/**
 * 文件操作分类
 */
enum class FileOperationCategory(val displayName: String) {
    BASIC("基础操作"),
    READ_WRITE("读写操作"),
    ADVANCED("高级操作")
}

/**
 * 文件操作数据模型
 */
data class FileItem(
    val type: FileOperationType,
    val title: String,
    val description: String,
    val category: FileOperationCategory = FileOperationCategory.BASIC,
    val dotDrawableRes: Int = R.drawable.bg_color_dot
)
