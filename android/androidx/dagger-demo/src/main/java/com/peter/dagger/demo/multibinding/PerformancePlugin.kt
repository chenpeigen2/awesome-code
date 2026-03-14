package com.peter.dagger.demo.multibinding

import javax.inject.Inject

/**
 * PerformancePlugin - 性能监控插件
 */
class PerformancePlugin @Inject constructor() : Plugin {
    override fun name(): String = "Performance"
    override fun execute(): String = "⚡ 监控性能指标..."
}
