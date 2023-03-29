package featuretest.arrays;

import java.lang.reflect.Array;

/**
 * 在jvm中对于数组array,可以通过Array 把对象封装成为一个Object ， 所以兑于对象的处理都可以转换成对于一个object的处理， 此类是array的包转类
 */
public class TestArray {
    /**
     * main
     */
    public static void main(String[] args) throws Exception {
        TestArray testArray = new TestArray();
        testArray.testOneObject();
        testArray.testOne();
        testArray.testTwo();

        testArray.testOneFloatObject();
    }

    /**
     * 放置数据
     *
     * @throws ClassNotFoundException
     **/
    public void testOneObject() throws ClassNotFoundException {
        Object object = Array.newInstance(Object.class, 10);
        Array.set(object, 3, "99999");
        System.out.println(Array.get(object, 3));
    }

    public void testOneFloatObject() throws ClassNotFoundException {
        var object = Array.newInstance(Float.class, 10);
        Array.set(object, 3, 12f);
        System.out.println(Array.get(object, 3));
    }

    /**
     * 一维素组
     */
    public void testOne() throws ClassNotFoundException {
        Class<?> classType = Class.forName("java.lang.String");
        Object object = Array.newInstance(classType, 10); //数组0,9
        Array.set(object, 5, "123");
        System.out.println(Array.get(object, 5));
        System.out.println(Array.getLength(object));
    }

    /**
     * 二维数组
     */
    public void testTwo() throws ClassNotFoundException {
        Class<?> classType = Class.forName("java.lang.String");
        Object object = Array.newInstance(classType, new int[]{
                10,
                10
        }); //数组0,9
        Array.set(Array.get(object, 5), 5, "123"); //[5][5] 为"123",先获取一维，在通过一维获取二维的数据
        System.out.println(Array.get(Array.get(object, 5), 5));
    }
}
