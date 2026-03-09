package com.peter.compose.demo.level1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * ElevationActivity - Elevation 与 translationZ 详解
 *
 * 学习目标：
 * 1. Compose 中的 elevation 和 shadow
 * 2. 传统 View 中的 elevation 和 translationZ
 * 3. 动态 elevation 变化
 * 4. 实际应用场景
 */
class ElevationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    ElevationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ElevationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Elevation 基础概念
        ElevationBasics()

        // 2. Compose shadow 示例
        ComposeShadowExamples()

        // 3. Card elevation 示例
        CardElevationExamples()

        // 4. 动态 elevation 变化
        DynamicElevationExample()

        // 5. 传统 View elevation 示例
        TraditionalViewElevationExamples()

        // 6. 传统 View translationZ 示例
        TraditionalViewTranslationZExamples()

        // 7. 实际应用场景
        PracticalExamples()
    }
}

/**
 * 1. Elevation 基础概念
 */
@Composable
fun ElevationBasics() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. Elevation 基础概念",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Elevation（高度）表示组件在 Z 轴上的位置，影响阴影大小和层级关系。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 层级示意图
            Text(
                text = "Material Design 高度层级:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevationLevelItem(level = 0, name = "0dp - 无阴影", description = "平面元素")
                ElevationLevelItem(level = 1, name = "1dp - 卡片", description = "Card 默认")
                ElevationLevelItem(level = 2, name = "2dp - 浮动按钮", description = "Raised Button")
                ElevationLevelItem(level = 3, name = "3dp - 按下状态", description = "Button Pressed")
                ElevationLevelItem(level = 4, name = "4dp - 菜单", description = "Menu, Dialog")
                ElevationLevelItem(level = 6, name = "6dp - 浮动按钮", description = "FAB Resting")
                ElevationLevelItem(level = 8, name = "8dp - 底部导航栏", description = "Bottom Nav")
                ElevationLevelItem(level = 12, name = "12dp - 按压浮动按钮", description = "FAB Pressed")
                ElevationLevelItem(level = 16, name = "16dp - 模态底部抽屉", description = "Modal Bottom Sheet")
                ElevationLevelItem(level = 24, name = "24dp - 抽屉/选择器", description = "Nav Drawer, Picker")
            }

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """// Compose shadow Modifier
Box(
    modifier = Modifier.shadow(8.dp, shape)
)

// Card elevation
Card(elevation = CardDefaults.cardElevation(4.dp))

// 传统 View elevation
view.elevation = 8f
view.translationZ = 4f""",
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
fun ElevationLevelItem(level: Int, name: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .shadow(level.dp, RoundedCornerShape(4.dp))
                .size(40.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${level}dp",
                style = MaterialTheme.typography.labelSmall
            )
        }
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 2. Compose shadow 示例
 */
