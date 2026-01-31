package org.peter.coroutines.performance

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlin.system.measureTimeMillis
import kotlin.system.measureNanoTime

/**
 * Performance Optimization Examples
 * 
 * This file demonstrates performance optimization techniques for coroutines:
 * 1. Choosing the right dispatcher
 * 2. Avoiding common performance pitfalls
 * 3. Optimizing Flow performance
 * 4. Memory optimization techniques
 * 5. Benchmarking and profiling
 */

object PerformanceExamples {
    
    /**
     * Example 1: Dispatcher Performance Comparison
     * Compares performance of different dispatchers
     */
    fun example1DispatcherPerformance() = runBlocking {
        println("Example 1: Dispatcher Performance Comparison")
        
        val iterations = 1000
        val workPerIteration = 1000 // Simple computation
        
        fun performWork(): Int {
            var sum = 0
            for (i in 1..workPerIteration) {
                sum += i
            }
            return sum
        }
        
        // Test different dispatchers
        val dispatchers = listOf(
            "Default" to Dispatchers.Default,
            "IO" to Dispatchers.IO,
            "Unconfined" to Dispatchers.Unconfined
        )
        
        for ((name, dispatcher) in dispatchers) {
            val time = measureTimeMillis {
                val jobs = List(iterations) {
                    async(dispatcher) {
                        performWork()
                    }
                }
                jobs.awaitAll()
            }
            println("$name dispatcher: $time ms for $iterations tasks")
        }
        
        println("---")
    }
    
    /**
     * Example 2: Structured Concurrency vs GlobalScope
     * Shows performance implications of structured concurrency
     */
    fun example2StructuredVsGlobal() = runBlocking {
        println("Example 2: Structured Concurrency vs GlobalScope")
        
        val taskCount = 100
        
        // Using structured concurrency
        val structuredTime = measureTimeMillis {
            coroutineScope {
                val jobs = List(taskCount) {
                    launch {
                        delay(10L) // Simulate work
                    }
                }
                jobs.forEach { it.join() }
            }
        }
        println("Structured concurrency: $structuredTime ms")
        
        // Using GlobalScope (not recommended)
        val globalTime = measureTimeMillis {
            val jobs = List(taskCount) {
                GlobalScope.launch {
                    delay(10L)
                }
            }
            jobs.forEach { it.join() }
        }
        println("GlobalScope: $globalTime ms")
        
        println("Note: Structured concurrency provides better resource management")
        println("---")
    }
    
    /**
     * Example 3: Flow Performance Optimization
     * Demonstrates Flow performance techniques
     */
    fun example3FlowPerformance() = runBlocking {
        println("Example 3: Flow Performance Optimization")
        
        val dataSize = 10000
        
        // Naive implementation
        val naiveTime = measureTimeMillis {
            flow {
                for (i in 1..dataSize) {
                    emit(i)
                }
            }
            .map { it * 2 }
            .filter { it % 3 == 0 }
            .collect { /* consume */ }
        }
        println("Naive flow: $naiveTime ms")
        
        // Optimized with buffer
        val bufferedTime = measureTimeMillis {
            flow {
                for (i in 1..dataSize) {
                    emit(i)
                }
            }
            .buffer() // Add buffer for better throughput
            .map { it * 2 }
            .filter { it % 3 == 0 }
            .collect { /* consume */ }
        }
        println("Buffered flow: $bufferedTime ms")
        
        // Using flowOn for context switching
        val flowOnTime = measureTimeMillis {
            flow {
                for (i in 1..dataSize) {
                    emit(i)
                }
            }
            .flowOn(Dispatchers.Default) // Process in background
            .collect { /* consume */ }
        }
        println("Flow with flowOn: $flowOnTime ms")
        
        println("---")
    }
    
    /**
     * Example 4: Avoiding Blocking in Coroutines
     * Shows the cost of blocking calls in coroutines
     */
    fun example4AvoidBlocking() = runBlocking {
        println("Example 4: Avoiding Blocking Calls")
        
        val iterations = 100
        
        // Blocking call (BAD)
        val blockingTime = measureTimeMillis {
            val jobs = List(iterations) {
                launch {
                    Thread.sleep(1) // BLOCKING - don't do this!
                }
            }
            jobs.forEach { it.join() }
        }
        println("With blocking sleep: $blockingTime ms")
        
        // Non-blocking call (GOOD)
        val nonBlockingTime = measureTimeMillis {
            val jobs = List(iterations) {
                launch {
                    delay(1) // NON-BLOCKING - use this!
                }
            }
            jobs.forEach { it.join() }
        }
        println("With non-blocking delay: $nonBlockingTime ms")
        
        println("Difference: ${blockingTime - nonBlockingTime} ms")
        println("---")
    }
    
