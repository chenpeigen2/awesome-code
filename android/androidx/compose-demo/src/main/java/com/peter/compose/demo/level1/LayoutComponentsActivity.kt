package com.peter.compose.demo.level1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * LayoutComponentsActivity - 布局组件
 *
 * 学习目标：
 * 1. Column: 垂直布局、对齐
 * 2. Row: 水平布局、权重
 * 3. Box: 叠加布局
 * 4. Card: 卡片容器
 */
class LayoutComponentsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    LayoutComponentsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LayoutComponentsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Column 垂直布局示例
        ColumnExamples()

        // 2. Row 水平布局示例
        RowExamples()

        // 3. Box 叠加布局示例
        BoxExamples()

        // 4. Card 卡片容器示例
        CardExamples()
    }
}

/**
 * ==================== Column 垂直布局示例 ====================
 */
@Composable
fun ColumnExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. Column 垂直布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 基础 Column
            Text(
                text = "基础 Column:",
                style = MaterialTheme.typography.labelMedium
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8EAF6))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp, 30.dp)
                        .background(Color(0xFF3F51B5))
                )
                Box(
                    modifier = Modifier
                        .size(80.dp, 30.dp)
                        .background(Color(0xFF7986CB))
                )
                Box(
                    modifier = Modifier
                        .size(100.dp, 30.dp)
                        .background(Color(0xFFC5CAE9))
                )
            }

            // horizontalAlignment 参数
            Text(
                text = "horizontalAlignment (水平对齐):",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Start 对齐
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Start", style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(40.dp, 20.dp).background(Color(0xFF4CAF50)))
                    Box(modifier = Modifier.size(60.dp, 20.dp).background(Color(0xFF81C784)))
                }

                // Center 对齐
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Center", style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(40.dp, 20.dp).background(Color(0xFFFF9800)))
                    Box(modifier = Modifier.size(60.dp, 20.dp).background(Color(0xFFFFB74D)))
                }

                // End 对齐
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFCE4EC))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("End", style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(40.dp, 20.dp).background(Color(0xFFE91E63)))
                    Box(modifier = Modifier.size(60.dp, 20.dp).background(Color(0xFFF06292)))
                }
            }

            // verticalArrangement 参数
            Text(
                text = "verticalArrangement (垂直排列):",
                style = MaterialTheme.typography.labelMedium
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.size(50.dp, 25.dp).background(Color(0xFF2196F3)))
                Box(modifier = Modifier.size(50.dp, 25.dp).background(Color(0xFF64B5F6)))
                Box(modifier = Modifier.size(50.dp, 25.dp).background(Color(0xFF90CAF9)))
            }
        }
    }
}

/**
 * ==================== Row 水平布局示例 ====================
 */
@Composable
fun RowExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. Row 水平布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 基础 Row
            Text(
                text = "基础 Row:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3E5F5))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.size(30.dp, 60.dp).background(Color(0xFF9C27B0)))
                Box(modifier = Modifier.size(30.dp, 80.dp).background(Color(0xFFBA68C8)))
                Box(modifier = Modifier.size(30.dp, 100.dp).background(Color(0xFFE1BEE7)))
            }

            // verticalAlignment 参数
            Text(
                text = "verticalAlignment (垂直对齐):",
                style = MaterialTheme.typography.labelMedium
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top 对齐
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2F1))
                        .padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("Top", modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(30.dp, 20.dp).background(Color(0xFF009688)))
                    Box(modifier = Modifier.size(30.dp, 40.dp).background(Color(0xFF4DB6AC)))
                    Box(modifier = Modifier.size(30.dp, 60.dp).background(Color(0xFF80CBC4)))
                }

                // Center 对齐
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFBE9E7))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Center", modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(30.dp, 20.dp).background(Color(0xFFFF5722)))
                    Box(modifier = Modifier.size(30.dp, 40.dp).background(Color(0xFFFF8A65)))
                    Box(modifier = Modifier.size(30.dp, 60.dp).background(Color(0xFFFFAB91)))
                }

                // Bottom 对齐
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F8E9))
                        .padding(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("Bottom", modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(30.dp, 20.dp).background(Color(0xFF8BC34A)))
                    Box(modifier = Modifier.size(30.dp, 40.dp).background(Color(0xFFAED581)))
                    Box(modifier = Modifier.size(30.dp, 60.dp).background(Color(0xFFC5E1A5)))
                }
            }

            // weight 权重
            Text(
                text = "Modifier.weight() 权重:",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color(0xFFE53935))
                )
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxSize()
                        .background(Color(0xFFFB8C00))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color(0xFFFDD835))
                )
            }
            Text(
                text = "权重比例: 1 : 2 : 1",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

/**
 * ==================== Box 叠加布局示例 ====================
 */
@Composable
fun BoxExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. Box 叠加布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 基础叠加
            Text(
                text = "基础叠加 (后写的在上层):",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFBBDEFB))
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF2196F3))
                )
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .size(60.dp)
                        .background(Color(0xFF64B5F6))
                )
                Box(
                    modifier = Modifier
                        .padding(40.dp)
                        .size(40.dp)
                        .background(Color(0xFF90CAF9))
                )
            }

            // contentAlignment 参数
            Text(
                text = "contentAlignment (内容对齐):",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TopStart
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8EAF6)),
                    contentAlignment = Alignment.TopStart
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(30.dp)
                            .background(Color(0xFF3F51B5))
                    )
                    Text(
                        text = "TopStart",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Center
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFCE4EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(30.dp)
                            .background(Color(0xFFE91E63))
                    )
                    Text(
                        text = "Center",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // BottomEnd
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(30.dp)
                            .background(Color(0xFF009688))
                    )
                    Text(
                        text = "BottomEnd",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // propagateMinConstraints
            Text(
                text = "实际应用 - 叠加徽章:",
                style = MaterialTheme.typography.labelMedium
            )
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                // 底层图标
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                // 徽章
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Red)
                )
            }
        }
    }
}

/**
 * ==================== Card 卡片容器示例 ====================
 */
@Composable
fun CardExamples() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. Card 卡片容器",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 基础 Card
            Text(
                text = "基础 Card:",
                style = MaterialTheme.typography.labelMedium
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "卡片标题",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "这是卡片内容，Card 是一个带阴影和圆角的容器组件。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 不同 elevation
            Text(
                text = "不同阴影高度 (elevation):",
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("elevation=0", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("elevation=4", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("elevation=8", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // 点击效果 Card
            Text(
                text = "可点击 Card (使用 Modifier.clickable):",
                style = MaterialTheme.typography.labelMedium
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* 点击事件 */ },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.padding(12.dp))
                    Column {
                        Text(
                            text = "可点击卡片",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "点击查看详情",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutComponentsPreview() {
    MaterialTheme {
        LayoutComponentsScreen()
    }
}
