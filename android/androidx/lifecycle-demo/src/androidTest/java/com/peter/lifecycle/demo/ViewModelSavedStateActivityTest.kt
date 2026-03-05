package com.peter.lifecycle.demo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peter.lifecycle.demo.viewmodel.ViewModelSavedStateActivity
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ViewModelSavedStateActivity UI 测试
 */
@RunWith(AndroidJUnit4::class)
class ViewModelSavedStateActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(ViewModelSavedStateActivity::class.java).use { scenario ->
            // 验证按钮和输入框显示
            onView(withId(R.id.etInput))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnSave))
                .check(matches(isDisplayed()))
            onView(withId(R.id.btnIncrement))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testSaveText() {
        ActivityScenario.launch(ViewModelSavedStateActivity::class.java).use { scenario ->
            // 输入文本
            onView(withId(R.id.etInput))
                .perform(typeText("test save"), closeSoftKeyboard())
            
            // 点击保存按钮
            onView(withId(R.id.btnSave)).perform(click())
            
            // 验证保存的文本
            onView(withId(R.id.tvSavedText))
                .check(matches(withText(containsString("test save"))))
        }
    }

    @Test
    fun testIncrementCounter() {
        ActivityScenario.launch(ViewModelSavedStateActivity::class.java).use { scenario ->
            // 点击计数器增加按钮
            onView(withId(R.id.btnIncrement)).perform(click())
            
            // 验证计数器值
            onView(withId(R.id.tvCounter))
                .check(matches(withText(containsString("1"))))
        }
    }

    @Test
    fun testAddToList() {
        ActivityScenario.launch(ViewModelSavedStateActivity::class.java).use { scenario ->
            // 输入列表项
            onView(withId(R.id.etInput))
                .perform(typeText("item1"), closeSoftKeyboard())
            
            // 点击添加按钮
            onView(withId(R.id.btnAddToList)).perform(click())
            
            // 验证列表项显示
            onView(withId(R.id.tvItems))
                .check(matches(withText(containsString("item1"))))
        }
    }

    @Test
    fun testMultipleCounterIncrements() {
        ActivityScenario.launch(ViewModelSavedStateActivity::class.java).use { scenario ->
            // 多次点击增加按钮
            onView(withId(R.id.btnIncrement)).perform(click())
            onView(withId(R.id.btnIncrement)).perform(click())
            onView(withId(R.id.btnIncrement)).perform(click())
            
            // 验证计数器值
            onView(withId(R.id.tvCounter))
                .check(matches(withText(containsString("3"))))
        }
    }
}
