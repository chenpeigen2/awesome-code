package com.peter.compose.demo.level2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

        // 5. Transformable 手势示例
        TransformableGesturesExample()

        // 6. 多点触控追踪示例
        MultiTouchTrackingExample()

        // 7. Scrollable 修饰符示例
        ScrollableModifierExample()

        // 8. 嵌套滑动示例
        NestedScrollingExample()
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

/**
 * Transformable 手势示例
 *
 * detectTransformGestures 用于检测双指缩放和旋转手势。
 * 可以获取缩放比例、旋转角度和偏移量，实现图片缩放旋转等效果。
 */
@Composable
fun TransformableGesturesExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. Transformable Gestures (双指缩放与旋转)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "detectTransformGestures 可以同时处理双指缩放、旋转和平移，适用于图片查看器等场景。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 变换状态
            var scale by remember { mutableFloatStateOf(1f) }
            var rotation by remember { mutableFloatStateOf(0f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            // 状态显示
            Text(
                text = "缩放: ${"%.2f".format(scale)}  旋转: ${"%.1f".format(rotation)}°  偏移: (${offset.x.roundToInt()}, ${offset.y.roundToInt()})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 可变换区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                        .scale(scale)
                        .rotate(rotation)
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotationChange ->
                                scale = (scale * zoom).coerceIn(0.3f, 4f)
                                rotation += rotationChange
                                offset = Offset(
                                    x = offset.x + pan.x,
                                    y = offset.y + pan.y
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "双指操作\n缩放/旋转/平移",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // 重置按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        scale = 1f
                        rotation = 0f
                        offset = Offset.Zero
                    }
                ) {
                    Text("重置变换")
                }
            }
        }
    }
}

/**
 * 多点触控追踪示例
 *
 * 使用 awaitPointerEventScope 和 awaitEachGesture 追踪多个触控点。
 * 可以获取每个触控指针的 ID 和位置，适用于多点触控交互场景。
 */
