package org.peter.couruntime

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val flow = MutableSharedFlow<Int>(
        replay = 2,
        extraBufferCapacity = 3,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    println("=== 测试 1：老订阅者 vs 新订阅者 ===\n")
    println("配置：replay=2, extraBufferCapacity=3\n")

    // 老订阅者：在发送之前订阅
    println("【老订阅者】开始订阅...")
    var oldCount = 0
    val oldSubscriber = launch {
        flow.collect {
            oldCount++
            println("  老订阅者收到: $it (第 $oldCount 个)")
        }
    }

    delay(50)  // 确保老订阅者已经订阅

    // 发送 10 个事件
    println("\n【生产者】开始发送 10 个事件:")
    repeat(10) { i ->
        flow.emit(i)
        println("  发送: $i")
        delay(10)
    }

    delay(100)

    // 新订阅者：在发送之后订阅
    println("\n【新订阅者】开始订阅...")
    var newCount = 0
    val newSubscriber = launch {
        flow.collect {
            newCount++
            if (newCount <= 2) {
                println("  新订阅者收到: $it (第 $newCount 个)")
            }
        }
    }


    // 新订阅者：在发送之后订阅
    println("\n【新订阅者1】开始订阅...")
    var newCount1 = 0
    val newSubscriber1 = launch {
        flow.collect {
            newCount1++
            if (newCount1 <= 2) {
                println("  新订阅者1收到: $it (第 $newCount1 个)")
            }
        }
    }

    delay(100)

    println("\n=== 总结 ===")
    println("老订阅者收到: $oldCount 个事件（所有事件）")
    println("新订阅者收到: $newCount 个事件（只收到 replay=2 个）")

    oldSubscriber.cancel()
    newSubscriber.cancel()
//    newSubscriber1.cancel()
}

