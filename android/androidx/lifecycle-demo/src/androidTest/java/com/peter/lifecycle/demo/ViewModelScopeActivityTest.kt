package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.advanced.ViewModelScopeActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ViewModelScopeActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class ViewModelScopeActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(ViewModelScopeActivity::class.java).use { scenario ->
            // 验证按钮显示
            onView(withId(R.id.btnFetchData))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnParallelFetch))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnCountdown))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testFetchDataButton() {
        ActivityScenario.launch(ViewModelScopeActivity::class.java).use { scenario ->
            // 点击获取数据按钮
            onView(withId(R.id.btnFetchData)).perform(click())
            
            // 验证进度条显示（异步，可能需要等待）
            // 这里只验证按钮可点击
            onView(withId(R.id.btnFetchData))
                .check(matches(isEnabled()))
        }
    }

    @Test
    fun testParallelFetchButton() {
        ActivityScenario.launch(ViewModelScopeActivity::class.java).use { scenario ->
            // 点击并行获取按钮
            onView(withId(R.id.btnParallelFetch)).perform(click())
            
            // 验证按钮可点击
            onView(withId(R.id.btnParallelFetch))
                .check(matches(isEnabled()))
        }
    }

    @Test
    fun testCountdownButton() {
        ActivityScenario.launch(ViewModelScopeActivity::class.java).use { scenario ->
            // 点击倒计时按钮
            onView(withId(R.id.btnCountdown)).perform(click())
            
            // 验证按钮可点击
            onView(withId(R.id.btnCountdown))
                .check(matches(isEnabled()))
        }
    }

    @Test
    fun testInitialResultText() {
        ActivityScenario.launch(ViewModelScopeActivity::class.java).use { scenario ->
            // 验证初始结果文本
            onView(withId(R.id.tvResult))
                .check(matches(withText("结果: ")))
        }
    }

    @Test
    fun testInitialCountdownText() {
        ActivityScenario.launch(ViewModelScopeActivity::class.java).use { scenario ->
            // 验证初始倒计时文本
            onView(withId(R.id.tvCountdown))
                .check(matches(withText("倒计时: 0")))
        }
    }
}
