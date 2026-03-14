package com.peter.dagger.demo.di

import com.peter.dagger.demo.multibinding.AnalyticsPlugin
import com.peter.dagger.demo.multibinding.CrashReportPlugin
import com.peter.dagger.demo.multibinding.PerformancePlugin
import com.peter.dagger.demo.multibinding.Plugin
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey
import javax.inject.Singleton

/**
 * MultibindingModule - 多绑定模块
 *
 * 演示 @Multibindings:
 * 1. @IntoSet - 将依赖添加到 Set<T>
 * 2. @IntoMap + @StringKey - 将依赖添加到 Map<String, T>
 * 3. @IntoMap + @ClassKey - 将依赖添加到 Map<Class<?>, T>
 *
 * 使用场景：
 * - 插件系统
 * - 策略模式
 * - 事件监听器集合
 */
@Module
interface MultibindingModule {

    // ============== Set 多绑定 ==============

    /**
     * @IntoSet 将 AnalyticsPlugin 添加到 Set<Plugin>
     */
    @Binds
    @IntoSet
    @Singleton
    fun bindAnalyticsPluginIntoSet(plugin: AnalyticsPlugin): Plugin

    @Binds
    @IntoSet
    @Singleton
    fun bindCrashReportPluginIntoSet(plugin: CrashReportPlugin): Plugin

    @Binds
    @IntoSet
    @Singleton
    fun bindPerformancePluginIntoSet(plugin: PerformancePlugin): Plugin

    // ============== Map 多绑定 ==============

    /**
     * @IntoMap 将依赖添加到 Map<String, Plugin>
     * @StringKey 指定 Map 的 key
     */
    @Binds
    @IntoMap
    @StringKey("analytics")
    @Singleton
    fun bindAnalyticsPluginIntoMap(plugin: AnalyticsPlugin): Plugin

    @Binds
    @IntoMap
    @StringKey("crash")
    @Singleton
    fun bindCrashReportPluginIntoMap(plugin: CrashReportPlugin): Plugin

    @Binds
    @IntoMap
    @StringKey("performance")
    @Singleton
    fun bindPerformancePluginIntoMap(plugin: PerformancePlugin): Plugin
}