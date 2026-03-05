package com.peter.layoutinflater.demo.async

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.peter.layoutinflater.demo.R
import com.peter.layoutinflater.demo.databinding.ActivityAsyncInflateBinding

/**
 * AsyncLayoutInflater 异步加载示例
 * 
 * 【核心概念】
 * AsyncLayoutInflater 是 AndroidX 提供的工具类，用于在后台线程加载布局，
 * 避免复杂布局加载阻塞主线程导致 ANR。
 * 
 * 【依赖】
 * implementation "androidx.asynclayoutinflater:asynclayoutinflater:1.0.0"
 * 
 * 【使用场景】
 * 1. 复杂布局首次加载
 * 2. 启动优化 - 延迟加载非关键 UI
 * 3. 动态加载大型视图
 */
class AsyncInflateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsyncInflateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsyncInflateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnSyncInflate.setOnClickListener {
            syncInflate()
        }

        binding.btnAsyncInflate.setOnClickListener {
            asyncInflate()
        }

        binding.btnClear.setOnClickListener {
            clearContainer()
        }
    }

    /**
     * 同步加载 - 在主线程执行
     * 
     * 【特点】
     * - 阻塞主线程
     * - 布局越复杂，耗时越长
     * - 可能导致 ANR
     */
    private fun syncInflate() {
        clearContainer()
        binding.progressBar.visibility = View.VISIBLE
        binding.tvTime.text = "正在同步加载..."

        val startTime = SystemClock.elapsedRealtime()

        // 【同步加载】在主线程执行
        // 如果布局复杂，会阻塞 UI 线程
        val view = LayoutInflater.from(this).inflate(
            R.layout.item_async_demo,
            binding.container,
            false
        )

        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime

        binding.container.addView(view)
        binding.progressBar.visibility = View.GONE

        binding.tvTime.text = buildString {
            append("【同步加载完成】\n")
            append("耗时: ${duration}ms\n")
            append("线程: ${Thread.currentThread().name}")
        }

        Toast.makeText(this, "同步加载完成: ${duration}ms", Toast.LENGTH_SHORT).show()
    }

    /**
     * 异步加载 - 在后台线程执行
     * 
     * 【特点】
     * - 不阻塞主线程
     * - 回调在主线程执行
     * - 适合复杂布局
     */
    private fun asyncInflate() {
        clearContainer()
        binding.progressBar.visibility = View.VISIBLE
        binding.tvTime.text = "正在异步加载..."

        val startTime = SystemClock.elapsedRealtime()

        // 【异步加载】使用 AsyncLayoutInflater
        val asyncInflater = AsyncLayoutInflater(this)

        asyncInflater.inflate(
            R.layout.item_async_demo,
            null
        ) { view, parentId, parent ->
            // 【回调】在主线程执行，可以安全操作 UI
            val endTime = SystemClock.elapsedRealtime()
            val duration = endTime - startTime

            binding.container.addView(view)
            binding.progressBar.visibility = View.GONE

            binding.tvTime.text = buildString {
                append("【异步加载完成】\n")
                append("耗时: ${duration}ms\n")
                append("回调线程: ${Thread.currentThread().name}\n")
                append("View 已添加到容器")
            }

            Toast.makeText(this, "异步加载完成: ${duration}ms", Toast.LENGTH_SHORT).show()
        }

        // 注意：inflate 调用后立即返回，不会等待加载完成
    }

    private fun clearContainer() {
        binding.container.removeAllViews()
        binding.tvTime.text = ""
    }
}

/**
 * 【完整示例】AsyncLayoutInflater 的最佳实践
 * 
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 * 
 *     // 1. 预加载优化
 *     private var preInflatedView: View? = null
 *     private val asyncInflater by lazy { AsyncLayoutInflater(this) }
 * 
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView(R.layout.activity_main)
 * 
 *         // 在合适的时机预加载
 *         preInflateComplexLayout()
 *     }
 * 
 *     // 预加载复杂布局
 *     private fun preInflateComplexLayout() {
 *         asyncInflater.inflate(R.layout.complex_layout, null) { view, _, _ ->
 *             preInflatedView = view
 *         }
 *     }
 * 
 *     // 需要显示时直接使用
 *     private fun showComplexLayout() {
 *         preInflatedView?.let { view ->
 *             container.addView(view)
 *             preInflatedView = null // 清空缓存
 *         } ?: run {
 *             // 如果还没加载完，显示加载中
 *             showLoading()
 *         }
 *     }
 * }
 * ```
 * 
 * 【原理】
 * AsyncLayoutInflater 内部：
 * 1. 使用 HandlerThread 创建后台线程
 * 2. 在后台线程调用 LayoutInflater.inflate()
 * 3. 通过 Handler 将结果回调到主线程
 * 
 * 【源码简化】
 * ```java
 * public class AsyncLayoutInflater {
 *     private static final HandlerThread sBackgroundThread;
 *     private static final Handler sBackgroundHandler;
 * 
 *     static {
 *         sBackgroundThread = new HandlerThread("AsyncLayoutInflater");
 *         sBackgroundThread.start();
 *         sBackgroundHandler = new Handler(sBackgroundThread.getLooper());
 *     }
 * 
 *     public void inflate(int resid, ViewGroup parent, OnInflateFinishedListener callback) {
 *         sBackgroundHandler.post(() -> {
 *             // 后台线程加载布局
 *             View view = mInflater.inflate(resid, parent, false);
 * 
 *             // 切换到主线程回调
 *             mHandler.post(() -> callback.onInflateFinished(view, resid, parent));
 *         });
 *     }
 * }
 * ```
 */
