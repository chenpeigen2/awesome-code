package com.peter.androidx.methodstats

// buildSrc/src/main/kotlin/OnClickPlugin.kt
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

// OnClickClassVisitor —— 真正插桩逻辑
class OnClickClassVisitor(
    api: Int,
    classVisitor: ClassVisitor
) : ClassVisitor(api, classVisitor) {

    private var className: String? = null

    override fun visit(
        version: Int, access: Int, name: String?,
        signature: String?, superName: String?, interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
    }

    override fun visitMethod(
        access: Int, name: String?, descriptor: String?,
        signature: String?, exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (mv != null && name != null && name != "<clinit>") {
            return OnClickMethodVisitor(api, mv, className)
        }
        return mv
    }
}

// OnClickMethodVisitor —— 方法里插具体代码
class OnClickMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    private val className: String?
) : MethodVisitor(api, methodVisitor) {

    override fun visitCode() {
        super.visitCode()

        mv.visitLdcInsn("MethodStats111")
        mv.visitLdcInsn(">>> $className")

        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/util/Log",
            "d",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )

        mv.visitInsn(Opcodes.POP)
    }
}

// Factory —— AGP 通过它创建 ClassVisitor
abstract class OnClickClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        return !className.contains("R$") && !className.contains("BuildConfig")
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return OnClickClassVisitor(
            instrumentationContext.apiVersion.get(),
            nextClassVisitor
        )
    }
}

// 插件入口 —— 在这里注册 Factory
abstract class OnClickPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents = project.extensions
            .getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                OnClickClassVisitorFactory::class.java,
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
