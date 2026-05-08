package com.peter.webview.demo

import android.os.Bundle
import android.webkit.WebView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.peter.webview.demo.util.LogWebChromeClient
import com.peter.webview.demo.util.LogWebViewClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * WebView Demo 基类
 *
 * 提供：
 * - WebView 初始化（启用 JS）
 * - 底部日志面板（实时显示 Android ↔ JS 通信事件）
 * - 生命周期管理
 *
 * 子类只需：
 * 1. 调用 setupWebView(htmlAsset) 加载 HTML 页面
 * 2. 重写 setupWebViewConfig() 做额外配置（如 addJavascriptInterface）
 * 3. 调用 log() 记录通信事件
 */
abstract class WebViewBaseActivity : AppCompatActivity() {

    protected lateinit var webView: WebView
    protected lateinit var tvLog: TextView
    protected lateinit var scrollView: ScrollView

    /** 子类可重写此属性，使用不同的布局（如 activity_webview_chat.xml） */
    protected open val layoutResId: Int = R.layout.activity_webview_log

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        webView = findViewById(R.id.webView)
        tvLog = findViewById(R.id.tvLog)
        scrollView = findViewById(R.id.scrollView)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = intent.getStringExtra("title") ?: ""
        toolbar.setNavigationOnClickListener { finish() }

        setupWebViewConfig()
        setupWebViewClient()
    }

    /**
     * 子类可重写此方法，做额外的 WebView 配置
     * 如：addJavascriptInterface, setWebChromeClient 等
     */
    protected open fun setupWebViewConfig() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
    }

    private fun setupWebViewClient() {
        webView.webViewClient = LogWebViewClient { tag, msg -> log(tag, msg) }
        webView.webChromeClient = LogWebChromeClient { tag, msg -> log(tag, msg) }
    }

    /**
     * 加载 assets/web/ 下的 HTML 文件
     */
    protected fun loadHtml(fileName: String) {
        webView.loadUrl("file:///android_asset/web/$fileName")
    }

    /**
     * 记录通信日志，带时间戳
     *
     * @param tag 标签，如 "Android→JS", "JS→Android"
     * @param message 日志内容
     */
    protected fun log(tag: String, message: String) {
        val time = timeFormat.format(Date())
        val logLine = "[$time] $tag: $message\n"
        runOnUiThread {
            tvLog.append(logLine)
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    /**
     * 清空日志
     */
    protected fun clearLog() {
        tvLog.text = ""
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
