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
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.TypeInsnNode

// OnClickClassNode —— 使用 Tree API 进行插桩
class OnClickClassNode(
    api: Int
) : ClassNode(api) {

    override fun visitEnd() {
        // 在 visitEnd 中对所有方法进行插桩
        for (method in methods) {
            // 跳过静态初始化块
            if (method.name == "<clinit>") continue

            // 在方法开头插入日志代码
            val insns = InsnList()

            // 1. Tag
            insns.add(LdcInsnNode("OnClickMethodVisitor"))

            // 2. Msg
            insns.add(LdcInsnNode(">>> $name ${method.name}"))

            // 3. 构造 Throwable 对象
            insns.add(TypeInsnNode(Opcodes.NEW, "java/lang/Throwable")) // NEW Throwable
            insns.add(InsnNode(Opcodes.DUP))                            // DUP 复制引用
            insns.add(MethodInsnNode(                                  // 调用构造函数 <init>
                Opcodes.INVOKESPECIAL,
                "java/lang/Throwable",
                "<init>",
                "()V",
                false
            ))
            // 此时栈顶是一个初始化好的 Throwable 对象

            // 4. 调用 Log.d (String, String, Throwable)
            insns.add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "android/util/Log",
                    "d",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I", // 注意参数变化
                    false
                )
            )

            // 5. 清理返回值
            insns.add(InsnNode(Opcodes.POP))

            // 插入到方法指令的最前面
            method.instructions.insert(insns)
        }
        // 修改完成后，将 ClassNode 传递给下一个 visitor
        accept(nextClassVisitor)
    }


    private var nextClassVisitor: ClassVisitor? = null

    fun setNextClassVisitor(cv: ClassVisitor) {
        this.nextClassVisitor = cv
    }
}

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
        val classNode = OnClickClassNode(
            instrumentationContext.apiVersion.get(),
        )
        classNode.setNextClassVisitor(nextClassVisitor)
        return classNode
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
