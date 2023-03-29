package jdkversions.jdk16;

public class LocalEnumsAndInterfaceDemo {

    // do not confuse with inners
    public static void main(String[] args) {

        interface Hello {
            int a = 12;
        }

        System.out.println(Hello.a);

        enum Seasons {
            SPRING, SUMMER;
        }

        System.out.println(Seasons.SPRING);

        // not allowed
//        @interface WTF{
//
//        }

        // local class
        class WTF {

        }

        // local record
        record WTFR() {

        }

    }
}
