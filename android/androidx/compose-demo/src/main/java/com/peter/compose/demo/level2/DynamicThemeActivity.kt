package com.peter.compose.demo.level2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * DynamicThemeActivity - 动态主题演示
 *
 * 学习目标：
 * 1. 理解 Material You 动态取色原理
 * 2. 掌握自定义 ColorScheme、Typography、Shape 系统
 * 3. 学会封装可复用的主题函数
 */
class DynamicThemeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 主屏幕使用默认主题
            MaterialTheme {
                Scaffold { innerPadding ->
                    DynamicThemeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// =====================================================
// 主屏幕组合
// =====================================================

@Composable
fun DynamicThemeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 标题说明
        Text(
            text = "动态主题系统",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "探索 Material 3 的主题定制能力：动态取色、自定义颜色、排版与形状。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 1. 动态取色 (Material You)
        DynamicColorSection()

        // 2. 自定义 ColorScheme
        CustomColorSchemeSection()

        // 3. 排版系统
        TypographySection()

        // 4. 形状系统
        ShapeSystemSection()

        // 5. 深色/浅色主题切换
        DarkLightThemeSection()

        // 6. 自定义主题函数封装
        CustomThemeFunctionSection()
    }
}

// =====================================================
// 通用组件
// =====================================================

/**
 * 区域标题组件
 * 用于分隔各个演示区域
 */
@Composable
fun SectionHeader(title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 色块展示组件
 * 用于展示 ColorScheme 中的单个颜色
 */
@Composable
fun ColorSwatch(
    colorName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = colorName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =====================================================
// 1. 动态取色 (Material You)
// =====================================================

/**
 * 动态取色演示
 *
 * Android 12+ (API 31) 支持 Material You 动态取色
 * 系统会从壁纸中提取颜色生成 ColorScheme
 * 使用 dynamicLightColorScheme / dynamicDarkColorScheme 获取
 */
@Composable
fun DynamicColorSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "1. 动态取色 (Material You)",
                description = "Android 12+ 从壁纸提取颜色，自动生成协调的配色方案。"
            )

            // 检查系统版本是否支持动态取色
            val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            var dynamicEnabled by remember { mutableStateOf(false) }

            if (supportsDynamic) {
                Text(
                    text = "当前设备支持动态取色 (Android ${Build.VERSION.SDK_INT})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 开关控制是否启用动态取色
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = dynamicEnabled,
                        onCheckedChange = { dynamicEnabled = it }
                    )
                    Text(
                        text = if (dynamicEnabled) "动态取色已开启" else "动态取色已关闭",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 展示动态取色的颜色色板
                val context = LocalContext.current
                val dynamicLightScheme = dynamicLightColorScheme(context)
                val defaultLightScheme = lightColorScheme()

                val displayScheme = if (dynamicEnabled) dynamicLightScheme else defaultLightScheme

                Text(
                    text = if (dynamicEnabled) "动态配色方案:" else "默认配色方案:",
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColorSwatch("Primary", displayScheme.primary)
                    ColorSwatch("Secondary", displayScheme.secondary)
                    ColorSwatch("Tertiary", displayScheme.tertiary)
                    ColorSwatch("Surface", displayScheme.surface)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColorSwatch("OnPrimary", displayScheme.onPrimary)
                    ColorSwatch("PrimaryContainer", displayScheme.primaryContainer)
                    ColorSwatch("SurfaceVariant", displayScheme.surfaceVariant)
                    ColorSwatch("Outline", displayScheme.outline)
                }
            } else {
                // 不支持动态取色的设备
                Text(
                    text = "当前设备不支持动态取色（需要 Android 12+，当前 API ${Build.VERSION.SDK_INT}）",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                // 即使不支持也展示默认色板
                val defaultScheme = lightColorScheme()
                Text(
                    text = "使用默认配色方案:",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColorSwatch("Primary", defaultScheme.primary)
                    ColorSwatch("Secondary", defaultScheme.secondary)
                    ColorSwatch("Tertiary", defaultScheme.tertiary)
                    ColorSwatch("Surface", defaultScheme.surface)
                }
            }
        }
    }
}

// =====================================================
// 2. 自定义 ColorScheme
// =====================================================

/**
 * 海洋蓝主题
 */
private val OceanBlueColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF0288D1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB3E5FC),
    tertiary = Color(0xFF00ACC1),
    surface = Color(0xFFE3F2FD),
    onSurface = Color(0xFF0D47A1),
    surfaceVariant = Color(0xFFBBDEFB),
    onSurfaceVariant = Color(0xFF1565C0),
)

