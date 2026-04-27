import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

expect fun getPlatformName(): String
expect fun getSystemInfo(): String
expect fun currentTimeMillis(): Long

fun main() = runBlocking {
    println("=== Kotlin/Native Demo ===")
    println()

    // 1. 平台信息 (expect/actual)
    println("[Platform] Running on: ${getPlatformName()}")
    println("[System] ${getSystemInfo()}")
    println()

    // 2. 时间处理
    println("[Time] Current millis: ${currentTimeMillis()}")
    println()

    // 3. 协程 (new-native runtime)
    println("[Coroutine] Starting async task...")
    delay(500)
    println("[Coroutine] Task completed after 500ms")
    println()

    // 4. 集合操作
    val numbers = (1..10).toList()
    val evens = numbers.filter { it % 2 == 0 }
    val sum = numbers.reduce { acc, n -> acc + n }
    println("[Collections] Numbers: $numbers")
    println("[Collections] Evens: $evens")
    println("[Collections] Sum: $sum")
    println()

    // 5. 密封类 + when
    val result = divide(10, 3)
    println("[Sealed] 10 / 3 = $result")
    val error = divide(10, 0)
    println("[Sealed] 10 / 0 = $error")
    println()

    // 6. inline class (value class)
    val distance = Distance(42.5)
    println("[ValueClass] Distance: ${distance.km} km, ${distance.miles} miles")
    println()

    println("=== Demo Complete ===")
}

sealed interface DivisionResult {
    data class Success(val value: Double) : DivisionResult
    data class Error(val message: String) : DivisionResult
}

fun divide(a: Int, b: Int): DivisionResult = if (b == 0) {
    DivisionResult.Error("Division by zero")
} else {
    DivisionResult.Success(a.toDouble() / b)
}

value class Distance(val km: Double) {
    val miles: Double get() = km * 0.621371
}
