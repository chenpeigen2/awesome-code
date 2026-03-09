package com.peter.compose.demo.level4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.wrapContentHeight

/**
 * CanvasDrawingActivity - Canvas 绘制
 *
 * 学习目标：
 * 1. Canvas 组件基础
 * 2. drawRect, drawCircle, drawPath
 * 3. 绘制自定义图形
 */
class CanvasDrawingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    CanvasDrawingScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CanvasDrawingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. 基础图形
        BasicShapesCanvas()

        // 2. 渐变和路径
        GradientPathCanvas()

        // 3. 动态绘制
        DynamicCanvas()

        // 4. 绘图板
        DrawingCanvas()
    }
}

/**
 * 基础图形绘制
 */
@Composable
fun BasicShapesCanvas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. 基础图形",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 drawRect, drawCircle, drawLine 绘制基础图形。",
                style = MaterialTheme.typography.bodyMedium
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                // 矩形
                drawRect(
                    color = Color(0xFF3F51B5),
                    topLeft = Offset(20f, 20f),
                    size = Size(80f, 80f)
                )

                // 圆角矩形
                drawRect(
                    color = Color(0xFFE91E63),
                    topLeft = Offset(120f, 20f),
                    size = Size(80f, 80f),
                    style = Stroke(width = 4f)
                )

                // 圆形
                drawCircle(
                    color = Color(0xFF009688),
                    radius = 40f,
                    center = Offset(280f, 60f)
                )

                // 圆环
                drawCircle(
                    color = Color(0xFFFF9800),
                    radius = 30f,
                    center = Offset(60f, 160f),
                    style = Stroke(width = 6f)
                )

                // 直线
                drawLine(
                    color = Color(0xFF9C27B0),
                    start = Offset(120f, 120f),
                    end = Offset(200f, 180f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )

                // 椭圆
                drawOval(
                    color = Color(0xFF4CAF50),
                    topLeft = Offset(220f, 130f),
                    size = Size(100f, 50f),
                    style = Stroke(width = 3f)
                )

                // 弧线
                drawArc(
                    color = Color(0xFF2196F3),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = true,
                    topLeft = Offset(340f, 20f),
                    size = Size(60f, 60f)
                )
            }
        }
    }
}

/**
 * 渐变和路径绘制
 */
@Composable
fun GradientPathCanvas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 渐变和路径",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 Brush 和 Path 绘制渐变和复杂图形。",
                style = MaterialTheme.typography.bodyMedium
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                // 渐变矩形
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6200EE),
                            Color(0xFF03DAC5)
                        ),
                        start = Offset.Zero,
                        end = Offset(150f, 100f)
                    ),
                    topLeft = Offset(20f, 20f),
                    size = Size(130f, 80f)
                )

                // 渐变圆形
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFEB3B),
                            Color(0xFFFF5722)
                        ),
                        center = Offset(260f, 60f),
                        radius = 50f
                    ),
                    center = Offset(260f, 60f),
                    radius = 50f
                )

                // 路径 - 三角形
                val trianglePath = Path().apply {
                    moveTo(60f, 120f)
                    lineTo(20f, 180f)
                    lineTo(100f, 180f)
                    close()
                }
                drawPath(
                    path = trianglePath,
                    color = Color(0xFFE91E63),
                    style = Stroke(width = 4f, join = StrokeJoin.Round)
                )

                // 路径 - 星形
                val starPath = Path().apply {
                    val centerX = 200f
                    val centerY = 150f
                    val outerRadius = 40f
                    val innerRadius = 20f

                    for (i in 0 until 10) {
                        val radius = if (i % 2 == 0) outerRadius else innerRadius
                        val angle = Math.toRadians((i * 36 - 90).toDouble())
                        val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
                        val y = centerY + (radius * kotlin.math.sin(angle)).toFloat()

                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(
                    path = starPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                    )
                )

                // 曲线路径
                val curvePath = Path().apply {
                    moveTo(280f, 120f)
                    cubicTo(
                        x1 = 300f, y1 = 100f,
                        x2 = 340f, y2 = 180f,
                        x3 = 360f, y3 = 140f
                    )
                }
                drawPath(
                    path = curvePath,
                    color = Color(0xFF9C27B0),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * 动态绘制
 */
@Composable
fun DynamicCanvas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 动态绘制",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "根据状态动态绘制图形。",
                style = MaterialTheme.typography.bodyMedium
            )

            var progress by remember { mutableFloatStateOf(0.5f) }

            Slider(
                value = progress,
                onValueChange = { progress = it },
                modifier = Modifier.fillMaxWidth()
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8EAF6))
            ) {
                // 进度条背景
                drawRect(
                    color = Color.White,
                    topLeft = Offset(20f, size.height / 2 - 15),
                    size = Size(size.width - 40f, 30f)
                )

                // 进度条前景
                val progressWidth = (size.width - 40f) * progress
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6200EE),
                            Color(0xFF03DAC5)
                        )
                    ),
                    topLeft = Offset(20f, size.height / 2 - 15),
                    size = Size(progressWidth, 30f)
                )

                // 百分比文本 - 使用 Box 覆盖显示
            }

            // 显示百分比
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.CenterVertically)
            )

            // 圆形进度
            var circleProgress by remember { mutableFloatStateOf(0.7f) }

            Slider(
                value = circleProgress,
                onValueChange = { circleProgress = it },
                modifier = Modifier.fillMaxWidth()
            )

            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // 背景圆
                drawCircle(
                    color = Color(0xFFE0E0E0),
                    radius = 50f,
                    style = Stroke(width = 10f)
                )

                // 进度圆
                val sweepAngle = 360f * circleProgress
                drawArc(
                    color = Color(0xFF4CAF50),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )

                // 中心文字 - 使用 Box 覆盖显示
            }

            // 显示百分比
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(circleProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

/**
 * 绘图板
 */
@Composable
fun DrawingCanvas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 绘图板",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 Canvas 和手势实现简单的绘图功能。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 存储绘制的路径
            val paths = remember { mutableStateListOf<List<Offset>>() }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { paths.clear() }) {
                    Text("清空")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .pointerInput(Unit) {
                        var currentPath = mutableListOf<Offset>()
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = mutableListOf(offset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val last = currentPath.last()
                                currentPath.add(last + dragAmount)
                                if (paths.isNotEmpty() && paths.last() !== currentPath) {
                                    paths.add(currentPath.toList())
                                } else if (paths.isEmpty()) {
                                    paths.add(currentPath.toList())
                                }
                            },
                            onDragEnd = {
                                if (currentPath.isNotEmpty()) {
                                    paths.add(currentPath.toList())
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    paths.forEach { path ->
                        if (path.size > 1) {
                            for (i in 0 until path.size - 1) {
                                drawLine(
                                    color = Color(0xFF6200EE),
                                    start = path[i],
                                    end = path[i + 1],
                                    strokeWidth = 4f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "在上方区域拖动绘制",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CanvasDrawingPreview() {
    MaterialTheme {
        CanvasDrawingScreen()
    }
}
