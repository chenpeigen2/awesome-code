package com.peter.compose.demo.level6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SharedElementTransitionActivity - 共享元素转场
 *
 * 学习目标：
 * 1. SharedTransitionLayout 和 SharedTransitionScope 的使用
 * 2. Modifier.sharedElement 实现图片/颜色块共享动画
 * 3. Modifier.sharedBounds 实现文字共享动画
 * 4. 列表 → 详情完整转场示例
 * 5. 自定义动画参数（spring、tween、overlay）
 */

// ========== 数据模型 ==========

/**
 * 列表项数据
 */
data class DemoItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val color: Color,
    val description: String
)

// 示例数据
val sampleItems = listOf(
    DemoItem(1, "星空探索", "探索宇宙的奥秘", Color(0xFF1A237E), "浩瀚无垠的宇宙中，星辰闪烁，每一颗都有着自己的故事。从黑洞到星云，从行星到卫星，宇宙充满了令人惊叹的奇观。"),
    DemoItem(2, "海洋世界", "深海的神秘生物", Color(0xFF006064), "海洋覆盖了地球表面的 71%，深海中隐藏着无数未知的生物。发光水母、巨型章鱼、深海鱼类，构成了一个奇妙的世界。"),
    DemoItem(3, "森林秘境", "热带雨林的生态", Color(0xFF1B5E20), "热带雨林是地球上最丰富的生态系统之一，拥有超过一半的动植物物种。每一棵树、每一片叶子都是生命的奇迹。"),
    DemoItem(4, "沙漠之旅", "撒哈拉的壮丽景观", Color(0xFFE65100), "撒哈拉沙漠是世界上最大的热沙漠，沙丘绵延不绝，日出日落的色彩变幻令人叹为观止。"),
    DemoItem(5, "极光之舞", "北极光的绚丽光芒", Color(0xFF4A148C), "极光是自然界最壮观的光学现象之一，当太阳风与地球磁场相互作用时，天空中便出现了绚丽的色彩。"),
    DemoItem(6, "冰川世界", "南极冰川的蓝色之美", Color(0xFF01579B), "南极洲拥有世界上最大的冰盖，冰川的蓝色来自光的散射，每一座冰山都是独一无二的雕塑。")
)

// ========== Activity ==========

class SharedElementTransitionActivity : ComponentActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    SharedElementTransitionScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ========== 主屏幕 ==========

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElementTransitionScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 页面标题
        Text(
            text = "Shared Element Transition",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // 1. SharedTransitionLayout 概念
        ConceptSection()

        // 2. 图片共享元素
        ImageSharedElementDemo()

        // 3. 文字共享元素
        TextSharedElementDemo()

        // 4. 列表 → 详情完整示例
        FullListDetailDemo()

        // 5. 自定义动画参数
        CustomAnimationDemo()
    }
}

// ========== 1. 概念说明 ==========

