package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ComposeTestingActivity - Compose 测试指南与演示
 *
 * 学习目标：
 * 1. 理解 Compose 测试框架的核心概念（语义树、查找器、断言、操作）
 * 2. 了解 testTag 的使用方式
 * 3. 通过计数器和待办列表 Demo 展示可测试的 UI
 * 4. 展示对应的测试代码
 * 5. 学习如何运行 Compose 测试
 */
class ComposeTestingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    ComposeTestingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ========== 主屏幕 ==========

@Composable
fun ComposeTestingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 页面标题
        Text(
            text = "Compose Testing",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "学习如何使用 Compose 测试框架来验证 UI 行为",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 1. 测试概述
        TestingOverviewSection()

        // 2. 计数器 Demo（可测试的 UI）
        CounterDemoSection()

        // 3. 待办列表 Demo（可测试的 UI）
        TodoDemoSection()

        // 4. 测试代码展示
        TestCodeShowcaseSection()

        // 5. 运行测试说明
        RunTestsSection()
    }
}

// ========== 1. 测试概述 ==========

@Composable
fun TestingOverviewSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "1. 测试概述",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 语义树
            ConceptCard(
                title = "语义树 (Semantics Tree)",
                description = "Compose 会为每个 UI 组件生成一棵语义树，测试框架通过语义树来查找和操作节点。" +
                        "使用 Modifier.semantics {} 可以自定义语义信息，Modifier.testTag() 则是最常用的语义标记。",
                color = Color(0xFF4CAF50)
            )

            // 查找器
            ConceptCard(
                title = "查找器 (Finders)",
                description = "onNodeWithText(\"文本\") - 按文本查找\n" +
                        "onNodeWithTag(\"tag\") - 按 testTag 查找\n" +
                        "onNodeWithContentDescription(\"描述\") - 按内容描述查找\n" +
                        "onAllNodesWithTag(\"tag\") - 查找所有匹配节点",
                color = Color(0xFF2196F3)
            )

            // 断言
            ConceptCard(
                title = "断言 (Assertions)",
                description = "assertIsDisplayed() - 验证节点可见\n" +
                        "assertTextEquals(\"文本\") - 验证文本内容\n" +
                        "assertIsEnabled() / assertIsNotEnabled() - 验证启用状态\n" +
                        "assertExists() / assertDoesNotExist() - 验证存在性",
                color = Color(0xFFFF9800)
            )

            // 操作
            ConceptCard(
                title = "操作 (Actions)",
                description = "performClick() - 执行点击\n" +
                        "performTextInput(\"文本\") - 输入文本\n" +
                        "performScrollTo() - 滚动到节点\n" +
                        "performTouchInput { ... } - 自定义触摸手势",
                color = Color(0xFF9C27B0)
            )
        }
    }
}

/**
 * 概念说明卡片
 */
@Composable
fun ConceptCard(title: String, description: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 色块标记
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            lineHeight = 18.sp
        )
    }
}

// ========== 2. 计数器 Demo ==========

