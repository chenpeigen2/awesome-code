package com.peter.compose.demo.level5

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * InteropActivity - 互操作
 *
 * 学习目标：
 * 1. AndroidView 嵌入 View
 * 2. View 中嵌入 Compose
 * 3. Mixed View/Compose 界面
 */
class InteropActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    InteropScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun InteropScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. AndroidView 基础
        AndroidViewBasics()

        // 2. WebView 示例
        WebViewExample()

        // 3. 双向绑定示例
        TwoWayBindingExample()

        // 4. 互操作最佳实践
        InteropBestPractices()
    }
}

/**
 * AndroidView 基础示例
 */
@Composable
fun AndroidViewBasics() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. AndroidView 基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 AndroidView 在 Compose 中嵌入传统 View。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 简单 TextView 示例
            var counter by remember { mutableIntStateOf(0) }

            Text(
                text = "嵌入 TextView:",
                style = MaterialTheme.typography.labelMedium
            )

            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text = "这是传统 TextView"
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            // 更新 View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { counter++ }) {
                    Text("增加: $counter")
                }
            }

            // 动态更新 View
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        textSize = 18f
                        setPadding(16, 16, 16, 16)
                    }
                },
                update = { textView ->
                    textView.text = "计数器: $counter"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """AndroidView(
    factory = { context ->
        TextView(context).apply {
            text = "Hello"
        }
    },
    update = { textView ->
        // 当状态变化时更新
        textView.text = "Count: " + count.toString()
    }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * WebView 示例
 */
@Composable
fun WebViewExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. WebView 示例",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "嵌入 WebView 显示网页内容。",
                style = MaterialTheme.typography.bodyMedium
            )

            var url by remember { mutableStateOf("https://www.baidu.com") }

            // WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(url)
                    }
                },
                update = { webView ->
                    webView.loadUrl(url)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            )

            Text(
                text = "WebView 代码:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """AndroidView(
    factory = { context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
    },
    update = { webView ->
        webView.loadUrl(url)
    }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 双向绑定示例
 */
@Composable
fun TwoWayBindingExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 双向绑定",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "AndroidView 与 Compose 状态的双向绑定。",
                style = MaterialTheme.typography.bodyMedium
            )

            var text by remember { mutableStateOf("初始文本") }

            // Compose 中显示
            Text(
                text = "Compose 状态: \"$text\"",
                style = MaterialTheme.typography.bodyMedium
            )

            // 传统 EditText
            AndroidView(
                factory = { context ->
                    android.widget.EditText(context).apply {
                        setText(text)
                        setPadding(16, 16, 16, 16)
                        setBackgroundResource(android.R.drawable.edit_text)
                        addTextChangedListener(object : android.text.TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                            override fun afterTextChanged(s: android.text.Editable?) {
                                text = s.toString()
                            }
                        })
                    }
                },
                update = { editText ->
                    // 防止循环更新
                    if (editText.text.toString() != text) {
                        editText.setText(text)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Compose 按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { text = "来自 Compose" }) {
                    Text("更新文本")
                }
                Button(onClick = { text = "" }) {
                    Text("清空")
                }
            }
        }
    }
}

/**
 * 互操作最佳实践
 */
@Composable
fun InteropBestPractices() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 最佳实践",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "AndroidView 使用的最佳实践。",
                style = MaterialTheme.typography.bodyMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BestPracticeItem(
                    title = "避免频繁创建 View",
                    description = "factory 应该创建并返回 View 实例，update 用于更新状态。"
                )

                BestPracticeItem(
                    title = "使用 remember 缓存 View 状态",
                    description = "对于复杂的 View 状态，使用 remember 保存。"
                )

                BestPracticeItem(
                    title = "处理生命周期",
                    description = "注意 View 的生命周期，在 DisposableEffect 中清理资源。"
                )

                BestPracticeItem(
                    title = "防止循环更新",
                    description = "在 update 块中检查是否需要更新，避免无限循环。"
                )

                BestPracticeItem(
                    title = "优先使用 Compose 组件",
                    description = "如果 Compose 有对应组件，优先使用 Compose 版本。"
                )
            }

            Text(
                text = "常见使用场景:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• WebView - 显示网页",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• MapView - 地图组件",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• VideoView - 视频播放",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• AdView - 广告组件",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 自定义 View - 第三方 UI 库",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun BestPracticeItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InteropPreview() {
    MaterialTheme {
        InteropScreen()
    }
}
