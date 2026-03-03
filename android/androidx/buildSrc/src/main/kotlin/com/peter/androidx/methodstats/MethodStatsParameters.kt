package com.peter.androidx.methodstats

import org.gradle.api.tasks.Input
import java.io.File

abstract class MethodStatsParameters : org.gradle.api.tasks.Input {

    @get:Input
    abstract var outputFile: String

    @get:Input
    abstract var includePatterns: List<String>

    @get:Input
    abstract var excludePatterns: List<String>

    @get:Input
    abstract var trackConstructors: Boolean

    @get:Input
    abstract var trackMethods: Boolean
}
