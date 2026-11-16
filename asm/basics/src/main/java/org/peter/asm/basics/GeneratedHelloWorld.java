package org.peter.asm.basics;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 用 Core API 从零生成一个可运行的类。
 * <p>
 * 生成的 {@code sample/HelloWorld} 结构等价于：
 *
 * <pre>{@code
 * package sample;
 * public class HelloWorld {
 *     public static String greeting = "Hello from ASM";
 *     public String greet(String who) { return "Hello, " + who + "!"; }
 *     public static String message() { return greeting; }
 *     public static void main(String[] args) { System.out.println(message()); }
 * }
 * }</pre>
 *
 * 运行：{@code ./gradlew :asm:basics:demo}
 */
public final class GeneratedHelloWorld implements Opcodes {

    private static final String CLASS_NAME = "sample/HelloWorld";
    private static final String CLASS_FILE_NAME = "sample.HelloWorld";

    private GeneratedHelloWorld() {
    }

    public static void main(String[] args) throws Exception {
        Object instance = newInstance(dump());
        System.out.println("generated: " + instance.getClass().getName());
        System.out.println("message(): " + callStatic(dump(), "message"));
        System.out.println("greet(\"ASM\"): " + instance.getClass()
                .getMethod("greet", String.class).invoke(instance, "ASM"));
    }

    /**
     * 生成字节码。
     * <p>
     * 使用 {@link ClassWriter#COMPUTE_FRAMES} 让 ASM 自动算栈帧，
     * 代价是它需要通过 {@link ClassWriter#getCommonSuperClass} 加载相关类；
     * 本例只依赖 {@code java/lang/Object}，不会触发类加载。
     */
    public static byte[] dump() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V25, ACC_PUBLIC | ACC_SUPER, CLASS_NAME, null, "java/lang/Object", null);

        cw.visitField(
                        ACC_PUBLIC | ACC_STATIC,
                        "greeting",
                        "Ljava/lang/String;",
                        null,
                        "Hello from ASM")
                .visitEnd();

        emitConstructor(cw);
        emitGreet(cw);
        emitMessage(cw);
        emitMain(cw);

        cw.visitEnd();
        return cw.toByteArray();
    }

    /** 生成 {@code public HelloWorld() { super(); }}。 */
    private static void emitConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0); // this
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // COMPUTE_MAXS 会重算，这里给占位值
        mv.visitEnd();
    }

    /** 生成 {@code public String greet(String who) { return "Hello, " + who + "!"; }}。 */
    private static void emitGreet(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC, "greet", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
        mv.visitCode();
        // new StringBuilder().append("Hello, ").append(who).append("!").toString()
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("Hello, ");
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);
        mv.visitVarInsn(ALOAD, 1); // who
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);
        mv.visitLdcInsn("!");
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);
        mv.visitMethodInsn(
                INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /** 生成 {@code public static String message() { return greeting; }}。 */
    private static void emitMessage(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC, "message", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, CLASS_NAME, "greeting", "Ljava/lang/String;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /** 生成 {@code public static void main(String[] args) { System.out.println(message()); }}。 */
    private static void emitMain(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitMethodInsn(INVOKESTATIC, CLASS_NAME, "message", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(
                INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    // ------------------------------------------------------------------
    // 测试/演示用的加载工具：把生成的字节码交给一个独立 ClassLoader
    // ------------------------------------------------------------------

    /** 加载生成类并 newInstance。每次调用都用一个全新的 ClassLoader，避免缓存干扰。 */
    public static Object newInstance(byte[] bytes) throws Exception {
        Class<?> type = load(bytes);
        return type.getDeclaredConstructor().newInstance();
    }

    /** 加载生成类并调用其静态无参方法。 */
    public static Object callStatic(byte[] bytes, String methodName) throws Exception {
        return load(bytes).getMethod(methodName).invoke(null);
    }

    public static Class<?> load(byte[] bytes) {
        return new GeneratedClassLoader(bytes).load();
    }

    private static final class GeneratedClassLoader extends ClassLoader {
        private final byte[] bytes;

        private GeneratedClassLoader(byte[] bytes) {
            super(GeneratedHelloWorld.class.getClassLoader());
            this.bytes = bytes;
        }

        private Class<?> load() {
            return defineClass(CLASS_FILE_NAME, bytes, 0, bytes.length);
        }
    }
}
