package com.peter.rxjava.demo.operators

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable

/**
 * 变换操作符演示
 *
 * 本 Activity 展示 RxJava 中的变换操作符：
 *
 * ## 变换操作符：
 * 变换操作符用于对 Observable 发射的数据进行转换处理。
 *
 * 1. **map**: 一对一转换，将每个数据项转换为另一个数据项
 * 2. **flatMap**: 一对多转换，将每个数据项转换为 Observable 并合并结果
 *    - 不保证顺序，适合并行请求
 * 3. **concatMap**: 类似 flatMap，但保证顺序（前一个完成后才处理下一个）
 * 4. **switchMap**: 只接收最新的 Observable，取消之前的
 *    - 适合搜索框输入等"只关心最新结果"的场景
 * 5. **scan**: 累积计算，每次发射当前累积值
 * 6. **buffer**: 将数据分组，按数量或时间打包发射
 *
 * ## flatMap vs concatMap vs switchMap：
 * - flatMap: 并发执行，不保证顺序
 * - concatMap: 串行执行，保证顺序
 * - switchMap: 取消旧的，只保留最新的
 */
class TransformOperatorsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.transform_operators)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateMap()
        demonstrateFlatMap()
        demonstrateConcatMap()
        demonstrateSwitchMap()
        demonstrateScan()
        demonstrateBuffer()
    }

    /**
     * 演示 map()：一对一变换
     */
    private fun demonstrateMap() {
        log("========== map() 一对一变换 ==========")
        Observable.just(1, 2, 3, 4, 5)
            .map { it * 10 }
            .subscribe(
                { value -> log("  map onNext: $value") },
                { error -> log("  map onError: ${error.message}") },
                { log("  map onComplete\n") }
            )
    }

    /**
     * 演示 flatMap()：一对多变换，不保证顺序
     */
    private fun demonstrateFlatMap() {
        log("========== flatMap() 一对多 ==========")
        Observable.just("A", "B", "C")
            .flatMap { letter ->
                // 将每个字母扩展为带数字的多个项
                Observable.just("${letter}1", "${letter}2")
            }
            .subscribe(
                { value -> log("  flatMap onNext: $value") },
                { error -> log("  flatMap onError: ${error.message}") },
                { log("  flatMap onComplete\n") }
            )
    }

    /**
     * 演示 concatMap()：一对多变换，保证顺序
     */
    private fun demonstrateConcatMap() {
        log("========== concatMap() 保证顺序 ==========")
        Observable.just("A", "B", "C")
            .concatMap { letter ->
                // 按 A → B → C 的顺序依次处理
                Observable.just("${letter}1", "${letter}2")
            }
            .subscribe(
                { value -> log("  concatMap onNext: $value") },
                { error -> log("  concatMap onError: ${error.message}") },
                { log("  concatMap onComplete\n") }
            )
    }

    /**
     * 演示 switchMap()：只保留最新
     */
    private fun demonstrateSwitchMap() {
        log("========== switchMap() 只保留最新 ==========")
        Observable.just(1, 2, 3)
            .switchMap { value ->
                // 每次发射新的值时，之前的 Observable 会被丢弃
                log("  switchMap 处理: $value")
                Observable.just("结果-$value")
            }
            .subscribe(
                { value -> log("  switchMap onNext: $value") },
                { error -> log("  switchMap onError: ${error.message}") },
                { log("  switchMap onComplete\n") }
            )
    }

    /**
     * 演示 scan()：累积计算
     */
    private fun demonstrateScan() {
        log("========== scan() 累积计算 ==========")
        Observable.just(1, 2, 3, 4, 5)
            .scan { acc, value -> acc + value }
            .subscribe(
                { value -> log("  scan onNext（当前累加和）: $value") },
                { error -> log("  scan onError: ${error.message}") },
                { log("  scan onComplete\n") }
            )
    }

    /**
     * 演示 buffer()：分组打包
     */
    private fun demonstrateBuffer() {
        log("========== buffer() 分组打包 ==========")
        Observable.just(1, 2, 3, 4, 5, 6, 7)
            .buffer(3) // 每 3 个数据打包成一组
            .subscribe(
                { value -> log("  buffer onNext: $value") },
                { error -> log("  buffer onError: ${error.message}") },
                { log("  buffer onComplete\n") }
            )
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
