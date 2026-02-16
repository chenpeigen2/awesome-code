package org.peter.okio.demo

import okio.ByteString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ByteStringDemoTest {

    @Test
    fun testCreation() {
        val fromBytes = ByteString.of(0x48, 0x65, 0x6C, 0x6C, 0x6F)
        assertEquals("Hello", fromBytes.utf8())
        
        val fromByteArray = ByteString.of(*"Hello World".toByteArray())
        assertEquals("Hello World", fromByteArray.utf8())
        
        val empty = ByteString.EMPTY
        assertEquals(0, empty.size)
    }

    @Test
    fun testEncoding() {
        val bytes = ByteString.of(*"Hello".toByteArray())
        
        assertEquals("Hello", bytes.utf8())
        assertEquals("48656c6c6f", bytes.hex())
        assertNotNull(bytes.base64())
    }

    @Test
    fun testHashing() {
        val data = ByteString.of(*"Hello World".toByteArray())
        
        assertNotNull(data.md5().hex())
        assertNotNull(data.sha1().hex())
        assertNotNull(data.sha256().hex())
        assertNotNull(data.sha512().hex())
        
        assertEquals(16, data.md5().size)
        assertEquals(20, data.sha1().size)
        assertEquals(32, data.sha256().size)
        assertEquals(64, data.sha512().size)
    }

    @Test
    fun testOperations() {
        val str1 = ByteString.of(*"Hello".toByteArray())
        val str2 = ByteString.of(*"Hello".toByteArray())
        val str3 = ByteString.of(*"World".toByteArray())
        
        assertEquals(str1, str2)
        assertNotEquals(str1, str3)
        
        val substring = str1.substring(0, 3)
        assertEquals("Hel", substring.utf8())
        
        assertTrue(str1.startsWith(ByteString.of(*"He".toByteArray())))
        assertTrue(str1.endsWith(ByteString.of(*"lo".toByteArray())))
    }
}