@Composable
fun ComposeShadowExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. Compose shadow Modifier",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 Modifier.shadow() 添加阴影效果。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 不同阴影大小
            Text(
                text = "不同阴影大小:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    val elevation = (index + 1) * 4
                    Box(
                        modifier = Modifier
                            .shadow(elevation.dp, RoundedCornerShape(8.dp))
                            .size(50.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${elevation}dp",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // 不同形状的阴影
            Text(
                text = "不同形状的阴影:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 圆形
                Box(
                    modifier = Modifier
                        .shadow(8.dp, CircleShape)
                        .size(50.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("圆形", style = MaterialTheme.typography.labelSmall)
                }

                // 圆角矩形
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .size(50.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("圆角", style = MaterialTheme.typography.labelSmall)
                }

                // 大圆角
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .size(50.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("大圆角", style = MaterialTheme.typography.labelSmall)
                }

                // 不对称圆角
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                        .size(50.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("不对称", style = MaterialTheme.typography.labelSmall)
                }
            }

            // 阴影颜色
            Text(
                text = "自定义阴影颜色:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 默认颜色
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .size(50.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("默认", style = MaterialTheme.typography.labelSmall)
                }

                // 蓝色阴影
                Box(
                    modifier = Modifier
                        .graphicsLayer { shadowElevation = 8.dp.toPx() }
                        .background(
                            Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("蓝色", style = MaterialTheme.typography.labelSmall)
                }

                // 带边框阴影效果
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5))
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "渐变",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            // 代码示例
            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """// 基本用法
Box(
    modifier = Modifier.shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        clip = false  // 是否裁剪内容
    )
)

// graphicsLayer 方式 (更灵活)
Box(
    modifier = Modifier.graphicsLayer {
        shadowElevation = 8.dp.toPx()
        shape = RoundedCornerShape(8.dp)
        clip = true
    }
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

/**
 * 3. Card elevation 示例
 */
@Composable
fun CardElevationExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. Card elevation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Material3 Card 的 elevation 支持不同状态。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 不同 elevation 的卡片
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("1dp", style = MaterialTheme.typography.titleSmall)
                            Text("默认状态", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("4dp", style = MaterialTheme.typography.titleSmall)
                            Text("悬停状态", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("8dp", style = MaterialTheme.typography.titleSmall)
                            Text("聚焦状态", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // 带状态的 Card
            var pressedCount by remember { mutableStateOf(0) }
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val elevation by animateDpAsState(
                targetValue = if (isPressed) 12.dp else 2.dp,
                animationSpec = tween(200),
                label = "elevation"
            )

            Text(
                text = "动态 elevation (点击卡片):",
                style = MaterialTheme.typography.labelMedium
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        pressedCount++
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = elevation
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPressed) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (isPressed) "按下中: ${elevation.value}dp" else "点击我",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "点击次数: $pressedCount",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // 代码示例
            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """Card(
    elevation = CardDefaults.cardElevation(
        defaultElevation = 2.dp,
        pressedElevation = 8.dp,
        hoveredElevation = 4.dp,
        focusedElevation = 4.dp,
        draggedElevation = 8.dp
    )
) {
    // content
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
 * 4. 动态 elevation 变化
 */
@Composable
fun DynamicElevationExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 动态 Elevation 变化",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "通过 Slider 动态调整 elevation。",
                style = MaterialTheme.typography.bodyMedium
            )

            var elevationValue by remember { mutableFloatStateOf(4f) }
            val animatedElevation by animateDpAsState(
                targetValue = elevationValue.dp,
                animationSpec = tween(300),
                label = "elevation"
            )

            Text(
                text = "当前 Elevation: ${elevationValue.toInt()}dp",
                style = MaterialTheme.typography.labelMedium
            )

            Slider(
                value = elevationValue,
                onValueChange = { elevationValue = it },
                valueRange = 0f..24f,
                modifier = Modifier.fillMaxWidth()
            )

            // 动态阴影效果
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .shadow(animatedElevation, RoundedCornerShape(16.dp))
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${elevationValue.toInt()}dp",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }

            // 代码示例
            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """var elevation by remember { mutableFloatStateOf(4f) }
val animatedElevation by animateDpAsState(
    targetValue = elevation.dp,
    animationSpec = tween(300)
)

Box(
    modifier = Modifier.shadow(animatedElevation, shape)
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

/**
 * 5. 传统 View elevation 示例
 */
@Composable
fun TraditionalViewElevationExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. 传统 View Elevation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "在传统 View 系统中使用 elevation。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 传统 View elevation 示例
            AndroidView(
                factory = { context ->
                    android.widget.LinearLayout(context).apply {
                        orientation = android.widget.LinearLayout.HORIZONTAL
                        setPadding(32, 32, 32, 32)
                        
                        // 创建多个不同 elevation 的 View
                        repeat(5) { index ->
                            val elevation = (index + 1) * 4f
                            val textView = android.widget.TextView(context).apply {
                                text = "${(index + 1) * 4}dp"
                                setPadding(24, 24, 24, 24)
                                setTextColor(android.graphics.Color.BLACK)
                                gravity = android.view.Gravity.CENTER
                                setElevation(elevation)
                                background = android.graphics.drawable.GradientDrawable().apply {
                                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                                    cornerRadius = 16f
                                    setColor(android.graphics.Color.WHITE)
                                }
                            }
                            val params = android.widget.LinearLayout.LayoutParams(
                                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(16, 0, 16, 0)
                            }
                            addView(textView, params)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
            )

            // 代码示例
            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """// XML 方式
<View
    android:elevation="8dp"
    android:background="@drawable/shape_with_outline" />

// Kotlin 代码方式
view.elevation = 8f  // 单位是像素

// 需要背景有 outline
view.background = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    cornerRadius = 16f
    setColor(Color.WHITE)
}

// 或使用 CardView
<androidx.cardview.widget.CardView
    app:cardElevation="8dp"
    app:cardCornerRadius="8dp" />""",
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
 * 6. 传统 View translationZ 示例
 */
@Composable
fun TraditionalViewTranslationZExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "6. 传统 View translationZ",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "translationZ 用于临时改变 Z 轴位置，常用于按压效果。",
                style = MaterialTheme.typography.bodyMedium
            )

            // translationZ vs elevation
            Text(
                text = "elevation vs translationZ:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "• elevation: 静态的基础高度，定义组件的层级\n• translationZ: 动态的偏移量，叠加在 elevation 上\n• 实际 Z = elevation + translationZ",
                style = MaterialTheme.typography.bodySmall
            )

            // 传统 View translationZ 示例
            AndroidView(
                factory = { context ->
                    android.widget.LinearLayout(context).apply {
                        orientation = android.widget.LinearLayout.VERTICAL
                        setPadding(32, 32, 32, 32)
                        
                        // 创建一个可点击的按钮效果
                        val button = android.widget.TextView(context).apply {
                            text = "点击查看 translationZ 效果"
                            setPadding(48, 32, 48, 32)
                            setTextColor(android.graphics.Color.WHITE)
                            gravity = android.view.Gravity.CENTER
                            elevation = 4f
                            translationZ = 0f
                            background = android.graphics.drawable.GradientDrawable().apply {
                                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                                cornerRadius = 24f
                                setColor(android.graphics.Color.parseColor("#6200EE"))
                            }
                            setOnClickListener {
                                // 点击动画
                                animate()
                                    .translationZ(8f)
                                    .setDuration(100)
                                    .withEndAction {
                                        animate()
                                            .translationZ(0f)
                                            .setDuration(200)
                                            .start()
                                    }
                                    .start()
                            }
                        }
                        val params = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        addView(button, params)
                        
                        // 状态选择器示例
                        val stateButton = android.widget.TextView(context).apply {
                            text = "StateListAnimator 方式"
                            setPadding(48, 32, 48, 32)
                            setTextColor(android.graphics.Color.WHITE)
                            gravity = android.view.Gravity.CENTER
                            background = android.graphics.drawable.GradientDrawable().apply {
                                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                                cornerRadius = 24f
                                setColor(android.graphics.Color.parseColor("#03DAC5"))
                            }
                            // 点击效果
                            setOnTouchListener { v, event ->
                                when (event.action) {
                                    android.view.MotionEvent.ACTION_DOWN -> {
                                        v.animate().translationZ(8f).setDuration(100).start()
                                    }
                                    android.view.MotionEvent.ACTION_UP,
                                    android.view.MotionEvent.ACTION_CANCEL -> {
                                        v.animate().translationZ(0f).setDuration(100).start()
                                    }
                                }
                                false
                            }
                        }
                        val stateParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { topMargin = 32 }
                        addView(stateButton, stateParams)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
            )

            // 代码示例
            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """// 设置基础 elevation
view.elevation = 4f

// 动态添加 translationZ
view.translationZ = 8f

// 实际 Z 值 = elevation + translationZ
val actualZ = view.elevation + view.translationZ  // = 12f

// 动画效果
view.animate()
    .translationZ(8f)
    .setDuration(100)
    .start()

// 使用 StateListAnimator (XML)
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <objectAnimator
            android:propertyName="translationZ"
            android:valueTo="8dp"
            android:duration="100" />
    </item>
    <item>
        <objectAnimator
            android:propertyName="translationZ"
            android:valueTo="0dp"
            android:duration="100" />
    </item>
</selector>""",
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
 * 7. 实际应用场景
 */
@Composable
fun PracticalExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "7. 实际应用场景",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 场景1: 可点击卡片
            Text(
                text = "场景1: 可点击卡片 (点击时抬起)",
                style = MaterialTheme.typography.labelMedium
            )

            val cardInteractionSource = remember { MutableInteractionSource() }
            val isCardPressed by cardInteractionSource.collectIsPressedAsState()
            val cardElevation by animateDpAsState(
                targetValue = if (isCardPressed) 12.dp else 2.dp,
                label = "cardElevation"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable(
                        interactionSource = cardInteractionSource,
                        indication = null
                    ) { },
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("点击卡片查看 elevation 变化")
                }
            }

            // 场景2: 浮动按钮
            Text(
                text = "场景2: 浮动按钮 (FAB 效果)",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 普通 FAB
                Box(
                    modifier = Modifier
                        .shadow(6.dp, CircleShape)
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }

                // 按压状态的 FAB
                val fabInteractionSource = remember { MutableInteractionSource() }
                val isFabPressed by fabInteractionSource.collectIsPressedAsState()
                val fabElevation by animateDpAsState(
                    targetValue = if (isFabPressed) 12.dp else 6.dp,
                    label = "fabElevation"
                )

                Box(
                    modifier = Modifier
                        .shadow(fabElevation, CircleShape)
                        .size(56.dp)
                        .background(
                            if (isFabPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            else MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .clickable(
                            interactionSource = fabInteractionSource,
                            indication = null
                        ) { },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }

                // Extended FAB
                Box(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("创建", color = Color.White)
                }
            }

            // 场景3: 底部导航栏
            Text(
                text = "场景3: 底部导航栏 (固定高度)",
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp)
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Text("首页", style = MaterialTheme.typography.labelMedium)
                    Text("搜索", style = MaterialTheme.typography.labelMedium)
                    Text("我的", style = MaterialTheme.typography.labelMedium)
                }
            }

            // 场景4: 对话框
            Text(
                text = "场景4: 对话框 (高 elevation)",
                style = MaterialTheme.typography.labelMedium
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .shadow(24.dp, RoundedCornerShape(16.dp))
                        .width(200.dp)
                        .height(100.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Dialog", style = MaterialTheme.typography.titleMedium)
                        Text("24dp elevation", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 代码示例
            Text(
                text = "应用场景最佳实践:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• 卡片: 1-4dp，点击时增加到 8dp",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• FAB: 6dp，点击时增加到 12dp",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 底部导航: 8dp 固定",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 菜单/弹窗: 8dp",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Dialog: 24dp",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 抽屉: 16dp",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElevationPreview() {
    MaterialTheme {
        ElevationScreen()
    }
}
