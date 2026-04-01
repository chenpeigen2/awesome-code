package com.peter.rxjava.demo.subjects

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.rxjava.demo.R
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject

/**
 * Subject 热观测演示
 *
 * 本 Activity 展示 RxJava 中四种 Subject 的区别：
 *
 * ## 什么是 Subject？
 * Subject 既是 Observable（可被订阅）又是 Observer（可发射数据），
 * 它是一种"热"的 Observable，可以向多个订阅者广播数据。
 *
 * ## 四种 Subject 对比：
 *
 * | Subject           | 新订阅者收到的数据                     |
 * |-------------------|--------------------------------------|
 * | PublishSubject    | 只收到订阅后发射的数据                  |
 * | BehaviorSubject   | 收到最后一个数据 + 后续数据              |
 * | ReplaySubject     | 收到所有历史数据 + 后续数据              |
 * | AsyncSubject      | 只收到 onComplete 前的最后一个数据       |
 *
 * ## 使用场景：
 * - PublishSubject: 事件总线、实时消息推送
 * - BehaviorSubject: UI 状态管理（新订阅者立即获得当前状态）
 * - ReplaySubject: 消息历史回放
 * - AsyncSubject: 等待最终结果
 *
 * @see io.reactivex.rxjava3.subjects.PublishSubject
 * @see io.reactivex.rxjava3.subjects.BehaviorSubject
 * @see io.reactivex.rxjava3.subjects.ReplaySubject
 * @see io.reactivex.rxjava3.subjects.AsyncSubject
 */
class SubjectsActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.subjects)
        toolbar.setNavigationOnClickListener { finish() }

        tvLog = findViewById(R.id.tvLog)

        demonstratePublishSubject()
        demonstrateBehaviorSubject()
        demonstrateReplaySubject()
        demonstrateAsyncSubject()
    }

    /**
     * 演示 PublishSubject：只收到订阅后发射的数据
     *
     * 最简单的 Subject，新订阅者不会收到之前发射的数据。
     */
    private fun demonstratePublishSubject() {
        log("========== PublishSubject ==========")
        val subject = PublishSubject.create<String>()

        // 第一个订阅者
        subject.subscribe { value -> log("  订阅者1: $value") }
        log("  订阅者1 已订阅")

        subject.onNext("数据A")
        subject.onNext("数据B")

        // 第二个订阅者（之后才订阅）
        subject.subscribe { value -> log("  订阅者2: $value") }
        log("  订阅者2 已订阅（不会收到 A 和 B）")

        subject.onNext("数据C")
        subject.onComplete()
        log("  结论: 订阅者2 只收到了数据C\n")
    }

    /**
     * 演示 BehaviorSubject：收到最后一个数据 + 后续数据
     *
     * BehaviorSubject 会记住最后一个发射的数据，
     * 新订阅者会立即收到这个缓存的数据。
     * 非常适合 UI 状态管理场景。
     */
    private fun demonstrateBehaviorSubject() {
        log("========== BehaviorSubject ==========")
        val subject = BehaviorSubject.createDefault("初始值")

        subject.subscribe { value -> log("  订阅者1: $value") }
        log("  订阅者1 已订阅（收到初始值）")

        subject.onNext("更新1")
        subject.onNext("更新2")

        subject.subscribe { value -> log("  订阅者2: $value") }
        log("  订阅者2 已订阅（立即收到最后一个值: 更新2）")

        subject.onNext("更新3")
        subject.onComplete()
        log("  结论: 订阅者2 收到了「更新2」和「更新3」\n")
    }

    /**
     * 演示 ReplaySubject：收到所有历史数据
     *
     * ReplaySubject 会缓存所有发射过的数据，
     * 新订阅者会收到所有历史数据。
     */
    private fun demonstrateReplaySubject() {
        log("========== ReplaySubject ==========")
        val subject = ReplaySubject.create<String>()

        subject.subscribe { value -> log("  订阅者1: $value") }
        log("  订阅者1 已订阅")

        subject.onNext("历史数据1")
        subject.onNext("历史数据2")
        subject.onNext("历史数据3")

        subject.subscribe { value -> log("  订阅者2: $value") }
        log("  订阅者2 已订阅（回放所有历史数据）")

        subject.onNext("新数据")
        subject.onComplete()
        log("  结论: 订阅者2 收到了所有数据（1,2,3 + 新数据）\n")
    }

    /**
     * 演示 AsyncSubject：只收到最后一个数据
     *
     * AsyncSubject 只会在 onComplete 后发射最后一个数据。
     * 所有订阅者都只会收到同一个值——最后一个。
     */
    private fun demonstrateAsyncSubject() {
        log("========== AsyncSubject ==========")
        val subject = AsyncSubject.create<String>()

        subject.subscribe { value -> log("  订阅者1: $value") }
        log("  订阅者1 已订阅")

        subject.onNext("数据A")
        subject.onNext("数据B")
        subject.onNext("数据C") // 最后一个

        subject.subscribe { value -> log("  订阅者2: $value") }
        log("  订阅者2 已订阅")

        subject.onComplete() // 只有 onComplete 后才发射
        log("  结论: 所有订阅者都只收到了「数据C」（最后一个）\n")
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        tvLog.text = logBuilder.toString()
    }
}
