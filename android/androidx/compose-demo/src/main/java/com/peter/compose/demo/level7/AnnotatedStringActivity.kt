package com.peter.compose.demo.level7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AnnotatedStringActivity - 富文本与内联样式
 *
 * 学习目标：
 * 1. AnnotatedString.Builder — SpanStyle 应用
 * 2. 点击注解 — StringAnnotation + ClickableText
 * 3. 实战：协议文本中的可点击链接
 */
class AnnotatedStringActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    AnnotatedStringScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnnotatedStringScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "AnnotatedString 富文本",
            style = MaterialTheme.typography.headlineMedium
        )

        SpanStyleDemo()
        MixedStyleDemo()
        ClickableAnnotationDemo()
        TermsOfServiceDemo()
    }
}

@Composable
fun SpanStyleDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SpanStyle 基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = buildAnnotatedString {
                    append("这是")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("粗体")
                    }
                    append("和")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("彩色")
                    }
                    append("文本")
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, fontSize = 20.sp)) {
                        append("斜体大字")
                    }
                    append(" 和 ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("下划线")
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = """buildAnnotatedString {
    append("这是")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("粗体")
    }
    append("和")
    withStyle(SpanStyle(color = primary)) {
        append("彩色")
    }
    append("文本")
}""",
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

@Composable
fun MixedStyleDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "混合样式 — 高亮关键词",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val keywords = listOf("Compose", "声明式", "状态")
            val text = "Jetpack Compose 是 Android 的声明式 UI 框架，核心概念是状态管理。"

            Text(
                text = buildAnnotatedString {
                    var remaining = text
                    for (keyword in keywords) {
                        val index = remaining.indexOf(keyword)
                        if (index >= 0) {
                            append(remaining.substring(0, index))
                            withStyle(
                                SpanStyle(
                                    color = Color.White,
                                    background = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(keyword)
                            }
                            remaining = remaining.substring(index + keyword.length)
                        }
                    }
                    append(remaining)
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ClickableAnnotationDemo() {
    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "点击注解 (StringAnnotation)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val annotatedText = buildAnnotatedString {
                append("点击 ")
                pushStringAnnotation(tag = "LINK", annotation = "https://developer.android.com")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Android 开发者文档")
                }
                pop()
                append(" 了解更多")
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "LINK", start = offset, end = offset
                    ).firstOrNull()?.let { annotation ->
                        Toast.makeText(context, "打开: ${annotation.item}", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Text(
                text = """val annotatedText = buildAnnotatedString {
    append("点击 ")
    pushStringAnnotation(tag = "LINK", annotation = url)
    withStyle(SpanStyle(color = primary, underline)) {
        append("Android 开发者文档")
    }
    pop()
    append(" 了解更多")
}

ClickableText(text = annotatedText, onClick = { offset ->
    annotatedText.getStringAnnotations("LINK", offset, offset)
        .firstOrNull()?.let { openUrl(it.item) }
})""",
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

@Composable
fun TermsOfServiceDemo() {
    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "实战：用户协议",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val annotatedText = buildAnnotatedString {
                append("注册即表示同意 ")
                pushStringAnnotation(tag = "TERMS", annotation = "terms")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("《用户协议》")
                }
                pop()
                append(" 和 ")
                pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("《隐私政策》")
                }
                pop()
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "TERMS", start = offset, end = offset
                    ).firstOrNull()?.let {
                        Toast.makeText(context, "打开用户协议", Toast.LENGTH_SHORT).show()
                    }
                    annotatedText.getStringAnnotations(
                        tag = "PRIVACY", start = offset, end = offset
                    ).firstOrNull()?.let {
                        Toast.makeText(context, "打开隐私政策", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnotatedStringScreenPreview() {
    MaterialTheme {
        AnnotatedStringScreen()
    }
}
