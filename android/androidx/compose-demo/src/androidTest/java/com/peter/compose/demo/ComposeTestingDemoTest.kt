package com.peter.compose.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Compose Testing 示例测试
 *
 * 演示 Compose 测试框架的核心功能：
 * 1. 查找节点 (Finders) - onNodeWithTag, onNodeWithText
 * 2. 执行操作 (Actions) - performClick, performTextInput
 * 3. 断言验证 (Assertions) - assertIsDisplayed, assertTextEquals, assertIsEnabled
 *
 * 运行方式：
 * ./gradlew :compose-demo:connectedAndroidTest
 * ./gradlew :compose-demo:connectedAndroidTest --tests "*.ComposeTestingDemoTest"
 */
class ComposeTestingDemoTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ========== 计数器测试 ==========

    /**
     * 测试计数器增加功能
     * 验证：点击 increment_btn 后，counter_value 文本从 "Count: 0" 变为 "Count: 1"
     */
    @Test
    fun testCounterIncrement() {
        var count by mutableIntStateOf(0)
        composeTestRule.setContent {
            Column {
                Text(
                    text = "Count: $count",
                    modifier = Modifier.testTag("counter_value")
                )
                Button(
                    onClick = { count++ },
                    modifier = Modifier.testTag("increment_btn")
                ) {
                    Text("+")
                }
            }
        }

        // 验证初始状态
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 0")

        // 点击增加按钮
        composeTestRule.onNodeWithTag("increment_btn").performClick()

        // 验证增加后的值
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 1")

        // 再次点击
        composeTestRule.onNodeWithTag("increment_btn").performClick()

        // 验证连续增加
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 2")
    }

    /**
     * 测试计数器减少功能
     * 验证：先增加再减少，确认值正确变化
     */
    @Test
    fun testCounterDecrement() {
        var count by mutableIntStateOf(0)
        composeTestRule.setContent {
            Column {
                Text(
                    text = "Count: $count",
                    modifier = Modifier.testTag("counter_value")
                )
                Button(
                    onClick = { count++ },
                    modifier = Modifier.testTag("increment_btn")
                ) {
                    Text("+")
                }
                Button(
                    onClick = { count-- },
                    modifier = Modifier.testTag("decrement_btn")
                ) {
                    Text("-")
                }
            }
        }

        // 先增加两次
        composeTestRule.onNodeWithTag("increment_btn").performClick()
        composeTestRule.onNodeWithTag("increment_btn").performClick()
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 2")

        // 减少一次
        composeTestRule.onNodeWithTag("decrement_btn").performClick()

        // 验证减少后的值
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 1")
    }

    /**
     * 测试计数器重置功能
     * 验证：增加后点击重置，值回到 0
     */
    @Test
    fun testCounterReset() {
        var count by mutableIntStateOf(0)
        composeTestRule.setContent {
            Column {
                Text(
                    text = "Count: $count",
                    modifier = Modifier.testTag("counter_value")
                )
                Button(
                    onClick = { count++ },
                    modifier = Modifier.testTag("increment_btn")
                ) {
                    Text("+")
                }
                Button(
                    onClick = { count = 0 },
                    modifier = Modifier.testTag("reset_btn")
                ) {
                    Text("Reset")
                }
            }
        }

        // 增加多次
        repeat(5) {
            composeTestRule.onNodeWithTag("increment_btn").performClick()
        }
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 5")

        // 点击重置
        composeTestRule.onNodeWithTag("reset_btn").performClick()

        // 验证回到初始值
        composeTestRule.onNodeWithTag("counter_value")
            .assertTextEquals("Count: 0")
    }

    // ========== 待办列表测试 ==========

    /**
     * 测试添加待办事项
     * 验证：输入文本后点击添加，新项目显示在列表中
     */
    @Test
    fun testTodoAdd() {
        var todos by mutableStateOf(listOf<String>())
        var inputText by mutableStateOf("")
        composeTestRule.setContent {
            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.testTag("todo_input"),
                    label = { Text("输入") }
                )
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
                todos.forEach { todo ->
                    Text(
                        text = todo,
                        modifier = Modifier.testTag("todo_item")
                    )
                }
            }
        }

        // 输入待办文本
        composeTestRule.onNodeWithTag("todo_input")
            .performTextInput("Buy milk")

        // 点击添加
        composeTestRule.onNodeWithTag("add_btn").performClick()

        // 验证项目已显示
        composeTestRule.onNodeWithText("Buy milk").assertIsDisplayed()

        // 添加第二个待办
        composeTestRule.onNodeWithTag("todo_input")
            .performTextInput("Walk dog")
        composeTestRule.onNodeWithTag("add_btn").performClick()

        // 验证两个项目都显示
        composeTestRule.onNodeWithText("Buy milk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Walk dog").assertIsDisplayed()
    }

    /**
     * 测试删除待办事项
     * 验证：添加后点击删除，项目从列表中移除
     */
    @Test
    fun testTodoDelete() {
        var todos by mutableStateOf(listOf<String>())
        var inputText by mutableStateOf("")
        composeTestRule.setContent {
            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.testTag("todo_input"),
                    label = { Text("输入") }
                )
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
                todos.forEachIndexed { index, todo ->
                    Row {
                        Text(
                            text = todo,
                            modifier = Modifier.testTag("todo_item")
                        )
                        Button(
                            onClick = { todos = todos.toMutableList().apply { removeAt(index) } },
                            modifier = Modifier.testTag("todo_delete_btn")
                        ) {
                            Text("删除")
                        }
                    }
                }
            }
        }

        // 添加待办项
        composeTestRule.onNodeWithTag("todo_input")
            .performTextInput("Task 1")
        composeTestRule.onNodeWithTag("add_btn").performClick()

        // 验证项目存在
        composeTestRule.onNodeWithText("Task 1").assertIsDisplayed()

        // 点击删除按钮
        composeTestRule.onNodeWithTag("todo_delete_btn").performClick()

        // 验证项目已删除
        composeTestRule.onNodeWithText("Task 1").assertDoesNotExist()
    }

    // ========== 查找器与断言测试 ==========

    /**
     * 测试通过文本查找节点
     * 验证：onNodeWithText 能正确找到包含指定文本的节点
     */
    @Test
    fun testNodeWithText() {
        composeTestRule.setContent {
            Column {
                Text("Hello Compose")
                Text("Hello Testing")
                Text("Hello World")
            }
        }

        // 通过文本查找节点
        composeTestRule.onNodeWithText("Hello Compose").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello Testing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()

        // 验证不存在的文本
        composeTestRule.onNodeWithText("Not Exist").assertDoesNotExist()

        // 使用 substring 匹配（默认忽略大小写为 false）
        composeTestRule.onNodeWithText("Hello", substring = true).assertIsDisplayed()
    }

    /**
     * 测试语义信息 (Semantics)
     * 验证：testTag 正确设置在语义树中，可以通过 tag 查找节点
     */
    @Test
    fun testSemantics() {
        composeTestRule.setContent {
            Column {
                Text(
                    text = "Label Text",
                    modifier = Modifier.testTag("label_tag")
                )
                Button(
                    onClick = {},
                    modifier = Modifier.testTag("button_tag")
                ) {
                    Text("Click Me")
                }
            }
        }

        // 通过 testTag 查找并验证文本
        composeTestRule.onNodeWithTag("label_tag")
            .assertTextEquals("Label Text")

        // 通过 testTag 查找按钮
        composeTestRule.onNodeWithTag("button_tag")
            .assertIsDisplayed()
            .assertIsEnabled()

        // 通过按钮中的文本查找
        composeTestRule.onNodeWithText("Click Me")
            .assertIsDisplayed()
    }

    /**
     * 测试断言功能
     * 验证：各种断言方法的正确使用
     */
    @Test
    fun testAssertions() {
        composeTestRule.setContent {
            Column {
                Text(
                    text = "Displayed Text",
                    modifier = Modifier.testTag("displayed_tag")
                )
                Button(
                    onClick = {},
                    modifier = Modifier.testTag("enabled_btn")
                ) {
                    Text("Enabled Button")
                }
            }
        }

        // assertIsDisplayed - 验证节点可见
        composeTestRule.onNodeWithTag("displayed_tag").assertIsDisplayed()

        // assertTextEquals - 验证文本内容完全匹配
        composeTestRule.onNodeWithTag("displayed_tag")
            .assertTextEquals("Displayed Text")

        // assertIsEnabled - 验证按钮可用
        composeTestRule.onNodeWithTag("enabled_btn").assertIsEnabled()

        // assertDoesNotExist - 验证节点不存在
        composeTestRule.onNodeWithTag("not_exist_tag").assertDoesNotExist()

        // assertCountEquals - 验证匹配节点的数量
        // （这里通过 onAllNodesWithTag 使用，但展示断言思路）
        composeTestRule.onNodeWithText("Displayed Text").assertIsDisplayed()
    }
}
