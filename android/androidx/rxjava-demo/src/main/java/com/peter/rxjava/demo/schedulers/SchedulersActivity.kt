package com.peter.rxjava.demo.schedulers

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Schedulers 线程调度演示
 *
 * 本 Activity 展示 RxJava 中的线程调度机制：
 *
 * ## 核心概念：
 * RxJava 默认在当前线程（订阅发生的线程）中执行所有操作。
 * 通过 subscribeOn 和 observeOn 可以切换执行线程。
 *
 * ## subscribeOn vs observeOn：
 * - **subscribeOn**: 指定 Observable 的执行线程（只生效一次）
 * - **observeOn**: 切换下游操作的线程（可以多次切换）
 *
 * ## Schedulers 类型：
 * 1. **Schedulers.io()**: IO 操作（网络、文件），线程池可无限增长
 * 2. **Schedulers.computation()**: CPU 密集计算，线程数=CPU 核数
 * 3. **Schedulers.newThread()**: 每次创建新线程
 * 4. **Schedulers.single()**: 单线程串行执行
 * 5. **AndroidSchedulers.mainThread()**: Android 主线程（UI 线程）
 *
 * ## 典型模式：
 * ```kotlin
 * Observable.just(data)
 *     .subscribeOn(Schedulers.io())         // 在 IO 线程执行
 *     .observeOn(AndroidSchedulers.mainThread()) // 在主线程接收
 *     .subscribe { result -> updateUI(result) }
 * ```
 *
 * @see io.reactivex.rxjava3.schedulers.Schedulers
 */
class SchedulersActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.schedulers)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateDefaultThread()
        demonstrateSubscribeOn()
        demonstrateObserveOn()
        demonstrateComputation()
        demonstrateSingle()
    }

    /**
     * 演示默认线程：在当前线程执行
     */
    private fun demonstrateDefaultThread() {
        log("========== 默认线程 ==========")
        Observable.just("A", "B", "C")
            .subscribe(
                { value -> log("  默认线程 onNext: $value，线程: ${Thread.currentThread().name}") },
                { error -> log("  默认线程 onError: ${error.message}") },
                { log("  默认线程 onComplete\n") }
            )
    }

    /**
     * 演示 subscribeOn()：指定上游执行线程
     *
     * subscribeOn 决定了 Observable 在哪个线程上执行（发射数据）。
     * 无论在链中哪个位置调用，都只以第一次调用为准。
     */
    private fun demonstrateSubscribeOn() {
        log("========== subscribeOn() ==========")
        Observable.just("X", "Y", "Z")
            .doOnNext { value ->
                log("  doOnNext: $value，线程: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { value -> log("  subscribeOn onNext: $value，线程: ${Thread.currentThread().name}") },
                { error -> log("  subscribeOn onError: ${error.message}") },
                { log("  subscribeOn onComplete\n") }
            )

        // 等待异步操作完成
        Thread.sleep(200)
    }

    /**
     * 演示 observeOn()：切换下游线程
     *
     * observeOn 影响的是它之后所有操作符的执行线程，
     * 可以多次调用来切换线程。
     */
    private fun demonstrateObserveOn() {
        log("========== observeOn() ==========")
        Observable.just(1, 2, 3)
            .doOnNext { value ->
                log("  第一次 doOnNext: $value，线程: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnNext { value ->
                log("  第二次 doOnNext: $value，线程: ${Thread.currentThread().name}")
            }
            .subscribe(
                { value -> log("  最终 onNext: $value，线程: ${Thread.currentThread().name}") },
                { error -> log("  observeOn onError: ${error.message}") },
                { log("  observeOn onComplete\n") }
            )

        Thread.sleep(200)
    }

    /**
     * 演示 computation()：CPU 密集计算调度器
     *
     * 适合 CPU 密集型任务，线程数量固定为 CPU 核数。
     * 不适合 IO 操作（会导致线程饥饿）。
     */
    private fun demonstrateComputation() {
        log("========== computation() ==========")
        val cpuCount = Runtime.getRuntime().availableProcessors()
        log("  CPU 核数: $cpuCount")
        log("  computation 调度器线程池大小 = CPU 核数\n")

        Observable.range(1, 3)
            .subscribeOn(Schedulers.computation())
            .subscribe(
                { value -> log("  computation onNext: $value，线程: ${Thread.currentThread().name}") },
                { error -> log("  computation onError: ${error.message}") },
                { log("  computation onComplete\n") }
            )

        Thread.sleep(200)
    }

    /**
     * 演示 single()：单线程串行执行
     *
     * 所有任务在同一个线程上串行执行，保证顺序。
     */
    private fun demonstrateSingle() {
        log("========== single() ==========")
        Observable.range(1, 3)
            .subscribeOn(Schedulers.single())
            .subscribe(
                { value -> log("  single onNext: $value，线程: ${Thread.currentThread().name}") },
                { error -> log("  single onError: ${error.message}") },
                { log("  single onComplete\n") }
            )

        Thread.sleep(200)
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
