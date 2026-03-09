package com.peter.compose.demo.level4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * ContentTransformActivity - 内容转换
 *
 * 学习目标：
 * 1. Crossfade: 淡入淡出切换
 * 2. AnimatedContent: 内容切换动画
 * 3. slideIn / slideOut: 滑动动画
 */
class ContentTransformActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    ContentTransformScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ContentTransformScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Crossfade 示例
        CrossfadeExample()

        // 2. AnimatedContent 示例
        AnimatedContentExample()

        // 3. 页面切换示例
        PageTransitionExample()
    }
}

/**
 * Crossfade 示例
 *
 * 在两个状态之间进行淡入淡出切换
 */
@Composable
fun CrossfadeExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. Crossfade",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Crossfade 在状态切换时执行淡入淡出动画。",
                style = MaterialTheme.typography.bodyMedium
            )

            var currentPage by remember { mutableIntStateOf(0) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Button(
                        onClick = { currentPage = index },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("页面 ${index + 1}")
                    }
                }
            }

            Crossfade(
                targetState = currentPage,
                animationSpec = tween(500),
                label = "crossfade"
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (page) {
                                0 -> MaterialTheme.colorScheme.primaryContainer
                                1 -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "页面 ${page + 1} 内容",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * AnimatedContent 示例
 *
 * 更灵活的内容切换动画，可以自定义进入和退出效果
 */
@Composable
fun AnimatedContentExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. AnimatedContent",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "AnimatedContent 提供更灵活的内容切换动画控制。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 计数器示例
            var count by remember { mutableIntStateOf(0) }

            Text(
                text = "数字切换动画:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { count-- }) {
                    Text("-")
                }

                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        if (targetState > initialState) {
                            // 数字增加：从下方滑入
                            slideInHorizontally { width -> width } togetherWith
                                    slideOutHorizontally { width -> -width }
                        } else {
                            // 数字减少：从上方滑入
                            slideInHorizontally { width -> -width } togetherWith
                                    slideOutHorizontally { width -> width }
                        }.using(SizeTransform(clip = false))
                    },
                    label = "count"
                ) { targetCount ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$targetCount",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }

                Button(onClick = { count++ }) {
                    Text("+")
                }
            }

            // 状态切换示例
            var state by remember { mutableStateOf(State.Loading) }

            Text(
                text = "状态切换动画:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                State.values().forEach { s ->
                    Button(
                        onClick = { state = s },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(s.name, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "state"
            ) { targetState ->
                StateContent(state = targetState)
            }
        }
    }
}

enum class State {
    Loading, Success, Error
}

@Composable
fun StateContent(state: State) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when (state) {
                    State.Loading -> MaterialTheme.colorScheme.surfaceVariant
                    State.Success -> Color(0xFF4CAF50)
                    State.Error -> Color(0xFFF44336)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (state) {
                State.Loading -> "⏳ 加载中..."
                State.Success -> "✅ 加载成功"
                State.Error -> "❌ 加载失败"
            },
            style = MaterialTheme.typography.titleMedium,
            color = when (state) {
                State.Loading -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> Color.White
            }
        )
    }
}

/**
 * 页面切换示例
 */
@Composable
fun PageTransitionExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 页面切换动画",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "模拟页面左右切换效果。",
                style = MaterialTheme.typography.bodyMedium
            )

            var pageIndex by remember { mutableIntStateOf(0) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { pageIndex = (pageIndex - 1).coerceAtLeast(0) },
                    enabled = pageIndex > 0
                ) {
                    Text("上一页")
                }

                Text(
                    text = "第 ${pageIndex + 1} 页",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Button(
                    onClick = { pageIndex = (pageIndex + 1).coerceAtMost(4) },
                    enabled = pageIndex < 4
                ) {
                    Text("下一页")
                }
            }

            AnimatedContent(
                targetState = pageIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        // 下一页：从右滑入
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        // 上一页：从左滑入
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "page"
            ) { page ->
                PageContent(page = page)
            }

            // 页面指示器
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == pageIndex) 10.dp else 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (index == pageIndex) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun PageContent(page: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when (page % 5) {
                    0 -> MaterialTheme.colorScheme.primaryContainer
                    1 -> MaterialTheme.colorScheme.secondaryContainer
                    2 -> MaterialTheme.colorScheme.tertiaryContainer
                    3 -> Color(0xFFFFE0B2)
                    else -> Color(0xFFC8E6C9)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "页面 ${page + 1}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "这是第 ${page + 1} 页的内容",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentTransformPreview() {
    MaterialTheme {
        ContentTransformScreen()
    }
}
