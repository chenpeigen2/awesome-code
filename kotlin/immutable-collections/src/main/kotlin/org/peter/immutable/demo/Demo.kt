package org.peter.immutable.demo

/** 打印一个带标题的演示段落。 */
fun section(title: String, block: () -> Unit) {
    println()
    println("=".repeat(78))
    println("▶ $title")
    println("=".repeat(78))
    block()
}

/** 对齐打印一行“结论 + 值”。 */
fun line(label: String, value: Any? = "") {
    println("  ${label.padEnd(42)} $value")
}

/** 让泛型集合的类型名更好读一些。 */
fun typeName(value: Any): String = value::class.java.simpleName

/** 测一段代码的耗时（毫秒，保留两位小数）。 */
inline fun timed(block: () -> Unit): Double {
    val start = System.nanoTime()
    block()
    return (System.nanoTime() - start) / 1_000_000.0
}
