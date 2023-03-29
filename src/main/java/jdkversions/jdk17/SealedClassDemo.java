package jdkversions.jdk17;

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
