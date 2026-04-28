sealed interface FileResult<T> {
    data class Ok<T>(val value: T) : FileResult<T>
    data class Err<T>(val message: String) : FileResult<T>
}

data class FileInfo(
    val size: Long,
    val isDirectory: Boolean,
    val mode: UInt,
    val lastModified: Long,
)

expect fun nativeWriteFile(path: String, content: String): FileResult<Unit>
expect fun nativeReadFile(path: String): FileResult<String>
expect fun nativeStat(path: String): FileResult<FileInfo>
expect fun nativeMkdir(path: String): FileResult<Unit>
expect fun nativeListDir(path: String): FileResult<List<String>>
expect fun nativeRemove(path: String): FileResult<Unit>
expect fun nativeRmdir(path: String): FileResult<Unit>
