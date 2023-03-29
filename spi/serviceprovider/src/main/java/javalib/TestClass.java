package javalib;

import java.util.ServiceLoader;

public class TestClass {

    public static void main(String[] argus) {
        ServiceLoader<IMyServiceProvider> serviceLoader = ServiceLoader.load(IMyServiceProvider.class);

        for (IMyServiceProvider item : serviceLoader) {
            System.out.println(item.getName() + ": " + item.hashCode());
        }
    }
}

