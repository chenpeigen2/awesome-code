package featuretest.rref;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.*;

public class WeakHashMapDemo {
    public static void main(String[] args) {
        Map weakHashMap = new WeakHashMap();

        //向weakHashMap中添加4个元素
        for (int i = 0; i < 3; i++) {
            weakHashMap.put("key-" + i, "value-" + i);
        }
        //输出添加的元素
//        System.out.println("数组长度：" + weakHashMap.size() + "，输出结果：" + weakHashMap);

        System.out.println(weakHashMap.toString());
        //主动触发一次GC
//        System.gc();

        System.out.println(weakHashMap.toString());
        //再输出添加的元素
//        System.out.println("数组长度：" + weakHashMap.size() + "，输出结果：" + weakHashMap);

//        Map<Integer,byte[]> hashMap = new WeakHashMap<>();
//        for(int i=0;i<1000000;i++){
//            hashMap.put(i, new byte[i]);
//        }
//
//        System.out.println(hashMap.size());

//        https://hongjiang.info/java-referencequeue/
//        List<WeakHashMap<Object, Integer[][]>> maps = new ArrayList<>();
//        int totalNum = 10000;
//        for (int i = 0; i < totalNum; i++) {
//            WeakHashMap<Object, Integer[][]> w = new WeakHashMap<>();
//            w.put(new Object(), new Integer[1000][1000]);
//            maps.add(w);
//            System.gc();
//            System.out.println(i);
//        }


        WeakHashMap w = new WeakHashMap();
        //三个key-value中的key 都是匿名对象，没有强引用指向该实际对象
        w.put(new String("语文"), new String("优秀"));
        w.put(new String("数学"), new String("及格"));
        w.put(new String("英语"), new String("中等"));
        //增加一个字符串的强引用
        w.put("java", new String("特别优秀"));
        System.out.println(w);
        //通知垃圾回收机制来进行回收
        System.gc();
        System.runFinalization();
        //再次输出w
        System.out.println("第二次输出:" + w);
    }
}
