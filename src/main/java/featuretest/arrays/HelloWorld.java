package featuretest.arrays;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HelloWorld {
    /**
     * use invoke for test
     *
     * @param args
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = String.class.getMethod("toString");

        m.setAccessible(true);

        System.out.println(m.toString());
        System.out.println(m.toGenericString());
        System.out.println(m.hashCode());
        String ss = new String("zxxxxxxxxxxxx");
        var s = m.invoke(ss);
        System.out.println((int) s);
    }
}
