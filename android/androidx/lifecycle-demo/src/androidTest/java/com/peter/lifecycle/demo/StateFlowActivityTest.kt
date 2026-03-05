package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.flow.StateFlowActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * StateFlowActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class StateFlowActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(StateFlowActivity::class.java).use { scenario ->
            // 验证初始状态
            onView(withId(R.id.tvCounter))
                .check(matches(withText("计数: 0")))
            onView(withId(R.id.tvTimer))
                .check(matches(withText("计时器: 0s")))
        }
    }

    @Test
    fun testIncrementButton() {
        ActivityScenario.launch(StateFlowActivity::class.java).use { scenario ->
            // 点击增加按钮
            onView(withId(R.id.btnIncrement)).perform(click())
            
            // 验证计数增加
            onView(withId(R.id.tvCounter))
                .check(matches(withText("计数: 1")))
        }
    }

    @Test
    fun testDecrementButton() {
        ActivityScenario.launch(StateFlowActivity::class.java).use { scenario ->
            // 点击减少按钮
            onView(withId(R.id.btnDecrement)).perform(click())
            
            // 验证计数减少
            onView(withId(R.id.tvCounter))
                .check(matches(withText("计数: -1")))
        }
    }

    @Test
    fun testMultipleIncrements() {
        ActivityScenario.launch(StateFlowActivity::class.java).use { scenario ->
            // 点击多次增加按钮
            onView(withId(R.id.btnIncrement)).perform(click())
            onView(withId(R.id.btnIncrement)).perform(click())
            onView(withId(R.id.btnIncrement)).perform(click())
            
            // 验证计数
            onView(withId(R.id.tvCounter))
                .check(matches(withText("计数: 3")))
        }
    }

    @Test
    fun testStartTimerButton() {
        ActivityScenario.launch(StateFlowActivity::class.java).use { scenario ->
            // 点击开始计时器按钮
            onView(withId(R.id.btnStartTimer)).perform(click())
            
            // 验证按钮可点击
            onView(withId(R.id.btnStartTimer))
                .check(matches(isEnabled()))
        }
    }

    @Test
    fun testStopTimerButton() {
        ActivityScenario.launch(StateFlowActivity::class.java).use { scenario ->
            // 点击开始然后停止
            onView(withId(R.id.btnStartTimer)).perform(click())
            onView(withId(R.id.btnStopTimer)).perform(click())
            
            // 验证按钮可点击
            onView(withId(R.id.btnStopTimer))
                .check(matches(isEnabled()))
        }
    }
}
