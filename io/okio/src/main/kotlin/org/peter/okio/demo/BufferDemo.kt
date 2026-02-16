package org.peter.okio.demo

import okio.Buffer

object BufferDemo {

    fun demonstrateBasicOperations() {
        println("=== Buffer 基本操作 ===")
        
        val buffer = Buffer()
        
        buffer.writeUtf8("Hello, Okio!")
        println("写入后大小: ${buffer.size}")
        
        val content = buffer.readUtf8()
        println("读取内容: $content")
        println("读取后大小: ${buffer.size}")
    }

    fun demonstrateWriteOperations() {
        println("\n=== Buffer 写入操作 ===")
        
        val buffer = Buffer()
        
        buffer.writeByte(0x48)
        buffer.writeShort(0x656C)
        buffer.writeInt(0x6C6F0000)
        buffer.writeUtf8(" World")
        
        println("写入多种类型数据后大小: ${buffer.size}")
        println("内容: ${buffer.readUtf8()}")
    }

    fun demonstrateReadOperations() {
        println("\n=== Buffer 读取操作 ===")
        
        val buffer = Buffer()
        buffer.writeInt(0x12345678)
        buffer.writeLong(Long.MAX_VALUE)
        buffer.writeUtf8("测试")
        
        println("读取Int: 0x${Integer.toHexString(buffer.readInt())}")
        println("读取Long: ${buffer.readLong()}")
        println("读取Utf8: ${buffer.readUtf8()}")
    }

    fun demonstrateCopy() {
        println("\n=== Buffer 复制操作 ===")
        
        val source = Buffer()
        source.writeUtf8("Hello World")
        
        val dest = Buffer()
        source.copyTo(dest, 0, 5)
        
        println("源Buffer剩余: ${source.readUtf8()}")
        println("目标Buffer: ${dest.readUtf8()}")
    }
}

fun main() {
    BufferDemo.demonstrateBasicOperations()
    BufferDemo.demonstrateWriteOperations()
    BufferDemo.demonstrateReadOperations()
    BufferDemo.demonstrateCopy()
}
