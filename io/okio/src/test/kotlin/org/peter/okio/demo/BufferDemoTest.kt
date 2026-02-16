package org.peter.okio.demo

import okio.Buffer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BufferDemoTest {

    @Test
    fun testBasicOperations() {
        val buffer = Buffer()
        
        buffer.writeUtf8("Hello, Okio!")
        assertEquals(12L, buffer.size)
        
        val content = buffer.readUtf8()
        assertEquals("Hello, Okio!", content)
        assertEquals(0L, buffer.size)
    }

    @Test
    fun testWriteOperations() {
        val buffer = Buffer()
        
        buffer.writeByte(0x48)
        buffer.writeShort(0x656C)
        buffer.writeInt(0x6C6F0000)
        buffer.writeUtf8(" World")
        
        assertTrue(buffer.size > 0)
    }

    @Test
    fun testReadOperations() {
        val buffer = Buffer()
        buffer.writeInt(0x12345678)
        buffer.writeLong(Long.MAX_VALUE)
        buffer.writeUtf8("测试")
        
        assertEquals(0x12345678, buffer.readInt())
        assertEquals(Long.MAX_VALUE, buffer.readLong())
        assertEquals("测试", buffer.readUtf8())
    }

    @Test
    fun testCopy() {
        val source = Buffer()
        source.writeUtf8("Hello World")
        
        val dest = Buffer()
        source.copyTo(dest, 0, 5)
        
        assertEquals(11L, source.size)
        assertEquals(5L, dest.size)
    }
}
