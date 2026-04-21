package com.peter.jni.demo.intermediate

import android.os.Bundle
import com.peter.jni.demo.BaseDemoActivity
import com.peter.jni.demo.R

data class User(var name: String, var age: Int) {
    fun getInfo(): String = "User(name=$name, age=$age)"
}

class ObjectManipulationActivity : BaseDemoActivity() {

    private lateinit var nativeLib: ObjectNativeLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLib = ObjectNativeLib()

        addSection("1. C++ 创建 Java 对象") {
            val user = nativeLib.createUser("Peter", 28)
            appendResult("C++ 创建 User: name=${user.name}, age=${user.age}")
        }

        addSection("2. C++ 访问/修改 Java 字段") {
            val user = User("Alice", 25)
            nativeLib.modifyUserFields(user)
            appendResult("修改后 User: name=${user.name}, age=${user.age}")
        }

        addSection("3. C++ 调用 Java 方法") {
            val user = User("Bob", 30)
            val info = nativeLib.callUserMethod(user)
            appendResult("C++ 调用 user.getInfo(): $info")
        }

        addSection("4. C++ 创建 String 并返回") {
            val result = nativeLib.createStringFromChars(charArrayOf('H', 'e', 'l', 'l', 'o'))
            appendResult("C++ 从 char[] 创建 String: $result")
        }
    }
}

class ObjectNativeLib {
    companion object {
        init {
            System.loadLibrary("jni_demo")
        }
    }

    external fun createUser(name: String, age: Int): User
    external fun modifyUserFields(user: User)
    external fun callUserMethod(user: User): String
    external fun createStringFromChars(chars: CharArray): String
}
