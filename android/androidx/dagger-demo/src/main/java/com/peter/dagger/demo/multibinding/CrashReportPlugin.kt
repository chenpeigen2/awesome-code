package com.peter.dagger.demo.multibinding

import javax.inject.Inject

/**
 * CrashReportPlugin - 崩溃报告插件
 */
class CrashReportPlugin @Inject constructor() : Plugin {
    override fun name(): String = "CrashReport"
    override fun execute(): String = "🐛 上传崩溃日志..."
}
