package org.peter.couruntime

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/*
    挂起函数的组合
 */

// ==================== DEFAULT ====================
// 立即调度，执行前可取消
private fun defaultDemo() = runBlocking {
    println("=== DEFAULT: 立即调度 ===")
    val elapsedTime = measureTimeMillis {
        val value1 = async { intValue1() }
        val value2 = async { intValue2() }

        val result1 = value1.await()
        val result2 = value2.await()

        println("$result1 + $result2 = ${result1 + result2}")
    }
    println("total time: $elapsedTime ms\n")
}

// ==================== LAZY ====================
// 延迟启动：只创建不调度，调用 start()/join()/await() 时才执行
private fun lazyDemo() = runBlocking {
    println("=== LAZY: 延迟启动 ===")
    val job: Job = launch(start = CoroutineStart.LAZY) {
        println("LAZY 协程开始执行")
        delay(500)
        println("LAZY 协程执行完毕")
    }
    println("协程已创建，但还未启动 (isActive=${job.isActive})")
    delay(100)
    println("调用 job.start() 启动协程...")
    job.start()
    job.join()
    println()
}

// ==================== ATOMIC ====================
// 原子启动：保证协程体一定会执行，即使启动前已被取消
private fun atomicDemo() = runBlocking {
    println("=== ATOMIC: 原子启动（不可取消） ===")
    val job: Job = launch(start = CoroutineStart.ATOMIC) {
        println("ATOMIC 协程已进入，isActive=$isActive")
        try {
            delay(500)
            println("这行不会执行，因为协程已被取消")
        } catch (e: Exception) {
            println("delay 抛出异常: ${e.message}")
        }
    }
    job.cancel() // 立即取消，但 ATOMIC 保证协程体仍然会进入
    job.join()
    println("ATOMIC 协程结束\n")
}

// ==================== UNDISPATCHED ====================
// 立即在当前线程执行到第一个挂起点，之后恢复走 Dispatcher
private fun undispatchedDemo() = runBlocking {
    println("=== UNDISPATCHED: 立即执行到第一个挂起点 ===")
    val job = launch(start = CoroutineStart.UNDISPATCHED) {
        println("1. UNDISPATCHED 协程立即执行（当前线程）")
        delay(500)
        println("3. 挂起恢复后，走 Dispatcher 调度执行")
    }
    println("2. 主协程继续执行（UNDISPATCHED 协程已挂起）")
    job.join()
    println()
}

// ==================== 取消行为对比 ====================
private fun cancelBehaviorDemo() = runBlocking {
    println("=== 取消行为对比 ===\n")

    // ---- DEFAULT: 竞态，可能不进入 ----
    println("--- DEFAULT: 进入体前取消 → 不执行 ---")
    runBlocking {
        val job = launch(start = CoroutineStart.DEFAULT) {
            println("  [DEFAULT] 协程体进入了")
        }
        job.cancelAndJoin()
        println("  结果: isCancelled=${job.isCancelled}\n")
    }

    // ---- DEFAULT: 进入体后，执行到挂起点才停 ----
    println("--- DEFAULT: 进入体后取消 → 执行到挂起点 ---")
    runBlocking {
        val job = launch {
            println("  [1] 进入协程体")
            println("  [2] 非挂起代码，cancel 拦不住")
            Thread.sleep(50)  // 模拟非挂起耗时，cancel 拦不住
            println("  [3] 仍然在执行")
            delay(1)          // ← 挂起点，检测到取消，抛异常
            println("  [4] 这行不会执行")
        }
        delay(10)  // 等 10ms 让协程进入体
        job.cancelAndJoin()
        println("  结果: isCancelled=${job.isCancelled}\n")
    }

    // ---- DEFAULT: finally 保证资源释放 ----
    println("--- DEFAULT: try-finally 保证清理 ---")
    runBlocking {
        val job = launch {
            try {
                println("  [1] 获取资源")
                delay(1000)  // ← 挂起点
                println("  [2] 不会执行")
            } finally {
                println("  [3] finally: 释放资源（一定会执行）")
            }
        }
        delay(10)
        job.cancelAndJoin()
        println("  结果: isCancelled=${job.isCancelled}\n")
    }

    // ---- LAZY: 未启动就取消 ----
    println("--- LAZY: 未启动直接取消 ---")
    runBlocking {
        val job = launch(start = CoroutineStart.LAZY) {
            println("  [LAZY] 这行不会打印")
        }
        // 从未 start，直接 cancel
        job.cancelAndJoin()
        println("  结果: isCancelled=${job.isCancelled}, isCompleted=${job.isCompleted}\n")
    }

    // ---- ATOMIC: 取消也要进入体 ----
    println("--- ATOMIC: 即使取消也进入协程体 ---")
    runBlocking {
        val job = launch(start = CoroutineStart.ATOMIC) {
            println("  [1] 进入协程体 (isActive=$isActive)")
            try {
                delay(1)  // ← 检测到取消
                println("  [2] 不会执行")
            } catch (e: Exception) {
                println("  [2] delay 抛出: ${e.javaClass.simpleName}")
            } finally {
                println("  [3] finally: 清理资源")
            }
        }
        job.cancelAndJoin()
        println("  结果: isCancelled=${job.isCancelled}\n")
    }

    // ---- UNDISPATCHED: 立即执行到挂起点 ----
    println("--- UNDISPATCHED: 当前线程立即执行到挂起点 ---")
    runBlocking {
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            println("  [1] 立即执行（isActive=$isActive）")
            try {
                delay(1)
                println("  [2] 不会执行")
            } finally {
                println("  [3] finally: 清理资源")
            }
        }
        job.cancelAndJoin()
        println("  结果: isCancelled=${job.isCancelled}\n")
    }

    // ---- 对比: 资源泄漏 vs 安全释放 ----
    println("--- 实战对比: 模拟 Mutex 泄漏 ---")
    runBlocking {
        var unlocked = false
        // DEFAULT: cancel 后 unlock 可能不执行
        val job1 = launch(start = CoroutineStart.DEFAULT) {
            try {
                delay(1000)
            } finally {
                unlocked = true  // 模拟 unlock
                println("  [DEFAULT] finally 执行了")
            }
        }
        job1.cancelAndJoin()
        println("  [DEFAULT] unlocked=$unlocked\n")
    }

    runBlocking {
        var unlocked = false
        // ATOMIC: 保证 finally 执行
        val job2 = launch(start = CoroutineStart.ATOMIC) {
            try {
                delay(1000)
            } finally {
                unlocked = true
                println("  [ATOMIC] finally 执行了")
            }
        }
        job2.cancelAndJoin()
        println("  [ATOMIC] unlocked=$unlocked\n")
    }
}

