package jdkversions.oldjava;

import java.io.Serializable;


// can't extend but can implementation
public enum EnumDemo implements Serializable {
    A(1), B, C, D, E;

    // same as class
    public static final int a = 12;


    // same as class
    public static void hello() {

    }

    // constructors only the private
    EnumDemo(int a) {

    }

    EnumDemo() {

    }

}
