package org.peter.coroutines.basics

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Basic Coroutine Examples
 * 
 * This file demonstrates fundamental coroutine concepts:
 * 1. Launching coroutines with launch()
 * 2. Using async/await for concurrent computations
 * 3. Understanding suspend functions
 * 4. Coroutine contexts and dispatchers
 * 5. Exception handling in coroutines
 */

object BasicExamples {
    
    /**
     * Example 1: Simple coroutine launch
     * Demonstrates the basic launch() function
     */
    fun example1SimpleLaunch() = runBlocking {
        println("Example 1: Simple Coroutine Launch")
        println("Main thread: ${Thread.currentThread().name}")
        
        launch {
            // This code runs in a coroutine
            delay(1000L) // Non-blocking delay
            println("Coroutine thread: ${Thread.currentThread().name}")
            println("Hello from coroutine!")
        }
        
        println("Hello from main!")
        delay(2000L) // Wait for coroutine to complete
        println("---")
    }
    
    /**
     * Example 2: Multiple coroutines
     * Shows how multiple coroutines can run concurrently
     */
    fun example2MultipleCoroutines() = runBlocking {
        println("Example 2: Multiple Coroutines")
        
        repeat(5) { i ->
            launch {
                delay((i * 100).toLong())
                println("Coroutine $i: ${Thread.currentThread().name}")
            }
        }
        
        delay(1000L)
        println("---")
    }
    
    /**
     * Example 3: Async/Await for concurrent computations
     * Demonstrates structured concurrency with async/await
     */
    fun example3AsyncAwait() = runBlocking {
        println("Example 3: Async/Await Pattern")
        
        val time = measureTimeMillis {
            val deferred1 = async { computeSomething(1000L) }
            val deferred2 = async { computeSomething(1500L) }
            
            val result1 = deferred1.await()
            val result2 = deferred2.await()
            
            println("Result: ${result1 + result2}")
        }
        
        println("Completed in $time ms")
        println("---")
    }
    
    /**
     * Example 4: Sequential vs Concurrent
     * Compares sequential execution with concurrent execution
     */
    fun example4SequentialVsConcurrent() = runBlocking {
        println("Example 4: Sequential vs Concurrent")
        
        // Sequential execution
        val sequentialTime = measureTimeMillis {
            val one = computeSomething(1000L)
            val two = computeSomething(1000L)
            println("Sequential result: ${one + two}")
        }
        println("Sequential time: ${sequentialTime}ms")
        
        // Concurrent execution
        val concurrentTime = measureTimeMillis {
            val one = async { computeSomething(1000L) }
            val two = async { computeSomething(1000L) }
            println("Concurrent result: ${one.await() + two.await()}")
        }
        println("Concurrent time: ${concurrentTime}ms")
        println("---")
    }
    
    /**
     * Example 5: Coroutine Contexts and Dispatchers
     * Shows different dispatchers for coroutines
     */
    fun example5Dispatchers() = runBlocking {
        println("Example 5: Coroutine Dispatchers")
        
        launch { // Uses parent's context (runBlocking)
            println("Default: ${Thread.currentThread().name}")
        }
        
        launch(Dispatchers.Default) {
            println("Default dispatcher: ${Thread.currentThread().name}")
        }
        
        launch(Dispatchers.IO) {
            println("IO dispatcher: ${Thread.currentThread().name}")
        }
        
        launch(newSingleThreadContext("MyThread")) {
            println("Custom thread: ${Thread.currentThread().name}")
        }
        
        delay(1000L)
        println("---")
    }
    
    /**
     * Example 6: Suspend Functions
     * Demonstrates how to create and use suspend functions
     */
    fun example6SuspendFunctions() = runBlocking {
        println("Example 6: Suspend Functions")
        
        val result = fetchData()
        println("Fetched data: $result")
        
        val processed = processData(result)
        println("Processed data: $processed")
        println("---")
    }
    
    /**
     * Example 7: Exception Handling
     * Shows different ways to handle exceptions in coroutines
     */
    fun example7ExceptionHandling() = runBlocking {
        println("Example 7: Exception Handling")
        
        // Try-catch in coroutine
        val job1 = launch {
            try {
                throw RuntimeException("Error in coroutine!")
            } catch (e: Exception) {
                println("Caught exception: ${e.message}")
            }
        }
        job1.join()
        
        // Exception propagation with async
        val deferred = async {
            throw RuntimeException("Error in async!")
        }
        
        try {
            deferred.await()
        } catch (e: Exception) {
            println("Caught async exception: ${e.message}")
        }
        
        println("---")
    }
    
    /**
     * Example 8: Coroutine Cancellation
     * Demonstrates how to cancel coroutines
     */
    fun example8Cancellation() = runBlocking {
        println("Example 8: Coroutine Cancellation")
        
        val job = launch {
            repeat(10) { i ->
                println("Job: I'm working $i...")
                delay(500L)
                
                // Check for cancellation
                if (isActive) {
                    // Continue working
                } else {
                    println("Job cancelled, cleaning up...")
                    return@launch
                }
            }
        }
        
        delay(1300L) // Let it work for a bit
        println("Main: I'm tired of waiting!")
        job.cancel() // Cancel the job
        job.join() // Wait for cancellation to complete
        println("Main: Now I can quit.")
        println("---")
    }
    
    /**
     * Example 9: Timeout
     * Shows how to use withTimeout for time-limited operations
     */
    fun example9Timeout() = runBlocking {
        println("Example 9: Timeout")
        
        try {
            withTimeout(1300L) {
                repeat(5) { i ->
                    println("I'm working $i...")
                    delay(500L)
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("Operation timed out!")
        }
        
        println("---")
    }
    
    /**
     * Example 10: Coroutine Scope
     * Demonstrates coroutine scope and structured concurrency
     */
    fun example10CoroutineScope() = runBlocking {
        println("Example 10: Coroutine Scope")
        
        coroutineScope {
            launch {
                delay(1000L)
                println("Task from coroutine scope")
            }
            
            delay(500L)
            println("Task from coroutine scope - parent")
        }
        
        println("Coroutine scope completed")
        println("---")
    }
    
    // Helper functions
    
    private suspend fun computeSomething(delayMillis: Long): Int {
        delay(delayMillis)
        return 42
    }
    
    private suspend fun fetchData(): String {
        delay(500L) // Simulate network delay
        return "Data from server"
    }
    
    private suspend fun processData(data: String): String {
        delay(300L) // Simulate processing time
        return "Processed: $data"
    }
    
    /**
     * Run all basic examples
     */
    fun runAll() = runBlocking {
        println("=== Running All Basic Examples ===")
        println()
        
        example1SimpleLaunch()
        example2MultipleCoroutines()
        example3AsyncAwait()
        example4SequentialVsConcurrent()
        example5Dispatchers()
        example6SuspendFunctions()
        example7ExceptionHandling()
        example8Cancellation()
        example9Timeout()
        example10CoroutineScope()
        
        println("=== All Basic Examples Completed ===")
    }
}