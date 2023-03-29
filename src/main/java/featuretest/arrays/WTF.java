package featuretest.arrays;

class A {

}

class B extends A {

}

class C extends A {

}


public class WTF {
    public static void main(String[] args) {
        A[] array1 = new A[3];
        array1[0] = new A();
        array1[1] = new B();
        array1[2] = new C();

        A[] array2 = new B[3];
        array2[0] = new A();
        array2[1] = new B();
        array2[2] = new C();
    }
}
