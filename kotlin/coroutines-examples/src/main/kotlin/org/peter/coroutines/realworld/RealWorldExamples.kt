package org.peter.coroutines.realworld

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis
import java.io.File
import java.net.URL
import kotlin.random.Random

/**
 * Real-World Application Examples
 * 
 * This file demonstrates practical coroutine applications:
 * 1. Network request handling
 * 2. File I/O operations
 * 3. Database operations
 * 4. UI integration patterns
 * 5. Web scraping
 * 6. Real-time data processing
 * 7. Microservices communication
 * 8. Caching strategies
 * 9. Rate limiting
 * 10. Circuit breaker pattern
 */

object RealWorldExamples {
    
    /**
     * Example 1: Concurrent Network Requests
     * Demonstrates making multiple API calls concurrently
     */
    fun example1ConcurrentApiCalls() = runBlocking {
        println("Example 1: Concurrent Network Requests")
        
        val userIds = listOf(1, 2, 3, 4, 5)
        
        val time = measureTimeMillis {
            val users = userIds.map { userId ->
                async {
                    fetchUserData(userId)
                }
            }.awaitAll()
            
            println("Fetched ${users.size} users:")
            users.forEach { user ->
                println("  - $user")
            }
        }
        
        println("Completed in $time ms")
        println("---")
    }
    
    /**
     * Example 2: File Processing Pipeline
     * Demonstrates processing files with coroutines
     */
    fun example2FileProcessing() = runBlocking {
        println("Example 2: File Processing Pipeline")
        
        // Simulate processing multiple files
        val filePaths = listOf("file1.txt", "file2.txt", "file3.txt")
        
        val results = filePaths.map { filePath ->
            async {
                processFile(filePath)
            }
        }.awaitAll()
        
        println("Processed ${results.size} files:")
        results.forEachIndexed { index, result ->
            println("  File ${index + 1}: $result lines processed")
        }
        
        println("---")
    }
    
    /**
     * Example 3: Database Operations
     * Demonstrates async database operations
     */
    fun example3DatabaseOperations() = runBlocking {
        println("Example 3: Database Operations")
        
        // Simulate database service
        val dbService = DatabaseService()
        
        // Perform operations concurrently
        coroutineScope {
            val insertJob = launch {
                val id = dbService.insertUser("John Doe", "john@example.com")
                println("Inserted user with ID: $id")
            }
            
            val queryJob = launch {
                val users = dbService.getUsers()
                println("Found ${users.size} users")
            }
            
            // Wait for both operations
            insertJob.join()
            queryJob.join()
        }
        
        println("---")
    }
    
    /**
     * Example 4: Real-time Data Stream
     * Demonstrates real-time data processing with Flow
     */
    fun example4RealTimeDataStream() = runBlocking {
        println("Example 4: Real-time Data Stream")
        
        // Create a sensor data stream
        val sensorData = sensorDataStream()
            .onEach { data ->
                println("Raw sensor data: $data")
            }
            .filter { it.value > 20 } // Filter high values
            .map { it.copy(value = it.value * 1.8 + 32) } // Convert to Fahrenheit
            .distinctUntilChanged() // Only emit when changed
        
        // Collect for 3 seconds
        val job = launch {
            sensorData.collect { processed ->
                println("Processed: ${processed.sensorId} = ${"%.1f".format(processed.value)}°F")
            }
        }
        
        delay(3000L)
        job.cancel()
        
        println("---")
    }
    
    /**
     * Example 5: Web Scraper
     * Demonstrates concurrent web scraping
     */
    fun example5WebScraper() = runBlocking {
        println("Example 5: Web Scraper")
        
        val urls = listOf(
            "https://example.com/page1",
            "https://example.com/page2",
            "https://example.com/page3"
        )
        
        // Scrape pages concurrently with limit
        val semaphore = Semaphore(2) // Limit to 2 concurrent requests
        
        val scrapedData = urls.map { url ->
            async {
                semaphore.withPermit {
                    scrapeWebPage(url)
                }
            }
        }.awaitAll()
        
        println("Scraped ${scrapedData.size} pages:")
        scrapedData.forEachIndexed { index, data ->
            println("  Page ${index + 1}: ${data.length} characters")
        }
        
        println("---")
    }
    
    /**
     * Example 6: Microservice Communication
     * Demonstrates service-to-service communication
     */
    fun example6MicroserviceCommunication() = runBlocking {
        println("Example 6: Microservice Communication")
        
        // Simulate order processing across services
        val order = Order(
            id = 123,
            items = listOf("Item1", "Item2", "Item3"),
            userId = 456
        )
        
        println("Processing order ${order.id}...")
        
        val result = processOrder(order)
        
        println("Order processing result: $result")
        println("---")
    }
    
