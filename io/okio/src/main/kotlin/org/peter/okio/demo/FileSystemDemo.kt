package org.peter.okio.demo

import okio.ByteString
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

object FileSystemDemo {

    private val fileSystem = FileSystem.SYSTEM

    fun demonstrateBasicFileOperations() {
        println("=== 基本文件操作 ===")
        
        val tempDir = System.getProperty("java.io.tmpdir").toPath() / "okio-demo"
        fileSystem.createDirectories(tempDir)
        
        val filePath = tempDir / "test.txt"
        
        fileSystem.sink(filePath).buffer().use { sink ->
            sink.writeUtf8("Hello, Okio FileSystem!")
        }
        println("文件已创建: $filePath")
        
        val content = fileSystem.source(filePath).buffer().use { source ->
            source.readUtf8()
        }
        println("文件内容: $content")
        
        fileSystem.delete(filePath)
        fileSystem.delete(tempDir)
        println("文件已删除")
    }

    fun demonstrateFileMetadata() {
        println("\n=== 文件元数据 ===")
        
        val tempDir = System.getProperty("java.io.tmpdir").toPath() / "okio-metadata-demo"
        fileSystem.createDirectories(tempDir)
        
        val filePath = tempDir / "metadata-test.txt"
        fileSystem.sink(filePath).buffer().use { it.writeUtf8("Test content") }
        
        val metadata = fileSystem.metadata(filePath)
        println("是否为文件: ${metadata.isRegularFile}")
        println("是否为目录: ${metadata.isDirectory}")
        println("文件大小: ${metadata.size} 字节")
        
        fileSystem.delete(filePath)
        fileSystem.delete(tempDir)
    }

    fun demonstrateDirectoryOperations() {
        println("\n=== 目录操作 ===")
        
        val tempDir = System.getProperty("java.io.tmpdir").toPath() / "okio-dir-demo"
        fileSystem.createDirectories(tempDir)
        
        val subDir = tempDir / "subdir"
        fileSystem.createDirectories(subDir)
        
        fileSystem.sink(tempDir / "file1.txt").buffer().use { it.writeUtf8("File 1") }
        fileSystem.sink(tempDir / "file2.txt").buffer().use { it.writeUtf8("File 2") }
        fileSystem.sink(subDir / "file3.txt").buffer().use { it.writeUtf8("File 3") }
        
        println("目录内容:")
        fileSystem.list(tempDir).forEach { path ->
            println("  ${path.name}")
        }
        
        fileSystem.deleteRecursively(tempDir)
        println("目录已递归删除")
    }

    fun demonstrateCopyMove() {
        println("\n=== 复制和移动 ===")
        
        val tempDir = System.getProperty("java.io.tmpdir").toPath() / "okio-copy-demo"
        fileSystem.createDirectories(tempDir)
        
        val sourcePath = tempDir / "source.txt"
        fileSystem.sink(sourcePath).buffer().use { it.writeUtf8("Original content") }
        
        val copyPath = tempDir / "copy.txt"
        fileSystem.copy(sourcePath, copyPath)
        println("文件已复制")
        
        val movePath = tempDir / "moved.txt"
        fileSystem.atomicMove(copyPath, movePath)
        println("文件已移动")
        
        println("源文件存在: ${fileSystem.exists(sourcePath)}")
        println("复制文件存在: ${fileSystem.exists(copyPath)}")
        println("移动文件存在: ${fileSystem.exists(movePath)}")
        
        fileSystem.deleteRecursively(tempDir)
    }
}

fun main() {
    FileSystemDemo.demonstrateBasicFileOperations()
    FileSystemDemo.demonstrateFileMetadata()
    FileSystemDemo.demonstrateDirectoryOperations()
    FileSystemDemo.demonstrateCopyMove()
}
