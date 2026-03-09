package com.peter.compose.demo.level4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * AnimationsActivity - 动画基础
 *
 * 学习目标：
 * 1. animate*AsState 状态动画
 * 2. AnimatedVisibility 显示/隐藏动画
 * 3. animateContentSize 内容变化动画
 */
class AnimationsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    AnimationsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimationsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. animate*AsState 示例
        AnimateAsStateExample()

        // 2. AnimatedVisibility 示例
        AnimatedVisibilityExample()

        // 3. animateContentSize 示例
        AnimateContentSizeExample()

        // 4. 无限动画示例
        InfiniteAnimationExample()
    }
}

/**
 * animate*AsState 示例
 *
 * 当状态改变时自动执行动画过渡
 */
@Composable
fun AnimateAsStateExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. animate*AsState",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "状态改变时自动执行过渡动画。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 颜色动画
            var colorState by remember { mutableStateOf(false) }
            val animatedColor by animateColorAsState(
                targetValue = if (colorState) MaterialTheme.colorScheme.primary
                              else MaterialTheme.colorScheme.secondary,
                animationSpec = tween(durationMillis = 500),
                label = "color"
            )

            Text(
                text = "颜色动画:",
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(animatedColor)
                    .clickable { colorState = !colorState },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "点击切换颜色",
                    color = Color.White
                )
            }

            // 尺寸动画
            var sizeState by remember { mutableStateOf(false) }
            val animatedSize by animateDpAsState(
                targetValue = if (sizeState) 100.dp else 60.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "size"
            )

            Text(
                text = "尺寸动画 (Spring 弹簧效果):",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable { sizeState = !sizeState },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "点击",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // 旋转动画
            var rotationState by remember { mutableStateOf(false) }
            val animatedRotation by animateFloatAsState(
                targetValue = if (rotationState) 360f else 0f,
                animationSpec = tween(durationMillis = 500),
                label = "rotation"
            )

            Text(
                text = "旋转动画:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.error)
                        .rotate(animatedRotation)
                        .clickable { rotationState = !rotationState },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔄",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

/**
 * AnimatedVisibility 示例
 *
 * 组件显示/隐藏时的动画效果
 */
@Composable
fun AnimatedVisibilityExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. AnimatedVisibility",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "组件显示/隐藏时的进入和退出动画。",
                style = MaterialTheme.typography.bodyMedium
            )

            var isVisible by remember { mutableStateOf(true) }

            Button(
                onClick = { isVisible = !isVisible },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isVisible) "隐藏" else "显示")
            }

            // 淡入淡出动画
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "淡入淡出动画",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // 滑入滑出动画
            var isSlideVisible by remember { mutableStateOf(true) }

            Button(
                onClick = { isSlideVisible = !isSlideVisible },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSlideVisible) "滑出" else "滑入")
            }

            AnimatedVisibility(
                visible = isSlideVisible,
                enter = slideIn(initialOffset = { fullSize -> IntOffset(-fullSize.width, 0) }),
                exit = slideOut(targetOffset = { fullSize -> IntOffset(-fullSize.width, 0) })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "滑入滑出动画",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * animateContentSize 示例
 *
 * 内容尺寸变化时的平滑过渡动画
 */
@Composable
fun AnimateContentSizeExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. animateContentSize",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "内容尺寸变化时的平滑过渡动画。",
                style = MaterialTheme.typography.bodyMedium
            )

            var isExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { isExpanded = !isExpanded }
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isExpanded) {
                        "这是展开后的内容。\n点击可以收起。\n这里可以显示更多详细信息。\n动画效果会平滑过渡。"
                    } else {
                        "点击展开查看更多内容..."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * 无限动画示例
 *
 * 持续运行的动画效果
 */
@Composable
fun InfiniteAnimationExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 无限动画",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 rememberInfiniteTransition 创建持续运行的动画。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 无限过渡
            val infiniteTransition = rememberInfiniteTransition(label = "infinite")

            // 旋转动画
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            // 缩放动画
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
            ) {
                // 旋转
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(rotation)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔄", style = MaterialTheme.typography.titleLarge)
                }

                // 缩放
                Box(
                    modifier = Modifier
                        .size(60.dp * scale)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⭐", style = MaterialTheme.typography.titleLarge)
                }
            }

            Text(
                text = "示例代码:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """val infiniteTransition = 
    rememberInfiniteTransition()

val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000),
        repeatMode = RepeatMode.Restart
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

@Preview(showBackground = true)
@Composable
fun AnimationsPreview() {
    MaterialTheme {
        AnimationsScreen()
    }
}
