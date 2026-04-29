package com.peter.compose.demo.level6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.graphics.RenderEffect as AndroidRenderEffect

/**
 * GraphicsLayerActivity - GraphicsLayer 与视觉效果
 *
 * 学习目标：
 * 1. GraphicsLayer 基础 — renderEffect
 * 2. RenderEffect.createBlurEffect — 实时模糊
 * 3. drawBehind 自定义绘制
 * 4. 实战：毛玻璃效果卡片
 */
class GraphicsLayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    GraphicsLayerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GraphicsLayerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "GraphicsLayer & 视觉效果",
            style = MaterialTheme.typography.headlineMedium
        )

        BlurEffectDemo()
        FrostedGlassDemo()
        CustomDrawDemo()
    }
}

@Composable
fun BlurEffectDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "模糊效果 (BlurEffect)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "调节滑块改变模糊半径",
                style = MaterialTheme.typography.bodyMedium
            )

            var blurRadius by remember { mutableFloatStateOf(0f) }

            Slider(
                value = blurRadius,
                onValueChange = { blurRadius = it },
                valueRange = 0f..25f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("模糊半径: %.1f".format(blurRadius), style = MaterialTheme.typography.bodySmall)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .graphicsLayer {
                        if (blurRadius > 0f) {
                            renderEffect = AndroidRenderEffect
                                .createBlurEffect(blurRadius, blurRadius, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    }
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "模糊我",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }

            Text(
                text = """Modifier.graphicsLayer {
    renderEffect = RenderEffect
        .createBlurEffect(radius, radius, TileMode.CLAMP)
        .asComposeRenderEffect()
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
fun FrostedGlassDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "毛玻璃效果",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "模糊背景 + 半透明叠加 = 毛玻璃卡片",
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2),
                                Color(0xFFf093fb)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(100.dp)
                        .graphicsLayer {
                            renderEffect = AndroidRenderEffect
                                .createBlurEffect(15f, 15f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                        .background(Color.White.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(12.dp))
                )

                Text(
                    text = "毛玻璃卡片",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Text(
                text = """// 模糊层
Modifier.graphicsLayer {
    renderEffect = blurEffect
}.background(Color.White.copy(alpha = 0.3f))

// 内容层（不受模糊影响）
Text("毛玻璃卡片")""",
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
fun CustomDrawDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "drawBehind 自定义绘制",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 drawBehind 绘制渐变边框",
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .drawBehind {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2),
                                    Color(0xFFf093fb)
                                )
                            ),
                            size = size
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(
                                size.width - 8.dp.toPx(),
                                size.height - 8.dp.toPx()
                            )
                        )
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "渐变边框",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = """Modifier.drawBehind {
    drawRect(brush = gradientBrush, size = size)
    drawRect(
        color = Color.White,
        topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
        size = Size(size.width - 8.dp.toPx(), ...)
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

@Preview(showBackground = true)
@Composable
fun GraphicsLayerScreenPreview() {
    MaterialTheme {
        GraphicsLayerScreen()
    }
}
