package featuretest.functions;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Core {
    public static void main(String[] args) {
        Consumer<String> nameConsumer = s -> {
            System.out.println(s);
        };


        BiConsumer<String,Integer> biConsumer = (s, integer) -> {
            System.out.println(s);
            System.out.println(integer);
        };

        nameConsumer.accept("sdfsadfsadf");

        nameConsumer.accept("hello world");


        biConsumer.accept("asfsdaf",111);
    }
}