@Composable
fun CounterDemoSection() {
    var count by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 计数器 Demo (Counter)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "这个计数器的每个组件都标记了 testTag，方便测试框架查找和操作。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 计数器 UI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 计数值显示 - testTag: counter_value
                Text(
                    text = "Count: $count",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("counter_value")
                )

                // 操作按钮行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 减少按钮 - testTag: decrement_btn
                    Button(
                        onClick = { count-- },
                        modifier = Modifier.testTag("decrement_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(text = "-", fontSize = 20.sp)
                    }

                    // 重置按钮 - testTag: reset_btn
                    Button(
                        onClick = { count = 0 },
                        modifier = Modifier.testTag("reset_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(text = "Reset")
                    }

                    // 增加按钮 - testTag: increment_btn
                    Button(
                        onClick = { count++ },
                        modifier = Modifier.testTag("increment_btn")
                    ) {
                        Text(text = "+", fontSize = 20.sp)
                    }
                }

                // 显示 testTag 信息
                Text(
                    text = "testTag: counter_value | increment_btn | decrement_btn | reset_btn",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ========== 3. 待办列表 Demo ==========

@Composable
fun TodoDemoSection() {
    var todos by remember { mutableStateOf(listOf<String>()) }
    var inputText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 待办列表 Demo (Todo)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "简单的待办列表，支持添加和删除操作。每个组件都有 testTag 标记。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 输入区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 输入框 - testTag: todo_input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("输入待办事项") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("todo_input"),
                    singleLine = true
                )

                // 添加按钮 - testTag: add_btn
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            todos = todos + inputText
                            inputText = ""
                        }
                    },
                    modifier = Modifier.testTag("add_btn")
                ) {
                    Text("Add")
                }
            }

            // 待办列表 - 使用固定高度显示，配合滚动
            if (todos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                ) {
                    items(todos) { todo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // 待办文本 - testTag: todo_item_text
                            Text(
                                text = todo,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("todo_item_text")
                            )
                            // 删除按钮 - testTag: todo_delete_btn
                            Button(
                                onClick = { todos = todos.filterNot { it == todo } },
                                modifier = Modifier.testTag("todo_delete_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("删除", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                // 空状态提示
                Text(
                    text = "暂无待办事项，请添加",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            // testTag 信息
            Text(
                text = "testTag: todo_input | add_btn | todo_item_text | todo_delete_btn",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========== 4. 测试代码展示 ==========

@Composable
fun TestCodeShowcaseSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "4. 测试代码展示",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "以下是对应上面 Demo 的测试代码，展示了 Compose 测试的核心用法：",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 测试代码卡片 1：计数器增加
            CodeCard(
                title = "测试计数器增加",
                code = "@Test\n" +
                        "fun testCounterIncrement() {\n" +
                        "    // 验证初始值\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"counter_value\")\n" +
                        "        .assertTextEquals(\"Count: 0\")\n\n" +
                        "    // 点击增加按钮\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"increment_btn\")\n" +
                        "        .performClick()\n\n" +
                        "    // 验证值已增加\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"counter_value\")\n" +
                        "        .assertTextEquals(\"Count: 1\")\n" +
                        "}"
            )

            // 测试代码卡片 2：计数器减少
            CodeCard(
                title = "测试计数器减少",
                code = "@Test\n" +
                        "fun testCounterDecrement() {\n" +
                        "    // 点击增加按钮两次\n" +
                        "    repeat(2) {\n" +
                        "        composeTestRule\n" +
                        "            .onNodeWithTag(\"increment_btn\")\n" +
                        "            .performClick()\n" +
                        "    }\n\n" +
                        "    // 验证值为 2\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"counter_value\")\n" +
                        "        .assertTextEquals(\"Count: 2\")\n\n" +
                        "    // 点击减少按钮\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"decrement_btn\")\n" +
                        "        .performClick()\n\n" +
                        "    // 验证值变为 1\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"counter_value\")\n" +
                        "        .assertTextEquals(\"Count: 1\")\n" +
                        "}"
            )

            // 测试代码卡片 3：计数器重置
            CodeCard(
                title = "测试计数器重置",
                code = "@Test\n" +
                        "fun testCounterReset() {\n" +
                        "    // 先增加几次\n" +
                        "    repeat(5) {\n" +
                        "        composeTestRule\n" +
                        "            .onNodeWithTag(\"increment_btn\")\n" +
                        "            .performClick()\n" +
                        "    }\n\n" +
                        "    // 验证值为 5\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"counter_value\")\n" +
                        "        .assertTextEquals(\"Count: 5\")\n\n" +
                        "    // 点击重置按钮\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"reset_btn\")\n" +
                        "        .performClick()\n\n" +
                        "    // 验证值回到 0\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"counter_value\")\n" +
                        "        .assertTextEquals(\"Count: 0\")\n" +
                        "}"
            )

            // 测试代码卡片 4：待办添加
            CodeCard(
                title = "测试待办添加",
                code = "@Test\n" +
                        "fun testTodoAdd() {\n" +
                        "    // 输入文本\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"todo_input\")\n" +
                        "        .performTextInput(\"Buy milk\")\n\n" +
                        "    // 点击添加\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"add_btn\")\n" +
                        "        .performClick()\n\n" +
                        "    // 验证项目已添加\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithText(\"Buy milk\")\n" +
                        "        .assertIsDisplayed()\n" +
                        "}"
            )

            // 测试代码卡片 5：待办删除
            CodeCard(
                title = "测试待办删除",
                code = "@Test\n" +
                        "fun testTodoDelete() {\n" +
                        "    // 添加一个待办项\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"todo_input\")\n" +
                        "        .performTextInput(\"Task 1\")\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"add_btn\")\n" +
                        "        .performClick()\n\n" +
                        "    // 验证项目存在\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithText(\"Task 1\")\n" +
                        "        .assertIsDisplayed()\n\n" +
                        "    // 点击删除\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithTag(\"todo_delete_btn\")\n" +
                        "        .performClick()\n\n" +
                        "    // 验证项目已删除\n" +
                        "    composeTestRule\n" +
                        "        .onNodeWithText(\"Task 1\")\n" +
                        "        .assertDoesNotExist()\n" +
                        "}"
            )
        }
    }
}

/**
 * 代码展示卡片（深色背景，模拟代码编辑器）
 */
@Composable
fun CodeCard(title: String, code: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 标题
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))

        // 代码区域 - 深色背景模拟代码编辑器
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E1E2E))
                .padding(12.dp)
        ) {
            // 文件标签
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF89B4FA))) {
                        append("@Test")
                    }
                },
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 代码内容
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFCDD6F4),
                lineHeight = 16.sp,
                fontSize = 10.sp
            )
        }
    }
}