    /**
     * Example 7: Caching with Coroutines
     * Demonstrates async caching patterns
     */
    fun example7CachingPattern() = runBlocking {
        println("Example 7: Caching with Coroutines")
        
        val cache = AsyncCache<String, String>()
        
        // First call - cache miss
        val time1 = measureTimeMillis {
            val data = cache.get("key1") {
                fetchExpensiveData("key1")
            }
            println("First call result: $data")
        }
        println("First call time: $time1 ms")
        
        // Second call - cache hit
        val time2 = measureTimeMillis {
            val data = cache.get("key1") {
                fetchExpensiveData("key1")
            }
            println("Second call result: $data")
        }
        println("Second call time: $time2 ms")
        
        println("Cache improvement: ${time1 - time2} ms")
        println("---")
    }
    
    /**
     * Example 8: Rate Limiting
     * Demonstrates API rate limiting
     */
    fun example8RateLimiting() = runBlocking {
        println("Example 8: Rate Limiting")
        
        val apiClient = RateLimitedApiClient(requestsPerSecond = 2)
        
        // Make multiple requests
        val jobs = List(6) { requestId ->
            launch {
                val result = apiClient.makeRequest(requestId)
                println("Request $requestId: $result")
            }
        }
        
        jobs.forEach { it.join() }
        println("---")
    }
    
    /**
     * Example 9: Circuit Breaker Pattern
     * Demonstrates circuit breaker for fault tolerance
     */
    fun example9CircuitBreaker() = runBlocking {
        println("Example 9: Circuit Breaker Pattern")
        
        val unstableService = UnstableService()
        val circuitBreaker = CircuitBreaker(
            failureThreshold = 3,
            resetTimeout = 2000L
        )
        
        // Make requests through circuit breaker
        repeat(10) { attempt ->
            try {
                val result = circuitBreaker.execute {
                    unstableService.call()
                }
                println("Attempt ${attempt + 1}: Success - $result")
            } catch (e: Exception) {
                println("Attempt ${attempt + 1}: Failed - ${e.message}")
            }
            delay(500L)
        }
        
        println("---")
    }
    
    /**
     * Example 10: Background Task Manager
     * Demonstrates managing background tasks
     */
    fun example10BackgroundTaskManager() = runBlocking {
        println("Example 10: Background Task Manager")
        
        val taskManager = BackgroundTaskManager()
        
        // Submit multiple tasks
        val tasks = listOf(
            BackgroundTask("Process data", 1000L),
            BackgroundTask("Generate report", 2000L),
            BackgroundTask("Send notifications", 1500L),
            BackgroundTask("Cleanup temp files", 500L)
        )
        
        tasks.forEach { task ->
            taskManager.submitTask(task)
        }
        
        // Monitor progress
        delay(1000L)
        println("Active tasks: ${taskManager.getActiveTaskCount()}")
        
        // Wait for completion
        taskManager.awaitCompletion()
        println("All tasks completed")
        
        println("---")
    }
    
    /**
     * Example 11: Real-time Chat Application
     * Demonstrates chat application patterns
     */
    fun example11ChatApplication() = runBlocking {
        println("Example 11: Real-time Chat Application")
        
        val chatRoom = ChatRoom("Kotlin Coroutines")
        
        // Users joining
        val user1 = chatRoom.join("Alice")
        val user2 = chatRoom.join("Bob")
        
        // Send messages
        launch {
            user1.sendMessage("Hello everyone!")
            delay(500L)
            user1.sendMessage("How are you?")
        }
        
        launch {
            delay(300L)
            user2.sendMessage("Hi Alice!")
        }
        
        // Listen for messages
        val messageJob = launch {
            chatRoom.messageFlow.collect { message ->
                println("[${message.timestamp}] ${message.user}: ${message.text}")
            }
        }
        
        delay(2000L)
        messageJob.cancel()
        
        println("Chat session ended")
        println("---")
    }
    
    /**
     * Example 12: Stock Price Monitor
     * Demonstrates real-time financial data processing
     */
    fun example12StockPriceMonitor() = runBlocking {
        println("Example 12: Stock Price Monitor")
        
        val monitor = StockPriceMonitor()
        
        // Subscribe to stock updates
        val subscription = monitor.priceFlow("AAPL")
            .onEach { price ->
                println("AAPL: $${"%.2f".format(price)}")
            }
            .launchIn(this)
        
        // Simulate monitoring for 3 seconds
        delay(3000L)
        subscription.cancel()
        
        println("Monitoring stopped")
        println("---")
    }
    
