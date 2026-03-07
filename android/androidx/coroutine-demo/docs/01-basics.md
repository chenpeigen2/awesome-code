# 协程基础

本文档详细介绍 Kotlin 协程的基础概念，包括 suspend 函数、协程构建器、调度器、Job 和 CoroutineScope。

## 目录

1. [suspend 函数详解](#suspend-函数详解)
2. [launch vs async](#launch-vs-async)
3. [Dispatchers 调度器](#dispatchers-调度器)
4. [Job 与协程控制](#job-与协程控制)
5. [CoroutineScope 作用域](#coroutinescope-作用域)

---

## suspend 函数详解

### 什么是 suspend 函数？

`suspend` 函数是一种可以在不阻塞线程的情况下挂起执行的函数。当协程执行到 suspend 函数时，它会保存当前状态并释放线程，等待操作完成后恢复执行。

### 核心概念

- **挂起 (Suspend)**: 协程暂停执行，释放线程资源
- **恢复 (Resume)**: 挂起操作完成后，协程从挂起点继续执行
- **非阻塞**: 挂起期间线程可以做其他工作，不会被阻塞

### 挂起函数 vs 阻塞函数

```kotlin
// 阻塞函数 - 阻塞当前线程
fun blockingFunction() {
    Thread.sleep(1000)  // 线程被阻塞，无法执行其他任务
}

// 挂起函数 - 不阻塞线程
suspend fun suspendingFunction() {
    delay(1000)  // 协程挂起，线程可以执行其他任务
}
```

### 示例代码

```kotlin
// 自定义 suspend 函数
private suspend fun fetchDataFromNetwork(): String {
    // 切换到 IO 调度器执行耗时操作
    return withContext(Dispatchers.IO) {
        delay(800)  // 模拟网络延迟
        "{'data': 'Hello from server!'}"
    }
}
```

### 关键点总结

1. `suspend` 关键字标记的函数只能在协程或其他 suspend 函数中调用
2. `delay()` 是挂起函数，`Thread.sleep()` 是阻塞函数
3. suspend 函数通过 Continuation 机制实现挂起和恢复
4. 编译器会将 suspend 函数转换为状态机

### 相关 Activity

- [SuspendFunctionActivity](../src/main/java/com/peter/coroutine/demo/basics/SuspendFunctionActivity.kt)

---

## launch vs async

### launch - 启动协程（"发射后不管"）

`launch` 启动一个新协程，返回 `Job` 对象。适用于执行不需要返回结果的任务。

```kotlin
val job: Job = scope.launch {
    // 执行任务
    delay(1000)
    println("Task completed")
}

// Job 可以用于控制协程
job.cancel()  // 取消协程
job.join()    // 等待协程完成
```

### async - 异步计算（"启动并等待结果"）

`async` 启动一个协程并返回 `Deferred<T>`，通过 `await()` 获取结果。

```kotlin
val deferred: Deferred<String> = scope.async {
    delay(1000)
    return@async "Result"
}

// 获取结果
val result = deferred.await()  // 挂起直到结果可用
```

### 并行执行示例

```kotlin
suspend fun parallelExecution() = coroutineScope {
    val startTime = System.currentTimeMillis()

    // 同时启动三个异步任务
    val deferred1 = async {
        delay(800)
        "Result-1"
    }

    val deferred2 = async {
        delay(600)
        "Result-2"
    }

    val deferred3 = async {
        delay(1000)
        "Result-3"
    }

    // 等待所有结果
    val result1 = deferred1.await()
    val result2 = deferred2.await()
    val result3 = deferred3.await()

    val totalTime = System.currentTimeMillis() - startTime
    // 总耗时约 1000ms，而非 2400ms
}
```

### 选择原则

| 场景 | 推荐使用 |
|------|----------|
| 需要返回值 | async |
| 只需执行任务 | launch |
| 并行计算 | async |
| UI 更新/事件处理 | launch |

### 相关 Activity

- [LaunchAsyncActivity](../src/main/java/com/peter/coroutine/demo/basics/LaunchAsyncActivity.kt)

---

## Dispatchers 调度器

### 什么是调度器？

调度器决定了协程在哪个线程或线程池上执行，是 `CoroutineContext` 的重要组成部分。

### 四种主要调度器

#### 1. Dispatchers.Default

- **用途**: CPU 密集型任务
- **特点**: 使用共享的后台线程池，线程数等于 CPU 核心数
- **适用场景**: 排序、过滤、计算等

```kotlin
launch(Dispatchers.Default) {
    // CPU 密集型计算
    val result = computeFibonacci(20)
}
```

#### 2. Dispatchers.IO

- **用途**: I/O 密集型任务
- **特点**: 按需创建线程，弹性扩展
- **适用场景**: 网络请求、文件读写、数据库操作

```kotlin
launch(Dispatchers.IO) {
    // I/O 操作
    val data = readFromFile()
}
```

#### 3. Dispatchers.Main

- **用途**: UI 操作
- **特点**: 通常只包含一个线程（主线程）
- **适用场景**: 更新 UI、处理用户交互

```kotlin
launch(Dispatchers.Main) {
    // UI 更新
    textView.text = "Updated"
}
```

#### 4. Dispatchers.Unconfined

- **用途**: 特殊场景
- **特点**: 在调用者线程启动，挂起后在恢复线程继续
- **注意**: 一般不推荐使用

### withContext 切换调度器

```kotlin
suspend fun fetchAndDisplay() {
    // 在 Main 开始
    log("Main: ${Thread.currentThread().name}")

    // 切换到 IO 执行网络请求
    val data = withContext(Dispatchers.IO) {
        log("IO: ${Thread.currentThread().name}")
        fetchData()
    }

    // 自动回到 Main
    log("Main: ${Thread.currentThread().name}")
    displayData(data)
}
```

### 调度器选择指南

| 任务类型 | 推荐调度器 |
|----------|------------|
| 网络请求 | Dispatchers.IO |
| 数据库操作 | Dispatchers.IO |
| 文件读写 | Dispatchers.IO |
| JSON 解析 | Dispatchers.Default |
| 图片处理 | Dispatchers.Default |
| UI 更新 | Dispatchers.Main |

### 相关 Activity

- [DispatchersActivity](../src/main/java/com/peter/coroutine/demo/basics/DispatchersActivity.kt)

---

## Job 与协程控制

### 什么是 Job？

`Job` 代表一个协程的句柄，用于控制协程的生命周期。每个 `launch` 返回一个 `Job`，`async` 返回 `Deferred`（继承自 `Job`）。

### Job 的状态

```
状态流转:
  New → Active → Completing → Completed
                ↘ Cancelling → Cancelled
```

状态查询属性：
- `isActive`: 是否处于活跃状态
- `isCancelled`: 是否已取消
- `isCompleted`: 是否已完成

### Job 的控制方法

#### cancel()

取消协程的执行。协程会在下一个挂起点检查取消状态。

```kotlin
val job = scope.launch {
    while (isActive) {  // 检查协程是否仍然活跃
        delay(500)
        // 执行任务
    }
}

job.cancel()  // 请求取消
```

#### join()

挂起当前协程，等待目标 Job 完成。

```kotlin
val job = scope.launch {
    delay(1000)
    println("Job completed")
}

job.join()  // 挂起直到 job 完成
println("After join")
```

#### cancelAndJoin()

组合操作：取消后等待完成。

```kotlin
job.cancelAndJoin()  // 取消并等待
```

### 取消的协作性

协程取消是协作式的，代码需要主动检查取消状态：

```kotlin
val job = launch {
    while (isActive) {  // 方式1: 使用 isActive 检查
        // 执行任务
    }
}

launch {
    repeat(1000) { i ->
        ensureActive()  // 方式2: 使用 ensureActive()
        // 执行任务
    }
}

launch {
    delay(100)  // 方式3: delay() 等挂起函数会自动检查
}
```

### 相关 Activity

- [JobActivity](../src/main/java/com/peter/coroutine/demo/basics/JobActivity.kt)

---

## CoroutineScope 作用域

### 什么是 CoroutineScope？

`CoroutineScope` 定义了协程的生命周期范围。它包含一个 `CoroutineContext`，决定了协程的执行行为。每个协程必须在一个 Scope 中启动，当 Scope 被取消时，其中的所有协程也会被取消——这就是**结构化并发**。

### 结构化并发原则

1. 每个协程有一个父级（除了根协程）
2. 父协程会等待所有子协程完成
3. 取消父协程会取消所有子协程
4. 子协程的失败会取消父协程（默认行为）

### 常用的 Scope

#### lifecycleScope

Android Lifecycle 相关的 Scope，Activity/Fragment 销毁时自动取消。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Activity 销毁时自动取消
        }
    }
}
```

#### viewModelScope

ViewModel 相关的 Scope，ViewModel 清除时自动取消。

```kotlin
class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // ViewModel 清除时自动取消
        }
    }
}
```

#### GlobalScope

全局 Scope，不推荐使用，因为它的生命周期是整个应用程序。

```kotlin
// 不推荐
GlobalScope.launch {
    // 永远不会自动取消
}
```

### 自定义 Scope

```kotlin
// 使用普通 Job
val customScope = CoroutineScope(Dispatchers.Default + Job())