@Composable
fun ConceptSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. SharedTransitionLayout 概念",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "SharedTransitionLayout 是 Compose 提供的共享元素转场容器。它为子组件提供了 SharedTransitionScope，" +
                        "使不同 UI 状态之间的元素可以平滑过渡动画。",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "核心 API：",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = """  SharedTransitionLayout {
    // 这里的内容可以访问 SharedTransitionScope
    AnimatedVisibility(visible = showList) {
        // 列表中的元素
        Box(Modifier.sharedElement(
            rememberSharedContentState(key = "image-1"),
            animatedVisibilityScope = this
        ))
    }
    AnimatedVisibility(visible = !showList) {
        // 详情中的元素，使用相同的 key
        Box(Modifier.sharedElement(
            rememberSharedContentState(key = "image-1"),
            animatedVisibilityScope = this
        ))
    }
}""",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    .padding(8.dp)
            )

            Text(
                text = "- sharedElement: 完全共享元素（大小、位置、形状都会动画）\n" +
                        "- sharedBounds: 共享边界（适合文字等只需要共享位置和边界的场景）",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========== 2. 图片共享元素 ==========

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageSharedElementDemo() {
    var showDetail by remember { mutableStateOf(false) }
    var selectedId by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 图片共享元素",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "点击色块查看 sharedElement 动画效果，色块从网格位置过渡到详情页。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 使用 SharedTransitionLayout 包裹动画内容
            SharedTransitionLayout {
                AnimatedVisibility(
                    visible = !showDetail,
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    // 网格视图
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(sampleItems) { item ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(item.color)
                                    // 共享元素标记，key 为 "img-{id}"
                                    .sharedElement(
                                        rememberSharedContentState(key = "img-${item.id}"),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                    .clickable {
                                        selectedId = item.id
                                        showDetail = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showDetail,
                    enter = fadeIn(animationSpec = tween(200))
                ) {
                    // 详情视图：显示被点击的色块（大尺寸）
                    val item = sampleItems.find { it.id == selectedId }
                    if (item != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(item.color)
                                    // 相同 key 的共享元素
                                    .sharedElement(
                                        rememberSharedContentState(key = "img-${item.id}"),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                    .clickable { showDetail = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "点击色块返回网格",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========== 3. 文字共享元素 ==========

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TextSharedElementDemo() {
    var showDetail by remember { mutableStateOf(false) }
    var selectedId by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 文字共享元素",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "点击列表项查看 sharedBounds 动画，标题文字在列表和详情之间平滑过渡。",
                style = MaterialTheme.typography.bodyMedium
            )

            SharedTransitionLayout {
                AnimatedVisibility(
                    visible = !showDetail,
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    // 文字列表
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleItems.take(4).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        selectedId = item.id
                                        showDetail = true
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                // 使用 sharedBounds 实现文字共享
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState(key = "title-${item.id}"),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showDetail,
                    enter = fadeIn(animationSpec = tween(200))
                ) {
                    val item = sampleItems.find { it.id == selectedId }
                    if (item != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 详情页中的大标题，与列表中的文字共享边界
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sharedBounds(
                                        rememberSharedContentState(key = "title-${item.id}"),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                    .clickable { showDetail = false }
                            )
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "点击标题返回列表",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========== 4. 列表 → 详情完整示例 ==========

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FullListDetailDemo() {
    var showDetail by remember { mutableStateOf(false) }
    var selectedId by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 列表 \u2192 详情完整示例",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "点击列表项进入详情，头像从小圆变为大方块，标题文字平滑放大，额外内容淡入。",
                style = MaterialTheme.typography.bodyMedium
            )

            SharedTransitionLayout {
                // 列表视图
                AnimatedVisibility(
                    visible = !showDetail,
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        selectedId = item.id
                                        showDetail = true
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 头像：小圆形
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                        // 共享元素：头像
                                        .sharedElement(
                                            rememberSharedContentState(key = "avatar-${item.id}"),
                                            animatedVisibilityScope = this@AnimatedVisibility
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.title.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    // 标题文字
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(key = "detail-title-${item.id}"),
                                            animatedVisibilityScope = this@AnimatedVisibility
                                        )
                                    )
                                    // 副标题
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // 右侧箭头提示
                                Text(
                                    text = ">",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }

                // 详情视图
                AnimatedVisibility(
                    visible = showDetail,
                    enter = fadeIn(animationSpec = tween(300))
                ) {
                    val item = sampleItems.find { it.id == selectedId }
                    if (item != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 顶部：返回按钮 + 大头像
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { showDetail = false }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "返回"
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // 头像：大方块
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(item.color)
                                        // 共享元素：头像（与列表中相同的 key）
                                        .sharedElement(
                                            rememberSharedContentState(key = "avatar-${item.id}"),
                                            animatedVisibilityScope = this@AnimatedVisibility
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.title.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    )
                                }
                            }

                            // 标题（共享边界）
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.sharedBounds(
                                    rememberSharedContentState(key = "detail-title-${item.id}"),
                                    animatedVisibilityScope = this@AnimatedVisibility
                                )
                            )

                            // 副标题（淡入）
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = item.color
                            )

                            // 分割线
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            // 详细描述内容
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp
                            )

                            // 额外信息卡片
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoChip(label = "ID", value = "#${item.id}")
                                InfoChip(label = "颜色", value = "#${item.color.value.toString(16).takeLast(6)}")
                                InfoChip(label = "字数", value = "${item.description.length}")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 信息标签组件
 */
@Composable
fun InfoChip(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ========== 5. 自定义动画参数 ==========

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CustomAnimationDemo() {
    var showDetail by remember { mutableStateOf(false) }
    var selectedId by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. 自定义动画参数",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "通过 renderInSharedContentScopeDuring、clipInOverlayDuringTransition、自定义动画规格" +
                        "来控制共享元素动画的行为。",
                style = MaterialTheme.typography.bodyMedium
            )

            SharedTransitionLayout {
                AnimatedVisibility(
                    visible = !showDetail,
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    // 列表视图 - 简化版
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleItems.take(3).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        selectedId = item.id
                                        showDetail = true
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(item.color)
                                        // 使用自定义 spring 动画规格
                                        .sharedElement(
                                            rememberSharedContentState(key = "custom-${item.id}"),
                                            animatedVisibilityScope = this@AnimatedVisibility,
                                            // 使用 spring 替代默认的 tween 动画
                                            boundsTransform = { _, _ ->
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            },
                                            // 过渡期间裁剪为圆角矩形
                                            clipInOverlayDuringTransition = OverlayClip(
                                                RoundedCornerShape(16.dp)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.title.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState(key = "custom-title-${item.id}"),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showDetail,
                    enter = fadeIn(animationSpec = tween(300))
                ) {
                    val item = sampleItems.find { it.id == selectedId }
                    if (item != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { showDetail = false }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "返回"
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(item.color)
                                        // 相同 key + 自定义 spring 动画
                                        .sharedElement(
                                            rememberSharedContentState(key = "custom-${item.id}"),
                                            animatedVisibilityScope = this@AnimatedVisibility,
                                            boundsTransform = { _, _ ->
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            },
                                            clipInOverlayDuringTransition = OverlayClip(
                                                RoundedCornerShape(16.dp)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.title.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 32.sp
                                    )
                                }
                            }

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.sharedBounds(
                                    rememberSharedContentState(key = "custom-title-${item.id}"),
                                    animatedVisibilityScope = this@AnimatedVisibility
                                )
                            )

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp
                            )

                            // 动画参数说明
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "使用的自定义参数：",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "boundsTransform = spring(dampingRatio = MediumBouncy, stiffness = Low)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(16.dp))",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "enter = fadeIn(tween(300)) / exit = fadeOut(tween(300))",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
