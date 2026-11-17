package sample;

/**
 * 被 JavaParser 解析的示例类，用来验证「从 classpath 资源读源码」这条路。
 */
public class Calculator {

    private int memory;

    public int add(int a, int b) {
        return a + b;
    }

    public int memory() {
        return memory;
    }
}
