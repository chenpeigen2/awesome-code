package org.peter.coroutines.channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Semaphore
import kotlin.system.measureTimeMillis

/**
 * Channel and Actor Examples
 * 
 * This file demonstrates Kotlin Channels and Actors:
 * 1. Basic Channel operations (send/receive)
 * 2. Different channel types (Rendezvous, Buffered, Conflated, Unlimited)
 * 3. Producer-consumer patterns
 * 4. Channel pipelines and fan-out/fan-in
 * 5. Actor pattern implementation
 */

object ChannelExamples {
    
    /**
     * Example 1: Basic Channel
     * Demonstrates simple send and receive operations
     */
    fun example1BasicChannel() = runBlocking {
        println("Example 1: Basic Channel")
        
        val channel = Channel<Int>()
        
        launch {
            // Producer
            for (x in 1..5) {
                println("Sending $x")
                channel.send(x)
                delay(100L)
            }
            channel.close() // Important: close when done
        }
        
        launch {
            // Consumer
            for (y in channel) {
                println("Received $y")
                delay(150L)
            }
            println("Channel closed")
        }.join()
        
        println("---")
    }
    
    /**
     * Example 2: Rendezvous Channel (default)
     * Demonstrates unbuffered channel (sender suspends until receiver is ready)
     */
    fun example2RendezvousChannel() = runBlocking {
        println("Example 2: Rendezvous Channel")
        
        val channel = Channel<String>() // Default is Rendezvous
        
        launch {
            repeat(3) { i ->
                println("Producer: Sending message $i")
                channel.send("Message $i") // Suspends until receiver is ready
                println("Producer: Message $i sent")
            }
            channel.close()
        }
        
        launch {
            delay(500L) // Delay to demonstrate suspension
            for (msg in channel) {
                println("Consumer: Received $msg")
                delay(300L)
            }
        }.join()
        
        println("---")
    }
    
    /**
     * Example 3: Buffered Channel
     * Demonstrates channel with buffer capacity
     */
    fun example3BufferedChannel() = runBlocking {
        println("Example 3: Buffered Channel")
        
        val channel = Channel<Int>(capacity = 3) // Buffer of size 3
        
        launch {
            repeat(5) { i ->
                println("Producer: Sending $i")
                channel.send(i) // Won't suspend until buffer is full
                println("Producer: $i sent (buffer has space)")
            }
            channel.close()
        }
        
        launch {
            delay(1000L) // Let producer fill buffer
            for (value in channel) {
                println("Consumer: Received $value")
                delay(500L) // Slow consumer
            }
        }.join()
        
        println("---")
    }
    
    /**
     * Example 4: Conflated Channel
     * Demonstrates channel that keeps only the latest value
     */
    fun example4ConflatedChannel() = runBlocking {
        println("Example 4: Conflated Channel")
        
        val channel = Channel<Int>(Channel.CONFLATED)
        
        launch {
            repeat(5) { i ->
                println("Producer: Sending $i")
                channel.send(i) // Only keeps latest value
                delay(100L)
            }
            channel.close()
        }
        
        launch {
            delay(300L) // Start consuming after some values sent
            for (value in channel) {
                println("Consumer: Received $value (may have missed some)")
                delay(200L)
            }
        }.join()
        
        println("---")
    }
    
    /**
     * Example 5: Unlimited Channel
     * Demonstrates channel with unlimited buffer (use with caution!)
     */
    fun example5UnlimitedChannel() = runBlocking {
        println("Example 5: Unlimited Channel")
        
        val channel = Channel<Int>(Channel.UNLIMITED)
        
        launch {
            repeat(10) { i ->
                channel.send(i) // Never suspends
                println("Producer: Sent $i (buffer unlimited)")
                delay(50L)
            }
            channel.close()
        }
        
        launch {
            delay(300L) // Let producer send many values
            for (value in channel) {
                println("Consumer: Received $value")
                delay(150L)
            }
        }.join()
        
        println("---")
    }
    
    /**
     * Example 6: Producer-Consumer Pattern
     * Demonstrates classic producer-consumer with multiple producers/consumers
     */
    fun example6ProducerConsumer() = runBlocking {
        println("Example 6: Producer-Consumer Pattern")
        
        val channel = Channel<Int>(capacity = 5)
        
        // Multiple producers
        val producers = List(3) { producerId ->
            launch {
                repeat(3) { i ->
                    val item = producerId * 10 + i
                    channel.send(item)
                    println("Producer $producerId: produced $item")
                    delay((50 + producerId * 20).toLong())
                }
            }
        }
        
        // Multiple consumers
        val consumers = List(2) { consumerId ->
            launch {
                for (item in channel) {
                    println("Consumer $consumerId: consumed $item")
                    delay(100L)
                }
                println("Consumer $consumerId: done")
            }
        }
        
        // Wait for producers to finish
        producers.forEach { it.join() }
        channel.close() // Close channel when producers are done
        
        // Wait for consumers to finish
        consumers.forEach { it.join() }
        
        println("---")
    }
    
