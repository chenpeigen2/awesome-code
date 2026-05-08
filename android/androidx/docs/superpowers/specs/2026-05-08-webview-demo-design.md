# WebView ↔ Android Native Communication Demo

## Overview

Create a `webview-demo` module demonstrating WebView ↔ Android bidirectional communication, structured from simple to advanced. Each demo is an independent Activity with an interactive HTML page and a real-time log panel. Accompanied by a standalone principle guide document suitable for teaching and interview preparation.

## Module Structure

```
webview-demo/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   ├── assets/web/
│   │   ├── android_call_js.html
│   │   ├── js_call_android.html
│   │   ├── url_scheme.html
│   │   ├── js_prompt.html
│   │   ├── bidirectional.html
│   │   └── web_message_port.html
│   └── java/com/peter/webview/demo/
│       ├── MainActivity.kt
│       ├── WebViewBaseActivity.kt
│       ├── basics/
│       │   ├── AndroidCallJsActivity.kt
│       │   └── JsCallAndroidActivity.kt
│       ├── intermediate/
│       │   ├── UrlSchemeActivity.kt
│       │   └── JsPromptActivity.kt
│       ├── advanced/
│       │   ├── BidirectionalActivity.kt
│       │   └── WebMessagePortActivity.kt
│       └── util/
│           └── LogWebViewClient.kt
```

## Demo Sequence

| # | Activity | Direction | Core API | Level |
|---|----------|-----------|----------|-------|
| 1 | AndroidCallJsActivity | Android → JS | `evaluateJavascript()` | Beginner |
| 2 | JsCallAndroidActivity | JS → Android | `@JavascriptInterface` | Beginner |
| 3 | UrlSchemeActivity | JS → Android | `shouldOverrideUrlLoading` | Intermediate |
| 4 | JsPromptActivity | JS → Android | `onJsPrompt` interception | Intermediate |
| 5 | BidirectionalActivity | Bidirectional | Combine 1+2 | Advanced |
| 6 | WebMessagePortActivity | Bidirectional | `WebMessagePort` (API 26+) | Advanced |

## Base Class: WebViewBaseActivity

Provides shared infrastructure for all demo Activities:

- **WebView initialization**: Enable JavaScript, configure WebChromeClient
- **Log panel**: ScrollView + TextView at the bottom, showing communication events with timestamps
- **`log(tag, message)`** method for consistent logging
- **Lifecycle management**: onResume/onPause/onDestroy properly handled
- **Layout**: Vertical LinearLayout with WebView (weight=1) + divider + log panel (height=200dp)

## Demo Details

### 1. AndroidCallJsActivity (Android → JS)

**Principle**: Android calls JavaScript functions in the WebView via `evaluateJavascript()`.

**UI**:
- WebView loads `android_call_js.html`
- Buttons: "Call JS with string", "Call JS with JSON", "Call JS with no return value"
- Each button triggers `evaluateJavascript("jsFunction(args)", callback)` and logs the result

**HTML page**:
- Defines JS functions: `showMessage(msg)`, `getData()` (returns JSON), `updateUI()`
- JS functions log their invocation to the page

### 2. JsCallAndroidActivity (JS → Android, @JavascriptInterface)

**Principle**: Android exposes a Java object to JavaScript via `addJavascriptInterface()`. JS calls methods on `window.Android`.

**UI**:
- WebView loads `js_call_android.html`
- Buttons from the HTML side trigger calls to Android methods
- Android side shows Toast / updates UI in response

**Key code**:
```kotlin
@JavascriptInterface
fun showToast(message: String) { ... }

@JavascriptInterface
fun getUserInfo(): String { ... }  // Returns JSON string
```

**HTML page**:
- Buttons: "Call Android Toast", "Get User Info"
- JS calls `window.Android.showToast("hello")` etc.

### 3. UrlSchemeActivity (URL Scheme Interception)

**Principle**: JS triggers navigation to a custom URL scheme (e.g., `jsbridge://method?params`). Android intercepts in `shouldOverrideUrlLoading()` and parses the URL to execute native logic.

**UI**:
- WebView loads `url_scheme.html`
- Android side logs intercepted URLs and decoded parameters

**Key code**:
```kotlin
override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
    val url = request.url.toString()
    if (url.startsWith("jsbridge://")) {
        // Parse and handle
        return true
    }
    return false
}
```

**HTML page**:
- Buttons that set `window.location` to custom scheme URLs
- Shows the URL being constructed

### 4. JsPromptActivity (onJsPrompt Interception)

**Principle**: JS calls `window.prompt(message)`. Android intercepts in `WebChromeClient.onJsPrompt()`, parses the message as a command, and returns a result string.

**UI**:
- WebView loads `js_prompt.html`
- Android side logs intercepted prompts and returned values

**Key code**:
```kotlin
override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsResult): Boolean {
    if (message.startsWith("jsbridge:")) {
        // Parse command, execute, set result
        result.confirm("result from Android")
        return true
    }
    return super.onJsPrompt(view, url, message, defaultValue, result)
}
```

### 5. BidirectionalActivity (Full Bidirectional Communication)

**Principle**: Combines `evaluateJavascript()` (Android → JS) and `@JavascriptInterface` (JS → Android) for a complete two-way communication scenario.

**Scenario**: A "chat" interface where the user can send messages from either side:
- Type in Android EditText → send to JS → JS displays in WebView
- Type in WebView input → send to Android → Android displays in a TextView

**HTML page**:
- Chat-style UI with input field and message list
- JS function `receiveFromAndroid(msg)` to display incoming messages
- Button to send message via `window.Android.sendToAndroid(msg)`

### 6. WebMessagePortActivity (API 26+ postMessage)

**Principle**: Uses the `WebMessagePort` API (Android API 26+) for secure, structured message passing. Unlike `@JavascriptInterface`, this doesn't expose a Java object to JS — instead, both sides create port endpoints and exchange `WebMessage` objects.

**UI**:
- WebView loads `web_message_port.html`
- Buttons to send messages from both sides
- Demonstrates structured data exchange

**Key code**:
```kotlin
val channel = webView.createWebMessageChannel()
channel[0].setWebMessageCallback(object : WebMessagePort.WebMessageCallback() {
    override fun onMessage(port: WebMessagePort, message: WebMessage) {
        log("Received from JS", message.data)
    }
})
webView.postWebMessage(WebMessage("hello from Android", arrayOf(channel[1])), Uri.EMPTY)
```

## Companion Document

**Path**: `docs/webview-communication-guide.md`

**Contents**:
1. WebView communication architecture overview
2. Each mechanism: principle, sequence diagram (text), use cases, pros/cons
3. Security considerations (`@JavascriptInterface` vulnerability history, URL scheme risks)
4. Interview Q&A (common questions with concise answers)
5. Best practices summary

## Build Configuration

- `compileSdk = 37`, `minSdk = 33` (consistent with project)
- Dependencies: `androidx.webkit:webkit` (for WebMessagePort)
- ViewBinding enabled
- Package: `com.peter.webview.demo`

## Testing

- Manual testing on device/emulator
- Each Activity independently runnable
- Log panel verifies communication flow