/**
 * 森林绿主题
 */
private val ForestGreenColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF558B2F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEDC8),
    tertiary = Color(0xFF7CB342),
    surface = Color(0xFFE8F5E9),
    onSurface = Color(0xFF1B5E20),
    surfaceVariant = Color(0xFFC8E6C9),
    onSurfaceVariant = Color(0xFF2E7D32),
)

/**
 * 日落橙主题
 */
private val SunsetOrangeColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCCBC),
    onPrimaryContainer = Color(0xFFBF360C),
    secondary = Color(0xFFFF6D00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFFFFAB00),
    surface = Color(0xFFFBE9E7),
    onSurface = Color(0xFFBF360C),
    surfaceVariant = Color(0xFFFFCCBC),
    onSurfaceVariant = Color(0xFFE65100),
)

/**
 * 薰衣草紫主题
 */
private val LavenderPurpleColorScheme = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1BEE7),
    onPrimaryContainer = Color(0xFF4A148C),
    secondary = Color(0xFF9C27B0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),
    tertiary = Color(0xFFCE93D8),
    surface = Color(0xFFF3E5F5),
    onSurface = Color(0xFF4A148C),
    surfaceVariant = Color(0xFFE1BEE7),
    onSurfaceVariant = Color(0xFF7B1FA2),
)

/**
 * 预设主题数据类
 */
private data class PresetTheme(
    val name: String,
    val colorScheme: androidx.compose.material3.ColorScheme
)

/**
 * 自定义 ColorScheme 演示
 *
 * 使用 lightColorScheme() 创建自定义配色方案
 * 通过切换不同的预设主题来展示视觉效果差异
 */
@Composable
fun CustomColorSchemeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "2. 自定义 ColorScheme",
                description = "使用 lightColorScheme() 创建自定义配色方案，展示不同主题的视觉效果。"
            )

            // 预设主题列表
            val presetThemes = remember {
                listOf(
                    PresetTheme("海洋蓝", OceanBlueColorScheme),
                    PresetTheme("森林绿", ForestGreenColorScheme),
                    PresetTheme("日落橙", SunsetOrangeColorScheme),
                    PresetTheme("薰衣草紫", LavenderPurpleColorScheme),
                )
            }

            var selectedThemeIndex by remember { mutableStateOf(0) }

            Text(
                text = "选择预设主题:",
                style = MaterialTheme.typography.labelMedium
            )

            // 主题选择按钮行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetThemes.forEachIndexed { index, theme ->
                    Button(
                        onClick = { selectedThemeIndex = index },
                        colors = ButtonDefaults.buttonColors(
                            // 选中的按钮使用对应主题的 primary 色
                            containerColor = if (index == selectedThemeIndex) {
                                theme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (index == selectedThemeIndex) {
                                theme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(theme.name, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // 使用选中的主题包裹演示内容
            val selectedTheme = presetThemes[selectedThemeIndex]
            MaterialTheme(
                colorScheme = selectedTheme.colorScheme
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "当前主题: ${selectedTheme.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 展示主题颜色色板
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ColorSwatch("Primary", MaterialTheme.colorScheme.primary)
                            ColorSwatch("Secondary", MaterialTheme.colorScheme.secondary)
                            ColorSwatch("Tertiary", MaterialTheme.colorScheme.tertiary)
                        }

                        // 演示各种组件在不同主题下的效果
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { }) {
                                Text("按钮")
                            }
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("边框按钮")
                            }
                        }

                        Text(
                            text = "这是一段使用当前主题样式的文字内容。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// =====================================================
// 3. 排版系统 (Typography)
// =====================================================

/**
 * 自定义排版配置
 * 修改了部分字体大小和粗细
 */
private val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 60.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 68.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 30.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 26.sp
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
    ),
)

/**
 * 排版系统演示
 *
 * Typography 定义了文字的视觉样式：字号、行高、字重等
 * Material 3 提供了一套完整的文字样式层级
 */
@Composable
fun TypographySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "3. 排版系统 (Typography)",
                description = "Typography 定义文字的视觉层级：字号、行高、字重等。"
            )

            // 默认排版展示
            Text(
                text = "默认排版样式:",
                style = MaterialTheme.typography.labelMedium
            )

            TypographyShowcase(
                label = "默认",
                typography = Typography()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 自定义排版展示
            Text(
                text = "自定义排版样式（增大字号和行高）:",
                style = MaterialTheme.typography.labelMedium
            )

            // 使用自定义排版包裹展示内容
            MaterialTheme(
                typography = CustomTypography
            ) {
                TypographyShowcase(
                    label = "自定义",
                    typography = CustomTypography
                )
            }
        }
    }
}

