package featuretest.collections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class LLinkedHashMap {

//    https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/LinkedHashMap.html
    public static void main(String[] args) {
        Map<String, String> m = new LinkedHashMap<>();

        m.put("z", "z");
        m.put("b", "b");
        m.put("d", "d");
        m.put("c", "c");
        m.put("e", "e");
        m.put("a", "a");

        m.put("z", "z1");
        m.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                System.out.println(s + "  " + s2);
            }
        });
    }
}
