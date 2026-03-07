# Kotlin Flow 教程

本文档详细介绍 Kotlin Flow 的核心概念，包括 Flow 基础、常用操作符、StateFlow 与 SharedFlow，以及冷流与热流的区别。

## 目录

1. [Flow 基础概念](#flow-基础概念)
2. [常用操作符](#常用操作符)
3. [StateFlow 与 SharedFlow](#stateflow-与-sharedflow)
4. [冷流与热流](#冷流与热流)

---

## Flow 基础概念

### 什么是 Flow？

Flow 是 Kotlin 协程中的数据流 API，用于处理异步数据流。它是一个可以顺序发出多个值的协程构建器，类似于 RxJava 的 Observable。

### Flow 的核心特性

#### 1. 冷流 (Cold Flow)

- Flow 是冷流，只有在被收集(collect)时才会执行
- 每次收集都会重新执行 Flow 的代码块
- 没有收集者时，Flow 不会产生任何数据

#### 2. 结构化并发

- Flow 遵循协程的结构化并发原则
- 当协程被取消时，Flow 也会自动取消

#### 3. 上下文保持

- Flow 的收集发生在调用者的协程上下文中
- 可以使用 `flowOn` 操作符切换上游流的执行上下文

### Flow 构建器

#### flow { }

最基本的构建器，可以使用 `emit()` 发送值。

```kotlin
fun createFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        emit(i)  // 发送值
        delay(100)
    }
}
```

#### flowOf()

从固定值创建 Flow。

```kotlin
val flow = flowOf(1, 2, 3, 4, 5)
```

#### asFlow()

将集合或序列转换为 Flow。

```kotlin
val flow = listOf(1, 2, 3).asFlow()
```

### 收集器

#### collect

终端操作符，收集 Flow 发出的所有值。

```kotlin
flow.collect { value ->
    println("Received: $value")
}
```

#### collectLatest

只收集最新的值，如果新值到来则取消之前的处理。

```kotlin
flow.collectLatest { value ->
    delay(100)  // 模拟耗时处理
    println("Processed: $value")
}
```

#### toList

将 Flow 收集为 List。

```kotlin
val list = flow.toList()
```

### flowOn 切换上下文

```kotlin
val ioFlow = flow {
    // 这个代码块在 IO 线程执行
    for (i in 1..3) {
        emit("Data-$i")
        delay(200)
    }
}.flowOn(Dispatchers.IO)

// collect 仍然在调用者的上下文中
ioFlow.collect { value ->
    // 这里在调用者的线程执行
}
```

### 相关 Activity

- [FlowBasicsActivity](../src/main/java/com/peter/coroutine/demo/flow/FlowBasicsActivity.kt)

---

## 常用操作符

### 转换操作符

#### map

转换每个发出的值。

```kotlin
flowOf(1, 2, 3, 4, 5)
    .map { it * it }  // 平方
    .collect { println(it) }  // 1, 4, 9, 16, 25
```

#### filter

过滤符合条件的值。

```kotlin
flowOf(1, 2, 3, 4, 5, 6)
    .filter { it % 2 == 0 }  // 只保留偶数
    .collect { println(it) }  // 2, 4, 6
```

#### transform

更灵活的转换，可以发射多个值或不发射。

```kotlin
flowOf(1, 2, 3)
    .transform { value ->
        emit("Start: $value")
        emit("Result: ${value * 10}")
    }
    .collect { println(it) }
// 输出: Start: 1, Result: 10, Start: 2, Result: 20, ...
```

### 组合操作符

#### zip

将两个 Flow 的值按顺序配对。

```kotlin
val flow1 = flowOf("A", "B", "C")
val flow2 = flowOf(1, 2, 3)

flow1.zip(flow2) { letter, number ->
    "$letter-$number"
}.collect { println(it) }  // A-1, B-2, C-3
```

#### combine

当任一 Flow 发出新值时，使用最新值重新计算。

```kotlin
val fastFlow = flow {
    emit(1)
    delay(100)
    emit(2)
    delay(100)
    emit(3)
}

val slowFlow = flow {
    emit("X")
    delay(150)
    emit("Y")
}

fastFlow.combine(slowFlow) { num, letter ->
    "$num + $letter"
}.collect { println(it) }
// 输出: 1+X, 1+Y, 2+Y, 3+Y
```

### 扁平化操作符

#### flatMapConcat

顺序处理内部 Flow。

```kotlin
flowOf(1, 2, 3)
    .flatMapConcat { value ->
        flow {
            emit("$value-A")
            delay(100)
            emit("$value-B")
        }
    }
    .collect { println(it) }
// 输出: 1-A, 1-B, 2-A, 2-B, 3-A, 3-B
```

#### flatMapMerge

并行处理内部 Flow。

```kotlin
flowOf(1, 2, 3)
    .flatMapMerge { value ->
        flow {
            delay(300 - value * 100L)
            emit("Task $value done")
        }
    }
    .collect { println(it) }
// 输出顺序可能不同，因为并行执行
```

#### flatMapLatest

只处理最新的内部 Flow。

```kotlin
flowOf(1, 2, 3)
    .flatMapLatest { value ->
        flow {
            delay(200)
            emit("Result $value")
        }
    }
    .collect { println(it) }
// 可能只输出: Result 3
```

### 回调操作符

```kotlin
flow {
    emit(1)
    emit(2)
    emit(3)
}
    .onStart {
        println("Flow started")
    }
    .onEach { value ->
        println("Processing: $value")
    }
    .onCompletion { cause ->
        if (cause == null) {
            println("Flow completed normally")
        } else {
            println("Flow completed with error: $cause")
        }
    }
    .collect { value ->
        println("Collected: $value")
    }
```

### 异常处理

```kotlin
flow {
    emit(1)
    throw RuntimeException("Error!")
}
    .catch { e ->
        println("Caught: ${e.message}")
        emit(-1)  // 可以发射一个默认值
    }
    .collect { println(it) }
// 输出: 1, Caught: Error!, -1
```

### 相关 Activity

- [FlowOperatorsActivity](../src/main/java/com/peter/coroutine/demo/flow/FlowOperatorsActivity.kt)

---

## StateFlow 与 SharedFlow

### StateFlow

StateFlow 是一个热流（Hot Flow），它始终有一个值，并且可以向多个收集者广播状态更新。它是 LiveData 的协程替代品。

#### 基本使用

```kotlin
class MyViewModel : ViewModel() {
    // 私有可变状态
    private val _state = MutableStateFlow("")
    // 公开不可变状态
    val state: StateFlow<String> = _state.asStateFlow()

    fun updateState(newValue: String) {
        _state.value = newValue
    }

    fun incrementCounter() {
        _state.update { it + 1 }  // 原子更新
    }
}
```

#### StateFlow vs LiveData

| 特性 | StateFlow | LiveData |
|------|-----------|----------|
| 初始值 | 必须提供 | 可选 |
| 线程安全 | 是 | 是 |
| 背压处理 | 支持 | 不支持 |
| 操作符 | 丰富的 Flow 操作符 | 有限的转换 |
| 生命周期感知 | 需要配合 repeatOnLifecycle | 内置支持 |
| 多收集者 | 支持 | 支持 |

### SharedFlow

SharedFlow 是一个可以向多个收集者广播事件的热流。

#### 基本使用

```kotlin
// 创建 SharedFlow
private val _events = MutableSharedFlow<String>(
    replay = 2,  // 新订阅者能收到的历史值数量
    extraBufferCapacity = 0,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
val events: SharedFlow<String> = _events.asSharedFlow()

// 发送事件
_events.emit("Event 1")

// 收集事件
events.collect { event ->
    println("Received: $event")
}
```

#### replay 参数

```kotlin
// replay = 0: 新订阅者收不到历史值
val noReplay = MutableSharedFlow<Int>(replay = 0)

// replay = 1: 新订阅者收到最近1个值
val replay1 = MutableSharedFlow<Int>(replay = 1)

// replay = 3: 新订阅者收到最近3个值
val replay3 = MutableSharedFlow<Int>(replay = 3)
```

### StateFlow vs SharedFlow

| 特性 | StateFlow | SharedFlow |
|------|-----------|------------|
| 初始值 | 必须有 | 可选 |
| replay | 固定为 1 | 可配置 |
| value 属性 | 有 | 没有 |
| 使用场景 | 状态管理 | 事件流 |

### 相关 Activity

- [StateFlowActivity](../src/main/java/com/peter/coroutine/demo/flow/StateFlowActivity.kt)

---

## 冷流与热流

### 冷流 (Cold Flow) - Flow

#### 特点

- 只有被收集时才会开始执行
- 每次收集都会创建一个新的流实例
- 不同收集者之间互不影响
- 数据从生产者到消费者是点对点的

#### 使用场景

- 网络请求
- 数据库查询
- 文件读取

```kotlin
val coldFlow = flow {
    println("Flow started")  // 每次收集都会打印
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

// 第一次收集
coldFlow.collect { println("Collector 1: $it") }
// 第二次收集 - 会重新执行
coldFlow.collect { println("Collector 2: $it") }
```

### 热流 (Hot Flow) - StateFlow / SharedFlow

#### 特点

- 无论是否有收集者，都会持续产生数据
- 多个收集者共享同一个流
- 新的收集者会收到当前/最新的值
- 可以在多个协程间广播数据

#### 使用场景

- UI 状态管理
- 事件广播
- 实时数据更新

```kotlin
val sharedFlow = MutableSharedFlow<Int>(replay = 1)

// 发送数据（即使没有收集者）
sharedFlow.emit(1)
sharedFlow.emit(2)

// 收集者稍后订阅，会收到 replay 缓存的值
sharedFlow.collect { println("Received: $it") }  // 收到 2
```

### 对比总结

| 特性 | 冷流 (Flow) | 热流 (StateFlow/SharedFlow) |
|------|-------------|------------------------------|
| 执行时机 | 被收集时 | 立即执行 |
| 多收集者 | 各自独立 | 共享数据 |
| 数据共享 | 不共享 | 共享 |
| 使用场景 | 一次性操作 | 持续状态/事件 |

### 相关 Activity

- [ColdHotFlowActivity](../src/main/java/com/peter/coroutine/demo/flow/ColdHotFlowActivity.kt)

---

## 关键点总结

1. **Flow 基础**: 冷流特性，flow 构建器，collect 收集
2. **操作符**: map/filter/transform 转换，zip/combine 组合，flatMap 扁平化
3. **StateFlow**: 状态管理，必须有初始值，替代 LiveData
4. **SharedFlow**: 事件流，replay 可配置，适合广播
5. **冷热流**: 冷流按需执行，热流持续执行

## 下一步

- [Channel 教程](03-channel.md) - 学习协程间通信
- [Android 集成教程](05-android-integration.md) - 学习 Android 中的 Flow 最佳实践
