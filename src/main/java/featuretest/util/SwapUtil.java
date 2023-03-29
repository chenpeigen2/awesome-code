package featuretest.util;

public class SwapUtil {
    public static void main(String[] args) {
        var a = 100;
        var b = 200;
        a = a ^ b;
        b = b ^ a;
        a = a ^ b;

        System.out.println(a);

        System.out.println(b);
    }
}
