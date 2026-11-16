package org.peter.asm.basics.fixture;

/**
 * 测试用的「被测类」：本文件就是它的独立真值（源码即规格）。
 * <p>
 * 用它编译出来的 .class 作为 ClassInspector 的输入，断言的是字节码里客观存在的结构。
 */
public class Greeter implements Runnable {

    private final String name;

    private static int counter;

    public Greeter(String name) {
        this.name = name;
    }

    public String greet() {
        counter++;
        return "Hello, " + name;
    }

    @Override
    public void run() {
        System.out.println(greet());
    }

    public static int counter() {
        return counter;
    }
}
