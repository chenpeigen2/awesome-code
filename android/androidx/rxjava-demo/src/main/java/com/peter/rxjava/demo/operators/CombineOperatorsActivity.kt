package com.peter.rxjava.demo.operators

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable

/**
 * 组合操作符演示
 *
 * 本 Activity 展示 RxJava 中的组合操作符：
 *
 * ## 组合操作符：
 * 用于将多个 Observable 组合成一个 Observable。
 *
 * 1. **zip**: 将多个 Observable 的数据按序组合，
 *    等所有 Observable 都发射后才组合，取最短的序列
 * 2. **merge**: 将多个 Observable 合并，交错发射
 *    - 所有数据按时间顺序发射
 * 3. **combineLatest**: 当任意 Observable 发射数据时，
 *    取所有 Observable 的最新值组合
 * 4. **concat**: 串行连接多个 Observable，
 *    前一个完成后才开始下一个
 * 5. **startWith**: 在数据流前面插入数据
 */
class CombineOperatorsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.combine_operators)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateZip()
        demonstrateMerge()
        demonstrateCombineLatest()
        demonstrateConcat()
        demonstrateStartWith()
    }

    /**
     * 演示 zip()：按序组合
     *
     * zip 会将多个 Observable 的数据按位置一一配对，
     * 类似于拉链将两边合在一起。
     */
    private fun demonstrateZip() {
        log("========== zip() 按序组合 ==========")
        val numbers = Observable.just(1, 2, 3)
        val letters = Observable.just("A", "B", "C")

        Observable.zip(numbers, letters) { num, letter ->
            "$letter$num" // 组合成 "A1", "B2", "C3"
        }.subscribe(
            { value -> log("  zip onNext: $value") },
            { error -> log("  zip onError: ${error.message}") },
            { log("  zip onComplete\n") }
        )
    }

    /**
     * 演示 merge()：合并交错发射
     *
     * merge 将多个 Observable 的数据合并到一个流中，
     * 数据按发射的时间顺序交错出现。
     */
    private fun demonstrateMerge() {
        log("========== merge() 合并交错 ==========")
        val source1 = Observable.just("A1", "A2", "A3")
        val source2 = Observable.just("B1", "B2", "B3")

        Observable.merge(source1, source2)
            .subscribe(
                { value -> log("  merge onNext: $value") },
                { error -> log("  merge onError: ${error.message}") },
                { log("  merge onComplete\n") }
            )
    }

    /**
     * 演示 combineLatest()：最新值组合
     *
     * 当任意一个 Observable 发射数据时，取所有 Observable 的最新值组合。
     * 与 zip 不同的是，combineLatest 在任意源发射时都会触发组合。
     */
    private fun demonstrateCombineLatest() {
        log("========== combineLatest() 最新值组合 ==========")
        val source1 = Observable.just(1, 2, 3)
        val source2 = Observable.just("A", "B")

        Observable.combineLatest(source1, source2) { num, letter ->
            "$letter$num"
        }.subscribe(
            { value -> log("  combineLatest onNext: $value") },
            { error -> log("  combineLatest onError: ${error.message}") },
            { log("  combineLatest onComplete\n") }
        )
    }

    /**
     * 演示 concat()：串行连接
     *
     * concat 会按顺序连接多个 Observable，
     * 前一个完成后才开始下一个，保证顺序。
     */
    private fun demonstrateConcat() {
        log("========== concat() 串行连接 ==========")
        val source1 = Observable.just("第一组-A", "第一组-B")
        val source2 = Observable.just("第二组-X", "第二组-Y")

        Observable.concat(source1, source2)
            .subscribe(
                { value -> log("  concat onNext: $value") },
                { error -> log("  concat onError: ${error.message}") },
                { log("  concat onComplete\n") }
            )
    }

    /**
     * 演示 startWith()：前面插入数据
     *
     * 在 Observable 数据流的前面插入指定数据，
     * 常用于在数据列表前插入"加载中"或"头部"数据。
     */
    private fun demonstrateStartWith() {
        log("========== startWith() 前面插入 ==========")
        Observable.just("数据1", "数据2", "数据3")
            .startWithItem("头部数据")
            .startWithItem("加载中...")
            .subscribe(
                { value -> log("  startWith onNext: $value") },
                { error -> log("  startWith onError: ${error.message}") },
                { log("  startWith onComplete\n") }
            )
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
