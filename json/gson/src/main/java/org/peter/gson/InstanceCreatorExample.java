package org.peter.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

/**
 * 演示 {@link InstanceCreator}：让 Gson 能反序列化「没有无参构造器」的类。
 * <p>
 * 默认情况下 Gson 反序列化时不用构造器（它靠 Unsafe 直接分配对象再塞字段），
 * 所以 {@code final} 字段拿不到值。注册 {@code InstanceCreator} 后，
 * Gson 改成先让你自己造一个实例，再往里填字段 —— 前提是字段不能是 {@code final}，
 * 否则反射赋值会失败。
 * <p>
 * 原来的实现把 {@code registerTypeAdapter(...)} 那一行<b>注释掉了</b>，
 * 于是这个「InstanceCreator 示例」其实什么也没演示。这里补上。
 */
public final class InstanceCreatorExample {

    /** 只有全参构造器，没有无参构造器。 */
    public static class User {
        // 注意：字段不能是 final —— Gson 要在实例创建之后再反射赋值
        private String name;
        private int age;
        private String email;

        public User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "User{name='%s', age=%d, email='%s'}".formatted(name, age, email);
        }
    }

    /**
     * 反序列化时被调用：Gson 需要新建 {@code User} 实例时就走这里。
     * <p>
     * 典型用途是对象需要「先占位、后填值」的场景，比如构造函数有副作用、
     * 或者必须经过工厂方法创建。
     */
    static final class UserInstanceCreator implements InstanceCreator<User> {
        private int invoked;

        @Override
        public User createInstance(java.lang.reflect.Type type) {
            invoked++;
            // 给一个空壳，Gson 随后会把 JSON 里的字段反射写进去
            return new User(null, 0, null);
        }

        int invokedTimes() {
            return invoked;
        }
    }

    private final UserInstanceCreator creator = new UserInstanceCreator();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(User.class, creator)
            .create();

    /** 反序列化并返回结果。 */
    public User parse(String json) {
        return gson.fromJson(json, User.class);
    }

    /** InstanceCreator 被调用的次数 —— 用来证明它真的生效了，而不是「碰巧也能跑」。 */
    public int creatorInvocations() {
        return creator.invokedTimes();
    }

    public String run() {
        User user = parse("""
                {"name":"张三","age":25,"email":"zhangsan@example.com"}
                """);
        return "InstanceCreator lets Gson build objects without a no-arg constructor\n"
                + "user: " + user + "\n"
                + "creator invoked: " + creatorInvocations() + " time(s)\n";
    }
}
