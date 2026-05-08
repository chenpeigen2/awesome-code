package com.peter.webview.demo.advanced

import android.net.Uri
import android.os.Bundle
import android.webkit.WebMessage
import android.webkit.WebMessagePort
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import com.peter.webview.demo.R
import com.peter.webview.demo.WebViewBaseActivity

/**
 * Demo 6: WebMessagePort（API 23+）
 *
 * === 通信原理 ===
 *
 * WebMessagePort 是 Android 提供的安全双向通信机制。
 * 与 @JavascriptInterface 不同，它不暴露 Java 对象给 JS，
 * 而是通过 MessagePort 通道进行结构化的消息传递。
 *
 * 核心 API：
 * 1. webView.createWebMessageChannel() → WebMessagePort[]
 *    创建一对消息端口（类似管道的两端）
 *
 * 2. port.setWebMessageCallback(callback)
 *    设置接收消息的回调
 *
 * 3. port.postMessage(webMessage)
 *    发送消息
 *
 * 4. webView.postWebMessage(webMessage, targetOrigin)
 *    向 WebView 发送消息（附带一个端口）
 *
 * 数据流：
 * ┌──────────┐                    ┌──────────┐
 * │          │  channel[0] ←→ channel[1]  │          │
 * │ Android  │                    │   JS     │
 * │          │  WebMessage        │          │
 * │          │  (结构化数据)       │          │
 * └──────────┘                    └──────────┘
 *
 * 对比 @JavascriptInterface：
 * - @JavascriptInterface：暴露 Java 对象，JS 直接调用方法
 * - WebMessagePort：不暴露对象，通过消息通道通信，更安全
 * - WebMessagePort 支持 transferable objects（如 ArrayBuffer）
 *
 * 适用场景：
 * - 安全要求较高的场景（不暴露原生对象）
 * - 需要传递二进制数据（ArrayBuffer）
 * - Service Worker 通信
 */
class WebMessagePortActivity : WebViewBaseActivity() {

    override val layoutResId = R.layout.activity_webview_chat

    private var nativePort: WebMessagePort? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadHtml("web_message_port.html")

        // 等页面加载完成后建立消息通道
        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                setupMessageChannel()
                setupButtons()
            }
        }
    }

    /**
     * 建立 WebMessagePort 消息通道
     *
     * 步骤：
     * 1. createWebMessageChannel() 创建一对端口 [port1, port2]
     * 2. port1 留在 Android 端，用于收发消息
     * 3. port2 通过 postWebMessage() 传递给 JS 端
     * 4. JS 端通过 navigator.messageChannel 接收 port2
     */
    private fun setupMessageChannel() {
        // 创建消息通道，返回两个端口
        val channel = webView.createWebMessageChannel()

        // channel[0]：Android 端保留的端口
        nativePort = channel[0]
        channel[0].setWebMessageCallback(object : WebMessagePort.WebMessageCallback() {
            override fun onMessage(port: WebMessagePort, message: WebMessage?) {
                val data = message?.data ?: ""
                log("JS→Android", "WebMessagePort 收到: $data")
                runOnUiThread {
                    findViewById<android.widget.TextView>(R.id.tvReceived).text = "来自 JS: $data"
                }
            }
        })

        // channel[1]：发送给 JS 端的端口
        // JS 端通过 navigator.serviceWorker.controller.postMessage() 或
        // window.onmessage 接收
        webView.postWebMessage(
            WebMessage("port", arrayOf(channel[1])),
            Uri.EMPTY
        )
        log("Android", "消息通道已建立")
    }

    private fun setupButtons() {
        val etInput = findViewById<EditText>(R.id.etInput)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val message = etInput.text.toString()
            if (message.isNotEmpty()) {
                nativePort?.let { port ->
                    log("Android→JS", "WebMessagePort 发送: $message")
                    port.postMessage(WebMessage(message))
                    etInput.text.clear()
                } ?: log("Android", "错误: 消息通道未建立")
            }
        }
    }

    override fun onDestroy() {
        nativePort?.close()
        super.onDestroy()
    }
}
