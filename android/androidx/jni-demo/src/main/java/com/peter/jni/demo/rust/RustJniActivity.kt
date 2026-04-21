package com.peter.jni.demo.rust

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

class RustJniActivity : BaseDemoActivity() {

    private lateinit var nativeLib: RustNativeLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = RustNativeLib()

        addSection("1. Rust 返回字符串") {
            appendResult("Rust 返回: ${nativeLib.getStringFromRust()}")
        }

        addSection("2. Rust 加法运算") {
            appendResult("100 + 200 = ${nativeLib.add(100, 200)}")
        }

        addSection("3. Rust 乘法运算") {
            appendResult("123 × 456 = ${nativeLib.multiply(123, 456)}")
        }

        addSection("4. Rust 斐波那契") {
            appendResult("fibonacci(50) = ${nativeLib.fibonacci(50)}")
        }

        addSection("5. Rust 反转字符串") {
            appendResult("reverse(\"Hello Rust!\") = ${nativeLib.reverseString("Hello Rust!")}")
        }

        addSection("6. Rust 异或加密/解密") {
            val data = "Rust JNI!".toByteArray()
            val key: Byte = 0x42
            val encrypted = nativeLib.xorEncrypt(data, key)
            val decrypted = nativeLib.xorEncrypt(encrypted, key)
            appendResult("原文: Rust JNI!\n加密(hex): ${encrypted.toHexString()}\n解密: ${String(decrypted)}")
        }

        addSection("7. Rust 判断素数") {
            val results = (1..20).map { n -> "$n=${nativeLib.isPrime(n)}" }.joinToString(", ")
            appendResult("1..20 是否素数: $results")
        }
    }
}
