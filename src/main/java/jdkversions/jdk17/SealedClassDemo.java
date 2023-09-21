package jdkversions.jdk17;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public sealed class SealedClassDemo permits Hello, SubClass {

}

non-sealed class SubClass extends SealedClassDemo {

}


final class Hello extends SealedClassDemo {

}

sealed class Shape {

}

final class Circle extends Shape {

}

non-sealed class Square extends Shape {

}

class App {
//    public static void main(String[] args) {
//        Shape a = new Circle();
//        switch (a){
//            case Circle c -> {
//
//            }
//        }
//    }
}

//https://baijiahao.baidu.com/s?id=1732041493754048192&wfr=spider&for=pc
sealed class ZA permits ZA1, ZA2 {

    public static void main(String[] args) {
    }
}

non-sealed class ZA1 extends ZA {

}

//sealed class ZA3 extends ZA {
//
//}

final class ZA2 extends ZA {

}