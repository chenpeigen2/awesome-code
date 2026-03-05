package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.livedata.TransformationsActivity
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TransformationsActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class TransformationsActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(TransformationsActivity::class.java).use { scenario ->
            // 验证输入框显示
            onView(withId(R.id.etInput))
                .check(matches(isDisplayed()))
            onView(withId(R.id.etSearch))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testFormatInput() {
        ActivityScenario.launch(TransformationsActivity::class.java).use { scenario ->
            // 输入文本
            onView(withId(R.id.etInput))
                .perform(typeText("hello"), closeSoftKeyboard())
            
            // 点击格式化按钮
            onView(withId(R.id.btnFormat)).perform(click())
            
            // 验证格式化结果
            onView(withId(R.id.tvFormatted))
                .check(matches(withText(containsString("【HELLO】"))))
        }
    }

    @Test
    fun testSearchContent() {
        ActivityScenario.launch(TransformationsActivity::class.java).use { scenario ->
            // 输入搜索内容
            onView(withId(R.id.etSearch))
                .perform(typeText("test query"), closeSoftKeyboard())
            
            // 点击搜索按钮
            onView(withId(R.id.btnSearch)).perform(click())
            
            // 验证搜索结果
            onView(withId(R.id.tvSearchResult))
                .check(matches(withText(containsString("搜索内容:"))))
        }
    }

    @Test
    fun testSearchUser() {
        ActivityScenario.launch(TransformationsActivity::class.java).use { scenario ->
            // 输入用户搜索
            onView(withId(R.id.etSearch))
                .perform(typeText("user:john"), closeSoftKeyboard())
            
            // 点击搜索按钮
            onView(withId(R.id.btnSearch)).perform(click())
            
            // 验证搜索结果
            onView(withId(R.id.tvSearchResult))
                .check(matches(withText(containsString("搜索用户:"))))
        }
    }

    @Test
    fun testSwitchUser() {
        ActivityScenario.launch(TransformationsActivity::class.java).use { scenario ->
            // 点击切换用户按钮
            onView(withId(R.id.btnSwitchUser)).perform(click())
            
            // 验证用户信息更新
            onView(withId(R.id.tvUserInfo))
                .check(matches(withText(containsString("当前用户:"))))
        }
    }

    @Test
    fun testMultipleUserSwitches() {
        ActivityScenario.launch(TransformationsActivity::class.java).use { scenario ->
            // 多次切换用户
            onView(withId(R.id.btnSwitchUser)).perform(click())
            onView(withId(R.id.btnSwitchUser)).perform(click())
            onView(withId(R.id.btnSwitchUser)).perform(click())
            
            // 验证用户信息显示
            onView(withId(R.id.tvUserInfo))
                .check(matches(isDisplayed()))
        }
    }
}
