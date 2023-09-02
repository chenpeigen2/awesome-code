package org.peter.couruntime

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/*
    挂起函数的组合
 */

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = async { intValue1() }
        val value2 = async { intValue2() }


        val result1 = value1.await()
        val result2 = value2.await()

        println("$result1 + $result2 = ${result1 + result2}")
    }

    println("total time: $elapsedTime")
}


private suspend fun intValue1(): Int {
    delay(1000)
    return 15
}

private suspend fun intValue2(): Int {
    delay(2000)
    return 20
}