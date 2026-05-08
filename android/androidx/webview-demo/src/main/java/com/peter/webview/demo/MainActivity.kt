package com.peter.webview.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.webview.demo.databinding.ActivityMainBinding

/**
 * WebView ↔ Android 原生通信 Demo
 *
 * 本 Demo 从简单到复杂展示 WebView 与 Android 原生的 6 种通信方式：
 *
 * 一、基础通信
 * 1. Android → JS — evaluateJavascript() 调用 JS 函数
 * 2. JS → Android — @JavascriptInterface 暴露原生方法
 *
 * 二、中级通信
 * 3. URL Scheme 拦截 — shouldOverrideUrlLoading 拦截自定义协议
 * 4. onJsPrompt 拦截 — 拦截 window.prompt() 实现 JS 调用原生
 *
 * 三、高级通信
 * 5. 双向通信综合案例 — 组合 evaluateJavascript + JavascriptInterface
 * 6. WebMessagePort — API 23+ postMessage 安全通道
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(getMenuItems()) { item ->
                item.intent?.let { intent ->
                    intent.putExtra("title", item.title)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> = listOf(
        // 一、基础通信
        MenuItem(title = getString(R.string.basics_title), isHeader = true),
        MenuItem(
            title = getString(R.string.android_call_js),
            description = getString(R.string.android_call_js_desc),
            intent = createAndroidCallJsIntent(this)
        ),
        MenuItem(
            title = getString(R.string.js_call_android),
            description = getString(R.string.js_call_android_desc),
            intent = createJsCallAndroidIntent(this)
        ),

        // 二、中级通信
        MenuItem(title = getString(R.string.intermediate_title), isHeader = true),
        MenuItem(
            title = getString(R.string.url_scheme),
            description = getString(R.string.url_scheme_desc),
            intent = createUrlSchemeIntent(this)
        ),
        MenuItem(
            title = getString(R.string.js_prompt),
            description = getString(R.string.js_prompt_desc),
            intent = createJsPromptIntent(this)
        ),

        // 三、高级通信
        MenuItem(title = getString(R.string.advanced_title), isHeader = true),
        MenuItem(
            title = getString(R.string.bidirectional),
            description = getString(R.string.bidirectional_desc),
            intent = createBidirectionalIntent(this)
        ),
        MenuItem(
            title = getString(R.string.web_message_port),
            description = getString(R.string.web_message_port_desc),
            intent = createWebMessagePortIntent(this)
        )
    )
}
