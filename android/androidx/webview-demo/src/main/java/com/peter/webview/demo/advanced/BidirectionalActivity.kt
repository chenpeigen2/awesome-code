package com.peter.webview.demo.advanced

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import com.peter.webview.demo.R
import com.peter.webview.demo.WebViewBaseActivity

/**
 * Demo 5: 双向通信综合案例
 *
 * === 通信原理 ===
 *
 * 组合使用两种通信方式实现完整的双向通信：
 * 1. JS → Android：@JavascriptInterface（JS 调用 window.Android.sendToNative()）
 * 2. Android → JS：evaluateJavascript（Android 调用 JS 的 receiveFromNative()）
 *
 * 数据流：
 * ┌──────────┐    sendToNative(msg)     ┌──────────┐
 * │          │ ──────────────────────→  │          │
 * │   JS     │                          │ Android  │
 * │  (Web)   │  ←────────────────────── │  (原生)   │
 * │          │    receiveFromNative(msg) │          │
 * └──────────┘                          └──────────┘
 *
 * 适用场景：
 * - Hybrid 应用中的实时通信
 * - 聊天、表单提交、数据同步等
 * - 需要频繁双向交互的场景
 */
class BidirectionalActivity : WebViewBaseActivity() {

    override val layoutResId = R.layout.activity_webview_chat

    inner class JsBridge {
        /**
         * JS 发送消息到 Android
         * JS 代码：window.Android.sendToNative("hello")
         */
        @JavascriptInterface
        fun sendToNative(message: String) {
            log("JS→Android", "sendToNative(\"$message\")")
            runOnUiThread {
                findViewById<android.widget.TextView>(R.id.tvReceived).text = "来自 JS: $message"
            }
        }
    }

    override fun setupWebViewConfig() {
        super.setupWebViewConfig()
        webView.addJavascriptInterface(JsBridge(), "Android")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadHtml("bidirectional.html")

        // Android 输入框 + 发送按钮
        val etInput = findViewById<EditText>(R.id.etInput)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val message = etInput.text.toString()
            if (message.isNotEmpty()) {
                log("Android→JS", "receiveFromNative(\"$message\")")
                // Android 调用 JS 函数，将消息发送到 WebView
                webView.evaluateJavascript("receiveFromNative('$message')", null)
                etInput.text.clear()
            }
        }
    }
}
