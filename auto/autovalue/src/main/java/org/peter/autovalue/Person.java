package org.peter.autovalue;

import com.google.auto.value.AutoValue;

/**
 * 最基础的值对象：只有工厂方法，没有 Builder。
 * <p>
 * 抽象类里的 {@code abstract} 方法会被 AutoValue 视为「属性」，
 * 编译期生成 {@code AutoValue_Person}，把 equals/hashCode/toString 全部实现掉。
 * 手写这些方法容易漏字段、容易写错 hashCode 契约，正是 AutoValue 要消灭的重复劳动。
 */
@AutoValue
public abstract class Person {

    /** 注意这里没有 setter —— 值对象一旦创建就不可变。 */
    public abstract String name();

    public abstract int age();

    /** 工厂方法名随意，但必须是 static 且返回本类型。 */
    public static Person create(String name, int age) {
        // 生成的构造器会做 null 检查：name 为 null 时抛 NullPointerException
        return new AutoValue_Person(name, age);
    }
}
