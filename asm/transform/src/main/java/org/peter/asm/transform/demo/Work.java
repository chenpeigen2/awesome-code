package org.peter.asm.transform.demo;

/**
 * 演示用的目标类：有三个方法，其中 {@code obsolete()} 会被删掉。
 */
public class Work {

    public int add(int a, int b) {
        return a + b;
    }

    /** 即将被 {@code MethodRemover} 删除的方法。 */
    public String obsolete() {
        return "should be gone";
    }

    public String label() {
        return "Work";
    }
}
