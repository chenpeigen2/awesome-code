package org.peter.asm.transform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.function.BiConsumer;

/**
 * 给每个方法体前后插入计时逻辑，不改变原有行为。
 * <p>
 * 用 asm-commons 的 {@link AdviceAdapter}：它跟踪局部变量表，让新插入的变量槽
 * 不会和已有变量冲突，也知道构造器里 {@code super()} 调用的位置。
 *
 * <p>增强后的方法等价于：
 * <pre>{@code
 * long start = System.nanoTime();
 * try {
 *     ...原方法体...
 * } finally {
 *     TimingRuntime.record("methodName", System.nanoTime() - start);
 * }
 * }</pre>
 *
 * 构造器（{@code <init>}）与静态初始化块（@code <clinit>}）被跳过：
 * 它们的执行时机由 JVM 控制，计时没有意义。
 *
 * <p>被增强的类只依赖 {@link TimingRuntime} 这个静态入口，
 * 不需要把任何回调对象编进字节码。
 */
public final class TimingTransformer implements Opcodes {

    /** 计时回调：方法名 + 耗时（纳秒）。 */
    @FunctionalInterface
    public interface TimingSink extends BiConsumer<String, Long> {
    }

    private static final Type SYSTEM = Type.getType(System.class);
    private static final Type RUNTIME = Type.getType(TimingRuntime.class);
    private static final Method NANO_TIME = new Method("nanoTime", "()J");
    private static final Method RECORD = new Method("record", "(Ljava/lang/String;J)V");

    private TimingTransformer() {
    }

    /**
     * 增强字节码，并把 {@code sink} 安装到当前线程。
     *
     * @param classBytes 原始字节码
     * @param sink       每次方法调用结束时收到一条计时记录
     * @return 增强后的字节码
     */
    public static byte[] transform(byte[] classBytes, TimingSink sink) {
        TimingRuntime.install(sink);
        return transform(classBytes);
    }

    /** 只做增强，sink 由调用方通过 {@link TimingRuntime#install} 自行管理。 */
    public static byte[] transform(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        // COMPUTE_FRAMES 必须配 EXPAND_FRAMES：AdviceAdapter 会插入新的跳转目标，
        // 压缩帧无法直接平移，展开后由 ASM 重新计算
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        reader.accept(new TimingClassVisitor(writer), ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    private static final class TimingClassVisitor extends org.objectweb.asm.ClassVisitor {

        private TimingClassVisitor(ClassWriter writer) {
            super(ASM9, writer);
        }

        @Override
        public org.objectweb.asm.MethodVisitor visitMethod(
                int access, String name, String descriptor, String signature, String[] exceptions) {
            var next = super.visitMethod(access, name, descriptor, signature, exceptions);

            boolean skip = "<init>".equals(name)
                    || "<clinit>".equals(name)
                    || (access & (ACC_ABSTRACT | ACC_NATIVE)) != 0;
            if (skip) {
                return next;
            }
            return new TimingMethodVisitor(next, access, name, descriptor);
        }
    }

    private static final class TimingMethodVisitor extends AdviceAdapter {

        private final String methodName;

        /** start 时间的局部变量槽，由 {@link #onMethodEnter()} 分配。 */
        private int startSlot;

        private TimingMethodVisitor(
                org.objectweb.asm.MethodVisitor delegate, int access, String name, String descriptor) {
            super(ASM9, delegate, access, name, descriptor);
            this.methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            startSlot = newLocal(Type.LONG_TYPE);
            invokeStatic(SYSTEM, NANO_TIME);
            storeLocal(startSlot);
        }

        /**
         * 每个 return / athrow 之前都会走到这里。
         * <p>
         * 此时栈顶已经放着返回值（或抛出的 Throwable），必须先按「真实类型」存进临时槽，
         * 否则后续插入的计时代码会把返回值顶掉。注意 ATHROW 时栈上是 Throwable，
         * 不能用方法声明的返回类型来存。
         */
        @Override
        protected void onMethodExit(int opcode) {
            Type stackType = opcode == ATHROW
                    ? Type.getType(Throwable.class)
                    : Type.getReturnType(methodDesc);

            int savedSlot = -1;
            if (opcode != RETURN) {
                savedSlot = newLocal(stackType);
                storeLocal(savedSlot, stackType);
            }

            push(methodName);
            invokeStatic(SYSTEM, NANO_TIME);
            loadLocal(startSlot);
            math(SUB, Type.LONG_TYPE);
            invokeStatic(RUNTIME, RECORD);

            if (savedSlot >= 0) {
                loadLocal(savedSlot, stackType);
            }
        }
    }
}
