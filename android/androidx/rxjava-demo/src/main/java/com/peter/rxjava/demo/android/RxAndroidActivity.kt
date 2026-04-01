package com.peter.rxjava.demo.android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * RxAndroid 集成演示
 *
 * 本 Activity 展示 RxJava 在 Android 中的最佳实践：
 *
 * ## 核心要点：
 * 1. **AndroidSchedulers.mainThread()**: 在主线程接收数据更新 UI
 * 2. **Lifecycle 感知**: 在 Activity/Fragment 销毁时取消订阅
 * 3. **CompositeDisposable**: 统一管理所有订阅
 *
 * ## 常见模式：
 * ```kotlin
 * compositeDisposable.add(
 *     observable
 *         .subscribeOn(Schedulers.io())
 *         .observeOn(AndroidSchedulers.mainThread())
 *         .subscribe(
 *             { data -> updateUI(data) },
 *             { error -> showError(error) }
 *         )
 * )
 * ```
 *
 * ## 注意事项：
 * - 永远在 onDestroy 中调用 compositeDisposable.clear()
 * - UI 操作必须在主线程
 * - 避免在 Observable 中持有 Activity 的强引用
 */
class RxAndroidActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.rx_android)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateMainThreadSwitching()
        demonstrateLifecycleAware()
        demonstrateRealWorldPattern()
    }

    /**
     * 演示线程切换：IO 线程处理 → 主线程更新 UI
     *
     * 这是 RxJava 在 Android 中最常见的模式：
     * 在后台线程执行耗时操作，然后切换到主线程更新 UI。
     */
    private fun demonstrateMainThreadSwitching() {
        log("========== 线程切换 ==========")
        log("  模拟网络请求：在 IO 线程处理，主线程更新 UI\n")

        compositeDisposable.add(
            Observable.just("用户数据")
                .doOnSubscribe { log("  订阅线程: ${Thread.currentThread().name}") }
                .subscribeOn(Schedulers.io())
                .doOnNext { log("  处理数据线程: ${Thread.currentThread().name}") }
                .map { data ->
                    // 模拟耗时处理
                    Thread.sleep(500)
                    "${data}（已处理）"
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        log("  接收结果线程: ${Thread.currentThread().name}")
                        log("  结果: $result\n")
                    },
                    { error -> log("  onError: ${error.message}\n") }
                )
        )
    }

    /**
     * 演示 Lifecycle 感知：自动取消订阅
     *
     * 在 Activity 销毁时通过 CompositeDisposable.clear() 取消所有订阅，
     * 防止 Activity 泄漏和回调崩溃。
     */
    private fun demonstrateLifecycleAware() {
        log("========== Lifecycle 感知 ==========")
        log("  启动一个持续发射数据的 Observable：\n")

        compositeDisposable.add(
            Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { count ->
                        log("  [Lifecycle] 收到数据: $count，UI 线程: ${Thread.currentThread().name}")
                    },
                    { error -> log("  [Lifecycle] onError: ${error.message}") },
                    { log("  [Lifecycle] onComplete") }
                )
        )

        log("  Activity 销毁时会自动取消此订阅\n")
    }

    /**
     * 演示实际开发模式：多数据源组合
     *
     * 模拟一个常见场景：同时请求用户信息和配置信息，
     * 两个请求都完成后更新 UI。
     */
    private fun demonstrateRealWorldPattern() {
        log("========== 实际开发模式 ==========")
        log("  模拟：同时请求用户信息 + 配置信息\n")

        val userInfo = Observable.create<String> { emitter ->
            Thread.sleep(300) // 模拟网络延迟
            emitter.onNext("用户: Peter")
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())

        val configInfo = Observable.create<String> { emitter ->
            Thread.sleep(500) // 模拟网络延迟
            emitter.onNext("配置: Dark Mode")
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())

        compositeDisposable.add(
            Observable.zip(userInfo, configInfo) { user, config ->
                "$user | $config"
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        log("  两个请求都完成！")
                        log("  合并结果: $result\n")
                    },
                    { error -> log("  请求失败: ${error.message}\n") }
                )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        val count = compositeDisposable.size()
        compositeDisposable.clear()
        log("  [onDestroy] 已取消 $count 个订阅，防止内存泄漏")
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
