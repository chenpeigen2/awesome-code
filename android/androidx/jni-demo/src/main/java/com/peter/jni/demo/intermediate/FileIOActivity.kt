package com.peter.jni.demo.intermediate

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R
import java.io.File

class FileIOActivity : BaseDemoActivity() {

    private lateinit var nativeLib: FileIONativeLib
    private lateinit var testFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = FileIONativeLib()
        testFile = File(cacheDir, "jni_test.txt")

        addSection("1. 写入文件（通过 JNI）") {
            nativeLib.writeFile(testFile.absolutePath, "Hello from JNI!\n第二行数据\nThird line")
            appendResult("已写入: ${testFile.absolutePath}")
        }

        addSection("2. 读取文件（通过 JNI）") {
            val content = nativeLib.readFile(testFile.absolutePath)
            appendResult("读取内容:\n$content")
        }

        addSection("3. 获取文件大小") {
            val size = nativeLib.getFileSize(testFile.absolutePath)
            appendResult("文件大小: $size 字节")
        }
    }
}

class FileIONativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun writeFile(path: String, content: String)
    external fun readFile(path: String): String
    external fun getFileSize(path: String): Long
}