@Composable
fun MultiTouchTrackingExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "6. Multi-touch Tracking (多点触控追踪)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 awaitPointerEventScope 和 awaitEachGesture 可以追踪多个触控点，获取每个指针的 ID 和位置。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 触控点状态
            var pointerCount by remember { mutableIntStateOf(0) }
            var pointerPositions by remember { mutableStateOf<List<Pair<Long, Offset>>>(emptyList()) }

            Text(
                text = "当前触控点数: $pointerCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 触控区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1A1A2E))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val firstDown = awaitFirstDown(requireUnconsumed = false)
                            pointerCount = 1
                            pointerPositions = listOf(
                                firstDown.id.value to firstDown.position
                            )
                            do {
                                val event = awaitPointerEvent()
                                pointerCount = event.changes.size
                                pointerPositions = event.changes.map {
                                    it.id.value to it.position
                                }
                                event.changes.forEach { it.consume() }
                            } while (event.changes.any { it.pressed })
                            // All pointers up
                            pointerCount = 0
                            pointerPositions = emptyList()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // 在每个触控点绘制彩色圆圈
                pointerPositions.forEachIndexed { index, pair ->
                    val (pointerId, position) = pair
                    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)
                    val color = colors[index % colors.size]
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(position.x.roundToInt() - 25, position.y.roundToInt() - 25) }
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.7f))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pointerId",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // 无触控时的提示
                if (pointerCount == 0) {
                    Text(
                        text = "多点触控此区域",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 指针位置信息
            if (pointerPositions.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "触控点信息:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    pointerPositions.forEach { (id, pos) ->
                        Text(
                            text = "  指针 $id: (${pos.x.roundToInt()}, ${pos.y.roundToInt()})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Scrollable 修饰符示例
 *
 * Modifier.scrollable 提供低级别的滚动处理，可以自定义滚动方向和行为。
 * 配合 ScrollableState 和 FlingBehavior 实现精细的滚动控制。
 */
@Composable
fun ScrollableModifierExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "7. Scrollable Modifier (可滚动修饰符)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Modifier.scrollable 提供低级别滚动处理，支持自定义方向、FlingBehavior 等。适用于自定义滚动组件。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 水平滚动偏移
            var horizontalOffset by remember { mutableFloatStateOf(0f) }
            val maxOffset = 600f

            Text(
                text = "水平滚动偏移: ${horizontalOffset.roundToInt()}px",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 水平滚动条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .scrollable(
                        orientation = Orientation.Horizontal,
                        state = remember {
                            ScrollableState { delta ->
                                val consumed = if (horizontalOffset + delta > maxOffset) {
                                    maxOffset - horizontalOffset
                                } else if (horizontalOffset + delta < 0f) {
                                    -horizontalOffset
                                } else {
                                    delta
                                }
                                horizontalOffset += consumed
                                consumed
                            }
                        }
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                // 滚动轨道
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                // 滚动指示器
                Box(
                    modifier = Modifier
                        .offset { IntOffset((horizontalOffset / maxOffset * 200f).roundToInt(), 0) }
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${((horizontalOffset / maxOffset) * 100).roundToInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // 进度条
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("0%", style = MaterialTheme.typography.bodySmall)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(((horizontalOffset / maxOffset) * 100).coerceIn(0f, 100f).dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                Text("100%", style = MaterialTheme.typography.bodySmall)
            }

            // 垂直滚动偏移
            var verticalOffset by remember { mutableFloatStateOf(0f) }
            val verticalMax = 300f

            Text(
                text = "垂直滚动偏移: ${verticalOffset.roundToInt()}px",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = remember {
                            ScrollableState { delta ->
                                val consumed = if (verticalOffset + delta > verticalMax) {
                                    verticalMax - verticalOffset
                                } else if (verticalOffset + delta < 0f) {
                                    -verticalOffset
                                } else {
                                    delta
                                }
                                verticalOffset += consumed
                                consumed
                            }
                        }
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                // 可视化垂直滚动位置
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "垂直滚动在此区域滑动",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, (verticalOffset / verticalMax * 60).roundToInt()) }
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${((verticalOffset / verticalMax) * 100).roundToInt()}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * 嵌套滑动示例
 *
 * 使用 Modifier.nestedScroll 和 NestedScrollConnection 实现嵌套滑动。
 * 父组件可以在子组件处理滚动之前或之后拦截和消费滚动事件。
 */
@Composable
fun NestedScrollingExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "8. Nested Scrolling (嵌套滑动)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "NestedScrollConnection 允许父组件在子组件消费滚动前/后进行拦截。这是协调嵌套滚动的核心机制。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 嵌套滑动状态
            var parentConsumed by remember { mutableStateOf(Offset.Zero) }
            var childConsumed by remember { mutableStateOf(Offset.Zero) }
            var parentPreScroll by remember { mutableStateOf(Offset.Zero) }
            var parentPostScroll by remember { mutableStateOf(Offset.Zero) }

            // 状态显示
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "父组件预消费: (${parentPreScroll.x.roundToInt()}, ${parentPreScroll.y.roundToInt()})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "子组件消费: (${childConsumed.x.roundToInt()}, ${childConsumed.y.roundToInt()})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "父组件后消费: (${parentPostScroll.x.roundToInt()}, ${parentPostScroll.y.roundToInt()})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // 嵌套滚动区域 - 外层 (父)
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        // 父组件先消费 20% 的垂直滚动
                        val consumed = Offset(0f, available.y * 0.2f)
                        parentPreScroll = consumed
                        return consumed
                    }

                    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                        // 子组件消费后，父组件再消费剩余的 10%
                        val postConsumed = Offset(0f, available.y * 0.1f)
                        parentPostScroll = postConsumed
                        return postConsumed
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .nestedScroll(nestedScrollConnection)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "外层容器 (父)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 内层 (子) - 可滚动列表
                    val innerScrollState = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(innerScrollState)
                        ) {
                            Text(
                                text = "内层容器 (子) - 滚动列表",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // 生成列表项
                            repeat(20) { index ->
                                val hue = (index * 18f) % 360f
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            Color(
                                                android.graphics.Color.HSVToColor(
                                                    floatArrayOf(hue, 0.3f, 0.9f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "列表项 ${index + 1}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF333333)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 重置按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        parentConsumed = Offset.Zero
                        childConsumed = Offset.Zero
                        parentPreScroll = Offset.Zero
                        parentPostScroll = Offset.Zero
                    }
                ) {
                    Text("重置统计")
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
