package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.viewmodel.ViewModelFactoryActivity
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ViewModelFactoryActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class ViewModelFactoryActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(ViewModelFactoryActivity::class.java).use { scenario ->
            // 验证按钮显示
            onView(withId(R.id.btnLoadUser))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnUpdateName))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testLoadUser() {
        ActivityScenario.launch(ViewModelFactoryActivity::class.java).use { scenario ->
            // 点击加载用户按钮
            onView(withId(R.id.btnLoadUser)).perform(click())
            
            // 验证用户信息显示
            onView(withId(R.id.tvUserId))
                .check(matches(withText(containsString("用户ID:"))))
            onView(withId(R.id.tvUserName))
                .check(matches(withText(containsString("用户名:"))))
        }
    }

    @Test
    fun testUpdateName() {
        ActivityScenario.launch(ViewModelFactoryActivity::class.java).use { scenario ->
            // 先加载用户
            onView(withId(R.id.btnLoadUser)).perform(click())
            
            // 输入新用户名
            onView(withId(R.id.etName))
                .perform(typeText("新名字"), closeSoftKeyboard())
            
            // 点击更新按钮
            onView(withId(R.id.btnUpdateName)).perform(click())
            
            // 验证用户名更新
            onView(withId(R.id.tvUserName))
                .check(matches(withText(containsString("新名字"))))
        }
    }
}
