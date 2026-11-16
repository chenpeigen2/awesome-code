package org.peter.asm.transform.fixture;

/**
 * 被测类：用来验证「计时增强」的方法体。
 * <p>
 * 每个方法都有确定的、可断言的返回值。
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public long multiply(long a, long b) {
        return a * b;
    }

    public String concat(String a, String b) {
        return a + b;
    }

    public double half(double value) {
        return value / 2;
    }

    public void boom() {
        throw new IllegalStateException("boom");
    }

    public static int square(int value) {
        return value * value;
    }
}
