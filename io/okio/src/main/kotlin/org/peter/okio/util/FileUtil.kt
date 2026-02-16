package org.peter.okio.util

import okio.Buffer
import okio.ByteString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object FileUtil {

    private val fileSystem = FileSystem.SYSTEM

    fun readText(path: Path, charset: Charset = StandardCharsets.UTF_8): String {
        return fileSystem.source(path).buffer().use { source ->
            source.readString(charset)
        }
    }

    fun writeText(path: Path, text: String, charset: Charset = StandardCharsets.UTF_8) {
        fileSystem.sink(path).buffer().use { sink ->
            sink.writeString(text, charset)
        }
    }

    fun readBytes(path: Path): ByteArray {
        return fileSystem.source(path).buffer().use { source ->
            source.readByteArray()
        }
    }

    fun writeBytes(path: Path, bytes: ByteArray) {
        fileSystem.sink(path).buffer().use { sink ->
            sink.write(bytes)
        }
    }

    fun copy(source: Path, target: Path) {
        fileSystem.copy(source, target)
    }

    fun move(source: Path, target: Path) {
        fileSystem.atomicMove(source, target)
    }

    fun delete(path: Path) {
        if (fileSystem.exists(path)) {
            fileSystem.delete(path)
        }
    }

    fun deleteRecursively(path: Path) {
        if (fileSystem.exists(path)) {
            fileSystem.deleteRecursively(path)
        }
    }

    fun exists(path: Path): Boolean {
        return fileSystem.exists(path)
    }

    fun createDirectory(path: Path) {
        fileSystem.createDirectories(path)
    }

    fun listFiles(path: Path): List<Path> {
        return fileSystem.list(path)
    }

    fun getFileSize(path: Path): Long {
        return fileSystem.metadata(path).size ?: 0L
    }

    fun isFile(path: Path): Boolean {
        return fileSystem.metadata(path).isRegularFile
    }

    fun isDirectory(path: Path): Boolean {
        return fileSystem.metadata(path).isDirectory
    }

    fun appendText(path: Path, text: String, charset: Charset = StandardCharsets.UTF_8) {
        fileSystem.appendingSink(path).buffer().use { sink ->
            sink.writeString(text, charset)
        }
    }

    fun getTempFile(prefix: String, suffix: String): Path {
        val fileName = "$prefix${System.currentTimeMillis()}$suffix"
        return System.getProperty("java.io.tmpdir").toPath() / fileName
    }
}
