package com.peter.compose.demo.level5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * CustomLayoutActivity - 自定义布局
 *
 * 学习目标：
 * 1. Layout 可组合函数
 * 2. MeasurePolicy 测量策略
 * 3. 实践：瀑布流布局
 */
class CustomLayoutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    CustomLayoutScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomLayoutScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. 简单自定义布局
        SimpleCustomLayoutExample()

        // 2. 瀑布流布局
        StaggeredGridLayoutExample()

        // 3. FlowRow 流式布局
        FlowRowExample()
    }
}

/**
 * 简单自定义布局示例
 *
 * 使用 Layout 函数创建自定义布局
 */
@Composable
fun SimpleCustomLayoutExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. 简单自定义布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 Layout 函数创建自定义布局，控制子元素的测量和放置。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 自定义垂直居中布局
            Text(
                text = "自定义垂直居中布局:",
                style = MaterialTheme.typography.labelMedium
            )

            CenteredColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                )
            }

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """@Composable
fun CenteredColumn(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        // 测量子元素
        val placeables = measurables.map {
            it.measure(constraints)
        }
        // 计算布局尺寸
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        // 布局
        layout(width, height) {
            var x = 0
            val totalHeight = placeables
                .maxOf { it.height }
            val yOffset = (height - totalHeight) / 2
            placeables.forEach { placeable ->
                placeable.placeRelative(x, yOffset)
                x += placeable.width
            }
        }
    }
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

/**
 * 自定义垂直居中布局
 */
@Composable
fun CenteredColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val width = constraints.maxWidth
        val height = constraints.maxHeight

        layout(width, height) {
            var xPosition = 0
            val maxHeight = placeables.maxOfOrNull { it.height } ?: 0
            val yPosition = (height - maxHeight) / 2

            placeables.forEach { placeable ->
                placeable.placeRelative(xPosition, yPosition)
                xPosition += placeable.width
            }
        }
    }
}

/**
 * 瀑布流布局示例
 */
@Composable
fun StaggeredGridLayoutExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 瀑布流布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "自定义瀑布流布局，子元素高度不一时自动排列。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 瀑布流布局
            StaggeredGrid(
                columns = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                val heights = listOf(60, 80, 50, 90, 70, 60, 80, 50, 70)
                val colors = listOf(
                    Color(0xFF3F51B5),
                    Color(0xFFE91E63),
                    Color(0xFF009688),
                    Color(0xFFFF9800),
                    Color(0xFF9C27B0),
                    Color(0xFF4CAF50),
                    Color(0xFF2196F3),
                    Color(0xFFFF5722),
                    Color(0xFF607D8B)
                )

                heights.forEachIndexed { index, height ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors[index]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * 瀑布流布局
 */
@Composable
fun StaggeredGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val columnWidth = constraints.maxWidth / columns
        val placeables = measurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = columnWidth,
                    maxWidth = columnWidth
                )
            )
        }

        // 跟踪每列的当前高度
        val columnHeights = IntArray(columns) { 0 }

        // 计算总高度
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }

        val totalHeight = columnHeights.maxOrNull() ?: 0

        layout(constraints.maxWidth, totalHeight) {
            // 重置列高度
            columnHeights.fill(0)

            // 放置元素
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                val x = column * columnWidth
                val y = columnHeights[column]

                placeable.placeRelative(x, y)
                columnHeights[column] += placeable.height
            }
        }
    }
}

/**
 * FlowRow 流式布局示例
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. FlowRow 流式布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 Compose 内置的 FlowRow 实现流式布局。",
                style = MaterialTheme.typography.bodyMedium
            )

            val tags = listOf(
                "Kotlin", "Android", "Compose", "Material Design",
                "Coroutines", "Flow", "ViewModel", "Hilt",
                "Room", "Retrofit", "Coil", "Navigation"
            )

            Text(
                text = "标签流式布局:",
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """FlowRow(
    horizontalArrangement = 
        Arrangement.spacedBy(8.dp),
    verticalArrangement = 
        Arrangement.spacedBy(8.dp)
) {
    tags.forEach { tag ->
        Chip(text = tag)
    }
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
fun CustomLayoutPreview() {
    MaterialTheme {
        CustomLayoutScreen()
    }
}
