# 协程异常处理教程

本文档详细介绍 Kotlin 协程中的异常处理机制，包括 try/catch 使用、CoroutineExceptionHandler 和 SupervisorJob。

## 目录

1. [try/catch 使用](#trycatch-使用)
2. [CoroutineExceptionHandler](#coroutineexceptionhandler)
3. [SupervisorJob](#supervisorjob)

---

## try/catch 使用

### 协程异常处理的核心概念

#### launch 的异常处理

- launch 中未捕获的异常会立即传播到父协程
- 可以直接在协程体内使用 try/catch
- 异常如果不处理会导致整个协程树取消

#### async 的异常处理

- async 的异常不会立即抛出，而是存储在 Deferred 中
- 只有在调用 `await()` 时才会抛出异常
- 可以在 `await()` 处使用 try/catch 捕获

### launch 中使用 try/catch

```kotlin
launch {
    try {
        delay(100)
        throw RuntimeException("Error!")
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
    // 协程继续执行
    println("Continuing after catch")
}
```

### async 在 await 时捕获

```kotlin
val deferred: Deferred<String> = async {
    delay(100)
    throw RuntimeException("Error!")
}

try {
    val result = deferred.await()
} catch (e: Exception) {
    println("Caught at await: ${e.message}")
}
```

### try/catch 的限制

重要：在 launch/async 外部包裹 try/catch 是无效的！因为协程是异步执行的，try 块在协程开始前就结束了。

```kotlin
// 错误示例 - 无法捕获
try {
    launch {
        delay(100)
        throw RuntimeException("Error!")  // 这个异常无法被外部捕获
    }
} catch (e: Exception) {
    // 这里永远不会被调用！
}

// 正确示例 - 在协程内部捕获
launch {
    try {
        delay(100)
        throw RuntimeException("Error!")
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
```

### 最佳实践

| 场景 | 推荐方式 |
|------|----------|
| launch | 在协程内部使用 try/catch |
| async | 在 await() 处使用 try/catch |
| 全局异常 | 使用 CoroutineExceptionHandler |

### 相关 Activity

- [TryCatchActivity](../src/main/java/com/peter/coroutine/demo/errorhandling/TryCatchActivity.kt)

---

## CoroutineExceptionHandler

### 什么是 CoroutineExceptionHandler？

`CoroutineExceptionHandler` 是协程上下文的一个元素，用于处理协程中未捕获的异常。它类似于线程的 `UncaughtExceptionHandler`。

### 基本使用

```kotlin
val exceptionHandler = CoroutineExceptionHandler { context, exception ->
    println("Caught exception: ${exception.message}")
    println("Context: $context")
}

// 在协程上下文中使用
scope.launch(exceptionHandler) {
    throw RuntimeException("Error!")
}
```

### 异常传播机制

#### 自动传播的异常

- launch 中未捕获的异常会自动传播
- 会取消父协程和兄弟协程
- 最终由 CoroutineExceptionHandler 处理

#### 暴露给用户的异常

- async 的异常通过 `await()` 暴露
- 需要显式处理，不会触发 CoroutineExceptionHandler

### 重要限制

CoroutineExceptionHandler 只能处理从协程内部自动传播出来的异常：

1. 只对 launch 有效
2. async 的异常需要通过 `await()` 处理
3. 必须作为根协程或 SupervisorJob 子协程的上下文

### 示例：处理 launch 异常

```kotlin
val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("Global handler caught: ${exception.message}")
}

scope.launch(exceptionHandler) {
    launch {
        delay(100)
        throw RuntimeException("Child error!")
    }
}
// 异常会传播到根协程，被 handler 捕获
```

### 示例：async 异常不触发 handler

```kotlin
val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("This won't be called for async")
}

scope.launch(exceptionHandler) {
    val deferred = async {
        throw RuntimeException("Async error!")
    }

    try {
        deferred.await()  // 在这里捕获
    } catch (e: Exception) {
        println("Caught at await: ${e.message}")
    }
}
```

### 使用场景

- 记录日志
- 向用户显示错误信息
- 错误监控和上报

### 相关 Activity

- [ExceptionHandlerActivity](../src/main/java/com/peter/coroutine/demo/errorhandling/ExceptionHandlerActivity.kt)

---

## SupervisorJob

### Job vs SupervisorJob

#### 普通 Job 的行为

- 子协程失败会取消父协程
- 父协程取消会取消所有子协程
- 一个子协程失败会影响其他子协程

#### SupervisorJob 的行为

- 子协程失败不会取消父协程
- 子协程失败不会影响其他子协程
- 父协程取消仍会取消所有子协程

### 使用 SupervisorJob

```kotlin
// 创建 SupervisorJob
val supervisorJob = SupervisorJob()
val scope = CoroutineScope(supervisorJob + Dispatchers.Default)

// 或者使用 MainScope (已包含 SupervisorJob)
val mainScope = MainScope()

scope.launch {
    // 子协程 1
    launch {
        delay(100)
        throw RuntimeException("Child 1 failed!")
    }

    // 子协程 2 - 不受影响
    launch {
        delay(200)
        println("Child 2 completed!")
    }
}
```

### supervisorScope

`supervisorScope` 创建一个临时的独立失败作用域。

```kotlin
scope.launch {
    supervisorScope {
        launch {
            delay(100)
            throw RuntimeException("Task A failed!")
        }

        launch {
            delay(200)
            println("Task B completed!")  // 仍然执行
        }
    }
    println("supervisorScope completed")
}
```

### 配合 CoroutineExceptionHandler

SupervisorJob 中的子协程需要自己处理异常。

```kotlin
val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("Caught: ${exception.message}")
}

val scope = CoroutineScope(SupervisorJob() + exceptionHandler)

scope.launch {
    throw RuntimeException("Error 1")  // 被 handler 捕获
}

scope.launch {
    delay(100)
    println("Still running")  // 不受影响
}
```

### 对比示例

#### 普通 Job

```kotlin
val job = Job()
val scope = CoroutineScope(job)

scope.launch {
    launch {
        delay(100)
        throw RuntimeException("Child failed!")
    }

    launch {
        delay(200)
        println("This won't print")  // 被取消
    }
}
```

#### SupervisorJob

```kotlin
val supervisorJob = SupervisorJob()
val scope = CoroutineScope(supervisorJob)

scope.launch {
    launch {
        delay(100)
        throw RuntimeException("Child failed!")
    }

    launch {
        delay(200)
        println("This will print!")  // 不受影响
    }
}
```

### 使用场景

- 多个独立网络请求（一个失败不影响其他）
- 并行数据处理任务
- UI 组件的独立更新

### 相关 Activity

- [SupervisorJobActivity](../src/main/java/com/peter/coroutine/demo/errorhandling/SupervisorJobActivity.kt)

---

## 关键点总结

1. **try/catch**: launch 在内部捕获，async 在 await 捕获
2. **外部 try/catch 无效**: 协程是异步执行的
3. **CoroutineExceptionHandler**: 处理 launch 的未捕获异常
4. **async 不触发 handler**: 需要在 await 处理
5. **SupervisorJob**: 子协程独立失败，不影响其他

## 异常处理决策表

| 场景 | 推荐方式 |
|------|----------|
| launch 内部可预知异常 | 内部 try/catch |
| async 返回值异常 | await 处 try/catch |
| launch 全局异常监控 | CoroutineExceptionHandler |
| 多个独立任务 | SupervisorJob + handler |
| 临时独立作用域 | supervisorScope |

## 下一步

- [Android 集成教程](05-android-integration.md) - 学习 Android 中的协程使用
- [进阶原理教程](06-advanced.md) - 深入理解协程原理