// ==================== Dispatcher 线程切换对比 ====================
// 展示每种 Start 模式在不同 Dispatcher 下的线程行为
private fun dispatcherDemo() = runBlocking {
    println("=== Dispatcher 线程切换对比 ===\n")
    val mainThread = Thread.currentThread().name

    // ---- DEFAULT + Default Dispatcher ----
    println("--- DEFAULT + Dispatchers.Default ---")
    val job1 = launch(Dispatchers.Default, start = CoroutineStart.DEFAULT) {
        println("  协程体线程: ${Thread.currentThread().name}")
    }
    job1.join()

    // ---- DEFAULT + Unconfined ----
    println("--- DEFAULT + Dispatchers.Unconfined ---")
    val job2 = launch(Dispatchers.Unconfined, start = CoroutineStart.DEFAULT) {
        println("  协程体线程: ${Thread.currentThread().name}")
    }
    job2.join()

    // ---- LAZY + Default（触发时才调度到 Default 线程）----
    println("--- LAZY + Dispatchers.Default ---")
    val job3 = launch(Dispatchers.Default, start = CoroutineStart.LAZY) {
        println("  协程体线程: ${Thread.currentThread().name}")
    }
    println("  start 前线程: $mainThread")
    job3.start()
    job3.join()

    // ---- ATOMIC + Default（保证在 Default 线程执行）----
    println("--- ATOMIC + Dispatchers.Default ---")
    val job4 = launch(Dispatchers.Default, start = CoroutineStart.ATOMIC) {
        println("  协程体线程: ${Thread.currentThread().name} (isActive=$isActive)")
    }
    job4.cancel()
    job4.join()

    // ---- UNDISPATCHED + Default（关键区别！）----
    println("--- UNDISPATCHED + Dispatchers.Default ---")
    println("  主线程: $mainThread")
    val job5 = launch(Dispatchers.Default, start = CoroutineStart.UNDISPATCHED) {
        println("  第一段线程: ${Thread.currentThread().name}  ← 当前线程，忽略 Dispatcher")
        delay(1)
        println("  第二段线程: ${Thread.currentThread().name}  ← 挂起恢复后，走 Dispatcher")
    }
    println("  UNDISPATCHED 不等协程执行完就返回了")
    job5.join()

    // ---- UNDISPATCHED + Unconfined 对比 ----
    println("\n--- UNDISPATCHED vs Unconfined ---")
    println("  UNDISPATCHED: 首段当前线程，恢复后走 Dispatcher")
    val job6 = launch(Dispatchers.Default, start = CoroutineStart.UNDISPATCHED) {
        print("  UNDISPATCHED 首段: ${threadTag()} → ")
        delay(1)
        println("恢复: ${threadTag()}")
    }
    println("  Unconfined: 首段当前线程，恢复后也走提交线程")
    val job7 = launch(Dispatchers.Unconfined) {
        print("  Unconfined 首段: ${threadTag()} → ")
        delay(1)
        println("恢复: ${threadTag()}")
    }
    job6.join()
    job7.join()

    // ---- DEFAULT + IO（实际场景）----
    println("\n--- 实战: DEFAULT vs UNDISPATCHED + Dispatchers.IO ---")
    val job8 = launch(Dispatchers.IO, start = CoroutineStart.DEFAULT) {
        println("  [DEFAULT] 全程在: ${threadTag()}")
    }
    val job9 = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
        println("  [UNDISPATCHED] 首段: ${threadTag()} (runBlocking 主线程)")
        delay(1)
        println("  [UNDISPATCHED] 恢复: ${threadTag()} (IO 线程)")
    }
    job8.join()
    job9.join()
    println()
}

/** 返回线程标签，用于区分 Default/IO/Main 线程 */
private fun threadTag(): String {
    val name = Thread.currentThread().name
    return when {
        name.contains("DefaultDispatcher") -> "Default($name)"
        name.contains("IO") -> "IO($name)"
        name.contains("main") -> "main($name)"
        else -> name
    }
}

fun main() {
    defaultDemo()
    lazyDemo()
    atomicDemo()
    undispatchedDemo()
    cancelBehaviorDemo()
    dispatcherDemo()
}

private suspend fun intValue1(): Int {
    delay(1000)
    return 15
}

private suspend fun intValue2(): Int {
    delay(2000)
    return 20
}