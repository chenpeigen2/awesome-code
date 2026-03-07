package com.peter.coroutine.demo.advanced

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 状态机原理演示
 *
 * 本 Activity 展示协程编译后生成的状态机代码结构
 *
 * ## 协程状态机概述
 * Kotlin 编译器会将每个 suspend 函数转换为一个状态机，使用 label（标签）
 * 来标记不同的执行状态，实现挂起和恢复的逻辑。
 *
 * ## 状态机核心组件：
 * 1. **label**: 状态标记，记录当前执行到哪个挂起点
 * 2. **局部变量**: 保存挂起前的局部变量，用于恢复
 * 3. **Continuation**: 保存和恢复状态的容器
 *
 * ## 编译器转换示例
 *
 * 原始协程代码：
 * ```kotlin
 * suspend fun example() {
 *     val a = step1()      // label 0 -> 1
 *     val b = step2()      // label 1 -> 2
 *     val c = step3()      // label 2 -> 3
 *     return a + b + c
 * }
 * ```
 *
 * 编译后状态机（简化）：
 * ```kotlin
 * fun example(cont: Continuation): Any? {
 *     // 状态机类
 *     class StateMachine(cont: Continuation) : ContinuationImpl(cont) {
 *         int label = 0
 *         Object a, b, c
 *
 *         fun doResume(Object result) {
 *             switch(label) {
 *                 case 0: {
 *                     label = 1
 *                     a = step1(this)  // 传入 this 作为 Continuation
 *                     if (a == COROUTINE_SUSPENDED) return
 *                 }
 *                 case 1: {
 *                     label = 2
 *                     b = step2(this)
 *                     if (b == COROUTINE_SUSPENDED) return
 *                 }
 *                 case 2: {
 *                     label = 3
 *                     c = step3(this)
 *                     if (c == COROUTINE_SUSPENDED) return
 *                 }
 *                 case 3: {
 *                     return (Int)a + (Int)b + (Int)c
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @see kotlin.coroutines.Continuation
 */
class StateMachineActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.state_machine)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateStateMachine()
    }

    /**
     * 演示状态机的工作原理
     */
    private fun demonstrateStateMachine() {
        log("========== 协程状态机原理演示 ==========\n")

        log("--- 演示1: 简单协程的状态转换 ---\n")

        lifecycleScope.launch {
            // 状态 0: 初始状态
            log("状态 0: 协程开始执行")
            log("  label = 0 -> 即将执行第一个挂起点\n")

            val step1Result = stepOne()
            // 状态 1: 第一个挂起点后
            log("状态 1: 从第一个挂起点恢复")
            log("  label = 1 -> step1() 返回: $step1Result")
            log("  保存的局部变量: step1Result = $step1Result\n")

            val step2Result = stepTwo()
            // 状态 2: 第二个挂起点后
            log("状态 2: 从第二个挂起点恢复")
            log("  label = 2 -> step2() 返回: $step2Result")
            log("  保存的局部变量: step1Result = $step1Result, step2Result = $step2Result\n")

            val step3Result = stepThree()
            // 状态 3: 最终状态
            log("状态 3: 协程完成")
            log("  label = 3 -> step3() 返回: $step3Result")
            log("  最终结果: $step1Result + $step2Result + $step3Result = ${step1Result + step2Result + step3Result}\n")

            log("--- 演示2: 状态机代码结构 ---\n")
            printStateMachineCode()
        }
    }

    /**
     * 模拟第一步操作
     */
    private suspend fun stepOne(): Int {
        delay(300)
        return 10
    }

    /**
     * 模拟第二步操作
     */
    private suspend fun stepTwo(): Int {
        delay(300)
        return 20
    }

    /**
     * 模拟第三步操作
     */
    private suspend fun stepThree(): Int {
        delay(300)
        return 30
    }

    /**
     * 打印状态机编译后的代码结构
     */
    private fun printStateMachineCode() {
        log("原始协程代码:\n")
        log("  suspend fun calculate(): Int {")
        log("      val a = stepOne()    // 挂起点 1")
        log("      val b = stepTwo()    // 挂起点 2")
        log("      val c = stepThree()  // 挂起点 3")
        log("      return a + b + c")
        log("  }\n")

        log("编译器生成的状态机（伪代码）:\n")
        log("  class CalculateStateMachine : ContinuationImpl {")
        log("      var label = 0        // 当前状态")
        log("      var a: Int = 0       // 保存的局部变量")
        log("      var b: Int = 0")
        log("      var c: Int = 0")
        log("      var result: Any? = null\n")

        log("      fun doResume(result: Any?): Any? {")
        log("          when (label) {")
        log("              0 -> {")
        log("                  label = 1")
        log("                  a = stepOne(this)")
        log("                  if (a == SUSPENDED) return")
        log("              }")
        log("              1 -> {")
        log("                  label = 2")
        log("                  b = stepTwo(this)")
        log("                  if (b == SUSPENDED) return")
        log("              }")
        log("              2 -> {")
        log("                  label = 3")
        log("                  c = stepThree(this)")
        log("                  if (c == SUSPENDED) return")
        log("              }")
        log("              3 -> return a + b + c")
        log("          }")
        log("      }")
        log("  }\n")

        log("==========================================\n")
    }

    /**
     * 添加日志并更新 UI
     */
    private fun log(message: String) {
        if (Thread.currentThread().name == "main") {
            logBuilder.append(message).append("\n")
            tvLog.text = logBuilder.toString()
        } else {
            runOnUiThread {
                logBuilder.append(message).append("\n")
                tvLog.text = logBuilder.toString()
            }
        }
    }
}
