@file:OptIn(ExperimentalForeignApi::class)

import platform.posix.*
import kotlinx.cinterop.*

actual fun nativeWriteFile(path: String, content: String): FileResult<Unit> {
    val fd = open(path, O_WRONLY or O_CREAT or O_TRUNC, 0x1A4u)
    if (fd < 0) return FileResult.Err("open failed: ${strerror(errno)?.toKString()}")
    try {
        val bytes = content.encodeToByteArray()
        val written = write(fd, bytes.refTo(0), bytes.size.toUInt())
        if (written < 0) return FileResult.Err("write failed: ${strerror(errno)?.toKString()}")
    } finally {
        close(fd)
    }
    return FileResult.Ok(Unit)
}

actual fun nativeReadFile(path: String): FileResult<String> {
    val fd = open(path, O_RDONLY)
    if (fd < 0) return FileResult.Err("open failed: ${strerror(errno)?.toKString()}")
    try {
        val bufSize = 4096
        val buf = ByteArray(bufSize)
        val sb = StringBuilder()
        while (true) {
            val n = read(fd, buf.refTo(0), bufSize.toUInt())
            if (n < 0) return FileResult.Err("read failed: ${strerror(errno)?.toKString()}")
            if (n == 0) break
            sb.append(buf.decodeToString(endIndex = n))
        }
        return FileResult.Ok(sb.toString())
    } finally {
        close(fd)
    }
}

actual fun nativeStat(path: String): FileResult<FileInfo> = memScoped {
    val info = alloc<stat>()
    if (stat(path, info.ptr) != 0) return FileResult.Err("stat failed: ${strerror(errno)?.toKString()}")
    FileResult.Ok(FileInfo(
        size = info.st_size.toLong(),
        isDirectory = (info.st_mode.toInt() and S_IFMT) == S_IFDIR,
        mode = (info.st_mode.toInt() and 0xFFF).toUInt(),
        lastModified = info.st_mtime.toLong(),
    ))
}

actual fun nativeMkdir(path: String): FileResult<Unit> {
    if (mkdir(path) != 0)
        return FileResult.Err("mkdir failed: ${strerror(errno)?.toKString()}")
    return FileResult.Ok(Unit)
}

actual fun nativeListDir(path: String): FileResult<List<String>> {
    val dir = opendir(path)
        ?: return FileResult.Err("opendir failed: ${strerror(errno)?.toKString()}")
    try {
        val entries = mutableListOf<String>()
        while (true) {
            val entry = readdir(dir) ?: break
            val name = entry.pointed.d_name.toKString()
            if (name != "." && name != "..") entries.add(name)
        }
        return FileResult.Ok(entries)
    } finally {
        closedir(dir)
    }
}

actual fun nativeRemove(path: String): FileResult<Unit> {
    if (remove(path) != 0) return FileResult.Err("remove failed: ${strerror(errno)?.toKString()}")
    return FileResult.Ok(Unit)
}

actual fun nativeRmdir(path: String): FileResult<Unit> {
    if (rmdir(path) != 0) return FileResult.Err("rmdir failed: ${strerror(errno)?.toKString()}")
    return FileResult.Ok(Unit)
}