// ========== 5. 运行测试说明 ==========

@Composable
fun RunTestsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. 运行测试",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Compose 测试属于 Android Instrumented Test，需要在设备或模拟器上运行。" +
                        "测试类使用 createComposeRule() 创建测试规则，可以直接设置 Compose 内容进行隔离测试。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            // 测试命令
            Text(
                text = "运行命令：",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // 命令展示
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1E2E))
                    .padding(12.dp)
            ) {
                Text(
                    text = "# 运行所有 instrumented 测试\n" +
                            "./gradlew :compose-demo:connectedAndroidTest\n\n" +
                            "# 运行单个测试类\n" +
                            "./gradlew :compose-demo:connectedAndroidTest \\\n" +
                            "  --tests \"com.peter.compose.demo.ComposeTestingDemoTest\"\n\n" +
                            "# 运行单个测试方法\n" +
                            "./gradlew :compose-demo:connectedAndroidTest \\\n" +
                            "  --tests \"*.ComposeTestingDemoTest.testCounterIncrement\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFA6E3A1),
                    lineHeight = 18.sp,
                    fontSize = 10.sp
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            // 测试类信息
            Text(
                text = "测试类信息：",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1E2E))
                    .padding(12.dp)
            ) {
                Text(
                    text = "// 测试文件位置\n" +
                            "compose-demo/src/androidTest/java/\n" +
                            "  com/peter/compose/demo/\n" +
                            "    ComposeTestingDemoTest.kt\n\n" +
                            "// 测试类名\n" +
                            "class ComposeTestingDemoTest\n\n" +
                            "// 包含的测试方法\n" +
                            "- testCounterIncrement()  // 测试计数增加\n" +
                            "- testCounterDecrement()  // 测试计数减少\n" +
                            "- testCounterReset()      // 测试计数重置\n" +
                            "- testTodoAdd()           // 测试添加待办\n" +
                            "- testTodoDelete()        // 测试删除待办\n" +
                            "- testNodeWithText()      // 测试文本查找\n" +
                            "- testSemantics()         // 测试语义信息\n" +
                            "- testAssertions()        // 测试断言功能",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFCDD6F4),
                    lineHeight = 18.sp,
                    fontSize = 10.sp
                )
            }

            // 测试说明
            Text(
                text = "注意：这些测试使用 createComposeRule() 创建独立的 Compose 环境，" +
                        "不需要启动 Activity，测试速度更快且更加隔离。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}
