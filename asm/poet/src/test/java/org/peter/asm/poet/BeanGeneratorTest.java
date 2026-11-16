package org.peter.asm.poet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JavaPoet 生成源码的规格。
 * <p>
 * 断言的是「生成的源码文本里包含正确的声明」——这就是 JavaPoet 的外部契约：
 * 它产出的不是字节码，而是能被人读到、被 javac 编译的 .java 文本。
 */
class BeanGeneratorTest {

    @Test
    void generatesPackageAndTypeDeclaration() {
        String source = BeanGenerator.generate("com.example", "User");

        assertTrue(source.startsWith("package com.example;"), source);
        assertTrue(source.contains("public final class User {"), source);
    }

    @Test
    void generatesFieldsForEveryDeclaredProperty() {
        String source = BeanGenerator.generate("com.example", "User",
                Property.of("name", String.class),
                Property.of("age", int.class));

        // JavaPoet 的约定：java.lang 下的类型不写全限定名，自动 import
        assertTrue(source.contains("private final String name;"), source);
        assertTrue(source.contains("private final int age;"), source);
    }

    @Test
    void generatesConstructorAssigningEveryField() {
        String source = BeanGenerator.generate("com.example", "User",
                Property.of("name", String.class),
                Property.of("age", int.class));

        assertTrue(source.contains("public User(String name, int age) {"), source);
        assertTrue(source.contains("this.name = name;"), source);
        assertTrue(source.contains("this.age = age;"), source);
    }

    @Test
    void generatesGetterForEveryProperty() {
        String source = BeanGenerator.generate("com.example", "User",
                Property.of("name", String.class),
                Property.of("count", long.class));

        assertTrue(source.contains("public String getName() {"), source);
        assertTrue(source.contains("return name;"), source);
        assertTrue(source.contains("public long getCount() {"), source);
        assertTrue(source.contains("return count;"), source);
    }

    @Test
    void generatesEqualsHashCodeAndToString() {
        String source = BeanGenerator.generate("com.example", "User",
                Property.of("name", String.class));

        assertTrue(source.contains("public boolean equals(Object o) {"), source);
        assertTrue(source.contains("public int hashCode() {"), source);
        assertTrue(source.contains("public String toString() {"), source);
        // java.util 不在 java.lang 下，JavaPoet 会保留全限定名并 import
        assertTrue(source.contains("Objects.equals(this.name, that.name)"), source);
        assertTrue(source.contains("Objects.hash(name)"), source);
        assertTrue(source.contains("return String.format(\"User{name=%s}\", name);"), source);
    }

    @Test
    void generatedSourceIsStableAcrossCalls() {
        String first = BeanGenerator.generate("com.example", "User", Property.of("name", String.class));
        String second = BeanGenerator.generate("com.example", "User", Property.of("name", String.class));

        assertEquals(first, second);
    }

    @Test
    void generatesNoArgConstructorWhenThereAreNoProperties() {
        String source = BeanGenerator.generate("com.example", "Empty");

        assertTrue(source.contains("public final class Empty {"), source);
        assertTrue(source.contains("public Empty() {"), source);
        assertFalse(source.contains("this."), source);
    }
}
