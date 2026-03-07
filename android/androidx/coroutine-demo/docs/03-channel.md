# Channel 教程

本文档详细介绍 Kotlin 协程中的 Channel，包括 Channel 基础、生产者消费者模式，以及 select 表达式的使用。

## 目录

1. [Channel 基础](#channel-基础)
2. [生产者消费者模式](#生产者消费者模式)
3. [select 表达式](#select-表达式)

---

## Channel 基础

### 什么是 Channel？

Channel 是协程之间传递数据的管道，类似于 `BlockingQueue`，但是非阻塞的。它实现了生产者-消费者模式，允许协程之间安全地传递数据。

### 核心概念

- **SendChannel**: 发送端，用于发送数据
- **ReceiveChannel**: 接收端，用于接收数据
- **send()**: 挂起函数，发送数据到通道
- **receive()**: 挂起函数，从通道接收数据

### Channel 容量策略

| 策略 | 容量 | 说明 |
|------|------|------|
| RENDEZVOUS | 0 (默认) | 发送者和接收者必须同时准备好 |
| BUFFERED | 64 | 有缓冲区，容量为 64（可配置） |
| UNLIMITED | Int.MAX_VALUE | 无限容量，发送永不挂起 |
| CONFLATED | -1 | 只保留最新元素，新元素覆盖旧元素 |

### 基本使用

```kotlin
// 创建 Channel
val channel = Channel<String>()

// 发送数据
launch {
    channel.send("Hello")
    channel.send("World")
    channel.close()  // 关闭通道
}

// 接收数据
launch {
    for (msg in channel) {
        println("Received: $msg")
    }
}
```

### RENDEZVOUS 策略 (默认)

发送者必须等待接收者准备好才能发送。

```kotlin
val channel = Channel<Int>()  // 容量为 0

launch {
    println("Sending 1...")
    channel.send(1)  // 会挂起，直到有接收者
    println("Sent 1")
}

delay(1000)  // 延迟接收
println("Receiving: ${channel.receive()}")
```

### BUFFERED 策略

有固定大小的缓冲区，缓冲区满时发送者会挂起。

```kotlin
val channel = Channel<Int>(3)  // 容量为 3

launch {
    repeat(5) { i ->
        println("Sending $i")
        channel.send(i)  // 前3个立即发送，第4个会挂起
        println("Sent $i")
    }
    channel.close()
}

delay(500)

for (value in channel) {
    println("Received: $value")
    delay(100)
}
```

### UNLIMITED 策略

缓冲区无限大，`send()` 永远不会挂起。可能导致内存溢出，谨慎使用。

```kotlin
val channel = Channel<Int>(Channel.UNLIMITED)

launch {
    repeat(1000) { i ->
        channel.send(i)  // 立即返回，不会挂起
    }
    channel.close()
}
```

### CONFLATED 策略

只保留最新的元素，新元素会覆盖旧元素。适用于只关心最新数据的场景。

```kotlin
val channel = Channel<Int>(Channel.CONFLATED)

launch {
    repeat(5) { i ->
        channel.send(i)
        delay(50)  // 快速发送
    }
    channel.close()
}

delay(300)  // 延迟接收

for (value in channel) {
    println("Received: $value")  // 可能只收到最新的值
}
```

### Channel 的关闭

```kotlin
val channel = Channel<String>()

launch {
    channel.send("First")
    channel.send("Second")
    channel.close()  // 关闭通道

    // 关闭后发送会抛出异常
    // channel.send("Third")  // ClosedSendChannelException
}

// 关闭后可以继续接收已发送的数据
println(channel.receive())  // First
println(channel.receive())  // Second

// 检查状态
println(channel.isClosedForSend)   // true
println(channel.isClosedForReceive)  // true
```

### 相关 Activity

- [ChannelBasicsActivity](../src/main/java/com/peter/coroutine/demo/channel/ChannelBasicsActivity.kt)

---

## 生产者消费者模式

### produce 构建器

`produce` 是一个协程构建器，专门用于创建生产者协程。它返回一个 `ReceiveChannel`。

#### 特点

- 自动创建 Channel
- 协程结束时自动关闭 Channel
- 异常时自动关闭 Channel
- 支持指定 Channel 容量

```kotlin
fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
    for (i in 1..5) {
        send(i)
        delay(100)
    }
    // 协程结束，Channel 自动关闭
}

// 使用
val numbers = produceNumbers()
for (num in numbers) {
    println("Received: $num")
}
```

### consumeEach 消费

`consumeEach` 是一个扩展函数，用于消费 Channel 中的所有元素。

```kotlin
val messages = produce<String> {
    listOf("A", "B", "C").forEach { send(it) }
}

messages.consumeEach { message ->
    println("Received: $message")
}
```

### 多个消费者

一个 Channel 可以被多个消费者同时消费。每个元素只会被一个消费者获取（竞争消费）。

```kotlin
val tasks = produce<Int>(capacity = Channel.UNLIMITED) {
    repeat(10) { send(it) }
}

// 创建 3 个消费者
val consumers = listOf("A", "B", "C").map { name ->
    async(Dispatchers.Default) {
        for (task in tasks) {
            println("$name processing task $task")
            delay(100)
        }
    }
}

consumers.awaitAll()
// 每个任务只被一个消费者处理
```

### 负载均衡模式

多个工作线程同时处理任务，任务自动分配给空闲的消费者。

```kotlin
fun CoroutineScope.processTasks(tasks: ReceiveChannel<Int>, name: String) = async {
    var count = 0
    for (task in tasks) {
        println("$name: Processing task $task")
        delay(100)
        count++
    }
    "$name processed $count tasks"
}

val taskChannel = produce<Int>(capacity = Channel.UNLIMITED) {
    repeat(20) { send(it) }
}

val results = listOf("Worker1", "Worker2", "Worker3").map { name ->
    processTasks(taskChannel, name)
}.awaitAll()

results.forEach { println(it) }
```

### 相关 Activity

- [ProduceConsumeActivity](../src/main/java/com/peter/coroutine/demo/channel/ProduceConsumeActivity.kt)

---

## select 表达式

### 什么是 Select 表达式？

`select` 允许同时等待多个挂起操作，哪个先完成就执行哪个。它类似于 Go 语言中的 select 语句，实现多路复用。

### 核心概念

- **select<T>**: 同时等待多个操作，返回最先完成的结果
- **onReceive**: Channel 接收的子句
- **onSend**: Channel 发送的子句
- **onTimeout**: 超时的子句

### 使用场景

- 从多个数据源获取数据，使用最快的
- 实现带超时的操作
- 多个 Channel 竞争消费
- 公平调度

### select onReceive

同时等待多个 Channel，接收最先到达的数据。

```kotlin
val fastChannel = produce<String> {
    delay(100)
    send("Fast")
}

val slowChannel = produce<String> {
    delay(500)
    send("Slow")
}

val result = select<String> {
    fastChannel.onReceive { value -> value }
    slowChannel.onReceive { value -> value }
}
println(result)  // "Fast"
```

### select onSend

同时尝试向多个 Channel 发送数据，哪个 Channel 先准备好接收，就发送到哪个。

```kotlin
val channelA = Channel<String>(1)
val channelB = Channel<String>(1)

// 消费者 A - 处理快
launch {
    while (true) {
        delay(100)
        val msg = channelA.receive()
        println("Consumer A: $msg")
    }
}

// 消费者 B - 处理慢
launch {
    while (true) {
        delay(300)
        val msg = channelB.receive()
        println("Consumer B: $msg")
    }
}

// 生产者使用 select 发送
repeat(6) { i ->
    val message = "Task$i"
    val sentTo = select<String> {
        channelA.onSend(message) { "A" }
        channelB.onSend(message) { "B" }
    }
    println("Sent $message to Channel $sentTo")
}
```

### 带超时的 select

```kotlin
val slowChannel = Channel<String>()

launch {
    delay(2000)
    slowChannel.send("Late data")
}

val result = withTimeoutOrNull(500) {
    select<String> {
        slowChannel.onReceive { value -> "Received: $value" }
    }
} ?: "Timeout!"

println(result)  // "Timeout!"
```

### selectUnbiased 公平调度

- **select**: 按子句声明顺序优先选择（有偏见）
- **selectUnbiased**: 随机选择（无偏见，公平调度）

```kotlin
val channelA = Channel<Int>()
val channelB = Channel<Int>()

launch {
    repeat(20) {
        channelA.send(1)
        channelB.send(2)
    }
}

// select (有偏见)
var countA = 0
repeat(10) {
    val result = select<Int> {
        channelA.onReceive { it }
        channelB.onReceive { it }
    }
    if (result == 1) countA++
}
println("Channel A selected: $countA times")  // 通常更多

// selectUnbiased (无偏见)
var countC = 0
var countD = 0
repeat(10) {
    val result = selectUnbiased<Int> {
        channelC.onReceive { it }
        channelD.onReceive { it }
    }
    if (result == 3) countC++ else countD++
}
println("Channel C: $countC, Channel D: $countD")  // 分布更均匀
```

### 相关 Activity

- [SelectExpressionActivity](../src/main/java/com/peter/coroutine/demo/channel/SelectExpressionActivity.kt)

---

## 关键点总结

1. **Channel 基础**: 协程间通信的管道，支持多种容量策略
2. **容量策略**: RENDEZVOUS(0)、BUFFERED(64)、UNLIMITED、CONFLATED
3. **produce 构建器**: 自动管理 Channel 生命周期的生产者
4. **多消费者**: 竞争消费，实现负载均衡
5. **select 表达式**: 多路复用，选择最先完成的操作

## 下一步

- [异常处理教程](04-error-handling.md) - 学习协程异常处理
- [Android 集成教程](05-android-integration.md) - 学习 Android 中的协程使用