// 使用 SupervisorJob（子协程失败不影响其他）
val supervisorScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

// 记得在适当时机取消
customScope.cancel()
```

### SupervisorJob

使用 `SupervisorJob` 时，子协程的失败不会影响其他子协程。

```kotlin
val supervisorScope = CoroutineScope(SupervisorJob())

supervisorScope.launch {
    throw RuntimeException("Failed")  // 这个失败
}

supervisorScope.launch {
    delay(100)
    println("I'm still running")  // 这个继续执行
}
```

### 相关 Activity

- [CoroutineScopeActivity](../src/main/java/com/peter/coroutine/demo/basics/CoroutineScopeActivity.kt)

---

## 关键点总结

1. **suspend 函数**: 非阻塞挂起，通过 Continuation 实现
2. **launch vs async**: launch 返回 Job，async 返回 Deferred
3. **Dispatchers**: 选择合适的调度器，使用 withContext 切换
4. **Job**: 控制协程生命周期，取消是协作式的
5. **CoroutineScope**: 结构化并发，自动取消子协程

## 下一步

- [Flow 教程](02-flow.md) - 学习 Kotlin Flow 数据流
- [Channel 教程](03-channel.md) - 学习协程间通信
- [异常处理教程](04-error-handling.md) - 学习协程异常处理
