package org.peter.okio.demo

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FileSystemDemoTest {

    private val fileSystem = FileSystem.SYSTEM
    private lateinit var tempDir: okio.Path

    @BeforeEach
    fun setup() {
        tempDir = System.getProperty("java.io.tmpdir").toPath() / "okio-test-${System.currentTimeMillis()}"
        fileSystem.createDirectories(tempDir)
    }

    @AfterEach
    fun cleanup() {
        if (fileSystem.exists(tempDir)) {
            fileSystem.deleteRecursively(tempDir)
        }
    }

    @Test
    fun testBasicFileOperations() {
        val filePath = tempDir / "test.txt"
        
        fileSystem.sink(filePath).buffer().use { sink ->
            sink.writeUtf8("Hello, Okio FileSystem!")
        }
        
        assertTrue(fileSystem.exists(filePath))
        
        val content = fileSystem.source(filePath).buffer().use { source ->
            source.readUtf8()
        }
        
        assertEquals("Hello, Okio FileSystem!", content)
    }

    @Test
    fun testFileMetadata() {
        val filePath = tempDir / "metadata-test.txt"
        fileSystem.sink(filePath).buffer().use { it.writeUtf8("Test content") }
        
        val metadata = fileSystem.metadata(filePath)
        
        assertTrue(metadata.isRegularFile)
        assertFalse(metadata.isDirectory)
        assertNotNull(metadata.size)
    }

    @Test
    fun testDirectoryOperations() {
        val subDir = tempDir / "subdir"
        fileSystem.createDirectories(subDir)
        
        fileSystem.sink(tempDir / "file1.txt").buffer().use { it.writeUtf8("File 1") }
        fileSystem.sink(tempDir / "file2.txt").buffer().use { it.writeUtf8("File 2") }
        
        val list = fileSystem.list(tempDir)
        
        assertTrue(list.any { it.name == "file1.txt" })
        assertTrue(list.any { it.name == "file2.txt" })
        assertTrue(list.any { it.name == "subdir" })
    }

    @Test
    fun testCopyMove() {
        val sourcePath = tempDir / "source.txt"
        fileSystem.sink(sourcePath).buffer().use { it.writeUtf8("Original content") }
        
        val copyPath = tempDir / "copy.txt"
        fileSystem.copy(sourcePath, copyPath)
        
        assertTrue(fileSystem.exists(sourcePath))
        assertTrue(fileSystem.exists(copyPath))
        
        val movePath = tempDir / "moved.txt"
        fileSystem.atomicMove(copyPath, movePath)
        
        assertFalse(fileSystem.exists(copyPath))
        assertTrue(fileSystem.exists(movePath))
    }
}
