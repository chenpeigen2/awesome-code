package com.peter.rxjava.demo.errorhandling

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * 错误处理操作符演示
 *
 * 本 Activity 展示 RxJava 中的错误处理操作符：
 *
 * ## 错误处理原则：
 * RxJava 中错误是终态事件，一旦 onError 被调用，Observable 就终止了。
 * 错误处理操作符允许我们在出错时进行恢复，而不是直接终止。
 *
 * ## 操作符：
 * 1. **onErrorReturn**: 出错时返回一个默认值，然后正常完成
 * 2. **onErrorResumeNext**: 出错时切换到另一个 Observable 继续
 * 3. **retry**: 出错时重新订阅，可指定重试次数
 * 4. **retryWhen**: 出错时根据条件决定是否重试，更灵活的重试策略
 */
class ErrorHandlingActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.error_handling)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateOnErrorReturn()
        demonstrateOnErrorResumeNext()
        demonstrateRetry()
        demonstrateRetryWhen()
    }

    /**
     * 演示 onErrorReturn()：出错时返回默认值
     *
     * 当发生错误时，发射一个默认值然后正常完成。
     * 适合"出错时使用兜底值"的场景。
     */
    private fun demonstrateOnErrorReturn() {
        log("========== onErrorReturn() ==========")
        Observable.create<String> { emitter ->
            emitter.onNext("正常数据1")
            emitter.onNext("正常数据2")
            emitter.onError(RuntimeException("模拟网络错误"))
            emitter.onNext("这不会被发射") // 错误后不再发射
        }
            .onErrorReturn { error ->
                log("  捕获错误: ${error.message}")
                "兜底数据（错误时返回的默认值）"
            }
            .subscribe(
                { value -> log("  onErrorReturn onNext: $value") },
                { error -> log("  onErrorReturn onError: ${error.message}") },
                { log("  onErrorReturn onComplete\n") }
            )
    }

    /**
     * 演示 onErrorResumeNext()：出错时切换到备用 Observable
     *
     * 当发生错误时，切换到另一个 Observable 继续发射数据。
     * 比 onErrorReturn 更灵活，可以发射多个备份数据。
     */
    private fun demonstrateOnErrorResumeNext() {
        log("========== onErrorResumeNext() ==========")
        Observable.create<String> { emitter ->
            emitter.onNext("主数据源: 数据1")
            emitter.onError(RuntimeException("主数据源崩溃"))
        }
            .onErrorResumeNext { error ->
                log("  主数据源出错: ${error.message}，切换到备用数据源")
                Observable.just("备用数据源: 数据A", "备用数据源: 数据B")
            }
            .subscribe(
                { value -> log("  onErrorResumeNext onNext: $value") },
                { error -> log("  onErrorResumeNext onError: ${error.message}") },
                { log("  onErrorResumeNext onComplete\n") }
            )
    }

    /**
     * 演示 retry()：出错时重试
     *
     * 当发生错误时，重新订阅 Observable。
     * 可以指定最大重试次数。
     */
    private fun demonstrateRetry() {
        log("========== retry() 重试 ==========")
        var attempt = 0

        Observable.create<String> { emitter ->
            attempt++
            log("  第 $attempt 次尝试")
            if (attempt < 3) {
                emitter.onError(RuntimeException("第 $attempt 次失败"))
            } else {
                emitter.onNext("第 $attempt 次成功！")
                emitter.onComplete()
            }
        }
            .retry(3) // 最多重试 3 次
            .subscribe(
                { value -> log("  retry onNext: $value") },
                { error -> log("  retry onError: ${error.message}") },
                { log("  retry onComplete\n") }
            )
    }

    /**
     * 演示 retryWhen()：条件重试
     *
     * retryWhen 比 retry 更灵活，可以：
     * - 根据错误类型决定是否重试
     * - 添加延迟后重试（如指数退避）
     * - 设置最大重试次数
     */
    private fun demonstrateRetryWhen() {
        log("========== retryWhen() 条件重试 ==========")
        var retryCount = 0

        Observable.create<String> { emitter ->
            retryCount++
            log("  第 $retryCount 次请求")
            if (retryCount < 3) {
                emitter.onError(RuntimeException("请求失败"))
            } else {
                emitter.onNext("请求成功！")
                emitter.onComplete()
            }
        }
            .retryWhen { errors ->
                errors.flatMap { error ->
                    log("  retryWhen 捕获错误: ${error.message}")
                    if (retryCount < 5) {
                        log("  延迟 500ms 后重试...")
                        Observable.timer(500, TimeUnit.MILLISECONDS)
                    } else {
                        Observable.error(error) // 超过重试次数，传递错误
                    }
                }
            }
            .subscribe(
                { value -> log("  retryWhen onNext: $value") },
                { error -> log("  retryWhen onError: ${error.message}") },
                { log("  retryWhen onComplete\n") }
            )
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
