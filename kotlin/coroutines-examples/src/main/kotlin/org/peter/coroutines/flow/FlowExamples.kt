package org.peter.coroutines.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis
import kotlin.random.Random

/**
 * Flow Examples
 * 
 * This file demonstrates Kotlin Flow - cold asynchronous streams:
 * 1. Basic Flow creation and consumption
 * 2. Flow operators (map, filter, transform, etc.)
 * 3. Cold vs Hot flows (StateFlow, SharedFlow)
 * 4. Flow exception handling
 * 5. Flow context and backpressure
 */

object FlowExamples {
    
    /**
     * Example 1: Basic Flow
     * Demonstrates creating and collecting a simple flow
     */
    fun example1BasicFlow() = runBlocking {
        println("Example 1: Basic Flow")
        
        simpleFlow().collect { value ->
            println("Collected: $value")
        }
        
        println("---")
    }
    
    /**
     * Example 2: Flow Operators
     * Shows various flow operators (map, filter, take, etc.)
     */
    fun example2FlowOperators() = runBlocking {
        println("Example 2: Flow Operators")
        
        flow {
            for (i in 1..10) {
                emit(i)
                delay(100L)
            }
        }
        .filter { it % 2 == 0 } // Keep only even numbers
        .map { it * 2 } // Double each value
        .take(3) // Take only first 3 values
        .collect { value ->
            println("Processed: $value")
        }
        
        println("---")
    }
    
    /**
     * Example 3: Flow Context
     * Demonstrates flowOn operator for context switching
     */
    fun example3FlowContext() = runBlocking {
        println("Example 3: Flow Context")
        
        println("Main thread: ${Thread.currentThread().name}")
        
        flow {
            for (i in 1..3) {
                println("Emitting $i on ${Thread.currentThread().name}")
                emit(i)
                delay(100L)
            }
        }
        .flowOn(Dispatchers.IO) // Switch to IO context for emission
        .collect { value ->
            println("Collecting $value on ${Thread.currentThread().name}")
        }
        
        println("---")
    }
    
    /**
     * Example 4: Flow Exception Handling
     * Shows how to handle exceptions in flows
     */
    fun example4FlowExceptionHandling() = runBlocking {
        println("Example 4: Flow Exception Handling")
        
        // Using catch operator
        flow {
            for (i in 1..3) {
                if (i == 2) {
                    throw RuntimeException("Error on $i")
                }
                emit(i)
            }
        }
        .catch { e ->
            println("Caught exception: ${e.message}")
            emit(-1) // Emit fallback value
        }
        .collect { value ->
            println("Collected: $value")
        }
        
        println("---")
    }
    
    /**
     * Example 5: Flow Completion
     * Demonstrates onCompletion operator
     */
    fun example5FlowCompletion() = runBlocking {
        println("Example 5: Flow Completion")
        
        flow {
            for (i in 1..3) {
                emit(i)
                delay(100L)
            }
        }
        .onCompletion { cause ->
            if (cause != null) {
                println("Flow completed with exception: ${cause.message}")
            } else {
                println("Flow completed successfully")
            }
        }
        .collect { value ->
            println("Collected: $value")
        }
        
        println("---")
    }
    
    /**
     * Example 6: StateFlow (Hot Flow)
     * Demonstrates StateFlow - a hot observable state holder
     */
    fun example6StateFlow() = runBlocking {
        println("Example 6: StateFlow")
        
        val stateFlow = MutableStateFlow(0)
        
        // Collector 1
        val job1 = launch {
            stateFlow.collect { value ->
                println("Collector 1: $value")
            }
        }
        
        // Update state
        delay(100L)
        stateFlow.value = 1
        delay(100L)
        stateFlow.value = 2
        
        // Collector 2 (late subscriber gets current value)
        val job2 = launch {
            stateFlow.collect { value ->
                println("Collector 2: $value")
            }
        }
        
        delay(100L)
        stateFlow.value = 3
        
        delay(100L)
        job1.cancel()
        job2.cancel()
        
        println("---")
    }
    
    /**
     * Example 7: SharedFlow (Hot Flow)
     * Demonstrates SharedFlow - a hot broadcast stream
     */
    fun example7SharedFlow() = runBlocking {
        println("Example 7: SharedFlow")
        
        val sharedFlow = MutableSharedFlow<Int>(
            replay = 2, // Replay last 2 values to new subscribers
            extraBufferCapacity = 10
        )
        
        // Emit values in background
        launch {
            repeat(5) { i ->
                sharedFlow.emit(i)
                delay(100L)
            }
        }
        
        // Wait a bit before collecting
        delay(250L)
        
        // Collector (gets replayed values + new ones)
        sharedFlow.collect { value ->
            println("Collected: $value")
        }
        
        delay(500L)
        println("---")
    }
    
    /**
     * Example 8: Flow Backpressure
     * Demonstrates handling backpressure with buffer and conflate
     */
    fun example8FlowBackpressure() = runBlocking {
        println("Example 8: Flow Backpressure")
        
        // Fast producer, slow consumer
        val time = measureTimeMillis {
            flow {
                for (i in 1..10) {
                    emit(i)
                    delay(50L) // Fast producer
                }
            }
            .buffer() // Buffer emissions to prevent blocking
            .collect { value ->
                delay(100L) // Slow consumer
                println("Processed: $value")
            }
        }
        
        println("Completed in $time ms")
        println("---")
    }
    
