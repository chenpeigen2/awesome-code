package featuretest.collections;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AAray {
    public static void main(String[] args) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);

        arrayList.add(1, 4);

        System.out.println();

        arrayList.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });
    }
}
