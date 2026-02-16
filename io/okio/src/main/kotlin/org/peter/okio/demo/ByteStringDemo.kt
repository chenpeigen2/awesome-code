package org.peter.okio.demo

import okio.ByteString

object ByteStringDemo {

    fun demonstrateCreation() {
        println("=== ByteString 创建 ===")
        
        val fromBytes = ByteString.of(0x48, 0x65, 0x6C, 0x6C, 0x6F)
        println("从字节创建: ${fromBytes.utf8()}")
        
        val fromByteArray = ByteString.of(*"Hello World".toByteArray())
        println("从字节数组创建: ${fromByteArray.utf8()}")
        
        val empty = ByteString.EMPTY
        println("空 ByteString 大小: ${empty.size}")
    }

    fun demonstrateEncoding() {
        println("\n=== ByteString 编码 ===")
        
        val bytes = ByteString.of(*"Hello".toByteArray())
        
        println("UTF-8: ${bytes.utf8()}")
        println("Hex: ${bytes.hex()}")
        println("Base64: ${bytes.base64()}")
    }

    fun demonstrateHashing() {
        println("\n=== ByteString 哈希 ===")
        
        val data = ByteString.of(*"Hello World".toByteArray())
        
        println("MD5: ${data.md5().hex()}")
        println("SHA-1: ${data.sha1().hex()}")
        println("SHA-256: ${data.sha256().hex()}")
        println("SHA-512: ${data.sha512().hex()}")
    }

    fun demonstrateOperations() {
        println("\n=== ByteString 操作 ===")
        
        val str1 = ByteString.of(*"Hello".toByteArray())
        val str2 = ByteString.of(*"Hello".toByteArray())
        val str3 = ByteString.of(*"World".toByteArray())
        
        println("str1 == str2: ${str1 == str2}")
        println("str1 == str3: ${str1 == str3}")
        
        val substring = str1.substring(0, 3)
        println("substring(0,3): ${substring.utf8()}")
        
        println("startsWith('He'): ${str1.startsWith(ByteString.of(*"He".toByteArray()))}")
        println("endsWith('lo'): ${str1.endsWith(ByteString.of(*"lo".toByteArray()))}")
    }
}

fun main() {
    ByteStringDemo.demonstrateCreation()
    ByteStringDemo.demonstrateEncoding()
    ByteStringDemo.demonstrateHashing()
    ByteStringDemo.demonstrateOperations()
}
