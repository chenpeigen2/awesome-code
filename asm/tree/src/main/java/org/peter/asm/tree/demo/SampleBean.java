package org.peter.asm.tree.demo;

/**
 * 演示用的普通 JavaBean，只有字段和构造器——getter/setter 由 {@link TreeDemo} 用 ASM 补上。
 * <p>
 * 注意它<b>没有</b> getter/setter：这正是演示的重点，
 * 我们不在源码里写它们，而是在字节码层面生成。
 */
public class SampleBean {

    private String name;
    private int age;

    public SampleBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /** 一个原本就存在的方法，用来证明增强后原有行为不变。 */
    public String describe() {
        return name + "/" + age;
    }
}
