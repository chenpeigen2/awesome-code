package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.flow.FlowLiveDataActivity
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

/**
 * FlowLiveDataActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class FlowLiveDataActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(FlowLiveDataActivity::class.java).use { scenario ->
            // 验证按钮显示
            onView(withId(R.id.btnUpdateFlow))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnUpdateLiveData))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testUpdateFlowButton() {
        ActivityScenario.launch(FlowLiveDataActivity::class.java).use { scenario ->
            // 点击更新 Flow 按钮
            onView(withId(R.id.btnUpdateFlow)).perform(click())
            
            // 验证 StateFlow 文本更新
            onView(withId(R.id.tvStateFlow))
                .check(matches(withText(containsString("StateFlow: 1"))))
        }
    }

    @Test
    fun testUpdateLiveDataButton() {
        ActivityScenario.launch(FlowLiveDataActivity::class.java).use { scenario ->
            // 点击更新 LiveData 按钮
            onView(withId(R.id.btnUpdateLiveData)).perform(click())
            
            // 验证 LiveData 文本更新
            onView(withId(R.id.tvLiveData))
                .check(matches(withText(containsString("LiveData: 1"))))
        }
    }

    @Test
    fun testMultipleUpdates() {
        ActivityScenario.launch(FlowLiveDataActivity::class.java).use { scenario ->
            // 多次点击更新按钮
            onView(withId(R.id.btnUpdateFlow)).perform(click())
            onView(withId(R.id.btnUpdateFlow)).perform(click())
            onView(withId(R.id.btnUpdateLiveData)).perform(click())
            
            // 验证状态更新
            onView(withId(R.id.tvStateFlow))
                .check(matches(withText(containsString("StateFlow: 2"))))
            onView(withId(R.id.tvLiveData))
                .check(matches(withText(containsString("LiveData: 1"))))
        }
    }
}
