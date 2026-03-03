package com.peter.androidx.methodstats

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class MethodStatsClassVisitorFactory : AsmClassVisitorFactory<MethodStatsParameters> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        val params = parameters.get()
        return MethodStatsClassVisitor(
            nextClassVisitor,
            classContext.currentClassData.className,
            params.includePatterns,
            params.excludePatterns,
            params.trackConstructors,
            params.trackMethods
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val params = parameters.get()
        val className = classData.className
        
        val isIncluded = params.includePatterns.any { pattern ->
            Regex(pattern).matches(className)
        }
        
        val isExcluded = params.excludePatterns.any { pattern ->
            Regex(pattern).matches(className)
        }
        
        return isIncluded && !isExcluded
    }
}