    /**
     * Example 5: Memory Optimization with Channels
     * Demonstrates memory-efficient channel usage
     */
    fun example5ChannelMemory() = runBlocking {
        println("Example 5: Channel Memory Optimization")
        
        val dataSize = 100000
        
        // Using list (memory intensive)
        val listMemory = measureMemoryUsage {
            val list = mutableListOf<Int>()
            coroutineScope {
                launch {
                    repeat(dataSize) { i ->
                        list.add(i)
                        delay(0) // Yield
                    }
                }
            }
            list.size
        }
        println("List approach: ${listMemory.first} ms, memory intensive")
        
        // Using channel (streaming, memory efficient)
        val channelMemory = measureMemoryUsage {
            val channel = Channel<Int>(capacity = Channel.UNLIMITED)
            
            coroutineScope {
                // Producer
                launch {
                    repeat(dataSize) { i ->
                        channel.send(i)
                    }
                    channel.close()
                }
                
                // Consumer
                launch {
                    var count = 0
                    for (value in channel) {
                        count++
                        // Process value immediately
                    }
                    count
                }.join()
            }
        }
        println("Channel approach: ${channelMemory.first} ms, memory efficient")
        
        println("---")
    }
    
    /**
     * Example 6: Coroutine Context Switching Overhead
     * Measures context switching costs
     */
    fun example6ContextSwitching() = runBlocking {
        println("Example 6: Context Switching Overhead")
        
        val switchCount = 1000
        
        // With frequent context switching
        val withSwitching = measureTimeMillis {
            coroutineScope {
                repeat(switchCount) { i ->
                    withContext(Dispatchers.IO) {
                        // Do minimal work
                    }
                }
            }
        }
        println("With context switching: $withSwitching ms")
        
        // Without context switching
        val withoutSwitching = measureTimeMillis {
            coroutineScope {
                repeat(switchCount) { i ->
                    // Do minimal work in same context
                }
            }
        }
        println("Without context switching: $withoutSwitching ms")
        
        val overhead = withSwitching - withoutSwitching
        println("Context switching overhead: $overhead ms (${
            String.format("%.2f", overhead.toDouble() / switchCount)
        } ms per switch)")
        
