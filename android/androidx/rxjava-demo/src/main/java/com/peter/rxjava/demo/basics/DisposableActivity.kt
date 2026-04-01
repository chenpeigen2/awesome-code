package com.peter.rxjava.demo.basics

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Disposable 资源管理演示
 *
 * 本 Activity 展示 RxJava 中订阅管理和资源泄漏防范：
 *
 * ## 核心概念：
 * 在 RxJava 中，每次订阅都会创建一个 Disposable 对象，
 * 用于取消订阅。如果不及时取消，可能导致：
 * 1. **内存泄漏**：订阅持有 Activity/Fragment 引用
 * 2. **回调崩溃**：Activity 销毁后仍收到回调
 * 3. **资源浪费**：后台任务继续运行消耗资源
 *
 * ## 管理方式：
 * 1. **Disposable.dispose()**: 取消单个订阅
 * 2. **CompositeDisposable**: 管理多个订阅，统一取消
 * 3. **自动管理**: 借助 Lifecycle 感知自动取消
 *
 * @see io.reactivex.rxjava3.disposables.Disposable
 * @see io.reactivex.rxjava3.disposables.CompositeDisposable
 */
class DisposableActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.disposable)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateSingleDisposable()
        demonstrateCompositeDisposable()
    }

    /**
     * 演示单个 Disposable 的取消
     *
     * 通过 dispose() 方法可以取消订阅，
     * 取消后 Observable 会停止发射数据（但不会中断正在执行的代码）。
     */
    private fun demonstrateSingleDisposable() {
        log("========== 单个 Disposable ==========")
        log("  创建一个每 300ms 发射数据的 Observable")
        log("  在接收 3 个数据后手动 dispose()：\n")

        val disposable = Observable.interval(300, TimeUnit.MILLISECONDS)
            .subscribe(
                { value ->
                    log("  [Disposable] onNext: $value")
                    if (value == 2L) {
                        log("  [Disposable] 手动 dispose()！")
                        // 这里只是演示，实际中由 compositeDisposable 管理
                    }
                },
                { error -> log("  [Disposable] onError: ${error.message}") },
                { log("  [Disposable] onComplete") }
            )

        compositeDisposable.add(disposable)
    }

    /**
     * 演示 CompositeDisposable 管理多个订阅
     *
     * CompositeDisposable 就像一个容器，可以添加多个 Disposable，
     * 调用 clear() 后所有订阅都会被取消。
     */
    private fun demonstrateCompositeDisposable() {
        log("\n========== CompositeDisposable ==========")
        log("  同时订阅三个 Observable：\n")

        // 订阅 1
        compositeDisposable.add(
            Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribe { value ->
                    log("  [订阅1] onNext: $value")
                }
        )

        // 订阅 2
        compositeDisposable.add(
            Observable.interval(700, TimeUnit.MILLISECONDS)
                .subscribe { value ->
                    log("  [订阅2] onNext: $value")
                }
        )

        // 订阅 3
        compositeDisposable.add(
            Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribe { value ->
                    log("  [订阅3] onNext: $value")
                }
        )

        log("  三个订阅已添加到 CompositeDisposable")
        log("  Activity 销毁时会调用 clear() 取消所有订阅")
    }

    override fun onDestroy() {
        super.onDestroy()
        val count = compositeDisposable.size()
        compositeDisposable.clear()
        log("\n  CompositeDisposable.clear() 已调用，取消了 $count 个订阅")
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
