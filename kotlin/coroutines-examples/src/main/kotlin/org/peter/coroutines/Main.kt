package org.peter.coroutines

import org.peter.coroutines.basics.BasicExamples
import org.peter.coroutines.structured.StructuredExamples
import org.peter.coroutines.flow.FlowExamples
import org.peter.coroutines.channels.ChannelExamples
import org.peter.coroutines.performance.PerformanceExamples
import org.peter.coroutines.realworld.RealWorldExamples
import kotlinx.coroutines.*

fun main() = runBlocking {
    println("=== Kotlin Coroutines Examples ===")
    println()
    
    // Ask user which examples to run
    println("Select examples to run:")
    println("1. Basic Coroutine Examples")
    println("2. Structured Concurrency Examples")
    println("3. Flow Examples")
    println("4. Channel Examples")
    println("5. Performance Examples")
    println("6. Real-world Examples")
    println("7. Run all examples (takes time)")
    println("8. Quick test (run one example from each category)")
    println()
    
    // For now, run quick test
    println("Running quick test...")
    println()
    
    runQuickTest()
    
    println()
    println("=== All tests completed successfully! ===")
    println()
    println("To run specific examples, modify the main() function.")
    println("Each example category has a runAll() method.")
}

suspend fun runQuickTest() {
    println("--- Testing Basic Examples ---")
    BasicExamples.example1SimpleLaunch()
    
    println("--- Testing Structured Concurrency ---")
    StructuredExamples.example1BasicScope()
    
    println("--- Testing Flow Examples ---")
    FlowExamples.example1BasicFlow()
    
    println("--- Testing Channel Examples ---")
    ChannelExamples.example1BasicChannel()
    
    println("--- Testing Performance Examples ---")
    PerformanceExamples.example1DispatcherPerformance()
    
    println("--- Testing Real-world Examples ---")
    RealWorldExamples.example1ConcurrentApiCalls()
}

// Helper functions to run all examples from each category
fun runBasicExamples() = runBlocking {
    BasicExamples.runAll()
}

fun runStructuredExamples() = runBlocking {
    StructuredExamples.runAll()
}

fun runFlowExamples() = runBlocking {
    FlowExamples.runAll()
}

fun runChannelExamples() = runBlocking {
    ChannelExamples.runAll()
}

fun runPerformanceExamples() = runBlocking {
    PerformanceExamples.runAll()
}

fun runRealWorldExamples() = runBlocking {
    RealWorldExamples.runAll()
}