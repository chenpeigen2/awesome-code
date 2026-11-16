package org.peter.asm.tree.fixture;

/**
 * 被测类：一个普通的 POJO，没有 getter/setter。
 * <p>
 * 源码就是「输入」的规格：字段名、字段类型是客观事实。
 */
public class Person {

    private String name;

    private int age;

    private boolean active;

    private double score;

    private long id;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name=" + name + ", age=" + age + "}";
    }
}
