package com.peter.compose.demo.level1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp

/**
 * PreviewActivity - Compose 预览与 @Preview
 *
 * 学习目标：
 * 1. @Preview 注解基础用法
 * 2. @Preview 参数：name, showBackground, backgroundColor
 * 3. 多预览注解 (浅色/深色模式)
 * 4. Preview 在 Android Studio 中的使用
 */
class PreviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    PreviewDemoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * 主演示界面
 */
@Composable
fun PreviewDemoScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Compose @Preview 注解示例",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Preview 注解允许在 Android Studio 中预览 Composable 函数，无需运行应用。\n\n" +
                    "常用参数：\n" +
                    "• name: 预览名称\n" +
                    "• showBackground: 显示背景\n" +
                    "• backgroundColor: 背景颜色\n" +
                    "• widthDp / heightDp: 预览尺寸\n" +
                    "• group: 分组预览\n\n" +
                    "请在 Android Studio 中打开此文件，查看右侧的预览面板。",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 这些组件在预览面板中可见
        SimplePreviewComponent()

        Spacer(modifier = Modifier.height(16.dp))

        ColoredPreviewComponent()
    }
}

/**
 * 基础 @Preview 注解
 *
 * name: 预览名称，在预览面板中显示
 * showBackground: 是否显示背景
 */
@Preview(
    name = "简单文本预览",
    showBackground = true
)
@Composable
fun SimplePreviewComponent() {
    Text(
        text = "这是一个简单的预览组件",
        style = MaterialTheme.typography.bodyLarge
    )
}

/**
 * 带背景色的 @Preview 注解
 *
 * backgroundColor: 设置背景颜色 (需要配合 showBackground = true)
 */
@Preview(
    name = "带背景色的预览",
    showBackground = true,
    backgroundColor = 0xFFE3F2FD  // 浅蓝色背景
)
@Composable
fun ColoredPreviewComponent() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "带自定义背景色的预览",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Blue
        )
    }
}

/**
 * 指定尺寸的预览
 *
 * widthDp / heightDp: 预览尺寸
 */
@Preview(
    name = "指定尺寸预览",
    showBackground = true,
    widthDp = 200,
    heightDp = 100
)
@Composable
fun SizedPreviewComponent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = "200x100 dp")
    }
}

/**
 * 浅色模式预览
 */
@Preview(
    name = "浅色模式",
    showBackground = true,
    wallpaper = Wallpapers.NONE
)
@Composable
fun LightModePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "浅色模式预览",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * 深色模式预览
 */
@Preview(
    name = "深色模式",
    showBackground = true,
    wallpaper = Wallpapers.NONE
)
@Composable
fun DarkModePreview() {
    MaterialTheme(colorScheme = androidx.compose.material3.darkColorScheme()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "深色模式预览",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * 多个 @Preview 注解
 *
 * 一个 Composable 可以有多个 @Preview 注解，
 * 用于展示不同状态或配置
 */
@Preview(name = "默认状态", showBackground = true)
@Preview(name = "大尺寸", showBackground = true, widthDp = 400, heightDp = 200)
@Composable
fun MultiPreviewComponent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "多预览示例",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

/**
 * 分组预览
 *
 * group: 将多个预览分组显示
 */
@Preview(name = "按钮状态1", group = "按钮示例", showBackground = true)
@Composable
fun GroupedPreview1() {
    Text(text = "分组预览 - 组件1")
}

@Preview(name = "按钮状态2", group = "按钮示例", showBackground = true)
@Composable
fun GroupedPreview2() {
    Text(text = "分组预览 - 组件2")
}