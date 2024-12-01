package org.peter.couruntime.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    flow<Int> {
        for (i in 1..5) {
            delay(1000)
            emit(i)
        }
    }.collect {
        print(it)
    }
}