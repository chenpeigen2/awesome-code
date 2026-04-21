package com.peter.jni.demo.rust

class RustNativeLib {
    companion object {
        init {
            System.loadLibrary("rust_jni")
        }
    }

    external fun getStringFromRust(): String
    external fun add(a: Int, b: Int): Int
    external fun multiply(a: Int, b: Int): Int
    external fun fibonacci(n: Int): Long
    external fun reverseString(input: String): String
    external fun xorEncrypt(data: ByteArray, key: Byte): ByteArray
    external fun isPrime(n: Int): Boolean
}
