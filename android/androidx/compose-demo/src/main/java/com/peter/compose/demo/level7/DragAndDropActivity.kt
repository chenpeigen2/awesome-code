package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// ============================================================
// 拖拽数据模型
// ============================================================

/**
 * 拖拽项数据类
 * @param id 唯一标识
 * @param title 显示标题
 * @param color 背景颜色
 */
data class DragItem(val id: Int, val title: String, val color: Color)

// ============================================================
// DragAndDropActivity - 拖拽排序演示
// 包含：基础拖拽、列表排序、网格排序、长按触发拖拽
// ============================================================

class DragAndDropActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    DragAndDropScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ============================================================
// 主屏幕 - 使用垂直滚动的 Column 分区展示各种拖拽示例
// ============================================================

@Composable
fun DragAndDropScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 页面标题
        Text(
            text = "拖拽与排序",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "学习 Compose 中的拖拽手势和列表排序交互",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 第一部分：基础拖拽
        BasicDragSection()

        HorizontalDivider()

        // 第二部分：列表拖拽排序（含视觉反馈）
        ListReorderSection()

        HorizontalDivider()

        // 第三部分：网格拖拽排序
        GridReorderSection()

        HorizontalDivider()

        // 第四部分：长按触发拖拽
        LongPressDragSection()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================================
// 1. 基础拖拽 (Basic Drag)
// 使用 Modifier.draggable 实现简单的水平拖拽
// ============================================================

@Composable
fun BasicDragSection() {
    SectionTitle(title = "基础拖拽", subtitle = "使用 Modifier.draggable 实现单轴拖拽")

    var offsetX by remember { mutableFloatStateOf(0f) }
    // draggableState 用于跟踪拖拽偏移量
    val state = rememberDraggableState { delta ->
        // 限制拖拽范围在 -200px 到 200px 之间，避免拖出可视区域
        offsetX = (offsetX + delta).coerceIn(-200f, 200f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 拖拽轨道容器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 可拖拽的圆形按钮
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = state
                    )
                    .size(56.dp)
                    .shadow(4.dp, CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "拖我",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 显示当前偏移值，方便观察拖拽效果
        Text(
            text = "当前偏移: ${offsetX.roundToInt()}px",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================
// 2. 列表拖拽排序 (List Reorder) + 拖拽视觉反馈 (Drag Visual Feedback)
// 使用 Modifier.pointerInput 配合手势检测实现拖拽排序
// ============================================================

@Composable
fun ListReorderSection() {
    SectionTitle(
        title = "列表拖拽排序",
        subtitle = "拖动左侧手柄图标进行排序，带视觉反馈效果"
    )

    // 初始化列表数据，10 个不同颜色的项目
    val items = remember {
        mutableStateListOf(
            DragItem(1, "项目 Alpha", Color(0xFFE57373)),
            DragItem(2, "项目 Beta", Color(0xFF64B5F6)),
            DragItem(3, "项目 Gamma", Color(0xFF81C784)),
            DragItem(4, "项目 Delta", Color(0xFFFFB74D)),
            DragItem(5, "项目 Epsilon", Color(0xFFBA68C8)),
            DragItem(6, "项目 Zeta", Color(0xFF4DB6AC)),
            DragItem(7, "项目 Eta", Color(0xFFF06292)),
            DragItem(8, "项目 Theta", Color(0xFFAED581)),
            DragItem(9, "项目 Iota", Color(0xFFFF8A65)),
            DragItem(10, "项目 Kappa", Color(0xFF9575CD))
        )
    }

    // 拖拽状态跟踪
    var dragIndex by remember { mutableIntStateOf(-1) }    // 当前正在拖拽的项索引
    var dragOffsetY by remember { mutableFloatStateOf(0f) } // 拖拽垂直偏移量

    // 每个列表项的高度（含间距），用于计算拖拽目标位置
    val itemHeight = 72f

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            val isDragged = dragIndex == index

            // 计算各项的垂直偏移：被拖拽项跟随手指，其他项根据位置变化平移让位
            val offsetY = when {
                isDragged -> dragOffsetY
                dragIndex >= 0 -> {
                    // 计算拖拽项预计到达的位置
                    val draggedFrom = dragIndex
                    val draggedTo = dragIndex + (dragOffsetY / itemHeight).roundToInt()
                    val targetPos = draggedTo.coerceIn(0, items.lastIndex)
                    // 在拖拽起始位置和目标位置之间的项需要移动
                    if (index in minOf(draggedFrom, targetPos)..maxOf(draggedFrom, targetPos)) {
                        if (draggedFrom < targetPos) {
                            // 向下拖拽：中间项向上移
                            if (index in (draggedFrom + 1)..targetPos) -itemHeight else 0f
                        } else {
                            // 向上拖拽：中间项向下移
                            if (index in targetPos until draggedFrom) itemHeight else 0f
                        }
                    } else {
                        0f
                    }
                }
                else -> 0f
            }

            // 视觉反馈动画：拖拽时放大并增加阴影
            val scale by animateFloatAsState(
                targetValue = if (isDragged) 1.05f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "scale"
            )
            val shadowElevation by animateFloatAsState(
                targetValue = if (isDragged) 12f else 2f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "shadow"
            )
            // 拖拽时颜色变淡，增强视觉区分
            val targetColor = if (isDragged) {
                item.color.copy(alpha = 0.85f)
            } else {
                item.color
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .shadow(shadowElevation.dp, RoundedCornerShape(12.dp))
                    .scale(scale)
                    .pointerInput(Unit) {
                        // 自定义手势检测：垂直拖拽排序
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            // 只在左侧拖拽手柄区域触发（x < 80px）
                            val handleWidth = 80f
                            if (down.position.x > handleWidth) return@awaitEachGesture
                            val startIndex = index

                            var totalDrag = 0f
                            // 持续跟踪拖拽
                            do {
                                val event = awaitPointerEvent()
                                val dragEvent = event.changes.first()
                                totalDrag += dragEvent.positionChange().y
                                dragOffsetY = totalDrag
                                dragIndex = startIndex

                                // 根据拖拽偏移计算目标位置并执行交换
                                val targetOffset = (totalDrag / itemHeight).roundToInt()
                                val targetIndex =
                                    (startIndex + targetOffset).coerceIn(0, items.lastIndex)
                                if (targetIndex != startIndex && targetIndex != dragIndex) {
                                    // 在列表中移动数据项
                                    val movedItem = items.removeAt(startIndex)
                                    items.add(targetIndex, movedItem)
                                    dragIndex = targetIndex
                                    // 调整偏移量，因为列表顺序已改变
                                    dragOffsetY -= (targetIndex - startIndex) * itemHeight
                                }
                            } while (dragEvent.pressed)

                            // 拖拽结束，重置所有状态
                            dragIndex = -1
                            dragOffsetY = 0f
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = targetColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 拖拽手柄图标（DragHandle）
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "拖拽手柄",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // 项目标题和 ID 信息
                    Column {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "ID: ${item.id}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// 3. 网格拖拽排序 (Grid Reorder)
// 3x3 网格布局，通过点击选择进行位置交换
// ============================================================

@Composable
fun GridReorderSection() {
    SectionTitle(
        title = "网格拖拽排序",
        subtitle = "点击两个不同的色块来交换位置"
    )

    // 9 个网格项，3x3 布局
    val gridItems = remember {
        mutableStateListOf(
            DragItem(1, "红", Color(0xFFE57373)),
            DragItem(2, "蓝", Color(0xFF64B5F6)),
            DragItem(3, "绿", Color(0xFF81C784)),
            DragItem(4, "橙", Color(0xFFFFB74D)),
            DragItem(5, "紫", Color(0xFFBA68C8)),
            DragItem(6, "青", Color(0xFF4DB6AC)),
            DragItem(7, "粉", Color(0xFFF06292)),
            DragItem(8, "黄", Color(0xFFFFF176)),
            DragItem(9, "靛", Color(0xFF7986CB))
        )
    }

    // 使用选中交换模式：先选中第一个，再选中第二个执行交换
    var selectedIndex by remember { mutableIntStateOf(-1) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(gridItems, key = { it.id }) { item ->
            val gridIndex = gridItems.indexOf(item)
            val isSelected = selectedIndex == gridIndex

            // 选中时的缩放动画，给用户视觉反馈
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 0.9f else 1f,
                label = "gridScale"
            )
            val borderWidth by animateFloatAsState(
                targetValue = if (isSelected) 3f else 0f,
                label = "gridBorder"
            )

            Card(
                modifier = Modifier
                    .height(96.dp)
                    .scale(scale)
                    .border(
                        borderWidth.dp,
                        Color.White,
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = item.color)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onLongClick = {
                                // 长按选中第一个项目
                                selectedIndex = gridIndex
                            },
                            onClick = {
                                if (selectedIndex >= 0 && selectedIndex != gridIndex) {
                                    // 已有选中项：交换两个色块的位置
                                    val temp = gridItems[selectedIndex]
                                    gridItems[selectedIndex] = gridItems[gridIndex]
                                    gridItems[gridIndex] = temp
                                    selectedIndex = -1
                                } else {
                                    // 第一次点击：选中当前色块
                                    selectedIndex = gridIndex
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.title,
                            color = if (item.color == Color(0xFFFFF176)) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "#${item.id}",
                            color = if (item.color == Color(0xFFFFF176)) {
                                Color.Black.copy(alpha = 0.6f)
                            } else {
                                Color.White.copy(alpha = 0.6f)
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // 操作提示文字
    Text(
        text = if (selectedIndex >= 0) {
            "已选中: ${gridItems[selectedIndex].title}，点击另一个色块交换"
        } else {
            "点击或长按色块选中，再点击另一个交换位置"
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

// ============================================================
// 4. 长按触发拖拽 (Long Press Trigger)
// 必须长按 500ms 后才能拖拽，防止误操作
// ============================================================

@Composable
fun LongPressDragSection() {
    SectionTitle(
        title = "长按触发拖拽",
        subtitle = "长按 500ms 后进入拖拽模式，可上下拖拽排序"
    )

    // 初始化列表数据 - 模拟后端服务列表
    val items = remember {
        mutableStateListOf(
            DragItem(1, "云存储", Color(0xFF42A5F5)),
            DragItem(2, "数据库", Color(0xFF66BB6A)),
            DragItem(3, "缓存", Color(0xFFFFA726)),
            DragItem(4, "消息队列", Color(0xFFEF5350)),
            DragItem(5, "负载均衡", Color(0xFFAB47BC)),
            DragItem(6, "安全认证", Color(0xFF26A69A)),
            DragItem(7, "日志系统", Color(0xFF78909C))
        )
    }

    // 拖拽状态
    var dragIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isLongPressed by remember { mutableStateOf(false) }

    val itemHeight = 68f

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            val isCurrentDrag = dragIndex == index

            // 计算非拖拽项的偏移量，与列表排序逻辑相同
            val offsetY = when {
                isCurrentDrag -> dragOffsetY
                dragIndex >= 0 -> {
                    val draggedFrom = dragIndex
                    val draggedTo = dragIndex + (dragOffsetY / itemHeight).roundToInt()
                    val targetPos = draggedTo.coerceIn(0, items.lastIndex)
                    if (index in minOf(draggedFrom, targetPos)..maxOf(draggedFrom, targetPos)) {
                        if (draggedFrom < targetPos) {
                            if (index in (draggedFrom + 1)..targetPos) -itemHeight else 0f
                        } else {
                            if (index in targetPos until draggedFrom) itemHeight else 0f
                        }
                    } else {
                        0f
                    }
                }
                else -> 0f
            }

            // 拖拽中的视觉反馈：轻微放大 + 阴影提升
            val scale by animateFloatAsState(
                targetValue = if (isCurrentDrag) 1.03f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "longPressScale"
            )
            val elevation by animateFloatAsState(
                targetValue = if (isCurrentDrag) 10f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "longPressElevation"
            )

            // 长按拖拽模式下改变背景透明度
            val containerColor = if (isCurrentDrag) {
                item.color.copy(alpha = 0.75f)
            } else {
                item.color
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .shadow(elevation.dp, RoundedCornerShape(12.dp))
                    .scale(scale)
                    .pointerInput(Unit) {
                        // 长按检测 + 拖拽手势组合
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downTime = System.currentTimeMillis()
                            var isDragStarted = false
                            var totalDrag = 0f
                            val startIdx = index

                            // 持续跟踪手指，判断是否达到长按阈值
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.first()

                                totalDrag += change.positionChange().y

                                // 长按阈值 500ms
                                if (!isDragStarted &&
                                    System.currentTimeMillis() - downTime > 500L
                                ) {
                                    isDragStarted = true
                                    isLongPressed = true
                                    dragIndex = startIdx
                                    dragOffsetY = 0f
                                }

                                // 长按激活后开始跟踪拖拽
                                if (isDragStarted) {
                                    dragOffsetY = totalDrag
                                    val targetOffset = (totalDrag / itemHeight).roundToInt()
                                    val targetIdx =
                                        (startIdx + targetOffset).coerceIn(0, items.lastIndex)
                                    if (targetIdx != startIdx && targetIdx != dragIndex) {
                                        val moved = items.removeAt(startIdx)
                                        items.add(targetIdx, moved)
                                        dragIndex = targetIdx
                                        dragOffsetY -= (targetIdx - startIdx) * itemHeight
                                    }
                                }
                            } while (change.pressed)

                            // 手指抬起，重置所有拖拽状态
                            dragIndex = -1
                            dragOffsetY = 0f
                            isLongPressed = false
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧拖拽指示器（三条横线图标）
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(24.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(2.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.7f),
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                            if (it < 2) Spacer(modifier = Modifier.height(2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 项目标题
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // 拖拽中的状态指示文字
                    if (isCurrentDrag) {
                        Text(
                            text = "拖拽中",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // 底部状态提示
    if (isLongPressed) {
        Text(
            text = "拖拽模式已激活，移动手指排序，松开完成",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    } else {
        Text(
            text = "长按列表项进入拖拽模式",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================
// 通用组件：区域标题
// ============================================================

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
