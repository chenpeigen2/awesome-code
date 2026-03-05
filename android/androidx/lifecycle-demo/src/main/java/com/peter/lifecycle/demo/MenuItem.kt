package com.peter.lifecycle.demo

import android.content.Context
import android.content.Intent
import com.peter.lifecycle.demo.advanced.ProcessLifecycleActivity
import com.peter.lifecycle.demo.advanced.RepeatOnLifecycleActivity
import com.peter.lifecycle.demo.advanced.ViewModelScopeActivity
import com.peter.lifecycle.demo.basic.LifecycleObserverActivity
import com.peter.lifecycle.demo.basic.CustomLifecycleOwnerActivity
import com.peter.lifecycle.demo.flow.FlowLiveDataActivity
import com.peter.lifecycle.demo.flow.SharedFlowActivity
import com.peter.lifecycle.demo.flow.StateFlowActivity
import com.peter.lifecycle.demo.livedata.CustomLiveDataActivity
import com.peter.lifecycle.demo.livedata.MediatorLiveDataActivity
import com.peter.lifecycle.demo.livedata.TransformationsActivity
import com.peter.lifecycle.demo.mvvm.MvvmActivity
import com.peter.lifecycle.demo.viewmodel.SharedViewModelActivity
import com.peter.lifecycle.demo.viewmodel.ViewModelFactoryActivity
import com.peter.lifecycle.demo.viewmodel.ViewModelSavedStateActivity

/**
 * 菜单项数据模型
 */
data class MenuItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val intent: Intent? = null
)

/**
 * 各 Activity 的 Intent 创建函数
 */

// Lifecycle 基础示例
fun createLifecycleObserverIntent(context: Context) = Intent(context, LifecycleObserverActivity::class.java)
fun createCustomLifecycleOwnerIntent(context: Context) = Intent(context, CustomLifecycleOwnerActivity::class.java)

// ViewModel 示例
fun createViewModelSavedStateIntent(context: Context) = Intent(context, ViewModelSavedStateActivity::class.java)
fun createViewModelFactoryIntent(context: Context) = Intent(context, ViewModelFactoryActivity::class.java)
fun createSharedViewModelIntent(context: Context) = Intent(context, SharedViewModelActivity::class.java)

// LiveData 示例
fun createTransformationsIntent(context: Context) = Intent(context, TransformationsActivity::class.java)
fun createMediatorLiveDataIntent(context: Context) = Intent(context, MediatorLiveDataActivity::class.java)
fun createCustomLiveDataIntent(context: Context) = Intent(context, CustomLiveDataActivity::class.java)

// Flow 示例
fun createStateFlowIntent(context: Context) = Intent(context, StateFlowActivity::class.java)
fun createSharedFlowIntent(context: Context) = Intent(context, SharedFlowActivity::class.java)
fun createFlowLiveDataIntent(context: Context) = Intent(context, FlowLiveDataActivity::class.java)

// 进阶示例
fun createViewModelScopeIntent(context: Context) = Intent(context, ViewModelScopeActivity::class.java)
fun createRepeatOnLifecycleIntent(context: Context) = Intent(context, RepeatOnLifecycleActivity::class.java)
fun createProcessLifecycleIntent(context: Context) = Intent(context, ProcessLifecycleActivity::class.java)

// 实战示例
fun createMvvmIntent(context: Context) = Intent(context, MvvmActivity::class.java)
