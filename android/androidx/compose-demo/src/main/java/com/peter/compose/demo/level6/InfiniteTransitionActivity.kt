package com.peter.compose.demo.level6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * InfiniteTransitionActivity - 无限动画与物理动画
 *
 * 学习目标：
 * 1. rememberInfiniteTransition: 旋转、脉冲、颜色循环
 * 2. spring(): 弹簧动画的 dampingRatio 参数
 * 3. 手势驱动动画: 拖拽 + 释放后弹簧回弹
 */
class InfiniteTransitionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    InfiniteTransitionScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun InfiniteTransitionScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "无限动画 & 物理动画",
            style = MaterialTheme.typography.headlineMedium
        )

        InfiniteTransitionBasics()
        SpringAnimationDemo()
        GestureSpringDemo()
    }
}

@Composable
fun InfiniteTransitionBasics() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "InfiniteTransition",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "无限循环动画：旋转加载、脉冲呼吸、颜色渐变",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing)
                    ),
                    label = "rotation"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .rotate(rotation)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(8.dp)
                            )
                    )
                    Text("旋转", style = MaterialTheme.typography.bodySmall)
                }

                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scale)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                CircleShape
                            )
                    )
                    Text("脉冲", style = MaterialTheme.typography.bodySmall)
                }

                val colorFraction by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing)
                    ),
                    label = "color"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Color(red = colorFraction, green = 1f - colorFraction, blue = 0.5f),
                                CircleShape
                            )
                    )
                    Text("颜色", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(
                text = """val infiniteTransition = rememberInfiniteTransition()
val rotation by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000, easing = LinearEasing)
    )
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

@Composable
fun SpringAnimationDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "spring() 弹簧动画",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "调节 dampingRatio（阻尼比）观察弹性差异",
                style = MaterialTheme.typography.bodyMedium
            )

            var dampingRatio by remember { mutableFloatStateOf(Spring.DampingRatioMediumBouncy) }
            var targetState by remember { mutableStateOf(false) }

            Text("dampingRatio: %.2f".format(dampingRatio), style = MaterialTheme.typography.bodySmall)

            Slider(
                value = dampingRatio,
                onValueChange = { dampingRatio = it },
                valueRange = 0.1f..1.5f,
                modifier = Modifier.fillMaxWidth()
            )

            val offsetX by animateFloatAsState(
                targetValue = if (targetState) 200f else 0f,
                animationSpec = spring(
                    dampingRatio = dampingRatio,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "spring"
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            Button(onClick = { targetState = !targetState }) {
                Text(if (targetState) "弹回左侧" else "弹到右侧")
            }

            Text(
                text = """animateFloatAsState(
    targetValue = if (target) 200f else 0f,
    animationSpec = spring(
        dampingRatio = dampingRatio,  // 0.1=弹性, 1.0=临界
        stiffness = Spring.StiffnessMedium
    )
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

@Composable
fun GestureSpringDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "手势驱动弹簧动画",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "拖拽圆形，释放后自动回弹",
                style = MaterialTheme.typography.bodyMedium
            )

            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }
            var isDragging by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val animatedOffsetX by animateFloatAsState(
                    targetValue = if (isDragging) offsetX else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "dragX"
                )
                val animatedOffsetY by animateFloatAsState(
                    targetValue = if (isDragging) offsetY else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "dragY"
                )

                Box(
                    modifier = Modifier
                        .offset { IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt()) }
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                },
                                onDragEnd = {
                                    isDragging = false
                                    offsetX = 0f
                                    offsetY = 0f
                                },
                                onDragStart = {
                                    isDragging = true
                                }
                            )
                        }
                )
            }

            Text(
                text = """var offsetX by remember { mutableFloatStateOf(0f) }
var isDragging by remember { mutableStateOf(false) }

val animatedX by animateFloatAsState(
    targetValue = if (isDragging) offsetX else 0f,
    animationSpec = spring(dampingRatio = Bouncy, stiffness = Low)
)

Modifier.pointerInput(Unit) {
    detectDragGestures(
        onDrag = { _, dragAmount -> offsetX += dragAmount.x },
        onDragEnd = { isDragging = false; offsetX = 0f }
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
fun InfiniteTransitionScreenPreview() {
    MaterialTheme {
        InfiniteTransitionScreen()
    }
}
