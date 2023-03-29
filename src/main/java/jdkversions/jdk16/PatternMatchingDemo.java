package jdkversions.jdk16;

public class PatternMatchingDemo {
//    https://openjdk.java.net/jeps/394
    public static void main(String[] args) {
        Object obj = "hello world";
        if (obj instanceof String s) {
            // Let pattern matching do the work!
            System.out.println(s);
        }
    }
}
