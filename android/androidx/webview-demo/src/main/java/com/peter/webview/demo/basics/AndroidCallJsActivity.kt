package com.peter.webview.demo.basics

import android.os.Bundle
import android.webkit.WebView
import com.peter.webview.demo.R
import com.peter.webview.demo.WebViewBaseActivity

/**
 * Demo 1: Android 调用 JavaScript
 *
 * === 通信原理 ===
 *
 * Android 通过 WebView 的 evaluateJavascript() 方法调用 JS 函数。
 * 这是最直接的单向通信方式：Android → JS。
 *
 * 核心 API：
 * - webView.evaluateJavascript("jsFunction(args)", callback)
 *   - 第1个参数：要执行的 JS 代码字符串
 *   - 第2个参数：ValueCallback<String>，接收 JS 函数的返回值
 *
 * 对比旧方案：
 * - 旧方案：webView.loadUrl("javascript:jsFunction(args)") — 无法获取返回值
 * - 新方案：evaluateJavascript() — 可获取返回值，异步回调
 *
 * 适用场景：
 * - 原生端主动向 WebView 发送数据
 * - 调用 JS 函数并获取返回值
 * - 动态更新 WebView 页面内容
 */
class AndroidCallJsActivity : WebViewBaseActivity() {

    override val layoutResId = R.layout.activity_webview_buttons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 按钮1：调用 JS 函数，传入字符串参数
        findViewById<android.widget.Button>(R.id.btnCallSimple).setOnClickListener {
            log("Android→JS", "evaluateJavascript('showMessage(\"Hello from Android!\")')")
            webView.evaluateJavascript("showMessage('Hello from Android!')") { result ->
                log("JS→Android", "返回值: $result")
            }
        }

        // 按钮2：调用 JS 函数，获取 JSON 数据
        findViewById<android.widget.Button>(R.id.btnCallGetJson).setOnClickListener {
            log("Android→JS", "evaluateJavascript('getUserData()')")
            webView.evaluateJavascript("getUserData()") { result ->
                log("JS→Android", "返回 JSON: $result")
            }
        }

        // 按钮3：调用 JS 函数，更新页面内容
        findViewById<android.widget.Button>(R.id.btnCallUpdate).setOnClickListener {
            val newText = "更新时间: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}"
            log("Android→JS", "evaluateJavascript('updateContent(\"$newText\")')")
            webView.evaluateJavascript("updateContent('$newText')") { result ->
                log("JS→Android", "更新完成: $result")
            }
        }

        // 按钮4：loadUrl 旧方案对比
        findViewById<android.widget.Button>(R.id.btnCallLoadUrl).setOnClickListener {
            log("Android→JS", "loadUrl('javascript:...')  — 旧方案，无返回值")
            webView.loadUrl("javascript:showMessage('This is loadUrl method (old way)')")
        }

        loadHtml("android_call_js.html")
    }
}
