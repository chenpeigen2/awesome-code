package com.peter.network.demo.interceptor

object InMemoryLogStore {
    private const val MAX_LOGS = 120
    private val logs = ArrayDeque<String>()

    @Synchronized
    fun add(message: String) {
        if (logs.size >= MAX_LOGS) {
            logs.removeFirst()
        }
        logs.addLast(message)
    }

    @Synchronized
    fun clear() {
        logs.clear()
    }

    @Synchronized
    fun snapshot(): List<String> = logs.toList()
}
