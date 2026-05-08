package com.peter.webview.demo.intermediate

import android.os.Bundle
import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.peter.webview.demo.WebViewBaseActivity

/**
 * Demo 4: onJsPrompt 拦截
 *
 * === 通信原理 ===
 *
 * JS 调用 window.prompt(message) 时，Android 的 WebChromeClient.onJsPrompt() 会被触发。
 * 我们可以将 prompt 的 message 作为通信协议，解析后执行原生逻辑，并通过 JsResult 返回结果。
 *
 * 核心 API：
 * - WebChromeClient.onJsPrompt(view, url, message, defaultValue, result)
 *   - message：JS 传入的 prompt 内容，我们用它传递命令
 *   - result.confirm(returnValue)：向 JS 返回结果
 *   - return true：拦截 prompt，不让系统弹窗
 *
 * 通信协议约定：
 * prompt 内容格式："jsbridge://method?param=value"
 *
 * 对比其他方式：
 * - @JavascriptInterface：推荐，但需要 addJavascriptInterface
 * - URL Scheme：只能单向，无法直接返回值
 * - onJsPrompt：可以返回值，不需要 addJavascriptInterface，但会触发 prompt 调用
 *
 * 适用场景：
 * - 不方便调用 addJavascriptInterface 的场景（如 WebView 在第三方 SDK 中）
 * - 需要同步返回值的 JS→Android 通信
 * - 与旧版 JSBridge 库兼容
 */
@Deprecated(message = "")
class JsPromptActivity : WebViewBaseActivity() {

    override fun setupWebViewConfig() {
        super.setupWebViewConfig()

        webView.webChromeClient = object : WebChromeClient() {

            /**
             * 拦截 JS 的 window.prompt() 调用
             *
             * JS 代码：var result = window.prompt("jsbridge://getUserInfo")
             * Android 解析 message，执行逻辑，通过 result.confirm() 返回值
             * JS 的 prompt() 调用会同步返回这个值
             */
            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                // 只拦截 jsbridge:// 协议的 prompt
                if (message == null || !message.startsWith("jsbridge://")) {
                    return super.onJsPrompt(view, url, message, defaultValue, result)
                }

                log("JS→Android", "拦截 prompt: $message")

                val uri = android.net.Uri.parse(message)
                val method = uri.host ?: ""

                when (method) {
                    "getUserInfo" -> {
                        val userInfo = """{"name":"Peter","role":"Android Developer"}"""
                        log("Android→JS", "返回: $userInfo")
                        result?.confirm(userInfo)
                    }
                    "calculate" -> {
                        val a = uri.getQueryParameter("a")?.toIntOrNull() ?: 0
                        val b = uri.getQueryParameter("b")?.toIntOrNull() ?: 0
                        val sum = a + b
                        log("Android→JS", "calculate($a, $b) = $sum")
                        result?.confirm(sum.toString())
                    }
                    "getTimestamp" -> {
                        val timestamp = System.currentTimeMillis()
                        log("Android→JS", "timestamp = $timestamp")
                        result?.confirm(timestamp.toString())
                    }
                    else -> {
                        log("Android", "未知方法: $method")
                        result?.confirm("error: unknown method")
                    }
                }

                return true // 拦截，不弹出系统 prompt 对话框
            }

            // 保留 console.log 拦截
            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    log("JS-console", "${it.messageLevel()}: ${it.message()}")
                }
                return true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadHtml("js_prompt.html")
    }
}
