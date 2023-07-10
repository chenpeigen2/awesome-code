import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import org.json.CDL;
import org.json.JSONObject;

import java.io.*;

public class GsonTest {

    static String s1 = """
            {
                "store": {
            		"book": [
            		    {
            		        "name":"dasd",
            		        "category": "reference",
            		        "author": "Nigel Rees",
            		        "title": "Sayings of the Century",
            		        "isbn": "0-395-19395-8",
            		        "price": 8.95,
            		        "marks" : [3,99,89]
            		    },
            		    {
            		        "category": "fiction",
            		        "author": "Evelyn Waugh",
            		        "title": "Sword of Honour",
            		        "price": 12.99,
            		        "marks" : [3,99,89,34,67567]
            		    },
            		    {
            		        "category": "fiction",
            		        "author": "Herman Melville",
            		        "title": "Moby Dick",
            		        "isbn": "0-553-21311-3",
            		        "price": 8.99,
            		        "marks" : [3,99,89]
            		    },
            		    {
            		        "category": "fiction",
            		        "author": "J. R. R. Tolkien",
            		        "title": "The Lord of the Rings",
            		        "isbn": "0-395-19395-8",
            		        "price": 22.99,
            		        "marks" : []
            		    }
            		]
            	}
            }
            """;

    static class User {
        @Expose(serialize = false, deserialize = true)
        private String name;

        @Expose
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {

        // 序列化时只包含@Expose注解标记的字段
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        User user = new User("John", 30);
        String json = gson.toJson(user); // {"name":"John"}
        System.out.println(json);

// 反序列化时只包含@Expose注解标记的字段
        String json2 = "{\"name\":\"John\",\"age\":30}";
        User user2 = gson.fromJson(json2, User.class); // user2.name = "John",
        System.out.println(user2);

        // gson
        var books_obj = JsonParser.parseString(s1).getAsJsonObject().get("store").getAsJsonObject();

        String s = books_obj.toString();
        System.out.println(s);

        System.out.println();
        // org
        JSONObject jsonObject = new JSONObject(s);
        var jarr = jsonObject.getJSONArray("book");

        var result = CDL.toString(jarr);


        System.out.println(result);

        File f = new File("book.csv");
        if (!f.exists()) {
            f.createNewFile();
        }
        Writer w = new OutputStreamWriter(new FileOutputStream(f));
        BufferedWriter br = new BufferedWriter(w);

        br.write(result);

        br.flush();
        br.close();


        A a = new A();
        Gson g = new Gson();
        String xx = g.toJson(a);

        System.out.println(xx);

        A b = g.fromJson(xx, A.class);
        System.out.println();
    }

    static class A {
        private int a;
        private String b;

        public A() {
            a = 12;
            b = "xff";
        }
    }
}
