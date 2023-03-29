package featuretest.arrays;

import java.lang.reflect.Array;

public class ArrayTest {
    // 数组的原始类型
    public static void main(String[] args) {
        Object[] arr = new Object[5];
        for (int i = 0; i < 5; i++) {
            arr[i] = i;
        }

        int a = (int) arr[0];
        int b = Array.getInt(arr, 1);
        System.out.println(a);
        System.out.println(b);

        Object intArray = Array.newInstance(int.class, 3);              //int [3]
        Object stringArray = Array.newInstance(String.class, 2, 3);      //String [2][3]

        Array.set(intArray,2,3);
        Array.set(stringArray,1,new String[]{"123","456"});

        System.out.println(Array.get(intArray,2));
        System.out.println(Array.get(Array.get(stringArray,1),1));

        System.out.println("-------cast-------");
        System.out.println(((int[]) intArray)[2]);
        System.out.println(((String [][])stringArray)[1][1]);
    }
}
