import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlin.js.Promise

// ========== 1. external: 声明 JavaScript 外部函数和对象 ==========
external val process: Process

external interface Process {
    val version: String
    val platform: String
    val arch: String
    fun cwd(): String
    val env: dynamic
}

external fun require(name: String): dynamic

external fun setTimeout(handler: () -> Unit, timeout: Int): dynamic

// ========== 2. inline JS: 直接嵌入 JavaScript 代码 ==========
fun jsGreet(name: String): dynamic {
    val greeting: dynamic = js("({})")
    greeting.value = "Hello, " + name + " from inline JS!"
    return greeting.value
}

fun jsTimestamp(): Double = js("Date.now()")

// ========== 3. dynamic 类型: 动态访问 JS 对象 ==========
fun demoDynamicType() {
    println("--- Dynamic Type ---")
    val obj: dynamic = js("({ name: 'Kotlin/JS', version: 2.3, features: ['coroutines', 'interop', 'serialization'] })")
    println("name: ${obj.name}")
    println("version: ${obj.version}")
    println("features: ${obj.features}")
    println()
}

// ========== 4. JSON 处理 ==========
fun demoJson() {
    println("--- JSON ---")
    val data = js("({ lang: 'Kotlin', target: 'JavaScript', year: 2026 })")
    val json = JSON.stringify(data)
    println("Stringify: $json")

    val parsed = JSON.parse<JsonData>(json)
    println("Parsed lang: ${parsed.lang}, year: ${parsed.year}")
    println()
}

external interface JsonData {
    val lang: String
    val target: String
    val year: Int
}

// ========== 5. Promise / async 互操作 ==========
fun demoAsync(): Promise<String> = MainScope().promise {
    println("--- Async/Promise ---")
    delay(300)
    println("  waited 300ms...")
    delay(200)
    println("  waited 200ms more")
    "Async completed"
}

// ========== 6. 集合操作 (Kotlin 惯用写法编译为 JS) ==========
fun demoCollections() {
    println("--- Collections ---")
    val words = listOf("Kotlin", "JavaScript", "TypeScript", "Dart", "Swift")
    val sorted = words.sortedBy { it.length }
    val grouped = words.groupBy { it.first() }
    val lengths = words.associateWith { it.length }

    println("Sorted by length: $sorted")
    println("Grouped by first char: $grouped")
    println("Word lengths: $lengths")
    println()
}

// ========== 7. 密封类 + when ==========
sealed interface JsResult {
    data class Success(val data: String) : JsResult
    data class Error(val message: String) : JsResult
}

fun fetchData(shouldFail: Boolean): JsResult = if (shouldFail) {
    JsResult.Error("Network timeout")
} else {
    JsResult.Success("""{"status":"ok","items":[1,2,3]}""")
}

fun demoSealed() {
    println("--- Sealed Class ---")
    for (fail in listOf(false, true)) {
        val result = fetchData(fail)
        val msg = when (result) {
            is JsResult.Success -> "Got: ${result.data}"
            is JsResult.Error -> "Failed: ${result.message}"
        }
        println("  fail=$fail -> $msg")
    }
    println()
}

// ========== 8. 扩展函数 ==========
fun String.titleCase(): String = replaceFirstChar { it.uppercase() }

fun demoExtensions() {
    println("--- Extensions ---")
    val greeting = "hello kotlin/js world".titleCase()
    println("  titleCase: $greeting")

    val numbers = (1..5).toList()
    println("  sum: ${numbers.sum()}, avg: ${numbers.average()}")
    println()
}

// ========== main ==========
fun main() {
    println("=== Kotlin/JS Demo ===")
    println()

    // Platform info
    println("--- Platform ---")
    println("Node.js version: ${process.version}")
    println("Platform: ${process.platform} (${process.arch})")
    println("CWD: ${process.cwd()}")
    println()

    // Inline JS
    println("--- Inline JS ---")
    println(jsGreet("World"))
    println("Timestamp: ${jsTimestamp()}")
    println()

    demoDynamicType()
    demoJson()
    demoCollections()
    demoSealed()
    demoExtensions()

    // Async demo
    demoAsync().then { result ->
        println("  -> $result")
        println()
        println("=== Demo Complete ===")
        Unit
    }
}
