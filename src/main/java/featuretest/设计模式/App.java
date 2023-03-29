package featuretest.设计模式;

import java.io.File;

public class App {
}

interface A {
    void print();
}

interface CC {
    void parse(File f);
}

class C1 implements CC {

    @Override
    public void parse(File f) {

    }

    public static void useC1As() {
        C2.setDefault(new C1());
    }
}

class C2 implements CC {

    private static CC cc;

    static {
        C1.useC1As();
    }

    public static void setDefault(CC cc1) {
        cc = cc1;
    }

    @Override
    public void parse(File f) {
        cc.parse(f);
    }
}