    /**
     * Example 9: Flow Combine and Zip
     * Demonstrates combining multiple flows
     */
    fun example9FlowCombine() = runBlocking {
        println("Example 9: Flow Combine and Zip")
        
        val flow1 = flowOf(1, 2, 3).onEach { delay(100L) }
        val flow2 = flowOf("A", "B", "C").onEach { delay(150L) }
        
        println("Zip (pairs):")
        flow1.zip(flow2) { a, b -> "$a -> $b" }
            .collect { println(it) }
        
        println("\nCombine (latest):")
        flow1.combine(flow2) { a, b -> "$a + $b" }
            .collect { println(it) }
        
        println("---")
    }
    
    /**
     * Example 10: Flow Transform
     * Demonstrates advanced flow transformations
     */
    fun example10FlowTransform() = runBlocking {
        println("Example 10: Flow Transform")
        
        flow {
            emit(1)
            emit(2)
            emit(3)
        }
        .transform { value ->
            // Can emit multiple values for each input
            emit("Number: $value")
            emit("Square: ${value * value}")
            if (value % 2 == 0) {
                emit("Even: $value")
            }
        }
        .collect { value ->
            println(value)
        }
        
        println("---")
    }
    
    /**
     * Example 11: Flow FlatMap
     * Demonstrates flatMap operators for nested flows
     */
    fun example11FlowFlatMap() = runBlocking {
        println("Example 11: Flow FlatMap")
        
        flowOf(1, 2, 3)
            .flatMapConcat { value ->
                flow {
                    emit("A$value")
                    delay(100L)
                    emit("B$value")
                }
            }
            .collect { value ->
                println(value)
            }
        
        println("---")
    }
    
    /**
     * Example 12: Flow Retry
     * Demonstrates retrying failed flows
     */
    fun example12FlowRetry() = runBlocking {
        println("Example 12: Flow Retry")
        
        var attempt = 0
        
        flow {
            attempt++
            println("Attempt $attempt")
            
            if (attempt < 3) {
                throw RuntimeException("Failed on attempt $attempt")
            }
            
            emit("Success!")
        }
        .retry(2) { cause ->
            println("Retrying due to: ${cause.message}")
            delay(100L)
            true // Retry
        }
        .collect { value ->
            println("Collected: $value")
        }
        
        println("---")
    }
    
    /**
     * Example 13: Flow Debounce and Sample
     * Demonstrates rate-limiting operators
     */
    fun example13FlowDebounce() = runBlocking {
        println("Example 13: Flow Debounce and Sample")
        
        // Simulate user typing
        val userInput = flow {
            emit("H")
            delay(50L)
            emit("He")
            delay(50L)
            emit("Hel")
            delay(200L) // Longer pause
            emit("Hell")
            delay(50L)
            emit("Hello")
        }
        
        println("Debounced (waits for pause):")
        userInput
            .debounce(100L) // Wait 100ms after last emission
            .collect { value ->
                println("Debounced: $value")
            }
        
        println("\nSampled (takes periodic samples):")
        userInput
            .sample(100L) // Take sample every 100ms
            .collect { value ->
                println("Sampled: $value")
            }
        
        println("---")
    }
    
    /**
     * Example 14: Flow with Live Data Simulation
     * Demonstrates real-time data streaming
     */
    fun example14LiveDataFlow() = runBlocking {
        println("Example 14: Live Data Flow")
        
        val sensorData = flow {
            var temperature = 20.0
            repeat(10) {
                temperature += (Math.random() * 2 - 1) // Random change
                emit("Temperature: ${"%.1f".format(temperature)}°C")
                delay(200L)
            }
        }
        
        sensorData
            .distinctUntilChanged() // Only emit when value changes
            .collect { reading ->
                println(reading)
            }
        
        println("---")
    }
    
    /**
     * Example 15: Complex Flow Pipeline
     * Demonstrates a complete flow processing pipeline
     */
    fun example15ComplexPipeline() = runBlocking {
        println("Example 15: Complex Flow Pipeline")
        
        dataProcessingPipeline()
            .catch { e ->
                println("Pipeline error: ${e.message}")
                emptyFlow()
            }
            .collect { result ->
                println("Final result: $result")
            }
        
        println("---")
    }
    
    // Helper functions
    
    private fun simpleFlow(): Flow<Int> = flow {
        println("Flow started")
        for (i in 1..3) {
            delay(100L)
            emit(i)
        }
    }
    
    private fun dataProcessingPipeline(): Flow<String> = flow {
        // Simulate data source
        for (i in 1..5) {
            emit(i)
            delay(50L)
        }
    }
    .filter { it > 0 }
    .map { it * 2 }
    .transform { value ->
        // Complex transformation
        emit("Processing: $value")
        delay(100L)
        emit("Result: ${value + 10}")
    }
    .onEach { println("Intermediate: $it") }
    .map { it.uppercase() }
    
    /**
     * Run all flow examples
     */
    fun runAll() = runBlocking {
        println("=== Running All Flow Examples ===")
        println()
        
        example1BasicFlow()
        example2FlowOperators()
        example3FlowContext()
        example4FlowExceptionHandling()
        example5FlowCompletion()
        example6StateFlow()
        example7SharedFlow()
        example8FlowBackpressure()
        example9FlowCombine()
        example10FlowTransform()
        example11FlowFlatMap()
        example12FlowRetry()
        example13FlowDebounce()
        example14LiveDataFlow()
        example15ComplexPipeline()
        
        println("=== All Flow Examples Completed ===")
    }
}