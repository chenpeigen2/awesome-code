package com.peter.webview.demo.intermediate

import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.peter.webview.demo.WebViewBaseActivity

/**
 * Demo 3: URL Scheme 拦截
 *
 * === 通信原理 ===
 *
 * JS 通过修改 window.location 或创建 <a> 标签跳转到自定义协议的 URL。
 * Android 在 shouldOverrideUrlLoading() 中拦截这些 URL，解析参数后执行原生逻辑。
 *
 * 核心 API：
 * - shouldOverrideUrlLoading(view, request)
 *   - 返回 true：拦截该 URL，不让 WebView 加载
 *   - 返回 false：放行，让 WebView 正常加载
 *
 * URL 格式约定：
 * jsbridge://method?key1=value1&key2=value2
 *
 * 对比 @JavascriptInterface：
 * - @JavascriptInterface：直接调用，同步返回，需要 API 17+
 * - URL Scheme：异步，无法直接返回值，但兼容性更好
 * - URL Scheme 更适合需要触发页面跳转的场景
 *
 * 适用场景：
 * - 不方便使用 addJavascriptInterface 的场景
 * - 需要拦截 URL 跳转做路由分发
 * - 与 iOS 保持一致的通信协议（iOS 也用这种方式）
 */
class UrlSchemeActivity : WebViewBaseActivity() {

    companion object {
        private const val SCHEME = "jsbridge"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置自定义 WebViewClient 拦截 URL
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                // 只拦截 jsbridge:// 协议的 URL
                if (!url.startsWith("$SCHEME://")) {
                    return false
                }

                log("JS→Android", "拦截 URL: $url")

                val uri = Uri.parse(url)
                val method = uri.host ?: ""
                val params = mutableMapOf<String, String>()
                uri.queryParameterNames.forEach { key ->
                    params[key] = uri.getQueryParameter(key) ?: ""
                }

                log("JS→Android", "解析: method=$method, params=$params")

                // 根据 method 分发处理
                when (method) {
                    "showToast" -> {
                        val message = params["msg"] ?: "No message"
                        log("Android", "执行: Toast(\"$message\")")
                        android.widget.Toast.makeText(this@UrlSchemeActivity, message, android.widget.Toast.LENGTH_SHORT).show()
                    }
                    "getData" -> {
                        val data = """{"temperature":25,"weather":"sunny"}"""
                        log("Android→JS", "返回数据: $data")
                        // URL Scheme 无法直接返回值，需要通过 evaluateJavascript 回传
                        view?.evaluateJavascript("onDataReceived('$data')", null)
                    }
                    "navigate" -> {
                        val page = params["page"] ?: "unknown"
                        log("Android", "执行: 导航到 $page")
                    }
                    else -> {
                        log("Android", "未知方法: $method")
                    }
                }

                return true // 拦截，不让 WebView 加载这个 URL
            }
        }

        loadHtml("url_scheme.html")
    }
}