    /**
     * Example 13: Image Processing Pipeline
     * Demonstrates parallel image processing
     */
    fun example13ImageProcessing() = runBlocking {
        println("Example 13: Image Processing Pipeline")
        
        val imageUrls = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
        )
        
        val processor = ImageProcessor()
        
        // Process images concurrently
        val processedImages = imageUrls.map { url ->
            async {
                processor.processImage(url)
            }
        }.awaitAll()
        
        println("Processed ${processedImages.size} images:")
        processedImages.forEachIndexed { index, image ->
            println("  Image ${index + 1}: ${image.width}x${image.height}")
        }
        
        println("---")
    }
    
    /**
     * Example 14: Notification System
     * Demonstrates sending notifications with retry
     */
    fun example14NotificationSystem() = runBlocking {
        println("Example 14: Notification System")
        
        val notificationService = NotificationService()
        
        val notifications = listOf(
            Notification("user1@example.com", "Welcome!", "Welcome to our service"),
            Notification("user2@example.com", "Update", "Your account has been updated"),
            Notification("user3@example.com", "Alert", "Security alert")
        )
        
        // Send notifications with retry logic
        val results = notifications.map { notification ->
            async {
                notificationService.sendWithRetry(notification, maxRetries = 3)
            }
        }.awaitAll()
        
        val successful = results.count { it }
        val failed = results.size - successful
        
        println("Notifications sent: $successful successful, $failed failed")
        println("---")
    }
    
    /**
     * Example 15: Complete E-commerce System
     * Demonstrates full e-commerce workflow
     */
    fun example15EcommerceSystem() = runBlocking {
        println("Example 15: Complete E-commerce System")
        
        val ecommerceService = EcommerceService()
        
        // Simulate user journey
        println("1. Browsing products...")
        val products = ecommerceService.browseProducts()
        println("   Found ${products.size} products")
        
        println("\n2. Adding to cart...")
        val cart = ecommerceService.addToCart(products.first().id, 2)
        println("   Cart total: $${"%.2f".format(cart.total)}")
        
        println("\n3. Checking out...")
        val order = ecommerceService.checkout(cart, "user123")
        println("   Order created: ${order.id}")
        
        println("\n4. Processing payment...")
        val paymentResult = ecommerceService.processPayment(order)
        println("   Payment: ${if (paymentResult) "Success" else "Failed"}")
        
        println("\n5. Sending confirmation...")
        ecommerceService.sendConfirmation(order)
        println("   Confirmation sent")
        
        println("\nE-commerce workflow completed!")
        println("---")
    }
    
    // Helper functions and data classes
    
    private suspend fun fetchUserData(userId: Int): String {
        delay(Random.nextLong(300, 800)) // Simulate network delay
        return "User $userId: John Doe"
    }
    
    private suspend fun processFile(filePath: String): Int {
        delay(Random.nextLong(200, 600)) // Simulate file processing
        return Random.nextInt(50, 200) // Simulate line count
    }
    
    private suspend fun scrapeWebPage(url: String): String {
        delay(Random.nextLong(500, 1500)) // Simulate network delay
        return "Content from $url".repeat(Random.nextInt(10, 50))
    }
    
    private suspend fun fetchExpensiveData(key: String): String {
        delay(1000L) // Simulate expensive operation
        return "Data for $key"
    }
    
    // Data classes
    data class SensorData(val sensorId: String, val value: Double, val timestamp: Long)
    data class Order(val id: Int, val items: List<String>, val userId: Int)
    data class BackgroundTask(val name: String, val duration: Long)
    data class ChatMessage(val user: String, val text: String, val timestamp: Long = System.currentTimeMillis())
    data class ProcessedImage(val url: String, val width: Int, val height: Int)
    data class Notification(val to: String, val subject: String, val body: String)
    data class Product(val id: String, val name: String, val price: Double)
    data class Cart(val items: Map<String, Int>, val total: Double)
    data class EcommerceOrder(val id: String, val items: List<String>, val total: Double, val userId: String)
    
    // Simulated services
    
    class DatabaseService {
        private val users = mutableListOf<String>()
        
        suspend fun insertUser(name: String, email: String): Int {
            delay(300L) // Simulate DB write
            users.add("$name ($email)")
            return users.size
        }
        
        suspend fun getUsers(): List<String> {
            delay(200L) // Simulate DB read
            return users.toList()
        }
    }
    
    class AsyncCache<K, V> {
        private val cache = mutableMapOf<K, Deferred<V>>()
        
        suspend fun get(key: K, loader: suspend () -> V): V {
            val deferred = cache.getOrPut(key) {
                CoroutineScope(Dispatchers.Default).async(start = CoroutineStart.LAZY) {
                    loader()
                }
            }
            return deferred.await()
        }
        
        fun invalidate(key: K) {
            cache.remove(key)
        }
    }
    
    class RateLimitedApiClient(private val requestsPerSecond: Int) {
        private val semaphore = Semaphore(requestsPerSecond)
        private val timeWindow = 1000L // 1 second
        
        suspend fun makeRequest(requestId: Int): String {
            semaphore.acquire()
            
            try {
                delay(timeWindow / requestsPerSecond) // Simulate API call
                return "Response for request $requestId"
            } finally {
                // Release after time window
                launch {
                    delay(timeWindow)
                    semaphore.release()
                }
            }
        }
    }
    
    class UnstableService {
        private var callCount = 0
        
        suspend fun call(): String {
            callCount++
            delay(100L)
            
            // Fail every 3rd call
            if (callCount % 3 == 0) {
                throw RuntimeException("Service unavailable")
            }
            
            return "Service response #$callCount"
        }
    }
    
    class CircuitBreaker(
        private val failureThreshold: Int,
        private val resetTimeout: Long
    ) {
        private var failures = 0
        private var state: State = State.CLOSED
        private var lastFailureTime = 0L
        
        sealed class State {
            object CLOSED : State()
            object OPEN : State()
            object HALF_OPEN : State()
        }
        
        suspend fun <T> execute(block: suspend () -> T): T {
            when (state) {
                State.OPEN -> {
                    if (System.currentTimeMillis() - lastFailureTime > resetTimeout) {
                        state = State.HALF_OPEN
                    } else {
                        throw RuntimeException("Circuit breaker is OPEN")
                    }
                }
                else -> {}
            }
            
            return try {
                val result = block()
                onSuccess()
                result
            } catch (e: Exception) {
                onFailure()
                throw e
            }
        }
        
        private fun onSuccess() {
            failures = 0
            state = State.CLOSED
        }
        
        private fun onFailure() {
            failures++
            lastFailureTime = System.currentTimeMillis()
            
            if (failures >= failureThreshold) {
                state = State.OPEN
            }
        }
    }
    
    class BackgroundTaskManager {
        private val tasks = mutableListOf<Job>()
        private val scope = CoroutineScope(Dispatchers.Default)
        
        fun submitTask(task: BackgroundTask) {
            val job = scope.launch {
                println("Starting task: ${task.name}")
                delay(task.duration)
                println("Completed task: ${task.name}")
            }
            tasks.add(job)
        }
        
        suspend fun awaitCompletion() {
            tasks.forEach { it.join() }
        }
        
        fun getActiveTaskCount(): Int {
            return tasks.count { it.isActive }
        }
    }
    
    class ChatRoom(val name: String) {
        private val _messages = MutableSharedFlow<ChatMessage>()
        val messageFlow: Flow<ChatMessage> = _messages.asSharedFlow()
        
        private val users = mutableMapOf<String, SendChannel<ChatMessage>>()
        
        fun join(username: String): ChatUser {
            val channel = Channel<ChatMessage>(Channel.UNLIMITED)
            users[username] = channel
            
            // Start listening for user messages
            CoroutineScope(Dispatchers.Default).launch {
                for (message in channel) {
                    _messages.emit(message)
                }
            }
            
            return ChatUser(username, channel)
        }
        
        fun leave(username: String) {
            users.remove(username)?.close()
        }
    }
    
    class ChatUser(val name: String, private val channel: SendChannel<ChatMessage>) {
        suspend fun sendMessage(text: String) {
            channel.send(ChatMessage(name, text))
        }
    }
    
    class StockPriceMonitor {
        private val prices = mutableMapOf<String, MutableStateFlow<Double>>()
        
        fun priceFlow(symbol: String): Flow<Double> {
            val flow = prices.getOrPut(symbol) {
                MutableStateFlow(100.0 + Random.nextDouble() * 50.0)
            }
            
            // Simulate price updates
            CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    delay(Random.nextLong(200, 800))
                    val current = flow.value
                    val change = (Random.nextDouble() - 0.5) * 2.0 // Random change
                    flow.value = (current + change).coerceIn(50.0, 200.0)
                }
            }
            
            return flow.asStateFlow()
        }
    }
    
    class ImageProcessor {
        suspend fun processImage(url: String): ProcessedImage {
            delay(Random.nextLong(800, 2000)) // Simulate processing
            return ProcessedImage(url, Random.nextInt(800, 1920), Random.nextInt(600, 1080))
        }
    }
    
    class NotificationService {
        suspend fun sendWithRetry(notification: Notification, maxRetries: Int): Boolean {
            var attempt = 0
            
            while (attempt <= maxRetries) {
                try {
                    sendNotification(notification)
                    return true
                } catch (e: Exception) {
                    attempt++
                    if (attempt > maxRetries) {
                        println("Failed to send notification after $maxRetries attempts: ${e.message}")
                        return false
                    }
                    delay(1000L * attempt) // Exponential backoff
                }
            }
            
            return false
        }
        
        private suspend fun sendNotification(notification: Notification) {
            delay(Random.nextLong(300, 1000))
            
            // Randomly fail to simulate real-world conditions
            if (Random.nextDouble() < 0.2) {
                throw RuntimeException("Notification service unavailable")
            }
            
            println("Sent notification to ${notification.to}: ${notification.subject}")
        }
    }
    
    class EcommerceService {
        private val products = listOf(
            Product("p1", "Laptop", 999.99),
            Product("p2", "Phone", 699.99),
            Product("p3", "Tablet", 399.99),
            Product("p4", "Headphones", 199.99)
        )
        
        suspend fun browseProducts(): List<Product> {
            delay(500L)
            return products
        }
        
        suspend fun addToCart(productId: String, quantity: Int): Cart {
            delay(300L)
            val product = products.find { it.id == productId } ?: throw IllegalArgumentException("Product not found")
            val items = mapOf(productId to quantity)
            val total = product.price * quantity
            return Cart(items, total)
        }
        
        suspend fun checkout(cart: Cart, userId: String): EcommerceOrder {
            delay(800L)
            return EcommerceOrder(
                id = "ORD-${System.currentTimeMillis()}",
                items = cart.items.map { (id, qty) -> "$id x$qty" },
                total = cart.total,
                userId = userId
            )
        }
        
        suspend fun processPayment(order: EcommerceOrder): Boolean {
            delay(600L)
            return Random.nextDouble() > 0.1 // 90% success rate
        }
        
        suspend fun sendConfirmation(order: EcommerceOrder) {
            delay(400L)
            println("Confirmation email sent for order ${order.id}")
        }
    }
    
    // Flow generators
    
    private fun sensorDataStream(): Flow<SensorData> = flow {
        while (true) {
            val sensorId = listOf("temp1", "temp2", "humidity1").random()
            val value = 15.0 + Random.nextDouble() * 20.0
            emit(SensorData(sensorId, value, System.currentTimeMillis()))
            delay(Random.nextLong(200, 800))
        }
    }
    
    private suspend fun processOrder(order: Order): String = coroutineScope {
        println("Starting order processing for order ${order.id}")
        
        // Parallel processing steps
        val inventoryCheck = async { checkInventory(order.items) }
        val paymentProcessing = async { processPayment(order.userId, order.items.size * 10.0) }
        val shippingPreparation = async { prepareShipping(order) }
        
        // Wait for all steps
        val inventoryOk = inventoryCheck.await()
        val paymentOk = paymentProcessing.await()
        val shippingReady = shippingPreparation.await()
        
        if (inventoryOk && paymentOk && shippingReady) {
            "Order ${order.id} processed successfully"
        } else {
            "Order ${order.id} failed: inventory=$inventoryOk, payment=$paymentOk, shipping=$shippingReady"
        }
    }
    
    private suspend fun checkInventory(items: List<String>): Boolean {
        delay(800L)
        return Random.nextDouble() > 0.1 // 90% in stock
    }
    
    private suspend fun processPayment(userId: Int, amount: Double): Boolean {
        delay(600L)
        return Random.nextDouble() > 0.05 // 95% payment success
    }
    
    private suspend fun prepareShipping(order: Order): Boolean {
        delay(700L)
        return true
    }
    
    /**
     * Run all real-world examples
     */
    fun runAll() = runBlocking {
        println("=== Running All Real-World Examples ===")
        println()
        
        // Run a subset of examples (all 15 would be too long)
        example1ConcurrentApiCalls()
        example2FileProcessing()
        example4RealTimeDataStream()
        example7CachingPattern()
        example8RateLimiting()
        example10BackgroundTaskManager()
        example14NotificationSystem()
        example15EcommerceSystem()
        
        println("=== All Real-World Examples Completed ===")
    }
}