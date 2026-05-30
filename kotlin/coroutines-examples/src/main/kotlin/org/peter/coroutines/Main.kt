package org.peter.coroutines

import org.peter.coroutines.advanced.AdvancedExamples
import org.peter.coroutines.basics.BasicExamples
import org.peter.coroutines.channels.ChannelExamples
import org.peter.coroutines.context.ContextExamples
import org.peter.coroutines.flow.FlowExamples
import org.peter.coroutines.performance.PerformanceExamples
import org.peter.coroutines.realworld.RealWorldExamples
import org.peter.coroutines.structured.StructuredExamples
import org.peter.coroutines.sync.SyncExamples
import kotlinx.coroutines.runBlocking

private val categories = linkedMapOf(
    "basic" to Category("Basic Coroutine Examples", { BasicExamples.runAll() }),
    "structured" to Category("Structured Concurrency Examples", { StructuredExamples.runAll() }),
    "flow" to Category("Flow Examples", { FlowExamples.runAll() }),
    "channel" to Category("Channel Examples", { ChannelExamples.runAll() }),
    "sync" to Category("Sync (Mutex/Semaphore) Examples", { SyncExamples.runAll() }),
    "advanced" to Category("Advanced Examples (Start/Handler)", { AdvancedExamples.runAll() }),
    "context" to Category("Coroutine Context Examples", { ContextExamples.runAll() }),
    "performance" to Category("Performance Examples", { PerformanceExamples.runAll() }),
    "realworld" to Category("Real-world Examples", { RealWorldExamples.runAll() }),
)

private data class Category(val title: String, val run: () -> Unit)

fun main(args: Array<String>) = runBlocking {
    println("=== Kotlin Coroutines Examples ===")
    println()

    when {
        args.isEmpty() -> {
            printMenu()
            println("Running quick test (one example per category)...")
            println()
            runQuickTest()
        }
        args[0] == "all" -> {
            categories.values.forEach { category ->
                println(">>> ${category.title}")
                category.run()
                println()
            }
        }
        args[0] == "list" -> printMenu()
        args[0] in categories -> {
            val category = categories.getValue(args[0])
            println(">>> ${category.title}")
            category.run()
        }
        else -> {
            println("Unknown category: ${args[0]}")
            printMenu()
        }
    }

    println()
    println("=== Done ===")
    println("Usage: ./gradlew :kotlin:coroutines-examples:run --args=\"<category>\"")
    println("Categories: ${categories.keys.joinToString(", ")}, all, list")
}

private fun printMenu() {
    println("Available categories:")
    categories.entries.forEachIndexed { index, (key, category) ->
        println("  ${index + 1}. $key — ${category.title}")
    }
    println("  all — run every category")
    println()
}

suspend fun runQuickTest() {
    println("--- Basic ---")
    BasicExamples.example1SimpleLaunch()

    println("--- Structured ---")
    StructuredExamples.example1BasicScope()

    println("--- Flow ---")
    FlowExamples.example1BasicFlow()

    println("--- Channel ---")
    ChannelExamples.example1BasicChannel()

    println("--- Sync ---")
    SyncExamples.example1MutexCounter()

    println("--- Advanced ---")
    AdvancedExamples.example1LazyStart()

    println("--- Context ---")
    ContextExamples.example1JobHierarchy()

    println("--- Performance ---")
    PerformanceExamples.example1DispatcherPerformance()

    println("--- Real-world ---")
    RealWorldExamples.example1ConcurrentApiCalls()
}

fun runBasicExamples() = BasicExamples.runAll()
fun runStructuredExamples() = StructuredExamples.runAll()
fun runFlowExamples() = FlowExamples.runAll()
fun runChannelExamples() = ChannelExamples.runAll()
fun runSyncExamples() = SyncExamples.runAll()
fun runAdvancedExamples() = AdvancedExamples.runAll()
fun runContextExamples() = ContextExamples.runAll()
fun runPerformanceExamples() = PerformanceExamples.runAll()
fun runRealWorldExamples() = RealWorldExamples.runAll()
