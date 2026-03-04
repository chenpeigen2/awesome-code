package com.peter.androidx.methodstats

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodStatsPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.afterEvaluate {
            println("[MethodStats] Method call logging plugin applied to: ${project.name}")
        }

        val androidComponents = project.extensions
            .getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->

            variant.instrumentation.transformClassesWith(
                MethodStatsClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {
                // 如果有参数，在这里配置
            }
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COPY_FRAMES
            )
        }
    }
}
