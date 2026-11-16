package org.peter.asm.basics;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * 一个 .class 文件的只读模型。
 * <p>
 * 读取走 Core API 的 {@link ClassReader}（流式访问者），但对外暴露的是不可变的查询对象：
 * 调用方拿到的是一份结构快照，不需要理解 visitXxx() 的调用顺序。
 *
 * <pre>{@code
 * ClassFile file = ClassFile.read(bytes);
 * file.method("greet", "()Ljava/lang/String;").ifPresent(m -> System.out.println(m.returnTypeName()));
 * }</pre>
 */
public final class ClassFile {

    /** 一个字段的结构信息。 */
    public record Field(String name, String descriptor, int access) {

        /** 例如 {@code Ljava/lang/String;} → {@code java.lang.String}。 */
        public String typeName() {
            return Type.getType(descriptor).getClassName();
        }

        public String modifiers() {
            return Modifiers.describe(access, MemberKind.FIELD);
        }

        public boolean isStatic() {
            return (access & Opcodes.ACC_STATIC) != 0;
        }
    }

    /** 一个方法的结构信息。 */
    public record Method(String name, String descriptor, int access) {

        public String returnTypeName() {
            return Type.getReturnType(descriptor).getClassName();
        }

        public List<String> parameterTypeNames() {
            return java.util.Arrays.stream(Type.getArgumentTypes(descriptor))
                    .map(Type::getClassName)
                    .toList();
        }

        public String modifiers() {
            return Modifiers.describe(access, MemberKind.METHOD);
        }

        public boolean isConstructor() {
            return "<init>".equals(name);
        }

        public boolean isStaticInitializer() {
            return "<clinit>".equals(name);
        }
    }

    private final String name;
    private final String superName;
    private final List<String> interfaces;
    private final int access;
    private final int version;
    private final List<Field> fields;
    private final List<Method> methods;

    private ClassFile(
            String name,
            String superName,
            List<String> interfaces,
            int access,
            int version,
            List<Field> fields,
            List<Method> methods) {
        this.name = name;
        this.superName = superName;
        this.interfaces = List.copyOf(interfaces);
        this.access = access;
        this.version = version;
        this.fields = List.copyOf(fields);
        this.methods = List.copyOf(methods);
    }

    /** 从字节码读取结构。不会保留调试信息，也不需要 frames。 */
    public static ClassFile read(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        Collector collector = new Collector();
        reader.accept(collector, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return collector.toClassFile();
    }

    /** JVM 内部名，例如 {@code org/peter/asm/basics/ClassFile}。 */
    public String name() {
        return name;
    }

    /** Java 源码里的类名，例如 {@code org.peter.asm.basics.ClassFile}。 */
    public String javaName() {
        return name.replace('/', '.');
    }

    /** 父类的 JVM 内部名；{@code java/lang/Object} 的父类为 {@code null}。 */
    public String superName() {
        return superName;
    }

    public List<String> interfaces() {
        return interfaces;
    }

    public List<Field> fields() {
        return fields;
    }

    public List<Method> methods() {
        return methods;
    }

    /** class 文件的 Java 版本号，例如 {@code 25}。 */
    public String versionName() {
        int major = version & 0xFFFF;
        return Integer.toString(major - 44);
    }

    public Optional<Field> field(String fieldName) {
        return fields.stream().filter(f -> f.name().equals(fieldName)).findFirst();
    }

    /** 按名字 + 描述符查找方法（重载方法必须用描述符区分）。 */
    public Optional<Method> method(String methodName, String descriptor) {
        return methods.stream()
                .filter(m -> m.name().equals(methodName) && m.descriptor().equals(descriptor))
                .findFirst();
    }

    public boolean isPublic() {
        return (access & Opcodes.ACC_PUBLIC) != 0;
    }

    public boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (access & Opcodes.ACC_ABSTRACT) != 0;
    }

    /** 生成类似 {@code javap} 的结构摘要。 */
    public String describe() {
        StringBuilder out = new StringBuilder();
        out.append(Modifiers.describe(access, MemberKind.CLASS))
                .append(" class ")
                .append(javaName());
        if (superName != null && !"java/lang/Object".equals(superName)) {
            out.append(" extends ").append(superName.replace('/', '.'));
        }
        if (!interfaces.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ", " implements ", "");
            interfaces.forEach(i -> joiner.add(i.replace('/', '.')));
            out.append(joiner);
        }
        out.append(" {\n");

        for (Field field : fields) {
            out.append("  ")
                    .append(field.modifiers()).append(' ')
                    .append(field.typeName()).append(' ')
                    .append(field.name())
                    .append(";\n");
        }

        for (Method method : methods) {
            // 构造器在源码里没有返回类型，写成 `类名(参数)` 而不是 `类名 类名(参数)`
            String signature = method.isConstructor()
                    ? javaName()
                    : method.returnTypeName() + " " + method.name();
            out.append("  ")
                    .append(method.modifiers()).append(' ')
                    .append(signature)
                    .append('(')
                    .append(String.join(", ", method.parameterTypeNames()))
                    .append(");\n");
        }

        out.append("}");
        return out.toString();
    }

    @Override
    public String toString() {
        return describe();
    }

    /** 流式访问者 → 不可变模型。 */
    private static final class Collector extends ClassVisitor {

        private String name;
        private String superName;
        private List<String> interfaces = List.of();
        private int access;
        private int version;
        private final List<Field> fields = new ArrayList<>();
        private final List<Method> methods = new ArrayList<>();

        private Collector() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(
                int version,
                int access,
                String name,
                String signature,
                String superName,
                String[] interfaces) {
            this.version = version;
            this.access = access;
            this.name = name;
            this.superName = superName;
            this.interfaces = interfaces == null ? List.of() : List.of(interfaces);
        }

        @Override
        public FieldVisitor visitField(
                int access, String name, String descriptor, String signature, Object value) {
            fields.add(new Field(name, descriptor, access));
            return null;
        }

        @Override
        public MethodVisitor visitMethod(
                int access, String name, String descriptor, String signature, String[] exceptions) {
            methods.add(new Method(name, descriptor, access));
            return null;
        }

        private ClassFile toClassFile() {
            return new ClassFile(name, superName, interfaces, access, version, fields, methods);
        }
    }
}
