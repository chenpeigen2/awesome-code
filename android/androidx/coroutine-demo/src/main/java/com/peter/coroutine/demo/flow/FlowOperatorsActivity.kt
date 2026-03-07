package com.peter.coroutine.demo.flow

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.coroutine.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

/**
 * Flow 操作符演示
 *
 * 本 Activity 展示 Kotlin Flow 中常用的操作符及其使用场景。
 *
 * ## Flow 操作符分类：
 *
 * ### 1. 转换操作符 (Transform Operators)
 * - `map`: 转换每个发出的值
 * - `filter`: 过滤符合条件的值
 * - `transform`: 更灵活的转换，可以发射多个值或不发射
 *
 * ### 2. 组合操作符 (Combination Operators)
 * - `zip`: 将两个 Flow 的值按顺序配对
 * - `combine`: 当任一 Flow 发出新值时，使用最新值重新计算
 *
 * ### 3. 扁平化操作符 (Flattening Operators)
 * - `flatMapConcat`: 顺序处理内部 Flow
 * - `flatMapMerge`: 并行处理内部 Flow
 * - `flatMapLatest`: 只处理最新的内部 Flow
 *
 * ### 4. 上下文操作符 (Context Operators)
 * - `flowOn`: 切换上游 Flow 的执行上下文
 * - `catch`: 捕获上游 Flow 的异常
 *
 * ### 5. 回调操作符 (Callback Operators)
 * - `onStart`: Flow 开始前执行
 * - `onEach`: 每个值处理前执行
 * - `onCompletion`: Flow 完成后执行
 *
 * @see kotlinx.coroutines.flow.map
 * @see kotlinx.coroutines.flow.filter
 * @see kotlinx.coroutines.flow.combine
 * @see kotlinx.coroutines.flow.zip
 */
class FlowOperatorsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.flow_operators)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        // 使用按钮布局
        addButtons()
    }

    private fun addButtons() {
        // 直接开始演示
        demonstrateAllOperators()
    }

    private fun demonstrateAllOperators() {
        log("========== Flow 操作符演示 ==========\n")

        lifecycleScope.launch {
            // 演示1: 转换操作符
            demonstrateTransformOperators()

            // 演示2: 组合操作符
            demonstrateCombinationOperators()

            // 演示3: 扁平化操作符
            demonstrateFlatteningOperators()

            // 演示4: 回调操作符
            demonstrateCallbackOperators()

            log("\n========== 演示完成 ==========")
        }
    }

    /**
     * 演示转换操作符
     */
    private suspend fun demonstrateTransformOperators() {
        log("--- 转换操作符 ---\n")

        // map: 转换每个值
        log("1. map 操作符:")
        flowOf(1, 2, 3, 4, 5)
            .map { it * it } // 平方
            .collect { log("  map 结果: $it") }

        // filter: 过滤值
        log("\n2. filter 操作符:")
        flowOf(1, 2, 3, 4, 5, 6)
            .filter { it % 2 == 0 } // 只保留偶数
            .collect { log("  filter 结果: $it") }

        // transform: 灵活转换
        log("\n3. transform 操作符:")
        flowOf(1, 2, 3)
            .transform { value ->
                emit("开始处理: $value")
                emit("结果: ${value * 10}")
            }
            .collect { log("  $it") }

        log("\n")
    }

    /**
     * 演示组合操作符
     */
    private suspend fun demonstrateCombinationOperators() {
        log("--- 组合操作符 ---\n")

        // zip: 配对组合
        log("1. zip 操作符:")
        val flow1 = flowOf("A", "B", "C")
        val flow2 = flowOf(1, 2, 3)

        flow1.zip(flow2) { letter, number ->
            "$letter-$number"
        }.collect { log("  zip 结果: $it") }

        // combine: 实时组合
        log("\n2. combine 操作符:")
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
            delay(150)
            emit("Z")
        }

        fastFlow.combine(slowFlow) { num, letter ->
            "$num + $letter"
        }.collect { log("  combine 结果: $it") }

        log("\n")
    }

    /**
     * 演示扁平化操作符
     */
    private suspend fun demonstrateFlatteningOperators() {
        log("--- 扁平化操作符 ---\n")

        // flatMapConcat: 顺序执行
        log("1. flatMapConcat 操作符:")
        flowOf(1, 2, 3)
            .flatMapConcat { value ->
                flow {
                    emit("$value-A")
                    delay(100)
                    emit("$value-B")
                }
            }
            .collect { log("  flatMapConcat: $it") }

        // flatMapMerge: 并行执行
        log("\n2. flatMapMerge 操作符:")
        flowOf(1, 2, 3)
            .flatMapMerge { value ->
                flow {
                    delay(300 - value * 100L) // 后面的先完成
                    emit("任务 $value 完成")
                }
            }
            .collect { log("  flatMapMerge: $it") }

        log("\n")
    }

    /**
     * 演示回调操作符
     */
    private suspend fun demonstrateCallbackOperators() {
        log("--- 回调操作符 ---\n")

        flow {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
        }
            .onStart {
                log("  [onStart] Flow 开始执行")
            }
            .onEach { value ->
                log("  [onEach] 处理中: $value")
            }
            .onCompletion { cause ->
                if (cause == null) {
                    log("  [onCompletion] Flow 正常完成")
                } else {
                    log("  [onCompletion] Flow 异常结束: $cause")
                }
            }
            .collect { value ->
                log("  [collect] 收到: $value")
            }

        log("\n")
    }

    /**
     * 添加日志
     */
    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
