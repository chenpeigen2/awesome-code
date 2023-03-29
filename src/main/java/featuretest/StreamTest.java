package featuretest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class User {
    int id;
    String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

public class StreamTest {
    public static void main(String[] args) {
        List<User> list = new ArrayList<>();
        list.add(new User(1, "zs"));
        list.add(new User(2, "zh"));
        list.add(new User(3, "zc"));
        list.add(new User(4, "zt"));
        list.stream().collect(Collectors.toMap(t -> {
            return t.getId();
        }, t -> {
            return t;
        }));
        Map<Integer, User> collect = list.stream().collect(Collectors.toMap(User::getId, t -> t));
        var l = list.stream().map(t -> t.getId()).collect(Collectors.toSet());
        l.stream().forEach(System.out::println);

//        list.stream().collect(Collectors.toMap(User::getId, t -> t));
        System.out.println(collect);
    }
}
