package com.peter.androidx.methodstats

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import java.io.File

open class MethodStatsReportTask : DefaultTask() {

    @Input
    var outputFile: String = "method_stats.json"

    @TaskAction
    fun generateReport() {
        val stats = MethodStatsClassVisitor.getStats()
        
        val json = buildString {
            appendLine("{")
            appendLine("  \"timestamp\": \"${System.currentTimeMillis()}\",")
            appendLine("  \"total_methods\": ${stats.size},")
            appendLine("  \"total_calls\": ${stats.values.sumOf { it.callCount }},")
            appendLine("  \"methods\": [")
            
            val entries = stats.entries.sortedByDescending { it.value.callCount }
            entries.forEachIndexed { index, entry ->
                val methodInfo = entry.value
                appendLine("    {")
                appendLine("      \"class\": \"${methodInfo.className}\",")
                appendLine("      \"method\": \"${methodInfo.methodName}\",")
                appendLine("      \"descriptor\": \"${methodInfo.methodDesc}\",")
                appendLine("      \"call_count\": ${methodInfo.callCount},")
                appendLine("      \"callers\": [")
                
                val callers = methodInfo.calls.map { it.callerClass + "." + it.callerMethod }.distinct()
                callers.forEachIndexed { callerIndex, caller ->
                    val count = methodInfo.calls.count { "${it.callerClass}.${it.callerMethod}" == caller }
                    append("        {\"caller\": \"$caller\", \"count\": $count}")
                    if (callerIndex < callers.size - 1) appendLine(",") else appendLine()
                }
                
                appendLine("      ]")
                append("    }")
                if (index < entries.size - 1) appendLine(",") else appendLine()
            }
            
            appendLine("  ]")
            appendLine("}")
        }
        
        val output = File(outputFile)
        output.parentFile?.mkdirs()
        output.writeText(json)
        
        println("[MethodStats] Report generated: ${output.absolutePath}")
        println("[MethodStats] Total methods tracked: ${stats.size}")
        println("[MethodStats] Total method calls: ${stats.values.sumOf { it.callCount }}")
    }
}
