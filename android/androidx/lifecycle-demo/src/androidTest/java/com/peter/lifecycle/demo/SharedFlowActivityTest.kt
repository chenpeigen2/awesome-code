package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.flow.SharedFlowActivity
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

/**
 * SharedFlowActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class SharedFlowActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(SharedFlowActivity::class.java).use { scenario ->
            // 验证初始状态
            onView(withId(R.id.etEvent))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnSendEvent))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testSendEvent() {
        ActivityScenario.launch(SharedFlowActivity::class.java).use { scenario ->
            // 输入事件内容
            onView(withId(R.id.etEvent))
                .perform(typeText("测试事件"), closeSoftKeyboard())
            
            // 点击发送按钮
            onView(withId(R.id.btnSendEvent)).perform(click())
            
            // 验证日志显示
            onView(withId(R.id.tvLog))
                .check(matches(withText(containsString("测试事件"))))
        }
    }

    @Test
    fun testSendNotification() {
        ActivityScenario.launch(SharedFlowActivity::class.java).use { scenario ->
            // 点击发送通知按钮
            onView(withId(R.id.btnSendNotification)).perform(click())
            
            // 验证通知显示
            onView(withId(R.id.tvNotification))
                .check(matches(withText(containsString("通知:"))))
        }
    }

    @Test
    fun testInputClearAfterSend() {
        ActivityScenario.launch(SharedFlowActivity::class.java).use { scenario ->
            // 输入事件内容
            onView(withId(R.id.etEvent))
                .perform(typeText("测试清除"), closeSoftKeyboard())
            
            // 点击发送按钮
            onView(withId(R.id.btnSendEvent)).perform(click())
            
            // 验证输入框被清除
            onView(withId(R.id.etEvent))
                .check(matches(withText("")))
        }
    }
}