    /**
     * Example 7: Channel Pipeline
     * Demonstrates processing pipeline with channels
     */
    fun example7ChannelPipeline() = runBlocking {
        println("Example 7: Channel Pipeline")
        
        // Create pipeline: numbers -> squares -> filtered -> results
        val numbers = produceNumbers()
        val squares = square(numbers)
        val filtered = filterEven(squares)
        
        // Collect results
        filtered.consumeEach { value ->
            println("Result: $value")
        }
        
        println("Pipeline completed")
        println("---")
    }
    
    /**
     * Example 8: Fan-out (Multiple consumers)
     * Demonstrates distributing work to multiple consumers
     */
    fun example8FanOut() = runBlocking {
        println("Example 8: Fan-out Pattern")
        
        val producer = produceNumbers(max = 10)
        
        // Multiple consumers
        repeat(3) { consumerId ->
            launch {
                for (msg in producer) {
                    println("Consumer $consumerId: processing $msg")
                    delay(100L) // Simulate work
                }
                println("Consumer $consumerId: done")
            }
        }
        
        delay(2000L) // Let consumers work
        producer.cancel() // Cancel producer
        
        println("---")
    }
    
    /**
     * Example 9: Fan-in (Multiple producers)
     * Demonstrates combining multiple producers into one channel
     */
    fun example9FanIn() = runBlocking {
        println("Example 9: Fan-in Pattern")
        
        val resultChannel = Channel<String>()
        
        // Multiple producers
        val producer1 = launch {
            repeat(3) { i ->
                resultChannel.send("Producer 1: Message $i")
                delay(150L)
            }
        }
        
        val producer2 = launch {
            repeat(3) { i ->
                resultChannel.send("Producer 2: Message $i")
                delay(200L)
            }
        }
        
        // Single consumer
        launch {
            delay(1000L) // Let producers send some messages
            for (msg in resultChannel) {
                println("Consumer: $msg")
            }
        }
        
        // Wait for producers to finish
        producer1.join()
        producer2.join()
        resultChannel.close()
        
        delay(500L)
        println("---")
    }
    
    /**
     * Example 10: Actor Pattern
     * Demonstrates actor pattern for shared mutable state
     */
    fun example10ActorPattern() = runBlocking {
        println("Example 10: Actor Pattern")
        
        // Create a counter actor
        val counter = counterActor()
        
        // Send increment messages from multiple coroutines
        val jobs = List(10) { jobId ->
            launch {
                repeat(100) { i ->
                    counter.send(IncrementMessage)
                }
                println("Job $jobId: sent 100 increments")
            }
        }
        
        // Wait for all jobs to complete
        jobs.forEach { it.join() }
        
        // Get the final count
        val responseChannel = Channel<Int>()
        counter.send(GetCountMessage(responseChannel))
        
        val finalCount = responseChannel.receive()
        println("Final count: $finalCount")
        
        counter.close() // Clean up
        println("---")
    }
    
    /**
     * Example 11: Select Expression
     * Demonstrates selecting from multiple channels
     * Note: select API is experimental in some coroutines versions
     */
    fun example11SelectExpression() = runBlocking {
        println("Example 11: Select Expression (Skipped - requires experimental API)")
        println("In coroutines 1.8.0, select API requires @OptIn(ExperimentalCoroutinesApi::class)")
        println("---")
    }
    
    /**
     * Example 12: Channel Timeouts
     * Demonstrates handling timeouts with channels
     * Note: select API is experimental in some coroutines versions
     */
    fun example12ChannelTimeouts() = runBlocking {
        println("Example 12: Channel Timeouts (Skipped - requires experimental API)")
        println("In coroutines 1.8.0, select API requires @OptIn(ExperimentalCoroutinesApi::class)")
        println("---")
    }
    
