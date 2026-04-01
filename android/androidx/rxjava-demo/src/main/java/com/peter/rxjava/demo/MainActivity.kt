package com.peter.rxjava.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.rxjava.demo.databinding.ActivityMainBinding

/**
 * RxJava Demo 主入口
 *
 * 本 Demo 包含以下内容：
 *
 * 一、RxJava 基础
 * 1. Observable 创建 - just/fromIterable/create/range/interval
 * 2. Single / Maybe / Completable - 四种响应式类型对比
 * 3. Disposable 资源管理 - 订阅取消与内存泄漏防范
 *
 * 二、操作符
 * 1. 变换操作符 - map/flatMap/concatMap/switchMap
 * 2. 过滤操作符 - filter/distinct/take/skip/debounce
 * 3. 组合操作符 - zip/merge/combineLatest/concat
 *
 * 三、调度器
 * 1. Schedulers 线程调度 - IO/Computation/Main/NewThread/Single
 *
 * 四、Subject
 * 1. Subject 热观测 - Publish/Behavior/Replay/AsyncSubject
 *
 * 五、错误处理
 * 1. 错误处理操作符 - onErrorReturn/onErrorResumeNext/retry/retryWhen
 *
 * 六、Android 集成
 * 1. RxAndroid 集成 - AndroidSchedulers.mainThread + Lifecycle
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(getMenuItems()) { item ->
                item.intent?.let { startActivity(it) }
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // 一、RxJava 基础
            MenuItem(
                title = getString(R.string.basics_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.observable),
                description = getString(R.string.observable_desc),
                intent = createObservableIntent(this)
            ),
            MenuItem(
                title = getString(R.string.single_maybe_completable),
                description = getString(R.string.single_maybe_completable_desc),
                intent = createSingleMaybeCompletableIntent(this)
            ),
            MenuItem(
                title = getString(R.string.disposable),
                description = getString(R.string.disposable_desc),
                intent = createDisposableIntent(this)
            ),

            // 二、操作符
            MenuItem(
                title = getString(R.string.operators_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.transform_operators),
                description = getString(R.string.transform_operators_desc),
                intent = createTransformOperatorsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.filter_operators),
                description = getString(R.string.filter_operators_desc),
                intent = createFilterOperatorsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.combine_operators),
                description = getString(R.string.combine_operators_desc),
                intent = createCombineOperatorsIntent(this)
            ),

            // 三、调度器
            MenuItem(
                title = getString(R.string.schedulers_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.schedulers),
                description = getString(R.string.schedulers_desc),
                intent = createSchedulersIntent(this)
            ),

            // 四、Subject
            MenuItem(
                title = getString(R.string.subjects_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.subjects),
                description = getString(R.string.subjects_desc),
                intent = createSubjectsIntent(this)
            ),

            // 五、错误处理
            MenuItem(
                title = getString(R.string.error_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.error_handling),
                description = getString(R.string.error_handling_desc),
                intent = createErrorHandlingIntent(this)
            ),

            // 六、Android 集成
            MenuItem(
                title = getString(R.string.android_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.rx_android),
                description = getString(R.string.rx_android_desc),
                intent = createRxAndroidIntent(this)
            )
        )
    }
}
