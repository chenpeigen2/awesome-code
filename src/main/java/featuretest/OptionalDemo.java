package featuretest;

import java.util.Optional;

public class OptionalDemo {
    public static void main(String[] args) {
        // value 值来自其它不确定的来源
        String value = "world";
        // 可能为 null

        value = null;
        Optional<String> nullable = Optional.ofNullable(value);

//        nullable.ifPresent(System.out::println);
        nullable.get();

        System.out.println(nullable.isEmpty());
        System.out.println(nullable.isPresent());

    }
}