    /**
     * Example 13: Broadcast Channel
     * Demonstrates broadcasting to multiple receivers
     */
    fun example13BroadcastChannel() = runBlocking {
        println("Example 13: Broadcast Channel")
        
        val broadcast = BroadcastChannel<Int>(capacity = 1)
        
        // Multiple subscribers
        val subscriber1 = broadcast.openSubscription()
        val subscriber2 = broadcast.openSubscription()
        
        launch {
            repeat(3) { i ->
                broadcast.send(i)
                println("Broadcast: sent $i")
                delay(100L)
            }
            broadcast.close()
        }
        
        launch {
            for (msg in subscriber1) {
                println("Subscriber 1: received $msg")
            }
        }
        
        launch {
            delay(150L) // Late subscriber
            for (msg in subscriber2) {
                println("Subscriber 2: received $msg (late)")
            }
        }
        
        delay(500L)
        println("---")
    }
    
    /**
     * Example 14: Channel with Error Handling
     * Demonstrates robust error handling with channels
     */
    fun example14ChannelErrorHandling() = runBlocking {
        println("Example 14: Channel Error Handling")
        
        val channel = Channel<Result<Int>>()
        
        launch {
            try {
                repeat(5) { i ->
                    if (i == 2) {
                        throw RuntimeException("Error at $i")
                    }
                    channel.send(Result.success(i))
                    delay(100L)
                }
            } catch (e: Exception) {
                channel.send(Result.failure(e))
            } finally {
                channel.close()
            }
        }
        
        for (result in channel) {
            result.fold(
                onSuccess = { value -> println("Success: $value") },
                onFailure = { error -> println("Error: ${error.message}") }
            )
        }
        
        println("---")
    }
    
    /**
     * Example 15: Complex Channel System
     * Demonstrates a complete channel-based system
     */
    fun example15ComplexChannelSystem() = runBlocking {
        println("Example 15: Complex Channel System")
        
        // Create a job processing system
        val jobQueue = Channel<Job>(capacity = 10)
        val resultChannel = Channel<JobResult>(capacity = 10)
        
        // Job producers
        val producers = List(3) { producerId ->
            launch {
                repeat(5) { jobId ->
                    val job = Job(producerId * 100 + jobId, "Job $producerId-$jobId")
                    jobQueue.send(job)
                    println("Producer $producerId: enqueued $job")
                    delay((50 + producerId * 30).toLong())
                }
            }
        }
        
        // Job processors (workers)
        val workers = List(2) { workerId ->
            launch {
                for (job in jobQueue) {
                    println("Worker $workerId: processing $job")
                    delay(200L) // Simulate work
                    val result = JobResult(job.id, "Processed by worker $workerId")
                    resultChannel.send(result)
                }
                println("Worker $workerId: done")
            }
        }
        
        // Result consumer
        val consumer = launch {
            var processed = 0
            for (result in resultChannel) {
                println("Consumer: received $result")
                processed++
                if (processed >= 15) break // We expect 15 jobs total
            }
            println("Consumer: processed $processed jobs")
        }
        
        // Wait for producers to finish
        producers.forEach { it.join() }
        jobQueue.close() // Close queue when no more jobs
        
        // Wait for workers to finish
        workers.forEach { it.join() }
        resultChannel.close()
        
        consumer.join()
        println("System completed")
        println("---")
    }
    
    // Helper functions and data classes
    
    private fun CoroutineScope.produceNumbers(max: Int = 5): ReceiveChannel<Int> = produce {
        for (x in 1..max) {
            send(x)
            delay(100L)
        }
    }
    
    private fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for (x in numbers) {
            send(x * x)
        }
    }
    
    private fun CoroutineScope.filterEven(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for (x in numbers) {
            if (x % 2 == 0) {
                send(x)
            }
        }
    }
    
    // Actor pattern implementation
    sealed class CounterMessage
    object IncrementMessage : CounterMessage()
    class GetCountMessage(val response: SendChannel<Int>) : CounterMessage()
    
    private fun CoroutineScope.counterActor(): SendChannel<CounterMessage> = actor {
        var count = 0
        
        for (msg in channel) {
            when (msg) {
                is IncrementMessage -> count++
                is GetCountMessage -> msg.response.send(count)
            }
        }
    }
    
    // Complex system data classes
    data class Job(val id: Int, val name: String)
    data class JobResult(val jobId: Int, val result: String)
    
    /**
     * Run all channel examples
     */
    fun runAll() = runBlocking {
        println("=== Running All Channel Examples ===")
        println()
        
        example1BasicChannel()
        example2RendezvousChannel()
        example3BufferedChannel()
        example4ConflatedChannel()
        example5UnlimitedChannel()
        example6ProducerConsumer()
        example7ChannelPipeline()
        example8FanOut()
        example9FanIn()
        example10ActorPattern()
        example11SelectExpression()
        example12ChannelTimeouts()
        example13BroadcastChannel()
        example14ChannelErrorHandling()
        example15ComplexChannelSystem()
        
        println("=== All Channel Examples Completed ===")
    }
}