package com.peter.rxjava.demo.basics

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

/**
 * Single / Maybe / Completable 演示
 *
 * 本 Activity 展示 RxJava 中四种响应式类型的区别：
 *
 * ## 四种响应式类型对比：
 *
 * | 类型       | 发射数据数量 | 错误   | 完成   | 典型场景               |
 * |-----------|-------------|--------|--------|----------------------|
 * | Observable | 0~N 个      | 支持   | 支持   | 通用数据流             |
 * | Single    | 恰好 1 个    | 支持   | 不适用 | 网络请求、数据库查询     |
 * | Maybe     | 0 或 1 个    | 支持   | 支持   | 可能为空的数据查询      |
 * | Completable| 0 个        | 支持   | 支持   | 写入操作、无返回值的任务 |
 *
 * @see io.reactivex.rxjava3.core.Single
 * @see io.reactivex.rxjava3.core.Maybe
 * @see io.reactivex.rxjava3.core.Completable
 */
class SingleMaybeCompletableActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.single_maybe_completable)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateSingle()
        demonstrateSingleError()
        demonstrateMaybeWithValue()
        demonstrateMaybeEmpty()
        demonstrateCompletable()
    }

    /**
     * 演示 Single：恰好发射一个数据或一个错误
     *
     * 典型使用场景：网络请求返回单个结果
     * - onSuccess: 成功获取数据
     * - onError: 请求失败
     */
    private fun demonstrateSingle() {
        log("========== Single（成功） ==========")
        Single.just("Hello from Single!")
            .subscribe(
                { value -> log("  Single onSuccess: $value\n") },
                { error -> log("  Single onError: ${error.message}\n") }
            )
    }

    /**
     * 演示 Single 发射错误
     */
    private fun demonstrateSingleError() {
        log("========== Single（错误） ==========")
        Single.error<String>(RuntimeException("网络请求失败"))
            .subscribe(
                { value -> log("  Single onSuccess: $value\n") },
                { error -> log("  Single onError: ${error.message}\n") }
            )
    }

    /**
     * 演示 Maybe：发射一个数据
     *
     * 典型使用场景：数据库查询，可能找到数据也可能找不到
     */
    private fun demonstrateMaybeWithValue() {
        log("========== Maybe（有数据） ==========")
        Maybe.just("Hello from Maybe!")
            .subscribe(
                { value -> log("  Maybe onSuccess: $value") },
                { error -> log("  Maybe onError: ${error.message}") },
                { log("  Maybe onComplete（无数据）\n") }
            )
    }

    /**
     * 演示 Maybe：不发射数据直接完成
     */
    private fun demonstrateMaybeEmpty() {
        log("========== Maybe（无数据） ==========")
        Maybe.empty<String>()
            .subscribe(
                { value -> log("  Maybe onSuccess: $value") },
                { error -> log("  Maybe onError: ${error.message}") },
                { log("  Maybe onComplete（无数据）\n") }
            )
    }

    /**
     * 演示 Completable：不发射数据，只关注完成或错误
     *
     * 典型使用场景：文件写入、数据库更新、日志记录
     * 不关心返回值，只关心操作是否成功。
     */
    private fun demonstrateCompletable() {
        log("========== Completable ==========")
        Completable.fromRunnable {
            log("  Completable: 执行耗时操作（模拟写入）...")
        }.subscribe(
            { log("  Completable onComplete（操作成功）\n") },
            { error -> log("  Completable onError: ${error.message}\n") }
        )
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
