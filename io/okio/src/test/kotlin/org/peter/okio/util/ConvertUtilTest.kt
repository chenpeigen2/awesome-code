package org.peter.okio.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ConvertUtilTest {

    @Test
    fun testBytesToHex() {
        val bytes = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F)
        val hex = ConvertUtil.bytesToHex(bytes)
        
        assertEquals("48656c6c6f", hex)
    }

    @Test
    fun testHexToBytes() {
        val hex = "48656c6c6f"
        val bytes = ConvertUtil.hexToBytes(hex)
        
        assertArrayEquals(byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F), bytes)
    }

    @Test
    fun testBytesToBase64() {
        val bytes = "Hello".toByteArray()
        val base64 = ConvertUtil.bytesToBase64(bytes)
        
        assertEquals("SGVsbG8=", base64)
    }

    @Test
    fun testBase64ToBytes() {
        val base64 = "SGVsbG8="
        val bytes = ConvertUtil.base64ToBytes(base64)
        
        assertArrayEquals("Hello".toByteArray(), bytes)
    }

    @Test
    fun testStringToHex() {
        val text = "Hello"
        val hex = ConvertUtil.stringToHex(text)
        
        assertEquals("48656c6c6f", hex)
    }

    @Test
    fun testHexToString() {
        val hex = "48656c6c6f"
        val text = ConvertUtil.hexToString(hex)
        
        assertEquals("Hello", text)
    }

    @Test
    fun testStringToBase64() {
        val text = "Hello"
        val base64 = ConvertUtil.stringToBase64(text)
        
        assertEquals("SGVsbG8=", base64)
    }

    @Test
    fun testBase64ToString() {
        val base64 = "SGVsbG8="
        val text = ConvertUtil.base64ToString(base64)
        
        assertEquals("Hello", text)
    }

    @Test
    fun testMd5() {
        val text = "Hello"
        val md5 = ConvertUtil.md5(text)
        
        assertEquals(32, md5.length)
    }

    @Test
    fun testSha256() {
        val text = "Hello"
        val sha256 = ConvertUtil.sha256(text)
        
        assertEquals(64, sha256.length)
    }

    @Test
    fun testSha512() {
        val text = "Hello"
        val sha512 = ConvertUtil.sha512(text)
        
        assertEquals(128, sha512.length)
    }

    @Test
    fun testHmacSha256() {
        val text = "Hello"
        val key = "secret"
        val hmac = ConvertUtil.hmacSha256(text, key)
        
        assertEquals(64, hmac.length)
    }

    @Test
    fun testBytesMd5() {
        val bytes = "Hello".toByteArray()
        val md5 = ConvertUtil.md5(bytes)
        
        assertEquals(ConvertUtil.md5("Hello"), md5)
    }
}
