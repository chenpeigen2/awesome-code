import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InstanceCreatorExample {

    class User {
        private final String name;
        private final int age;
        private final String email;

        // 只有带参数的构造函数，没有无参构造函数
        public User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        // Getters
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
            return String.format("User{name='%s', age=%d, email='%s'}", name, age, email);
        }
    }


    void main() {
        // 1. 创建 Gson 实例并注册 InstanceCreator
        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(User.class, new UserInstanceCreator())
                .create();

        // 2. JSON 字符串
        String json = "{\"name\":\"张三\",\"age\":25,\"email\":\"zhangsan@example.com\"}";

        // 3. 反序列化
        User user = gson.fromJson(json, User.class);

        // 4. 输出结果
        System.out.println("反序列化结果: " + user);
        System.out.println("姓名: " + user.getName());
        System.out.println("年龄: " + user.getAge());
        System.out.println("邮箱: " + user.getEmail());
    }

}


