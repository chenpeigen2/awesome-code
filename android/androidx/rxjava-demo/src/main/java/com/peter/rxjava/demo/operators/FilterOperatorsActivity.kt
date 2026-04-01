package com.peter.rxjava.demo.operators

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * 过滤操作符演示
 *
 * 本 Activity 展示 RxJava 中的过滤操作符：
 *
 * ## 过滤操作符：
 * 用于从数据流中选择性地过滤数据。
 *
 * 1. **filter**: 根据条件过滤数据
 * 2. **distinct**: 去除重复数据
 * 3. **take**: 只取前 N 个数据
 * 4. **skip**: 跳过前 N 个数据
 * 5. **debounce**: 防抖，只在安静期后发射最后一个数据
 * 6. **throttleFirst**: 节流，在时间窗口内只取第一个
 * 7. **firstElement / lastElement**: 取第一个/最后一个
 * 8. **elementAt**: 取指定位置的数据
 */
class FilterOperatorsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.filter_operators)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstrateFilter()
        demonstrateDistinct()
        demonstrateTake()
        demonstrateSkip()
        demonstrateFirstLast()
        demonstrateElementAt()
    }

    /**
     * 演示 filter()：条件过滤
     */
    private fun demonstrateFilter() {
        log("========== filter() 条件过滤 ==========")
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .filter { it % 2 == 0 } // 只保留偶数
            .subscribe(
                { value -> log("  filter onNext: $value（偶数）") },
                { error -> log("  filter onError: ${error.message}") },
                { log("  filter onComplete\n") }
            )
    }

    /**
     * 演示 distinct()：去重
     */
    private fun demonstrateDistinct() {
        log("========== distinct() 去重 ==========")
        Observable.just("A", "B", "A", "C", "B", "D", "A")
            .distinct()
            .subscribe(
                { value -> log("  distinct onNext: $value") },
                { error -> log("  distinct onError: ${error.message}") },
                { log("  distinct onComplete\n") }
            )
    }

    /**
     * 演示 take()：只取前 N 个
     */
    private fun demonstrateTake() {
        log("========== take() 取前 N 个 ==========")
        Observable.just("A", "B", "C", "D", "E")
            .take(3) // 只取前 3 个
            .subscribe(
                { value -> log("  take(3) onNext: $value") },
                { error -> log("  take onError: ${error.message}") },
                { log("  take onComplete\n") }
            )
    }

    /**
     * 演示 skip()：跳过前 N 个
     */
    private fun demonstrateSkip() {
        log("========== skip() 跳过前 N 个 ==========")
        Observable.just("A", "B", "C", "D", "E")
            .skip(2) // 跳过前 2 个
            .subscribe(
                { value -> log("  skip(2) onNext: $value") },
                { error -> log("  skip onError: ${error.message}") },
                { log("  skip onComplete\n") }
            )
    }

    /**
     * 演示 firstElement() / lastElement()
     */
    private fun demonstrateFirstLast() {
        log("========== firstElement / lastElement ==========")
        Observable.just(10, 20, 30, 40, 50)
            .firstElement()
            .subscribe { value -> log("  firstElement: $value") }

        Observable.just(10, 20, 30, 40, 50)
            .lastElement()
            .subscribe { value -> log("  lastElement: $value\n") }
    }

    /**
     * 演示 elementAt()：取指定位置
     */
    private fun demonstrateElementAt() {
        log("========== elementAt() 指定位置 ==========")
        Observable.just("A", "B", "C", "D", "E")
            .elementAt(2) // 取索引为 2 的数据（即 "C"）
            .subscribe(
                { value -> log("  elementAt(2): $value\n") },
                { error -> log("  elementAt onError: ${error.message}\n") }
            )
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
