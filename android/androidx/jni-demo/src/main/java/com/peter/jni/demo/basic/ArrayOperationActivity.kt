package com.peter.jni.demo.basic

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

class ArrayOperationActivity : BaseDemoActivity() {

    private lateinit var nativeLib: ArrayNativeLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = ArrayNativeLib()

        addSection("1. int 数组求和") {
            val arr = intArrayOf(10, 20, 30, 40, 50)
            appendResult("sum([10,20,30,40,50]) = ${nativeLib.sumArray(arr)}")
        }

        addSection("2. int 数组排序（原地）") {
            val arr = intArrayOf(5, 3, 8, 1, 9, 2, 7)
            nativeLib.sortArray(arr)
            appendResult("sort([5,3,8,1,9,2,7]) = ${arr.toList()}")
        }

        addSection("3. int 数组反转") {
            val arr = intArrayOf(1, 2, 3, 4, 5)
            nativeLib.reverseArray(arr)
            appendResult("reverse([1,2,3,4,5]) = ${arr.toList()}")
        }

        addSection("4. byte 数组异或加密") {
            val data = "Hello JNI!".toByteArray()
            val key: Byte = 0x55
            val encrypted = nativeLib.xorByteArray(data, key)
            val decrypted = nativeLib.xorByteArray(encrypted, key)
            appendResult("原文: Hello JNI!\n加密(hex): ${encrypted.toHexString()}\n解密: ${String(decrypted)}")
        }

        addSection("5. 创建 int 数组（C++ 创建返回）") {
            val result = nativeLib.createIntArray(10)
            appendResult("createIntArray(10) = ${result.toList()}")
        }
    }
}

class ArrayNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun sumArray(arr: IntArray): Int
    external fun sortArray(arr: IntArray)
    external fun reverseArray(arr: IntArray)
    external fun xorByteArray(data: ByteArray, key: Byte): ByteArray
    external fun createIntArray(size: Int): IntArray
}
