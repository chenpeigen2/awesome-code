package org.peter.basic.models;

public class Statics {
    public static int a = 1;
    private static String b = "b";
    private static int c;
    private static String d;

    static {
        c = 2;
        d = "hello";
    }

    public static void e() {
        System.out.println("e");
    }

    public static int f() {
        System.out.println("f");
        return 1;
    }

    private static String g() {
        System.out.println("g");
        return "g";
    }

}