/**
 * 排版样式展示组件
 * 逐个展示各层级的文字样式
 */
@Composable
private fun TypographyShowcase(
    label: String,
    typography: Typography
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "[$label 排版]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // displayLarge - 最大标题
            Text(
                text = "Display Large",
                style = typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // headlineMedium - 中号标题
            Text(
                text = "Headline Medium",
                style = typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // titleLarge - 大号标题
            Text(
                text = "Title Large",
                style = typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // bodyLarge - 正文大号
            Text(
                text = "Body Large - 这是正文内容样式，适用于段落文字展示。",
                style = typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // labelSmall - 标签小号
            Text(
                text = "Label Small - 用于标签和辅助说明",
                style = typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =====================================================
// 4. 形状系统 (Shape)
// =====================================================

/**
 * 自定义形状配置
 * small: 切角 4dp（类似 iOS 的裁切角效果）
 * medium: 圆角 12dp（较圆润）
 * large: 切角 24dp（大号切角效果）
 */
private val CustomShapes = Shapes(
    small = CutCornerShape(4.dp),
    medium = RoundedCornerShape(12.dp),
    large = CutCornerShape(24.dp),
)

/**
 * 形状系统演示
 *
 * Shapes 定义了组件的外观形状
 * 分为 small、medium、large 三个级别
 * 不同组件使用不同级别的形状
 */
@Composable
fun ShapeSystemSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "4. 形状系统 (Shape)",
                description = "Shapes 定义组件外观：small/medium/large 三个级别影响不同组件。"
            )

            // 并排对比：默认形状 vs 自定义形状
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 左侧：默认形状
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "默认形状",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 使用默认形状的 MaterialTheme
                    MaterialTheme(
                        shapes = Shapes()
                    ) {
                        ShapeDemoComponents()
                    }
                }

                // 右侧：自定义形状
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "自定义形状",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 使用自定义形状的 MaterialTheme
                    MaterialTheme(
                        shapes = CustomShapes
                    ) {
                        ShapeDemoComponents()
                    }
                }
            }

            // 形状参数说明
            Text(
                text = "自定义形状参数: small=CutCornerShape(4dp), medium=RoundedCornerShape(12dp), large=CutCornerShape(24dp)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 形状演示组件
 * 展示 Card、Button、TextField 在当前 Shape 系统下的外观
 */
@Composable
private fun ShapeDemoComponents() {
    // Card 使用 large 形状
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = "Card (large)",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }

    // Button 使用 medium 形状
    Button(
        onClick = { },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Button (medium)")
    }

    // TextField 使用 small 形状
    OutlinedTextField(
        value = "TextField (small)",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true
    )
}

// =====================================================
// 5. 深色/浅色主题切换
// =====================================================

/**
 * 深色/浅色主题切换演示
 *
 * 使用 isSystemInDarkTheme() 获取系统暗色模式设置
 * 通过 Switch 组件手动切换深色/浅色主题
 * 使用 MaterialTheme 包裹内容来应用不同的 ColorScheme
 */
@Composable
fun DarkLightThemeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "5. 深色/浅色主题切换",
                description = "根据 isSystemInDarkTheme() 判断当前系统模式，并支持手动切换。"
            )

            // 获取系统当前暗色模式状态作为默认值
            val systemDarkTheme = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemDarkTheme) }

            // 切换开关
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Switch(
                    checked = darkTheme,
                    onCheckedChange = { darkTheme = it }
                )
                Text(
                    text = if (darkTheme) "深色模式" else "浅色模式",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "(系统默认: ${if (systemDarkTheme) "深色" else "浅色"})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 使用对应主题包裹演示内容
            val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
            MaterialTheme(
                colorScheme = colorScheme
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "当前主题: ${if (darkTheme) "深色" else "浅色"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 展示各颜色在深/浅色主题下的表现
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ColorSwatch("Primary", MaterialTheme.colorScheme.primary)
                            ColorSwatch("Secondary", MaterialTheme.colorScheme.secondary)
                            ColorSwatch("Tertiary", MaterialTheme.colorScheme.tertiary)
                            ColorSwatch("Surface", MaterialTheme.colorScheme.surface)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { }) {
                                Text("填充按钮")
                            }
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("边框按钮")
                            }
                        }

                        Text(
                            text = "观察同一组件在深色和浅色主题下的颜色差异。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// =====================================================
// 6. 自定义主题函数封装
// =====================================================

/**
 * 自定义主题函数
 *
 * 将主题逻辑封装为可复用的 Composable 函数
 * 支持以下特性：
 * - 自动跟随系统深色模式
 * - Android 12+ 支持动态取色
 * - 可手动覆盖深色模式和动态取色设置
 *
 * @param darkTheme 是否使用深色主题，默认跟随系统
 * @param dynamicColor 是否启用动态取色，默认开启
 * @param content 主题包裹的内容
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // 优先使用动态取色（需要 Android 12+）
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // 深色模式使用暗色配色
        darkTheme -> darkColorScheme()
        // 默认使用浅色配色
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}

/**
 * 自定义主题函数演示
 *
 * 展示如何在实际应用中使用封装好的 AppTheme
 * 提供深色模式和动态取色的开关控制
 */
@Composable
fun CustomThemeFunctionSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "6. 自定义主题函数封装",
                description = "将主题逻辑封装为 AppTheme() 函数，支持深色模式、动态取色的统一管理。"
            )

            // 展示 AppTheme 的代码片段说明
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "AppTheme 封装逻辑:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "1. 优先检查动态取色（Android 12+）",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "2. 动态取色不可用时回退到深色/浅色方案",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "3. 将 colorScheme + typography + shapes 统一传入 MaterialTheme",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // 控制面板
            val systemDarkTheme = isSystemInDarkTheme()
            var useDarkTheme by remember { mutableStateOf(systemDarkTheme) }
            var useDynamicColor by remember { mutableStateOf(true) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Switch(
                        checked = useDarkTheme,
                        onCheckedChange = { useDarkTheme = it }
                    )
                    Text("深色", style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Switch(
                        checked = useDynamicColor,
                        onCheckedChange = { useDynamicColor = it },
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    )
                    Text("动态取色", style = MaterialTheme.typography.bodySmall)
                }
            }

            // 使用封装的 AppTheme 展示效果
            AppTheme(
                darkTheme = useDarkTheme,
                dynamicColor = useDynamicColor
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "AppTheme 演示",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 颜色色板
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ColorSwatch("Primary", MaterialTheme.colorScheme.primary)
                            ColorSwatch("Secondary", MaterialTheme.colorScheme.secondary)
                            ColorSwatch("Tertiary", MaterialTheme.colorScheme.tertiary)
                        }

                        // 组件演示
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { }) {
                                Text("按钮")
                            }
                            FloatingActionButton(
                                onClick = { },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text("+", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        Text(
                            text = "深色: ${if (useDarkTheme) "开" else "关"} | 动态取色: ${if (useDynamicColor) "开" else "关"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// =====================================================
// 预览
// =====================================================

@Preview(showBackground = true, name = "动态主题演示")
@Composable
fun DynamicThemePreview() {
    MaterialTheme {
        DynamicThemeScreen()
    }
}
