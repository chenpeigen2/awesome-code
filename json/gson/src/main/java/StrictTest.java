import com.google.gson.Gson;

public class StrictTest {

    class User {
        private final String name;
        private final int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
            System.out.println("构造函数被调用，外部实例: " + StrictTest.this);
        }

        // 添加一个需要外部类引用的方法
        public void useOuter() {
            System.out.println("使用外部类: " + StrictTest.this);
        }
    }

    public static void main(String[] args) {
        StrictTest outer = new StrictTest();

        System.setProperty("gson.disableUnsafe", "true");



        Gson gson = new Gson();
        String json = "{\"name\":\"张三\",\"age\":25}";

        try {
            // 在不同环境下，这个可能成功也可能失败
            User user = gson.fromJson(json, User.class);
            System.out.println("成功: " + user);

            // 尝试使用需要外部引用的方法
            user.useOuter();  // 这行可能抛出异常！
        } catch (Exception e) {
            System.out.println("失败: " + e.getClass().getName() + " - " + e.getMessage());
        }

        String json1 = "{\"name\":\"Alice\",\"age\":30}";
        Person p = gson.fromJson(json1, Person.class);
        System.out.println(p);
    }
}
