package com.peter.jni.demo.basic

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

class BasicCallActivity : BaseDemoActivity() {

    private lateinit var nativeLib: BasicNativeLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = BasicNativeLib()

        addSection("1. native 返回字符串") {
            appendResult("C++ 返回: ${nativeLib.getStringFromNative()}")
        }

        addSection("2. 加法运算") {
            appendResult("10 + 20 = ${nativeLib.add(10, 20)}")
        }

        addSection("3. 减法运算") {
            appendResult("50 - 18 = ${nativeLib.subtract(50, 18)}")
        }

        addSection("4. 乘法运算") {
            appendResult("7 × 8 = ${nativeLib.multiply(7, 8)}")
        }

        addSection("5. 除法运算") {
            appendResult("100.0 ÷ 3.0 = ${"%.4f".format(nativeLib.divide(100.0, 3.0))}")
        }
    }
}

class BasicNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun getStringFromNative(): String
    external fun add(a: Int, b: Int): Int
    external fun subtract(a: Int, b: Int): Int
    external fun multiply(a: Int, b: Int): Int
    external fun divide(a: Double, b: Double): Double
}
