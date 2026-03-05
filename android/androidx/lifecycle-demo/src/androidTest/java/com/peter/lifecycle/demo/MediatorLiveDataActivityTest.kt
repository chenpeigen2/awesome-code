package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.livedata.MediatorLiveDataActivity
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

/**
 * MediatorLiveDataActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class MediatorLiveDataActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(MediatorLiveDataActivity::class.java).use { scenario ->
            // 验证输入框显示
            onView(withId(R.id.etMinPrice))
                .check(matches(isDisplayed()))
            onView(withId(R.id.etMaxPrice))
                .check(matches(isDisplayed()))
            onView(withId(R.id.etUsername))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testPriceRangeInput() {
        ActivityScenario.launch(MediatorLiveDataActivity::class.java).use { scenario ->
            // 输入价格区间
            onView(withId(R.id.etMinPrice))
                .perform(typeText("100"), closeSoftKeyboard())
            onView(withId(R.id.etMaxPrice))
                .perform(typeText("500"), closeSoftKeyboard())
            
            // 验证筛选结果显示
            onView(withId(R.id.tvFilterResult))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testCategorySelection() {
        ActivityScenario.launch(MediatorLiveDataActivity::class.java).use { scenario ->
            // 选择类别
            onView(withId(R.id.rbFood)).perform(click())
            
            // 验证筛选结果更新
            onView(withId(R.id.tvFilterResult))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testFormValidation() {
        ActivityScenario.launch(MediatorLiveDataActivity::class.java).use { scenario ->
            // 输入表单数据
            onView(withId(R.id.etUsername))
                .perform(typeText("testuser"), closeSoftKeyboard())
            onView(withId(R.id.etPassword))
                .perform(typeText("password123"), closeSoftKeyboard())
            onView(withId(R.id.etConfirmPassword))
                .perform(typeText("password123"), closeSoftKeyboard())
            
            // 验证表单验证结果
            onView(withId(R.id.tvValidationError))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testSubmitButtonInitiallyDisabled() {
        ActivityScenario.launch(MediatorLiveDataActivity::class.java).use { scenario ->
            // 验证提交按钮初始状态
            onView(withId(R.id.btnSubmit))
                .check(matches(not(isEnabled())))
        }
    }
}