        println("---")
    }
    
    /**
     * Example 7: Flow Backpressure Strategies
     * Compares different backpressure handling approaches
     */
    fun example7FlowBackpressure() = runBlocking {
        println("Example 7: Flow Backpressure Strategies")
        
        val itemCount = 1000
        
        // Fast producer, slow consumer scenario
        fun fastProducer() = flow {
            repeat(itemCount) { i ->
                emit(i)
                // No delay - very fast
            }
        }
        
        // Test different strategies
        val strategies = listOf(
            "No buffer" to { flow: Flow<Int> -> flow },
            "Buffer 100" to { flow: Flow<Int> -> flow.buffer(100) },
            "Conflate" to { flow: Flow<Int> -> flow.conflate() }
        )
        
        for ((name, strategy) in strategies) {
            val time = measureTimeMillis {
                strategy(fastProducer())
                    .collect { value ->
                        delay(1L) // Slow consumer
                        // Consume value
                    }
            }
            println("$name: $time ms")
        }
        
        println("---")
    }
    
    /**
     * Example 8: Async Initialization Pattern
     * Demonstrates efficient async initialization
     */
    fun example8AsyncInitialization() = runBlocking {
        println("Example 8: Async Initialization Pattern")
        
        // Lazy async initialization
        class ResourceManager {
            private val resource by lazy {
                async(start = CoroutineStart.LAZY) {
                    println("Initializing expensive resource...")
                    delay(1000L) // Simulate expensive initialization
                    "Expensive Resource"
                }
            }
            
            suspend fun getResource(): String {
                return resource.await()
            }
        }
        
        val manager = ResourceManager()
        
        // Multiple calls only initialize once
        val time = measureTimeMillis {
            coroutineScope {
                val jobs = List(5) {
                    launch {
                        val resource = manager.getResource()
                        println("Got resource: $resource")
                    }
                }
                jobs.forEach { it.join() }
            }
        }
        
        println("Total time with lazy initialization: $time ms")
        println("(Resource initialized only once)")
        println("---")
    }
    
    /**
     * Example 9: Batch Processing Optimization
     * Shows batching for better performance
     */
    fun example9BatchProcessing() = runBlocking {
        println("Example 9: Batch Processing Optimization")
        
        val totalItems = 1000
        val batchSize = 100
        
        // Process items one by one (slow)
        val sequentialTime = measureTimeMillis {
            repeat(totalItems) { i ->
                processItem(i)
            }
        }
        println("Sequential processing: $sequentialTime ms")
        
        // Process in batches (faster)
        val batchTime = measureTimeMillis {
            val batches = totalItems / batchSize
            
            coroutineScope {
                repeat(batches) { batch ->
                    launch {
                        val start = batch * batchSize
                        val end = minOf(start + batchSize, totalItems)
                        
                        for (i in start until end) {
                            processItem(i)
                        }
                    }
                }
            }
        }
        println("Batch processing ($batchSize per batch): $batchTime ms")
        
        println("Improvement: ${sequentialTime - batchTime} ms (${ 
            String.format("%.1f", (sequentialTime.toDouble() / batchTime) * 100 - 100)
        }% faster)")
        println("---")
    }
    
    /**
     * Example 10: Coroutine Pool Sizing
     * Demonstrates optimal coroutine pool sizing
     */
    fun example10PoolSizing() = runBlocking {
        println("Example 10: Coroutine Pool Sizing")
        
        val taskCount = 100
        val workPerTask = 1000000 // CPU-intensive work
        
        fun cpuIntensiveWork(): Int {
            var result = 0
            for (i in 1..workPerTask) {
                result += i % 7
            }
            return result
        }
        
        // Test different levels of concurrency
        val concurrencyLevels = listOf(1, 2, 4, 8, 16, 32)
        
        for (level in concurrencyLevels) {
            val time = measureTimeMillis {
                val semaphore = Semaphore(level)
                
                coroutineScope {
                    val jobs = List(taskCount) {
                        launch(Dispatchers.Default) {
                            semaphore.withPermit {
                                cpuIntensiveWork()
                            }
                        }
                    }
                    jobs.awaitAll()
                }
            }
            println("Concurrency level $level: $time ms")
        }
        
        println("Note: Optimal level depends on CPU cores and task type")
        println("---")
    }
    
    /**
     * Example 11: Flow Cancellation Optimization
     * Shows efficient flow cancellation patterns
     */
    fun example11FlowCancellation() = runBlocking {
        println("Example 11: Flow Cancellation Optimization")
        
        // Flow that's expensive to cancel
        val expensiveFlow = flow {
            try {
                repeat(100) { i ->
                    emit(i)
                    delay(100L)
                }
            } finally {
                // Expensive cleanup
                delay(500L)
                println("Expensive cleanup completed")
            }
        }
        
        // Cancel after short time
        val time = measureTimeMillis {
            val job = launch {
                expensiveFlow.collect { value ->
                    if (value == 3) {
                        println("Cancelling at value $value")
                        cancel() // Cancel coroutine
                    }
                }
            }
            job.join()
        }
        
        println("Cancellation took: $time ms")
        println("---")
    }
    
    /**
     * Example 12: Memory Leak Prevention
     * Demonstrates avoiding common memory leaks
     */
    fun example12MemoryLeakPrevention() = runBlocking {
        println("Example 12: Memory Leak Prevention")
        
        class EventListener {
            private var callback: (() -> Unit)? = null
            
            fun setCallback(callback: () -> Unit) {
                this.callback = callback
            }
            
            fun clear() {
                callback = null
            }
        }
        
        // Potential memory leak
        val listener = EventListener()
        var resource: String? = "Large Resource"
        
        // BAD: Capturing reference in callback
        listener.setCallback {
            println("Callback accessing: $resource")
        }
        
        // Even after clearing reference, callback still holds it
        resource = null
        
        // GOOD: Use weak references or clear callbacks
        listener.clear()
        
        println("Always clear callbacks and references in coroutines")
        println("---")
    }
    
    /**
     * Example 13: Profiling Coroutines
     * Shows how to profile coroutine performance
     */
    fun example13Profiling() = runBlocking {
        println("Example 13: Profiling Coroutines")
        
        // Simple profiling helper
        suspend fun <T> profile(name: String, block: suspend () -> T): T {
            val startTime = System.nanoTime()
            val startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            
            val result = block()
            
            val endTime = System.nanoTime()
            val endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            
            val duration = (endTime - startTime) / 1_000_000.0 // ms
            val memoryUsed = (endMemory - startMemory) / 1024.0 / 1024.0 // MB
            
            println("$name: ${String.format("%.2f", duration)} ms, " +
                   "${String.format("%.2f", memoryUsed)} MB memory")
            
            return result
        }
        
        // Profile different operations
        profile("Coroutine launch") {
            coroutineScope {
                val jobs = List(100) {
                    launch {
                        delay(1L)
                    }
                }
                jobs.forEach { it.join() }
            }
        }
        
        profile("Flow processing") {
            flow {
                repeat(1000) { i ->
                    emit(i)
                }
            }
            .map { it * 2 }
            .collect { /* consume */ }
        }
        
        println("---")
    }
    
    /**
     * Example 14: Optimizing Exception Handling
     * Shows performance impact of exception handling
     */
    fun example14ExceptionHandling() = runBlocking {
        println("Example 14: Exception Handling Performance")
        
        val iterations = 10000
        
        // With exception (slow)
        val exceptionTime = measureTimeMillis {
            repeat(iterations) {
                try {
                    throw RuntimeException("Test")
                } catch (e: Exception) {
                    // Handle
                }
            }
        }
        println("With exception: $exceptionTime ms")
        
        // Without exception (fast)
        val noExceptionTime = measureTimeMillis {
            repeat(iterations) {
                // Normal flow
            }
        }
        println("Without exception: $noExceptionTime ms")
        
        println("Exception overhead: ${exceptionTime - noExceptionTime} ms")
        println("Avoid exceptions in performance-critical loops")
        println("---")
    }
    
    /**
     * Example 15: Complete Performance Checklist
     * Provides a comprehensive performance checklist
     */
    fun example15PerformanceChecklist() {
        println("Example 15: Coroutine Performance Checklist")
        println()
        
        val checklist = listOf(
            "✅ Use appropriate dispatchers (Default for CPU, IO for I/O)",
            "✅ Prefer structured concurrency over GlobalScope",
            "✅ Use delay() instead of Thread.sleep()",
            "✅ Add buffers to Flows for better throughput",
            "✅ Use flowOn() for expensive transformations",
            "✅ Limit concurrent coroutines with semaphores",
            "✅ Cancel coroutines when no longer needed",
            "✅ Clear references in callbacks to prevent leaks",
            "✅ Batch process items when possible",
            "✅ Profile and measure before optimizing",
            "✅ Avoid exceptions in performance-critical code",
            "✅ Use lazy initialization for expensive resources",
            "✅ Consider context switching overhead",
            "✅ Choose right channel type for use case",
            "✅ Monitor memory usage with large data sets"
        )
        
        checklist.forEach { println(it) }
        println("---")
    }
    
    // Helper functions
    
    private suspend fun processItem(item: Int) {
        // Simulate some processing
        delay(1L)
    }
    
    private fun measureMemoryUsage(block: () -> Unit): Pair<Long, String> {
        val runtime = Runtime.getRuntime()
        
        // Run GC to get clean measurement
        runtime.gc()
        val startMemory = runtime.totalMemory() - runtime.freeMemory()
        
        val startTime = System.currentTimeMillis()
        block()
        val endTime = System.currentTimeMillis()
        
        val endMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = endMemory - startMemory
        
        return Pair(endTime - startTime, formatMemory(memoryUsed))
    }
    
    private fun formatMemory(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1) {
            String.format("%.2f MB", mb)
        } else {
            String.format("%.2f KB", kb)
        }
    }
    
    /**
     * Run all performance examples
     */
    fun runAll() = runBlocking {
        println("=== Running All Performance Examples ===")
        println()
        
        example1DispatcherPerformance()
        example2StructuredVsGlobal()
        example3FlowPerformance()
        example4AvoidBlocking()
        example5ChannelMemory()
        example6ContextSwitching()
        example7FlowBackpressure()
        example8AsyncInitialization()
        example9BatchProcessing()
        example10PoolSizing()
        example11FlowCancellation()
        example12MemoryLeakPrevention()
        example13Profiling()
        example14ExceptionHandling()
        example15PerformanceChecklist()
        
        println("=== All Performance Examples Completed ===")
    }
}