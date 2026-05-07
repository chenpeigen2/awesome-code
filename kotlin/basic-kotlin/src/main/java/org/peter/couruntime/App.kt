package org.peter.couruntime

import kotlinx.coroutines.CoroutineStart
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

fun main() {
    defaultDemo()
    lazyDemo()
    atomicDemo()
    undispatchedDemo()
    cancelBehaviorDemo()
}

private suspend fun intValue1(): Int {
    delay(1000)
    return 15
}

private suspend fun intValue2(): Int {
    delay(2000)
    return 20
}