package org.peter.coroutines.structured

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Structured Concurrency Examples
 * 
 * This file demonstrates structured concurrency patterns:
 * 1. CoroutineScope and structured lifecycle
 * 2. SupervisorScope for independent failure handling
 * 3. Coroutine context inheritance
 * 4. Job hierarchy and cancellation propagation
 * 5. Exception handling in structured concurrency
 */

object StructuredExamples {
    
    /**
     * Example 1: Basic CoroutineScope
     * Demonstrates structured concurrency with coroutineScope
     */
    fun example1BasicScope() = runBlocking {
        println("Example 1: Basic CoroutineScope")
        
        coroutineScope {
            launch {
                delay(1000L)
                println("Task 1 completed")
            }
            
            launch {
                delay(500L)
                println("Task 2 completed")
            }
        }
        
        println("All tasks in scope completed")
        println("---")
    }
    
    /**
     * Example 2: Nested Scopes
     * Shows how scopes can be nested and how cancellation propagates
     */
    fun example2NestedScopes() = runBlocking {
        println("Example 2: Nested Scopes")
        
        coroutineScope {
            launch {
                println("Parent coroutine started")
                
                coroutineScope {
                    launch {
                        delay(300L)
                        println("Child 1 completed")
                    }
                    
                    launch {
                        delay(500L)
                        println("Child 2 completed")
                    }
                }
                
                println("Parent coroutine completed")
            }
        }
        
        println("All nested scopes completed")
        println("---")
    }
    
    /**
     * Example 3: SupervisorScope
     * Demonstrates supervisor scope where failures don't cancel siblings
     */
    fun example3SupervisorScope() = runBlocking {
        println("Example 3: SupervisorScope")
        
        supervisorScope {
            val child1 = launch {
                try {
                    delay(1000L)
                    println("Child 1 completed successfully")
                } catch (e: CancellationException) {
                    println("Child 1 cancelled: ${e.message}")
                }
            }
            
            val child2 = launch {
                delay(300L)
                throw RuntimeException("Child 2 failed!")
            }
            
            // Wait for both children
            try {
                child1.join()
                child2.join()
            } catch (e: Exception) {
                println("Caught exception from child: ${e.message}")
            }
        }
        
        println("Supervisor scope completed (child 2 failed but didn't cancel child 1)")
        println("---")
    }
    
    /**
     * Example 4: Job Hierarchy
     * Shows parent-child relationship in coroutine jobs
     */
    fun example4JobHierarchy() = runBlocking {
        println("Example 4: Job Hierarchy")
        
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        
        val child1 = scope.launch {
            delay(1000L)
            println("Child 1 completed")
        }
        
        val child2 = scope.launch {
            delay(1500L)
            println("Child 2 completed")
        }
        
        delay(500L)
        println("Cancelling parent job...")
        parentJob.cancel() // This cancels all children
        
        try {
            child1.join()
            child2.join()
        } catch (e: CancellationException) {
            println("Children cancelled due to parent cancellation")
        }
        
        println("---")
    }
    
    /**
     * Example 5: Structured Exception Handling
     * Demonstrates proper exception handling in structured concurrency
     */
    fun example5StructuredExceptionHandling() = runBlocking {
        println("Example 5: Structured Exception Handling")
        
        try {
            coroutineScope {
                launch {
                    delay(100L)
                    throw RuntimeException("Error in child 1!")
                }
                
                launch {
                    delay(200L)
                    println("Child 2 completed")
                }
            }
        } catch (e: Exception) {
            println("Caught exception from scope: ${e.message}")
        }
        
        println("Scope completed with exception handling")
        println("---")
    }
    
    /**
     * Example 6: Coroutine Context Inheritance
     * Shows how coroutines inherit context from their parent
     */
    fun example6ContextInheritance() = runBlocking {
        println("Example 6: Coroutine Context Inheritance")
        
        val customDispatcher = newSingleThreadContext("CustomThread")
        
        coroutineScope {
            // Parent uses custom dispatcher
            withContext(customDispatcher) {
                println("Parent context: ${Thread.currentThread().name}")
                
                launch {
                    // Child inherits parent's context
                    println("Child 1 context: ${Thread.currentThread().name}")
                    delay(100L)
                }
                
                launch(Dispatchers.IO) {
                    // Child overrides with different dispatcher
                    println("Child 2 context: ${Thread.currentThread().name}")
                    delay(100L)
                }
            }
        }
        
        customDispatcher.close()
        println("---")
    }
    
