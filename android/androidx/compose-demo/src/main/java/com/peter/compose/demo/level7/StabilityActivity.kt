package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * StabilityActivity - Compose 编译器稳定性优化
 *
 * 学习目标：
 * 1. 不稳定类型导致的不必要重组
 * 2. @Stable 注解的作用
 * 3. 稳定性对比
 * 4. 如何检查 skippability
 */
class StabilityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    StabilityScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ===== 不稳定类型示例 =====

class UnstableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>
)

@Stable
class StableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>
)

// ===== 计数器用于观察重组 =====

@Composable
fun RecompositionCounter(label: String) {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { count++ }
    Text(
        text = "$label 重组次数: $count",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun StabilityScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Compose 稳定性优化",
            style = MaterialTheme.typography.headlineMedium
        )

        UnstableClassDemo()
        StableAnnotationDemo()
        StabilityComparison()
        SkippabilityTips()
    }
}

@Composable
fun UnstableClassDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "不稳定类型 (Unstable)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "普通 class + List 参数 → 编译器无法跳过重组",
                style = MaterialTheme.typography.bodyMedium
            )

            var trigger by remember { mutableIntStateOf(0) }

            Button(onClick = { trigger++ }) {
                Text("触发父组件重组 ($trigger)")
            }

            val user = remember { UnstableUser("张三", 25, listOf("编程", "阅读")) }
            UnstableUserCard(user)

            Text(
                text = """class UnstableUser(  // 不稳定！
    val name: String,
    val age: Int,
    val hobbies: List<String>  // List 不可推断为不可变
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

@Composable
fun UnstableUserCard(user: UnstableUser) {
    RecompositionCounter("UnstableUserCard")
    Text("${user.name} · ${user.age}岁 · ${user.hobbies.joinToString()}")
}

@Composable
fun StableAnnotationDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "@Stable 注解",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "@Stable 告诉编译器：这个类的公开属性不会变化，可以跳过重组",
                style = MaterialTheme.typography.bodyMedium
            )

            var trigger by remember { mutableIntStateOf(0) }

            Button(onClick = { trigger++ }) {
                Text("触发父组件重组 ($trigger)")
            }

            val user = remember { StableUser("李四", 30, listOf("运动", "音乐")) }
            StableUserCard(user)

            Text(
                text = """@Stable
class StableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>  // 开发者承诺不会修改
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

@Composable
fun StableUserCard(user: StableUser) {
    RecompositionCounter("StableUserCard")
    Text("${user.name} · ${user.age}岁 · ${user.hobbies.joinToString()}")
}

@Composable
fun StabilityComparison() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "稳定性对比",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "对比观察：点击按钮后哪个组件重组了？",
                style = MaterialTheme.typography.bodyMedium
            )

            var trigger by remember { mutableIntStateOf(0) }

            Button(onClick = { trigger++ }) {
                Text("触发重组 ($trigger)")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("不稳定", style = MaterialTheme.typography.labelLarge)
                    val unstable = remember { UnstableUser("A", 20, listOf("X")) }
                    UnstableUserCard(unstable)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("@Stable", style = MaterialTheme.typography.labelLarge)
                    val stable = remember { StableUser("B", 25, listOf("Y")) }
                    StableUserCard(stable)
                }
            }
        }
    }
}

@Composable
fun SkippabilityTips() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "如何检查 Skippability",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = """1. 生成 Compose Compiler 报告：
   在 gradle.properties 添加：
   composeCompilerReports=true

2. 构建项目后查看报告：
   build/compose_compiler/report.txt

3. 报告中关注：
   - "skippable" = 可跳过（参数稳定）
   - "unstable" = 不稳定参数
   - "restartable" = 可重启

4. 最佳实践：
   - 使用 data class + 不可变类型
   - 使用 @Stable/@Immutable 注解
   - 避免在 Composable 参数中使用 var 属性
   - 使用 kotlinx.collections.immutable 替代 List""",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StabilityScreenPreview() {
    MaterialTheme {
        StabilityScreen()
    }
}
