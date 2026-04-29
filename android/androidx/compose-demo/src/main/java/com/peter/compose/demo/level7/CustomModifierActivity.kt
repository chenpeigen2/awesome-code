package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * CustomModifierActivity - 自定义 Modifier 深入
 *
 * 学习目标：
 * 1. drawBehind — 自定义背景绘制
 * 2. drawWithContent — 在内容前后绘制
 * 3. pointerInput — 自定义手势检测
 * 4. 组合：渐变边框 Modifier
 * 5. Modifier.composed vs Modifier.Node 对比说明
 */
class CustomModifierActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    CustomModifierScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomModifierScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "自定义 Modifier",
            style = MaterialTheme.typography.headlineMedium
        )

        DrawBehindDemo()
        DrawWithContentDemo()
        PointerInputDemo()
        GradientBorderDemo()
        ModifierNodeExplanation()
    }
}

@Composable
fun DrawBehindDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "drawBehind — 自定义背景",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "在内容后面绘制自定义图形",
                style = MaterialTheme.typography.bodyMedium
            )

            // 渐变背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2)
                                )
                            ),
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "渐变背景",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // 圆点背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawBehind {
                        val dotRadius = 4.dp.toPx()
                        val spacing = 24.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            var y = 0f
                            while (y < size.height) {
                                drawCircle(
                                    color = Color.LightGray.copy(alpha = 0.5f),
                                    radius = dotRadius,
                                    center = Offset(x, y)
                                )
                                y += spacing
                            }
                            x += spacing
                        }
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("圆点背景", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = """Modifier.drawBehind {
    drawRoundRect(
        brush = gradientBrush,
        cornerRadius = CornerRadius(16.dp.toPx())
    )
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
fun DrawWithContentDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "drawWithContent — 前后绘制",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "在内容前面或后面叠加绘制层",
                style = MaterialTheme.typography.bodyMedium
            )

            // 光晕效果
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawWithContent {
                        drawCircle(
                            color = Color(0xFF667eea).copy(alpha = 0.3f),
                            radius = size.minDimension * 0.6f,
                            center = center
                        )
                        drawContent()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "光晕效果",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 条纹遮罩
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .drawWithContent {
                        drawContent()
                        val stripeWidth = 20.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawRect(
                                color = Color.White.copy(alpha = 0.1f),
                                topLeft = Offset(x, 0f),
                                size = Size(stripeWidth / 2, size.height)
                            )
                            x += stripeWidth
                        }
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("条纹遮罩", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = """Modifier.drawWithContent {
    // 先画背景效果
    drawCircle(color = glowColor, radius = ...)
    // 再画内容
    drawContent()
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
fun PointerInputDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "pointerInput — 手势检测",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "长按显示涟漪效果，点击显示坐标",
                style = MaterialTheme.typography.bodyMedium
            )

            var tapInfo by remember { mutableStateOf("点击或长按下方区域") }
            var rippleCenter by remember { mutableStateOf<Offset?>(null) }
            var showRipple by remember { mutableStateOf(false) }
            val rippleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                tapInfo = "点击位置: (${offset.x.toInt()}, ${offset.y.toInt()})"
                                showRipple = false
                            },
                            onLongPress = { offset ->
                                tapInfo = "长按位置: (${offset.x.toInt()}, ${offset.y.toInt()})"
                                rippleCenter = offset
                                showRipple = true
                            }
                        )
                    }
                    .drawBehind {
                        if (showRipple && rippleCenter != null) {
                            drawCircle(
                                color = rippleColor,
                                radius = 40.dp.toPx(),
                                center = rippleCenter!!
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(tapInfo)
            }

            Text(
                text = """Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = { offset -> /* 点击 */ },
        onLongPress = { offset -> /* 长按 */ }
    )
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
fun GradientBorderDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "渐变边框 Modifier",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "组合 drawBehind 实现可复用的渐变边框",
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .gradientBorder(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFFf093fb)
                        ),
                        borderWidth = 4.dp,
                        cornerRadius = 16.dp
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("渐变边框效果", style = MaterialTheme.typography.titleMedium)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .gradientBorder(
                        colors = listOf(
                            Color(0xFF00C853),
                            Color(0xFF00BFA5),
                            Color(0xFF00B0FF)
                        ),
                        borderWidth = 3.dp,
                        cornerRadius = 12.dp
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("绿色渐变边框", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = """fun Modifier.gradientBorder(
    colors: List<Color>,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 8.dp
) = this.drawBehind {
    drawRoundRect(
        brush = Brush.linearGradient(colors),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = borderWidth.toPx())
    )
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
fun ModifierNodeExplanation() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Modifier.Node vs composed",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = """旧 API: Modifier.composed {}
- 每次重组都会创建新的 Modifier 实例
- 有额外的内存分配开销
- 可以访问 Composition 的状态

新 API: Modifier.Node
- 只创建一次 Node 实例
- 通过 update {} 更新参数
- 性能更好，减少内存分配
- 推荐用于库作者和复杂自定义 Modifier

使用建议：
• 简单的绘制/布局 → drawBehind / drawWithContent
• 需要状态和副作用 → Modifier.Node
• 简单的参数变换 → 普通 Modifier 扩展函数
• 库级别的复杂 Modifier → Modifier.Node

Compose BOM 2024.02+ 已稳定支持 Modifier.Node""",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 渐变边框 Modifier 扩展函数
 */
fun Modifier.gradientBorder(
    colors: List<Color>,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 8.dp
) = this.drawBehind {
    drawRoundRect(
        brush = Brush.linearGradient(colors),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = borderWidth.toPx())
    )
}

@Preview(showBackground = true)
@Composable
fun CustomModifierScreenPreview() {
    MaterialTheme {
        CustomModifierScreen()
    }
}
