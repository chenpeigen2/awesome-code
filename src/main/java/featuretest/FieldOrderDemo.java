package featuretest;

public class FieldOrderDemo {

    // 属性也有实例化的顺序

    // not allowed
//    static int third = second;
//
    static int second = 2;

    static int first = test();

    static int test() {
        return second;
    }

    public static void main(String[] args) {

//        System.out.println(test());
        System.out.println(first);
    }
}
