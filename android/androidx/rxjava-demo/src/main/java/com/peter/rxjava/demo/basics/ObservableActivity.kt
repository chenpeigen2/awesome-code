package com.peter.rxjava.demo.basics

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * Observable 创建演示
 *
 * 本 Activity 展示 RxJava 中创建 Observable 的各种方式：
 *
 * ## 核心概念：
 * Observable 是 RxJava 中最基本的数据源，它可以发射零个或多个数据，
 * 然后以成功或错误的方式终止。
 *
 * ## Observable 的三种通知：
 * 1. **onNext(T)**: 发射一个数据项
 * 2. **onError(Throwable)**: 发射一个错误通知，之后不再发射数据
 * 3. **onComplete()**: 发射完成通知，之后不再发射数据
 *
 * ## 创建方式：
 * - `just()`: 发射固定数量的数据项（最多10个）
 * - `fromIterable()`: 从集合创建
 * - `create()`: 手动创建，可精确控制发射逻辑
 * - `range()`: 发射一个范围内的整数
 * - `interval()`: 按固定时间间隔发射递增整数
 * - `timer()`: 延迟后发射一个数据
 * - `defer()`: 延迟创建，每次订阅时才创建新的 Observable
 *
 * @see io.reactivex.rxjava3.core.Observable
 */
class ObservableActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.observable)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateJust()
        demonstrateFromIterable()
        demonstrateRange()
        demonstrateCreate()
        demonstrateInterval()
        demonstrateTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // interval 创建的 Observable 需要手动取消
        // 这里因为是演示，interval 的 Disposable 会在 Activity 销毁时泄漏
        // 实际项目中应使用 CompositeDisposable 管理
    }

    /**
     * 演示 just()：发射固定数据项
     */
    private fun demonstrateJust() {
        log("========== just() ==========")
        Observable.just("Hello", "RxJava", "World")
            .subscribe(
                { value -> log("  just onNext: $value") },
                { error -> log("  just onError: ${error.message}") },
                { log("  just onComplete\n") }
            )
    }

    /**
     * 演示 fromIterable()：从集合创建
     */
    private fun demonstrateFromIterable() {
        log("========== fromIterable() ==========")
        val list = listOf("Kotlin", "Java", "Python", "Go")
        Observable.fromIterable(list)
            .subscribe(
                { value -> log("  fromIterable onNext: $value") },
                { error -> log("  fromIterable onError: ${error.message}") },
                { log("  fromIterable onComplete\n") }
            )
    }

    /**
     * 演示 range()：发射一个范围内的整数
     */
    private fun demonstrateRange() {
        log("========== range() ==========")
        Observable.range(1, 5)
            .subscribe(
                { value -> log("  range onNext: $value") },
                { error -> log("  range onError: ${error.message}") },
                { log("  range onComplete\n") }
            )
    }

    /**
     * 演示 create()：手动创建 Observable
     *
     * create() 允许你精确控制数据的发射，但需要注意：
     * 1. 必须检查 ObservableEmitter.isDisposed()
     * 2. 不能在 onError 之后发射数据
     * 3. 不能在 onComplete 之后发射数据
     */
    private fun demonstrateCreate() {
        log("========== create() ==========")
        Observable.create<String> { emitter ->
            emitter.onNext("第一条数据")
            emitter.onNext("第二条数据")
            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        }.subscribe(
            { value -> log("  create onNext: $value") },
            { error -> log("  create onError: ${error.message}") },
            { log("  create onComplete\n") }
        )
    }

    /**
     * 演示 interval()：按固定间隔发射递增整数
     *
     * interval 创建的是一个"冷" Observable，每个订阅者都会收到完整序列。
     * 注意：interval 默认在 computation 调度器上执行。
     */
    private fun demonstrateInterval() {
        log("========== interval() ==========")
        log("  每 500ms 发射一次，共发射 3 次：")
        Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(3)
            .subscribe(
                { value -> log("  interval onNext: $value") },
                { error -> log("  interval onError: ${error.message}") },
                { log("  interval onComplete\n") }
            )
    }

    /**
     * 演示 timer()：延迟后发射单个数据
     */
    private fun demonstrateTimer() {
        log("========== timer() ==========")
        log("  延迟 1 秒后发射：")
        Observable.timer(1, TimeUnit.SECONDS)
            .subscribe(
                { value -> log("  timer onNext: $value") },
                { error -> log("  timer onError: ${error.message}") },
                { log("  timer onComplete\n") }
            )
    }

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
