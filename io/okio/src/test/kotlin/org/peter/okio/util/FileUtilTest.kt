package org.peter.okio.util

import okio.Path.Companion.toPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FileUtilTest {

    private lateinit var tempDir: okio.Path

    @BeforeEach
    fun setup() {
        tempDir = System.getProperty("java.io.tmpdir").toPath() / "okio-util-test-${System.currentTimeMillis()}"
        FileUtil.createDirectory(tempDir)
    }

    @AfterEach
    fun cleanup() {
        FileUtil.deleteRecursively(tempDir)
    }

    @Test
    fun testWriteAndReadText() {
        val filePath = tempDir / "test.txt"
        val content = "Hello, FileUtil!"
        
        FileUtil.writeText(filePath, content)
        val readContent = FileUtil.readText(filePath)
        
        assertEquals(content, readContent)
    }

    @Test
    fun testWriteAndReadBytes() {
        val filePath = tempDir / "test.bin"
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        
        FileUtil.writeBytes(filePath, bytes)
        val readBytes = FileUtil.readBytes(filePath)
        
        assertArrayEquals(bytes, readBytes)
    }

    @Test
    fun testCopy() {
        val sourcePath = tempDir / "source.txt"
        val targetPath = tempDir / "copy.txt"
        
        FileUtil.writeText(sourcePath, "Copy test")
        FileUtil.copy(sourcePath, targetPath)
        
        assertTrue(FileUtil.exists(targetPath))
        assertEquals("Copy test", FileUtil.readText(targetPath))
    }

    @Test
    fun testMove() {
        val sourcePath = tempDir / "source.txt"
        val targetPath = tempDir / "moved.txt"
        
        FileUtil.writeText(sourcePath, "Move test")
        FileUtil.move(sourcePath, targetPath)
        
        assertFalse(FileUtil.exists(sourcePath))
        assertTrue(FileUtil.exists(targetPath))
        assertEquals("Move test", FileUtil.readText(targetPath))
    }

    @Test
    fun testDelete() {
        val filePath = tempDir / "delete.txt"
        
        FileUtil.writeText(filePath, "Delete test")
        assertTrue(FileUtil.exists(filePath))
        
        FileUtil.delete(filePath)
        assertFalse(FileUtil.exists(filePath))
    }

    @Test
    fun testExists() {
        val filePath = tempDir / "exists.txt"
        
        assertFalse(FileUtil.exists(filePath))
        
        FileUtil.writeText(filePath, "Exists test")
        assertTrue(FileUtil.exists(filePath))
    }

    @Test
    fun testListFiles() {
        FileUtil.writeText(tempDir / "file1.txt", "1")
        FileUtil.writeText(tempDir / "file2.txt", "2")
        
        val files = FileUtil.listFiles(tempDir)
        
        assertEquals(2, files.size)
    }

    @Test
    fun testGetFileSize() {
        val filePath = tempDir / "size.txt"
        val content = "Size test"
        
        FileUtil.writeText(filePath, content)
        
        assertEquals(content.toByteArray().size.toLong(), FileUtil.getFileSize(filePath))
    }

    @Test
    fun testIsFileAndIsDirectory() {
        val filePath = tempDir / "file.txt"
        FileUtil.writeText(filePath, "test")
        
        assertTrue(FileUtil.isFile(filePath))
        assertFalse(FileUtil.isDirectory(filePath))
        
        assertFalse(FileUtil.isFile(tempDir))
        assertTrue(FileUtil.isDirectory(tempDir))
    }

    @Test
    fun testAppendText() {
        val filePath = tempDir / "append.txt"
        
        FileUtil.writeText(filePath, "Line1\n")
        FileUtil.appendText(filePath, "Line2\n")
        
        assertEquals("Line1\nLine2\n", FileUtil.readText(filePath))
    }
}
