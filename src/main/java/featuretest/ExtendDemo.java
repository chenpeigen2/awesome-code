package featuretest;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


class A1 implements Comparable<A1> {
    @Override
    public int compareTo(A1 o) {
        return 0;
    }

}

class B1 extends A1 {

    public static <T extends Comparable<? extends T>> void sort(List<T> list) {
        list.sort(null);
    }

    public static void main(String[] args) {
        List<Integer> l = new ArrayList<>();
//        l.sort(new Comparator<A1>() {
//            @Override
//            public int compare(A1 o1, A1 o2) {
//                return 0;
//            }
//        });

        sort(l);
    }
}

class BASE {
    public int x = 123;

    public void a() {
        System.out.println("super a");
        b();
        this.b();
        System.out.println(this.x);
        System.out.println("-----------------");
    }

    public void b() {
        System.out.println(this.x);
        System.out.println("super b");
    }
}

class Sub extends BASE {
    public int x = 234;

    public void b() {
        System.out.println(x);
        System.out.println("fuck you");
    }
}

public class ExtendDemo {


    public static void main(String[] args) throws ParseException, InterruptedException {

        // method 遵循原型链 先从this -> super
        // field 在函数中遵循原型链，先从this找起->super

        // this 永远是this
        BASE app = new Sub();
        app.a();
        System.out.println(app.x);
//        // field 不遵循原型链 ， 主要是你的声明有相关{也可以理解成为另外一个原型链，this直接被覆盖成声明的this???}
//        System.out.println(app.x);
    }
}