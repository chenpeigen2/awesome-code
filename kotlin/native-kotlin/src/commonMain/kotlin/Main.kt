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

    // 7. POSIX 文件操作
    demoFileOps()

    println("=== Demo Complete ===")
}

private fun demoFileOps() {
    val testDir = "/tmp/knative-demo"
    val testFile = "$testDir/hello.txt"

    println("[FileOps] --- POSIX C API 文件操作演示 ---")
    println()

    // 创建目录
    when (val r = nativeMkdir(testDir)) {
        is FileResult.Ok -> println("[FileOps] mkdir $testDir  OK")
        is FileResult.Err -> println("[FileOps] mkdir failed: ${r.message}")
    }

    // 写文件 (open + write + close)
    val content = "Hello from Kotlin/Native!\n调用 POSIX C API 写入的文件。\n"
    when (val r = nativeWriteFile(testFile, content)) {
        is FileResult.Ok -> println("[FileOps] write $testFile  OK (${content.encodeToByteArray().size} bytes)")
        is FileResult.Err -> println("[FileOps] write failed: ${r.message}")
    }

    // 读文件 (open + read + close)
    when (val r = nativeReadFile(testFile)) {
        is FileResult.Ok -> {
            println("[FileOps] read  $testFile:")
            r.value.lines().forEach { println("         | $it") }
        }
        is FileResult.Err -> println("[FileOps] read failed: ${r.message}")
    }

    // stat 文件信息
    when (val r = nativeStat(testFile)) {
        is FileResult.Ok -> {
            val info = r.value
            println("[FileOps] stat  $testFile: size=${info.size}, isDir=${info.isDirectory}, mode=0${info.mode.toString(8)}, mtime=${info.lastModified}")
        }
        is FileResult.Err -> println("[FileOps] stat failed: ${r.message}")
    }

    // stat 目录信息
    when (val r = nativeStat(testDir)) {
        is FileResult.Ok -> println("[FileOps] stat  $testDir: isDir=${r.value.isDirectory}, mode=0${r.value.mode.toString(8)}")
        is FileResult.Err -> println("[FileOps] stat failed: ${r.message}")
    }

    // 创建子文件
    nativeWriteFile("$testDir/a.txt", "file a")
    nativeWriteFile("$testDir/b.txt", "file b")

    // 列出目录内容 (opendir + readdir + closedir)
    when (val r = nativeListDir(testDir)) {
        is FileResult.Ok -> println("[FileOps] ls    $testDir: ${r.value}")
        is FileResult.Err -> println("[FileOps] ls failed: ${r.message}")
    }

    // 删除文件
    when (val r = nativeRemove(testFile)) {
        is FileResult.Ok -> println("[FileOps] rm    $testFile  OK")
        is FileResult.Err -> println("[FileOps] rm failed: ${r.message}")
    }
    nativeRemove("$testDir/a.txt")
    nativeRemove("$testDir/b.txt")

    // 删除目录
    when (val r = nativeRmdir(testDir)) {
        is FileResult.Ok -> println("[FileOps] rmdir $testDir  OK")
        is FileResult.Err -> println("[FileOps] rmdir failed: ${r.message}")
    }

    // 验证目录已删除
    when (nativeStat(testDir)) {
        is FileResult.Ok -> println("[FileOps] stat  $testDir: still exists (unexpected)")
        is FileResult.Err -> println("[FileOps] stat  $testDir: gone (as expected)")
    }
    println()
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
