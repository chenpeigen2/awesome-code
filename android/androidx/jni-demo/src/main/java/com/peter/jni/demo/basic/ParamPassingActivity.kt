package com.peter.jni.demo.basic

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

class ParamPassingActivity : BaseDemoActivity() {

    private lateinit var nativeLib: ParamNativeLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = ParamNativeLib()

        addSection("1. 传递 int 并返回平方") {
            appendResult("square(12) = ${nativeLib.square(12)}")
        }

        addSection("2. 传递 float 并返回格式化字符串") {
            appendResult("formatFloat(3.14159) = ${nativeLib.formatFloat(3.14159f)}")
        }

        addSection("3. 传递 boolean 取反") {
            appendResult("invertBoolean(true) = ${nativeLib.invertBoolean(true)}")
        }

        addSection("4. 传递 String 并拼接") {
            appendResult("greet(\"Android JNI\") = ${nativeLib.greet("Android JNI")}")
        }

        addSection("5. 修改传入的 String") {
            appendResult("reverseString(\"Hello World\") = ${nativeLib.reverseString("Hello World")}")
        }

        addSection("6. 传递 long 值") {
            appendResult("factorial(10) = ${nativeLib.factorial(10L)}")
        }
    }
}

class ParamNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun square(n: Int): Int
    external fun formatFloat(value: Float): String
    external fun invertBoolean(value: Boolean): Boolean
    external fun greet(name: String): String
    external fun reverseString(str: String): String
    external fun factorial(n: Long): Long
}
