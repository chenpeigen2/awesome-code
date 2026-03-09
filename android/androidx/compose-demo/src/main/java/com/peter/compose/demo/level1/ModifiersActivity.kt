package com.peter.compose.demo.level1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * ModifiersActivity - 修饰符基础
 *
 * 学习目标：
 * 1. padding, margin (通过 padding 模拟)
 * 2. size, width, height
 * 3. background, border
 * 4. click, semantic
 */
class ModifiersActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    ModifiersScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ModifiersScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. padding 示例
        PaddingExamples()

        // 2. size 示例
        SizeExamples()

        // 3. background & border 示例
        BackgroundBorderExamples()

        // 4. clickable 示例
        ClickableExamples()

        // 5. 链式调用顺序
        ModifierOrderExamples()
    }
}

/**
 * ==================== padding 示例 ====================
 */
@Composable
fun PaddingExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. padding 修饰符",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 统一 padding
            Text(
                text = "统一 padding(16.dp):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFFE8EAF6))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color(0xFF3F51B5))
                )
            }

            // 水平和垂直 padding
            Text(
                text = "水平 padding(horizontal = 24.dp) + 垂直 padding(vertical = 8.dp):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFFE8F5E9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .background(Color(0xFF4CAF50))
                )
            }

            // 分别设置四个方向
            Text(
                text = "分别设置四边 padding(start=8, top=16, end=32, bottom=8):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFFCE4EC))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, top = 16.dp, end = 32.dp, bottom = 8.dp)
                        .background(Color(0xFFE91E63))
                )
            }

            // padding 顺序的重要性
            Text(
                text = "padding 顺序的影响:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 先 background 后 padding
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF2196F3))
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        )
                    }
                    Text(
                        text = "先背景后padding",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // 先 padding 后 background
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(16.dp)
                            .background(Color(0xFF2196F3))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        )
                    }
                    Text(
                        text = "先padding后背景",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

/**
 * ==================== size 示例 ====================
 */
@Composable
fun SizeExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. size 修饰符",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 固定尺寸
            Text(
                text = "固定尺寸 size(60.dp):",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF3F51B5))
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF7986CB))
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFC5CAE9))
                )
            }

            // width 和 height
            Text(
                text = "width(100.dp) 和 height(40.dp):",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp)
                        .background(Color(0xFFE91E63))
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .background(Color(0xFFF06292))
                )
            }

            // fillMaxWidth / fillMaxHeight
            Text(
                text = "fillMaxWidth(0.5f) - 占父容器 50% 宽度:",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(40.dp)
                    .background(Color(0xFF009688))
            )

            // size 范围限制
            Text(
                text = "size 范围限制:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 40.dp, minHeight = 40.dp)
                        .background(Color(0xFFFF9800))
                )
                Text("minSize", style = MaterialTheme.typography.labelSmall)

                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 60.dp, minHeight = 30.dp)
                        .background(Color(0xFFFFC107))
                )
                Text("defaultMin", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * ==================== background & border 示例 ====================
 */
@Composable
fun BackgroundBorderExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. background & border",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 纯色背景
            Text(
                text = "纯色背景:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFF3F51B5))
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE91E63))
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFF009688))
                )
            }

            // 渐变背景
            Text(
                text = "渐变背景 (Brush.verticalGradient):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6200EE),
                                Color(0xFF03DAC5)
                            )
                        )
                    )
            )

            Text(
                text = "渐变背景 (Brush.linearGradient):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFF4ECDC4)
                            )
                        )
                    )
            )

            // border 边框
            Text(
                text = "border 边框:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 普通边框
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(2.dp, Color(0xFF3F51B5), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2dp", style = MaterialTheme.typography.labelSmall)
                }

                // 粗边框
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(4.dp, Color(0xFFE91E63), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("4dp", style = MaterialTheme.typography.labelSmall)
                }

                // 圆形边框
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(2.dp, Color(0xFF009688), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Circle", style = MaterialTheme.typography.labelSmall)
                }

                // 切角边框
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(2.dp, Color(0xFFFF9800), CutCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cut", style = MaterialTheme.typography.labelSmall)
                }
            }

            // BorderStroke
            Text(
                text = "BorderStroke (渐变边框):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        BorderStroke(2.dp, Brush.linearGradient(
                            colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5))
                        )),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("渐变边框", style = MaterialTheme.typography.labelSmall)
            }

            // clip 裁剪
            Text(
                text = "clip 裁剪形状:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF3F51B5))
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE91E63))
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CutCornerShape(8.dp))
                        .background(Color(0xFF009688))
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                        .background(Color(0xFFFF9800))
                )
            }

            // shadow 阴影
            Text(
                text = "shadow 阴影:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("4dp", style = MaterialTheme.typography.labelSmall)
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("8dp", style = MaterialTheme.typography.labelSmall)
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("12dp", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

/**
 * ==================== clickable 示例 ====================
 */
@Composable
fun ClickableExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. clickable 点击",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 基础 clickable
            Text(
                text = "基础 Modifier.clickable:",
                style = MaterialTheme.typography.labelMedium
            )
            var clickCount = remember { 0 }
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        clickCount++
                    }
                    .background(
                        if (isPressed) Color(0xFF7986CB) else Color(0xFF3F51B5),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "点击次数: $clickCount",
                    color = Color.White
                )
            }

            // 带语义的 clickable
            Text(
                text = "带语义的 Modifier.semantics:",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .semantics {
                        contentDescription = "这是一个可点击的按钮，用于演示语义功能"
                    }
                    .clickable { }
                    .background(Color(0xFF009688), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "带语义描述的点击区域",
                    color = Color.White
                )
            }

            // 点击涟漪效果
            Text(
                text = "不同点击效果:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 有涟漪
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clickable { }
                        .background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("涟漪效果")
                }

                // 无涟漪
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { }
                        .background(Color(0xFFFCE4EC), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("无涟漪")
                }
            }
        }
    }
}

/**
 * ==================== 链式调用顺序示例 ====================
 */
@Composable
fun ModifierOrderExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. Modifier 链式调用顺序",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Modifier 链式调用顺序非常重要！不同的顺序会产生不同的效果。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 示例：padding 和 background 的顺序
            Text(
                text = "示例1: padding 与 background 顺序",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 先 background 后 padding
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFF3F51B5))  // 先设置背景
                            .padding(16.dp)  // 后设置 padding
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        )
                    }
                    Text(
                        text = "先背景后padding",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }

                // 先 padding 后 background
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp)  // 先设置 padding
                            .background(Color(0xFF3F51B5))  // 后设置背景
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        )
                    }
                    Text(
                        text = "先padding后背景",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 示例：size 和 padding 的顺序
            Text(
                text = "示例2: size 与 padding 顺序",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 先 size 后 padding
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(16.dp)
                            .background(Color(0xFFE91E63))
                    )
                    Text(
                        text = "先size后padding\n内容变小",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }

                // 先 padding 后 size
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(80.dp)
                            .background(Color(0xFFE91E63))
                    )
                    Text(
                        text = "先padding后size\n整体变大",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 示例：clickable 和 padding 的顺序
            Text(
                text = "示例3: clickable 与 padding 顺序",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 先 clickable 后 padding
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { }
                        .padding(16.dp)
                        .background(Color(0xFFFCE4EC), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "点击区域\n包含外padding",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }

                // 先 padding 后 clickable
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .clickable { }
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "点击区域\n不含外padding",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModifiersPreview() {
    MaterialTheme {
        ModifiersScreen()
    }
}
