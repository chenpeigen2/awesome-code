package com.peter.webview.demo.basics

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.peter.webview.demo.R
import com.peter.webview.demo.WebViewBaseActivity

/**
 * Demo 2: JS 调用 Android（@JavascriptInterface）
 *
 * === 通信原理 ===
 *
 * Android 通过 addJavascriptInterface() 将一个 Java 对象暴露给 JS。
 * JS 通过 window.<interfaceName>.<method>() 调用原生方法。
 *
 * 核心 API：
 * - webView.addJavascriptInterface(javaObject, "interfaceName")
 *   - javaObject：要暴露给 JS 的 Java/Kotlin 对象
 *   - interfaceName：JS 中的全局变量名（如 "Android"）
 * - @JavascriptInterface 注解：标记哪些方法可以被 JS 调用
 *
 * 安全注意事项：
 * - 只有标注 @JavascriptInterface 的方法才会暴露给 JS
 * - API 17+ 强制要求注解，防止任意方法被调用
 * - 不要在暴露的方法中执行敏感操作（如文件删除、支付）
 * - 建议对 JS 传入的参数做校验
 *
 * 适用场景：
 * - JS 需要调用原生能力（如 Toast、获取设备信息）
 * - JS 需要向原生传递数据
 * - Hybrid 应用中 JS 调用原生 API
 */
class JsCallAndroidActivity : WebViewBaseActivity() {

    /**
     * 暴露给 JS 的接口对象
     *
     * JS 通过 window.Android.xxx() 调用这些方法
     */
    inner class JsBridge {

        /**
         * JS 调用：显示 Toast
         * JS 代码：window.Android.showToast("Hello")
         */
        @JavascriptInterface
        fun showToast(message: String) {
            log("JS→Android", "showToast(\"$message\")")
            runOnUiThread {
                Toast.makeText(this@JsCallAndroidActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * JS 调用：获取用户信息（返回 JSON 字符串）
         * JS 代码：const info = window.Android.getUserInfo()
         *
         * 注意：@JavascriptInterface 方法不能直接返回复杂对象，
         * 需要返回 JSON 字符串，由 JS 端 JSON.parse() 解析
         */
        @JavascriptInterface
        fun getUserInfo(): String {
            log("JS→Android", "getUserInfo() 被调用")
            val json = """{"name":"Peter","age":28,"city":"Shanghai"}"""
            log("Android→JS", "返回 JSON: $json")
            return json
        }

        /**
         * JS 调用：执行计算（演示参数传递和返回值）
         * JS 代码：const result = window.Android.calculate(10, 20)
         */
        @JavascriptInterface
        fun calculate(a: Int, b: Int): Int {
            val result = a + b
            log("JS→Android", "calculate($a, $b) = $result")
            return result
        }

        /**
         * JS 调用：异步回调（通过 evaluateJavascript 回传结果）
         *
         * @JavascriptInterface 方法在 JS 线程同步执行，
         * 如果需要异步操作，需要通过 evaluateJavascript 回传
         */
        @JavascriptInterface
        fun fetchData(callbackId: String) {
            log("JS→Android", "fetchData(callbackId=$callbackId) — 异步请求")
            // 模拟异步操作（如网络请求）
            Thread {
                Thread.sleep(1000)
                val data = """{"status":"ok","data":[1,2,3]}"""
                log("Android→JS", "异步回传: onCallback('$callbackId', '$data')")
                webView.post {
                    webView.evaluateJavascript("onCallback('$callbackId', '$data')", null)
                }
            }.start()
        }
    }

    override fun setupWebViewConfig() {
        super.setupWebViewConfig()
        // 将 JsBridge 对象暴露给 JS，JS 通过 window.Android 访问
        webView.addJavascriptInterface(JsBridge(), "Android")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadHtml("js_call_android.html")
    }
}
