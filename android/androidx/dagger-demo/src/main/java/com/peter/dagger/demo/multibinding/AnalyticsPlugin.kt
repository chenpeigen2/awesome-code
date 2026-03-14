package com.peter.dagger.demo.multibinding

import javax.inject.Inject

/**
 * AnalyticsPlugin - 分析插件
 */
class AnalyticsPlugin @Inject constructor() : Plugin {
    override fun name(): String = "Analytics"
    override fun execute(): String = "📊 执行数据分析..."
}
