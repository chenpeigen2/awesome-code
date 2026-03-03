package com.peter.androidx.methodstats

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.tasks.Input

interface MethodStatsParameters : InstrumentationParameters {

    @get:Input
    var outputFile: String

    @get:Input
    var includePatterns: List<String>

    @get:Input
    var excludePatterns: List<String>

    @get:Input
    var trackConstructors: Boolean

    @get:Input
    var trackMethods: Boolean
}
