package com.peter.webview.demo.util

import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * 通用 WebViewClient，拦截 JS console.log 输出到日志面板
 */
class LogWebViewClient(
    private val onLog: (tag: String, message: String) -> Unit
) : WebViewClient()

class LogWebChromeClient(
    private val onLog: (tag: String, message: String) -> Unit
) : WebChromeClient() {

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        consoleMessage?.let {
            onLog("JS-console", "${it.messageLevel()}: ${it.message()} [${it.sourceId()}:${it.lineNumber()}]")
        }
        return true
    }
}
