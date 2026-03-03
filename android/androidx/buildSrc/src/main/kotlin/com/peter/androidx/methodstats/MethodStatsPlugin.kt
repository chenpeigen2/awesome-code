package com.peter.androidx.methodstats

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class MethodStatsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create("methodStats", MethodStatsExtension::class.java)
        
        project.afterEvaluate {
            println("[MethodStats] Analyzing method calls in project: ${project.name}")
            println("[MethodStats] Output file: ${ext.outputFile}")
            println("[MethodStats] Include patterns: ${ext.includePatterns}")
            println("[MethodStats] Exclude patterns: ${ext.excludePatterns}")
        }
        
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            if (variant.name.capitalize() == "Debug") {
                variant.transformClassesWith(MethodStatsClassVisitorFactory::class.java, InstrumentationScope.PROJECT) {
                    it.outputFile.set(ext.outputFile)
                    it.includePatterns.set(ext.includePatterns)
                    it.excludePatterns.set(ext.excludePatterns)
                    it.trackConstructors.set(ext.trackConstructors)
                    it.trackMethods.set(ext.trackMethods)
                }
                variant.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }
    }
}

open class MethodStatsExtension {
    var outputFile: String = "method_stats.json"
    var includePatterns: MutableList<String> = mutableListOf(".*")
    var excludePatterns: MutableList<String> = mutableListOf()
    var trackConstructors: Boolean = true
    var trackMethods: Boolean = true
}
