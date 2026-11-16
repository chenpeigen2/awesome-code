package org.peter.apt.demo;

import org.peter.apt.Builder;

/**
 * 演示用的目标类：两个 setter 被 {@link Builder} 标记。
 * <p>
 * 编译之后，{@code PersonBuilder} 会被注解处理器生成出来——
 * 源码里没有这个文件，但 {@link AptDemo} 可以正常引用它。
 */
public class Person {

    private String name;
    private int age;
    private String email;

    @Builder
    public void setName(String name) {
        this.name = name;
    }

    @Builder
    public void setAge(int age) {
        this.age = age;
    }

    @Builder
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Person{name=" + name + ", age=" + age + ", email=" + email + "}";
    }
}
