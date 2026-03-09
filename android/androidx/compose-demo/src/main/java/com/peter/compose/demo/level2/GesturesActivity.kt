package com.peter.compose.demo.level2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * GesturesActivity - 手势处理
 *
 * 学习目标：
 * 1. Modifier.clickable: 简单点击
 * 2. Modifier.pointerInput: 自定义手势检测
 * 3. detectTapGestures: 点击、长按、双击
 * 4. detectDragGestures: 拖拽手势
 */
class GesturesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    GesturesScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GesturesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. clickable 点击示例
        ClickableExample()

        // 2. detectTapGestures 示例
        TapGesturesExample()

        // 3. detectDragGestures 示例
        DragGesturesExample()

        // 4. 组合手势示例
        CombinedGesturesExample()
    }
}

/**
 * clickable 点击示例
 */
@Composable
fun ClickableExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. Modifier.clickable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "clickable 是最简单的点击处理方式，适用于大多数点击场景。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 简单点击计数
            var clickCount by remember { mutableIntStateOf(0) }

            Text(
                text = "简单点击计数:",
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { clickCount++ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "点击次数: $clickCount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // 点击效果对比
            Text(
                text = "点击效果对比:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 带涟漪效果
                var count1 by remember { mutableIntStateOf(0) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { count1++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "涟漪效果\n$count1",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // 无涟漪效果
                var count2 by remember { mutableIntStateOf(0) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { count2++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "无涟漪\n$count2",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * detectTapGestures 示例
 *
 * detectTapGestures 提供更丰富的点击手势检测：
 * - onTap: 单击
 * - onDoubleTap: 双击
 * - onLongPress: 长按
 * - onPress: 按下状态
 */
@Composable
fun TapGesturesExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. detectTapGestures",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "detectTapGestures 提供单击、双击、长按等手势检测。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 手势状态
            var tapStatus by remember { mutableStateOf("请操作") }
            var tapCount by remember { mutableIntStateOf(0) }
            var doubleTapCount by remember { mutableIntStateOf(0) }
            var longPressCount by remember { mutableIntStateOf(0) }

            Text(
                text = "当前状态: $tapStatus",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                tapCount++
                                tapStatus = "单击 (共 $tapCount 次)"
                            },
                            onDoubleTap = {
                                doubleTapCount++
                                tapStatus = "双击 (共 $doubleTapCount 次)"
                            },
                            onLongPress = {
                                longPressCount++
                                tapStatus = "长按 (共 $longPressCount 次)"
                            },
                            onPress = {
                                tapStatus = "按下..."
                                tryAwaitRelease()
                                tapStatus = "释放"
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "尝试：单击、双击、长按",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // 统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("单击: $tapCount", style = MaterialTheme.typography.bodySmall)
                Text("双击: $doubleTapCount", style = MaterialTheme.typography.bodySmall)
                Text("长按: $longPressCount", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * detectDragGestures 示例
 *
 * detectDragGestures 用于检测拖拽手势
 * 可以获取拖拽的偏移量，用于移动组件
 */
@Composable
fun DragGesturesExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. detectDragGestures",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "detectDragGestures 用于检测拖拽手势，可以实现拖动效果。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 拖拽位置
            var boxOffset by remember { mutableStateOf(Offset.Zero) }

            Text(
                text = "拖拽偏移: (${boxOffset.x.roundToInt()}, ${boxOffset.y.roundToInt()})",
                style = MaterialTheme.typography.labelMedium
            )

            // 可拖拽区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // 可拖拽的方块
                Box(
                    modifier = Modifier
                        .offset { IntOffset(boxOffset.x.roundToInt(), boxOffset.y.roundToInt()) }
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .pointerInput(Unit) {
                            detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                                change.consume()
                                boxOffset = Offset(
                                    x = boxOffset.x + dragAmount.x,
                                    y = boxOffset.y + dragAmount.y
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "拖我",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // 重置按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.TextButton(
                    onClick = { boxOffset = Offset.Zero }
                ) {
                    Text("重置位置")
                }
            }

            // 垂直拖拽示例
            var verticalOffset by remember { mutableFloatStateOf(0f) }

            Text(
                text = "垂直拖拽滑块:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 轨道
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )

                // 可拖拽滑块
                Box(
                    modifier = Modifier
                        .offset { IntOffset(verticalOffset.roundToInt(), 0) }
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                verticalOffset = (verticalOffset + dragAmount.x).coerceIn(-100f, 100f)
                            }
                        }
                )

                // 轨道
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
        }
    }
}

/**
 * 组合手势示例
 *
 * 展示如何组合多种手势
 */
@Composable
fun CombinedGesturesExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 组合手势示例",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "结合点击和拖拽的综合示例。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 状态
            val initialColor = MaterialTheme.colorScheme.primary
            var boxColor by remember { mutableStateOf(initialColor) }
            var boxOffset by remember { mutableStateOf(Offset.Zero) }
            var isPressed by remember { mutableStateOf(false) }

            Text(
                text = if (isPressed) "按下中..." else "可点击变色，可拖拽移动",
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(boxOffset.x.roundToInt(), boxOffset.y.roundToInt()) }
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPressed) boxColor.copy(alpha = 0.7f) else boxColor
                        )
                        .pointerInput(Unit) {
                            // 点击变色
                            detectTapGestures(
                                onTap = {
                                    // 随机颜色
                                    boxColor = Color(
                                        red = (0..255).random(),
                                        green = (0..255).random(),
                                        blue = (0..255).random()
                                    )
                                },
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            // 拖拽移动
                            detectDragGestures { _, dragAmount ->
                                boxOffset = Offset(
                                    x = (boxOffset.x + dragAmount.x).coerceIn(-100f, 100f),
                                    y = (boxOffset.y + dragAmount.y).coerceIn(-50f, 50f)
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "点击变色\n拖拽移动",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // 重置按钮
            val primaryColor = MaterialTheme.colorScheme.primary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        boxOffset = Offset.Zero
                        boxColor = primaryColor
                    }
                ) {
                    Text("重置")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GesturesPreview() {
    MaterialTheme {
        GesturesScreen()
    }
}
