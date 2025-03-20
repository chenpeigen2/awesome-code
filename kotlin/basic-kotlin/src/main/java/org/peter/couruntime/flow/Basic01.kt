package org.peter.couruntime.flow

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {    // 主线程进入协程作用域
    val customScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    customScope.launch(Dispatchers.IO) {  // 直接使用 runBlocking 的作用域
        println("what are you doing")
    }


    // 自动等待所有子协程完成
}