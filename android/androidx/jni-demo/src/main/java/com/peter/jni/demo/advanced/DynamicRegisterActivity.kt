package com.peter.jni.demo.advanced

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

class DynamicRegisterActivity : BaseDemoActivity() {

    private lateinit var nativeLib: DynamicNativeLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = DynamicNativeLib()

        addSection("说明: 使用 JNI_OnLoad + RegisterNatives 动态注册") {
            appendResult("无需遵循 Java_package_method 命名规则\n函数名自由定义，编译期即可检查签名")
        }

        addSection("1. 动态注册 - 获取版本") {
            appendResult("version = ${nativeLib.getVersion()}")
        }

        addSection("2. 动态注册 - FNV-1a 哈希") {
            appendResult("hash(\"Hello JNI Dynamic!\") = ${nativeLib.md5("Hello JNI Dynamic!")}")
        }

        addSection("3. 动态注册 - Base64 编码") {
            appendResult("base64Encode(\"Hello JNI Dynamic!\") = ${nativeLib.base64Encode("Hello JNI Dynamic!")}")
        }
    }
}

class DynamicNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun getVersion(): String
    external fun md5(input: String): String
    external fun base64Encode(input: String): String
}
