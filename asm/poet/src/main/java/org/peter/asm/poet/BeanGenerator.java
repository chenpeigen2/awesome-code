package org.peter.asm.poet;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用 JavaPoet 生成一个不可变的 POJO 源码。
 * <p>
 * 和 {@code asm} 系列模块对照：ASM 直接产出字节码，JavaPoet 产出「人类可读的 Java 源码」，
 * 交给 javac 去编译。两者是同一件事的两个层次：
 *
 * <ul>
 *   <li>JavaPoet：声明式、类型安全、生成的代码可读可改，适合注解处理器；</li>
 *   <li>ASM：可以修改已有类、插入指令，适合运行期增强；</li>
 * </ul>
 *
 * 对 {@code generate("com.example", "User", of("name", String.class))}，产出大致是：
 *
 * <pre>{@code
 * package com.example;
 *
 * public final class User {
 *   private final java.lang.String name;
 *
 *   public User(java.lang.String name) {
 *     this.name = name;
 *   }
 *
 *   public java.lang.String getName() {
 *     return name;
 *   }
 *   ...
 * }
 * }</pre>
 */
public final class BeanGenerator {

    private BeanGenerator() {
    }

    /** 生成指定包名 + 类名的源码文本。 */
    public static String generate(String packageName, String className, Property... properties) {
        return generate(packageName, className, Arrays.asList(properties));
    }

    /** 生成指定包名 + 类名的源码文本。 */
    public static String generate(String packageName, String className, List<Property> properties) {
        Objects.requireNonNull(packageName, "packageName");
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(properties, "properties");

        TypeSpec type = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addFields(fields(properties))
                .addMethod(constructor(className, properties))
                .addMethods(getters(properties))
                .addMethod(equalsMethod(className, properties))
                .addMethod(hashCodeMethod(properties))
                .addMethod(toStringMethod(className, properties))
                .build();

        return JavaFile.builder(packageName, type).build().toString();
    }

    private static List<FieldSpec> fields(List<Property> properties) {
        return properties.stream()
                .map(p -> FieldSpec.builder(p.type(), p.name(), Modifier.PRIVATE, Modifier.FINAL).build())
                .toList();
    }

    private static MethodSpec constructor(String className, List<Property> properties) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        for (Property property : properties) {
            builder.addParameter(ParameterSpec.builder(property.type(), property.name()).build());
            builder.addStatement("this.$N = $N", property.name(), property.name());
        }
        return builder.build();
    }

    private static List<MethodSpec> getters(List<Property> properties) {
        return properties.stream()
                .map(p -> MethodSpec.methodBuilder("get" + p.capitalizedName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(p.type())
                        .addStatement("return $N", p.name())
                        .build())
                .toList();
    }

    /**
     * 生成 {@code equals}：先判同一引用，再判类型，最后逐字段比较。
     * <p>
     * 对基本类型用 {@code ==}，对引用类型用 {@link Objects#equals}——这一点由
     * JavaPoet 的 {@code $T} / 手工分支决定，所以这里按类型分两条语句。
     */
    private static MethodSpec equalsMethod(String className, List<Property> properties) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(Object.class, "o")
                .addStatement("if (this == o) return true")
                .addStatement("if (!(o instanceof $L that)) return false", className);

        if (properties.isEmpty()) {
            return builder.addStatement("return true").build();
        }

        CodeBlock conditions = properties.stream()
                .map(BeanGenerator::equalityCondition)
                .collect(CodeBlock.joining(" && "));

        return builder.addStatement("return $L", conditions).build();
    }

    private static CodeBlock equalityCondition(Property property) {
        TypeName type = TypeName.get(property.type());
        if (type.isPrimitive()) {
            return CodeBlock.of("this.$N == that.$N", property.name(), property.name());
        }
        return CodeBlock.of("$T.equals(this.$N, that.$N)",
                Objects.class, property.name(), property.name());
    }

    private static MethodSpec hashCodeMethod(List<Property> properties) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class);

        if (properties.isEmpty()) {
            return builder.addStatement("return $T.hash()", Objects.class).build();
        }

        String arguments = properties.stream()
                .map(Property::name)
                .collect(Collectors.joining(", "));
        return builder.addStatement("return $T.hash($L)", Objects.class, arguments).build();
    }

    private static MethodSpec toStringMethod(String className, List<Property> properties) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class);

        if (properties.isEmpty()) {
            return builder.addStatement("return $S", className + "{}").build();
        }

        String format = className + "{"
                + properties.stream().map(p -> p.name() + "=%s").collect(Collectors.joining(", "))
                + "}";
        String arguments = properties.stream().map(Property::name).collect(Collectors.joining(", "));
        return builder.addStatement("return $T.format($S, $L)", String.class, format, arguments).build();
    }

    /** 供 demo 使用：直接打印生成的源码。 */
    public static void main(String[] args) {
        System.out.println(generate("com.example", "User",
                Property.of("name", String.class),
                Property.of("age", int.class)));
    }
}
