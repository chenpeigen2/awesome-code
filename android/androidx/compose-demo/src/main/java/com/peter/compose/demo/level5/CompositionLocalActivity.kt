package com.peter.compose.demo.level5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * CompositionLocalActivity - CompositionLocal
 *
 * 学习目标：
 * 1. compositionLocalOf 创建局部作用域数据
 * 2. CompositionLocalProvider 提供数据
 * 3. 实践：主题切换
 */

// ========== 定义 CompositionLocal ==========

/**
 * 自定义颜色配置
 */
data class AppColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val text: Color
)

// 浅色主题
val LightColors = AppColors(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFF5F5F5),
    text = Color(0xFF000000)
)

// 深色主题
val DarkColors = AppColors(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF121212),
    text = Color(0xFFFFFFFF)
)

// 创建 CompositionLocal
val LocalAppColors = compositionLocalOf { LightColors }

/**
 * 用户配置
 */
data class UserConfig(
    val userName: String,
    val userLevel: Int
)

val LocalUserConfig = compositionLocalOf {
    UserConfig(userName = "Guest", userLevel = 0)
}

// ========== Activity ==========

class CompositionLocalActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    CompositionLocalScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CompositionLocalScreen(modifier: Modifier = Modifier) {
    var isDarkMode by remember { mutableStateOf(false) }

    // 提供主题颜色
    CompositionLocalProvider(
        LocalAppColors provides if (isDarkMode) DarkColors else LightColors
    ) {
        val colors = LocalAppColors.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(colors.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. CompositionLocal 基础
            CompositionLocalBasics()

            // 2. 主题切换
            ThemeSwitcher(
                isDarkMode = isDarkMode,
                onToggle = { isDarkMode = !isDarkMode }
            )

            // 3. 用户配置示例
            UserConfigExample()

            // 4. 嵌套 CompositionLocal
            NestedCompositionLocalExample()
        }
    }
}

@Composable
fun CompositionLocalBasics() {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. CompositionLocal 基础",
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary
            )

            Text(
                text = "CompositionLocal 用于在组件树中隐式传递数据，无需逐层传递参数。",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.text
            )

            Text(
                text = "当前主题颜色:",
                style = MaterialTheme.typography.labelMedium,
                color = colors.text
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.primary)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.secondary)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.background)
                )
            }

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelMedium,
                color = colors.text
            )

            Text(
                text = """// 定义
val LocalAppColors = compositionLocalOf { LightColors }

// 提供
CompositionLocalProvider(
    LocalAppColors provides darkColors
) {
    // 子组件可访问
    val colors = LocalAppColors.current
}""",
                style = MaterialTheme.typography.bodySmall,
                color = colors.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ThemeSwitcher(
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 主题切换",
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary
            )

            Text(
                text = "通过 CompositionLocalProvider 切换主题。",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.text
            )

            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isDarkMode) "切换到浅色模式" else "切换到深色模式")
            }

            // 演示不同主题下的 UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "主色",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "次色",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Text(
                text = "当前模式: ${if (isDarkMode) "深色" else "浅色"}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.text
            )
        }
    }
}

@Composable
fun UserConfigExample() {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 用户配置示例",
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary
            )

            Text(
                text = "通过 CompositionLocal 传递用户配置信息。",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.text
            )

            // 提供用户配置
            CompositionLocalProvider(
                LocalUserConfig provides UserConfig(
                    userName = "张三",
                    userLevel = 5
                )
            ) {
                UserInfoDisplay()
                UserLevelBadge()
            }
        }
    }
}

@Composable
fun UserInfoDisplay() {
    val colors = LocalAppColors.current
    val userConfig = LocalUserConfig.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.primary.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userConfig.userName.first().toString(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Column {
            Text(
                text = userConfig.userName,
                style = MaterialTheme.typography.titleSmall,
                color = colors.text
            )
            Text(
                text = "等级: Lv.${userConfig.userLevel}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.text.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun UserLevelBadge() {
    val colors = LocalAppColors.current
    val userConfig = LocalUserConfig.current

    val levelColor = when {
        userConfig.userLevel >= 10 -> Color(0xFFFFD700)
        userConfig.userLevel >= 5 -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(levelColor)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Lv.${userConfig.userLevel} ${userConfig.userName}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun NestedCompositionLocalExample() {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 嵌套 CompositionLocal",
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary
            )

            Text(
                text = "内层 CompositionLocalProvider 可以覆盖外层的值。",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.text
            )

            // 外层值
            Text(
                text = "外层颜色:",
                style = MaterialTheme.typography.labelMedium,
                color = colors.text
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.primary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 内层覆盖
            CompositionLocalProvider(
                LocalAppColors provides AppColors(
                    primary = Color(0xFFFF5722),
                    secondary = Color(0xFFFFEB3B),
                    background = Color(0xFFFFF3E0),
                    text = Color(0xFFBF360C)
                )
            ) {
                val innerColors = LocalAppColors.current

                Text(
                    text = "内层覆盖的颜色:",
                    style = MaterialTheme.typography.labelMedium,
                    color = innerColors.text
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(innerColors.primary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 恢复外层值
            Text(
                text = "回到外层:",
                style = MaterialTheme.typography.labelMedium,
                color = colors.text
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompositionLocalPreview() {
    MaterialTheme {
        CompositionLocalProvider(
            LocalAppColors provides LightColors
        ) {
            CompositionLocalScreen()
        }
    }
}
