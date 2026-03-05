package com.peter.lifecycle.demo.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.lifecycle.demo.databinding.ActivityLifecycleObserverBinding

/**
 * LifecycleObserver 示例
 * 
 * 知识点：
 * 1. DefaultLifecycleObserver - 推荐方式，使用接口默认方法
 * 2. @OnLifecycleEvent - 已废弃，但仍可用（需要额外依赖）
 * 3. 生命周期方法：onCreate, onStart, onResume, onPause, onStop, onDestroy
 * 
 * 优点：
 * - 解耦：将生命周期相关逻辑从 Activity/Fragment 中分离
 * - 复用：同一个 Observer 可以被多个组件使用
 * - 测试：独立的生命周期逻辑更容易测试
 */
class LifecycleObserverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLifecycleObserverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLifecycleObserverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 注册生命周期观察者
        lifecycle.addObserver(MyLocationObserver(this))
        lifecycle.addObserver(AnalyticsObserver())
        
        binding.tvInfo.text = """
            本示例演示 LifecycleObserver 的基本用法。
            
            已注册两个 Observer：
            1. MyLocationObserver - 模拟位置监听
            2. AnalyticsObserver - 模拟数据统计
            
            查看 Logcat 输出，观察生命周期回调。
        """.trimIndent()
    }
}
