# WebView ↔ Android 原生通信原理指南

## 目录

1. [通信架构总览](#1-通信架构总览)
2. [六种通信方式详解](#2-六种通信方式详解)
3. [方式对比表](#3-方式对比表)
4. [安全注意事项](#4-安全注意事项)
5. [面试常见问题](#5-面试常见问题)
6. [最佳实践](#6-最佳实践)

---

## 1. 通信架构总览

WebView 与 Android 原生的通信分为两个方向：

```
┌─────────────────────────────────────────────────────┐
│                    Android 原生                       │
│                                                      │
│  ┌──────────┐    evaluateJavascript()    ┌─────────┐│
│  │ Activity │ ─────────────────────────→ │ WebView ││
│  │          │ ←───────────────────────── │  (JS)   ││
│  └──────────┘    @JavascriptInterface     └─────────┘│
│                  URL Scheme 拦截                      │
│                  onJsPrompt 拦截                      │
│                  WebMessagePort                       │
└─────────────────────────────────────────────────────┘
```

**Android → JS**：只有一种核心方式 — `evaluateJavascript()`

**JS → Android**：有多种方式 — `@JavascriptInterface`、URL Scheme、`onJsPrompt`、`WebMessagePort`

---

## 2. 六种通信方式详解

### 2.1 Android → JS：evaluateJavascript()

**核心 API**：
```kotlin
webView.evaluateJavascript("jsFunction('arg')") { returnValue ->
    // JS 函数的返回值
}
```

**时序图**：
```
Android                    WebView(JS)
  │                            │
  │  evaluateJavascript(       │
  │    "showMessage('hi')"     │
  │    callback)               │
  │ ─────────────────────────→ │
  │                            │  执行 showMessage('hi')
  │                            │  return "JS received: hi"
  │  callback("JS received: hi")
  │ ←───────────────────────── │
```

**旧方案对比**：
```kotlin
// 旧方案：无法获取返回值
webView.loadUrl("javascript:showMessage('hi')")

// 新方案：可获取返回值，推荐使用
webView.evaluateJavascript("showMessage('hi')") { result -> }
```

**适用场景**：原生主动推送数据到 WebView、调用 JS 函数获取返回值

---

### 2.2 JS → Android：@JavascriptInterface

**核心 API**：
```kotlin
// Android 端
class JsBridge {
    @JavascriptInterface
    fun showToast(msg: String) { ... }
}
webView.addJavascriptInterface(JsBridge(), "Android")

// JS 端
window.Android.showToast("Hello")
```

**时序图**：
```
WebView(JS)                  Android
  │                            │
  │  window.Android.           │
  │    showToast("Hello")      │
  │ ─────────────────────────→ │
  │                            │  @JavascriptInterface
  │                            │  showToast("Hello")
  │                            │  → Toast 显示
```

**适用场景**：JS 调用原生能力（Toast、设备信息、文件操作等）

---

### 2.3 JS → Android：URL Scheme 拦截

**核心 API**：
```kotlin
// Android 端
override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
    val url = request.url.toString()
    if (url.startsWith("jsbridge://")) {
        // 解析 URL，执行原生逻辑
        return true  // 拦截
    }
    return false  // 放行
}

// JS 端
window.location = "jsbridge://showToast?msg=Hello"
```

**URL 格式约定**：
```
jsbridge://method?key1=value1&key2=value2
│         │     │
│         │     └── 查询参数
│         └── 方法名（host）
└── 自定义协议
```

**适用场景**：与 iOS 保持一致的通信协议、路由分发

---

### 2.4 JS → Android：onJsPrompt 拦截

**核心 API**：
```kotlin
// Android 端
override fun onJsPrompt(view: WebView, url: String, message: String,
                        defaultValue: String, result: JsPromptResult): Boolean {
    if (message.startsWith("jsbridge://")) {
        // 解析 message，执行逻辑
        result.confirm("return value")  // 返回值给 JS
        return true  // 拦截，不弹系统对话框
    }
    return super.onJsPrompt(view, url, message, defaultValue, result)
}

// JS 端
var result = window.prompt("jsbridge://getUserInfo")
// result 是 Android 通过 confirm() 返回的值
```

**关键点**：`prompt()` 是同步调用，JS 会阻塞等待 Android 返回结果

**适用场景**：不方便 addJavascriptInterface、需要同步返回值

---

### 2.5 双向通信：evaluateJavascript + @JavascriptInterface

**组合方式**：
```
JS → Android:  window.Android.sendToNative(msg)
Android → JS:  evaluateJavascript("receiveFromNative(msg)")
```

**时序图**：
```
WebView(JS)                  Android
  │                            │
  │  window.Android.           │
  │    sendToNative("hi")      │
  │ ─────────────────────────→ │
  │                            │  处理消息
  │                            │  evaluateJavascript(
  │                            │    "receiveFromNative('hello')")
  │  receiveFromNative("hello")
  │ ←───────────────────────── │
  │  显示消息                   │
```

**适用场景**：聊天、表单提交、实时数据同步

---

### 2.6 WebMessagePort（API 23+）

**核心 API**：
```kotlin
// Android 端
val channel = webView.createWebMessageChannel()
channel[0].setWebMessageCallback(object : WebMessagePort.WebMessageCallback() {
    override fun onMessage(port: WebMessagePort, message: WebMessage) {
        val data = message.data  // 收到 JS 消息
        port.postMessage(WebMessage("reply"))  // 回复
    }
})
webView.postWebMessage(WebMessage("port", arrayOf(channel[1])), Uri.EMPTY)

// JS 端
window.addEventListener('message', function(event) {
    var port = event.ports[0];
    port.onmessage = function(e) { console.log(e.data); };
    port.postMessage("hello from JS");
});
```

**对比 @JavascriptInterface**：
- `@JavascriptInterface`：暴露 Java 对象，JS 直接调用方法
- `WebMessagePort`：不暴露对象，通过消息通道通信，更安全

---

## 3. 方式对比表

| 方向 | 方式 | 同步/异步 | 返回值 | 最低 API | 安全性 |
|------|------|----------|--------|----------|--------|
| Android→JS | evaluateJavascript | 异步回调 | 有 | 19 | 高 |
| Android→JS | loadUrl("javascript:...") | 同步 | 无 | 1 | 低 |
| JS→Android | @JavascriptInterface | 同步 | 有 | 17 | 中 |
| JS→Android | URL Scheme | 异步 | 无 | 1 | 高 |
| JS→Android | onJsPrompt | 同步 | 有 | 1 | 中 |
| 双向 | WebMessagePort | 异步 | 有 | 23 | 高 |

**选型建议**：
- 简单调用 → `@JavascriptInterface`
- 需要兼容旧设备 → URL Scheme
- 安全要求高 → `WebMessagePort`
- 需要同步返回值 → `onJsPrompt` 或 `@JavascriptInterface`

---

## 4. 安全注意事项

### 4.1 @JavascriptInterface 漏洞历史

**Android 4.2 之前的漏洞**：
- 没有 `@JavascriptInterface` 注解要求
- JS 可以通过反射调用 Java 对象的任意方法（包括 `Runtime.exec()`）
- 攻击者可以通过 WebView 执行任意命令

**修复**：API 17+ 强制要求 `@JavascriptInterface` 注解，只有标注的方法才会暴露

**最佳实践**：
```kotlin
class JsBridge {
    @JavascriptInterface
    fun safeMethod(param: String) {
        // 校验参数，避免注入
        if (param.contains("<script>")) return
        // 执行逻辑
    }
}
```

### 4.2 URL Scheme 风险

- 不要在 URL 中传递敏感信息（token、密码）
- 对 URL 参数做校验和转义
- 使用 HTTPS 替代自定义协议（如果可能）

### 4.3 WebView 安全配置

```kotlin
// 禁用文件访问（如果不需要）
webView.settings.allowFileAccess = false
webView.settings.allowFileAccessFromFileURLs = false
webView.settings.allowUniversalAccessFromFileURLs = false

// 禁用混合内容（HTTP/HTTPS 混合加载）
webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
```

---

## 5. 面试常见问题

### Q1: WebView 与原生有哪些通信方式？

**A**：分为两个方向：
- **Android → JS**：`evaluateJavascript()`（推荐）、`loadUrl("javascript:...")`（旧方案）
- **JS → Android**：`@JavascriptInterface`（推荐）、URL Scheme 拦截、`onJsPrompt` 拦截、`WebMessagePort`（API 23+）

### Q2: evaluateJavascript 和 loadUrl("javascript:...") 的区别？

**A**：
- `evaluateJavascript`：异步执行，可通过回调获取返回值，API 19+
- `loadUrl("javascript:...")`：同步执行，无法获取返回值，API 18 以下有安全漏洞

### Q3: @JavascriptInterface 的原理是什么？

**A**：
- Android 通过 `addJavascriptInterface(obj, "name")` 将 Java 对象映射为 JS 的 `window.name` 全局变量
- JS 调用 `window.name.method()` 时，WebView 通过 JNI 找到对应的 Java 方法并执行
- 只有标注 `@JavascriptInterface` 的方法才会暴露（API 17+）
- 方法在 WebView 的 JS 线程执行，更新 UI 需要 `runOnUiThread`

### Q4: URL Scheme 拦截的原理？

**A**：
- JS 通过 `window.location` 或 `<a>` 标签跳转到自定义协议 URL
- WebView 在 `shouldOverrideUrlLoading()` 中拦截 URL
- Android 解析 URL 的 scheme、host、query parameters
- 返回 `true` 表示拦截，返回 `false` 表示放行

### Q5: onJsPrompt 为什么能实现 JS 调用原生？

**A**：
- JS 调用 `window.prompt(message)` 时，系统会弹出输入对话框
- 重写 `WebChromeClient.onJsPrompt()` 可以拦截这个调用
- 将 `message` 作为通信协议（如 `"jsbridge://method"`），解析后执行原生逻辑
- 通过 `JsPromptResult.confirm(value)` 返回值给 JS
- 返回 `true` 阻止系统弹窗

### Q6: WebMessagePort 与 @JavascriptInterface 的区别？

**A**：
- `@JavascriptInterface`：暴露 Java 对象给 JS，JS 直接调用方法，同步执行
- `WebMessagePort`：不暴露对象，通过消息通道异步通信，支持传递 ArrayBuffer 等结构化数据
- `WebMessagePort` 更安全（不暴露原生对象），但 API 要求更高（23+）

### Q7: @JavascriptInterface 方法在哪个线程执行？

**A**：在 WebView 的 JS 线程执行，不是主线程。更新 UI 需要 `runOnUiThread` 或 `Handler.post()`。

### Q8: 如何处理 JS 调用原生的异步操作？

**A**：
- `@JavascriptInterface` 方法是同步的，不能直接返回异步结果
- 解决方案：传入 callbackId，异步操作完成后通过 `evaluateJavascript("callback(id, data)")` 回传
- 或使用 `WebMessagePort`，天然支持异步消息

---

## 6. 最佳实践

### 6.1 通信协议设计

```kotlin
// 推荐：统一协议格式
// JS → Android: window.Android.invoke(method, paramsJson)
// Android → JS: evaluateJavascript("window.onNativeCallback(method, resultJson)")

class JsBridge {
    @JavascriptInterface
    fun invoke(method: String, paramsJson: String): String {
        return when (method) {
            "getUserInfo" -> getUserInfo()
            "showToast" -> { showToast(paramsJson); "{}" }
            else -> """{"error":"unknown method"}"""
        }
    }
}
```

### 6.2 错误处理

```kotlin
// Android 端
@JavascriptInterface
fun safeMethod(param: String): String {
    return try {
        // 业务逻辑
        """{"result":"ok"}"""
    } catch (e: Exception) {
        """{"error":"${e.message}"}"""
    }
}
```

### 6.3 线程安全

```kotlin
@JavascriptInterface
fun fetchData(callbackId: String) {
    // 在后台线程执行耗时操作
    Thread {
        val data = networkRequest()
        // 回传结果到 JS（必须切回 WebView 线程）
        webView.post {
            webView.evaluateJavascript(
                "onCallback('$callbackId', '$data')", null
            )
        }
    }.start()
}
```

### 6.4 WebView 配置

```kotlin
webView.settings.apply {
    javaScriptEnabled = true
    domStorageEnabled = true
    // 安全配置
    allowFileAccess = false
    allowFileAccessFromFileURLs = false
    allowUniversalAccessFromFileURLs = false
    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
}
```