    /**
     * Example 7: Scope Cancellation
     * Demonstrates cancelling an entire scope
     */
    fun example7ScopeCancellation() = runBlocking {
        println("Example 7: Scope Cancellation")
        
        val scope = CoroutineScope(Dispatchers.Default)
        
        scope.launch {
            repeat(10) { i ->
                println("Task A: iteration $i")
                delay(100L)
            }
        }
        
        scope.launch {
            repeat(10) { i ->
                println("Task B: iteration $i")
                delay(150L)
            }
        }
        
        delay(450L)
        println("Cancelling entire scope...")
        scope.cancel() // Cancels all coroutines in this scope
        
        delay(1000L) // Give time for cancellation to complete
        println("Scope cancelled")
        println("---")
    }
    
    /**
     * Example 8: Structured Concurrency with async
     * Shows structured concurrency with async/await pattern
     */
    fun example8StructuredAsync() = runBlocking {
        println("Example 8: Structured Concurrency with async")
        
        val time = measureTimeMillis {
            coroutineScope {
                val deferred1 = async { fetchUserData(1000L) }
                val deferred2 = async { fetchUserProfile(800L) }
                
                try {
                    val user = deferred1.await()
                    val profile = deferred2.await()
                    println("Combined result: $user with $profile")
                } catch (e: Exception) {
                    println("Failed to fetch data: ${e.message}")
                }
            }
        }
        
        println("Completed in $time ms")
        println("---")
    }
    
    /**
     * Example 9: SupervisorJob Pattern
     * Demonstrates using SupervisorJob for independent coroutines
     */
    fun example9SupervisorJob() = runBlocking {
        println("Example 9: SupervisorJob Pattern")
        
        val supervisor = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.Default + supervisor)
        
        val job1 = scope.launch {
            delay(200L)
            throw RuntimeException("Job 1 failed!")
        }
        
        val job2 = scope.launch {
            repeat(5) { i ->
                delay(100L)
                println("Job 2: iteration $i")
            }
        }
        
        delay(600L)
        
        // Job 1 failed but job 2 continues
        println("Job 1 is cancelled: ${job1.isCancelled}")
        println("Job 2 is active: ${job2.isActive}")
        
        scope.cancel() // Clean up
        println("---")
    }
    
    /**
     * Example 10: Complex Structured Workflow
     * Demonstrates a complex workflow with structured concurrency
     */
    fun example10ComplexWorkflow() = runBlocking {
        println("Example 10: Complex Structured Workflow")
        
        val result = processOrderWorkflow()
        println("Order processing result: $result")
        println("---")
    }
    
    // Helper functions
    
    private suspend fun fetchUserData(delayMillis: Long): String {
        delay(delayMillis)
        return "User Data"
    }
    
    private suspend fun fetchUserProfile(delayMillis: Long): String {
        delay(delayMillis)
        return "User Profile"
    }
    
    private suspend fun processOrderWorkflow(): String = coroutineScope {
        println("Starting order processing workflow...")
        
        // Process payment and inventory concurrently
        val paymentDeferred = async { processPayment() }
        val inventoryDeferred = async { checkInventory() }
        
        // Wait for both to complete
        val paymentResult = paymentDeferred.await()
        val inventoryResult = inventoryDeferred.await()
        
        if (!paymentResult || !inventoryResult) {
            throw RuntimeException("Order processing failed")
        }
        
        // Process shipping sequentially
        val shippingResult = processShipping()
        
        "Order completed successfully. Shipping: $shippingResult"
    }
    
    private suspend fun processPayment(): Boolean {
        delay(800L)
        println("Payment processed")
        return true
    }
    
    private suspend fun checkInventory(): Boolean {
        delay(600L)
        println("Inventory checked")
        return true
    }
    
    private suspend fun processShipping(): String {
        delay(400L)
        println("Shipping processed")
        return "Shipped"
    }
    
    /**
     * Run all structured concurrency examples
     */
    fun runAll() = runBlocking {
        println("=== Running All Structured Concurrency Examples ===")
        println()
        
        example1BasicScope()
        example2NestedScopes()
        example3SupervisorScope()
        example4JobHierarchy()
        example5StructuredExceptionHandling()
        example6ContextInheritance()
        example7ScopeCancellation()
        example8StructuredAsync()
        example9SupervisorJob()
        example10ComplexWorkflow()
        
        println("=== All Structured Concurrency Examples Completed ===")
    }
}